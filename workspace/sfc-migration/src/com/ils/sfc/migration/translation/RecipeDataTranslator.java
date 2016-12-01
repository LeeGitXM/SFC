package com.ils.sfc.migration.translation;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.recipe.objects.Array;
import com.ils.sfc.common.recipe.objects.Data;
import com.ils.sfc.common.recipe.objects.EMData;
import com.ils.sfc.common.recipe.objects.Group;
import com.ils.sfc.common.recipe.objects.Input;
import com.ils.sfc.common.recipe.objects.Matrix;
import com.ils.sfc.common.recipe.objects.Output;
import com.ils.sfc.common.recipe.objects.OutputRamp;
import com.ils.sfc.common.recipe.objects.SQC;
import com.ils.sfc.common.recipe.objects.Structure;
import com.ils.sfc.common.recipe.objects.Timer;
import com.ils.sfc.common.recipe.objects.Value;
import com.ils.sfc.migration.Converter;
import com.ils.sfc.migration.map.PropertyValueMapper;
import com.ils.sfc.migration.map.TagMapper;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

import system.ils.sfc.common.Constants;

/** Translator to take G2 export XML to an "Associated Data" xml element for an Ignition SFC Step.
 *  If null is returned, errors occurred. Call getErrors() to see them. */ 
public class RecipeDataTranslator {
	private static final String CLSS = "RecipeDataTranslator";
	private static LoggerEx log = LogUtil.getLogger(RecipeDataTranslator.class.getName());
	private static Map<String, Class<?>> concreteClassesByG2Name = new HashMap<String, Class<?>>();
	static {
		concreteClassesByG2Name.put("S88-RECIPE-DATA-GROUP", Group.class);
		concreteClassesByG2Name.put("S88-RECIPE-INPUT-DATA", Input.class);
		concreteClassesByG2Name.put("S88-RECIPE-OUTPUT-DATA", Output.class);
		concreteClassesByG2Name.put("S88-RECIPE-OUTPUT-RAMP-DATA", OutputRamp.class);
		concreteClassesByG2Name.put("S88-RECIPE-MATRIX-DATA", Matrix.class);
		concreteClassesByG2Name.put("S88-RECIPE-SQC-DATA", SQC.class);
		concreteClassesByG2Name.put("S88-RECIPE-SQC-TXT", SQC.class);
		concreteClassesByG2Name.put("S88-RECIPE-TIMER", Timer.class);
		concreteClassesByG2Name.put("S88-RECIPE-VALUE-DATA", Value.class);
		concreteClassesByG2Name.put("S88-RECIPE-STRUCTURE-DATA", Structure.class);
		// we need a UDT for EM-RECIPE-DATA (??)
		concreteClassesByG2Name.put("EM-RECIPE-DATA", EMData.class);
		concreteClassesByG2Name.put("S88-RECIPE-SEQUENCE-DATA", Array.class);
		concreteClassesByG2Name.put("S88-RECIPE-QUANTITY-LIST-DATA", Array.class);
		concreteClassesByG2Name.put("S88-RECIPE-TEXT-LIST-DATA", Array.class);
		concreteClassesByG2Name.put("S88-RECIPE-QUANTITY-ARRAY-DATA", Array.class);
		concreteClassesByG2Name.put("S88-RECIPE-VALUE-ARRAY-DATA", Array.class);
	}
	public static final String G2_CLASS_NAME = "class-name";
	public static final String SYMBOL_PREFIX = "the symbol ";
	public static final String STRUCTURE_PREFIX = "structure (";
	
	// Use map for migration
	public static Map<String,String> g2ToIgName = new HashMap<String,String>();
	static {
		g2ToIgName.put("advice", Constants.ADVICE);
		g2ToIgName.put("array-key", Constants.ARRAY_KEY);
		g2ToIgName.put("category", Constants.CATEGORY);
		g2ToIgName.put("columns", Constants.COLUMNS);
		g2ToIgName.put("column-key", Constants.COLUMN_KEY);
		g2ToIgName.put("column-keyed", null);
		g2ToIgName.put("description", Constants.DESCRIPTION);
		g2ToIgName.put("download", Constants.DOWNLOAD);
		g2ToIgName.put("elements", Constants.LENGTH);
		g2ToIgName.put(G2_CLASS_NAME, Constants.CLASS);
		g2ToIgName.put("help", Constants.HELP);
		g2ToIgName.put("high-limit", Constants.HIGH_LIMIT);
		g2ToIgName.put("high_limit", Constants.HIGH_LIMIT);
		g2ToIgName.put("key", Constants.KEY);
		g2ToIgName.put("keyed", null);
		g2ToIgName.put("label", Constants.LABEL);
		g2ToIgName.put("low-limit", Constants.LOW_LIMIT);
		g2ToIgName.put("low_limit", Constants.LOW_LIMIT);
		g2ToIgName.put("max-timing", Constants.MAX_TIMING);
		g2ToIgName.put("ramp-time", Constants.RAMP_TIME);
		g2ToIgName.put("rows", Constants.ROWS);
		g2ToIgName.put("row-key", Constants.ROW_KEY);
		g2ToIgName.put("row-keyed", null);
		g2ToIgName.put("tag", Constants.TAG_PATH);
		g2ToIgName.put("target", Constants.TARGET_VALUE);
		g2ToIgName.put("timing", Constants.TIMING);
		g2ToIgName.put("type", Constants.TYPE);
		g2ToIgName.put("units", Constants.UNITS);
		g2ToIgName.put("update-frequency", Constants.UPDATE_FREQUENCY);
		g2ToIgName.put("val", Constants.VALUE);
		g2ToIgName.put("val-type", Constants.OUTPUT_TYPE);   // Is this legitimate?
		g2ToIgName.put("valueType", Constants.VALUE_TYPE);   // Synthesized
		g2ToIgName.put("write-confirm", Constants.WRITE_CONFIRM);

		// for the weird EM-RECIPE-DATA, just translate directly for the moment:
		g2ToIgName.put("pres", Constants.PRES);
		g2ToIgName.put("hilim", Constants.HILIM);
		g2ToIgName.put("recc", Constants.RECC);
		g2ToIgName.put("modattr_val", Constants.MODATTR_VAL);
		g2ToIgName.put("lolim", Constants.LOLIM);
		g2ToIgName.put("dscr", Constants.DSCR);
		g2ToIgName.put("stag", Constants.STAG);
		g2ToIgName.put("modattr",Constants.MODATTR);
		g2ToIgName.put("ctag", Constants.CTAG);
		g2ToIgName.put("chg_lev", Constants.CHG_LEV);
	}

	//private List<Data> recipeData = new ArrayList<Data>();
	private final java.util.List<String> errors = new ArrayList<String>();
	private final Element blockElement;
	private final Converter delegate;
	
	public RecipeDataTranslator(Converter converter,Element blockElement) {
		this.blockElement = blockElement;
		this.delegate = converter;
	}
	
	public static Collection<Class<?>> getConcreteClasses() {
		return new HashSet<Class<?>>(concreteClassesByG2Name.values());
	}

	public static Class<?> getConcreteClassForG2Class(String g2ClassName) {
		return concreteClassesByG2Name.get(g2ClassName);
	}	

	public java.util.List<String> getErrors() {
		return errors;
	}

	/** 
	 * Create JSONObject representing RecipeData and add as an "associated-data" 
	 * element to a chart step. 
	 */
	public Element createAssociatedDataElement(Document chartDocument, String factoryId) throws JSONException {	
		List<Data> recipeData = DOMToData(factoryId);
		JSONObject assocDataJsonObj = Data.toAssociatedData(recipeData);
		Element assocdata = chartDocument.createElement(Constants.ASSOCIATED_DATA);
		Node textNode = chartDocument.createTextNode(assocDataJsonObj.toString());
		assocdata.appendChild(textNode);
		return assocdata;
	}
	
	public List<Data> DOMToData(String factoryId) {
		final java.util.List<Data> flatRecipeObjects = new ArrayList<Data>();
		NodeList recipeNodes = blockElement.getElementsByTagName("recipe");
		log.infof("RecipeDataTranslator:DOMTOData: block has %d recipe nodes", recipeNodes.getLength());
		for (int temp = 0; temp < recipeNodes.getLength(); temp++) {			 
			Node nNode = recipeNodes.item(temp);	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {	 
				Element recipeElement = (Element) nNode;
				NamedNodeMap attributes = recipeElement.getAttributes();
				Map<String,String> attMap = new HashMap<String,String>();
				
				for(int i = 0; i < attributes.getLength(); i++) {
					Node item = attributes.item(i);
					String name = item.getNodeName();
					String value = item.getTextContent();
					log.infof("RecipeDataTranslator:DOMTOData: %s = %s", name,value);
					attMap.put(name, value);					
				}
				customizeRecipeAttributeMap(attMap);
				createObject(factoryId,flatRecipeObjects, attMap);
			}
		}
		return restoreHierarchy(flatRecipeObjects);
	}
		
	/** Set a property by name. Will throw IllegalArgumentException if the property is not present,
	 *  unless this is a Structure in which case it will be created.  
	 * @throws org.apache.wicket.ajax.json.JSONException */
	public void setProperty(String factoryId,Data data, String name, String strValue) throws JSONException, org.apache.wicket.ajax.json.JSONException {		
		BasicProperty<?> property = data.getProperty(name);
		
		if(data instanceof Array && property.equals(IlsProperty.VALUE) ) {
			Double[] array = parseDoubleArray(strValue);
			//array = new Double[] { 1.2,3.4,5.6};
			data.setValue(property, array);
			property = data.getProperty(Constants.VALUE_TYPE);
			data.setValue(property,Constants.FLOAT);
			property = data.getProperty(Constants.LENGTH);
			data.setValue(property,array.length);
		}
		else if(data instanceof Matrix && property.equals(IlsProperty.VALUE)) {
			data.setValue(property, parseMatrixValue(strValue));
		}
		else if(data instanceof Structure && property.equals(IlsProperty.JSON_OBJECT)) {
			createJsonStructure((Structure)data, strValue) ;
		}
		else {
			Object objValue = null;
			if(property.getType() == String.class) {
				objValue = IlsProperty.getTranslationForG2Value(factoryId, property,strValue, log);
			}
			else if(strValue.length() > 0 ) { 
				objValue = IlsProperty.parseObjectValue(strValue, property.getType());
			}
			if(objValue != null && property.getType().isAssignableFrom(objValue.getClass())) {
				log.infof("RecipeDataTranslator:setProperty: %s = %s (from %s)",property.getName(),objValue.toString(),strValue);
				data.getProperties().setDirect(property, objValue);
			}
			else if(objValue==null) {
				log.infof("RecipeDataTranslator:setProperty: %s = null (from %s)",property.getName(),strValue);
			}
			else {
				errors.add(objValue + "(" + objValue.getClass().getSimpleName() + 
					") is wrong type for property " + property.getName() + "(" +
					property.getType().getSimpleName());
			}
		}
	}

	private void createObject(String factoryId,final java.util.List<Data> recipeObjects,
							  Map<String, String> attMap) {
		// Find the Recipe Data class we want to instantiate
		String g2ClassName = attMap.get(G2_CLASS_NAME);
		String valueType = attMap.get(Constants.TYPE);
		Class<?> aClass = getConcreteClassForG2Class(g2ClassName);
		if(aClass != null) {

			// a special case: if it is a simple value with a sequence value,
			// make it a List type:
			boolean listFromValue = false;
			if(aClass == Value.class && Constants.SEQUENCE.equals(valueType)) {
				aClass = Array.class;
				listFromValue = true;
			}

			// Create the instance and populate the properties
			try {
				Data data = Data.createNewInstance(aClass.getCanonicalName());
				recipeObjects.add(data);
				for(String g2Key: attMap.keySet()) {
					if(g2Key.equals(G2_CLASS_NAME)) continue;
					String igKey = g2ToIgName.get(g2Key);
					String strValue = attMap.get(g2Key);
					if(igKey == null && g2ToIgName.containsKey(g2Key)) {
						// ignore this one
					}
					else if(Constants.UUID.equals(g2Key)) {
						data.setG2Id(strValue);
					}
					else if(Constants.PARENT_GROUP.equals(g2Key)) {
						data.setParentG2Id(strValue);
					}
					else if(igKey == null) {
						errors.add("no translation for attribute " + g2Key + " in " + g2ClassName);
					}
					else {
						if(data.hasProperty(igKey)) {
							setProperty(factoryId,data, igKey, strValue.trim());
						}
						else if(!listFromValue){
							// expected property not found
							// if the list is from a Value, we suppress this as
							// the list will not have several Value properties
							errors.add("no property named " + igKey + " in " + data.getClass().getSimpleName());
						}
						//System.out.println(igKey + ": " + strValue);
					}
				}
				//System.out.println(data.toJSON());
			} 
			catch (Exception e) {
				errors.add("Unexpected error creating " + g2ClassName + " recipe object: " + e.getMessage());
				e.printStackTrace();
			}
		}
		else {
			errors.add("No concrete class found for G2 class " + g2ClassName);
			log.errorf("%s.createObject: ERROR no concrete class found for G2 recipe class %s", CLSS,g2ClassName);
		}
	}
	/**
	 * Fudge the mappings from G2 to what we need for specific classes
	 * @param map the attribute map of the recipe data element
	 */
	private void customizeRecipeAttributeMap(Map<String,String>map) {
		
		String claz = map.get("class-name");
		if( claz!=null && claz.equalsIgnoreCase("S88-RECIPE-VALUE-DATA")) {
			// "type" is really "value-type"
			PropertyValueMapper mapper = delegate.getPropertyValueMapper();
			String type = map.get(Constants.TYPE);         // G2 type - text, quantity
			String ignitionType = mapper.modifyPropertyValueForIgnition(Constants.VALUE_TYPE, type);
			map.put(Constants.VALUE_TYPE, ignitionType);   // Ignition type - string, float
		}
		else if(claz!=null && claz.equalsIgnoreCase("S88-RECIPE-OUTPUT-DATA")) {
			// for tag path, use the SQLite mapping tables
			TagMapper mapper = delegate.getTagMapper();
			String gsiName = map.get(Constants.TAG);         // G2 type - text, quantity
			String tagPath = mapper.getTagPath(gsiName);
			if( tagPath!=null) {
				map.put(Constants.TAG, tagPath);   // Ignition type - string, float
			}
			else {
				errors.add("S88-RECIPE-OUTPUT-DATA: Tag lookup failed for: " + gsiName);
			}
			// Hard-code mode as a String
			gsiName = map.get("val-type");         // Mapped to Ignition as outputType
			if( gsiName!=null && gsiName.equalsIgnoreCase(Constants.MODE)) {
				map.put(Constants.VALUE_TYPE,Constants.STRING);
			}
		}
	}

	private void createJsonStructure(Structure struct, String strValue) throws JSONException {
		String[] keyVals = strValue.substring(STRUCTURE_PREFIX.length(), strValue.length() - 1).split(",");
		JSONObject jobj = new JSONObject();
		for(String keyVal: keyVals) {
			int colonIndex = keyVal.indexOf(':');
			String key = keyVal.substring(0, colonIndex).trim();
			String valString = keyVal.substring(colonIndex + 1, keyVal.length()).trim();
			Object objValue = parseObjectValue(valString);
			jobj.put(key, objValue);
		}
		struct.setValue(IlsProperty.JSON_OBJECT, jobj.toString());
	}

	private boolean isList(String strValue) {
		return strValue.startsWith("{") || strValue.startsWith("sequence(");
	}
	
	private String parseMatrixValue(String strValue) throws org.apache.wicket.ajax.json.JSONException, JSONException {
		JSONArray outerArray = new JSONArray();
		int lbIndex = 0;
		int count = 0;
		while((lbIndex = strValue.indexOf('{', lbIndex)) != -1 ) {
			int rbIndex = strValue.indexOf('}', lbIndex);
			String[] rowVals = strValue.substring(lbIndex + 1, rbIndex).split(",");
			JSONArray innerArray = new JSONArray();
			for(int i = 0; i < rowVals.length; i++) {
				String sval = rowVals[i].trim();
				double dval = Double.parseDouble(sval);
				innerArray.put(i, dval);
			}
			outerArray.put(count++, innerArray);
			lbIndex = rbIndex + 1;
		}
		return outerArray.toString();
	}

	private Double[] parseDoubleArray(String strValue) {
		Double[] values = new Double[0];
		try {
			if(strValue.startsWith("{")) {
				String[] stringVals = strValue.substring(1,strValue.length()-1).split(",");
				values = new Double[stringVals.length];
				int index = 0;
				for(String sval: stringVals) {
					double dblVal = IlsProperty.parseDouble(sval.trim()).doubleValue();
					values[index]=dblVal;
					index = index+1;
				}
			}
		}
		catch(ParseException pe) {
			log.warnf("parseDoubleArray - Parse error converting %s to an array (%s)", strValue,pe.getMessage());
		}
		return values;
	}
	private Object[] parseListValue(String strValue) {
		java.util.List<Object> values = new java.util.ArrayList<Object>();
		if(strValue.startsWith("{")) {
			String[] stringVals = strValue.substring(1, strValue.length() - 1).split(",");
			for(String sval: stringVals) {
				Object objVal = IlsProperty.parseObjectValue(sval.trim(), null);
				values.add(objVal);
			}
		}
		else if(strValue.startsWith("sequence")) {
			int lparenIndex = strValue.indexOf("(");
			int rparenIndex = strValue.indexOf(")");
			String[] stringVals = strValue.substring(lparenIndex+1, rparenIndex).split(",");
			for(String sval: stringVals) {
				values.add(parseObjectValue(sval));
			}
		}
		else {
			Object objVal = IlsProperty.parseObjectValue(strValue.trim(), null);
			values.add(objVal);
		}
		return values.toArray();
	}

	private Object parseObjectValue(String strValue) {
		if(strValue.startsWith(SYMBOL_PREFIX)) {	// G2 Symbol
			return strValue.substring(SYMBOL_PREFIX.length(), strValue.length());
		}
		else if(strValue.startsWith("'")) { // single-quoted string
			// remove the quotes
			return strValue.substring(1, strValue.length() - 1);
		}
		else if(isList(strValue)) {  // some kind of list
			return parseListValue(strValue);
		}
		else {	// a primitive value, hopefully
			return IlsProperty.parseObjectValue(strValue, null);
		}
	}

	/** Restore the hierarchy of Groups with children. */
	private List<Data> restoreHierarchy(java.util.List<Data> flatRecipeObjects) {
		List<Data> hierarchicalData = new ArrayList<Data>();
		Map<String,Data> objectsById = new HashMap<String,Data>();
		for(Data data: flatRecipeObjects) {
			objectsById.put(data.getG2Id(), data);
		}
		for(Data data: flatRecipeObjects) {
			if(data.getParentG2Id() == null) {
				hierarchicalData.add(data);
			}
			if(data.getParentG2Id() != null) {
				Data parent = objectsById.get(data.getParentG2Id());
				if(parent == null) {
					errors.add("no parent for id " + data.getParentG2Id());
					continue;
				}
				else if(!(parent instanceof Group)) {
					errors.add("parent object with id " + data.getParentG2Id() + " is not a Group");			
				}
				else {
					((Group)parent).getChildren().add(data);
				}
			}
		}		
		return hierarchicalData;
	}



}
