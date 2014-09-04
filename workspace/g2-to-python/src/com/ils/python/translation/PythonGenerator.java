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
		constantLookup = mapOfMaps.get(TranslationConstants.MAP_CONSTANTS);
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
		doc.append(stripBraces(ctx.COMMENT().getText()));
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
		translation.put(TranslationConstants.PY_MODULE_NAME, pythonName(name,true));
		translation.put(TranslationConstants.PY_G2_PROC, name);

		return null;
	}
	// ================================= Declaration Methods ===================================
	@Override 
	public Object visitDeclarationInitialized(G2ProcedureParser.DeclarationInitializedContext ctx) {
		appendIndent(1);          // Declarations always indented 1
		buf.append(String.format("%s = %s\n",pythonName(ctx.G2NAME().getText(),false),ctx.value().getText()));
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
		log.infof("CALL");
		buf.append("CALL");
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
		String property = pythonName(ctx.G2NAME(0).getText(),true);
		String clss = pythonName(ctx.G2NAME(1).getText(),false);
		buf.append(String.format("get%s(%s)",property,clss));
		importLookup.put("get"+property, "");
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
		log.infof("ExprOperator %d",currentIndent);
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
		log.infof("BLOCK Statement %d",currentIndent);
		if( ctx.blockerr() != null) {
			buf.append("try:\n");
			currentIndent++;
		}
		for(StatementContext sctx:ctx.statement()) {
			visit(sctx);
		}
		if( ctx.blockerr() != null) {
			currentIndent--;
			visit(ctx.blockerr());
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
	public Object visitStatementConclusion(G2ProcedureParser.StatementConclusionContext ctx) {
		if( ctx.casetter() !=null ) {
			visit(ctx.casetter());     // Does not include the argument, nor closing parend
			visit(ctx.expr());
			buf.append(")");
		}
		else if(ctx.variable()!=null   ) { 
			buf.append("CONCLUDE VAR=");
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
		appendIndent(currentIndent);
		visit(ctx.sfragment());
		buf.append("\n");
		int count = ctx.COMMENT().size();
		int index = 0;
		while( index<count) {
			appendIndent(currentIndent);
			buf.append("# ");
			buf.append(stripBraces(ctx.COMMENT(index).getText()));
			buf.append("\n");
			index++;
		}	
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
		text = extractActiveElementsForLogging(text);
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
		buf.append(stripBraces(ctx.COMMENT().getText()));
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
			String property = pythonName(ctx.G2NAME(0).getText(),true);
			String instance = pythonName(ctx.G2NAME(1).getText(),false);
			buf.append(String.format("get%s(%s)", property,instance));
			importLookup.put("get"+property, "");
		}
		else {
			recordError("Incorrect classs getter syntax",ctx.start.getText(),"following");
		}
		return null; 
	}
	// Implement as a setter method.  Alternatively we can use a procedure setter
	// dot notation.  "The property of instance" 
	// WARNING: Needs insertion of arguments and a final end parenthesis.
	@Override 
	public Object visitClassAttributeSetter(G2ProcedureParser.ClassAttributeSetterContext ctx) {
		if( ctx.G2NAME().size() > 1 ) {
			String property = pythonName(ctx.G2NAME(0).getText(),true);
			String instance = pythonName(ctx.G2NAME(1).getText(),false);
			buf.append(String.format("set%s(%s,", property,instance));
			importLookup.put("set"+property,"");
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
		log.infof("PARENT class = %s",ctx.getParent().getClass().getName());
		ParseTree child = ctx.getParent().getChild(0);
		log.infof("CHILD local variable = %s",child.getText());
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
		selfArgument = pythonName(name,false);
		return null; 
	}
	// This is a clause, so the indent is already made.
	@Override 
	public Object visitIfWithClauses(G2ProcedureParser.IfWithClausesContext ctx) {
		buf.append("if ");
		visit(ctx.expr());
		buf.append(":\n");
		currentIndent++;
		// Since this is a fragment, we need to supply indent
		appendIndent(currentIndent);
		visit(ctx.sfragment());
		buf.append("\n");
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
    	
    	log.info(TAG+msg);
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
	 * Given the full path name of a Python module, return the module name.
	 * @param s the module
	 * @return the package
	 */
	private String getModuleName(String s) {
		String[] components = s.split("[.]");
		return components[components.length-1];
	}
	/**
	 * Given a the name of a full Python module, return the package.
	 * @param s the module
	 * @return the package
	 */
	private String getPackageName(String s) {
		int pos = s.lastIndexOf(".");
		if( pos>0) {
			return s.substring(0, pos);
		}
		else {
			return s;
		}
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
		
		// Is it a "this" replacement
		if(selfEquivalent!=null && s.equalsIgnoreCase(selfEquivalent) && selfArgument!=null) return selfArgument;

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
		log.infof("extractActiveElementsForLogging: string is %s", input);
		// Order is important, so our initial pass is with the general pattern matcher.
		Matcher matcher = generalArgPattern.matcher(input);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String element = matcher.group();
			//log.infof("FOUND AN ARG: %s", element);
			element = pythonName(element,false);
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
				member = pythonName(member,true);
				String instance = matcher.group(2);
				instance = pythonName(instance,false);
				sb.append(String.format(",get%s(%s).toString()",member,instance));
				importLookup.put("get"+member, "");
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
}
