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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.sqlite.JDBC;

import com.ils.g2.procedure.G2ProcedureLexer;
import com.ils.g2.procedure.G2ProcedureParser;
import com.ils.python.lookup.ClassMapper;
import com.ils.python.lookup.EnumerationMapper;
import com.ils.python.lookup.GlobalMapper;
import com.ils.python.lookup.ProcedureMapper;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;



public class G2ProcedureTranslator {
	private final static String TAG = "Translator";
	private static final String USAGE = "Usage: translate <dir> <dbpath>";
	@SuppressWarnings("unused")
	private final static JDBC driver = new JDBC(); // Force driver to be loaded
	private final LoggerEx log;
	private final ClassMapper classMapper;
	private final EnumerationMapper enumMapper;
	private final GlobalMapper globalMapper;
	private final ProcedureMapper procedureMapper;
	private Map<String,Map<String,String>> mapOfMaps;
	private Map<String,Object> translationResults = null;
	private String packageName = "";                  // No package by default
	private File targetDirectory = new File(".");     // Current directory
	
	
	public G2ProcedureTranslator() {
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.classMapper = new ClassMapper();
		this.enumMapper  = new EnumerationMapper(); 
		this.globalMapper = new GlobalMapper();
		this.procedureMapper = new ProcedureMapper();
		this.mapOfMaps = new HashMap<>();
	}
	public void processDatabase(String path) {
		String connectPath = "jdbc:sqlite:"+path;

		// Read database to generate conversion maps
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(connectPath);
			mapOfMaps.put(TranslationConstants.MAP_CLASSES, classMapper.createMap(connection));
			mapOfMaps.put(TranslationConstants.MAP_ENUMERATIONS, enumMapper.createMap(connection));
			mapOfMaps.put(TranslationConstants.MAP_IMPORTS, new HashMap<String,String>());
			mapOfMaps.put(TranslationConstants.MAP_PROCEDURES, procedureMapper.createMap(connection));
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			System.err.println(TAG+": "+e.getMessage());
		}
		finally {
			try {
				if(connection != null)
					connection.close();
			} 
			catch(SQLException e) {
				// connection close failed.
				System.err.println(TAG+": "+e.getMessage());
			}
		}
	}
	
	/**
	 * Read standard input. Convert into Python method
	 */
	public void processInput() {
		
		// Read of stdin is expected to be from a re-directed file. 
		// We gobble the whole thing here. Scrub out CR.
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		StringBuffer input = new StringBuffer();
		String s = null;
		try{
			while ((s = in.readLine()) != null ) {
				s = s.replaceAll("\r", "");
				input.append(s);
				input.append("\n");
			}
		}
		catch(IOException ignore) {}
		
		// Now run through ANTLR parser
		try {
			translationResults = translateProcedure(input.toString(),packageName);
		}
		catch(IOException ioe) {
			System.err.println(String.format("%s: Parsing exception (%s)",TAG,ioe.getMessage()));;
		}
	}
	
	/**
	 * Write the translated code to std out. All of the TranslationConstants are strings
	 */
	public void createOutput() {
		if( translationResults==null) return;      // Message printed with exception.
		
		// Check for errors. If any, these go on std error.
		String errmsg = (String)translationResults.get(TranslationConstants.ERR_MESSAGE);
		if( errmsg!=null ) {
			String line = (String)translationResults.get(TranslationConstants.ERR_LINE);
			String pos = (String)translationResults.get(TranslationConstants.ERR_POSITION);
			String tkn = (String)translationResults.get(TranslationConstants.ERR_TOKEN);
			
			String msg = String.format("%s: ERROR: %s, %s%s%s",TAG,errmsg,
					(line==null?"":"line:"+line),
					(pos==null?"":":"+pos),
					(tkn==null?"":" at:"+tkn));
			log.errorf(msg);
		}
		else {
			// Make sure that output directories exist.
			// To start with, the target output must exist
			if( targetDirectory.isDirectory() ) {
				String[] pathSegments = packageName.split("[.]");
				String targetPath = targetDirectory.getAbsolutePath();
				for(String seg:pathSegments) {
					targetPath = targetPath+File.separator+seg;
					targetDirectory = new File(targetPath);
					if( !targetDirectory.exists() ) {
						if( !targetDirectory.mkdir() ) {
							String msg = String.format("%s: ERROR: Failed to create output directory %s ",TAG,targetPath);
							log.errorf(msg);
							return;       // Can't do anything more
						}
					}
				}
				// Target directory is ready
				String fname = (String)translationResults.get(TranslationConstants.PY_MODULE);
				if( fname!=null ) {
					File outFile = new File(targetPath+File.separator+fname+".py");
					log.infof("%s.createOutput: outfile = %s",TAG,outFile.getAbsolutePath());
					
				    try {
				    	FileWriter writer = new FileWriter(outFile, false);  // Do not append
				        PrintWriter printer = new PrintWriter(writer);
				        printer.append(getCopyright());
				        printer.append(getDocstring());
				        printer.append(getImports());
				        printer.append(getCode());
				        printer.close();
				    } 
				    catch (IOException ioe) {
				    	String msg = String.format("%s: ERROR: Writing to %s (%s)",TAG,outFile.getAbsolutePath(),ioe.getLocalizedMessage());
						log.errorf(msg); 
				    }
				}
				else {
					String msg = String.format("%s: ERROR: Parse failed to find a procedure name",TAG);
					log.errorf(msg);
				}
				
			}
			else {
				String msg = String.format("%s: ERROR: Target directory %s does not exist",TAG,
						targetDirectory.getAbsolutePath());
				log.errorf(msg);
			}
		}	
	}
	
	// ====================== Setters/Getters ========================
	public String getPackageName() {return packageName;}
	public void setPackageName(String packageName) {this.packageName = packageName;}
	public File getTargetDirectory() {return targetDirectory;}
	public void setTargetDirectory(File targetDirectory) {this.targetDirectory = targetDirectory;}
	

	private String getCopyright() {
		return "# Copyright 2014 ILS Automation. All rights reserved.\n";
	}
	private String getImports() {
		Map<String,String> imports = mapOfMaps.get(TranslationConstants.PY_IMPORTS);
		String defaultPackage = (String)translationResults.get(TranslationConstants.PY_PACKAGE);
		StringBuffer result = new StringBuffer();
		if( imports!=null ) {
			for(String key:imports.keySet()) {
				String imp = imports.get(key);
				if( imp.length()>0) {
					result.append(imp);
					result.append("\n");
				}
				else {
					// Create the import from the default package
					result.append(String.format("from %s import %s\n",defaultPackage,key));
				}
			}
		}
		return result.toString();
	}
	private String getCode() {
		String code = (String)translationResults.get(TranslationConstants.PY_CODE);
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
	private HashMap<String,Object> translateProcedure(String proc, String packageName) throws IOException {
		HashMap<String,Object> pyMap = new HashMap<String,Object>();
		
		// Convert the input expression to a stream
		pyMap.put(TranslationConstants.PY_G2_CODE, proc);
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
		PythonGenerator visitor = new PythonGenerator(pyMap,mapOfMaps);
		visitor.visit(tree);
		StringBuffer procedure = visitor.getTranslation();  // Procedure less imports
		pyMap.put(TranslationConstants.PY_CODE,procedure.toString());
		if( log.isDebugEnabled() ) {
			dump(pyMap);
		}
		return pyMap;
	}
	
	/**
	 * For debugging purposes - dump the contents of the translation map
	 */
	private void dump(HashMap<String,Object> pyMap) {
		log.info("======================= G2 Code ======================");
		Object proc = pyMap.get(TranslationConstants.PY_G2_CODE);
		if( proc==null ) proc = "<null>";
		log.info("\n"+proc.toString());
		log.info("===================== Python Code =====================");
		proc = pyMap.get(TranslationConstants.PY_CODE);
		if( proc==null ) proc = "<null>";
		log.info("\n"+proc.toString());
		log.info("===================== Imports =====================");
		Map<String,String> imports = mapOfMaps.get(TranslationConstants.PY_IMPORTS);
		StringBuffer result = new StringBuffer();
		if( imports!=null ) {
			for(String imp:imports.keySet()) {
				log.info(imp);
			}
		}
		log.info("============================================================");
		Object val = pyMap.get(TranslationConstants.PY_PACKAGE);
		if( val==null ) val = "<null>";
		log.infof("Package:    %s",val.toString());
		val = pyMap.get(TranslationConstants.PY_G2_PROC);
		if( val==null ) val = "<null>";
		log.infof("G2 Procedure: %s",val.toString());
		val = pyMap.get(TranslationConstants.PY_MODULE);
		if( val==null ) val = "<null>";
		log.infof("ModuleName: %s",val.toString());
		val = pyMap.get(TranslationConstants.PY_METHOD);
		if( val==null ) val = "<null>";
		log.infof("MethodName: %s",val.toString());
	}
	/**
	 * Given a the name of a full Python module, return the package.
	 * @param s the module
	 * @return the package
	 */
	private String derivePackageName(String s) {
		int pos = s.lastIndexOf(".");
		if( pos>0) {
			return s.substring(0, pos);
		}
		else {
			return s;
		}
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
		if( args.length < 2) {
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

		String path = args[0];
		// In case we've been fed a Windows path, convert
		path = path.replace("\\", "/");
		File dir = new File(path);
		trans.setTargetDirectory(dir);

		// The second argument is a path to the database
		String dbpath = args[1];
		// In case we've been fed a Windows path, convert
		dbpath = dbpath.replace("\\", "/");
		trans.processDatabase(dbpath);
		trans.processInput();
		trans.createOutput();
	}

}
