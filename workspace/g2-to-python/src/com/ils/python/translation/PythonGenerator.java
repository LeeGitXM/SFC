package com.ils.python.translation;
/**
 *   (c) 2013-2014  ILS Automation. All rights reserved.
 */


import java.util.HashMap;

import com.ils.g2.procedure.G2ProcedureBaseVisitor;
import com.ils.g2.procedure.G2ProcedureParser;
import com.ils.g2.procedure.G2ProcedureParser.StatementContext;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;


/**
 *  This translator overrides the generated G2ExpressionVisitor
 *  with customizations that output the desired expression.
 */
public class PythonGenerator extends G2ProcedureBaseVisitor<Object>  {
	private static final String TAG = "PythonGenerator";
	private LoggerEx log;
	private final StringBuffer buf;
	private final HashMap<String,Object> translation;
	private int currentIndent = 0;
	private final HashMap<String,String> constantLookup;
	/**
	 * Constructor.
	 * @param dict results dictionary
	 */
	public PythonGenerator(HashMap<String,Object> t,HashMap<String,HashMap<String,String>> mapOfMaps) {
		log = LogUtil.getLogger(getClass().getPackage().getName());
		this.buf = new StringBuffer();
		this.translation = t;
		constantLookup = mapOfMaps.get(TranslationConstants.MAP_CONSTANTS);
	}
	/**
	 * @return the StringBuffer constructed as part of the visiting. This is the code.
	 */
	public StringBuffer getTranslation() { return buf; }
	
	// ================================= Overridden Methods =====================================
	// In the variable list all we need is the variable name
	@Override 
	public Object visitArgDeclaration(G2ProcedureParser.ArgDeclarationContext ctx) { 
		buf.append(ctx.G2NAME().toString());
		return null;
	}
	@Override 
	public Object visitAssignmentStatement(G2ProcedureParser.AssignmentStatementContext ctx) {
		appendIndent();
		buf.append(String.format("%s = ",ctx.G2NAME().getText()));
		visit(ctx.expr());
		buf.append("\n");
		return null; 
	}
	@Override 
	public Object visitBraceComment(G2ProcedureParser.BraceCommentContext ctx) {
		appendIndent();
		buf.append("# ");
		buf.append(stripBraces(ctx.COMMENT().getText()));
		buf.append("\n");
		return null; 
	}
	@Override 
	public Object visitBeginBlock(G2ProcedureParser.BeginBlockContext ctx) {
		currentIndent++;
		for(StatementContext sctx:ctx.statement()) {
			visit(sctx);
		}
		currentIndent--;
		return null; 
	}
	// Implement a G2 for .. as a Python while ...
	@Override 
	public Object visitCountdownFor(G2ProcedureParser.CountdownForContext ctx) { 
		int icount = ctx.iexpr().size();
		if( icount>1) {
			String varname = ctx.G2NAME().getText();
			String start = ctx.iexpr(0).getText();
			String end   = ctx.iexpr(1).getText();
			String decrement = ctx.ivalue().getText();
			appendIndent();
			buf.append(String.format("%s = %s\n",varname,start));
			
			appendIndent();
			buf.append(String.format("while %s >= %s:\n",varname,end));
			currentIndent++;
			visit(ctx.statement());
			appendIndent();
			buf.append(String.format("%s = %s %s\n",varname,varname,decrement));
			currentIndent--;
		}
		else {
			recordError("Incorrect for loop syntax",ctx.start.getText(),"following");
		}
		return null; 
	}
	@Override 
	public Object visitFirstArgInList(G2ProcedureParser.FirstArgInListContext ctx) { 
		visit(ctx.arg());
		return null; 
	}
	@Override 
	public Object visitIfThenClause(G2ProcedureParser.IfThenClauseContext ctx) {
		appendIndent();
		buf.append("if ");
		visit(ctx.lexpr());
		buf.append(":\n");
		currentIndent++;
		visit(ctx.statement());
		currentIndent--;
		return null; 
	}
	@Override 
	public Object visitInitializedVariable(G2ProcedureParser.InitializedVariableContext ctx) {
		appendIndent();
		buf.append(String.format("%s = %s\n",ctx.G2NAME().getText(),ctx.value().getText()));
		return null; 
	}
	@Override 
	public Object visitIntegerValue(G2ProcedureParser.IntegerValueContext ctx) {
		buf.append(ctx.INTEGER().getText());
		return null; 
	}
	@Override 
	public Object visitIntExpressionOperator(G2ProcedureParser.IntExpressionOperatorContext ctx) { 
		visit(ctx.iexpr(0));
		buf.append(String.format(" %s ",ctx.OPR().getText()));
		visit(ctx.iexpr(1));
		return null; 
	}
	@Override 
	public Object visitIntParentheses(G2ProcedureParser.IntParenthesesContext ctx) {
		buf.append(ctx.POPEN().getText());
		visit(ctx.iexpr());
		buf.append(ctx.PCLOSE().getText());
		return null; 
	}
	@Override 
	public Object visitIntVariable(G2ProcedureParser.IntVariableContext ctx) {
		buf.append(ctx.G2NAME().getText());
		return null; 
	}
	@Override
	public Object visitLogicalFalse(G2ProcedureParser.LogicalFalseContext ctx) { 
		buf.append("False"); 
		return null; 
	}
	@Override 
	public Object visitLogicalOperator(G2ProcedureParser.LogicalOperatorContext ctx) {
		visit(ctx.lexpr(0));
		buf.append(String.format(" %s ",ctx.LOPR().getText().toLowerCase()));
		visit(ctx.lexpr(1));
		return null; 
	}
	@Override
	public Object visitLogicalTrue(G2ProcedureParser.LogicalTrueContext ctx) { 
		buf.append("True"); 
		return null; 
	}
	@Override 
	public Object visitNumericValue(G2ProcedureParser.NumericValueContext ctx) {
		buf.append(ctx.nvalue().getText());
		return null; 
	}
	@Override 
	public Object visitNumericVariable(G2ProcedureParser.NumericVariableContext ctx) {
		buf.append(ctx.G2NAME().getText());
		return null; 
	}
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
		currentIndent = 1;
		String name = ctx.G2NAME().getText();
		translation.put(TranslationConstants.PY_MODULE_NAME, pythonName(name));
		translation.put(TranslationConstants.PY_G2_PROC, name);
		
		return null;
	}
	@Override 
	public Object visitRelationalOperator(G2ProcedureParser.RelationalOperatorContext ctx) { 
		visit(ctx.nexpr(0));
		if(ctx.EQU()!=null)       buf.append(" == ");
		else if(ctx.NEQU()!=null) buf.append(" != ");
		else if(ctx.ROPR()!=null) buf.append(String.format(" %s ",ctx.ROPR().getText()));
		visit(ctx.nexpr(1));
		return null; 
	}
	@Override 
	public Object visitReturnStatement(G2ProcedureParser.ReturnStatementContext ctx) {
		appendIndent();
		buf.append("return ");
		visit(ctx.expr());
		buf.append("\n");
		return null; 
	}
	@Override 
	public Object visitSubsequentArgInList(G2ProcedureParser.SubsequentArgInListContext ctx) { 
		buf.append(ctx.COMMA().getText());
		visit(ctx.arg());
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
	// Strip quotes off of the input string
	private String stripQuotes(String txt) {
		String result = txt;
		result = result.trim();
		if( result.endsWith("\"") )   result = result.substring(0,result.length()-1);
		if( result.startsWith("\"") ) result = result.substring(1);
		
		return result;
	}
	
	/**
	 * Convert a G2 name into a camelCase name for use in Python
	 * @param name
	 * @return a name appropriate for Python
	 * @see http://stackoverflow.com/questions/1086123/titlecase-conversion
	 */
	private String pythonName(String s) {

	    final String ACTIONABLE_DELIMITERS = "-_"; // these cause the character following
	                                               // to be capitalized

	    StringBuilder sb = new StringBuilder();
	    boolean capNext = true;
	    boolean isDelimiter = false;

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
	    return sb.toString();
	}
	/**
	 * Append the correct statement indent to the buffer
	 */
	private void appendIndent() {
		int index=currentIndent;
		while( index>0 ) {
			buf.append("   ");  // Three space indentation
			index--;
		}
	}
	
}
