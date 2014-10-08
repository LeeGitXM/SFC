package com.ils.sfc.step;

import com.ils.sfc.common.PauseStepDelegate;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ChartStepFactory;
import com.inductiveautomation.sfc.api.elements.StepElement;
import com.inductiveautomation.sfc.definitions.StepDefinition;


public class PauseStepFactory extends PauseStepDelegate implements ChartStepFactory {

    @Override
    public StepElement create(ChartContext chartContext, StepDefinition definition) {
        return new PauseStep(chartContext, definition);
    }
}
