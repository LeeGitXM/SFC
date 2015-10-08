package com.ils.sfc.common;

/**
 * Holds info for mocking out a level of chartScope data
 */
public class MockInfo {
	private final String chartPath;
	private final String stepName;
	private final String stepFactoryId;

	public MockInfo(String chartPath, String stepName, String stepFactoryId) {
		super();
		this.chartPath = chartPath;
		this.stepName = stepName;
		this.stepFactoryId= stepFactoryId;
	}

	public String getChartPath() {
		return chartPath;
	}

	public String getStepName() {
		return stepName;
	}

	public String getStepFactoryId() {
		return stepFactoryId;
	}
	
}

