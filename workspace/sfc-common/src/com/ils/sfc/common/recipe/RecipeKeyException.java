package com.ils.sfc.common.recipe;

/** An exception indicating a key does not exist */
@SuppressWarnings("serial")
public class RecipeKeyException extends RecipeDataException {

	public RecipeKeyException(String key) {
		super("key " + key + " does not exist");
	}
}
