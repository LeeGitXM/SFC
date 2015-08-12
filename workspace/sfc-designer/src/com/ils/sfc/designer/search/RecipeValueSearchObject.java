package com.ils.sfc.designer.search;

import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.client.step.AbstractIlsStepUI;
import com.inductiveautomation.ignition.client.util.gui.ErrorUtil;
import com.inductiveautomation.ignition.designer.findreplace.SearchObject;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
/**
 * Return either the binding or the value string.
 * @author chuckc
 *
 */
public class RecipeValueSearchObject implements SearchObject {
	
	private final DesignerContext context;
	private final String parentName;
	private final String property;
	private final long resourceId;
	private final ResourceBundle rb;
	
	public RecipeValueSearchObject(DesignerContext ctx,String parent,long resid,String prop) {
		this.context = ctx;
		this.parentName = parent;
		this.property = prop;
		this.resourceId = resid;
		this.rb = ResourceBundle.getBundle("com.ils.sfc.designer.designer");  // designer.properties
	}
	
	@Override
	public Icon getIcon() {
		ImageIcon icon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/table.png"));
		return icon;
	}

	@Override
	public String getName() {
		return property;
	}

	@Override
	public String getOwnerName() {
		return parentName;
	}

	@Override
	public String getText() {
		return property;
	}

	@Override
	public void locate() {
		ChartLocator locator = new ChartLocator(context);
		locator.locate(resourceId);
	}

	@Override
	public void setText(String arg0) throws IllegalArgumentException {
		ErrorUtil.showWarning(rb.getString("Locator.RecipeDataChangeWarning"), rb.getString("Locator.WarningTitle"));
		
	}
}
