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
import com.inductiveautomation.ignition.gateway.localdb.DBInterface.Transaction;

public class PVMonitorConfig extends RowConfig {
	private static LoggerEx logger = LogUtil.getLogger(PVMonitorConfig.class.getName());
	private List<Row> rows = new ArrayList<Row>();
	private static Map<String,String> translationMap = new HashMap<String,String>();
	static {
		translationMap.put(Constants.MONITOR.toLowerCase(), Constants.MONITOR);
		translationMap.put(Constants.WATCH.toLowerCase(), Constants.WATCH);
		translationMap.put(Constants.ABS.toLowerCase(), Constants.ABS);
		translationMap.put(Constants.PCT.toLowerCase(), Constants.PCT);
		translationMap.put(Constants.IMMEDIATE.toLowerCase(), Constants.IMMEDIATE);
		translationMap.put(Constants.WAIT.toLowerCase(), Constants.WAIT);
		translationMap.put(Constants.HIGH_LOW.toLowerCase(), Constants.HIGH_LOW);
		translationMap.put(Constants.HIGH.toLowerCase(), Constants.HIGH);
		translationMap.put(Constants.LOW.toLowerCase(), Constants.LOW);
		translationMap.put("setpoint", Constants.SETPOINT);
		translationMap.put("value", Constants.VALUE);
		translationMap.put("named item", Constants.TAG);
		translationMap.put("recipe data", Constants.RECIPE);
	}
	public static class Row {
		public boolean enabled = true;
		public String pvKey;
		public String targetType;
		public Object targetNameIdOrValue;
		public String strategy;
		public String limits;
		public String download;
		public double persistence;
		public double consistency;
		public double deadTime;
		public double tolerance;
		public String toleranceType;
		public String status;

		// transient values used in monitoring (maybe this should be a separate object)
		@JsonIgnore
		public double targetValue;  
		@JsonIgnore
		public double lowLimit;  
		@JsonIgnore
		public double highLimit;  
		@JsonIgnore
		public double inToleranceTime;  //  time at which the value last entered tolerance
		@JsonIgnore
		public double outToleranceTime;  //  time at which the value last exited tolerance
		@JsonIgnore
		public boolean inTolerance;	// was the last pv in tolerance?
		@JsonIgnore
		public double downloadTime;  //  time at which the setpoint was downloaded
		@JsonIgnore
		public boolean isDownloaded;
		@JsonIgnore
		public boolean persistenceOK;
		@JsonIgnore
		public Object ioRD;   // recipe data for the IO object
		@JsonIgnore
		public Object io;   // the controller
		@JsonIgnore
		public Object isOutput;   // is the io recipe data an Output object?
	}

	public List<Row> getRows() {
		return rows;
	}
	
	public static PVMonitorConfig fromJSON(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, PVMonitorConfig.class);
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
		PVMonitorConfig config = new PVMonitorConfig();
		List<Element> configElements = getBlockConfigurationElements(g2block);
		for(Element configElement: configElements) {
			PVMonitorConfig.Row newRow = new PVMonitorConfig.Row();
			config.getRows().add(newRow);
			NamedNodeMap attributes = configElement.getAttributes();
			for(int i = 0; i < attributes.getLength(); i++) {
				Node item = attributes.item(i);
				String name = item.getNodeName();
				String strValue = item.getTextContent();	

				if(name.equals("deadTime")) {
					try {	
						newRow.deadTime = IlsProperty.parseDouble(strValue);
					} catch (ParseException e) {
						logger.error("Error parsing PVMonitorConfig.deadTime double value from " + strValue);
					}
				}
				else if(name.equals("strategy")) {
					newRow.strategy = translationMap.get(strValue.toLowerCase());
				}
				else if(name.equals("toleranceType")) {
					newRow.toleranceType = translationMap.get(strValue.toLowerCase());
				}
				else if(name.equals("tolerance")) {
					try {
						newRow.tolerance = IlsProperty.parseDouble(strValue);
					} catch (ParseException e) {
						logger.error("Error parsing PVMonitorConfig.tolerance double value from " + strValue);
					}
				}
				else if(name.equals("consistency")) {
					try {
						newRow.consistency = IlsProperty.parseDouble(strValue);
					} catch (ParseException e) {
						logger.error("Error parsing PVMonitorConfig.consistency double value from " + strValue);
					}
				}
				else if(name.equals("persistence")) {
					try {
						newRow.persistence = IlsProperty.parseDouble(strValue);
					} catch (ParseException e) {
						logger.error("Error parsing PVMonitorConfig.persistence double value from " + strValue);
					}
				}
				else if(name.equals("download")) {
					newRow.download = translationMap.get(strValue.toLowerCase());
				}
				else if(name.equals("limits")) {
					newRow.limits = translationMap.get(strValue.toLowerCase());
				}
				else if(name.equals("targetId")) {
					newRow.targetNameIdOrValue = strValue;
				}
				else if(name.equals("targetType")) {
					// TODO: should this be an enum?
					newRow.targetType = strValue;
				}
				else if(name.equals("pvId")) {
					newRow.pvKey = strValue;
				}
				else if(name.equals("state")) {
					// TODO: should this be an enum?
					newRow.status = strValue;
				}
				else {
					logger.error("Error parsing PVMonitorConfig: Unknown G2 attribute - " + name);
				}
			}
		}
		String json = null;
		try {
			json = config.toJSON();
		} catch(JsonProcessingException e) {
			logger.error("Error generating json for PVMonitorConfig", e);
		}
		result.put(Constants.PV_MONITOR_CONFIG, json);
		return result;
	}

}
