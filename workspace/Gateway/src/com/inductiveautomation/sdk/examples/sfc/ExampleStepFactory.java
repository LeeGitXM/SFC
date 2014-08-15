package com.inductiveautomation.sdk.examples.sfc;

import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ChartStep;
import com.inductiveautomation.sfc.api.ChartStepFactory;
import com.inductiveautomation.sfc.definitions.StepDefinition;


public class ExampleStepFactory extends AbstractExampleStepDelegate implements ChartStepFactory {

    @Override
    public ChartStep create(ChartContext chartContext, StepDefinition definition) {
        return new ExampleStep(chartContext, definition);
    }
}
