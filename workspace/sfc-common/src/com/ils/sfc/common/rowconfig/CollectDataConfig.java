package com.ils.sfc.common.rowconfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import system.ils.sfc.common.Constants;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

public class CollectDataConfig extends RowConfig {
	private static LoggerEx logger = LogUtil.getLogger(CollectDataConfig.class.getName());
	public String errorHandling = Constants.ABORT;
	private List<Row> rows = new ArrayList<Row>();
	private static Map<String,String> translationMap = new HashMap<String,String>();
	static {
		translationMap.put("Current Value".toLowerCase(), Constants.CURRENT);
		translationMap.put("Average".toLowerCase(), Constants.AVERAGE);
		translationMap.put("Maximum".toLowerCase(), Constants.MINIMUM);
		translationMap.put("Minimum".toLowerCase(), Constants.MAXIMUM);
		translationMap.put("Standard Deviation".toLowerCase(), Constants.STANDARD_DEVIATION);
	}
	
	public static class Row {
		public String recipeKey;
		public String location;
		public String tagPath;
		public String valueType;
		public String pastWindow;
		public String defaultValue;		
	}

	public List<Row> getRows() {
		return rows;
	}
	
	public static CollectDataConfig fromJSON(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, CollectDataConfig.class);
	}

	@Override
	public void addRow() {
		rows.add(new Row());
	}
	
	@Override
	public void addRow(int index) {
		rows.add(index, new Row());
	}

	@Override
	public void removeRow(int index) {
		rows.remove(index);
	}

	@JsonIgnore
	@Override
	public int getRowCount() {
		return rows.size();
	}

	public static Map<String,String> convert(Element g2block) {
		Map<String, String> result = new HashMap<String, String>();
		CollectDataConfig config = new CollectDataConfig();
		List<Element> configElements = getBlockConfigurationElements(g2block);
		for(Element configElement: configElements) {
			CollectDataConfig.Row newRow = new CollectDataConfig.Row();
			config.getRows().add(newRow);
			NamedNodeMap attributes = configElement.getAttributes();
			for(int i = 0; i < attributes.getLength(); i++) {
				Node item = attributes.item(i);
				String name = item.getNodeName();
				String strValue = item.getTextContent();	

				if(name.equals("defaultValue")) {
					newRow.defaultValue = strValue;
				}
				else if(name.equals("window")) {
					newRow.pastWindow = strValue;
				}
				else if(name.equals("valueType")) {
					newRow.valueType = translationMap.get(strValue.toLowerCase());
				}
				else if(name.equals("variableName")) {
					newRow.tagPath = strValue;
				}
				else if(name.equals("location")) {
					newRow.location = recipeLocationTranslation.get(strValue.toLowerCase());
				}
				else if(name.equals("key")) {
					newRow.recipeKey = translateRecipeKey(strValue);
				}
			}
		}
		String json = null;
		try {
			json = config.toJSON();
		} catch(JsonProcessingException e) {
			logger.error("Error generating json for CollectDataConfig", e);
		}
		result.put(Constants.MONITOR_DOWNLOADS_CONFIG, json);
		return result;
	}

}
