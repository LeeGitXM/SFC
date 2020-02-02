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
 * Simply return the stepname for editing.
 * @author chuckc
 *
 */
public class StepSearchObject implements SearchObject {

	private final String chartPath;
	private final long chartResourceId;
	private final Element step;
	private final DesignerContext context;
	private final ResourceBundle rb;
	private final LoggerEx log;
	
	public StepSearchObject(DesignerContext ctx, String chartPath, long resid, Element step) {
		this.context = ctx;
		this.chartResourceId = resid;
		this.chartPath = chartPath;
		this.step = step;
		this.rb = ResourceBundle.getBundle("com.ils.sfc.designer.designer");  // designer.properties
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		log.infof("Initializing a StepSearchObject");
	}
	@Override
	public Icon getIcon() {
		ImageIcon icon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/step.png"));
		return icon;
	}

	@Override
	public String getName() {
		return step.getAttribute("name");
	}

	@Override
	public String getOwnerName() {
		return chartPath;
	}

	@Override
	public String getText() {
		
		String xmlString = "";
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(step);
			transformer.transform(source, result);
			xmlString = result.getWriter().toString();

		} catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		log.tracef("StepSearchObject Returning: %s", xmlString);
		return xmlString;
	}

	@Override
	public void locate() {
		ChartLocator locator = new ChartLocator(context);
		locator.locate(chartResourceId);
	}

	@Override
	public void setText(String arg0) throws IllegalArgumentException {
		ErrorUtil.showWarning(rb.getString("Locator.StepChangeWarning"), rb.getString("Locator.WarningTitle"));
		
	}
}
