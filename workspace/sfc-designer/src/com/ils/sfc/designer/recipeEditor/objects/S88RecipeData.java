package com.ils.sfc.designer.recipeEditor.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ils.sfc.util.IlsProperty;
import com.ils.sfc.util.IlsSfcCommonUtils;

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
 */
public abstract class S88RecipeData {
	private List<IlsProperty<?>> properties = new ArrayList<IlsProperty<?>>();
	private Map<IlsProperty<?>,Object> valuesByProperty = new HashMap<IlsProperty<?>,Object>();
	public static final String CLASS = "class";	// Not in S88 spec; the name of the Java class
	// KEY attribute not needed (?), is held separately in dictionary
	public static final String LABEL = "label";
	public static final String DESCRIPTION = "description";
	public static final String HELP = "help";
	public static final String ADVICE = "advice";
	
	public static final String VAL = "val";  // added by leaf classes
	
	public S88RecipeData() {
		addProperty(CLASS, String.class, "");
		addProperty(LABEL, String.class, "");
		addProperty(DESCRIPTION, String.class, "");
		addProperty(HELP, String.class, "");
		addProperty(ADVICE, String.class, "");
	}
	
	/** subclasses should extend this, ie extract their properties then call
	 *  super.fromMap()
	 */
	public static S88RecipeData createFromMap(Map<String,String> map) {
		String className = (String) map.get(CLASS);
		S88RecipeData data;
		try {
			data = (S88RecipeData) Class.forName(className).newInstance();
			data.fromMap(map);
			return data;
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/** Compose objects from the Ignition step property. */
	public void fromIgnition(String json) {
		// TODO: deserialize the json to a map
		Map<String,String> map = null;
		fromMap(map);
	}
	
	/** Initialize this object from a map. */
	public void fromMap(Map<String,String> map) {
		for(IlsProperty<?> property: properties) {
			String svalue = map.get(property.getName());
			Object value = IlsSfcCommonUtils.parseProperty(property, svalue);
			valuesByProperty.put(property, value);
		}		
	}

	/** Export objects into the text format for an Ignition step property */
	public String toIgnition() {
		 Map<String,String> map = toMap();
		 // TODO: json serialize map
		 return null;
	}
	
	/** Export objects to a map format */
	public Map<String,String> toMap() {
		Map<String,String> map = new HashMap<String,String>();
		for(IlsProperty<?> property: properties) {
			Object value = valuesByProperty.get(property);
			map.put(property.getName(), value != null ? value.toString() : null);
		}
		return map;
	}
	
	/** Translate from G2 export to Ignition step property. Example of G2 XML element:
	 * <recipe key="bar" label="bar" description="A barby piece of recipe data" help="More useless help" advice="More useless advice" units="DEGC" type="float" category="Simple Constant" val="37.567" high-limit="" low-limit=""  />
	 */
	@SuppressWarnings("deprecation")
	public static String fromG2(String g2Xml) {
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
		return null;
	}

	
	@SuppressWarnings("unchecked")
	protected void addProperty(String name, Class<?> aClass, Object defaultValueOrNull) {
		@SuppressWarnings("rawtypes")
		IlsProperty<?> property = new IlsProperty(name, aClass, defaultValueOrNull);
		properties.add(property);
		valuesByProperty.put(property, defaultValueOrNull);
	}
		
}
