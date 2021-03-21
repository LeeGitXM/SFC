package com.ils.sfc.step;

import com.ils.sfc.common.step.OcAlertStepDelegate;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.api.elements.StepElement;
import com.inductiveautomation.sfc.api.elements.StepFactory;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class OcAlertStepFactory extends OcAlertStepDelegate implements StepFactory{

    @Override
    public StepElement create(ChartContext chartContext,  ScopeContext scopeContext,
    	StepDefinition definition) {
        return new OcAlertStep(chartContext, scopeContext, definition);
    }
}
