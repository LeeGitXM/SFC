package com.ils.sfc.common.rowconfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfirmControllersConfig extends RowConfig {
	private List<Row> rows = new ArrayList<Row>();
	
	public static class Row {
		public String key;
		public boolean checkSPFor0;
		public boolean checkPathToValve;
	}
	
	public List<Row> getRows() {
		return rows;
	}

	public static ConfirmControllersConfig fromJSON(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, ConfirmControllersConfig.class);
	}
}
