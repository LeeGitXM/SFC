package com.ils.sfc.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CollectDataConfig {
	public String errorHandling = IlsSfcNames.ABORT;
	private List<Row> rows = new ArrayList<Row>();
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

	public String toJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
	
	public static CollectDataConfig fromJSON(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, CollectDataConfig.class);
	}

}
