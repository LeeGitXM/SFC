package com.ils.sfc.common.step;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Element;

import com.ils.sfc.util.IlsProperty;
import com.ils.sfc.util.IlsSfcCommonUtils;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.sfc.api.StepDelegate;
import com.inductiveautomation.sfc.api.XMLParseException;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public abstract class AbstractIlsStepDelegate implements StepDelegate {
	private final IlsProperty<?>[] properties;
	
	protected AbstractIlsStepDelegate(IlsProperty<?>[] properties) {
		this.properties = properties;
		// initialize sort order
		for(int i = 0; i < properties.length; i++) {
			properties[i].setSortOrder(i);
		}
	}
	
	@Override
	public void toXML(XMLStreamWriter writer, ChartUIElement element, String arg2)
			throws XMLStreamException {
		IlsSfcCommonUtils.toXML(writer, element);
	}

	@Override
	public void fromXML(Element dom, ChartUIElement ui)
			throws XMLParseException {
		IlsSfcCommonUtils.fromXML(dom, ui, properties);
	}

	@Override
	public List<Property<?>> getCompilationAlteringProperties() {
		return null;
	}

	public Property<?>[] getProperties() {
		return properties;
	}

}

