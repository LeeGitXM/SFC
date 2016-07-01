package com.ils.sfc.common.recipe.objects;

import com.ils.sfc.common.IlsProperty;

/**
  There is no G2 Equivalent (as far as I know)

 */
public class Timer extends DataWithUnits {
	
	public Timer() {
		addProperty(IlsProperty.DRIVER);     // Date.class
		addProperty(IlsProperty.RUNTIME);    // Double.class
		addProperty(IlsProperty.START_TIME); // Date.class
		addProperty(IlsProperty.STATE);      // String.class
	}
}
