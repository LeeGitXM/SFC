/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */
package com.ils.sfc.translation;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Translator {
	private final static String TAG = "Translator";
	private static final String USAGE = "Usage: migrator <database>";
	@SuppressWarnings("unused")



	 
	public Translator() {
		//this.root = rc;
	}
	
	public void processTranslateFile(String path) {
		
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
		
		// Now convert into G2 method
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
	
	/**
	 * Entry point for the application. 
	 * Usage: Translator <path> 
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
        /*
        RootClass root = RootClass.APPLICATION;
        String rootClass = System.getProperty("root.class");   // Application, Problem
		if( rootClass!=null) {
			try {
				root = RootClass.valueOf(rootClass);
			}
			catch(IllegalArgumentException iae) {
				System.err.println(String.format("%s: Unknown root.class (%s)",TAG,iae.getMessage()));
			}
		}
       */
		Translator m = new Translator();
		String path = args[0];
		// In case we've been fed a Windows path, convert
		path = path.replace("\\", "/");
		/*
		try {
			m.processDatabase(path);
			m.processInput();
			if(root.equals(RootClass.APPLICATION) ) {
				m.migrateApplication();
			}
			else if(root.equals(RootClass.DIAGRAM)) {
				m.migrateDiagram();
			}
			m.createOutput();
		}
		catch(Exception ex) {
			System.err.println(String.format("%s.main: UncaughtException (%s)",TAG,ex.getMessage()));
			ex.printStackTrace(System.err);
		}
		*/
	}

}
