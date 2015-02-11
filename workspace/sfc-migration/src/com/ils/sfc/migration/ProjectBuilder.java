/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.ils.sfc.migration.file.ProjectWalker;
import com.inductiveautomation.ignition.common.Base64;
import com.inductiveautomation.ignition.common.model.ApplicationScope;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
/**
 * Traverse the tree of Ignition-ready XML files that represent
 * SFC charts. Bundle the result in an Ignition project file,
 * ready for import into the global project.
 *
 */
public class ProjectBuilder {
	private final static String TAG = "ProjectBuilder";
	private static final String USAGE = "Usage: builder <dir> <project_file>";
	private static final LoggerEx log = LogUtil.getLogger(Converter.class.getPackage().getName());
	private long resid = 100;
	public static boolean haltOnError = false;
	PrintWriter out = null;
	private boolean ok = true;                     // Allows us to short circuit processing
	
	public ProjectBuilder() {
	}
	
	/**
	 * Open a stream writer to the output.
	 */
	public void prepareOutput(Path project) {
		try {
			out = new PrintWriter(Files.newBufferedWriter(project,StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.CREATE));
		}
		catch(IOException ioe) {
			log.errorf("%s.prepareOutput: Unable to open %s for writing (%s)",TAG,project.toString(),ioe.getMessage());
			ok = false;
		}
	}
	/**
	 * Walk the XML tree - convert XML files to project resources
	 */
	public void processInput(Path path) {
		if( !ok ) return;
		
		// Create a project walker with the root
		ProjectWalker walker = new ProjectWalker(path,this);
		try {
			log.infof("%s.processInput: Walking %s",TAG,path.toString());
			Files.walkFileTree(path, walker);
			log.infof("%s.processInput: walking comlete.",TAG);
		}
		catch(IOException ioe) {
			log.infof("%s.processInput: Walk failed (%s)",TAG,ioe.getMessage());
		}
	}
	/**
	 * Write the fixed part of the header.
	 */
	public void writeHeader(String name) {
		if( !ok ) return;
		
		Date now = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<project>");
		out.println("<version>");
		out.println(" <major>7</major>");
		out.println(" <minor>7</minor>");
		out.println(" <rev>3</rev>");
		out.println(" <build>2015021401</build>");
		out.println("</version>");
		out.printf("<timestamp>%s</timestamp>\n",df.format(now));
		out.println("<id>-1</id>");
		out.println("<name>[global]</name>");
		out.printf("<title>%s</title>\n",name);
		out.printf("<description>Automated migration of %s</description>\n",name);
		out.println("<enabled>true</enabled>");
		out.printf("<lastModified>%d</lastModified>\n",now.getTime());
		out.println("<lastModifiedBy>auto</lastModifiedBy>");
		out.println("<editCount>0</editCount>");
		out.printf("<uuid>%s</uuid>\n",UUID.randomUUID().toString());
		out.println("<resources>");
	}
	
	
	/**
	 * Write the fixed part of the XML trailer.
	 */
	public void writeTrailer() {
		if( !ok ) return;
		
		out.println("</resources>");
		out.println("<deletedResources></deletedResources>");
		out.println("</project>");
	}
	
	/**
	 * Write the XML for a new chart resource.
	 * @param name
	 * @param uuid
	 * @param parentuuid
	 */
	public void addChart(Path filepath,String parentuuidString) {
		resid++;
		// Get the name from the path
		String name = filenameFromPath(filepath);
		log.infof("%s.addChart: %s (%d)",TAG,name,resid);
		out.printf("<resource id='%d' name='%s' module='com.inductiveautomation.sfc'",resid,name);
		out.printf(" modver=''  ver='0' dirty='true' editcount='1' type='sfc-chart-ui-model' ");
		out.printf(" parent='%s' oemlocked='false' scope='%s' protected='false'>\n",parentuuidString,ApplicationScope.DESIGNER);
		out.println("<doc></doc>");
		// We construct a project resource so that we can get the serialized value that we need to store.
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		Document g2doc = null;
		Document ignitiondoc = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			ignitiondoc = docBuilder.newDocument();
			initializeChart(ignitiondoc);
			g2doc = docBuilder.parse(filepath.toFile());
			g2doc.getDocumentElement().normalize();
			
			
			
			
			//out.printf("<bytes><![CDATA[%s]]></bytes>",Base64.encodeBytes(bytes));
		}
		catch(IOException ioe) {
			log.errorf("%s.addChart: Error reading %s (%s)",TAG,filepath.toString(),ioe.getMessage());
		} 
		catch (ParserConfigurationException pce) {
			log.errorf("%s.addChart: Error parsing %s (%s)",TAG,filepath.toString(),pce.getMessage());
		}
		catch (SAXException sax) {
			log.errorf("%s.addChart: Error analyzing %s (%s)",TAG,filepath.toString(),sax.getMessage());
		}
		

		
		out.println("</resource>");
	}
	
	/**
	 * Write the XML for a new folder resource.
	 * @param name
	 * @param uuid
	 * @param parentuuid
	 */
	public void addFolder(String name,String uuidString,String parentuuidString) {
		resid++;
		log.infof("%s.addFolder: %s (%d)",TAG,name,resid);
		out.printf("<resource id='%d' name='%s' module='com.inductiveautomation.sfc'",resid,name);
		out.printf(" modver=''  ver='0' dirty='true' editcount='1' type='%s' ",ProjectResource.FOLDER_RESOURCE_TYPE);
		out.printf(" parent='%s' oemlocked='false' scope='%s' protected='false'>\n",parentuuidString,ApplicationScope.DESIGNER);
		out.println("<doc></doc>");
		// We construct a project resource so that we can get the serialized value that we need to store.
		// The bytes stored are the UUID as a Base64-encoded byte array.
		UUID uuid = UUID.fromString(uuidString);
		byte[] bytes = asByteArray(uuid);
		// log.infof("UUID= %s",Base64.encodeBytes(bytes));
		out.printf("<bytes><![CDATA[%s]]>",Base64.encodeBytes(bytes));
		out.println("</bytes></resource>");
	}
	
	/**
	 * Close the stream writer.
	 */
	public void shutdown() {
		if( out!=null ) {
			out.flush();
			out.close();
		}
	}
	// @see: http://stackoverflow.com/questions/772802/storing-uuid-as-base64-string
	private byte[] asByteArray(UUID uuid) {
    	long msb = uuid.getMostSignificantBits();
    	long lsb = uuid.getLeastSignificantBits();
    	byte[] buffer = new byte[16];

    	for (int i = 0; i < 8; i++) {
    		buffer[i] = (byte) (msb >>> 8 * (7 - i));
    	}
    	for (int i = 8; i < 16; i++) {
    		buffer[i] = (byte) (lsb >>> 8 * (7 - i));
    	}

    	return buffer;
    }
	private String filenameFromPath(Path path) {
		String name = path.toString();
		int index = name.lastIndexOf(File.separator);
		if( index>0) name = name.substring(index+1);
		// Strip off extension
		index = name.lastIndexOf(".");
		if( index>0 ) name = name.substring(0,index);
		return name;
	}
	
	// Add a single chart element to the document
	private void initializeChart(Document doc) {
		Element chart = doc.createElement("sfc");
		
		chart.setAttribute("canvas", "20 20");
		chart.setAttribute("execution-mode", "Callable");
		chart.setAttribute("hot-editable", "false");
		chart.setAttribute("persist-state", "true");
		chart.setAttribute("timestamp", new Date().toString());
		chart.setAttribute("version", "7.7.2 (b2014121709)");
		chart.setAttribute("zoom", "1.0");
		doc.appendChild(chart);
	}
	
	private static Path pathFromString(String spath) {
		// In case we've been fed a Windows path, convert
		spath = spath.replace("\\", "/");
		return Paths.get(spath);
	}
	
	/**
	 * Usage: ProjectBuilder [-x] <indir> <outfile>
	 */
	static void usage() {
		System.out.println(USAGE);
		System.exit(1);
	}
	
	/**
	 * Entry point for the application. 
	 * Usage: ProjectBuilder <indir> <outfil>
	 * 
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
        
        // process command-line args
        int argi = 0;
        while (argi < args.length) {
            String arg = args[argi];
            if (!arg.startsWith("-"))
                break;
            if (arg.length() < 2)
                usage();
            for (int i=1; i<arg.length(); i++) {
                char c = arg.charAt(i);
                switch (c) {
                    case 'x': haltOnError = true; break;
                    default : usage();
                }
            }
            argi++;
        }
      
		ProjectBuilder m = new ProjectBuilder();
		try {
			Path indir = pathFromString(args[argi++]);
			Path outfile = pathFromString(args[argi++]);
			m.prepareOutput(outfile);
			String name = outfile.getFileName().toString();
			// Strip .proj
			int pos = name.indexOf(".");
			if( pos>0 ) name = name.substring(pos);
			m.writeHeader(name);
			m.processInput(indir);
			m.writeTrailer();
			m.shutdown();
		}
		catch(Exception ex) {
			System.err.println(String.format("%s.main: UncaughtException (%s)",TAG,ex.getMessage()));
			ex.printStackTrace(System.err);
		}
	}

}
