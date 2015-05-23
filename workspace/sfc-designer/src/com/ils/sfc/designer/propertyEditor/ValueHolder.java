package com.ils.sfc.designer.propertyEditor;

/** An "callback" type interface for an editor panel that has requested a value from another panel. */
public interface ValueHolder {	
	public void setValue(Object value);
	public int getIndex();
}
