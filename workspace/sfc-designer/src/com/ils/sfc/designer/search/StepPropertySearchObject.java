package com.ils.sfc.designer.search;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.inductiveautomation.ignition.designer.findreplace.SearchObject;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.definitions.ElementDefinition;
/**
 * The property iteration is trivial. On first call
 * return the property name, on the second call return its value.
 * @author chuckc
 *
 */
public class StepPropertySearchObject implements SearchObject {
	private final DesignerContext context;
	private final String parentName;
	private final String value;
	
	public StepPropertySearchObject(DesignerContext ctx,String parent, String val) {
		this.context = ctx;
		this.parentName = parent;
		this.value = val;
	}
	@Override
	public Icon getIcon() {
		ImageIcon icon = null;
		return icon;
	}

	@Override
	public String getName() {
		return parentName;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setText(String arg0) throws IllegalArgumentException {
		throw new IllegalArgumentException("A step name/data can only be changed in the step editor");	
	}

}
