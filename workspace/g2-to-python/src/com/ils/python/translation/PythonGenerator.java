package com.ils.python.translation;
/**
 *   (c) 2013-2014  ILS Automation. All rights reserved.
 */


import java.util.HashMap;

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
	private final HashMap<String,Object> translation;
	private final HashMap<String,String> constantLookup;
	private final HashMap<String,String> importLookup;
	private int currentIndent = 0;
	private String selfArgument = null;     // Name of the first argument
	private String selfEquivalent = null;	// Argument used for "this procedure".	
	/**
	 * Constructor.
	 * @param dict results dictionary
	 */
	public PythonGenerator(HashMap<String,Object> t,HashMap<String,HashMap<String,String>> mapOfMaps) {
		log = LogUtil.getLogger(getClass().getPackage().getName());
		this.buf = new StringBuffer();
		this.translation = t;
		constantLookup = mapOfMaps.get(TranslationConstants.MAP_CONSTANTS);
		importLookup = mapOfMaps.get(TranslationConstants.MAP_IMPORTS);
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
		String name = pythonName(ctx.G2NAME().getText(),true);
		translation.put(TranslationConstants.PY_MODULE_NAME, pythonName(name,true));
		translation.put(TranslationConstants.PY_G2_PROC, name);

		return null;
	}
	// ================================= Declaration Methods ===================================
	@Override 
	public Object visitDeclarationInitialized(G2ProcedureParser.DeclarationInitializedContext ctx) {
		appendIndent(1);      // Declarations always indented 1
		buf.append(String.format("%s = %s\n",pythonName(ctx.G2NAME().getText(),false),ctx.value().getText()));
		return null; 
	}
	@Override public Object visitDeclarationSelf(G2ProcedureParser.DeclarationSelfContext ctx) {
		selfEquivalent = ctx.G2NAME().getText();
		return null;
	}
	// ================================= Expression Methods ====================================
	@Override public Object visitExprClassMember(G2ProcedureParser.ExprClassMemberContext ctx) {
		// In this context, we have a setter
		String member = ctx.G2NAME(0).getText();
		String clss = ctx.G2NAME(1).getText();
		buf.append(String.format(" %s.get%s()",pythonName(clss,true),pythonName(member,true)));
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
	@Override 
	public Object visitExprValue(G2ProcedureParser.ExprValueContext ctx) {
		String val = ctx.value().getText();
		//log.infof("ExprValue: %s", val);
		buf.append(val);
		return null; 
	}
	@Override 
	public Object visitExprVariable(G2ProcedureParser.ExprVariableContext ctx) {
		String var = ctx.variable().getText();
		//log.infof("ExprVariable: %s", var);
		buf.append(var);
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
	@Override 
	public Object visitStatementBlock(G2ProcedureParser.StatementBlockContext ctx) {
		currentIndent++;
		log.infof("BLOCK Statement %d",currentIndent);
		for(StatementContext sctx:ctx.statement()) {
			visit(sctx);
		}
		currentIndent--;
		return null; 
	}
	@Override 
	public Object visitStatementConclusion(G2ProcedureParser.StatementConclusionContext ctx) {
		appendIndent(currentIndent);
		if( ctx.casetter() !=null ) {
			visit(ctx.casetter());     // Does not include the argument
			buf.append("(");
			visit(ctx.expr());
			buf.append(")");
		}
		else if(ctx.variable()!=null   ) { 
			visit(ctx.variable());
			buf.append("=");
			visit(ctx.expr());
		}
		buf.append("\n");
		return null; 
	}
	@Override 
	public Object visitStatementPost(G2ProcedureParser.StatementPostContext ctx) 
	{ 
		importLookup.put("LogUtil","from com.inductiveautomation.ignition.common.util import LogUtil");
		importLookup.put("LoggerEx","from com.inductiveautomation.ignition.common.util import LoggerEx");
		appendIndent(currentIndent);
		buf.append("log = LogUtil.getLogger("+LOG_PACKAGE+")\n");
		appendIndent(currentIndent);
		String raw = ctx.STRING().getText();
		buf.append("log.infof("+raw+")\n");
		return null;
	}
	@Override 
	public Object visitStatementReturn(G2ProcedureParser.StatementReturnContext ctx) {
		buf.append("return ");
		visit(ctx.expr());
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
	// ================================= Helper Methods ========================================
	// In the variable list all we need is the variable name
	@Override 
	public Object visitArgDeclaration(G2ProcedureParser.ArgDeclarationContext ctx) { 
		buf.append(pythonName(ctx.G2NAME().toString(),false));
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
	@Override 
	public Object visitFirstArgInList(G2ProcedureParser.FirstArgInListContext ctx) { 
		visit(ctx.arg());
		selfArgument =pythonName(ctx.arg().getText(),false);
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
	private String pythonName(String s,boolean leadingCap) {
		// First do some lookups
		String substitution = constantLookup.get(s);
		if( substitution!=null) return substitution;
		
		// Is it a "this" replacement
		if(selfEquivalent!=null && s.equalsIgnoreCase(selfEquivalent) && selfArgument!=null) return selfArgument;

	    final String ACTIONABLE_DELIMITERS = "-_"; // these cause the character following
	                                               // to be capitalized

	    StringBuilder sb = new StringBuilder();
	    boolean capNext = leadingCap;
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
	private void appendIndent(int indent) {
		int index=indent;
		//log.infof("appendIdent %d",index);
		while( index>0 ) {
			buf.append("   ");  // Three space indentation
			index--;
		}
	}
}
