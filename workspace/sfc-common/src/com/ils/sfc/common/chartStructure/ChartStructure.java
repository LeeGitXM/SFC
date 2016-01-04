package com.ils.sfc.common.chartStructure;

import java.util.ArrayList;
import java.util.List;

import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.definitions.ElementDefinition;

/** 
 * A class to hold the relationship structure of a single SFC chart in a way that is 
 *  convenient for our purposes. 
 */
public class ChartStructure {
	private static String TAG = "ChartStructure";
	private final LoggerEx log;
	private final static boolean DEBUG_CHART = false;
	// Enclosing Steps in another chart that contain this chart:
	private final List<StepStructure> parents = new ArrayList<StepStructure>();
	private final List<StepStructure> steps; 	// the steps in this chart
	private final String name;                  // the name (last path element) of this chart
	private final long resourceId;
	private final String path;                  // the path of this chart
	
	public ChartStructure(String name,long resid,String path) {
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.name = name;
		this.resourceId = resid;
		this.path = path;
		this.steps= new ArrayList<StepStructure>();
		if(log.isTraceEnabled()||DEBUG_CHART) log.infof("%s: Created %s (%d)", TAG,name,resid);
	}
	
	
	public void addParent(StepStructure step) {
		parents.add(step);
	}  
	
	
	public String getName() {return this.name;}
	public long getResourceId() { return this.resourceId; }
	public String getPath() { return this.path; }

	//Get all chart steps that enclose this chart.
	public List<StepStructure> getParents() {return parents;}
	public List<StepStructure> getSteps() {return steps;}
	
	/** 
	 * Find a step with the given factory id, recursing down to enclosed charts if any.
	 * @return null if none found.
	 */ 
	public StepStructure findStepWithFactoryId(String factoryId) {
		for(StepStructure step: getSteps()) {
			if(ElementDefinition.ElementType.Step.equals(step.getElementType()) && step.getFactoryId().equals(factoryId)) {
				return step;
			}
		}
		return null;
	}

	/** Find a step with the given factory id, recursing down to enclosed charts if any.
	 * @return null if none found.
	 */
	public StepStructure findStepWithFactoryIdInSubtree(String factoryId) {
		StepStructure result = null;
		if((result = findStepWithFactoryId(factoryId)) != null ) {
			return result;
		}
		// Didn't find it in this chart; look in subcharts, or parallel section
		for(StepStructure step: getSteps()) {
			if(step.getEnclosedChart() != null) {
				if((result = step.getEnclosedChart().findStepWithFactoryIdInSubtree(factoryId)) != null) {
					return result;
				}
			}
		}
		return null;
	}

	/** Find a step with the given id, recursing down to enclosed charts if any. 
	 * @return null if none found.
	 */ 
	public StepStructure findStepWithId(String id) {
		for(StepStructure step: getSteps()) {
			if(step.getId().equals(id)) {
				return step;
			}
		}
		return null;
	}
	
	/** Find a step with the given factory id, recursing down to enclosed charts if any.
	* @return null if none found.
	*/  
	public StepStructure findStepWithIdInSubtree(String id) {
		StepStructure result = null;
		if((result = findStepWithId(id)) != null ) {
			return result;
		}
		// Didn't find it in this chart; look in subcharts
		for(StepStructure step: getSteps()) {
			if(step.getEnclosedChart() != null) {
				if((result = step.getEnclosedChart().findStepWithIdInSubtree(id)) != null) {
					return result;
				}
			}
		}
		return null;
	}

	public void addStep(StepStructure step) {
		steps.add(step);
	}
}