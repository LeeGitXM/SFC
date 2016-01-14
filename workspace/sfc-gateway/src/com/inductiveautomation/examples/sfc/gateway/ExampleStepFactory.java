package com.inductiveautomation.examples.sfc.gateway;

import com.inductiveautomation.examples.sfc.common.AbstractExampleStepDelegate;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.api.elements.StepElement;
import com.inductiveautomation.sfc.api.elements.StepFactory;
import com.inductiveautomation.sfc.definitions.StepDefinition;

/**
 * Created by carlg on 12/5/2015.
 */
public class ExampleStepFactory extends AbstractExampleStepDelegate implements StepFactory {
    @Override
    public StepElement create(ChartContext context, ScopeContext scopeContext, StepDefinition definition) {
        return new ExampleStep(definition);
    }
}
