package com.ils.sfc.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ReviewDataConfig {
	private List<Row> rows = new ArrayList<Row>();
	public static class Row {
		public String configKey;
		public String valueKey;
		public String recipeScope;
		public String prompt;
		public String advice;
		public String unitType;
		public String units;
		
		@JsonIgnore
		public boolean isBlank() {
			return IlsSfcCommonUtils.isEmpty(valueKey); 
		}
	}

	public List<Row> getRows() {
		return rows;
	}

	public String toJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
	
	public static ReviewDataConfig fromJSON(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, ReviewDataConfig.class);
	}

	public static void main(String[] args) {
		try {
			ReviewDataConfig config = new ReviewDataConfig();
			//config.rows.add(new Row());
			String json = config.toJSON();
			System.out.print(json);
		}
		catch(Exception e ) {
			e.printStackTrace();
		}
	}
}
