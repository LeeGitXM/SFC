package com.ils.sfc.common.rowconfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WriteOutputConfig extends RowConfig implements java.io.Serializable {
	private List<Row> rows = new ArrayList<Row>();
	
	public static class Row  implements java.io.Serializable {
		public String key;
		public boolean confirmWrite;

		// transient values used in monitoring (maybe this should be a separate object)
		@JsonIgnore
		public String tagPath;
		@JsonIgnore
		public double value;
		@JsonIgnore
		public double timingMinutes;
		@JsonIgnore
		public boolean written;
		@JsonIgnore
		public Object outputRD;
		@JsonIgnore
		public Object io;

}

	public List<Row> getRows() {
		return rows;
	}
	
	public static WriteOutputConfig fromJSON(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, WriteOutputConfig.class);
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
