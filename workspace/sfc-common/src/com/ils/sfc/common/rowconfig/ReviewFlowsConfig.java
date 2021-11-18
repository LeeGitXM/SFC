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
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;


public class ReviewFlowsConfig  extends RowConfig {
	private static LoggerEx logger = LogUtil.getLogger(ReviewFlowsConfig.class.getName());
	private List<Row> rows = new ArrayList<Row>();
	public static class Row {
		public String configKey;
		public String flow1Key;
		public String flow2Key;
		public String flow3Key;
		public String destination;
		public String prompt;
		public String units;
		public String advice;
		
		@JsonIgnore
		public boolean isBlank() {
			return IlsSfcCommonUtils.isEmpty(flow1Key); 
		}
	}

	public List<Row> getRows() {
		return rows;
	}

	public static ReviewFlowsConfig fromJSON(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, ReviewFlowsConfig.class);
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
		ReviewFlowsConfig config = new ReviewFlowsConfig();
		List<Element> configElements = getBlockConfigurationElements(g2block);
		for(Element configElement: configElements) {
			ReviewFlowsConfig.Row newRow = new ReviewFlowsConfig.Row();
			config.getRows().add(newRow);
			NamedNodeMap attributes = configElement.getAttributes();
			for(int i = 0; i < attributes.getLength(); i++) {
				Node item = attributes.item(i);
				String name = item.getNodeName();
				String strValue = item.getTextContent();	

				if(name.equals("key")) {
					newRow.configKey = strValue.toLowerCase();
				}
				else if(name.equals("flow1Id")) {
					newRow.flow1Key = strValue;
				}
				else if(name.equals("flow2Id")) {
					newRow.flow2Key = strValue;
				}
				else if(name.equals("flow3Id")) {
					newRow.flow3Key = strValue;
				}
				else if(name.equals("destination")) {
					newRow.destination = recipeLocationTranslation.get(strValue.toLowerCase());
				}
				else if(name.equals("prompt")) {
					newRow.prompt = strValue;
				}
				else if(name.equals("advice")) {
					newRow.advice = strValue.toLowerCase();
				}
				else if(name.equals("units")) {
					newRow.units = strValue;
				}
			}
		}
		String json = null;
		try {
			json = config.toJSON();
		} catch(JsonProcessingException e) {
			logger.error("Error generating json for ReviewDataConfig", e);
		}
		result.put(Constants.REVIEW_FLOWS, json);
		return result;	
	}
}
