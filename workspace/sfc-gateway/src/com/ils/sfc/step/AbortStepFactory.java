package com.ils.sfc.step;

import com.ils.sfc.common.step.AbortStepDelegate;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.api.elements.StepElement;
import com.inductiveautomation.sfc.api.elements.StepFactory;
import com.inductiveautomation.sfc.definitions.StepDefinition;


public class AbortStepFactory extends AbortStepDelegate implements StepFactory {

    @Override
    public StepElement create(ChartContext chartContext, ScopeContext scopeContext,
    	StepDefinition definition) {
        return new AbortStep(chartContext, definition);
    }
}
