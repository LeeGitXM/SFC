/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.sqlite.JDBC;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.ils.sfc.migration.block.G2Chart;
import com.ils.sfc.migration.file.CopyWalker;
import com.ils.sfc.migration.map.ClassNameMapper;
import com.ils.sfc.migration.map.ProcedureMapper;
import com.ils.sfc.migration.map.PropertyMapper;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.elements.ChartElement;
/**
 * Copy charts from the G2 chart tree, convert them to Ignition SFC-compliant
 * XML. Store the results in a directory tree ready for subsequent conversion
 * into an Ignition project. The paths in the new tree are scrubbed to remove
 * embedded spaces in the names.
 */
public class Converter {
	private final static String TAG = "Converter";
	private static final String USAGE = "Usage: converter [-x] <database> <from> <to> <start>";
	public static boolean haltOnError = false;
	private static final LoggerEx log = LogUtil.getLogger(Converter.class.getPackage().getName());
	
	@SuppressWarnings("unused")
	private final static JDBC driver = new JDBC(); // Force driver to be loaded
	private boolean ok = true;                     // Allows us to short circuit processing
	private G2Chart g2chart = null;                // G2 Chart read from XML
	private ChartElement chart = null;             // The result
	private final ClassNameMapper classMapper;
	private final ProcedureMapper procedureMapper;
	private final PropertyMapper propertyMapper;
 
	public Converter() {
		classMapper = new ClassNameMapper();
		procedureMapper = new ProcedureMapper();
		propertyMapper = new PropertyMapper();
	}
	
	/**
	 * Step 1: Read the database and create maps between various elements.
	 * 
	 * @param path
	 */
	public void processDatabase(Path path) {
		String connectPath = "jdbc:sqlite:"+path.toString();
		log.infof("%s.processDatabase: database path = %s",TAG,path.toString());
		// Read database to generate conversion maps
		@SuppressWarnings("resource")
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(connectPath);
			classMapper.createMap(connection);
			procedureMapper.createMap(connection);
			propertyMapper.createMap(connection);
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			log.errorf("%s.processDatabase: Database error (%s)",TAG,e.getMessage());
			ok = false;
		}
		finally {
			try {
				if(connection != null)
					connection.close();
			} 
			catch(SQLException e) {
				// connection close failed.
				log.errorf("%s.processDatabase: Error closing database (%s)",TAG,e.getMessage());
			}
		}
	}
	/**
	 * Step 2: Guarantee that the output directory is ready. If it doesn't 
	 *         exist, create it. If it is not empty, report an error.
	 */
	public void prepareOutput(Path dir) {
		if( !ok ) return;
		// Attempt to create
		if( !Files.exists(dir) ) {
			Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr-x");
			FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
			try {
				Files.createDirectory(dir,attr);
			}
			catch(IOException ioe) {
				ok = false;
				System.err.println(String.format("%s: Failed to create output directory %s (%s)",TAG,dir.toString(),ioe.getMessage()));
			}
		}
		// Check for directory
		else if (!Files.isDirectory(dir)) {
			ok = false;
			System.err.println(String.format("%s: Target output exists, but is not a directory (%s)",TAG,dir.toString()));
		}
		else {
			// Look in the directory for any non-hidden files
			File[] files = dir.toFile().listFiles();
			int index = 0;
			while(index<files.length) {
				if( !files[index].isHidden() ) {
					ok = false;
					System.err.println(String.format("%s: Output directory exists, but is not empty (%s)",TAG,dir.toString()));
				}
				index++;
			}
		}
	}

	/**
	 * Step 3: Traverse the directory designated as input and replicate its
	 *         structure on the output. Convert any .xml files found and
	 *         place them in the output structure. Each .xml file represents
	 *         a chart.
	 */
	public void processInput(Path indir,Path outdir,String start) {
		if( !ok ) return;
		
		// Create the copy walker with the root
		CopyWalker walker = new CopyWalker(indir,outdir,this);
		try {
			
			Path startpath = Paths.get(indir.toString(), start);
			log.infof("%s.processInput: Walking %s",TAG,startpath.toString());
			Files.walkFileTree(startpath, walker);
			log.infof("%s.processInput: walking comlete.",TAG);
		}
		catch(IOException ioe) {
			log.infof("%s.processInput: Walk failed (%s)",TAG,ioe.getMessage());
		}
	}
	
	/**
	 * This is where the real conversion takes place. Create an XML document out
	 * of the input. Create an XML document that represents the output, then
	 * write it.
	 * 
	 * @param infile
	 * @param outfile file location in which to write the output. 
	 */
	public void convertFile(Path infile,Path outfile) {
		log.infof("%s.convertFile ...%s",TAG,infile.getFileName().toString());
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		Document doc = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse(infile.toFile());
			doc.getDocumentElement().normalize();
			
			Document outDoc = convertXML(doc);
			
			// Add .xml to outpat
			outfile = Paths.get(outfile.toString()+".xml");
			Files.write(outfile, outDoc.toString().getBytes(), StandardOpenOption.CREATE_NEW,StandardOpenOption.WRITE);
		}
		catch(IOException ioe) {
			log.errorf("%s.convertFile: Error reading %s (%s)",TAG,infile.toString(),ioe.getMessage());
		} 
		catch (ParserConfigurationException pce) {
			log.errorf("%s.convertFile: Error parsing %s (%s)",TAG,infile.toString(),pce.getMessage());
		}
		catch (SAXException sax) {
			log.errorf("%s.convertFile: Error walking %s (%s)",TAG,infile.toString(),sax.getMessage());
		}
	}
	
	private Document convertXML(Document in) {
		Document out = in;
		return out;
	}
	
	/**
	 * Alter the last segment of a complete path. This
	 * may be a directory name of file name. Re-assemble 
	 * the path.
	 * @param inpath
	 * @return
	 */
	public Path mungeFileName(Path inpath) {
		Path lastSegment = inpath.getFileName();
		Path outpath = Paths.get(inpath.toString(),toCamelCase(lastSegment.toString()));
		return outpath;
	}
	
	/**
	 * Remove dashes and spaces. Convert to camel-case.
	 * @param input
	 * @return munged name
	 */
	public String toCamelCase(String input) {
		// Replace XXX with an underscore
		input = input.replace("-XXX-", "_");
		//Strip off the .xml
		input = input.replace(".xml", "");
	    StringBuilder camelCase = new StringBuilder();
	    boolean nextTitleCase = true;
	    //log.tracef("toCamelCase: %s",input);
	    for (char c : input.toCharArray()) {
	    	// Apparently a / to TitleCase is '_'. Just pass as-is
	    	if (c=='/'  ) {
	    		nextTitleCase = true;
	            ;
	        }
	    	else if (Character.isSpaceChar(c)) {
	            nextTitleCase = true;
	            continue;
	        } 
	        // remove illegal characters
	        else if (c=='-' ||
	        		 c=='#' ||
	        		 c=='.'    ) {
	            nextTitleCase = true;
	            continue;
	        } 
	        else if (nextTitleCase) {
	            c = Character.toUpperCase(c);
	            nextTitleCase = false;
	        }
	        else {
	        	c = Character.toLowerCase(c);
	        }
	        camelCase.append(c);
	    }
	    log.tracef("toCamelCase: result %s",camelCase.toString());
	    return camelCase.toString();
	}	
	/**
	 * Usage: Converter [-f] <databasepath> <indir> <outdir>
	 */
	static void usage() {
		System.out.println(USAGE);
		System.exit(1);
	}
	
	private static Path pathFromString(String spath) {
		// In case we've been fed a Windows path, convert
		spath = spath.replace("\\", "/");
		return Paths.get(spath);
	}
	
	/**
	 * Entry point for the application. 
	 * 
	 * NOTE: For Windows, specify path as: C:/home/work/migrate.db
	 *       For Mac/Linux:    /home/work/migrate.db
	 * We automatically adjust windows path, if specified with backslashes.
	 * 
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {

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
      
		Converter m = new Converter();
        if (args.length - argi < 4) {
            usage();
        }
        
		
		try {
			m.processDatabase(pathFromString(args[argi++]));
			Path indir = pathFromString(args[argi++]);
			log.infof("%s.main: indir = %s",TAG,indir.toString());
			// The output directory is the root holder for the new tree that will be created.
			Path outdir = pathFromString(args[argi++]);
			log.infof("%s.main: outdir = %s",TAG,outdir.toString());
			m.prepareOutput(outdir);
			m.processInput(indir,outdir,args[argi]);
		}
		catch(Exception ex) {
			System.err.println(String.format("%s.main: UncaughtException (%s)",TAG,ex.getMessage()));
			ex.printStackTrace(System.err);
		}
		log.infof("%s.main: COMPLETE",TAG);
	}

}
