package com.ils.sfc.common.recipe;

import com.ils.sfc.common.recipe.RecipeData.Scope;

import junit.framework.TestCase;

public class RecipeDataTestCase extends TestCase {
	public static final String ID = "id";
	public static final String OPERATION_ID = "opid";
	public static final String PHASE_ID = "phid";
	
	public void testAddStep() throws RecipeDataException {
		RecipeData recipeData = new RecipeData();
		StepData stepData = new StepData("name", ID, "factoryString", "type" );
		recipeData.addStep(stepData);
		assertEquals(stepData, recipeData.getStep(ID));
	}

	public void testRemoveStep() throws RecipeDataException {
		RecipeData recipeData = new RecipeData();
		StepData stepData = new StepData("name", ID, "factoryString", "type" );
		recipeData.addStep(stepData);
		assertTrue(recipeData.stepExists(ID));
		recipeData.removeStep(ID);
		assertFalse(recipeData.stepExists(ID));
		try {
			recipeData.getStep(ID);
			fail();
		} 
		catch(RecipeDataException e ) {}
	}
	
	public void testGetParentWithFactoryId() throws RecipeDataException {
		RecipeData recipeData = new RecipeData();
		StepData operationStepData = new StepData("name", OPERATION_ID, RecipeData.OPERATION_FACTORY_ID, "type" );
		StepData phaseStepData = new StepData("name", PHASE_ID, RecipeData.PHASE_FACTORY_ID, "type" );
		phaseStepData.setParentId(operationStepData.getId());
		recipeData.addStep(operationStepData);
		recipeData.addStep(phaseStepData);
		// leaf level
		assertEquals(phaseStepData, recipeData.getParentWithFactoryId(phaseStepData.getId(), RecipeData.PHASE_FACTORY_ID));
		// parent
		assertEquals(operationStepData, recipeData.getParentWithFactoryId(phaseStepData.getId(), RecipeData.OPERATION_FACTORY_ID));
		try {
			recipeData.getParentWithFactoryId(phaseStepData.getId(), "dummy");
			fail();
		} 
		catch(RecipeDataException e ) {}	
	}

	public void testLocal() throws RecipeDataException {
		RecipeData recipeData = new RecipeData();
		StepData stepData = new StepData("name", ID, "factoryString", "type" );
		recipeData.addStep(stepData);
		Object value = Integer.valueOf(2);
		String path = "folder.aValue";
		recipeData.setLocal(ID, path, value, true);
		assertEquals(value, recipeData.getLocal(ID, path));
		assertEquals(value, recipeData.get(Scope.Local, ID, path));
		assertEquals(value, stepData.get(path));
		try {
			recipeData.setLocal(ID, "nonexistent", value, false);
			fail();
		} 
		catch(RecipeDataException e ) {}	
	}

	public void testPrevious() throws RecipeDataException {
		RecipeData recipeData = new RecipeData();
		StepData stepData = new StepData("name", ID, "factoryString", "type" );
		StepData prevData = new StepData("name", "prevId", "factoryString", "type" );
		stepData.setPreviousStepId(prevData.getId());
		recipeData.addStep(stepData);
		recipeData.addStep(prevData);
		Object value = Integer.valueOf(2);
		String path = "folder.aValue";
		recipeData.setPrevious(ID, path, value, true);
		assertEquals(value, recipeData.getPrevious(ID, path));
		assertEquals(value, recipeData.get(Scope.Previous, ID, path));
		assertEquals(value, prevData.get(path));
		try {
			recipeData.setPrevious(ID, "nonexistent", value, false);
			fail();
		} 
		catch(RecipeDataException e ) {}	
	}

	public void testSuperior() throws RecipeDataException {
		RecipeData recipeData = new RecipeData();
		StepData stepData = new StepData("name", ID, "factoryString", "type" );
		StepData supData = new StepData("name", "supId", "factoryString", "type" );
		stepData.setParentId(supData.getId());
		recipeData.addStep(stepData);
		recipeData.addStep(supData);
		Object value = Integer.valueOf(2);
		String path = "folder.aValue";
		recipeData.setSuperior(ID, path, value, true);
		assertEquals(value, recipeData.getSuperior(ID, path));
		assertEquals(value, recipeData.get(Scope.Superior, ID, path));
		assertEquals(value, supData.get(path));
		try {
			recipeData.setSuperior(ID, "nonexistent", value, false);
			fail();
		} 
		catch(RecipeDataException e ) {}	
	}
	
	public void testPhase() throws RecipeDataException {
		RecipeData recipeData = new RecipeData();
		StepData stepData = new StepData("name", ID, "factoryString", "type" );
		StepData phaseData = new StepData("name", "phid", RecipeData.PHASE_FACTORY_ID, "type" );
		stepData.setParentId(phaseData.getId());
		recipeData.addStep(stepData);
		recipeData.addStep(phaseData);
		Object value = Integer.valueOf(2);
		String path = "folder.aValue";
		recipeData.setPhase(ID, path, value, true);
		assertEquals(value, recipeData.getPhase(ID, path));
		assertEquals(value, recipeData.get(Scope.Phase, ID, path));
		assertEquals(value, phaseData.get(path));
		try {
			recipeData.setPhase(ID, "nonexistent", value, false);
			fail();
		} 
		catch(RecipeDataException e ) {}	
	}
	
	public void testOperation() throws RecipeDataException {
		RecipeData recipeData = new RecipeData();
		StepData stepData = new StepData("name", ID, "factoryString", "type" );
		StepData opData = new StepData("name", "opid", RecipeData.OPERATION_FACTORY_ID, "type" );
		stepData.setParentId(opData.getId());
		recipeData.addStep(stepData);
		recipeData.addStep(opData);
		Object value = Integer.valueOf(2);
		String path = "folder.aValue";
		recipeData.setOperation(ID, path, value, true);
		assertEquals(value, recipeData.getOperation(ID, path));
		assertEquals(value, recipeData.get(Scope.Operation, ID, path));
		assertEquals(value, opData.get(path));
		try {
			recipeData.setOperation(ID, "nonexistent", value, false);
			fail();
		} 
		catch(RecipeDataException e ) {}	
	}
	
	public void testProcedure() throws RecipeDataException {
		RecipeData recipeData = new RecipeData();
		StepData stepData = new StepData("name", ID, "factoryString", "type" );
		StepData prData = new StepData("name", "prid", RecipeData.PROCEDURE_FACTORY_ID, "type" );
		stepData.setParentId(prData.getId());
		recipeData.addStep(stepData);
		recipeData.addStep(prData);
		Object value = Integer.valueOf(2);
		String path = "folder.aValue";
		recipeData.setProcedure(ID, path, value, true);
		assertEquals(value, recipeData.getProcedure(ID, path));
		assertEquals(value, recipeData.get(Scope.UnitProcedure, ID, path));
		assertEquals(value, prData.get(path));
		try {
			recipeData.setProcedure(ID, "nonexistent", value, false);
			fail();
		} 
		catch(RecipeDataException e ) {}	
	}

	public void testNamed() throws RecipeDataException {
		RecipeData recipeData = new RecipeData();
		Object value = Integer.valueOf(2);
		String path = "folder.aValue";
		recipeData.setNamed(path, value, true);
		assertEquals(value, recipeData.getNamed(path));
		assertEquals(value, recipeData.get(Scope.Named, ID, path));
		try {
			recipeData.setNamed("nonexistent", value, false);
			fail();
		} 
		catch(RecipeDataException e ) {}	
	}

}
