/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */
package com.ils.sfc.designer;

import java.util.ArrayList;
import java.util.List;

import com.ils.sfc.client.step.AbstractIlsStepUI;
import com.ils.sfc.common.IlsSfcNames;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.step.*;
import com.ils.sfc.designer.recipeEditor.RecipeDataEditorFrame;
import com.ils.sfc.designer.stepEditor.IlsStepEditor;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.model.DesignerModuleHook;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;
import com.inductiveautomation.sfc.client.api.ClientStepRegistryProvider;
import com.inductiveautomation.sfc.designer.SFCDesignerHook;
import com.inductiveautomation.sfc.designer.api.StepConfigRegistry;
import com.jidesoft.docking.DockableFrame;

public class IlsSfcDesignerHook extends AbstractDesignerModuleHook implements DesignerModuleHook {
	private DesignerContext context = null;
	private final LoggerEx log;
	//private SFCWorkspace sfcWorkspace;
	//private JPopupMenu stepPopup;
	private SFCDesignerHook iaSfcHook;
	private RecipeDataEditorFrame recipeEditorFrame = new RecipeDataEditorFrame();
	
	//private ChartManagerService chartManager;
	private static String[] editorFactoryIds = {
    	QueueMessageStepProperties.FACTORY_ID,
    	SetQueueStepProperties.FACTORY_ID,
    	ShowQueueStepProperties.FACTORY_ID,
    	ClearQueueStepProperties.FACTORY_ID,
    	YesNoStepProperties.FACTORY_ID,
    	AbortStepProperties.FACTORY_ID,
    	PauseStepProperties.FACTORY_ID,
    	ControlPanelMessageStepProperties.FACTORY_ID,
    	TimedDelayStepProperties.FACTORY_ID,
    	DeleteDelayNotificationStepProperties.FACTORY_ID,
    	PostDelayNotificationStepProperties.FACTORY_ID,
       	EnableDisableStepProperties.FACTORY_ID,
       	SelectInputStepProperties.FACTORY_ID,
       	LimitedInputStepProperties.FACTORY_ID,
       	DialogMessageStepProperties.FACTORY_ID,
       	CollectDataStepProperties.FACTORY_ID,
       	InputStepProperties.FACTORY_ID,
       	RawQueryStepProperties.FACTORY_ID,
       	SimpleQueryStepProperties.FACTORY_ID,
       	SaveDataStepProperties.FACTORY_ID,
       	PrintFileStepProperties.FACTORY_ID,
       	PrintWindowStepProperties.FACTORY_ID,
       	CloseWindowStepProperties.FACTORY_ID,
       	ShowWindowStepProperties.FACTORY_ID,
        ReviewDataStepProperties.FACTORY_ID,   
        ReviewDataWithAdviceStepProperties.FACTORY_ID,   
        ReviewFlowsStepProperties.FACTORY_ID,   
        // enclosing step uses IA editor
	};
	
	public IlsSfcDesignerHook() {
		log = LogUtil.getLogger(getClass().getPackage().getName());
	}
		
	@Override
	public List<DockableFrame> getFrames() {
      	List<DockableFrame> frames = new ArrayList<>();
       	frames.add(recipeEditorFrame);       	
       	return frames;
	}
	
	@Override
	public void initializeScriptManager(ScriptManager mgr) {
		super.initializeScriptManager(mgr);
		PythonCall.setScriptMgr(mgr);
		mgr.addStaticFields("system.ils.sfc", IlsSfcNames.class);
	}
	
	@Override
	public void startup(DesignerContext ctx, LicenseState activationState) throws Exception {
		this.context = ctx;
        log.debug("IlsSfcDesignerHook.startup...");
		iaSfcHook = (SFCDesignerHook)context.getModule(SFCModule.MODULE_ID);
      	iaSfcHook.getWorkspace().getInnerWorkspace().addDesignableWorkspaceListener(recipeEditorFrame);
		recipeEditorFrame.getController().setContext(context);
		
		// Register steps
		ClientStepRegistry stepRegistry =  ((ClientStepRegistryProvider)iaSfcHook).getStepRegistry();
		for(ClientStepFactory clientStepFactory: AbstractIlsStepUI.clientStepFactories) {
			stepRegistry.register(clientStepFactory);
		}
		    	
		// register the step config factories (ie the editors)
		IlsStepEditor.Factory editorFactory = new IlsStepEditor.Factory();
    	StepConfigRegistry configRegistry = (StepConfigRegistry) context.getModule(SFCModule.MODULE_ID);
    	for(String factoryId: editorFactoryIds) {
    		configRegistry.register(factoryId, editorFactory);
    	}    	
	}
	
	@Override
	public void shutdown() {	
	}

}
