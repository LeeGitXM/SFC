package com.ils.sfc.common.recipe.objects;

/**
??
 */
public class EMData extends Data {
	
	public EMData() {
		addDynamicProperty("pres", 0.);
		addDynamicProperty("hilim", 0.);
		addDynamicProperty("recc", 0.);
		addDynamicProperty("modattr_val", "");
		addDynamicProperty("lolim", 0.);
		addDynamicProperty("dscr", "");
		addDynamicProperty("stag", "");
		addDynamicProperty("modattr", "");
		addDynamicProperty("ctag", "");
		addDynamicProperty("chg_lev",  "");
	}
}
