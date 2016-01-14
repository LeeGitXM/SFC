package com.inductiveautomation.examples.sfc.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.inductiveautomation.ignition.common.expressions.DefaultFunctionFactory.EscapeXML;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.sfc.api.StepDelegate;
import com.inductiveautomation.sfc.api.XMLParseException;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults.CompilationError;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class AbstractExampleStepDelegate implements StepDelegate,
		ExampleStepProperties {

	private List<Property<?>> compilationAlteringProperties;

	protected AbstractExampleStepDelegate() {
		compilationAlteringProperties = new ArrayList<Property<?>>(1);
		compilationAlteringProperties.add(EXAMPLE_PROPERTY);
	}

	@Override
	public List<Property<?>> getCompilationAlteringProperties() {
		return compilationAlteringProperties;
	}

	@Override
	public String getId() {
		return FACTORY_ID;
	}

	@Override
	public void toXML(XMLStreamWriter writer, ChartUIElement element, String arg2)
			throws XMLStreamException {
		int value = element.getOrDefault(EXAMPLE_PROPERTY);
		writer.writeStartElement("my-property");
		writer.writeCharacters(Integer.toString(value));
		writer.writeEndElement();
	}

	@Override
	public void fromXML(Element dom, ChartUIElement ui)
			throws XMLParseException {
		NodeList list = dom.getElementsByTagName("my-property");
		if (list.getLength()>0) {
			int propValue = Integer.parseInt(list.item(0).getTextContent());
			ui.set(EXAMPLE_PROPERTY, propValue);
		}
	}
	
	@Override
	public void validate(ChartUIElement element, ChartCompilationResults results) {
		// check stuff in element
		if (element.getOrDefault(ExampleStepProperties.EXAMPLE_PROPERTY) < 1) {
			results.addError(new CompilationError("Example error: number must be positive", element.getLocation()));
		}
	}

}
