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
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.findreplace.SearchObject;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
/**
 * If the property is null, simply return the stepname for editing.
 * @author chuckc
 * name = 
 * <notes>
 * <start-script>
 * <stop-script>
 * <error-handler-script>
 * <timer-script>
 */
public class StepPropertySearchObject implements SearchObject {

	private final String chartPath;
	private final String chartType;
	private final Element property;
	private final DesignerContext context;
	private final ResourceBundle rb;
	private final long resourceId;
	private final LoggerEx log;
	
	public StepPropertySearchObject(DesignerContext ctx, String chartPath, String type, long res, Element property) {
		this.context = ctx;
		this.chartType = type;
		this.chartPath = chartPath;
		this.property = property;
		this.resourceId = res;
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
		return property.getNodeName();
	}

	@Override
	public String getOwnerName() {
		return chartPath + " - " + chartType;
	}

	@Override
	public String getText() {
		String text = property.getTextContent();
		//log.info("StepSearchObject Returning: " + text);
		return text;
	}

	@Override
	public void locate() {
		ChartLocator locator = new ChartLocator(context);
		locator.locate(resourceId);
	}

	@Override
	public void setText(String arg0) throws IllegalArgumentException {
		ErrorUtil.showWarning(rb.getString("Locator.StepChangeWarning"), rb.getString("Locator.WarningTitle"));
		
	}
}
