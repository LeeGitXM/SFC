package com.ils.sfc.step;

import com.ils.sfc.common.AbstractSetQueueStepDelegate;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ChartStep;
import com.inductiveautomation.sfc.api.ChartStepFactory;
import com.inductiveautomation.sfc.definitions.StepDefinition;


public class SetQueueStepFactory extends AbstractSetQueueStepDelegate implements ChartStepFactory {

    @Override
    public ChartStep create(ChartContext chartContext, StepDefinition definition) {
        return new SetQueueStep(chartContext, definition);
    }
}
