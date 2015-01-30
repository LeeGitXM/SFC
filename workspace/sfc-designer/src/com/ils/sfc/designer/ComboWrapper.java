package com.ils.sfc.designer;

/** Handy class to allow populating a combo box with objects, but with control over
 *  the label that shows.
 */
public class ComboWrapper {
	private String label;
	private Object object;
	
	public ComboWrapper(String label, Object object) {
		super();
		this.label = label;
		this.object = object;
	}
	
	public String getLabel() {
		return label;
	}
	
	public Object getObject() {
		return object;
	};
	
	public String toString() {
		return label;
	}
	
}
