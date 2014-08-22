/**
 *   (c) 2014  ILS Automation. All rights reserved. 
 */
package com.ils.sfc.test.client;

import com.ils.sfc.test.common.IlsSfcTestProperties;
import com.inductiveautomation.ignition.client.model.ClientContext;
import com.inductiveautomation.ignition.common.expressions.ExpressionFunctionManager;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.xmlserialization.deserialization.XMLDeserializer;
import com.inductiveautomation.vision.api.client.ClientModuleHook;

public class IlsSfcTestClientHook implements ClientModuleHook {

	/**
	 * Make the tag-creation script functions available.
	 */
	@Override
	public void initializeScriptManager(ScriptManager mgr) {
		mgr.addScriptModule(IlsSfcTestProperties.MOCK_SCRIPT_PACKAGE,MockChartScriptFunctions.class);
	}

	@Override
	public void configureDeserializer(XMLDeserializer arg0) {
	}
	
	@Override
	public void notifyActivationStateChanged(LicenseState arg0) {	
	}

	@Override
	public void shutdown() {
	}

	@Override
	public void startup(ClientContext arg0, LicenseState arg1) throws Exception {
	}
	
	@Override
	public void configureFunctionFactory(ExpressionFunctionManager factory) {
	}
}
