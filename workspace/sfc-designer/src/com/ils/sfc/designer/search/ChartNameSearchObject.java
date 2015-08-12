package com.ils.sfc.designer.search;

import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.client.step.AbstractIlsStepUI;
import com.inductiveautomation.ignition.client.util.gui.ErrorUtil;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.designer.findreplace.SearchObject;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
/**
 * Simply return the chart name for editing.
 * @author chuckc
 *
 */
public class ChartNameSearchObject implements SearchObject {

	private final String parentPath;
	private final ProjectResource chartResource;
	private final DesignerContext context;
	private final ResourceBundle rb;
	
	public ChartNameSearchObject(DesignerContext ctx,String parent,ProjectResource pr) {
		this.context = ctx;
		this.chartResource = pr;
		this.parentPath = parent;
		this.rb = ResourceBundle.getBundle("com.ils.sfc.designer.designer");  // designer.properties
	}
	@Override
	public Icon getIcon() {
		ImageIcon icon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/chart.png"));
		return icon;
	}

	@Override
	public String getName() {
		return chartResource.getName();
	}

	@Override
	public String getOwnerName() {
		return parentPath;
	}

	@Override
	public String getText() {
		return chartResource.getName();
	}

	@Override
	public void locate() {
		ChartLocator locator = new ChartLocator(context);
		locator.locate(chartResource.getResourceId());
	}

	@Override
	public void setText(String arg0) throws IllegalArgumentException {
		ErrorUtil.showWarning(rb.getString("Locator.ChartChangeWarning"), rb.getString("Locator.WarningTitle"));
	}

}
