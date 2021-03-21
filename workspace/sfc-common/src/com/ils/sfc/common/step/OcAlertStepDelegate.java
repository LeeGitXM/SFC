package com.ils.sfc.common.step;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.StepPropertyValidator;
import com.ils.sfc.common.chartStructure.SimpleHierarchyAnalyzer.ChartInfo;
import com.ils.sfc.common.rowconfig.ManualDataEntryConfig;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class OcAlertStepDelegate extends AbstractIlsStepDelegate implements
OcAlertStepProperties{
	
	protected OcAlertStepDelegate() {
		super(properties);
	}
	
	@Override
	public String getId() {
		return FACTORY_ID;
	}
	
	@Override
	public void validate(ChartUIElement element, ChartCompilationResults results) {
		// TODO: check stuff in element
		//results.addError(new CompilationError("bad stuff", element.getLocation()));
	}
}