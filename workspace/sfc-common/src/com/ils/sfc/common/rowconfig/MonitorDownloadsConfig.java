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

public class MonitorDownloadsConfig extends RowConfig {
	private static LoggerEx logger = LogUtil.getLogger(MonitorDownloadsConfig.class.getName());
	private List<Row> rows = new ArrayList<Row>();
	
	public static class Row {
		public String key;
		public String labelAttribute;
		public String units;
	}

	public List<Row> getRows() {
		return rows;
	}

	public static MonitorDownloadsConfig fromJSON(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, MonitorDownloadsConfig.class);
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
		MonitorDownloadsConfig config = new MonitorDownloadsConfig();
		List<Element> configElements = getBlockConfigurationElements(g2block);
		for(Element configElement: configElements) {
			MonitorDownloadsConfig.Row newRow = new MonitorDownloadsConfig.Row();
			config.getRows().add(newRow);
			NamedNodeMap attributes = configElement.getAttributes();
			for(int i = 0; i < attributes.getLength(); i++) {
				Node item = attributes.item(i);
				String name = item.getNodeName();
				String strValue = item.getTextContent();	

				if(name.equals("key")) {
					newRow.key = strValue;
				}
				else if(name.equals("units")) {
					newRow.units = strValue;
				}
				else if(name.equals("dcsTagAttribute")) {
					newRow.labelAttribute = strValue;
				}
			}
		}
		String json = null;
		try {
			json = config.toJSON();
		} catch(JsonProcessingException e) {
			logger.error("Error generating json for MonitorDownloadsConfig", e);
		}
		result.put(Constants.MONITOR_DOWNLOADS_CONFIG, json);
		return result;
	}
}
