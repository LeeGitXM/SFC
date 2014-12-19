package com.ils.sfc.common.recipe;

import java.util.HashMap;
import java.util.Map;

import com.ils.sfc.common.chartStructure.IlsSfcChartStructure;
import com.ils.sfc.common.chartStructure.IlsSfcChartStructure.Parent;
import com.ils.sfc.common.chartStructure.IlsSfcChartStructureMgr;
import com.ils.sfc.common.chartStructure.IlsSfcStepStructure;
import com.ils.sfc.common.step.OperationStepProperties;
import com.ils.sfc.common.step.PhaseStepProperties;
import com.ils.sfc.common.step.ProcedureStepProperties;
import com.inductiveautomation.sfc.elements.steps.action.ActionStepProperties;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;

import junit.framework.TestCase;

public class RecipeDataTestCase extends TestCase {
	private static final Object value = Integer.valueOf(2);
	private static final String path = "folder.aValue";

	// Some dummy data to mock up charts with the Procedure/Operation/Phase hierarchy:
	private IlsSfcChartStructure procChart = new IlsSfcChartStructure();
	private IlsSfcStepStructure procedureStep = new IlsSfcStepStructure(procChart, "procId", 
		ProcedureStepProperties.FACTORY_ID, "procStep", null, Boolean.TRUE);
	private IlsSfcChartStructure opChart = new IlsSfcChartStructure();
	private IlsSfcStepStructure operationStep = new IlsSfcStepStructure(opChart, "opId", 
		OperationStepProperties.FACTORY_ID, "opStep", null, Boolean.TRUE);
	private IlsSfcChartStructure phaseChart = new IlsSfcChartStructure();
	private IlsSfcStepStructure phaseStep = new IlsSfcStepStructure(phaseChart, "phaseId", 
		PhaseStepProperties.FACTORY_ID, "phaseStep", null, Boolean.TRUE);
	private IlsSfcChartStructure parentChart = new IlsSfcChartStructure();
	private IlsSfcStepStructure parentStep = new IlsSfcStepStructure(parentChart, "parentId", 
		EnclosingStepProperties.FACTORY_ID, "parentStep", null, Boolean.TRUE);
	private IlsSfcChartStructure chart = new IlsSfcChartStructure();
	private IlsSfcStepStructure predStep = new IlsSfcStepStructure(chart, "predId", 
		ActionStepProperties.FACTORY_ID, "predStep", null, Boolean.FALSE);
	private IlsSfcStepStructure childStep = new IlsSfcStepStructure(chart, "childId", 
		ActionStepProperties.FACTORY_ID, "childStep", predStep, Boolean.FALSE);
	private Map<String,IlsSfcChartStructure> chartMap = new HashMap<String,IlsSfcChartStructure> ();
	private IlsSfcChartStructureMgr structureMgr = new IlsSfcChartStructureMgr(chartMap);
	private RecipeData recipeData = new RecipeData();
	
	public void setUp() {
		// Finish wiring together the dummy charts
		recipeData.setStructureManager(structureMgr);
		chartMap.put("procChart", procChart);
		procChart.addStep(procedureStep);
		procedureStep.setEnclosedChart(opChart);
		chartMap.put("opChart", opChart);
		opChart.addStep(operationStep);
		operationStep.setEnclosedChart(phaseChart);
		opChart.getParents().add(new Parent(procChart, procedureStep));
		chartMap.put("phaseChart", phaseChart);
		phaseChart.addStep(phaseStep);
		phaseChart.getParents().add(new Parent(opChart, operationStep));
		phaseStep.setEnclosedChart(parentChart);
		chartMap.put("parentChart", parentChart);
		parentChart.addStep(parentStep);
		parentChart.getParents().add(new Parent(phaseChart, phaseStep));
		parentStep.setEnclosedChart(chart);
		chartMap.put("chart", chart);
		chart.addStep(childStep);
		chart.addStep(predStep);
		chart.getParents().add(new Parent(parentChart, parentStep));
	}
	
	public void testLocal() throws RecipeDataException {
		recipeData.set(RecipeScope.Local, childStep.getId(), path, value, true);
		assertEquals(value, recipeData.getAtLocalScope(childStep.getId(),  path));
		assertEquals(value, recipeData.get(RecipeScope.Local, childStep.getId(), path));
	}

	public void testPrevious() throws RecipeDataException {
		recipeData.set(RecipeScope.Previous, childStep.getId(), path, value, true);
		assertEquals(value, recipeData.getAtPreviousScope(childStep.getId(),  path));
		assertEquals(value, recipeData.get(RecipeScope.Previous, childStep.getId(), path));
		assertEquals(value, recipeData.get(RecipeScope.Local, predStep.getId(), path));
	}

	public void testSuperior() throws RecipeDataException {
		recipeData.set(RecipeScope.Superior, childStep.getId(), path, value, true);
		assertEquals(value, recipeData.getAtSuperiorScope(childStep.getId(),  path));
		assertEquals(value, recipeData.get(RecipeScope.Superior, childStep.getId(), path));
		assertEquals(value, recipeData.get(RecipeScope.Local, parentStep.getId(), path));
	}

	public void testPhase() throws RecipeDataException {
		recipeData.set(RecipeScope.Phase, childStep.getId(), path, value, true);
		assertEquals(value, recipeData.getAtPhaseScope(childStep.getId(),  path));
		assertEquals(value, recipeData.get(RecipeScope.Phase, childStep.getId(), path));
		assertEquals(value, recipeData.get(RecipeScope.Local, phaseStep.getId(), path));
	}

	public void testOperation() throws RecipeDataException {
		recipeData.set(RecipeScope.Operation, childStep.getId(), path, value, true);
		assertEquals(value, recipeData.getAtOperationScope(childStep.getId(),  path));
		assertEquals(value, recipeData.get(RecipeScope.Operation, childStep.getId(), path));
		assertEquals(value, recipeData.get(RecipeScope.Local, operationStep.getId(), path));
	}

	public void testProcedure() throws RecipeDataException {
		recipeData.set(RecipeScope.UnitProcedure, childStep.getId(), path, value, true);
		assertEquals(value, recipeData.getAtProcedureScope(childStep.getId(),  path));
		assertEquals(value, recipeData.get(RecipeScope.UnitProcedure, childStep.getId(), path));
		assertEquals(value, recipeData.get(RecipeScope.Local, procedureStep.getId(), path));
	}

}
