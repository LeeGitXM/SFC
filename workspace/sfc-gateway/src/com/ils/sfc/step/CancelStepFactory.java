package com.ils.sfc.step;

import javax.xml.stream.XMLStreamWriter;

import com.ils.sfc.common.step.CancelStepDelegate;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.api.elements.StepElement;
import com.inductiveautomation.sfc.api.elements.StepFactory;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;


public class CancelStepFactory extends CancelStepDelegate implements StepFactory {

    @Override
    public StepElement create(ChartContext chartContext, ScopeContext scopeContext,
    	StepDefinition definition) {
        return new CancelStep(chartContext, scopeContext, definition);
    }
    @Override
    public void toXml(XMLStreamWriter write,ChartUIElement cue,String val) {
    	
    }
}
