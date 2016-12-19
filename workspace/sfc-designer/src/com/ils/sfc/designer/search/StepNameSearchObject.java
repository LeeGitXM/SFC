package com.ils.sfc.designer.search;

import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.client.step.AbstractIlsStepUI;
import com.inductiveautomation.ignition.client.util.gui.ErrorUtil;
import com.inductiveautomation.ignition.designer.findreplace.SearchObject;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
/**
 * Simply return the stepname for editing.
 * @author chuckc
 *
 */
public class StepNameSearchObject implements SearchObject {

	private final String chartPath;
	private final long chartResourceId;
	private final String stepName;
	private final DesignerContext context;
	private final ResourceBundle rb;
	
	public StepNameSearchObject(DesignerContext ctx,String path,long resid,String step) {
		this.context = ctx;
		this.chartResourceId = resid;
		this.chartPath = path;
		this.stepName = step;
		this.rb = ResourceBundle.getBundle("com.ils.sfc.designer.designer");  // designer.properties
	}
	@Override
	public Icon getIcon() {
		ImageIcon icon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/step.png"));
		return icon;
	}

	@Override
	public String getName() {
		return stepName;
	}

	@Override
	public String getOwnerName() {
		return chartPath;
	}

	@Override
	public String getText() {
		return stepName;
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
