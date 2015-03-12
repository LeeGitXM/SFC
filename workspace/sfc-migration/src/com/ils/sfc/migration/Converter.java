/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.sqlite.JDBC;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ils.sfc.migration.map.ClassNameMapper;
import com.ils.sfc.migration.map.ProcedureMapper;
import com.ils.sfc.migration.map.PropertyMapper;
import com.ils.sfc.migration.map.PropertyValueMapper;
import com.ils.sfc.migration.translation.ConnectionRouter;
import com.ils.sfc.migration.translation.GridPoint;
import com.ils.sfc.migration.translation.StepLayoutManager;
import com.ils.sfc.migration.translation.StepTranslator;
import com.ils.sfc.migration.visitor.CopyWalker;
import com.ils.sfc.migration.visitor.PathWalker;
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
	private final PropertyValueMapper propertyValueMapper;
	private final Map<String,String> pathForFile;     // A map of the complete path indexed by file name
	private final StepTranslator stepTranslator;
	private Path pythonRoot = null;
 
	public Converter() {
		this.classMapper = new ClassNameMapper();
		this.procedureMapper = new ProcedureMapper();
		this.propertyMapper = new PropertyMapper();
		this.propertyValueMapper = new PropertyValueMapper();
		this.pathForFile = new HashMap<>();
		this.stepTranslator = new StepTranslator(this);
	}
	
	public ClassNameMapper getClassMapper() { return classMapper; }
	
	// The path of interest for a chart is an SFC folder hierarchy
	public String getPathForChart(String filename)  { 
		String filepath =  pathForFile.get(filename);
		if( filepath==null ) {
			log.infof("%s.getPathForChart: No path recorded for file %s",TAG,filepath);
			filepath = "";
		}
		return filepath;
	}
	public void setPythonRoot(Path root) { this.pythonRoot = root; }
	/**
	 * Step 1: Read the database and create maps between various elements.
	 * 
	 * @param path
	 */
	public void processDatabase(Path path) {
		String connectPath = "jdbc:sqlite:"+path.toString();
		log.debugf("%s.processDatabase: database path = %s",TAG,path.toString());
		// Read database to generate conversion maps
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(connectPath);
			classMapper.createMap(connection);
			procedureMapper.createMap(connection);
			propertyMapper.createMap(connection);
			propertyValueMapper.createMap(connection);
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
				log.errorf("%s: Failed to create output directory %s (%s)",TAG,dir.toString(),ioe.getMessage());
			}
		}
		// Check for directory
		else if (!Files.isDirectory(dir)) {
			ok = false;
			log.errorf("%s: Target output exists, but is not a directory (%s)",TAG,dir.toString());
		}
		// We don't know the exact name of the output yet, so we make sure that the entire directory is clear
		/*
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
		*/
	}
	/**
	 * Step 3: Traverse the directory designated as input and create a 
	 *         map of relative path versus file name. We use this later
	 *         on to resolve paths to encapsulations 
	 */
	public void createPathMap(Path indir,String start) {
		if( !ok ) return;
		
		// Create a path walker with the root
		PathWalker walker = new PathWalker(indir,this.pathForFile,this);
		try {
			Path startpath = Paths.get(indir.toString(), start);
			Files.walkFileTree(startpath, walker);
		}
		catch(IOException ioe) {
			log.infof("%s.createPathMap: Walk failed (%s)",TAG,ioe.getMessage());
		}
	}
	
	/**
	 * Step 4: Traverse the directory designated as input and replicate its
	 *         structure on the output. Convert any .xml files found and
	 *         place them in the output structure. Each .xml file represents
	 *         a chart. 
	 */
	public void processInput(Path indir,Path outdir,String start) {
		if( !ok ) return;
		
		// Create a copy walker with the root
		CopyWalker walker = new CopyWalker(indir,outdir,this);
		try {
			
			Path startpath = Paths.get(indir.toString(), start);
			log.tracef("%s.processInput: Walking %s",TAG,startpath.toString());
			Files.walkFileTree(startpath, walker);
		}
		catch(IOException ioe) {
			log.warnf("%s.processInput: Walk failed (%s)",TAG,ioe.getMessage());
		}
	}
	public String chartNameFromPath(Path path) {
		String name = path.toString();
		int index = name.lastIndexOf(File.separator);
		if( index>0) name = name.substring(index+1);
		// Strip off extension
		index = name.lastIndexOf(".");
		if( index>0 ) name = name.substring(0,index);
		name = toCamelCase(name);
		return name;
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
			String xml = docToString(chartdoc);
			log.infof("%s.processInput: Writing to %s",TAG,outfile.toString());
			log.debug(xml);
			Files.write(outfile, xml.getBytes(), StandardOpenOption.CREATE_NEW);
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
		NodeList steps = g2doc.getElementsByTagName("block");
		// A common idiom is a single block in the chart. We need to add begin/end.
		if(steps.getLength()==1) {
			Element block = (Element)steps.item(0);
			updateChartForSingletonStep(chart,block);
		}
		else {
			StepLayoutManager layout = new StepLayoutManager(chart,g2doc);
			Element root = chart.getDocumentElement();   // "sfc"
			Map<String,Element> blockMap =  layout.getBlockMap();
			Map<String,GridPoint> gridMap = layout.getGridMap();
			for(String uuid:gridMap.keySet()) {
				GridPoint gp = gridMap.get(uuid);
				//g2block element has child "block", plus one or more recipes
				// --- null returns apply to anchors and jumps, ignore
				Element g2block = blockMap.get(uuid);
				if( g2block!=null ) root.appendChild(stepTranslator.translate(chart,g2block,gp.x,gp.y));
			}
			// The layout creates anchors and jumps
			List<Element> anchorElements = layout.getAnchors();   // Includes jumps
			for(Element e:anchorElements) {
				root.appendChild(e);
			}
			// The layout does NOT create connections. Create them here.
			ConnectionRouter router = new ConnectionRouter(layout.getConnectionMap(),gridMap);
			List<Element> linkElements = router.createLinks(chart);
			for(Element e:linkElements) {
				root.appendChild(e);
			}
			root.setAttribute("zoom", String.valueOf(layout.getZoom()));
		}
	}
	/**
	 * The G2 chart has only a single block. Add begin, end steps.
	 * @param chart the result
	 * @param g2doc the G2 export
	 */
	private void updateChartForSingletonStep(Document chart,Element g2block) {
		Element root = chart.getDocumentElement();   // "sfc"
		root.appendChild(createBeginStep(chart,UUID.randomUUID(),1,1));
		root.appendChild(stepTranslator.translate(chart,g2block,1,2));
		root.appendChild(createEndStep(chart,UUID.randomUUID(),1,3));
	}
	
	/**
	 * Update the contents of an Ignition chart document based on a corresponding G2 version.
	 * @param chart the result
	 * @param g2doc the G2 export
	 */
	private String docToString(Document doc) {
		TransformerFactory tf = TransformerFactory.newInstance();
		String result = "";
		try {
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
		    result = writer.getBuffer().toString().replaceAll("\r", "");
		}
		catch(TransformerConfigurationException tce) {
			log.errorf("%s.docToString: Error creating transformer for string conversion (%s)",TAG,tce.getMessage());
		}
		catch(TransformerException te) {
			log.errorf("%s.docToString: Error transforming document (%s)",TAG,te.getMessage());
		}
		return result;
	}
	
	/**
	 * If the G2 block contains a "callback", then read its converted value from the 
	 * file system and insert as a step property.
	 * 
	 * @param step
	 * @param g2block
	 */
	public void insertOnStartFromG2Block(Document chart,Element step,Element g2block) {
		
		String g2attribute = g2block.getAttribute("callback");
		if( g2attribute.length()>0) {
			String script = propertyValueMapper.modifyPropertyValueForIgnition("callback",g2attribute);
			// As returned the script contains the module - strip it off.
			script = pathNameForModule(script);
			log.infof("%s.insertOnStartFromG2Block: callback = %s->%s",TAG,g2attribute,script);
			if( script!=null  ) {
				Path scriptPath = Paths.get(pythonRoot.toString(),script);
				try {
					byte[] bytes = Files.readAllBytes(scriptPath);
					if( bytes!=null && bytes.length>0) {
						Element startelement = chart.createElement("start-script");
						Node textNode = chart.createTextNode(new String(bytes));
						startelement.appendChild(textNode);
						step.appendChild(startelement);
					}
					else {
						log.errorf("%s.insertOnStartFromG2Block: Empty file %s",TAG,scriptPath.toString());
					}
					
				}
				catch(IOException ioe) {
					log.errorf("%s.insertOnStartFromG2Block: Error reading script %s (%s)",TAG,scriptPath.toString(),ioe.getMessage());
				}
			}
		}
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
	 * Create a file path from a module name. Strip off the entry point
	 * and add a ".py"
	 * @param inpath
	 * @return
	 */
	public String pathNameForModule(String modulePath) {
		String out = modulePath;
		int pos = out.lastIndexOf(".");
		if( pos>0 ) out = out.substring(0, pos);
		return out+".py";
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
	 * Iterate over all properties for the specified class, creating them
	 * in the step. If available, pull corresponding values from the G2 element.
	 * 
	 * @param step
	 * @param g2block
	 */
	public void updateStepFromG2Block(Document chart,Element step,Element g2block) {
		String factoryId = step.getAttribute("factory-id");
		log.debugf("%s.updateStepFromG2Block: step class = %s",TAG,factoryId);
		List<String> properties = propertyMapper.getPropertyList(factoryId);
		if( properties!=null ) {
			for( String property:properties ) {
				String g2attribute = propertyMapper.g2Property(factoryId,property);
				String value = g2block.getAttribute(g2attribute);
				// Alter the value, if so specified
				value = propertyValueMapper.modifyPropertyValueForIgnition(property, value);
				log.debugf("%s.updateStepFromG2Block: %s(%s) = %s (altered)",TAG,property,g2attribute,value);
				Element propelement = chart.createElement(property);
				Node textNode = chart.createTextNode(value);
				propelement.appendChild(textNode);
				step.appendChild(propelement);
			}
		}
		else {
			if( factoryId.startsWith("com.ils") ) log.warnf("updateStepFromG2Block: WARNING: No properties found for class %s",factoryId);
		}
		
	}
	
	// Add a single chart element to the document
	private Element createBeginStep(Document chart,UUID uuid,int x,int y) {
		Element step = chart.createElement("step");
		step.setAttribute("id", uuid.toString());
		step.setAttribute("location", String.format("%d %d", x,y));
		step.setAttribute("name", "__begin");
		step.setAttribute("factory-id", "begin-step");
		return step;
	}
	// Add a single chart element to the document
	private Element createEndStep(Document chart,UUID uuid,int x,int y) {
		Element step = chart.createElement("step");
		step.setAttribute("id", uuid.toString());
		step.setAttribute("location", String.format("%d %d", x,y));
		step.setAttribute("name", "__end");
		step.setAttribute("factory-id", "end-step");
		return step;
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
			m.setPythonRoot(pathFromString(args[argi++]));
			Path indir = pathFromString(args[argi++]);
			log.tracef("%s.main: indir = %s",TAG,indir.toString());
			// The output directory is the root holder for the new tree that will be created.
			Path outdir = pathFromString(args[argi++]);
			String rootFile = args[argi];
			log.tracef("%s.main: outdir = %s",TAG,outdir.toString());
			m.prepareOutput(outdir);
			m.createPathMap(indir,rootFile);
			m.processInput(indir,outdir,rootFile);
		}
		catch(Exception ex) {
			System.err.println(String.format("%s.main: UncaughtException (%s)",TAG,ex.getMessage()));
			ex.printStackTrace(System.err);
		}
		log.infof("%s.main: COMPLETE",TAG);
	}

	
}
