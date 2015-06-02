package com.ils.sfc.common.rowconfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ils.sfc.common.rowconfig.ReviewDataConfig.Row;

public class MonitorDownloadConfig extends RowConfig {
	private List<Row> rows = new ArrayList<Row>();
	
	public static class Row {
	}

	public List<Row> getRows() {
		return rows;
	}

	public static MonitorDownloadConfig fromJSON(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, MonitorDownloadConfig.class);
	}
}