package com.ils.sfc.common.recipe.objects;

import com.ils.sfc.common.IlsProperty;

/**
??
 */
public class EMData extends Data {
	
	public EMData() {
		addProperty(IlsProperty.HIGH_LIMIT);
		addProperty(IlsProperty.LOW_LIMIT);
	}
}
