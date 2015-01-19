package com.ils.sfc.common.chartStructure;

import java.util.ArrayList;
import java.util.List;

/** A class to hold the relationship structure of a single SFC chart in a way that is 
 *  convenient for our purposes. 
 */
public class IlsSfcChartStructure {
	public static class Parent {
		IlsSfcChartStructure chart;
		IlsSfcStepStructure step;
		
		public Parent(IlsSfcChartStructure chart, IlsSfcStepStructure step) {
			this.chart = chart;
			this.step = step;
		}
	}
	
	public IlsSfcChartStructure(String name) {
		this.name = name;
	}
	
	private String name; // the name of this chart
	// All the steps in this chart:
	private List<IlsSfcStepStructure> steps = new ArrayList<IlsSfcStepStructure>();	// the steps in this chart
	// Enclosing Steps in another chart that contain this chart:
	private List<Parent> parents = new ArrayList<Parent>();
	
	public String getName() {
		return name;
	}

	public void addParent(IlsSfcChartStructure chart, IlsSfcStepStructure step) {
		parents.add(new Parent(chart,step));
	}  
	
	/** Get all chart steps that enclose this chart. */
	public List<Parent> getParents() {
		return parents;
	}
	
	public List<IlsSfcStepStructure> getSteps() {
		return steps;
	}
	
	/** Find a step with the given factory id, recursing down to enclosed charts if any. Returns null
	 *  if none found. */
	public IlsSfcStepStructure findStepWithFactoryId(String factoryId) {
		for(IlsSfcStepStructure step: getSteps()) {
			if(step.getFactoryId().equals(factoryId)) {
				return step;
			}
		}
		return null;
	}
	
	/** Find a step with the given factory id, recursing down to enclosed charts if any. Returns null
	 *  if none found. */
	public IlsSfcStepStructure findStepWithFactoryIdInSubtree(String factoryId) {
		IlsSfcStepStructure result = null;
		if((result = findStepWithFactoryId(factoryId)) != null ) {
			return result;
		}
		// Didn't find it in this chart; look in subcharts
		for(IlsSfcStepStructure step: getSteps()) {
			if(step.getEnclosedChart() != null) {
				if((result = step.getEnclosedChart().findStepWithFactoryIdInSubtree(factoryId)) != null) {
					return result;
				}
			}
		}
		return null;
	}

	/** Find a step with the given id, recursing down to enclosed charts if any. Returns null
	 *  if none found. */
	public IlsSfcStepStructure findStepWithId(String id) {
		for(IlsSfcStepStructure step: getSteps()) {
			if(step.getId().equals(id)) {
				return step;
			}
		}
		return null;
	}
	
	/** Find a step with the given factory id, recursing down to enclosed charts if any. Returns null
	 *  if none found. */
	public IlsSfcStepStructure findStepWithIdInSubtree(String id) {
		IlsSfcStepStructure result = null;
		if((result = findStepWithId(id)) != null ) {
			return result;
		}
		// Didn't find it in this chart; look in subcharts
		for(IlsSfcStepStructure step: getSteps()) {
			if(step.getEnclosedChart() != null) {
				if((result = step.getEnclosedChart().findStepWithIdInSubtree(id)) != null) {
					return result;
				}
			}
		}
		return null;
	}

	public void addStep(IlsSfcStepStructure step) {
		steps.add(step);
	}
}
