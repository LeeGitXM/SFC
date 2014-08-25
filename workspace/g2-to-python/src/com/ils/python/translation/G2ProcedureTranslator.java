/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */
package com.ils.python.translation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.fasterxml.jackson.databind.ObjectMapper;



public class G2ProcedureTranslator {
	private final static String TAG = "Translator";
	private static final String USAGE = "Usage: translate <dir>";
	
	private String packageName = "";                  // No package by default
	private File targetDirectory = new File(".");     // Current directory
	
	
	public G2ProcedureTranslator() {
		//this.root = rc;
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
			byte[] bytes = input.toString().getBytes();
			ObjectMapper mapper = new ObjectMapper();
			/*
			mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
			mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
			mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
			if( root==RootClass.APPLICATION) {
				g2application = mapper.readValue(new String(bytes), G2Application.class);
				if( g2application==null ) {
					System.err.println(TAG+": Failed to deserialize input application");
					ok = false;
				}
			}
			else {
				g2diagram = mapper.readValue(new String(bytes), G2Diagram.class);
				if( g2diagram==null ) {
					System.err.println(TAG+": Failed to deserialize input diagram");
					ok = false;
				}
			}
			*/
		}

		catch(Exception ex) {
			System.err.println(String.format("%s: Deserialization exception (%s)",TAG,ex.getMessage()));;
		}
	}
	
	/**
	 * Write the translated code to std out
	 */
	public void createOutput() {
		//if( !ok ) return;
		
		
			 
		System.out.println("output");
		
	}
	
	// ====================== Setters/Getters ========================
	public String getPackageName() {return packageName;}
	public void setPackageName(String packageName) {this.packageName = packageName;}
	public File getTargetDirectory() {return targetDirectory;}
	public void setTargetDirectory(File targetDirectory) {this.targetDirectory = targetDirectory;}
	
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
