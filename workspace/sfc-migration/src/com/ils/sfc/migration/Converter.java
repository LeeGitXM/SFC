/**
 *   (c) 2015-2016  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ils.sfc.common.rowconfig.RowConfig;
import com.ils.sfc.common.step.AllSteps;
import com.ils.sfc.migration.map.ClassNameMapper;
import com.ils.sfc.migration.map.ProcedureMapper;
import com.ils.sfc.migration.map.PropertyMapper;
import com.ils.sfc.migration.map.PropertyValueMapper;
import com.ils.sfc.migration.map.TagMapper;
import com.ils.sfc.migration.translation.ChartStructureTranslator;
import com.ils.sfc.migration.translation.ConnectionHub;
import com.ils.sfc.migration.translation.ConnectionRouter;
import com.ils.sfc.migration.translation.GridPoint;
import com.ils.sfc.migration.translation.StepLayoutManager;
import com.ils.sfc.migration.translation.StepTranslator;
import com.ils.sfc.migration.visitor.CopyWalker;
import com.ils.sfc.migration.visitor.PathWalker;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
/**
 * Copy charts from the G2 chart tree, convert them to Ignition SFC-compliant
 * XML. Store the results in a directory tree ready for subsequent conversion
 * into an Ignition project. The paths in the new tree are scrubbed to remove
 * embedded spaces in the names.
 */
public class Converter {
	private final static String CLSS = "Converter";
	private static final String USAGE = "Usage: converter [-x] <database> <from> <to> <start>";
	private final static String NOLABEL = "nolabel";
	public static boolean haltOnError = false;
	private static final LoggerEx log = LogUtil.getLogger(Converter.class.getPackage().getName());
	
	@SuppressWarnings("unused")
	private final static JDBC driver = new JDBC(); // Force driver to be loaded
	private boolean ok = true;                     // Allows us to short circuit processing
	private final ClassNameMapper classMapper;
	private final ProcedureMapper procedureMapper;
	private final PropertyMapper propertyMapper;
	private final PropertyValueMapper propertyValueMapper;
	private final Map<String,String> pathMap;     // A map "pretty" partial path indexed by file complete input path
	private final StepTranslator stepTranslator;
	private final TagMapper tagMapper;
	private Path pythonRoot = null;
	private String outRoot  = null;    // Root folder for SFC
 
	public Converter() {
		this.classMapper = new ClassNameMapper();
		this.procedureMapper = new ProcedureMapper();
		this.propertyMapper = new PropertyMapper();
		this.propertyValueMapper = new PropertyValueMapper();
		this.pathMap = new HashMap<>();
		this.stepTranslator = new StepTranslator(this);
		this.tagMapper = new TagMapper();
	}
	

	/**
	 * Step 1: Read the database and create maps between various elements.
	 * 
	 * @param path
	 */
	public void processDatabase(Path path) {
		String connectPath = "jdbc:sqlite:"+path.toString();
		log.debugf("%s.processDatabase: database path = %s",CLSS,path.toString());
		// Read database to generate conversion maps
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(connectPath);
			classMapper.createMap(connection);
			procedureMapper.createMap(connection);
			propertyMapper.createMap(connection);
			propertyValueMapper.createMap(connection);
			tagMapper.createMap(connection);
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			log.errorf("%s.processDatabase: Database error (%s)",CLSS,e.getMessage());
			ok = false;
		}
		finally {
			try {
				if(connection != null)
					connection.close();
			} 
			catch(SQLException e) {
				// connection close failed.
				log.errorf("%s.processDatabase: Error closing database (%s)",CLSS,e.getMessage());
			}
		}
	}
	/**
	 * Step 2: Guarantee that the output directory is ready. If it doesn't 
	 *         exist, create it.
	 */
	public void prepareOutput(Path dir) {
		if( !ok ) return;
		// Attempt to create
		log.infof("%s.prepareOutput: Output root is: %s",CLSS,dir.toString());
		if( !Files.exists(dir) ) {
			Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr-x");
			FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
			try {
				log.debugf("%s.prepareOutput: Creating %s",CLSS,dir.toString()); 
				Files.createDirectories(dir,attr);
			}
			catch(IOException ioe) {
				ok = false;
				log.errorf("%s: Failed to create output directory %s (%s)",CLSS,dir.toString(),ioe.getMessage());
			}
		}
		// Check for directory
		else if (!Files.isDirectory(dir)) {
			ok = false;
			log.errorf("%s: Target output exists, but is not a directory (%s)",CLSS,dir.toString());
		}

	}
	/**
	 * Step 3: Traverse the directory designated as input and create a 
	 *         map of relative path versus "ugly" file name. We use this later
	 *         on to resolve paths to encapsulations and to generate corresponding
	 *         file paths for the output.
	 */
	public void createPathMap(Path indir,String start) {
		if( !ok ) return;
		
		outRoot = toCamelCase(start);
		
		// Create a path walker with the root
		PathWalker walker = new PathWalker(indir,this);
		try {
			Path startpath = Paths.get(indir.toString(), start);
			log.infof("%s.createPathMap: Walking %s",CLSS,startpath.toString());
			Files.walkFileTree(startpath, walker);
		}
		catch(IOException ioe) {
			log.warnf("%s.createPathMap: Walk failed (%s)",CLSS,ioe.getMessage());
		}
	}
	/**
	 * Step 4: Traverse the path map and create any directories needed on output.
	 */
	public void createOutputDirectories(Path outdir) {
		if( !ok ) return;
		
		for( String partpath:pathMap.values() ) {
			int pos = partpath.lastIndexOf("/");
			Path dir = Paths.get(outdir.toString(),outRoot,partpath.substring(0, pos));
			log.debugf("%s.createOutputDirectories: Creating %s",CLSS,dir.toString());
			try {
				Files.createDirectories(dir);
			}
			catch(IOException ioe) {
				log.warnf("%s.createOutputDirectories: Failed to create %s",CLSS,ioe.getMessage());
			}
		}
	}
	/**
	 * Step 5: Traverse the path map one more time. If any files map directly
	 *         to directories that have been created, then map them to files 
	 *         of the same name within that subdirectory.
	 */
	public void revisePaths(Path outdir) {
		if( !ok ) return;
		
		for( String key:pathMap.keySet() ) {
			String path = pathMap.get(key);
			Path dir = Paths.get(outdir.toString(),outRoot,path);
			if( Files.isDirectory(dir, LinkOption.NOFOLLOW_LINKS)) {
				Path fname = dir.getFileName();
				log.debugf("%s.revisePaths: Generating %s/%s",CLSS,path,fname.toString());
				String extended = pathMap.get(key) + "/" + fname.toString();
				pathMap.put(key,extended);
			}
		}
	}
	
	/**
	 * Step 6: Traverse the directory designated as input and replicate its
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
			log.infof("%s.processInput: %s",CLSS,startpath.toString());
			Files.walkFileTree(startpath, walker);
		}
		catch(IOException ioe) {
			log.warnf("%s.processInput: Walk failed (%s)",CLSS,ioe.getMessage());
		}
	}
	
	/**
	 * Called by the path-walker as it visits each file.
	 * Convert the file into an XML document, find the S88-Begin block. Extract the 
	 * "pretty" chart path, add to lookup by ugly name. The "ugly" name is a partial path,
	 * less the .xml.
	 * 
	 * @param inpath relative path to the G2 XML file 
	 */
	public void analyzePath(Path fullPath,String inpath) {
		// First create the G2 chart as an XML document.
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;

		try {
			docBuilder = docFactory.newDocumentBuilder();

			Document g2doc = docBuilder.parse(fullPath.toFile());
			Element root = g2doc.getDocumentElement();
			NodeList blocks = root.getElementsByTagName("block");
			int count = blocks.getLength();
			int index = 0;
			Element block = null;
			while(index<count) {
				block = (Element)blocks.item(index);
				String claz = block.getAttribute("class");
				if( claz!=null && claz.equalsIgnoreCase("S88-BEGIN") ) {
					break;
				}
				index++;
			}
			String path = block.getAttribute("block-full-path-label");
			if( path==null ) path = block.getAttribute("name");
			int pos = path.lastIndexOf(".");
			if( pos>0 ) path = path.substring(0, pos); 
			String prettyPath = prettyPath(path);
			log.infof("%s.analyzePath: path map of %s becomes %s",CLSS,inpath,prettyPath);
			pathMap.put(inpath, prettyPath);
			
		}
		catch (ParserConfigurationException pce) {
			log.errorf("%s.analyzePath: Error parsing %s (%s)",CLSS,inpath,pce.getMessage());
		}
		catch (SAXException sax) {
			log.errorf("%s.analyzePath: Error analyzing %s (%s)",CLSS,inpath,sax.getMessage());
		}
		catch (IOException ioe) {
			log.errorf("%s.analyzePath: Failure to read %s (%s)",CLSS,inpath,ioe.getMessage());
		}
		
	}
	
	/**
	 * This is where the real conversion takes place. This method gets called
	 * as we walk the tree. Create an XML document out of the G2 input file. 
	 * Then create a corresponding XML document that represents the Ignition 
	 * equivalent. Finally write it to the output. Each output file represents a SFC chart.
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
			// Read the file to a string so we can scrub it
			byte[] encoded = Files.readAllBytes(infile);
			String xml =  new String(encoded, StandardCharsets.UTF_8);
			xml = xml.replaceAll("&", "&amp;");
			xml = xml.replaceAll("&amp;amp;", "&amp;");  // In case it was laready escaped
			
			InputSource is = new InputSource(new StringReader(xml));
			Document g2doc = docBuilder.parse(is);
			g2doc.getDocumentElement().normalize();

			Document chartdoc = docBuilder.newDocument();  // This is the Ignition version
			initializeChart(chartdoc);
			updateChartForG2(chartdoc,g2doc,xml);
			
			// Write the chart to the output
			xml = docToString(chartdoc);
			log.debugf("%s.convertFile: Creating directory %s ...",CLSS,outfile.getParent().toString());
			log.trace(xml);
			Files.createDirectories(outfile.getParent());
			log.debugf("%s.convertFile: Writing to %s ...",CLSS,outfile.toString());
			Files.write(outfile, xml.getBytes());  // CREATE_NEW,TRUNCATE_EXISTING.WRITE
		}
		catch (ParserConfigurationException pce) {
			log.errorf("%s.convertFile: Error parsing %s (%s)",CLSS,infile.toString(),pce.getMessage());
		}
		catch (SAXException sax) {
			log.errorf("%s.convertFile: Error analyzing %s (%s)",CLSS,infile.toString(),sax.getMessage());
		}
		catch (IOException ioe) {
			log.errorf("%s.convertFile: Failure to write %s (%s)",CLSS,outfile.toString(),ioe.getMessage());
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

	public ClassNameMapper getClassMapper()       { return classMapper; }
	public ProcedureMapper getProcedureMapper()   { return procedureMapper; }
	public TagMapper       getTagMapper()         { return tagMapper; }
	
	public Path getPythonRoot() { return this.pythonRoot; }
	public void setPythonRoot(Path root) { this.pythonRoot = root; }
	
	/**
	 * Update the contents of an Ignition chart document based on a corresponding G2 version.
	 * @param chart the result
	 * @param g2doc the G2 export
	 */
	private void updateChartForG2(Document chart,Document g2doc,String xml) {
		NodeList steps = g2doc.getElementsByTagName("block");
		// A common idiom is a single block in the chart. We need to add begin/end.
		if(steps.getLength()==1) {
			Element block = (Element)steps.item(0);
			updateChartForSingletonStep(chart,block,xml);
		}
		else {
			// There are potential some structural inconsistencies between
			// G2 and Ignition. Attempt to handle these before performing a layout.
			ChartStructureTranslator cts = new ChartStructureTranslator(g2doc,this);
			cts.refactor();

			StepLayoutManager layout = new StepLayoutManager(this,g2doc,chart);
			Map<String,Element> blockMap =  layout.getBlockMap();
			Map<String,GridPoint> gridMap = layout.getGridMap();
			for(String uuid:gridMap.keySet()) {
				GridPoint gp = gridMap.get(uuid);
				if( !gp.isConnected()) continue;    // This block was not connected, ignore
				//g2block element has child "block", plus one or more recipes
				// --- null returns apply to anchors and jumps, ignore
				Element g2block = blockMap.get(uuid);
				ConnectionHub hub = layout.getConnectionMap().get(uuid);
				if( g2block!=null && hub!=null ) {
					Element child = stepTranslator.translate(chart,g2block,gp.x,gp.y,xml);
					// Parallel blocks are created by the layout manager and will be null here.
					if( child!=null ) hub.getChartElement().appendChild(child);
				}
			}

			// The layout does NOT create connections. Create them here.
			// When created they are added as elements to the chart.
			ConnectionRouter router = new ConnectionRouter(layout);
			router.createLinks(chart);
			
			// The layout creates anchors and jumps - and adds them to the chart.
			// Go back to the layout and size any parallel zones to cover children.
			// While we're at it, change the child locations relative to the zone.
			layout.sizeParallelAreas();
			chart.getDocumentElement().setAttribute("canvas",layout.getCanvasSize());
			chart.getDocumentElement().setAttribute("zoom", String.valueOf(layout.getZoom()));
		}
	}
	/**
	 * This G2 chart has only a single block. Add begin, end steps.
	 * @param chart the result
	 * @param g2doc the G2 export
	 */
	private void updateChartForSingletonStep(Document chart,Element g2block,String xml) {
		Element root = chart.getDocumentElement();   // "sfc"
		root.appendChild(createBeginStep(chart,UUID.randomUUID(),1,1));
		root.appendChild(stepTranslator.translate(chart,g2block,1,2, xml));
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
			log.errorf("%s.docToString: Error creating transformer for string conversion (%s)",CLSS,tce.getMessage());
		}
		catch(TransformerException te) {
			te.printStackTrace();
			log.errorf("%s.docToString: Error transforming document (%s)",CLSS,te.getMessage());
		}
		return result;
	}
	
	/**
	 * If the G2 block maps to action-step, then look for a "callback" then read its converted value from the 
	 * file system and insert as a step property.
	 * 
	 * @param step
	 * @param g2block
	 */
	public void insertOnStartFromG2Block(Document chart,Element step,Element g2block) {
		
		String g2attribute = g2block.getAttribute("callback");
		if( g2attribute.length()>0) {
			String script = propertyValueMapper.modifyPropertyValueForIgnition("callback",g2attribute);
			if( script!=null && !script.isEmpty() ) {
				// As returned the script contains the module - strip it off.
				script = pathNameForModule(script);
				log.tracef("%s.insertOnStartFromG2Block: callback (%s) = %s",CLSS,g2attribute,script);

				Path scriptPath = Paths.get(pythonRoot.toString()+"/onstart",script);
				try {
					byte[] bytes = Files.readAllBytes(scriptPath);
					if( bytes!=null && bytes.length>0) {
						Element startelement = chart.createElement("start-script");
						Node textNode = chart.createTextNode(new String(bytes));
						startelement.appendChild(textNode);
						step.appendChild(startelement);
					}
					else {
						log.errorf("%s.insertOnStartFromG2Block: Empty callback script %s",CLSS,scriptPath.toString());
					}
				}
				catch(IOException ioe) {
					log.errorf("%s.insertOnStartFromG2Block: Error reading callback script %s (%s)",CLSS,scriptPath.toString(),g2attribute);
				}
			}
			else {
				log.errorf("%s.insertOnStartFromG2Block: No corresponding translated callback script for %s",CLSS,g2attribute);
			}
		}
		// See if there is an on-start method based on the G2 block class
		// We have seen TransitionCallbackAction steps 
		else {
			g2attribute = g2block.getAttribute("class").trim();
			String script = propertyValueMapper.modifyPropertyValueForIgnition("class",g2attribute);
			if( script!=null && !script.isEmpty()  ) {
				// As returned the script contains the module - strip it off.
				script = pathNameForModule(script);
				log.tracef("%s.insertOnStartFromG2Block: class (%s) = %s",CLSS,g2attribute,script);

				Path scriptPath = Paths.get(pythonRoot.toString()+"/onstart",script);
				try {
					byte[] bytes = Files.readAllBytes(scriptPath);
					if( bytes!=null && bytes.length>0) {
						Element startelement = chart.createElement("start-script");
						Node textNode = chart.createTextNode(new String(bytes));
						startelement.appendChild(textNode);
						step.appendChild(startelement);
					}
					else {
						log.errorf("%s.insertOnStartFromG2Block: Empty %s class script at %s",CLSS,g2attribute,scriptPath.toString());
					}

				}
				catch(IOException ioe) {
					log.errorf("%s.insertOnStartFromG2Block: Error reading class %s script %s",CLSS,g2attribute,scriptPath.toString());
				}
			}
			// TranslationCallbacks are artificially inserted by ChartStructureTranslator.addCallbackToUpstreamStep() and do not have class attributes
			else if( !g2block.getAttribute("name").equalsIgnoreCase("TRANSITION-CALLBACK-ACTION")) {
				log.errorf("%s.insertOnStartFromG2Block: No corresponding callback script for block class %s",CLSS,g2attribute);
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
	 * and add a ".py". Force the first character to be lower case.
	 * @param inpath
	 * @return
	 */
	public String pathNameForModule(String modulePath) {
		String out = modulePath;
		int pos = out.lastIndexOf(".");
		if( pos>0 ) out = out.substring(0, pos);
		return out+".py";
	}
	
	// The path of interest for a chart is an SFC folder hierarchy. Deduce this from
	// the file path name.
	public String partialPathFromInfile(String filename)  {
		StringBuilder pathBuilder = new StringBuilder();
		String filepath =  pathMap.get(filename);
		if( filepath==null ) {
			log.warnf("%s.partialPathFromInfile: No path recorded for file %s",CLSS,filename);
		}
		else {
			pathBuilder.append(outRoot);
			pathBuilder.append(filepath);
			log.tracef("%s.partialPathFromInfile: Path for file %s = %s",CLSS,filename,pathBuilder.toString());
		}
		return pathBuilder.toString();
	}
	
	/**
	 * Result will have a leading "/"
	 * @param path
	 * @return
	 */
	private String prettyPath(String path) {
		path.replaceAll("[.][.]", String.format(".%s.",NOLABEL));
		if( path.endsWith(".")) path = path+NOLABEL;
		String[] segments = path.split("[.]");
		StringBuilder builder = new StringBuilder();
		for(String seg:segments) {
			builder.append("/");
			builder.append(toCamelCase(seg));
		}
		return builder.toString();
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
	    //log.tracef("toCamelCase: result %s",camelCase.toString());
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
		log.debugf("%s.updateStepFromG2Block: step class = %s",CLSS,factoryId);
		
		// These are the properties which we expect from this step
		Property<?>[] properties = AllSteps.getIlsProperties(factoryId);
		if( properties!=null && properties.length>0 ) {
			for( Property<?> property:properties ) {
				String propertyName = property.getName();
				if( propertyName.equals("chart-path")) continue;    // Handled separately
				String g2attribute = propertyMapper.g2Property(factoryId,propertyName);
				if( g2attribute!=null ) {
					String g2value = g2block.getAttribute(g2attribute);
					String value = propertyValueMapper.modifyPropertyValueForIgnition(propertyName,g2value);
					if( value==null ) {
						value = propertyValueMapper.getDefaultValueForIgnition(propertyName);
					}

					// Create the property
					if( value!=null) {
						Element propelement = chart.createElement(propertyName);
						Node textNode = chart.createTextNode(value);
						propelement.appendChild(textNode);
						step.appendChild(propelement);
					}
					else {
						log.warnf("%s.updateStepFromG2Block: %s block has no attribute corresponding to %s",CLSS,factoryId,propertyName);
					}
				}
				else {
					log.warnf("%s.updateStepFromG2Block: property mapper for %s has no G2 attribute for %s",CLSS,factoryId,propertyName);
				}
			}

		}
		else if(factoryId.startsWith("com.ils") && properties==null) {
			log.warnf("%s.updateStepFromG2Block: class %s has no defined properties",CLSS,factoryId);
		}
		
		// Convert the block configurations, if any:
		Map<String,String> blockPropertyMap = RowConfig.convert(factoryId, g2block);
		if(blockPropertyMap != null) {
			for(String key: blockPropertyMap.keySet()) {
				Element propelement = chart.createElement(key);
				Node textNode = chart.createTextNode(blockPropertyMap.get(key));
				propelement.appendChild(textNode);
				step.appendChild(propelement);
			}
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
		chart.setAttribute("version", "7.7.5-beta22 (b2015061613)");
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
			log.tracef("%s.main: indir = %s",CLSS,indir.toString());
			// The output directory is the root holder for the new tree that will be created.
			Path outdir = pathFromString(args[argi++]);
			String rootFile = args[argi];
			log.infof("%s.main: root = %s",CLSS,rootFile);
			log.infof("%s.main: outdir = %s",CLSS,outdir.toString());
			m.prepareOutput(outdir);
			m.createPathMap(indir,rootFile);
			m.createOutputDirectories(outdir);
			m.revisePaths(outdir);
			m.processInput(indir,outdir,rootFile);
		}
		catch(Exception ex) {
			System.err.println(String.format("%s.main: UncaughtException (%s)",CLSS,ex.getMessage()));
			ex.printStackTrace(System.err);
		}
		log.infof("%s.main: COMPLETE",CLSS);
	}

	
}
