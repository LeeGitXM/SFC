package com.ils.sfc.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ils.sfc.common.oldRecipe.RecipeData;

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
