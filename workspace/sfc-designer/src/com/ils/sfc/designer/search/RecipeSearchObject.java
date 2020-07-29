package com.ils.sfc.designer.search;

import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.python.core.PyDictionary;

import com.ils.sfc.client.step.AbstractIlsStepUI;
import com.inductiveautomation.ignition.client.util.gui.ErrorUtil;
import com.inductiveautomation.ignition.designer.findreplace.SearchObject;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
/**
 * This is a proxy for search results executed in Python against the database.
 *
 */
public class RecipeSearchObject implements SearchObject {
	// These are field names in the dictionary returned from Python
	public final String NAME = "NAME";
	public final String PARENT = "PARENT";
	public final String RES = "RES";
	public final String TEXT = "TEXT";
	private final DesignerContext context;
	private final String name;
	private final String parentName;
	private final String text;
	private final long resourceId;
	private final ResourceBundle rb;
	
	public RecipeSearchObject(DesignerContext ctx,PyDictionary dict) {
		this.context = ctx;
		this.name = (String)dict.get(NAME);
		this.parentName = (String)dict.get(PARENT);
		this.text = (String)dict.get(TEXT);
		this.resourceId = (Long)dict.get(RES);
		this.rb = ResourceBundle.getBundle("com.ils.sfc.designer.designer");  // designer.properties
	}
	
	@Override
	public Icon getIcon() {
		ImageIcon icon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/table.png"));
		return icon;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getOwnerName() {
		return parentName;
	}

	@Override
	public String getText() {
		return text;
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
