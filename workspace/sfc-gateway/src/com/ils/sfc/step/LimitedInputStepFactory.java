package com.ils.sfc.step;

import com.ils.sfc.common.step.LimitedInputStepDelegate;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ChartStepFactory;
import com.inductiveautomation.sfc.api.elements.StepElement;
import com.inductiveautomation.sfc.definitions.StepDefinition;


public class LimitedInputStepFactory extends LimitedInputStepDelegate implements ChartStepFactory {

    @Override
    public StepElement create(ChartContext chartContext, StepDefinition definition) {
        return new LimitedInputStep(chartContext, definition);
    }
}
