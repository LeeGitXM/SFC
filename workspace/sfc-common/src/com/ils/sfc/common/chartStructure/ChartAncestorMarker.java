package com.ils.sfc.common.chartStructure;


// A bean that holds useful information for tracing one's heritage 
public class ChartAncestorMarker {
	public final String chartPath;
	public final String factoryId;
	public final String stepName;  // name of the enclosing step
	
	public ChartAncestorMarker(String path, String id,String name) {
		super();
		this.chartPath = path;
		this.factoryId = id;
		this.stepName = name;
	}

	public String getChartPath() {
		return chartPath;
	}

	public String getFactoryId() {
		return factoryId;
	}

	public String getStepName() {
		return stepName;
	}
}