package com.ils.python.translation;
/**
 *   (c) 2013-2014  ILS Automation. All rights reserved.
 */


import java.util.HashMap;

import com.ils.g2.procedure.G2ProcedureBaseVisitor;
import com.ils.g2.procedure.G2ProcedureParser;
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
	
	/**
	 * Constructor.
	 * @param dict results dictionary
	 */
	public PythonGenerator(HashMap<String,Object> t) {
		log = LogUtil.getLogger(getClass().getPackage().getName());
		this.buf = new StringBuffer();
		this.translation = t;
	}
	/**
	 * @return the StringBuffer constructed as part of the visiting. This is the code.
	 */
	public StringBuffer getTranslation() { return buf; }
	
	// ================================= Overridden Methods =====================================
	// In the variable list all we need is the variable name
	@Override 
	public Object visitArgDeclaration(G2ProcedureParser.ArgDeclarationContext ctx) { 
		buf.append(ctx.VARNAME().toString());
		return null;
	}
	@Override 
	public Object visitFirstArgInList(G2ProcedureParser.FirstArgInListContext ctx) { 
		visit(ctx.arg());
		return null; 
	}
	// The docstring is saved off separately. Written a
	@Override 
	public Object visitProcedureDocstring(G2ProcedureParser.ProcedureDocstringContext ctx) {
		StringBuffer doc = new StringBuffer();
		doc.append("'''\n");
		doc.append(ctx.COMMENT().getText());
		doc.append("\n'''");
		translation.put(TranslationConstants.PY_DOC_STRING, doc);
		return null; 
	}
	
	// Create the header. All methods are "evalate".
	@Override 
	public Object visitProcedureHeader(G2ProcedureParser.ProcedureHeaderContext ctx) { 
		buf.append("def evaluate(");
		visit(ctx.arglist());
		buf.append("):");
		translation.put(TranslationConstants.PY_INDENT, new Integer(1));
		return null;
	}
	
	// Convert the G2Name to a Python name. Record in the dictionary.
	@Override 
	public Object visitProcedureName(G2ProcedureParser.ProcedureNameContext ctx) { 
		String pyName = pythonName(ctx.PNAME().getText());
		translation.put(TranslationConstants.PY_MODULE_NAME, pyName);
		translation.put(TranslationConstants.PY_G2_PROC, ctx.PNAME());
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
			msg = String.format("%s%s \'%s\'",text,verb,context);
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
	
	
}
