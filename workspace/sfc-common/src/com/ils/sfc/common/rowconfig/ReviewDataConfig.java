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
import com.ils.sfc.common.IlsSfcCommonUtils;


public class ReviewDataConfig  extends RowConfig {
	private List<Row> rows = new ArrayList<Row>();
	public static class Row {
		public String configKey;
		public String valueKey;
		public String recipeScope;
		public String prompt;
		public String advice;
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
