package com.ils.sfc.step;

import com.ils.sfc.common.AbstractClearQueueStepDelegate;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ChartStep;
import com.inductiveautomation.sfc.api.ChartStepFactory;
import com.inductiveautomation.sfc.definitions.StepDefinition;


public class ClearQueueStepFactory extends AbstractClearQueueStepDelegate implements ChartStepFactory {

    @Override
    public ChartStep create(ChartContext chartContext, StepDefinition definition) {
        return new ClearQueueStep(chartContext, definition);
    }
}
