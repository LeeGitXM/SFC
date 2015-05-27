package com.ils.sfc.step;

import com.ils.sfc.common.step.SaveQueueStepDelegate;
import com.ils.sfc.step.SaveQueueStep;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.api.elements.StepElement;
import com.inductiveautomation.sfc.api.elements.StepFactory;
import com.inductiveautomation.sfc.definitions.StepDefinition;


public class SaveQueueStepFactory extends SaveQueueStepDelegate implements StepFactory {

    @Override
    public StepElement create(ChartContext chartContext,  ScopeContext scopeContext,
    	StepDefinition definition) {
        return new SaveQueueStep(chartContext, scopeContext, definition);
    }
}
