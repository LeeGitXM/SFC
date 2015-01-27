package com.ils.sfc.designer.recipeEditor;

import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

/** A node for the Recipe Data browser tree
 *
 */
class RecipeDataTreeNode extends DefaultMutableTreeNode {
	private String name;
	private boolean isMap;
	private boolean contentsAreEditable;
	
	public RecipeDataTreeNode(String name, Object userObject) {
		super(userObject);
		isMap = userObject instanceof Map;
		this.name = name;
	}
	
	@SuppressWarnings("unchecked") Map<String,Object> getMap() {
		return (Map<String,Object>)getUserObject();
	}
	
	public boolean valueIsEditable() {
		return !name.equals(RecipeDataTypes.TYPE) && !isMap; // i.e. value is primitive type
	}
	
	public boolean isBytes() {
		return userObject instanceof byte[];
	}
	
	public boolean contentsAreEditable() {
		return contentsAreEditable && !isBytes();
	}

	public void setContentsEditable(boolean isEditable) {
		this.contentsAreEditable = isEditable;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(name);
		if(isMap) {
			String type = (String)getMap().get("type");
			if(type != null) {
				buf.append(" (");
				buf.append(type);
				buf.append(")");
			}
		}
		else if(isBytes()) {
			buf.append("<bytes>");
		}
		else {
			buf.append(" = ");
			String valueString = getUserObject() != null ? getUserObject().toString() : "null";
			buf.append(valueString);
		}
		return buf.toString();
	}
}

