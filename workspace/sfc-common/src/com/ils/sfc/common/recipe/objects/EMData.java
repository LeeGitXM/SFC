package com.ils.sfc.common.recipe.objects;

import com.ils.sfc.common.IlsProperty;

/**
 * ExxonMobil-specific data type
 */
public class EMData extends Data {
	
	public EMData() {
		addProperty(IlsProperty.PRES);
		addProperty(IlsProperty.HILIM);
		addProperty(IlsProperty.RECC);
		addProperty(IlsProperty.MODATTR_VAL);
		addProperty(IlsProperty.LOLIM);
		addProperty(IlsProperty.DSCR);
		addProperty(IlsProperty.STAG);
		addProperty(IlsProperty.MODATTR);
		addProperty(IlsProperty.CTAG);
		addProperty(IlsProperty.CHG_LEV);
	}
}
