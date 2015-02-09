/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

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
		out.println(" <build>2015021400</build>");
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
	 * Close the stream writer.
	 */
	public void shutdown() {
		if( out!=null ) {
			out.flush();
			out.close();
		}
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
			m.writeTrailer();
			m.shutdown();
		}
		catch(Exception ex) {
			System.err.println(String.format("%s.main: UncaughtException (%s)",TAG,ex.getMessage()));
			ex.printStackTrace(System.err);
		}
	}

}
