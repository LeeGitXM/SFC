package com.ils.python.translation;
/**
 *   (c) 2013-2014  ILS Automation. All rights reserved.
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.tree.ParseTree;

import com.ils.g2.procedure.G2ProcedureBaseVisitor;
import com.ils.g2.procedure.G2ProcedureParser;
import com.ils.g2.procedure.G2ProcedureParser.ElseifclauseContext;
import com.ils.g2.procedure.G2ProcedureParser.StatementContext;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;


/**
 *  This translator overrides the generated G2ExpressionVisitor
 *  with customizations that output the desired expression.
 */
public class PythonGenerator extends G2ProcedureBaseVisitor<Object>  {
	private static final String TAG = "PythonGenerator";
	private static final String LOG_PACKAGE = "com.ils.python.translate";
	private LoggerEx log;
	private final StringBuffer buf;
	private final Map<String,Object> translation;
	private final Map<String,String> classLookup;
	private final Map<String,String> constantLookup;
	private final Map<String,String> importLookup;
	private final Map<String,String> procedureLookup;
	private final Map<String,String> variableClassMap;
	private int currentIndent = 0;
	private String errorArgument = null;    // Name of exception in current error clause (g2Name)
	private String selfArgument = null;     // Name of the first argument
	private String selfEquivalent = null;	// Argument used for "this procedure".
	private final Pattern generalArgPattern;
	private final Pattern singleVariablePattern;
	private final Pattern memberValuePattern;
	/**
	 * Constructor.
	 * @param dict results dictionary
	 */
	public PythonGenerator(Map<String,Object>t, Map<String,Map<String,String>> mapOfMaps) {
		log = LogUtil.getLogger(getClass().getPackage().getName());
		this.buf = new StringBuffer();
		this.translation = t;
		classLookup = mapOfMaps.get(TranslationConstants.MAP_CLASSES);
		constantLookup = mapOfMaps.get(TranslationConstants.MAP_ENUMERATIONS);
		importLookup = mapOfMaps.get(TranslationConstants.MAP_IMPORTS);
		procedureLookup = mapOfMaps.get(TranslationConstants.MAP_PROCEDURES);
		variableClassMap = new HashMap<String,String>();
		
		generalArgPattern = Pattern.compile("\\[[A-Za-z0-9-_ \t]+\\]");
		singleVariablePattern = Pattern.compile("\\[[A-Za-z0-9-_]+\\]");
		memberValuePattern = Pattern.compile("\\[\\s*the\\s*([A-Za-z0-9-_]+)\\s*of\\s*([A-Za-z0-9-_]+)\\]");
	}
	/**
	 * @return the StringBuffer constructed as part of the visiting. This is the code.
	 */
	public StringBuffer getTranslation() { return buf; }
	
	// ================================= Procedure Methods =====================================
	// The docstring is saved off separately. Written a
	@Override 
	public Object visitProcedureDocstring(G2ProcedureParser.ProcedureDocstringContext ctx) {
		StringBuffer doc = new StringBuffer();
		doc.append("\n'''\n");
		doc.append(stripBraces(removeNewlines(ctx.COMMENT().getText())));
		doc.append("\n'''\n");
		translation.put(TranslationConstants.PY_DOC_STRING, doc.toString());
		return null; 
	}

	// Create the header. All methods are "evalate".
	@Override 
	public Object visitProcedureHeader(G2ProcedureParser.ProcedureHeaderContext ctx) {
		buf.append("def evaluate(");
		visit(ctx.arglist());
		buf.append("):\n");
		currentIndent = 1;  // First block increments it
		String name = ctx.G2NAME().getText();
		translation.put(TranslationConstants.PY_G2_PROC, name);
		// Attempt a lookup. On success use the translated name, plus package.
		// otherwise use the default package and pythonize the name,
		String pyName = procedureLookup.get(name);
		if( pyName!=null ) {
			translation.put(TranslationConstants.PY_METHOD, getMethodName(pyName));
			translation.put(TranslationConstants.PY_MODULE, getModuleName(pyName));
			translation.put(TranslationConstants.PY_PACKAGE, getPackageName(pyName));
		}
		else {
			translation.put(TranslationConstants.PY_MODULE, pythonName(name,true));
			translation.put(TranslationConstants.PY_METHOD, TranslationConstants.DEFAULT_METHOD_NAME);
		}

		return null;
	}
	// ================================= Declaration Methods ===================================
	@Override 
	public Object visitDeclarationInitialized(G2ProcedureParser.DeclarationInitializedContext ctx) {
		appendIndent(1);          // Declarations always indented 1
		String val = ctx.value().getText();
		// Val may be a boolean
		if( val.equalsIgnoreCase("true")) val = "True";
		else if(val.equalsIgnoreCase("false")) val = "False";
		buf.append(String.format("%s = %s\n",pythonName(ctx.G2NAME().getText(),false),val));
		visit(ctx.datatype());    // For side effects only
		return null; 
	}
	@Override 
	public Object visitDeclarationInitializedByMember(G2ProcedureParser.DeclarationInitializedByMemberContext ctx) {
		appendIndent(1);      // Declarations always indented 1
		String varname= pythonName(ctx.G2NAME().getText(),false);
		buf.append(String.format("%s = ",varname));
		visit(ctx.cagetter());
		buf.append("\n");
		visit(ctx.datatype());       // For side effects only.
		return null; 
	}
	@Override public Object visitDeclarationSelf(G2ProcedureParser.DeclarationSelfContext ctx) {
		selfEquivalent = ctx.G2NAME().getText();
		return null;
	}
	// ================================= Expression Methods ====================================
	@Override 
	public Object visitExprCall(G2ProcedureParser.ExprCallContext ctx) {
		String procName = ctx.G2NAME().getText();
		String pyName = procedureLookup.get(procName);
		if( pyName!=null ) {
			String moduleName = getModuleName(pyName);
			buf.append(moduleName);
			buf.append(ctx.POPEN().getText());
			visit(ctx.exprlist());
			buf.append(ctx.PCLOSE().getText());
			importLookup.put(moduleName,String.format("from %s import %s",getPackageName(pyName),moduleName));
		}
		else {
			recordError("No python equivalent defined for "+procName,"call", "after" );
		}
		return null; 
	}
	@Override 
	public Object visitExprClassMember(G2ProcedureParser.ExprClassMemberContext ctx) {
		// In this context, we have a setter
		String property = ctx.G2NAME(0).getText();
		String clss = ctx.G2NAME(1).getText();
		buf.append(createGetterCall(property,clss));
		return null; 
	}
	@Override 
	public Object visitExprLogicalOperator(G2ProcedureParser.ExprLogicalOperatorContext ctx) {
		visit(ctx.expr(0));
		String opr = "";
		if( ctx.LOPR()!=null )     opr = ctx.LOPR().getText().toLowerCase();
		else if(ctx.EQU()!=null)   opr = "==";
		else if(ctx.NEQU()!=null)  opr = "!=";
		
		buf.append(String.format(" %s ",opr));
		visit(ctx.expr(1));
		return null; 
	}
	@Override 
	public Object visitExprOperator(G2ProcedureParser.ExprOperatorContext ctx) { 
		visit(ctx.expr(0));
		buf.append(String.format(" %s ",ctx.OPR().getText()));
		visit(ctx.expr(1));
		return null; 
	}
	@Override 
	public Object visitExprParentheses(G2ProcedureParser.ExprParenthesesContext ctx) {
		buf.append(ctx.POPEN().getText());
		visit(ctx.expr());
		buf.append(ctx.PCLOSE().getText());
		return null; 
	}
	@Override 
	public Object visitExprRelationalOperator(G2ProcedureParser.ExprRelationalOperatorContext ctx) { 
		visit(ctx.expr(0));
		if(ctx.EQU()!=null)       buf.append(" == ");
		else if(ctx.NEQU()!=null) buf.append(" != ");
		else if(ctx.ROPR()!=null) buf.append(String.format(" %s ",ctx.ROPR().getText()));
		visit(ctx.expr(1));
		return null; 
	}
	// This is really a no-op
	@Override 
	public Object visitExprValue(G2ProcedureParser.ExprValueContext ctx) {
		visit(ctx.value());
		return null; 
	}
	@Override 
	public Object visitExprVariable(G2ProcedureParser.ExprVariableContext ctx) {
		String var = ctx.variable().getText();
		//log.infof("ExprVariable: %s", var);
		buf.append(pythonName(var,false));
		return null; 
	}
	// ============================== Statement Fragment Methods ================================
	@Override 
	public Object visitStatementAssign(G2ProcedureParser.StatementAssignContext ctx) {
		String val = pythonName(ctx.G2NAME().getText(),false);
		buf.append(String.format("%s = ",val));
		visit(ctx.expr());
		return null; 
	}
	// BEGIN .. END is literally a no-op unless there is a ON ERROR clause.
	@Override 
	public Object visitStatementBlock(G2ProcedureParser.StatementBlockContext ctx) {
		//log.infof("BLOCK Statement %d",currentIndent);
		if( ctx.blockerr() != null) {
			appendIndent(currentIndent);
			buf.append("try:\n");
			currentIndent++;
		}
		if(!ctx.statement().isEmpty()) {
			for(StatementContext sctx:ctx.statement()) {
				visit(sctx);
			}
		}
		else {
			appendIndent(currentIndent);
			buf.append("pass\n");   // To keep syntax legal
		}
		
		if( ctx.blockerr() != null) {
			currentIndent--;
			visit(ctx.blockerr());
		}
		return null; 
	}
	@Override 
	public Object visitStatementCall(G2ProcedureParser.StatementCallContext ctx) {
		String procName = ctx.G2NAME().getText();
		String pyName = procedureLookup.get(procName);
		if( pyName!=null ) {
			String moduleName = getModuleName(pyName);
			buf.append(moduleName);
			buf.append(ctx.POPEN().getText());
			visit(ctx.exprlist());
			buf.append(ctx.PCLOSE().getText());
			importLookup.put(moduleName,String.format("from %s import %s",getPackageName(pyName),moduleName));
		}
		else {
			recordError("No python equivalent defined for "+procName,"call", "after" );
		}
		return null; 
	}
	@Override 
	public Object visitStatementCallWithReturn(G2ProcedureParser.StatementCallWithReturnContext ctx) { 
		visit(ctx.varlist());
		buf.append("=");
		String procName = ctx.G2NAME().getText();
		String pyName = procedureLookup.get(procName);
		if( pyName!=null ) {
			String moduleName = getModuleName(pyName);
			buf.append(moduleName);
			buf.append(ctx.POPEN().getText());
			visit(ctx.exprlist());
			buf.append(ctx.PCLOSE().getText());
			importLookup.put(moduleName,String.format("from %s import %s",getPackageName(pyName),moduleName));
		}
		else {
			recordError("No python equivalent defined for "+procName,"call", "after" );
		}
		return null; 
	}
	@Override 
	public Object visitStatementChange(G2ProcedureParser.StatementChangeContext ctx) { 
		visit(ctx.casetter());
		if( ctx.cagetter()!=null)   visit(ctx.cagetter());
		else if(ctx.expr()!=null)   visit(ctx.expr());
		buf.append(")");
		return null; 
	}
	@Override 
	public Object visitStatementConclusion(G2ProcedureParser.StatementConclusionContext ctx) {
		if( ctx.casetter() !=null ) {
			visit(ctx.casetter());     // Does not include the argument, nor closing parend
			visit(ctx.expr());
			buf.append(")");
		}
		else if(ctx.variable()!=null   ) { 
			visit(ctx.variable());   
			buf.append("=");
			visit(ctx.expr());
		}
		else {
			buf.append("NONE");
			recordError("Incorrect Conclude syntax",ctx.start.getText(),"following");
		}
		buf.append("\n");
		return null; 
	}
	@Override 
	public Object visitStatementDelete(G2ProcedureParser.StatementDeleteContext ctx) { 
		buf.append("pass;  # Delete is not necessary in python");
		return null; 
	}
	// This is meant for breaking out of a loop ...
	@Override 
	public Object visitStatementExitIf(G2ProcedureParser.StatementExitIfContext ctx) { 
		buf.append("if ");
		visit(ctx.expr());
		buf.append(": break\n");
		return null; 
	}
	@Override 
	public Object visitStatementPost(G2ProcedureParser.StatementPostContext ctx) 
	{ 
		importLookup.put("LogUtil","from com.inductiveautomation.ignition.common.util import LogUtil");
		importLookup.put("LoggerEx","from com.inductiveautomation.ignition.common.util import LoggerEx");
		buf.append("log = LogUtil.getLogger(\""+LOG_PACKAGE+"\")\n");
		String raw = ctx.STRING().getText();
		String expanded = extractActiveElementsForLogging(raw);
		appendIndent(currentIndent);
		buf.append("log.infof("+expanded+")\n");
		return null;
	}
	@Override
	public Object visitStatementRepeat(G2ProcedureParser.StatementRepeatContext ctx) { 
		buf.append("while True:\n");
		currentIndent++;
		for(StatementContext sctx:ctx.statement()) {
			visit(sctx);
		}
		currentIndent--;
		return null; 
	}
	@Override 
	public Object visitStatementReturn(G2ProcedureParser.StatementReturnContext ctx) {
		buf.append("return");
		if( ctx.expr()!=null ) {
			buf.append(" ");
			visit(ctx.expr());
		}
		return null; 
	}
	// Handle indentation here, not within statement fragments.
	@Override 
	public Object visitStatementRoot(G2ProcedureParser.StatementRootContext ctx) {
		if( ctx.sfragment()!=null) {
			appendIndent(currentIndent);
			visit(ctx.sfragment());
			buf.append("\n");
		}
		else if(ctx.block()!=null) {
			visit(ctx.block());     // block() is a wrapper for a list of statements, does not get an indent
		}
		
		int count = ctx.COMMENT().size();
		int index = 0;
		while( index<count) {
			appendIndent(currentIndent);
			buf.append("# ");
			buf.append(stripBraces(removeNewlines(ctx.COMMENT(index).getText())));
			buf.append("\n");
			index++;
		}	
		return null; 
	}
	@Override 
	public Object visitStatementStart(G2ProcedureParser.StatementStartContext ctx) { 
		String procName = ctx.G2NAME().getText();
		String pyName = procedureLookup.get(procName);
		if( pyName!=null ) {
			String moduleName = getModuleName(pyName);
			buf.append("system.util.invokeAsynchronous(");
			buf.append(moduleName);
			buf.append(ctx.POPEN().getText());
			visit(ctx.exprlist());
			buf.append(ctx.PCLOSE().getText());
			importLookup.put(moduleName,String.format("from %s import %s",getPackageName(pyName),moduleName));
		}
		else {
			recordError("No python equivalent defined for "+procName,"call", "after" );
		}
		return null; 
	}
	@Override 
	public Object visitStatementWait(G2ProcedureParser.StatementWaitContext ctx) { 
		importLookup.put("time", "time");
		buf.append("time.sleep(");
		visit(ctx.variable());
		buf.append(")");
		return null; 
	}
	// ==================================== Value Methods ======================================
	@Override 
	public Object visitValueArray(G2ProcedureParser.ValueArrayContext ctx) {
		String val = ctx.G2NAME().getText();
		val = pythonName(val,false);
		buf.append(val);
		buf.append(ctx.BOPEN().getText());
		buf.append(ctx.INTEGER().getText());
		buf.append(ctx.BCLOSE().getText());
		return null; 
	}
	// This is a pass-thru
	@Override 
	public Object visitValueLogical(G2ProcedureParser.ValueLogicalContext ctx) { 
		visit(ctx.lvalue());
		return null; 
	}
	@Override 
	public Object visitValueNumeric(G2ProcedureParser.ValueNumericContext ctx) { 
		buf.append(ctx.nvalue().getText());
		return null; 
	}
	@Override
	public String visitValueString(G2ProcedureParser.ValueStringContext ctx) { 
		String text = ctx.STRING().getText();
		text = extractActiveElementsForString(text);
		buf.append(text);
		return null; 
	}
	@Override 
	public Object visitValueSymbol(G2ProcedureParser.ValueSymbolContext ctx) { 
		buf.append("\"");
		buf.append(ctx.G2NAME().getText());
		buf.append("\"");
		return null;
	}
	// ================================= Helper Methods ========================================
	// In the variable list all we need is the variable name.
	// We also map variable names to class.
	@Override 
	public Object visitArgDeclaration(G2ProcedureParser.ArgDeclarationContext ctx) { 
		buf.append(pythonName(ctx.G2NAME().toString(),false));
		visit(ctx.datatype());  // For side effects only
		return null;
	}

	@Override 
	public Object visitBlockComment(G2ProcedureParser.BlockCommentContext ctx) {
		appendIndent(currentIndent);
		buf.append("# ");
		buf.append(stripBraces(removeNewlines(ctx.COMMENT().getText())));
		buf.append("\n");
		visit(ctx.sfragment());    // The rest of the statement fragment
		return null; 
	}
	@Override 
	public Object visitBlockErrorClause(G2ProcedureParser.BlockErrorClauseContext ctx) { 
		appendIndent(currentIndent); 
		buf.append("except:\n");
		currentIndent++;
		for(StatementContext sctx:ctx.statement()) {
			visit(sctx);
		}
		currentIndent--;
		return null;
	}
	
	// Implement as a getter method.  Alternatively we can use a procedure getter
	// dot notation.  "The property of instance"
	@Override 
	public Object visitClassAttributeGetter(G2ProcedureParser.ClassAttributeGetterContext ctx) {
		if( ctx.G2NAME().size() > 1 ) {
			String procName     = createGetterCall(ctx.G2NAME(0).getText(),ctx.G2NAME(1).getText());
			buf.append(procName);
		}
		else {
			recordError("Incorrect classs getter syntax",ctx.start.getText(),"following");
		}
		return null; 
	}
	// Implement as a setter method.  Alternatively we can use a procedure setter
	// dot notation.  "The property of instance" 
	// WARNING: Raw values.
	@Override 
	public Object visitClassAttributeSetter(G2ProcedureParser.ClassAttributeSetterContext ctx) {
		if( ctx.G2NAME().size() > 1 ) {
			String procName = createSetterCall(ctx.G2NAME(0).getText(),ctx.G2NAME(1).getText());
			buf.append(procName);
		}
		else {
			recordError("Incorrect classs setter syntax",ctx.start.getText(),"following");
		}
		return null; 
	}
	// This endpoint exists only for the purposes of generating a map of variables to their
	// respective class. These are stored in the variableClassMap in non-G2 form
	@Override 
	public Object visitClassDatatype(G2ProcedureParser.ClassDatatypeContext ctx) { 
		String className = ctx.G2NAME().getText();
		// Take the G2NAME arg of the first child of the parent as the local name
		log.infof("ClassDatatype: PARENT class = %s",ctx.getParent().getClass().getName());
		ParseTree child = ctx.getParent().getChild(0);
		log.infof("ClassDatatype: CHILD local variable = %s",child.getText());
		String[] locals = child.getText().split(",");
		for( String local:locals) {
			variableClassMap.put(local, className);
		}
		return null;
	}
	// Implement a G2 for .. as a Python while ... The first line is already indented.
	@Override 
	public Object visitForByDecreasing(G2ProcedureParser.ForByDecreasingContext ctx) { 
		int icount = ctx.expr().size();
		if( icount>1) {
			String varname = ctx.G2NAME().getText();
			String start = ctx.expr(0).getText();
			String end   = ctx.expr(1).getText();
			String decrement = ctx.ivalue().getText();
			buf.append(String.format("%s = %s\n",varname,start));
			
			appendIndent(currentIndent);
			buf.append(String.format("while %s >= %s:\n",varname,end));
			currentIndent++;
			for(StatementContext sctx:ctx.statement()) {
				visit(sctx);
			}
			appendIndent(currentIndent);
			buf.append(String.format("%s = %s %s",varname,varname,decrement));
			currentIndent--;
		}
		else {
			recordError("Incorrect for loop syntax",ctx.start.getText(),"following");
		}
		return null; 
	}
	// This is used only in the parsing of the main procudure arguments.
	@Override 
	public Object visitFirstArgInList(G2ProcedureParser.FirstArgInListContext ctx) { 
		visit(ctx.arg());
		String name = ctx.arg().getText();
		int pos = name.indexOf(":");
		if( pos>0 ) name = name.substring(0, pos);
		selfArgument = name;
		return null; 
	}
	@Override 
	public Object visitIfElseClause(G2ProcedureParser.IfElseClauseContext ctx) { 
		appendIndent(currentIndent);
		buf.append("else:\n");
		currentIndent++;
		// Since this is a fragment, we need to supply indent (unless it's a block)
		if( ctx.sfragment()!=null) {
			appendIndent(currentIndent);
			visit(ctx.sfragment());
			buf.append("\n");
		}
		else if(ctx.block()!=null ) {
			visit(ctx.block());
		}
		currentIndent--;
		return null; 
	}
	@Override 
	public Object visitIfElseIfClause(G2ProcedureParser.IfElseIfClauseContext ctx) {
		appendIndent(currentIndent);
		buf.append("elif ");
		visit(ctx.expr());
		buf.append(":\n");
		currentIndent++;
		// Since this is a fragment, we need to supply indent (unless it's a block)
		if( ctx.sfragment()!=null) {
			appendIndent(currentIndent);
			visit(ctx.sfragment());
			buf.append("\n");
		}
		else if(ctx.block()!=null ) {
			visit(ctx.block());
		}
		currentIndent--;
		return null;
	}
	// This is a clause, so the indent is already made.
	@Override 
	public Object visitIfWithClauses(G2ProcedureParser.IfWithClausesContext ctx) {
		buf.append("if ");
		visit(ctx.expr());
		buf.append(":\n");
		currentIndent++;
		// Since this is a fragment, we need to supply indent (unless it's a block)
		if( ctx.sfragment()!=null) {
			appendIndent(currentIndent);
			visit(ctx.sfragment());
			buf.append("\n");
		}
		else if(ctx.block()!=null ) {
			visit(ctx.block());
		}
		currentIndent--;
		for(ElseifclauseContext eicc:ctx.elseifclause()) {
			visit(eicc);
		}
		if(ctx.elseclause()!=null) visit(ctx.elseclause());
		return null; 
	}
	@Override
	public Object visitLogicalFalse(G2ProcedureParser.LogicalFalseContext ctx) { 
		buf.append("False"); 
		return null; 
	}

	@Override
	public Object visitLogicalTrue(G2ProcedureParser.LogicalTrueContext ctx) { 
		buf.append("True"); 
		return null; 
	}


	@Override 
	public Object visitSubsequentArgInList(G2ProcedureParser.SubsequentArgInListContext ctx) { 
		visit(ctx.arglist());
		buf.append(ctx.COMMA().getText());
		visit(ctx.arg());
		return null;
	}
	@Override 
	public Object visitSubsequentExpressionInList(G2ProcedureParser.SubsequentExpressionInListContext ctx) {
		visit(ctx.exprlist());
		buf.append(ctx.COMMA().getText());
		visit(ctx.expr());
		return null;
	}
	@Override
	public Object visitSubsequentVarInList(G2ProcedureParser.SubsequentVarInListContext ctx) { 
		visit(ctx.varlist());
		buf.append(ctx.COMMA().getText());
		visit(ctx.variable());
		return null; 
	}
	@Override 
	public Object visitVariableArray(G2ProcedureParser.VariableArrayContext ctx) { 
		String var = ctx.G2NAME().getText();
		buf.append(pythonName(var,false));
		buf.append("[");
		visit(ctx.expr());
		buf.append("]");
		return null; 
	}
	@Override 
	public Object visitVariableNamed(G2ProcedureParser.VariableNamedContext ctx) {
		String var = ctx.G2NAME().getText();
		buf.append(pythonName(var,false));
		return null; 
	}
	// ================================= End Overridden Methods =====================================
	
	private void recordError(String text,String context, String verb ) {
		String msg = "";
		if(verb==null) verb = "";
		if(verb.length()>0) verb = " "+verb;
		if( context!=null && context.length()>0 ) {
			msg = String.format("%s %s \'%s\'",text,verb,context);
			translation.put(TranslationConstants.ERR_TOKEN, context);
		}
		else {
			msg = String.format("%s%s",text,verb); // In this case verb may have an argument
		}
    	
    	log.info(TAG+":recordError:"+msg);
    	translation.put(TranslationConstants.ERR_MESSAGE, msg);
    	translation.put(TranslationConstants.ERR_LINE, "1");
    	translation.put(TranslationConstants.ERR_POSITION,"0");
    }

	// The G2 versions differ from our expression language
	private String translateEqualityOperator(String arg) {
		String result = arg;
		if( arg.equalsIgnoreCase("=")) result = "=";
		else if( arg.equalsIgnoreCase("/=")) result = "!=";
		return result;
	}
	// We've found instances of a space between a dash and digits
	// in negative numbers, Remove the space
	private String scrubNumber(String input) {
		String value = input;
		if( value.startsWith("- ") && value.length()>2)  value = "-"+value.substring(2); 
		return value;	
	}
	// Strip braces off of the input string
	private String stripBraces(String txt) {
		String result = txt;
		result = result.trim();
		if( result.endsWith("}") )   result = result.substring(0,result.length()-1);
		if( result.startsWith("{") ) result = result.substring(1);

		return result;
	}
	// Strip braces off of the input string
	private String stripBrackets(String txt) {
		String result = txt;
		result = result.trim();
		if( result.endsWith("]") )   result = result.substring(0,result.length()-1);
		if( result.startsWith("[") ) result = result.substring(1);
		return result;
	}
	// Strip quotes off of the input string
	private String stripQuotes(String txt) {
		String result = txt;
		result = result.trim();
		if( result.endsWith("\"") )   result = result.substring(0,result.length()-1);
		if( result.startsWith("\"") ) result = result.substring(1);
		
		return result;
	}
	/**
	 * Create the name and argument of a getter procedure that incorporates both 
	 * the class and the property. Use a mapping from declarations to deduce the 
	 * class of the variable. Then go one step further and get the corresponding
	 * python-class.
	 * @param property
	 * @param localVariable
	 * @return
	 */
	private String createGetterCall(String property,String localVariable) {
		property = pythonName(property,true);
		// If it's a "this", then replace
		if(selfEquivalent!=null && localVariable.equalsIgnoreCase(selfEquivalent) && selfArgument!=null) localVariable = selfArgument;
		String className = variableClassMap.get(localVariable);
		if( className!=null ) {
			String localClass = className;
			className = classLookup.get(localClass);
			if( className!=null) {
				className = getModuleName(className);
			}
			else {
				recordError("No class mapped for G2 class "+localClass,"", "" );
			}
		}
		else {
			recordError("No class declared for variable "+localVariable,"", "" );
		}
		
		localVariable = pythonName(localVariable,false);
		if(className == null) className = "";
		String procName = String.format("get%s%s(%s)", className,property,localVariable);
		importLookup.put("get"+className+property, "");
		return procName;
	}
	/**
	 * Create the name and first part of an argument list for a setter procedure
	 * that incorporates both the class and the property in its name. As a side effect,
	 * we add this to the list of necessary imports.
	 * NOTE: The calling entity must afdd the remaining arguments and a closing parend.
	 * @param property in raw G2 form
	 * @param localVariable
	 * @return
	 */
	private String createSetterCall(String property,String localVariable) {
		if(selfEquivalent!=null && localVariable.equalsIgnoreCase(selfEquivalent) && selfArgument!=null) localVariable = selfArgument;
		property = pythonName(property,true);
		String className = variableClassMap.get(localVariable);
		if( className!=null ) {
			String localClass = className;
			className = classLookup.get(localClass);
			if( className!=null) {
				className = getModuleName(className);
			}
			else {
				recordError("No class mapped for G2 class "+localClass,"", "" );
			}
		}
		else {
			recordError("No class declared for variable "+localVariable,"", "" );
		}
		
		localVariable = pythonName(localVariable,false);
		if(className == null) className = "";
		String procName = String.format("set%s%s(%s,", className,property,localVariable);
		importLookup.put("set"+className+property, "");
		return procName;
	}
	/**
	 * Given the full path name of a Python module plus method, return the method name.
	 * @param s the module
	 * @return the module name
	 */
	private String getMethodName(String s) {
		String[] components = s.split("[.]");
		return components[components.length-1];
	}
	/**
	 * Given the full path name of a Python module plus method, return the module name.
	 * @param s the module
	 * @return the module name
	 */
	private String getModuleName(String s) {
		String[] components = s.split("[.]");
		return components[components.length-2];
	}
	/**
	 * Given a the full name of a Python module, plus method, return the package.
	 * @param s the module
	 * @return the package
	 */
	private String getPackageName(String s) {
		int pos = s.lastIndexOf(".");
		if( pos>0) {
			s = s.substring(0, pos);
			// We want the second to last ...
			pos = s.lastIndexOf(".");
			if( pos>0) {
				s = s.substring(0, pos);
			}
		}
		return s;
	}
	/**
	 * Convert a G2 name into a camelCase name for use in Python
	 * @param name
	 * @return a name appropriate for Python
	 * @see http://stackoverflow.com/questions/1086123/titlecase-conversion
	 */
	private String pythonName(String s,boolean leadingCap) {
		// First do some lookups
		String substitution = constantLookup.get(s);
		if( substitution!=null) return "\""+substitution+"\"";
		
		// If it's a "this", then replace. 
		if(selfEquivalent!=null && s.equalsIgnoreCase(selfEquivalent) && 
				selfArgument!=null && !selfEquivalent.equalsIgnoreCase(selfArgument)  ) {
			return pythonName(selfArgument,false);
		}

	    final String ACTIONABLE_DELIMITERS = "-_"; // these cause the character following
	                                               // to be capitalized

	    StringBuilder sb = new StringBuilder();
	    boolean capNext = leadingCap;
	    boolean isDelimiter = false;
	    //log.infof("pythonName: IN=%s",s);
	    for (char c : s.toCharArray()) {
	    	isDelimiter = (ACTIONABLE_DELIMITERS.indexOf((int) c) >= 0);
	    	if( isDelimiter ) {
	    		capNext = true;
	    		continue;   // Skip delimiters
	    	}
	        c = (capNext)
	                ? Character.toUpperCase(c)
	                : Character.toLowerCase(c);
	        sb.append(c);
	        capNext = false;
	    }
	    //log.infof("pythonName: OUT=%s",sb.toString());
	    return sb.toString();
	}
	private String removeNewlines(String input) {
		String output = input.replaceAll("\n", " ");
		output = output.replaceAll("\r", " ");
		return output;
	}
	/**
	 * Append the correct statement indent to the buffer
	 */
	private void appendIndent(int indent) {
		int index=indent;
		//log.infof("appendIdent %d",index);
		while( index>0 ) {
			buf.append("   ");  // Three space indentation
			index--;
		}
	}
	// Use regular expressions to analyze string. Make appropriate
	// substitutions, add quotes around fixed strings. The resulting
	// string is appropriate for the logging function.
	private String extractActiveElementsForLogging(String input) {
		List<String> args = new ArrayList<>();
		//log.infof("extractActiveElementsForLogging: string is %s", input);
		// Order is important, so our initial pass is with the general pattern matcher.
		Matcher matcher = generalArgPattern.matcher(input);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String element = matcher.group();
			//log.infof("FOUND AN ARG: %s", element);
			args.add(element);
			matcher.appendReplacement(sb,"%s");
		}
		matcher.appendTail(sb);
		// We now have the main string with %s substitutions.
		// Now we need to convert the arguments
		for(String arg:args) {
			// The arg is a complete match of the pattern, including brackets
			// Try the member value first, it seems to be most common
			matcher = memberValuePattern.matcher(arg);
			if(matcher.matches()) {
				String member = matcher.group(1);
				String instance = matcher.group(2);
				sb.append(",");
				sb.append(createGetterCall(member,instance));
			}
			else {
				matcher = singleVariablePattern.matcher(arg);
				if(matcher.matches()) {
					arg = matcher.group();
					arg = stripBrackets(arg);
					arg = pythonName(arg,false);
					sb.append(String.format(",%s.toString()",arg));
				}
				else {
					// In desparation, we just add the contents as a string
					arg = stripBrackets(arg);
					sb.append(String.format(",\"%s\"",arg));
				}
			}
		}
		return sb.toString();
	}
	// Use regular expressions to analyze string. Make appropriate
	// substitutions, add quotes around fixed strings. The resulting
	// string is appropriate as a normal python string.
	private String extractActiveElementsForString(String input) {
		List<String> args = new ArrayList<>();
		//log.infof("extractActiveElementsForString: string is %s", input);
		// Order is important, so our initial pass is with the general pattern matcher.
		Matcher matcher = generalArgPattern.matcher(input);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String element = matcher.group();
			//log.infof("FOUND AN ARG: %s", element);
			args.add(element);
			matcher.appendReplacement(sb,"%s");
		}
		matcher.appendTail(sb);
		if( args.size()>0 ) {
			sb.append("%(");
			int count = 0;
			// We now have the main string with %s substitutions.
			// Now we need to convert the arguments
			for(String arg:args) {
				if(count>0) sb.append(",");
				// The arg is a complete match of the pattern, including brackets
				// Try the member value first, it seems to be most common
				matcher = memberValuePattern.matcher(arg);
				if(matcher.matches()) {
					String member = matcher.group(1);
					String instance = matcher.group(2);
					sb.append(createGetterCall(member,instance));
				}
				else {
					matcher = singleVariablePattern.matcher(arg);
					if(matcher.matches()) {
						arg = matcher.group();
						arg = stripBrackets(arg);
						arg = pythonName(arg,false);
						sb.append(String.format("str(%s)",arg));
					}
					else {
						// In desparation, we just add the contents as a string
						arg = stripBrackets(arg);
						sb.append(String.format("\"%s\"",arg));
					}
				}
				count++;
			}
			sb.append(")");
		}
		return sb.toString();
	}
}
