package com.ils.sfc.common;

import java.util.ArrayList;
import java.util.List;

public class ReviewDataConfig {
	List<Row> rows = new ArrayList<Row>();
	public static class Row {
		public String configKey;
		public String valueKey;
		public String recipeScope;
		public String prompt;
		public String advice;
		public String unitType;
		public String units;
	}

	public List<Row> getRows() {
		return rows;
	}

}
