package com.ils.sfc.designer.search;

import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.client.step.AbstractIlsStepUI;
import com.inductiveautomation.ignition.client.util.gui.ErrorUtil;
import com.inductiveautomation.ignition.designer.findreplace.SearchObject;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
/**
 * Simply return the key name for editing.
 * @author chuckc
 *
 */
public class RecipeKeySearchObject implements SearchObject {

	private final String stepPath;
	private final long chartResourceId;
	private final String keyName;
	private final DesignerContext context;
	private final ResourceBundle rb;
	
	public RecipeKeySearchObject(DesignerContext ctx,String path,long resid,String key) {
		this.context = ctx;
		this.chartResourceId = resid;
		this.stepPath = path;
		this.keyName = key;
		this.rb = ResourceBundle.getBundle("com.ils.sfc.designer.designer");  // designer.properties
	}
	@Override
	public Icon getIcon() {
		ImageIcon icon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/key.png"));
		return icon;
	}

	@Override
	public String getName() {
		return keyName;
	}

	@Override
	public String getOwnerName() {
		return stepPath;
	}

	@Override
	public String getText() {
		return keyName;
	}

	@Override
	public void locate() {
		ChartLocator locator = new ChartLocator(context);
		locator.locate(chartResourceId);
	}

	@Override
	public void setText(String arg0) throws IllegalArgumentException {
		ErrorUtil.showWarning(rb.getString("Locator.KeyChangeWarning"), rb.getString("Locator.WarningTitle"));
		
	}

}
