package com.ils.sfc.common.rowconfig;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import system.ils.sfc.common.Constants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ils.sfc.common.step.CollectDataStepProperties;
import com.ils.sfc.common.step.ConfirmControllersStepProperties;
import com.ils.sfc.common.step.ManualDataEntryStepProperties;
import com.ils.sfc.common.step.MonitorDownloadStepProperties;
import com.ils.sfc.common.step.PVMonitorStepProperties;
import com.ils.sfc.common.step.ReviewDataStepProperties;
import com.ils.sfc.common.step.WriteOutputStepProperties;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

public abstract class RowConfig {
	private static final LoggerEx logger = LogUtil.getLogger(RowConfig.class.getName());
	
	// NOTE: all translation keys are uppercase, even if the actual
	// G2 key is mixed case
	protected static Map<String,String> recipeLocationTranslation = new HashMap<String,String>();
	static {
		recipeLocationTranslation.put("local", Constants.LOCAL);
		recipeLocationTranslation.put("previous", Constants.PRIOR);
		recipeLocationTranslation.put("superior", Constants.SUPERIOR );
		recipeLocationTranslation.put("operation", Constants.OPERATION);
		recipeLocationTranslation.put("phase", Constants.PHASE);
		recipeLocationTranslation.put("procedure", Constants.GLOBAL);
		recipeLocationTranslation.put("global", Constants.GLOBAL);
		recipeLocationTranslation.put("named", Constants.NAMED);
	}
	
	protected static String translateRecipeKey(String key) {
		if(key.endsWith(".val")) {
			return key.replace(".val", ".value");
		}
		else {
			return key;
		}
	}
	
	public String toJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
	
	/** Create a config from a serialized object, or create from scratch */
	public static RowConfig fromJSON(String json, Class<?> aClass)  {
		if(json != null && json.length() > 0) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				return (RowConfig) mapper.readValue(json, aClass);
			} catch (Exception e) {
				logger.error("Error deserializing config", e);
			} 
		}
		else {
			try {
				return (RowConfig) aClass.newInstance();
			} catch (Exception e) {
				logger.error("Error creating new config", e);				
			}
		}
		return null;
	}

	public abstract void addRow();
	
	public abstract void addRow(int index);

	public abstract void removeRow(int index);

	public abstract int getRowCount();

	static List<Element> getBlockConfigurationElements(Element blockElement) {
		List<Element> result = new ArrayList<Element>();
		NodeList configNodes = blockElement.getElementsByTagName("blockConfiguration");
		for (int temp = 0; temp < configNodes.getLength(); temp++) {			 
			Node nNode = configNodes.item(temp);	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {	 
				result.add((Element)nNode);
			}
		}
		return result;
	}

	/** Given the step's factory id and a DOM element for the step xml,
	 * convert any blockConfiguration elements and return key/value pairs
	 * that are the converted step properties.
	 * If there are no blockConfiguration elements the returned map will
	 * be empty; If the step type cannot have blockConfiguration elements
	 * null is returned.
	 */
	public static Map<String, String> convert(String factoryId, Element g2block) {
		if(factoryId.equals(CollectDataStepProperties.FACTORY_ID)) {	
			return CollectDataConfig.convert(g2block);
		}
		else if(factoryId.equals(ConfirmControllersStepProperties.FACTORY_ID)) {
			return ConfirmControllersConfig.convert(g2block);
		}
		else if(factoryId.equals(ManualDataEntryStepProperties.FACTORY_ID)) {	
			return ManualDataEntryConfig.convert(g2block);
		}
		else if(factoryId.equals(MonitorDownloadStepProperties.FACTORY_ID)) {			
			return MonitorDownloadsConfig.convert(g2block);
		}
		else if(factoryId.equals(PVMonitorStepProperties.FACTORY_ID)) {			
			return PVMonitorConfig.convert(g2block);
		}
		else if(factoryId.equals(ReviewDataStepProperties.FACTORY_ID)) {			
			return ReviewDataConfig.convert(g2block);
		}
		else if(factoryId.equals(WriteOutputStepProperties.FACTORY_ID)) {			
			return WriteOutputConfig.convert(g2block);
		}
		else {
			return null;
		}
	}


	public static void main(String[] args) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			String filename = "C:/temp/migration/TEST-UNIT-PROCEDURE-17/TEST-UNIT-PROCEDURE-17/S88-OPERATION-XXX-5387/Tst#17DtEntry.MnlDtOprtn.xml";
			Document doc = db.parse(new File(filename));
			Element docElement = doc.getDocumentElement();
			NodeList blockNodes = docElement.getElementsByTagName("block");
			for (int temp = 0; temp < blockNodes.getLength(); temp++) {			 
				Node nNode = blockNodes.item(temp);	 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {	 
					Element blockElement = (Element) nNode;
					Map<String, String> result = convert(ManualDataEntryStepProperties.FACTORY_ID, blockElement);
					for(String key: result.keySet()) {
						System.out.println(key + ": " + result.get(key));
					}
				}
			}
		}
		catch(Exception e ) {
			e.printStackTrace();
		}
	}
}
