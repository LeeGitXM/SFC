/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */
package com.ils.sfc.designer;

import com.ils.sfc.client.step.*;
import com.ils.sfc.common.step.AbortStepProperties;
import com.ils.sfc.common.step.ClearQueueStepProperties;
import com.ils.sfc.common.step.CollectDataStepProperties;
import com.ils.sfc.common.step.ControlPanelMessageStepProperties;
import com.ils.sfc.common.step.DebugPropertiesStepProperties;
import com.ils.sfc.common.step.DeleteDelayNotificationStepProperties;
import com.ils.sfc.common.step.DialogMessageStepProperties;
import com.ils.sfc.common.step.EnableDisableStepProperties;
import com.ils.sfc.common.step.InputStepProperties;
import com.ils.sfc.common.step.LimitedInputStepProperties;
import com.ils.sfc.common.step.PauseStepProperties;
import com.ils.sfc.common.step.PostDelayNotificationStepProperties;
import com.ils.sfc.common.step.QueueMessageStepProperties;
import com.ils.sfc.common.step.RawQueryStepProperties;
import com.ils.sfc.common.step.SelectInputStepProperties;
import com.ils.sfc.common.step.SetQueueStepProperties;
import com.ils.sfc.common.step.ShowQueueStepProperties;
import com.ils.sfc.common.step.SimpleQueryStepProperties;
import com.ils.sfc.common.step.TimedDelayStepProperties;
import com.ils.sfc.common.step.YesNoStepProperties;
import com.ils.sfc.util.IlsResponseManager;
import com.ils.sfc.util.IlsSfcNames;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.model.DesignerModuleHook;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;
import com.inductiveautomation.sfc.client.api.ClientStepRegistryProvider;
import com.inductiveautomation.sfc.designer.api.StepConfigRegistry;

public class IlsSfcDesignerHook extends AbstractDesignerModuleHook implements DesignerModuleHook {
	private DesignerContext context = null;
	private final LoggerEx log;
	
	public IlsSfcDesignerHook() {
		log = LogUtil.getLogger(getClass().getPackage().getName());
	}
		
	@Override
	public void initializeScriptManager(ScriptManager mgr) {
		super.initializeScriptManager(mgr);
		PythonCall.setScriptMgr(mgr);
		mgr.addScriptModule("system.ils.sfc", IlsResponseManager.class);
		mgr.addStaticFields("system.ils.sfc", IlsSfcNames.class);
	}
	
	@Override
	public void startup(DesignerContext ctx, LicenseState activationState) throws Exception {
		this.context = ctx;

    	// register step factories. this is duplicated in IlsSfcClientHook.
		Object iaSfcHook = context.getModule(SFCModule.MODULE_ID);
		ClientStepRegistry stepRegistry =  ((ClientStepRegistryProvider)iaSfcHook).getStepRegistry();
		stepRegistry.register(QueueMessageStepUI.FACTORY);
		stepRegistry.register(SetQueueStepUI.FACTORY);
		stepRegistry.register(ShowQueueStepUI.FACTORY);
		stepRegistry.register(ClearQueueStepUI.FACTORY);
		stepRegistry.register(YesNoStepUI.FACTORY);
		stepRegistry.register(AbortStepUI.FACTORY);
		stepRegistry.register(PauseStepUI.FACTORY);
		stepRegistry.register(ControlPanelMessageStepUI.FACTORY);
		stepRegistry.register(TimedDelayStepUI.FACTORY);
		stepRegistry.register(DeleteDelayNotificationStepUI.FACTORY);
		stepRegistry.register(PostDelayNotificationStepUI.FACTORY);
		stepRegistry.register(EnableDisableStepUI.FACTORY);
		stepRegistry.register(SelectInputStepUI.FACTORY);
		stepRegistry.register(LimitedInputStepUI.FACTORY);
		stepRegistry.register(DialogMessageStepUI.FACTORY);
		stepRegistry.register(CollectDataStepUI.FACTORY);
		stepRegistry.register(InputStepUI.FACTORY);
		stepRegistry.register(RawQueryStepUI.FACTORY);
		stepRegistry.register(SimpleQueryStepUI.FACTORY);
		stepRegistry.register(DebugPropertiesStepUI.FACTORY);
		    	
		// register the config factories (ie the editors)
		IlsStepEditor.Factory editorFactory = new IlsStepEditor.Factory();
    	StepConfigRegistry configRegistry = (StepConfigRegistry) context.getModule(SFCModule.MODULE_ID);
    	configRegistry.register(QueueMessageStepProperties.FACTORY_ID, editorFactory);
    	configRegistry.register(SetQueueStepProperties.FACTORY_ID, editorFactory);
    	configRegistry.register(ShowQueueStepProperties.FACTORY_ID, editorFactory);
    	configRegistry.register(ClearQueueStepProperties.FACTORY_ID, editorFactory);
    	configRegistry.register(YesNoStepProperties.FACTORY_ID, editorFactory);
    	configRegistry.register(AbortStepProperties.FACTORY_ID, editorFactory);
    	configRegistry.register(PauseStepProperties.FACTORY_ID, editorFactory);
    	configRegistry.register(ControlPanelMessageStepProperties.FACTORY_ID, editorFactory);
    	configRegistry.register(TimedDelayStepProperties.FACTORY_ID, editorFactory);
    	configRegistry.register(DeleteDelayNotificationStepProperties.FACTORY_ID, editorFactory);
    	configRegistry.register(PostDelayNotificationStepProperties.FACTORY_ID, editorFactory);
       	configRegistry.register(EnableDisableStepProperties.FACTORY_ID, editorFactory);
       	configRegistry.register(SelectInputStepProperties.FACTORY_ID, editorFactory);
       	configRegistry.register(LimitedInputStepProperties.FACTORY_ID, editorFactory);
       	configRegistry.register(DialogMessageStepProperties.FACTORY_ID, editorFactory);
       	configRegistry.register(CollectDataStepProperties.FACTORY_ID, editorFactory);
       	configRegistry.register(InputStepProperties.FACTORY_ID, editorFactory);
       	configRegistry.register(RawQueryStepProperties.FACTORY_ID, editorFactory);
       	configRegistry.register(SimpleQueryStepProperties.FACTORY_ID, editorFactory);
       	configRegistry.register(DebugPropertiesStepProperties.FACTORY_ID, editorFactory);
	}
		
	@Override
	public void shutdown() {	
	}

}
