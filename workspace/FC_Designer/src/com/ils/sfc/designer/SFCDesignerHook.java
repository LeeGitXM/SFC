/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */
package com.ils.sfc.designer;


import com.ils.sfc.common.SFCProperties;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;

public class SFCDesignerHook extends AbstractDesignerModuleHook  {
	private static final String TAG = "SFCDesignerHook";
	public static String BLOCK_BUNDLE_NAME   = "block";        // Properties file is block.properties
	public static String HOOK_BUNDLE_NAME   = "designer";      // Properties file is designer.properties
	public static String PREFIX = SFCProperties.BUNDLE_PREFIX; // Properties are accessed by this prefix


	private DesignerContext context = null;
	private final LoggerEx log;
	
	public SFCDesignerHook() {
		log = LogUtil.getLogger(getClass().getPackage().getName());
	}
	
	
	@Override
	public void initializeScriptManager(ScriptManager mgr) {
		super.initializeScriptManager(mgr);
		//mgr.addScriptModule(SFCProperties.APPLICATION_SCRIPT_PACKAGE,ApplicationScriptFunctions.class);
	}
	
	@Override
	public void startup(DesignerContext ctx, LicenseState activationState) throws Exception {
		this.context = ctx;
	
	}
	
	
	
	@Override
	public void shutdown() {	
	}
}
