/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.json.XML;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.inductiveautomation.ignition.common.Base64;

/**
 * Given an Ignition project file, scan for CDATA elements. 
 * Perform a Base64 conversion and print the contents. In
 * SFC projects, the charts are stored in XML, converted to
 *
 */
public class ProjectDebugger {
	private final static String TAG = "ProjectDebugger";
	private static final String USAGE = "Usage: project_debug";
	private boolean ok = true;   // Allows us to short circuit processing

 
	public ProjectDebugger() {
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
	 * Walk the XML tree - dump CDATA to standard output.
	 */
	public void processInput(Document doc) {
		if( !ok ) return;
		NodeList resourceList = doc.getElementsByTagName("resource");
		
		System.out.println("\n---------------------------------------------------");
		for (int res = 0; res < resourceList.getLength(); res++) {
			Node resourceNode = resourceList.item(res);
			if (resourceNode.getNodeType() == Node.ELEMENT_NODE) {
				Element resourceElement = (Element)resourceNode;
				String name = resourceElement.getAttribute("name");
				System.out.println("Resource: " + name+" ...");
				
				NodeList bytesList = resourceElement.getElementsByTagName("bytes");
				for (int dataId = 0; dataId < bytesList.getLength(); dataId++) {
					Node dataNode = bytesList.item(dataId);
					NodeList list = dataNode.getChildNodes();
					String data;
					for(int indx = 0; indx < list.getLength(); indx++){
						if(list.item(indx) instanceof CharacterData){
							CharacterData child = (CharacterData) list.item(indx);
							data = child.getData();
							if(data != null && data.trim().length() > 0) {
								System.out.println("\n" + data);
								byte[] bytes = Base64.decode(data);
								try {
									GZIPInputStream xmlInput = new GZIPInputStream(new ByteArrayInputStream(bytes));
									BufferedReader reader = new BufferedReader(new InputStreamReader(xmlInput));
									StringBuilder xml = new StringBuilder();
									String line;
									while( (line=reader.readLine()) !=null ) {
										xml.append(line);
										xml.append("\n");
									}
									System.out.println("\n" + xml.toString());
								}
								catch(IOException ioe) {
									System.out.println("ProjectBuilder.processInput: IOException ("+ioe.getLocalizedMessage()+")");
								}
							}
							
						}
			        }
			    }
			}
		}
	}
	

	/**
	 * Entry point for the application. 
	 * Usage: ProjectDebugger <path> 
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
		Document doc = m.readXML(path);
		m.processInput(doc);
	}
}
