package com.ils.sfc.common.recipe.objects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ils.sfc.common.IlsProperty;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.util.LoggerEx;

public class StepPropertyTranslator {
	// G2 properties that have no direct translation to Ignition step properties:
	static Set<String> ignoredProperties = new HashSet<String>();
	static Set<String> ignoredClasses = new HashSet<String>();
	static {
		ignoredClasses.add("S88-PARALLEL-TRANSITION");
		ignoredClasses.add("S88-CONDITIONAL-TRANSITION");
		ignoredClasses.add("S88-ENCAPSULATION-TASK");
		
		ignoredProperties.add("block-full-path-label");
		ignoredProperties.add("class");
		ignoredProperties.add("conditional-block-recheck-interval-seconds");
		ignoredProperties.add("configurationDialogId");
		ignoredProperties.add("configurationDialogActions");
		ignoredProperties.add("dcsTagAttribute"); // ?? in MonitorDownloads 
		ignoredProperties.add("full-path");
		ignoredProperties.add("hide-control-panel-when-complete"); // G2 Operation has this
		ignoredProperties.add("post-to-error-queue-procedure"); // G2 Unit Procedure has this
		ignoredProperties.add("post-to-message-queue-procedure"); // G2 Unit Procedure has this
		
		ignoredProperties.add("publish-status-to-control-panel"); // G2 Operation has this
		ignoredProperties.add("set-message-queue-name"); // G2 Operation has this
		ignoredProperties.add("show-control-panel"); // G2 Unit Procedure has this
		ignoredProperties.add("show-message-queue-procedure"); // G2 Unit Procedure has this

		ignoredProperties.add("execution-mode");
		ignoredProperties.add("passed-parameters");
		ignoredProperties.add("return-parameters");

		ignoredProperties.add("uuid");
		ignoredProperties.add("x");
		ignoredProperties.add("y");
	}
	
	/** Get a dictionary of Ignition property names and values corresponding to the
	 *  g2 names/values found in the given XML Node of a G2 block.
	 */
	public static Map<String, String> getStepTranslation(String factoryId, Node stepNode, 
		LoggerEx logger) {
    	// First, collect the property names/values in a Map for ease of use
    	Map<String,String> g2Properties = new HashMap<String,String>();
    	Map<String, String> translation = new HashMap<String, String>();
    	NamedNodeMap attributes = stepNode.getAttributes();
    	for(int j = 0; j < attributes.getLength(); j++ ) {
    		String name =  attributes.item(j).getNodeName();
    		String value =  attributes.item(j).getNodeValue();
    		g2Properties.put(name, value);
    	}
    	
    	// Now do the translation, ignoring 
    	String g2Class = g2Properties.get("class");
    	String stepName = g2Properties.get("name");
    	if(ignoredClasses.contains(g2Class)) {
    		logger.debugf("ignoring %s %s", g2Class, stepName);
    	}
    	else {
    		logger.debugf("translating %s %s", g2Class, stepName);
            for(String propName: g2Properties.keySet()) {
        		String propValueStr = g2Properties.get(propName);
        		if(ignoredProperties.contains(propName)) {
            		logger.debugf("ignoring property %s", propName);
        		}
        		else {
        			BasicProperty<?> mappedProperty = IlsProperty.getTranslationForG2Property(propName);
        			if(mappedProperty != null) {
        				String mappedValueStr = IlsProperty.getTranslationForG2Value(factoryId, 
        					mappedProperty, propValueStr, logger);
        				// TODO: check enum translation
                		logger.debugf("mapped property %s : %s to %s : %s", propName, propValueStr, mappedProperty.getName(), mappedValueStr);
         				translation.put(mappedProperty.getName(), mappedValueStr);
        			}
        			else {
        				logger.errorf("no translation for property %s in class %s", propName, g2Class);
        			}
        		}
        	}
    	}
    	return translation;
	}
	
}
