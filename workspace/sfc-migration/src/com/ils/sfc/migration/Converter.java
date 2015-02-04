/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.sqlite.JDBC;

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
	private static final String USAGE = "Usage: converter [-x] <database> <from> <to>";
	public static boolean haltOnError = false;
	private static final LoggerEx log = LogUtil.getLogger(Converter.class.getPackage().getName());;
	
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
		else if (dir.toFile().listFiles().length>0) {
			ok = false;
			System.err.println(String.format("%s: Output directory exists, but is not empty (%s)",TAG,dir.toString()));
		}
	}

	/**
	 * Step 3: Traverse the directory designated as input and replicate its
	 *         structure on the output. Convert any .xml files found and
	 *         place them in the output structure. Each .xml file represents
	 *         a chart.
	 */
	public void processInput(Path indir,Path outdir) {
		if( !ok ) return;
		
		CopyWalker walker = new CopyWalker(indir,outdir,this);
		try {
			System.err.println("processInput ... walking\n");
			Files.walkFileTree(indir, walker);
			System.err.println("processInput ... done\n");
		}
		catch(IOException ioe) {
			System.err.println(String.format("%s: Walk failed (%s)",TAG,ioe.getMessage()));
		}
	}
	
	/**
	 * This is where the real conversion takes place
	 * 
	 * @param infile
	 * @param outfile file location in which to write the output. We will munge the 
	 *                file name before writing.
	 */
	public void convertFile(Path infile,Path outfile) {
		log.infof("%s.convertFile ...%s",TAG,infile.getFileName().toString());
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
        if (args.length - argi < 3) {
            usage();
        }
        
		
		try {
			m.processDatabase(pathFromString(args[argi++]));
			Path indir = pathFromString(args[argi++]);
			log.infof("%s.maim: indir = %s",TAG,indir.toString());
			Path outdir = pathFromString(args[argi++]);
			log.infof("%s.maim: outdir = %s",TAG,outdir.toString());
			m.prepareOutput(outdir);
			m.processInput(indir,outdir);
		}
		catch(Exception ex) {
			System.err.println(String.format("%s.main: UncaughtException (%s)",TAG,ex.getMessage()));
			ex.printStackTrace(System.err);
		}
		log.infof("%s.maim: COMPLETE",TAG);
	}

}
