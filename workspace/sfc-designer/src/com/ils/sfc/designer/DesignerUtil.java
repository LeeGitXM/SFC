package com.ils.sfc.designer;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.border.EmptyBorder;

import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.designer.workspace.editors.ChartPathComboBox;

public class DesignerUtil {
	static DesignerContext context; 
	
	public static ChartPathComboBox getChartPathComboBox() {
		ChartPathComboBox cb = new ChartPathComboBox();
		Project globalProject = context.getGlobalProject().getProject();
		cb.initRoot(globalProject);
		cb.setBorder(new EmptyBorder(0,0,0,0));
		return cb;
	}
	
	public static void setConstraints(GridBagConstraints con, int anchor, int fill,
		int gridheight, int gridwidth, int gridx, int gridy, Insets insets, double weightx, double weighty ) {
		con.anchor = anchor;
		con.fill = fill;
		con.gridheight = gridheight;
		con.gridwidth = gridwidth;
		con.gridx = gridx;
		con.gridy = gridy;
		con.insets = insets;
		con.weightx = weightx;
		con.weighty = weighty;
	}


}
