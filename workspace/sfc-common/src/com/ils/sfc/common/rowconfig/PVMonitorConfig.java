package com.ils.sfc.common.rowconfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PVMonitorConfig extends RowConfig {
	private List<Row> rows = new ArrayList<Row>();
	public static class Row {
		public boolean enabled;
		public String pvKey;
		public String targetType;
		public String targetNameIdValue;
		public String strategy;
		public String limits;
		public String download;
		public double persistence;
		public double consistency;
		public double deadTime;
		public double tolerance;
		public String type;
		public String status;
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


}
