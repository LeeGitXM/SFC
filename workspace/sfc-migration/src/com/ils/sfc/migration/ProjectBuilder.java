/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
/**
 * Traverse the tree of Ignition-ready XML files that represent
 * SFC charts. Bundle the result in an Ignition project file,
 * ready for impprt into the global project.
 *
 */
public class ProjectBuilder {
	private final static String TAG = "ProjectBuilder";
	private static final String USAGE = "Usage: builder <dir>";
	private boolean ok = true;                     // Allows us to short circuit processing
	 
	public ProjectBuilder() {
	}
	
	public Document readXML(String path) {
		Document doc = null;
		File file = new File(path);
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse(file);
			doc.getDocumentElement().normalize();
		} 
		catch (ParserConfigurationException pce) {
			System.out.println("ProjectBuilder.readXML: ParserConfigurationException ("+pce.getLocalizedMessage()+")");
			ok = false;
		} 
		catch (SAXException saxe) {
			System.out.println("ProjectBuilder.readXML: SAXException ("+saxe.getLocalizedMessage()+")");
			ok = false;
		} 
		catch (IOException ioe) {
			System.out.println("ProjectBuilder.readXML: IOException ("+ioe.getLocalizedMessage()+")");
			ok = false;
		}
		return doc;
	}
	
	/**
	 * Read standard input. Convert into G2 Chart and steps
	 */
	public void processInput(Document doc) {
		if( !ok ) return;
		

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
        
      
		ProjectBuilder m = new ProjectBuilder();
		String path = args[0];
		// In case we've been fed a Windows path, convert
		path = path.replace("\\", "/");
		try {
			Document doc = m.readXML(path);
			m.processInput(doc);
			m.migrateCharts();
			m.createOutput();
		}
		catch(Exception ex) {
			System.err.println(String.format("%s.main: UncaughtException (%s)",TAG,ex.getMessage()));
			ex.printStackTrace(System.err);
		}
	}

}
