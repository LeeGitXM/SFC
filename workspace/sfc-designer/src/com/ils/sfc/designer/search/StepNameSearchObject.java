package com.ils.sfc.designer.search;

import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.client.step.AbstractIlsStepUI;
import com.inductiveautomation.ignition.client.util.gui.ErrorUtil;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.findreplace.SearchObject;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
/**
 * The property is Name.
 */
public class StepNameSearchObject implements SearchObject {

	private final String chartPath;
	private final String chartType;
	private final long resourceId;
	private final String name;
	private final DesignerContext context;
	private final ResourceBundle rb;
	private final LoggerEx log;
	
	public StepNameSearchObject(DesignerContext ctx, String chartPath, String type, long resid,String property) {
		this.context = ctx;
		this.chartType = type;
		this.chartPath = chartPath;
		this.name = property;
		this.resourceId = resid;
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
		return "Name";
	}

	@Override
	public String getOwnerName() {
		return chartPath + " - "+chartType;
	}

	@Override
	public String getText() {
		return name;
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
