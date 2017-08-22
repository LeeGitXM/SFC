package com.ils.sfc.designer.search;

import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.w3c.dom.Element;

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

	private String chartName = "";
	private final long resourceId;
	private final String parentPath;
	private final DesignerContext context;
	private final ResourceBundle rb;
	
	public ChartNameSearchObject(DesignerContext ctx, String parent, Long resourceId) {
		this.context = ctx;
		this.parentPath = parent;
		this.resourceId = resourceId;
		this.rb = ResourceBundle.getBundle("com.ils.sfc.designer.designer");  // designer.properties
		
		int pos = parentPath.lastIndexOf("/");
		if (pos > 0){
			this.chartName = parentPath.substring(pos + 1);
		}
		else {
			this.chartName = parent;
		}
	}
		
	@Override
	public Icon getIcon() {
		ImageIcon icon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/chart.png"));
		return icon;
	}

	@Override
	public String getName() {
		return chartName;
	}

	@Override
	public String getOwnerName() {
		return parentPath;
	}

	@Override
	public String getText() {
		return chartName;
	}

	@Override
	public void locate() {
		ChartLocator locator = new ChartLocator(context);
		locator.locate(resourceId);
	}

	@Override
	// We can't rename a resource in the project tree from the find replace.
	public void setText(String arg0) throws IllegalArgumentException {
		ErrorUtil.showWarning(rb.getString("Locator.ChartChangeWarning"), rb.getString("Locator.WarningTitle"));
	}

}
