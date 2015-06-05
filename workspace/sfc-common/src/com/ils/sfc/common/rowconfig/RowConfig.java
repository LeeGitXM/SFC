package com.ils.sfc.common.rowconfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

public abstract class RowConfig {
	private static final LoggerEx logger = LogUtil.getLogger(RowConfig.class.getName());
			
	public String toJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
	
	/** Create a config from a serialized object, or create from scratch */
	public static RowConfig fromJSON(String json, Class<?> aClass)  {
		if(json != null && json.length() > 0) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				return (RowConfig) mapper.readValue(json, aClass);
			} catch (Exception e) {
				logger.error("Error deserializing config", e);
			} 
		}
		else {
			try {
				return (RowConfig) aClass.newInstance();
			} catch (Exception e) {
				logger.error("Error creating new config", e);				
			}
		}
		return null;
	}

	public abstract void addRow();

	public abstract void removeRow(int index);

	public abstract int getRowCount();
}
