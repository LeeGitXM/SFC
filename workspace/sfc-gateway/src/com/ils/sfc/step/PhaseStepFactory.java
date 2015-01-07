package com.ils.sfc.step;

import com.ils.sfc.common.step.PhaseStepDelegate;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.elements.StepElement;
import com.inductiveautomation.sfc.api.elements.StepFactory;
import com.inductiveautomation.sfc.definitions.StepDefinition;


public class PhaseStepFactory extends PhaseStepDelegate implements StepFactory {

    @Override
    public StepElement create(ChartContext chartContext, StepDefinition definition) {
        return new PhaseStep(chartContext, definition);
    }
}
