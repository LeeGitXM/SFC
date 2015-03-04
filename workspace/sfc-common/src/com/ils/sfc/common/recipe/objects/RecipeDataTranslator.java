package com.ils.sfc.common.recipe.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import com.ils.sfc.common.IlsSfcNames;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

/** Translator to take G2 export XML to an "Associated Data" xml element for an Ignition SFC Step.
 *  If null is returned, errors occurred. Call getErrors() to see them. */ 
public class RecipeDataTranslator {
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
		concreteClassesByG2Name.put("S88-RECIPE-VALUE-DATA", Value.class);
		concreteClassesByG2Name.put("S88-RECIPE-STRUCTURE-DATA", Structure.class);
		concreteClassesByG2Name.put("EM-RECIPE-DATA", EMData.class);

		concreteClassesByG2Name.put("S88-RECIPE-SEQUENCE-DATA", RecipeList.class);
		concreteClassesByG2Name.put("S88-RECIPE-QUANTITY-LIST-DATA", RecipeList.class);
		concreteClassesByG2Name.put("S88-RECIPE-TEXT-LIST-DATA", RecipeList.class);
		concreteClassesByG2Name.put("S88-RECIPE-QUANTITY-ARRAY-DATA", RecipeList.class);
		concreteClassesByG2Name.put("S88-RECIPE-VALUE-ARRAY-DATA", RecipeList.class);
	}
	public static final String G2_CLASS_NAME = "class-name";
	public static final String SYMBOL_PREFIX = "the symbol ";
	public static final String STRUCTURE_PREFIX = "structure (";

	private static Map<String,String> g2ToIgName = new HashMap<String,String>();
	static {
		g2ToIgName.put("advice", IlsSfcNames.ADVICE);
		g2ToIgName.put("array-key", IlsSfcNames.ARRAY_KEY);
		g2ToIgName.put("category", IlsSfcNames.CATEGORY);
		g2ToIgName.put("columns", IlsSfcNames.COLUMNS);
		g2ToIgName.put("column-key", IlsSfcNames.COLUMN_KEY);
		g2ToIgName.put("column-keyed", IlsSfcNames.COLUMN_KEYED);
		g2ToIgName.put("description", IlsSfcNames.DESCRIPTION);
		g2ToIgName.put("download", IlsSfcNames.DOWNLOAD);
		g2ToIgName.put("elements", IlsSfcNames.ELEMENTS
				);
		g2ToIgName.put(G2_CLASS_NAME, IlsSfcNames.CLASS);
		g2ToIgName.put("help", IlsSfcNames.HELP);
		g2ToIgName.put("high-limit", IlsSfcNames.HIGH_LIMIT);
		g2ToIgName.put("high_limit", IlsSfcNames.HIGH_LIMIT);
		g2ToIgName.put("key", IlsSfcNames.KEY);
		g2ToIgName.put("keyed", IlsSfcNames.KEYED);
		g2ToIgName.put("label", IlsSfcNames.LABEL);
		g2ToIgName.put("low-limit", IlsSfcNames.LOW_LIMIT);
		g2ToIgName.put("low_limit", IlsSfcNames.LOW_LIMIT);
		g2ToIgName.put("max-timing", IlsSfcNames.MAX_TIMING);
		g2ToIgName.put("ramp-time", IlsSfcNames.RAMP_TIME);
		g2ToIgName.put("rows", IlsSfcNames.ROWS);
		g2ToIgName.put("row-key", IlsSfcNames.ROW_KEY);
		g2ToIgName.put("row-keyed", IlsSfcNames.ROW_KEYED);
		g2ToIgName.put("tag", IlsSfcNames.TAG_PATH);
		g2ToIgName.put("target", IlsSfcNames.TARGET_VALUE);
		g2ToIgName.put("timing", IlsSfcNames.TIMING);
		g2ToIgName.put("type", IlsSfcNames.TYPE);
		g2ToIgName.put("units", IlsSfcNames.UNITS);
		g2ToIgName.put("update-frequency", IlsSfcNames.UPDATE_FREQUENCY);
		g2ToIgName.put("val", IlsSfcNames.VALUE);
		g2ToIgName.put("val-type", IlsSfcNames.TYPE);
		g2ToIgName.put("write-confirm", IlsSfcNames.WRITE_CONFIRM);

		// for the weird EM-RECIPE-DATA, just translate directly for the moment:
		g2ToIgName.put("pres", "pres");
		g2ToIgName.put("hilim", "hilim");
		g2ToIgName.put("recc", "recc");
		g2ToIgName.put("modattr_val", "modattr_val");
		g2ToIgName.put("lolim", "lolim");
		g2ToIgName.put("dscr", "dscr");
		g2ToIgName.put("stag", "stag");
		g2ToIgName.put("modattr", "modattr");
		g2ToIgName.put("ctag", "ctag");
		g2ToIgName.put("chg_lev", "chg_lev");
	}

	//private List<Data> recipeData = new ArrayList<Data>();
	private final java.util.List<String> errors = new ArrayList<String>();
	private Element blockElement;
	
	public RecipeDataTranslator(Element blockElement) {
		this.blockElement = blockElement;
	}
	
	public static Collection<Class<?>> getConcreteClasses() {
		return concreteClassesByG2Name.values();
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
	public Element createAssociatedDataElement(Document chart) throws JSONException {	
		List<Data> recipeData = DOMToData();
		JSONObject jobj = Data.toAssociatedData(recipeData);
		Element assocdata = chart.createElement("associated-data");
		Node textNode = chart.createTextNode(jobj.toString());
		assocdata.appendChild(textNode);
		return assocdata;
	}
	
	public List<Data> DOMToData() {
		final java.util.List<Data> flatRecipeObjects = new ArrayList<Data>();
		NodeList recipeNodes = blockElement.getElementsByTagName("recipe");
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
					attMap.put(name, value);					
				}
				createObject(flatRecipeObjects, attMap);
			}
		}
		return restoreHierarchy(flatRecipeObjects);
	}
	
	/** Set a property by name. Will throw IllegalArgumentException if the property is not present,
	 *  unless this is a Structure in which case it will be created.  
	 * @throws org.apache.wicket.ajax.json.JSONException */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setProperty(Data data, String name, String strValue) throws JSONException, org.apache.wicket.ajax.json.JSONException {		
		IlsProperty<?> property = data.getProperty(name);
		
		if(property == null) {
			errors.add("no property named " + name + " in " + data.getClass().getSimpleName());
		}
		else if(data instanceof RecipeList && property.equals(IlsProperty.VALUE)) {
			data.setValue(property, parseListValue(strValue));
		}
		else if(data instanceof Matrix && property.equals(IlsProperty.VALUE)) {
			data.setValue(property, parseMatrixValue(strValue));
		}
		else if(data instanceof Structure && property.equals(IlsProperty.JSON_OBJECT)) {
			createJsonStrucure((Structure)data, strValue) ;
		}
		else {
			Object objValue = null;
			if(property != null && property.getType() == String.class) {
				objValue = strValue;
			}
			else if(strValue.length() > 0 ) { 
				objValue = IlsProperty.parseObjectValue(strValue, property.getType());
			}
			if(objValue == null ||property.getType().isAssignableFrom(objValue.getClass())) {
				data.getProperties().setDirect(property, objValue);
			}
			else {
				errors.add(objValue + "(" + objValue.getClass().getSimpleName() + 
					") is wrong type for property " + property.getName() + "(" +
					property.getType().getSimpleName());
			}
		}
	}

	private void createObject(
			final java.util.List<Data> recipeObjects,
			Map<String, String> attMap) {
		// Find the Recipe Data class we want to instantiate
		String g2ClassName = attMap.get(G2_CLASS_NAME);
		String valueType = attMap.get(IlsSfcNames.TYPE);
		//System.out.println();
		//System.out.println(g2ClassName);
		Class<?> aClass = getConcreteClassForG2Class(g2ClassName);
		if(aClass == null) {
			errors.add("No concrete class found for G2 class " + g2ClassName);
		}
		
		// a special case: if it is a simple value with a sequence value,
		// make it a List type:
		if(aClass == Value.class && IlsSfcNames.SEQUENCE.equals(valueType)) {
			aClass = RecipeList.class;
		}
		
		// Create the instance and populate the properties
		try {
			Data data = (Data)aClass.newInstance();
			recipeObjects.add(data);
			for(String g2Key: attMap.keySet()) {
				if(g2Key.equals(G2_CLASS_NAME)) continue;
				String igKey = g2ToIgName.get(g2Key);
				String strValue = attMap.get(g2Key);
				if(IlsSfcNames.UUID.equals(g2Key)) {
					data.setId(strValue);
				}
				else if(IlsSfcNames.PARENT_GROUP.equals(g2Key)) {
					data.setParentId(strValue);
				}
				else if(igKey == null) {
					errors.add("no translation for attribute " + g2Key + " in " + g2ClassName);
				}
				else {
					setProperty(data, igKey, strValue.trim());
					//System.out.println(igKey + ": " + strValue);
				}
			}
			//System.out.println(data.toJSON());
		} catch (Exception e) {
			errors.add("Unexpected error creating " + g2ClassName + " recipe object: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void createJsonStrucure(Structure struct, String strValue) throws JSONException {
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
	
	private java.util.List<Object> parseListValue(String strValue) {
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
		/*
	for(Object val: values) {
			System.out.print("  ");
			System.out.println(val);
		}
		*/
		return values;
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
			objectsById.put(data.getId(), data);
		}
		for(Data data: flatRecipeObjects) {
			if(data.getParentId() == null) {
				hierarchicalData.add(data);
			}
			if(data.getParentId() != null) {
				Data parent = objectsById.get(data.getParentId());
				if(parent == null) {
					errors.add("no parent for id " + data.getParentId());
					continue;
				}
				else if(!(parent instanceof Group)) {
					errors.add("parent object with id " + data.getParentId() + " is not a Group");			
				}
				else {
					((Group)parent).getChildren().add(data);
				}
			}
		}		
		return hierarchicalData;
	}



}
