package com.ils.sfc.step;

import com.ils.sfc.common.SimpleQueryStepDelegate;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ChartStepFactory;
import com.inductiveautomation.sfc.api.elements.StepElement;
import com.inductiveautomation.sfc.definitions.StepDefinition;


public class SimpleQueryStepFactory extends SimpleQueryStepDelegate implements ChartStepFactory {

    @Override
    public StepElement create(ChartContext chartContext, StepDefinition definition) {
        return new SimpleQueryStep(chartContext, definition);
    }
}
