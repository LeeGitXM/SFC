package com.ils.sfc.step;

import com.ils.sfc.common.step.ManualDataEntryStepDelegate;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.api.elements.StepElement;
import com.inductiveautomation.sfc.api.elements.StepFactory;
import com.inductiveautomation.sfc.definitions.StepDefinition;


public class ManualDataEntryStepFactory extends ManualDataEntryStepDelegate implements StepFactory {

    @Override
    public StepElement create(ChartContext chartContext,  ScopeContext scopeContext,
    	StepDefinition definition) {
        return new ManualDataEntryStep(chartContext, scopeContext, definition);
    }
}
