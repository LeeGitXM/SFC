package com.ils.sfc.common.recipe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ils.sfc.common.recipe.RecipeData;

public class ReviewDataConfig {
	List<Row> rows = new ArrayList<Row>();
	public static class Row {
		public String configKey;
		public String valueKey;
		public String recipeScope;
		public String prompt;
		public String units;
	}

	public List<Row> getRows() {
		return rows;
	}

	/** Serialize this object into bytes. */
	public byte[] serialize() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		String json = mapper.writeValueAsString(this);
		byte[] bytes = json.getBytes();
		return bytes;
	}

	/** Deserialize an instance from bytes. */
	public static RecipeData deserialize(byte[] bytes) throws JsonParseException, JsonMappingException, IOException { 
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		RecipeData recipeData = mapper.readValue(bytes, RecipeData.class);
		return recipeData;
	}

}
