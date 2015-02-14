package com.ils.sfc.common.recipe.objects;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.ils.sfc.common.IlsSfcCommonUtils;
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
	public static final String G2_CLASS_NAME = "class-name";
	
	private static Map<String,String> g2ToIgName = new HashMap<String,String>();
	static {
		g2ToIgName.put("advice", IlsSfcNames.ADVICE);
		g2ToIgName.put("category", IlsSfcNames.CATEGORY);
		g2ToIgName.put("description", IlsSfcNames.DESCRIPTION);
		g2ToIgName.put(G2_CLASS_NAME, IlsSfcNames.CLASS);
		g2ToIgName.put("help", IlsSfcNames.HELP);
		g2ToIgName.put("high-limit", IlsSfcNames.HIGH_LIMIT);
		g2ToIgName.put("key", IlsSfcNames.KEY);
		g2ToIgName.put("label", IlsSfcNames.LABEL);
		g2ToIgName.put("low-limit", IlsSfcNames.LOW_LIMIT);
		g2ToIgName.put("type", IlsSfcNames.TYPE);
		g2ToIgName.put("units", IlsSfcNames.UNITS);
		g2ToIgName.put("val", IlsSfcNames.VALUE);
	}

	private java.util.List<String> errors = new ArrayList<String>();
	private InputStream xmlIn;
	
	public RecipeDataTranslator(InputStream xmlIn) {
		this.xmlIn = xmlIn;
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

	/** Translate from JSONObject to corresponding xml element for the Associated Data property. */
	@SuppressWarnings("deprecation")
	public String translate() throws JSONException {	
		JSONObject jobj = G2ToJSON();
		return jobj != null ? "<associated-data>" + jobj.toString() + "</associated-data>" : null;
	}
	
	/** 
	 * Create JSONObject representing RecipeData and add as an "associated-data" 
	 * element to a chart step. 
	 */
	public Element createAssociatedDataElement(Document chart) throws JSONException {	
		JSONObject jobj = G2ToJSON();
		Element assocdata = chart.createElement("associated-data");
		Node textNode = chart.createTextNode(jobj.toString());
		assocdata.appendChild(textNode);
		return assocdata;
	}
	
	/** Translate from G2 export to JSONObject */
	public JSONObject G2ToJSON() throws JSONException  {
		final Group group = new Group();
		group.setKey("recipeData");
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			DefaultHandler handler = new DefaultHandler() {
				@Override
				public void startElement(String uri, String localName, String qName, Attributes attributes) {
					if(qName.equals("recipe")) {
						
						// read the XML attributes
						int numAttributes = attributes.getLength();
						Map<String,String> attMap = new HashMap<String,String>();
						for(int i = 0; i < numAttributes; i++) {
							attMap.put(attributes.getQName(i), attributes.getValue(i));
						}
						
						// Find the Recipe Data class we want to instantiate
						String g2ClassName = attMap.get(G2_CLASS_NAME);
						Class<?> aClass = getConcreteClassForG2Class(g2ClassName);
						if(aClass == null) {
							errors.add("No concrete class found for G2 class " + g2ClassName);
						}
						
						// Create the instance and populate the properties
						try {
							Data data = (Data)aClass.newInstance();
							group.getChildren().add(data);
							for(String g2Key: attMap.keySet()) {
								String igKey = g2ToIgName.get(g2Key);
								if(igKey == null) {
									throw new IllegalArgumentException("no translation for attribute " + g2Key);
								}
								String strValue = attMap.get(g2Key);
								Object objValue = IlsSfcCommonUtils.parseObjectValue(strValue);
								data.setProperty(igKey, objValue);
							}
						} catch (Exception e) {
							errors.add("Unexpected error creating recipe object: " + e.getMessage());
						}
					}
				}
			};
			parser.parse(xmlIn, handler);
			xmlIn.close();
		} catch (Exception e) {
			errors.add("Unexpected error in G2 translation" + e.getMessage());
		} 

		return errors.size() == 0 ? group.toJSON() : null;
	}
	
	public static void main(String[] args) {
		try {
			InputStream in = new java.io.FileInputStream("C:/root/repo/svn/EMChemicals/G2Artifacts/Sequential Control/Test-Unit-Procedure-1/TEST-UNIT-PROCEDURE-1.xml");
			RecipeDataTranslator rdTranslator = new RecipeDataTranslator(in);
			String data = rdTranslator.translate();
			if(data != null) {
				System.out.println(data);
			}
			else {
				for(String errMsg: rdTranslator.getErrors()) {
					System.out.println(errMsg);
				}
			}
			in.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
