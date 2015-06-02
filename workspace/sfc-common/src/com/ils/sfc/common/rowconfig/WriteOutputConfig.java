package com.ils.sfc.common.rowconfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WriteOutputConfig extends RowConfig {
	private List<Row> rows = new ArrayList<Row>();
	
	public static class Row {
		public String key;
		public boolean confirmWrite;
	}

	public List<Row> getRows() {
		return rows;
	}
	
	public static WriteOutputConfig fromJSON(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, WriteOutputConfig.class);
	}
}
