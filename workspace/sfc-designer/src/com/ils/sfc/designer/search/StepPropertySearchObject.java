package com.ils.sfc.designer.search;

import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.client.step.AbstractIlsStepUI;
import com.inductiveautomation.ignition.client.util.gui.ErrorUtil;
import com.inductiveautomation.ignition.designer.findreplace.SearchObject;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
/**
 * The property iteration is trivial. On first call
 * return the property name, on the second call return its value.
 * @author chuckc
 *
 */
public class StepPropertySearchObject implements SearchObject {
	private final DesignerContext context;
	private final long resourceId;
	private final String parentName;
	private final String propertyName;
	private final String value;
	private final ResourceBundle rb;
	
	public StepPropertySearchObject(DesignerContext ctx,String parent,long resid,String name, String val) {
		this.context = ctx;
		this.resourceId = resid;
		this.parentName = parent;
		this.propertyName = name;
		this.value = val;
		this.rb = ResourceBundle.getBundle("com.ils.sfc.designer.designer");  // designer.properties
	}
	@Override
	public Icon getIcon() {
		ImageIcon icon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/text_tree.png"));
		return icon;
	}

	@Override
	public String getName() {
		return propertyName;
	}

	/**
	 * This should be a path to the object.
	 */
	@Override
	public String getOwnerName() {
		return parentName;
	}

	@Override
	public String getText() {
		return value;
	}

	@Override
	public void locate() {
		ChartLocator locator = new ChartLocator(context);
		locator.locate(resourceId);
	}

	@Override
	public void setText(String arg0) throws IllegalArgumentException {
		ErrorUtil.showWarning(rb.getString("Locator.ScopeDataChangeWarning"), rb.getString("Locator.WarningTitle"));	
	}

}
