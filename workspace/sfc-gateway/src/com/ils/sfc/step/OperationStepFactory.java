package com.ils.sfc.step;

import com.ils.sfc.common.step.OperationStepDelegate;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.elements.StepElement;
import com.inductiveautomation.sfc.api.elements.StepFactory;
import com.inductiveautomation.sfc.definitions.StepDefinition;


public class OperationStepFactory extends OperationStepDelegate implements StepFactory {

    @Override
    public StepElement create(ChartContext chartContext, StepDefinition definition) {
        return new OperationStep(chartContext, definition);
    }
}
