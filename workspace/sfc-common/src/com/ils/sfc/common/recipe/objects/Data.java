package com.ils.sfc.common.recipe.objects;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcNames;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.config.PropertyValue;

/**
superiorClass: sequence (the symbol S88-OBJECT)
attributes:sequence (structure (
    ATTRIBUTE-NAME: the symbol KEY,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: ""),
  structure (ATTRIBUTE-NAME: the symbol LABEL,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: ""),
  structure (ATTRIBUTE-NAME: the symbol DESCRIPTION,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: ""),
  structure (ATTRIBUTE-NAME: the symbol HELP,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: ""),
  structure (ATTRIBUTE-NAME: the symbol ADVICE,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: ""),
  structure (ATTRIBUTE-NAME: the symbol LAST-UPDATE-TIMESTAMP,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol FLOAT,
    ATTRIBUTE-INITIAL-VALUE: 0.0))
    
    Use cases:
       1. Translate G2 export xml to Ignition recipe data (JSON-serialized map)
       2a. Translate Ignition recipe data to map "model" for RecipeDataBrowser       
       2b. Translate map "model" for RecipeDataBrowser to Ignition recipe data
       3. For a single object, produce a map that can be added to the map "model" for RecipeDataBrowser       
       4a. For a single object in the map "model", produce a list of PropertyRow objects to feed the PropertyEditor
       4b. From a list of PropertyRow objects from the PropertyEditor, produce a map that can be added to the map "model" for RecipeDataBrowser
 */
public abstract class Data {
	protected BasicPropertySet properties = new BasicPropertySet();
	protected String s88Level;
	
	private static Map<String, Class<?>> concreteClassesByG2Name = new HashMap<String, Class<?>>();
	static {
		concreteClassesByG2Name.put("S88-RECIPE-DATA-GROUP", Group.class);
		concreteClassesByG2Name.put("S88-RECIPE-QUANTITY-ARRAY-DATA", QuantityArray.class);
		concreteClassesByG2Name.put("S88-RECIPE-VALUE-ARRAY-DATA", ValueArray.class);
		concreteClassesByG2Name.put("S88-RECIPE-INPUT-DATA", Input.class);
		concreteClassesByG2Name.put("S88-RECIPE-OUTPUT-DATA", Output.class);
		concreteClassesByG2Name.put("S88-RECIPE-OUTPUT-RAMP-DATA", OutputRamp.class);
		concreteClassesByG2Name.put("S88-RECIPE-MATRIX-DATA", Matrix.class);
		concreteClassesByG2Name.put("S88-RECIPE-QUANTITY-LIST-DATA", QuantityList.class);
		concreteClassesByG2Name.put("S88-RECIPE-SQC-DATA", SQC.class);
		concreteClassesByG2Name.put("S88-RECIPE-VALUE-DATA", Value.class);
		concreteClassesByG2Name.put("S88-RECIPE-TEXT-LIST-DATA", TextList.class);
		concreteClassesByG2Name.put("S88-RECIPE-SEQUENCE-DATA", Sequence.class);
		concreteClassesByG2Name.put("S88-RECIPE-STRUCTURE-DATA", Structure.class);
	}

	public static Collection<Class<?>> getConcreteClasses() {
		return concreteClassesByG2Name.values();
	}

	public Data() {
		addProperty(IlsProperty.CLASS);
		addProperty(IlsProperty.KEY);
		addProperty(IlsProperty.LABEL);
		addProperty(IlsProperty.DESCRIPTION);
		addProperty(IlsProperty.HELP);
		addProperty(IlsProperty.ADVICE);
		
		properties.set(IlsProperty.CLASS, getClass().getSimpleName());
	}

	public String getS88Level() {
		return s88Level;
	}

	public void setS88Level(String s88Level) {
		this.s88Level = s88Level;
	}

	public String getKey() {
		return (String) properties.get(IlsProperty.KEY);
	}
	
	public void setKey(String value) {
		properties.set(IlsProperty.KEY, value);
	}
	
	public boolean isGroup() {
		return this instanceof Group;
	}
	
	public BasicPropertySet getProperties() {
		return properties;
	}

	public void setProperties(BasicPropertySet propertyValues) {
		this.properties = propertyValues;
	}

	/** Add a property with the default value. */
	@SuppressWarnings("unchecked")
	protected void addProperty(IlsProperty property) {
		properties.set(property, property.getDefaultValue());
	}
	
	/** Translate from G2 export to ignition AdditionalData property. Example of G2 XML element:
	 * <recipe key="bar" label="bar" description="A barby piece of recipe data" help="More useless help" advice="More useless advice" units="DEGC" type="float" category="Simple Constant" val="37.567" high-limit="" low-limit=""  />
	 */
	@SuppressWarnings("deprecation")
	public static JSONObject fromG2(String g2Xml) {
		// TODO: implement
		/*
		Map<String,String> g2Attributes = new HashMap<String,String>();
		int eqIndex = -1;
		while((eqIndex = g2Xml.indexOf('"', eqIndex)) != -1) {
			int keyIndex = eqIndex - 1;
			while(!Character.isSpace(g2Xml.charAt(keyIndex))) keyIndex--;
			String key = g2Xml.substring(keyIndex + 1, eqIndex);
			int valueIndex = eqIndex + 2;
			while(g2Xml.charAt(keyIndex) != '"') ++keyIndex;
			String value = g2Xml.substring(eqIndex + 2, valueIndex);
			g2Attributes.put(key, value);
		}
		*/
		return null;
	}
		
	/** Convert a recipe data hierarchy to a JSON Object */
	public JSONObject toJSON() throws JSONException {
		JSONObject jsonObj = new JSONObject();
		for(PropertyValue<?> pvalue: properties) {
			if(pvalue.getValue() != null) {
				jsonObj.put(pvalue.getProperty().getName(), pvalue.getValue());
			}
		}
		if(s88Level != null) {
			jsonObj.put(IlsSfcNames.S88_LEVEL, s88Level);
		}
		return jsonObj;
	}

	/** Create a recipe data hierarchy from a JSON Object that was
	 *  originally created from a recipe hierarchy (i.e. not
	 *  just some random JSONbject. */
	public static Data fromJson(JSONObject jsonObj) throws Exception {
		String simpleClassName = jsonObj.getString(IlsSfcNames.CLASS);
		String packageName = Data.class.getPackage().getName();
		String fullClassName = packageName + "." + simpleClassName;
		Data data = (Data)Class.forName(fullClassName).newInstance();
		data.setFromJson(jsonObj);
		return data;
	}

	/** The recursive part of fromJSON() */
	protected void setFromJson(JSONObject jsonObj) throws Exception {
		Map<Property<?>,java.lang.Object> rawValueMap = properties.getRawValueMap();
		for(Property<?> prop: rawValueMap.keySet()) {
			if(jsonObj.has(prop.getName())) {
				rawValueMap.put(prop, jsonObj.get(prop.getName()));
			}
		}
		if(jsonObj.has(IlsSfcNames.S88_LEVEL)) {
			s88Level = (String)jsonObj.get(IlsSfcNames.S88_LEVEL);
		}	
	}
	
	protected void printSpace(int count) {
		for(int i = 0; i < count; i++) {
			System.out.print("   ");
		}
	}
	
	protected void print(int level) {
		for(PropertyValue<?> pvalue: properties) {
			printSpace(level);
			System.out.println(pvalue.getProperty().getName() + ": " + pvalue.getValue());
		}
	}

	public static void main(String[] args) {
		try {
			Group group = new Group();
			Input input = new Input();
			group.getChildren().add(input);
			System.out.println("===================IN==================");
			group.print(0);
			JSONObject outObj = group.toJSON();
			String jsonString = outObj.toString();
			JSONObject inObj = new JSONObject(jsonString);
			Data data = Data.fromJson(inObj);
			System.out.println("===================OUT==================");
			data.print(0);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
