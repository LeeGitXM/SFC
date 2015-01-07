package com.ils.sfc.step;

import com.ils.sfc.common.step.DialogMessageStepDelegate;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.elements.StepElement;
import com.inductiveautomation.sfc.api.elements.StepFactory;
import com.inductiveautomation.sfc.definitions.StepDefinition;


public class DialogMessageStepFactory extends DialogMessageStepDelegate implements StepFactory {

    @Override
    public StepElement create(ChartContext chartContext, StepDefinition definition) {
        return new DialogMessageStep(chartContext, definition);
    }
}
