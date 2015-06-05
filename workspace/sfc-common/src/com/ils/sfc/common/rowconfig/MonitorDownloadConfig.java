package com.ils.sfc.common.rowconfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

}
