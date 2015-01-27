/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration;

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
import org.sqlite.JDBC;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ils.sfc.migration.map.ClassNameMapper;
import com.ils.sfc.migration.map.ProcedureMapper;
import com.ils.sfc.migration.map.PropertyMapper;
import com.ils.sfc.migration.map.TagMapper;
import com.inductiveautomation.sfc.api.elements.ChartElement;

public class Migrator {
	private final static String TAG = "Migrator";
	private static final String USAGE = "Usage: migrator <database>";
	@SuppressWarnings("unused")
	private final static JDBC driver = new JDBC(); // Force driver to be loaded
	private final static int MINX = 50;              // Allow whitespace around diagram.
	private final static int MINY = 50;
	private final static double SCALE_FACTOR = 1.25; // Scale G2 to Ignition positions
	private boolean ok = true;                     // Allows us to short circuit processing
	private G2Chart g2chart = null;                // G2 Chart read from XML
	private ChartElement chart = null;             // The result
	private final ClassNameMapper classMapper;
	private final ProcedureMapper procedureMapper;
	private final PropertyMapper propertyMapper;
	private final TagMapper tagMapper;


	 
	public Migrator() {
		classMapper = new ClassNameMapper();
		procedureMapper = new ProcedureMapper();
		propertyMapper = new PropertyMapper();
		tagMapper = new TagMapper();
	}
	
	public void processDatabase(String path) {
		String connectPath = "jdbc:sqlite:"+path;

		// Read database to generate conversion maps
		@SuppressWarnings("resource")
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(connectPath);
			classMapper.createMap(connection);
			procedureMapper.createMap(connection);
			propertyMapper.createMap(connection);
			tagMapper.createMap(connection);
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			System.err.println(TAG+": "+e.getMessage());
			ok = false;
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
	 * Read standard input. Convert into G2 Chart and steps
	 */
	public void processInput() {
		if( !ok ) return;
		
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
			ok = false;
		}
	}
	
	
	/**
	 * Convert from G2 objects into a set of SFC Charts
	 */
	public void migrateCharts() {
		if( !ok ) return;
		
		
	}
	
	
	
	

	
	
	/**
	 * Write the SFC View Objects to std out
	 */
	public void createOutput() {
		if( !ok ) return;
		
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
        
      
		Migrator m = new Migrator();
		String path = args[0];
		// In case we've been fed a Windows path, convert
		path = path.replace("\\", "/");
		try {
			m.processDatabase(path);
			m.processInput();
			m.migrateCharts();
			m.createOutput();
		}
		catch(Exception ex) {
			System.err.println(String.format("%s.main: UncaughtException (%s)",TAG,ex.getMessage()));
			ex.printStackTrace(System.err);
		}
	}

}
