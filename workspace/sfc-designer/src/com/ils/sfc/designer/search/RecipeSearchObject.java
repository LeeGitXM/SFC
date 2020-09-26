package com.ils.sfc.designer.search;

import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.python.core.PyDictionary;

import com.ils.sfc.client.step.AbstractIlsStepUI;
import com.inductiveautomation.ignition.client.util.gui.ErrorUtil;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.findreplace.SearchObject;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
/**
 * This is a proxy for search results executed in Python against the database.
 *
 */
public class RecipeSearchObject implements SearchObject {
	// These are field names in the dictionary returned from Python
	public final String PATH = "PATH";
	public final String STEP = "STEP";
	public final String KEY = "KEY";
	public final String RES = "RES";
	public final String TEXT = "TEXT";
	private final DesignerContext context;
	private final String path;
	private final String step;
	private final String key;
	private final String text;
	private final long resourceId;
	private final ResourceBundle rb;
	
	public RecipeSearchObject(DesignerContext ctx, long resourceId, PyDictionary dict) {
		this.context = ctx;
		this.path = (String)dict.get(PATH);
		this.step = (String)dict.get(STEP);
		this.key = (String)dict.get(KEY);
		this.text = (String)dict.get(TEXT);
		
		long resid = -1;
		String stringValue = (String)dict.get(RES);
		try {		
			if( stringValue!=null ) resid =  Integer.parseInt(stringValue);
		}
		catch(NumberFormatException e) {
			LoggerEx log = LogUtil.getLogger(getClass().getPackage().getName());
			log.warn("RecipeSearchObject:bad integer format: " + stringValue);
		}
		this.resourceId = resourceId;
		this.rb = ResourceBundle.getBundle("com.ils.sfc.designer.designer");  // designer.properties
	}
	
	@Override
	public Icon getIcon() {
		ImageIcon icon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/step.png"));
		return icon;
	}

	@Override
	public String getName() {
		return "Recipe Data:" + this.key;
	}

	@Override
	public String getOwnerName() {
		return this.path + ": Step " + this.step;
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
