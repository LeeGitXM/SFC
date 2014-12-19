package com.ils.sfc.common.chartStructure;

import java.util.Map;

/** Basically a container to hold structure info for all the SFC charts, 
 *  plus any utility methods that need to go across charts. 
 *
 */
public class IlsSfcChartStructureMgr {
	private Map<String, IlsSfcChartStructure> chartsByName;
	
	public IlsSfcChartStructureMgr(Map<String, IlsSfcChartStructure> chartsByName) {
		this.chartsByName = chartsByName;
	}
	
	public Map<String, IlsSfcChartStructure> getChartsByName() {
		return chartsByName;
	}
	
	/** Looking across all charts, find the step with the given id. */
	public IlsSfcStepStructure getStepWithId(String id) {
		IlsSfcStepStructure result = null;
		for(IlsSfcChartStructure chart: chartsByName.values()) {
			if((result = chart.findStepWithId(id)) != null) {
				return result;
			}
		}
		return null;
	}

	/** Looking across all charts, find the step with the given id. */
	public IlsSfcStepStructure getStepWithFactoryId(String factoryId) {
		IlsSfcStepStructure result = null;
		for(IlsSfcChartStructure chart: chartsByName.values()) {
			if((result = chart.findStepWithFactoryId(factoryId)) != null) {
				return result;
			}
		}
		return null;
	}

}
