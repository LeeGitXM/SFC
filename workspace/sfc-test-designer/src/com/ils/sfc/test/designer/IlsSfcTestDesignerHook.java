/**
 *   (c) 2014  ILS Automation. All rights reserved.
 *  
 *   This is the .
 */
package com.ils.sfc.test.designer;

import com.ils.sfc.test.common.IlsSfcTestProperties;
import com.inductiveautomation.ignition.common.expressions.ExpressionFunctionManager;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;

/**
 *  This is the class that is instantiated on startup of the designer. Its purpose
 *  is to populate the project tree with a node for workspace creation.
 */
public class IlsSfcTestDesignerHook extends AbstractDesignerModuleHook {
	private final LoggerEx logger;
	
	public IlsSfcTestDesignerHook() {
		logger = LogUtil.getLogger(getClass().getPackage().getName());
		logger.info("Initializing designer hook");
	}
	
	@Override
	public void initializeScriptManager(ScriptManager mgr) {
		super.initializeScriptManager(mgr);
		mgr.addScriptModule(IlsSfcTestProperties.MOCK_SCRIPT_PACKAGE,MockChartScriptFunctions.class);
	}
	
	@Override
	public void startup(DesignerContext context, LicenseState activationState) throws Exception {
		logger.info("Startup...");
		super.startup(context, activationState);
	}
	
	@Override
	public void configureFunctionFactory(ExpressionFunctionManager factory) {
		super.configureFunctionFactory(factory);
	}
}
