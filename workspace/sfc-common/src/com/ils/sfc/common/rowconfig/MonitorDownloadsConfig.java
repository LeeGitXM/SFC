package com.ils.sfc.common.rowconfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MonitorDownloadsConfig extends RowConfig {
	private List<Row> rows = new ArrayList<Row>();
	
	public static class Row {
		public String key;
		public String labelAttribute;
		public String units;
	}

	public List<Row> getRows() {
		return rows;
	}

	public static MonitorDownloadsConfig fromJSON(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, MonitorDownloadsConfig.class);
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

	public static Map<String,String> convert(Element g2block) {
		Map<String, String> result = new HashMap<String, String>();
		return result;
	}
}
