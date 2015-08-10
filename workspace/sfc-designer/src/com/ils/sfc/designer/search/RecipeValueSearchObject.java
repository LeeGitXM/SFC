package com.ils.sfc.designer.search;

import javax.swing.Icon;

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
	
	public RecipeValueSearchObject(DesignerContext ctx,String parent,String prop) {
		this.context = ctx;
		this.parentName = parent;
		this.property = prop;
	}
	
	@Override
	public Icon getIcon() {
		return null;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setText(String arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}
}
