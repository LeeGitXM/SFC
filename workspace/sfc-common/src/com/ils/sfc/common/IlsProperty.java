package com.ils.sfc.common;

import com.inductiveautomation.ignition.common.config.BasicProperty;

@SuppressWarnings("serial")
public class IlsProperty<T> extends BasicProperty<T> {
	private int sortOrder;

	public IlsProperty(String name, Class<T> clazz, T defaultValue) {
		super(name, clazz, defaultValue);
	}
	
	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
		
}
