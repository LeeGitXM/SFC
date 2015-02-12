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
import java.util.Date;
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
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.ils.sfc.migration.map.ClassNameMapper;
import com.ils.sfc.migration.map.ProcedureMapper;
import com.ils.sfc.migration.map.PropertyMapper;
import com.ils.sfc.migration.visitor.CopyWalker;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
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
	private final ClassNameMapper classMapper;
	private final ProcedureMapper procedureMapper;
	private final PropertyMapper propertyMapper;
	
 
	public Converter() {
		this.classMapper = new ClassNameMapper();
		this.procedureMapper = new ProcedureMapper();
		this.propertyMapper = new PropertyMapper();
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
	 * This is where the real conversion takes place. This method gets called
	 * as we walk the tree. Create an XML document out of the G2 input file. 
	 * Create an XML document that represents the Ignition equivalent, then 
	 * write it to the output. Each output file represents a SFC chart.
	 * 
	 * @param infile
	 * @param outfile file location in which to write the output. 
	 */
	public void convertFile(Path infile,Path outfile) {
		// Get the name from the path
		String name = chartNameFromPath(outfile);
		// First create the G2 chart as an XML document.
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;

		try {
			docBuilder = docFactory.newDocumentBuilder();

			Document g2doc = docBuilder.parse(infile.toFile());
			g2doc.getDocumentElement().normalize();

			Document chartdoc = docBuilder.newDocument();  // This is the Ignition version
			initializeChart(chartdoc);
			updateChartForG2(chartdoc,g2doc);
			
			// Write the chart to the output
			Files.write(outfile, chartdoc.toString().getBytes(), StandardOpenOption.CREATE_NEW);
		}
		catch (ParserConfigurationException pce) {
			log.errorf("%s.addChart: Error parsing %s (%s)",TAG,infile.toString(),pce.getMessage());
		}
		catch (SAXException sax) {
			log.errorf("%s.addChart: Error analyzing %s (%s)",TAG,infile.toString(),sax.getMessage());
		}
		catch (IOException ioe) {
			log.errorf("%s.addChart: Failure to read %s or write %s (%s)",TAG,infile.toString(),outfile.toString(),ioe.getMessage());
		}
		
	}
	
	/**
	 * Update the contents of an Ignition chart document based on a corresponding G2 version.
	 * @param chart the result
	 * @param g2doc the G2 export
	 */
	private void updateChartForG2(Document chart,Document g2doc) {

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
	
	private String chartNameFromPath(Path path) {
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
