package com.ils.sfc.designer.exim;

import javax.swing.tree.DefaultMutableTreeNode;

class ChartTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = -6754645882363887509L;
	private final String path;
	// The constructor
	public ChartTreeNode(String userData, boolean allowChildren) {
		super(userData,allowChildren);
		this.path = userData;
	}
	
	// Display the last component.
	@Override
	public String toString() {
		String result = path;
		int slashSpot = path.lastIndexOf("/");
		if( slashSpot>=0) {
			result = path.substring(slashSpot+1);
		}
		return result;
	}
}
