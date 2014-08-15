package com.inductiveautomation.sdk.examples.sfc;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Collections;
import java.util.List;

import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ChartStep;
import com.inductiveautomation.sfc.api.ChartStepFactory;
import com.inductiveautomation.sfc.api.XMLParseException;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;
import org.w3c.dom.Element;

public class ExampleStepFactory extends AbstractExampleStepDelegate implements ChartStepFactory {

    @Override
    public ChartStep create(ChartContext chartContext, StepDefinition definition) {
        return new ExampleStep(chartContext, definition);
    }
}
