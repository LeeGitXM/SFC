package com.ils.sfc.step;

import com.ils.sfc.common.ControlPanelMessageStepDelegate;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ChartStepFactory;
import com.inductiveautomation.sfc.api.elements.StepElement;
import com.inductiveautomation.sfc.definitions.StepDefinition;


public class ControlPanelMessageStepFactory extends ControlPanelMessageStepDelegate implements ChartStepFactory {

    @Override
    public StepElement create(ChartContext chartContext, StepDefinition definition) {
        return new ControlPanelMessageStep(chartContext, definition);
    }
}
