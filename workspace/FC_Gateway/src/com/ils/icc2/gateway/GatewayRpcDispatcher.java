/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */
package com.ils.icc2.gateway;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ils.block.annotation.ExecutableBlock;
import com.ils.common.block.BlockProperty;
import com.ils.common.block.PalettePrototype;
import com.ils.common.block.ProcessBlock;
import com.ils.icc2.common.ClassList;
import com.ils.icc2.common.ICC2Properties;
import com.ils.icc2.common.serializable.SerializableResourceDescriptor;
import com.ils.icc2.gateway.engine.BlockExecutionController;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

/**
 *  The RPC Dispatcher is the point of entry for incoming RPC requests.
 *  Its purpose is simply to parse out a request and send it to the
 *  right handler. This class supports the aggregate of RPC interfaces.
 *  
 *  Make use of the BlockRequestHandler so as to provide
 *  a common handler for both the RPC and scripting interfaces.
 */
public class GatewayRpcDispatcher   {
	private static String TAG = "GatewayRpcDispatcher";
	private final LoggerEx log;
	

	/**
	 * Constructor. There is a separate dispatcher for each project.
	 */
	public GatewayRpcDispatcher() {
		log = LogUtil.getLogger(getClass().getPackage().getName());
	}
	
	public String getControllerState() {
		return BlockRequestHandler.getInstance().getExecutionState();
	}
	
	public void startController() {
		BlockRequestHandler.getInstance().startController();
	}
	
	public void stopController() {
		BlockRequestHandler.getInstance().stopController();
	}


	/**
	 * Query the specified block for its properties. If the block does not exist, create it, given the
	 * specified class name. In the case of a new block, its diagram may also need to be created. 
	 * 
	 * @param projectId
	 * @param resourceId
	 * @param blockId
	 * @param className
	 * @return properties for the block
	 */
	public List<String> getBlockProperties(String className,Long projectId,Long resourceId,String blockId) {
		log.infof("%s.getBlockProperties: %s %d:%d %s",TAG,className,projectId.longValue(),resourceId.longValue(),blockId);
		UUID blockUUID = null;
		try {
			blockUUID = UUID.fromString(blockId);
		}
		catch(IllegalArgumentException iae) {
			log.warnf("%s: getBlockProperties: Block UUID string is illegal (%s), creating new",TAG,blockId);
			blockUUID = UUID.nameUUIDFromBytes(blockId.getBytes());
		}
		BlockProperty[] propertyArray = BlockRequestHandler.getInstance().
					getBlockProperties(className,projectId.longValue(),resourceId.longValue(),blockUUID);
		List<String> result = null;
		if( propertyArray!=null ) {
			result = new ArrayList<String>();
			for( BlockProperty prop:propertyArray ) {
				// Python can return some nulls in the array
				if( prop!=null ) {
					result.add(prop.toJson());
				}
			}			
		}
		else {
			log.warnf("%s: getBlockProperties: %s block %d:%d has no properties",TAG,className,projectId.longValue(),resourceId.longValue());
		}
		log.infof("%s: getBlockProperties: returns %s",TAG,result.toString());
		return result;
	}
	

	/**
	 * Deserialize the incoming defaults, add/update from model, re-serialize.
	 * @param proj
	 * @param res
	 * @param connectionId
	 * @param json
	 * @return a JSON string describing the attributes of the connection
	 */
	public String getConnectionAttributes(Long proj, Long res,String connectionId,String json) {
		long projectId = proj.longValue();
		long resourceId = res.longValue();
		log.debugf("%s: getConnectionAttributes: %d:%d:%s =\n%s",TAG,projectId,resourceId,connectionId,json);
		
		ObjectMapper mapper = new ObjectMapper();
		Hashtable<String, Hashtable<String, String>> attributeTable;
		try {
			attributeTable = mapper.readValue(json, new TypeReference<Hashtable<String,Hashtable<String,String>>>(){});
			Hashtable<String,Hashtable<String,String>> results = BlockRequestHandler.getInstance().getConnectionAttributes(projectId,resourceId,connectionId,attributeTable);
			log.debugf("%s: created table = %s",TAG,results);
			json =  mapper.writeValueAsString(results);
			log.debugf("%s: JSON=%s",TAG,json);
		} 
		catch (JsonParseException jpe) {
			log.warnf("%s: getConnectionAttributes: parsing exception (%s)",TAG,jpe.getLocalizedMessage());
		} 
		catch (JsonMappingException jme) {
			log.warnf("%s: getConnectionAttributes: mapping exception(%s)",TAG,jme.getLocalizedMessage());
		} 
		catch (IOException ioe) {
			log.warnf("%s: getConnectionAttributes: io exception(%s)",TAG,ioe.getLocalizedMessage());
		}
		return json;
	}


	/** The blocks implemented in Java are expected to reside in a jar named "icc2-blocks.jar".
	 *  We consider only classes that are in a "com/ils/block" package.
	 *  @return a lost of strings each of which is a JSON description of a prototype descriptor
	 */
	public List<String> getBlockPrototypes() {
		log.infof("%s: getBlockPrototypes ...",TAG);
		List<String> results = new ArrayList<String>();
		ClassList cl = new ClassList();
		List<Class<?>> classes = cl.getAnnotatedClasses(ICC2Properties.BLOCK_JAR_NAME, ExecutableBlock.class,"com/ils/block/");
		for( Class<?> cls:classes) {
			log.infof("   found block class: %s",cls.getName());
			try {
				Object obj = cls.newInstance();
				if( obj instanceof ProcessBlock ) {
					PalettePrototype bp = ((ProcessBlock)obj).getBlockPrototype();
					String json = bp.toJson();
					log.debugf("   json: %s",json);
					results.add(json);
				}
				else {
					log.warnf("%s: Class %s not a ProcessBlock",TAG,cls.getName());
				}
			} 
			catch (InstantiationException ie) {
				log.warnf("%s:getBlockPrototypes: Exception instantiating block (%s)",TAG,ie.getLocalizedMessage());
			} 
			catch (IllegalAccessException iae) {
				log.warnf("%s:getBlockPrototypes: Access exception (%s)",TAG,iae.getMessage());
			}
			catch (Exception ex) {
				log.warnf("%s: getBlockPrototypes: Runtime exception (%s)",TAG,ex.getMessage(),ex);
			}
		}
		log.debugf("%s: getBlockPrototypes: returning %d palette prototypes",TAG,results.size());
		return results;
	}
	public List<String> getDiagramTreePaths(String projectName) {
		List<String> results = BlockExecutionController.getInstance().getDiagramTreePaths(projectName);
		return results;
	}
	
	/** 
	 *  @return a list of serializable resource descriptor
	 */
	public List<SerializableResourceDescriptor> queryControllerResources() {
		log.infof("%s: queryControllerResources ...",TAG);
		return  BlockRequestHandler.getInstance().queryControllerResources();
	}
	
	

}
