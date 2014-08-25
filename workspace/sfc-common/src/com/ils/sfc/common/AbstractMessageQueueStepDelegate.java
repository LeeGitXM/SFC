package com.ils.sfc.common;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Element;

import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.sfc.api.StepDelegate;
import com.inductiveautomation.sfc.api.XMLParseException;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults.CompilationError;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class AbstractMessageQueueStepDelegate implements StepDelegate,
		MessageQueueStepProperties {

	@Override
	public List<Property<?>> getCompilationAlteringProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		return FACTORY_ID;
	}

	@Override
	public void toXML(XMLStreamWriter writer, ChartUIElement element, String arg2)
			throws XMLStreamException {
		String message = element.getOrDefault(MESSAGE_PROPERTY);
		writer.writeStartElement(MESSAGE_PROPERTY.getName());
		writer.writeCharacters(message);
		writer.writeEndElement();
	}

	@Override
	public void fromXML(Element dom, ChartUIElement ui)
			throws XMLParseException {
		ui.set(MESSAGE_PROPERTY, IlsSfcCommonUtils.getStringProperty(MESSAGE_PROPERTY, dom));
	}
	
	@Override
	public void validate(ChartUIElement element, ChartCompilationResults results) {
		// check stuff in element
		results.addError(new CompilationError("bad stuff", element.getLocation()));
	}

}
