package com.ils.sfc.designer.search;

import javax.swing.Icon;

import com.ils.sfc.common.IlsSfcModule;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.designer.findreplace.SearchObject;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
/**
 * Simply return the diagram name for editing.
 * @author chuckc
 *
 */
public class ChartNameSearchObject implements SearchObject {
	private final ProjectResource chart;
	private final DesignerContext context;
	
	public ChartNameSearchObject(DesignerContext ctx,ProjectResource pr) {
		this.context = ctx;
		this.chart = pr;
	}
	@Override
	public Icon getIcon() {
		return null;
	}

	@Override
	public String getName() {
		return chart.getName();
	}

	@Override
	public String getOwnerName() {
		return IlsSfcModule.MODULE_NAME;
	}

	@Override
	public String getText() {
		return chart.getName();
	}

	@Override
	public void locate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setText(String arg0) throws IllegalArgumentException {
		throw new IllegalArgumentException("A chart name can be changed only in the Designer navigation tree");
		
	}

}
