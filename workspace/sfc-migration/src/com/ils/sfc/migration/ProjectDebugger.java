/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.sqlite.JDBC;

import com.ils.sfc.migration.block.G2Chart;
import com.ils.sfc.migration.map.ClassNameMapper;
import com.ils.sfc.migration.map.ProcedureMapper;
import com.ils.sfc.migration.map.PropertyMapper;
import com.ils.sfc.migration.map.TagMapper;
import com.inductiveautomation.sfc.api.elements.ChartElement;

/**
 * Given an Ignition project file, scan for CDATA elements. 
 * Perform a Base64 conversion and print the contents. In
 * SFC projects, the charts are stored in XML, converted to
 *
 */
public class ProjectDebugger {
	private final static String TAG = "ProjectDebugger";
	private static final String USAGE = "Usage: project_debug";



	 
	public ProjectDebugger() {
	}
	/**
	 *
	 * @param project string representation of the project file.
	 */
	public void scanProject(String project) {
		
	}
	
	/**
	 * Read standard input. Convert into a string.
	 */
	public String readInput(String path) {
		
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
		catch(Exception ex) {
			System.err.println(String.format("%s: Deserialization exception (%s)",TAG,ex.getMessage()));
		}
		return input.toString();
	}
	
	

	
	
	
	
	/**
	 * Entry point for the application. 
	 * Usage: Migrator <databasepath> 
	 * 
	 * NOTE: For Windows, specify path as: C:/home/work/migrate.db
	 *       For Mac/Linux:    /home/work/migrate.db
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
        
      
		ProjectDebugger m = new ProjectDebugger();
		String path = args[0];
		// In case we've been fed a Windows path, convert
		path = path.replace("\\", "/");
		try {
			String project = m.readInput(path);
			m.scanProject(project);
		}
		catch(Exception ex) {
			System.err.println(String.format("%s.main: UncaughtException (%s)",TAG,ex.getMessage()));
			ex.printStackTrace(System.err);
		}
	}

}
