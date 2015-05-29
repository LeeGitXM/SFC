package com.ils.sfc.step;

import com.ils.sfc.common.step.MonitorDownloadStepDelegate;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.api.elements.StepElement;
import com.inductiveautomation.sfc.api.elements.StepFactory;
import com.inductiveautomation.sfc.definitions.StepDefinition;


public class MonitorDownloadStepFactory extends MonitorDownloadStepDelegate implements StepFactory {

    @Override
    public StepElement create(ChartContext chartContext,  ScopeContext scopeContext,
    	StepDefinition definition) {
        return new MonitorDownloadStep(chartContext, scopeContext, definition);
    }
}
