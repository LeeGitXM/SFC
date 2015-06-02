package com.ils.sfc.common.rowconfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ils.sfc.common.IlsSfcCommonUtils;


public class ReviewDataConfig  extends RowConfig {
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

	public static ReviewDataConfig fromJSON(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, ReviewDataConfig.class);
	}

}
