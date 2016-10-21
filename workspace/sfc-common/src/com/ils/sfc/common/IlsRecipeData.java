/**
 *   (c) 2016  ILS Automation. All rights reserved.
 */
package com.ils.sfc.common;

import java.util.Collection;
import java.util.HashSet;

import com.ils.sfc.common.recipe.objects.Array;
import com.ils.sfc.common.recipe.objects.EMData;
import com.ils.sfc.common.recipe.objects.Group;
import com.ils.sfc.common.recipe.objects.Input;
import com.ils.sfc.common.recipe.objects.Matrix;
import com.ils.sfc.common.recipe.objects.Output;
import com.ils.sfc.common.recipe.objects.OutputRamp;
import com.ils.sfc.common.recipe.objects.SQC;
import com.ils.sfc.common.recipe.objects.Structure;
import com.ils.sfc.common.recipe.objects.Timer;
import com.ils.sfc.common.recipe.objects.Value;

public class IlsRecipeData {
	private static Collection<Class<?>> recipeDataClasses = new HashSet<>();
	static {
		recipeDataClasses.add(Group.class);
		recipeDataClasses.add(Input.class);
		recipeDataClasses.add(Output.class);
		recipeDataClasses.add(OutputRamp.class);
		recipeDataClasses.add(Matrix.class);
		recipeDataClasses.add(SQC.class);
		recipeDataClasses.add(Timer.class);
		recipeDataClasses.add(Value.class);
		recipeDataClasses.add(Structure.class);
		recipeDataClasses.add(Array.class);
		recipeDataClasses.add(EMData.class);
	}
	
	public static Collection<Class<?>> getRecipeClasses() { return recipeDataClasses; }
}
