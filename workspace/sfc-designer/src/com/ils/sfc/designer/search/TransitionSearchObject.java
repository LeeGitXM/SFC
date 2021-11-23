package com.ils.sfc.designer.search;

import java.io.StringWriter;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;

import com.ils.sfc.client.step.AbstractIlsStepUI;
import com.inductiveautomation.ignition.client.util.gui.ErrorUtil;
import com.inductiveautomation.ignition.common.project.resource.ProjectResourceId;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.findreplace.SearchObject;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
/**
 * Transition
 * @author chuckc
 *
 */
public class TransitionSearchObject implements SearchObject {

	private final String chartPath;
	private final ProjectResourceId chartResourceId;
	private final Element step;
	private final DesignerContext context;
	private final ResourceBundle rb;
	private final LoggerEx log;
	
	public TransitionSearchObject(DesignerContext ctx, String chartPath, ProjectResourceId resid, Element step) {
		this.context = ctx;
		this.chartResourceId = resid;
		this.chartPath = chartPath;
		this.step = step;
		this.rb = ResourceBundle.getBundle("com.ils.sfc.designer.designer");  // designer.properties
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
	}
	@Override
	public Icon getIcon() {
		ImageIcon icon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/step.png"));
		return icon;
	}

	@Override
	public String getName() {
		return "Expression";
	}

	@Override
	public String getOwnerName() {
		String location = step.getAttribute("location");
		return chartPath + " - transition at - " + location;
	}

	@Override
	public String getText() {
		String text = step.getTextContent();
		//log.infof("TransitionSearchObject expression: %s", text);
		return text;
	}

	@Override
	public void locate() {
		ChartLocator locator = new ChartLocator(context);
		locator.locate(chartResourceId.getResourcePath());
	}

	@Override
	public void setText(String arg0) throws IllegalArgumentException {
		ErrorUtil.showWarning(rb.getString("Locator.StepChangeWarning"), rb.getString("Locator.WarningTitle"));
		
	}
}
