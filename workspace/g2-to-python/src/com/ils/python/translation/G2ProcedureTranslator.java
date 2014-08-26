/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */
package com.ils.python.translation;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.ils.g2.procedure.G2ProcedureLexer;
import com.ils.g2.procedure.G2ProcedureParser;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;



public class G2ProcedureTranslator {
	private final static String TAG = "Translator";
	private static final String USAGE = "Usage: translate <dir>";
	
	private LoggerEx log;
	private HashMap<String,Object> translationResults = null;
	private String packageName = "";                  // No package by default
	private File targetDirectory = new File(".");     // Current directory
	
	
	public G2ProcedureTranslator() {
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
	}
	
	
	/**
	 * Read standard input. Convert into Python method
	 */
	public void processInput() {
		
		// Read of stdin is expected to be from a re-directed file. 
		// We gobble the whole thing here. Scrub out CR
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		StringBuffer input = new StringBuffer();
		String s = null;
		try{
			while ((s = in.readLine()) != null && s.length() != 0) {
				s = s.replaceAll("\r", "");
				input.append(s);
			}
		}
		catch(IOException ignore) {}
		
		// Now run through ANTLR parser
		try {
			translationResults = translateProcedure(input.toString(),packageName);
		}
		catch(Exception ex) {
			System.err.println(String.format("%s: Parsing exception (%s)",TAG,ex.getMessage()));;
		}
	}
	
	/**
	 * Write the translated code to std out
	 */
	public void createOutput() {
		if( translationResults==null) return;      // Message printed with exception.
		
		// Check for errors. If any, these go on std error.
		String errmsg = (String)translationResults.get(TranslationConstants.ERR_MESSAGE);
		if( errmsg!=null ) {
			Integer line = (Integer)translationResults.get(TranslationConstants.ERR_LINE);
			Integer pos = (Integer)translationResults.get(TranslationConstants.ERR_POSITION);
			String tkn = (String)translationResults.get(TranslationConstants.ERR_TOKEN);
			
			String msg = String.format("%s: ERROR: %s, %s%s%s",TAG,errmsg,
					(line==null?"":"line:"+String.valueOf(line.intValue())),
					(pos==null?"":":"+String.valueOf(pos.intValue())),
					(tkn==null?"":" at:"+tkn));
			System.err.println(msg);
		}
		else {
			// Make sure that output directories exist.
			// To start with, the target output must exist
			if( targetDirectory.isDirectory() ) {
				String[] pathSegments = packageName.split(".");
				String target = targetDirectory.getAbsolutePath();
				for(String seg:pathSegments) {
					String next = target+File.pathSeparator+seg;
					targetDirectory = new File(next);
					if( !targetDirectory.exists() ) {
						if( !targetDirectory.mkdir() ) {
							String msg = String.format("%s: ERROR: Failed to create output directory %s ",TAG,next);
							System.err.println(msg);
							return;       // Can't do anything more
						}
					}
				}
				// Target directory is ready
				String fname = (String)translationResults.get(TranslationConstants.PY_MODULE_NAME);
				if( fname!=null ) {
					File outFile = new File(targetDirectory+File.pathSeparator+fname+".py");
				    try {
				    	FileWriter writer = new FileWriter(outFile, true);
				        PrintWriter printer = new PrintWriter(writer);
				        printer.append(getCopyright());
				        printer.append(getDocstring());
				        printer.append(getImports());
				        printer.append(getCode());
				        printer.close();
				    } 
				    catch (IOException ioe) {
				    	String msg = String.format("%s: ERROR: Writing to %s (%s)",TAG,outFile.getAbsolutePath(),ioe.getLocalizedMessage());
						System.err.println(msg); 
				    }
				}
				else {
					String msg = String.format("%s: ERROR: Parse failed to find a procedure name",TAG);
					System.err.println(msg);
				}
				
			}
			else {
				String msg = String.format("%s: ERROR: Target directory %s does not exist",TAG,
						targetDirectory.getAbsolutePath());
				System.err.println(msg);
			}
		}	
	}
	
	// ====================== Setters/Getters ========================
	public String getPackageName() {return packageName;}
	public void setPackageName(String packageName) {this.packageName = packageName;}
	public File getTargetDirectory() {return targetDirectory;}
	public void setTargetDirectory(File targetDirectory) {this.targetDirectory = targetDirectory;}
	

	private String getCopyright() {
		return "Copyright 2014 ILS Automation. All rights reserved.";
	}
	private String getImports() {
		List<String> imports = (List<String>)translationResults.get(TranslationConstants.PY_IMPORTS);
		StringBuffer result = new StringBuffer();
		for(String imp:imports) {
			result.append(imp);
			result.append("\n");
		}
		return result.toString();
	}
	private String getCode() {
		String code = (String)translationResults.get(TranslationConstants.PY_MODULE_CODE);
		if( code==null ) code = "";
		return code;
	}
	
	private String getDocstring() {
		String doc = (String)translationResults.get(TranslationConstants.PY_DOC_STRING);
		if( doc==null ) doc = "";
		return doc;
	}
	
	/**
	 * This is the method for translation of a procedure. It uses the visitor pattern to
	 * traverse the parse tree and generate the output expression.
	 * 
	 * @param proc text of a G2 procedure
	 * @param packageName a Python package for procedure references
	 * @return a map containing the translated procedure and other ancillary information
	 */
	private HashMap<String,Object> translateProcedure(String proc, String packageName) throws Exception {
		HashMap<String,Object> pyMap = new HashMap<String,Object>();
		
		// Convert the input expression to a stream
		log.debug(TAG+": Parsing "+proc);
		pyMap.put(TranslationConstants.PY_G2_PROC, proc);
		pyMap.put(TranslationConstants.PY_PACKAGE, packageName);
		ByteArrayInputStream bais = new ByteArrayInputStream(proc.getBytes());
		ANTLRInputStream in = new ANTLRInputStream(bais);
		G2ProcedureLexer lexer = new QuietLexer(in);
		lexer.removeErrorListeners();  // Quiet lexer gripes
		//lexer.addErrorListener(new ExpressionLexerErrorListener());
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		G2ProcedureParser parser = new G2ProcedureParser(tokens);
		parser.removeErrorListeners(); // remove default error listener
	    parser.addErrorListener(new ProcedureErrorListener(pyMap));
		parser.setErrorHandler(new ProcedureErrorStrategy(pyMap));
		ParseTree tree = parser.procedure();   // Start with definition of a logical expression.
		PythonGenerator visitor = new PythonGenerator(pyMap);
		visitor.visit(tree);
		StringBuffer procedure = visitor.getTranslation();  // Procedure less imports
		pyMap.put(TranslationConstants.PY_PRELIM,procedure.toString());

		return pyMap;
	}
	/**
	 * Entry point for the application. 
	 * Usage: translate
	 * 
	 * NOTE: For Windows, specify path as: C:/home/work/method.txt
	 *       For Mac/Linux:    /home/work/method.txt
	 * We automatically adjust windows path, if specified with backslashes.
	 * 
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
			
		// Look for database path as an argument
		if( args.length == 0) {
			System.out.println(USAGE);
			System.exit(1);
		}
		// Some of the embedded jars use log4j - redirect to std error. Log level is system property "log.level"
		ConsoleAppender appender = new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN),"System.err");
		BasicConfigurator.configure(appender);
		String levelString = System.getProperty("log.level");
		Level level = Level.WARN;
		if( levelString!=null) level = Level.toLevel(levelString);
        Logger.getRootLogger().setLevel(level); //set log level
        
        G2ProcedureTranslator trans = new G2ProcedureTranslator();

        // Read system properties to obtain the python package of the generated module.
        String pythonPackage = System.getProperty("procedure.package");   
		if( pythonPackage!=null) {
			trans.setPackageName(pythonPackage);
		}
		
		// Analyze command-line argument to obtain the target directory name
		if( args.length>0 ) {
			String path = args[0];
			// In case we've been fed a Windows path, convert
			path = path.replace("\\", "/");
			File dir = new File(path);
			trans.setTargetDirectory(dir);
		}
		
		trans.processInput();
	}

}
