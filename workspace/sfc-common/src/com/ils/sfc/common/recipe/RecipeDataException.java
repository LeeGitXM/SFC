package com.ils.sfc.common.recipe;

/** An exception indicating a problem with the structure of Recipe Data. */
@SuppressWarnings("serial")
public class RecipeDataException extends Exception {
	public RecipeDataException(String msg) {
		super(msg);
	}
}
