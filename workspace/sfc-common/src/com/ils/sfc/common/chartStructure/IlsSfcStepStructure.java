package com.ils.sfc.common.chartStructure;

import com.ils.sfc.common.chartStructure.IlsSfcChartStructure.Parent;

/** A class to hold an SFC Step's relationships in a way that is handy for us. */
public class IlsSfcStepStructure {
	private final String id; // required
	private final String name; // required; unique within chart
	private final String factoryId; // required
	private final IlsSfcChartStructure chart; // the chart that contains this step; required
	private final IlsSfcStepStructure previous; // preceding step, if any; nullable
	private String enclosedChartName; // the full path name of the enclosed chart; may be null or bogus
	private IlsSfcChartStructure enclosedChart; // null unless enclosedChartName refers to a valid chart
	private boolean isEnclosingStep;
	
	public IlsSfcStepStructure(IlsSfcChartStructure chart, String id, String factoryId, String name, 
		IlsSfcStepStructure previous, boolean isEnclosingStep) {
		this.chart = chart;
		this.id = id;
		this.factoryId = factoryId;
		this.name = name;
		this.previous = previous;
		this.isEnclosingStep = isEnclosingStep;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getFactoryId() {
		return factoryId;
	}

	public IlsSfcChartStructure getChart() {
		return chart;
	}

	public IlsSfcStepStructure getPrevious() {
		return previous;
	}

	public String getEnclosedChartName() {
		return enclosedChartName;
	}

	public boolean isEnclosingStep() {
		return isEnclosingStep;
	}

	public void setEnclosedChartName(String enclosedChartName) {
		this.enclosedChartName = enclosedChartName;
	}
	
	public void setEnclosingStep(boolean isEnclosingStep) {
		this.isEnclosingStep = isEnclosingStep;
	}

	public IlsSfcChartStructure getEnclosedChart() {
		return enclosedChart;
	}

	public void setEnclosedChart(IlsSfcChartStructure enclosedChart) {
		this.enclosedChart = enclosedChart;
	}

	/** return the step that encloses this one, else null. If more than one step encloses this one, 
	 *  one is arbitrarily chosen.
	 */
	public IlsSfcStepStructure getParent() {
		if(chart.getParents().size() > 0) {
			return chart.getParents().get(0).step;
		}
		else {
			return null;
		}
	}
	
	/** Get the Procedure at or above this step in the hierarchy, else null. */
	public IlsSfcStepStructure getProcedure() {
		return this.findParentWithNameEnding("Procedure");
	}
	
	/** Get the Operation at or above this step in the hierarchy, else null. */
	public IlsSfcStepStructure getOperation() {
		return this.findParentWithNameEnding("Operation");
	}
	
	/** Get the Procedure at or above this step in the hierarchy, else null. */
	public IlsSfcStepStructure getPhase() {
		return this.findParentWithNameEnding("Phase");
	}
	
	/** Find an enclosing parent (or self) with the given factory id. Returns null
	 *  if none found. */
	public IlsSfcStepStructure findParentWithNameEnding(String ending) {
		if(name.endsWith(ending)) {
			return this;
		}
		else {
			IlsSfcStepStructure result = null;
			for(Parent parent: chart.getParents()) {
				if((result = parent.step.findParentWithNameEnding(ending)) != null) {
					return result;
				}
			}
		}
		return null;
	}

}
