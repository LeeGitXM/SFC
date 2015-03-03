package com.ils.sfc.step;

import com.ils.sfc.common.step.PhaseStepDelegate;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.api.elements.StepElement;
import com.inductiveautomation.sfc.api.elements.StepFactory;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStep;

public class PhaseStepFactory extends PhaseStepDelegate implements StepFactory {

    @Override
    public StepElement create(ChartContext chartContext,  ScopeContext scopeContext,
    	StepDefinition definition) {
        return new EnclosingStep(chartContext, definition, scopeContext);
    }
}
