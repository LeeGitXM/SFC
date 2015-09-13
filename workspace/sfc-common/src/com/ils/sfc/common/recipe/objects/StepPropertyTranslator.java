package com.ils.sfc.common.recipe.objects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.step.AllSteps;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.util.LoggerEx;

public class StepPropertyTranslator {
	// G2 properties that have no direct translation to Ignition step properties:
	static Set<String> ignoredG2Properties = new HashSet<String>();
	static Set<String> migratedG2Properties = new HashSet<String>();
	static Set<String> ignoredG2Classes = new HashSet<String>();
	static {
		ignoredG2Classes.add("S88-PARALLEL-TRANSITION");
		ignoredG2Classes.add("S88-CONDITIONAL-TRANSITION");
		ignoredG2Classes.add("S88-ENCAPSULATION-TASK");
		
		migratedG2Properties.add("classToCreate");  // class type for SimpleQuery
		migratedG2Properties.add("interfaceName");  // data source for Simple Query
		migratedG2Properties.add("printer");  // SaveData
		migratedG2Properties.add("spreadsheetPopulateMethod");  // ManualDataEntry		
		migratedG2Properties.add("spreadsheetSpecification");
		
		ignoredG2Properties.add("block-full-path-label");
		ignoredG2Properties.add("class");
		ignoredG2Properties.add("conditional-block-recheck-interval-seconds");
		ignoredG2Properties.add("configurationDialogId");
		ignoredG2Properties.add("configurationDialogActions");
		ignoredG2Properties.add("dcsTagAttribute"); // ?? in MonitorDownloads 
		ignoredG2Properties.add("full-path");
		ignoredG2Properties.add("hide-control-panel-when-complete"); // G2 Operation has this
		ignoredG2Properties.add("post-to-error-queue-procedure"); // G2 Unit Procedure has this
		ignoredG2Properties.add("post-to-message-queue-procedure"); // G2 Unit Procedure has this
		
		ignoredG2Properties.add("publish-status-to-control-panel"); // G2 Operation has this
		ignoredG2Properties.add("set-message-queue-name"); // G2 Operation has this
		ignoredG2Properties.add("show-control-panel"); // G2 Unit Procedure has this
		ignoredG2Properties.add("show-message-queue-procedure"); // G2 Unit Procedure has this

		ignoredG2Properties.add("execution-mode");
		ignoredG2Properties.add("passed-parameters");
		ignoredG2Properties.add("return-parameters");

		ignoredG2Properties.add("uuid");
		ignoredG2Properties.add("x");
		ignoredG2Properties.add("y");
	}
	
	/** Get a dictionary of Ignition property names and values corresponding to the
	 *  g2 names/values found in the given XML Node of a G2 block.
	 */
	public static Map<String, String> getStepTranslation(String factoryId, 
		String igStepName, Node stepNode, LoggerEx logger) {
		
    	// First, collect the property names/values in a Map for ease of use
    	Map<String,String> g2Properties = new HashMap<String,String>();
    	NamedNodeMap attributes = stepNode.getAttributes();
    	for(int j = 0; j < attributes.getLength(); j++ ) {
    		String name =  attributes.item(j).getNodeName();
    		String value =  attributes.item(j).getNodeValue();
    		// need to avoid null values for xml representation
     		g2Properties.put(name, value != null ? value : "");
    	}
    	
    	// Now do the translation
    	Map<String, String> translation = new HashMap<String, String>();    	
    	String g2Id = g2Properties.get("uuid");
    	String g2Class = g2Properties.get("class");
    	String g2StepName = g2Properties.get("name");
    	if(ignoredG2Classes.contains(g2Class)) {
    		logger.debugf("ignoring %s %s", g2Class, g2StepName);
    	}
    	else {
    		logger.debugf("translating %s %s %s", g2Class, g2StepName, g2Id);
            for(String g2PropName: g2Properties.keySet()) {
        		String g2PropValue = g2Properties.get(g2PropName);
        		if(ignoredG2Properties.contains(g2PropName)) {
            		logger.debugf("ignoring property %s", g2PropName);
        		}
        		else {
        			BasicProperty<?> mappedProperty = IlsProperty.getTranslationForG2Property(factoryId, g2PropName);
        			if(mappedProperty != null) {
        				String mappedValueStr = IlsProperty.getTranslationForG2Value(factoryId, g2StepName,
        					mappedProperty, g2PropValue, logger);
        				// TODO: check enum translation
                   		logger.debugf("mapped property %s : %s to %s : %s", g2PropName, g2PropValue, mappedProperty.getName(), mappedValueStr);
         				translation.put(mappedProperty.getName(), mappedValueStr);
        			}
        			else {
        				if(migratedG2Properties.contains(g2PropName)) {
        					logger.warnf("property %s : %s in step %s %s may require manual migration", g2PropName, g2PropValue, g2StepName, g2Id);        					
        				}
        				else {
        					logger.errorf("no translation for property %s in %s %s %s", g2PropName, g2Class, g2StepName, g2Id);
        				}
        			}
        		}
        	}
    	}

		// cross-check with all step properties we expect to be translated:
		if(factoryId.startsWith("com.ils")) {
			Property<?>[] ilsSfcProperties = AllSteps.getIlsProperties(factoryId);
			for(Property<?> prop: ilsSfcProperties) {
				String propName = prop.getName();
				// some properties are not translated, or are treated differently:
				if(IlsProperty.isUnMappedProperty(propName)) {
					continue;
				}
				if(!translation.keySet().contains(propName)) {
					logger.errorf("no g2 info for Ignition step property %s in %s %s %s", propName, factoryId, g2StepName, g2Id);
				}
			}
		}

		return translation;
	}
	
}
