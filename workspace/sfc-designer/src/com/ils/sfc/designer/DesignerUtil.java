package com.ils.sfc.designer;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class DesignerUtil {
	
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
