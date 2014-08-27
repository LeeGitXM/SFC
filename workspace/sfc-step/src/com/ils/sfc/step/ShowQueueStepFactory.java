package com.ils.sfc.step;

import com.ils.sfc.common.AbstractShowQueueStepDelegate;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ChartStep;
import com.inductiveautomation.sfc.api.ChartStepFactory;
import com.inductiveautomation.sfc.definitions.StepDefinition;


public class ShowQueueStepFactory extends AbstractShowQueueStepDelegate implements ChartStepFactory {

    @Override
    public ChartStep create(ChartContext chartContext, StepDefinition definition) {
        return new ShowQueueStep(chartContext, definition);
    }
}
