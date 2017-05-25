/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.ils.sfc.migration.visitor.ProjectWalker;
import com.inductiveautomation.ignition.common.Base64;
import com.inductiveautomation.ignition.common.model.ApplicationScope;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
/**
 * Traverse the tree of Ignition-ready XML files that represent
 * SFC charts. Bundle the result in an Ignition project file,
 * ready for import into the global project.
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
			out = new PrintWriter(Files.newBufferedWriter(project,StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.CREATE,StandardOpenOption.WRITE));
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
			log.infof("%s.processInput: walking complete.",TAG);
		}
		catch(IOException ioe) {
			log.infof("%s.processInput: Walk failed (%s)",TAG,ioe.getMessage());
		}
	}
	/**
	 * Write the fixed part of the header.
	 */
	public void writeHeader() {
		if( !ok ) return;
		
		Date now = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<project>");
		out.println("<version>");
		out.println(" <major>7</major>");
		out.println(" <minor>9</minor>");
		out.println(" <rev>2</rev>");
		out.println(" <build>201704131</build>");
		out.println("</version>");
		out.printf("<timestamp>%s</timestamp>\n",df.format(now));
		out.println("<id>-1</id>");
		out.println("<name>[global]</name>");
		out.printf("<title></title>\n");
		out.printf("<description></description>\n");
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
		String name = resourceNameFromPath(filepath);
		log.infof("%s.addChart: %s (%d), parent %s",TAG,name,resid,parentuuidString);
		out.printf("<resource id='%d' name='%s' module='com.inductiveautomation.sfc'",resid,name);
		out.printf(" modver=''  ver='0' dirty='true' editcount='1' type='sfc-chart-ui-model' ");
		out.printf(" parent='%s' oemlocked='false' scope='%s' protected='false'>\n",parentuuidString,ApplicationScope.GATEWAY);
		out.println("<doc></doc>");
		// The file represents a chart. Zip and Base64 encode it. 
		try {
			byte[] chartBytes = Files.readAllBytes(filepath);
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
		    OutputStream zipper=new GZIPOutputStream(baos);
		    zipper.write(chartBytes);
		    zipper.close();
			out.printf("<bytes><![CDATA[%s]]></bytes>",Base64.encodeBytes(baos.toByteArray()));
		}
		catch(IOException ioe) {
			log.errorf("%s.addChart: Error reading %s (%s)",TAG,filepath.toString(),ioe.getMessage());
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
		log.infof("%s.addFolder: %s (%d) %s, parent %s",TAG,name,resid,uuidString,parentuuidString);
		out.printf("<resource id='%d' name='%s' module='com.inductiveautomation.sfc'",resid,name);
		out.printf(" modver=''  ver='0' dirty='true' editcount='1' type='%s' ",ProjectResource.FOLDER_RESOURCE_TYPE);
		out.printf(" parent='%s' oemlocked='false' scope='%s' protected='false'>\n",parentuuidString,ApplicationScope.GATEWAY);
		out.println("<doc></doc>");
		// We construct a project resource so that we can get the serialized value that we need to store.
		// The bytes stored are the UUID as a Base64-encoded byte array.
		UUID uuid = UUID.fromString(uuidString);
		byte[] bytes = asByteArray(uuid);
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
	// Original gives us the bytes in reverse order
	private byte[] asByteArray(UUID uuid) {
    	long msb = uuid.getMostSignificantBits();
    	long lsb = uuid.getLeastSignificantBits();
    	byte[] buffer = new byte[16];

    	for (int i = 0; i < 8; i++) {
    		buffer[15-i] = (byte) (msb >>> 8 * (7 - i));
    	}
    	for (int i = 8; i < 16; i++) {
    		buffer[15-i] = (byte) (lsb >>> 8 * (7 - i));
    	}
    	return buffer;
    }
	
	private String resourceNameFromPath(Path path) {
		String name = path.toString();
		int index = name.lastIndexOf(File.separator);
		if( index>0) name = name.substring(index+1);
		// Strip off extension
		index = name.lastIndexOf(".");
		if( index>0 ) name = name.substring(0,index);
		return name;
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
			m.writeHeader();
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
