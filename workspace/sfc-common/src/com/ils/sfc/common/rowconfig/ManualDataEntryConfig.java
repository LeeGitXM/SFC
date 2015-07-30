package com.ils.sfc.common.rowconfig;

import java.io.IOException;
import java.text.ParseException;
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
import com.ils.sfc.common.IlsProperty;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

public class ManualDataEntryConfig extends RowConfig {
	private static LoggerEx logger = LogUtil.getLogger(ManualDataEntryConfig.class.getName());
	private List<Row> rows = new ArrayList<Row>();
	
	public static class Row {
		public String key;
		public String destination;
		public String prompt;
		public String units;
		public Object defaultValue;
		public Double lowLimit;
		public Double highLimit;
	}
	
	public List<Row> getRows() {
		return rows;
	}

	
	public static ManualDataEntryConfig fromJSON(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, ManualDataEntryConfig.class);
	}

	@Override
	public void addRow() {
		rows.add(new Row());
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
		ManualDataEntryConfig config = new ManualDataEntryConfig();
		List<Element> configElements = getBlockConfigurationElements(g2block);
		for(Element configElement: configElements) {
			ManualDataEntryConfig.Row newRow = new ManualDataEntryConfig.Row();
			config.getRows().add(newRow);
			NamedNodeMap attributes = configElement.getAttributes();
			for(int i = 0; i < attributes.getLength(); i++) {
				Node item = attributes.item(i);
				String name = item.getNodeName();
				String strValue = item.getTextContent();	
				if(name.equals("val")) {
					newRow.defaultValue = IlsProperty.parseObjectValue(strValue, null);
				}
				else if(name.equals("units")) {
					newRow.units = strValue;
				}
				else if(name.equals("prompt")) {
					newRow.prompt = strValue;
				}
				else if(name.equals("destination")) {
					newRow.destination = strValue;
				}
				else if(name.equals("id")) {
					if(strValue.endsWith(".val")) {
						strValue = strValue.replace(".val", ".value");
					}
					newRow.key = strValue;
				}
				else if(name.equals("lowLimit")) {
					try {
						newRow.lowLimit = IlsProperty.parseDouble(strValue);
					} catch (ParseException e) {
						logger.error("Error reading lowLimit: " + strValue);
					}
				}
				else if(name.equals("highLimit")) {
					try {
						newRow.highLimit = IlsProperty.parseDouble(strValue);
					} catch (ParseException e) {
						logger.error("Error reading highLimit: " + strValue);
					}
				}
			}
		}
		String json = null;
		try {
			json = config.toJSON();
		} catch(JsonProcessingException e) {
			logger.error("Error generating json for ManualDataEntryConfig", e);
		}
		json = json.replace("\"", "&quot;");
		result.put(Constants.MANUAL_DATA_CONFIG, json);
		return result;
	}
}
