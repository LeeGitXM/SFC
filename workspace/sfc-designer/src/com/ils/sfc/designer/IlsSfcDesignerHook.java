/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */
package com.ils.sfc.designer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import com.ils.sfc.client.step.AbstractIlsStepUI;
import com.ils.sfc.common.chartStructure.IlsSfcChartStructureCompiler;
import com.ils.sfc.common.chartStructure.IlsSfcChartStructureMgr;
import com.ils.sfc.common.recipe.RecipeData;
import com.ils.sfc.common.recipe.RecipeDataManager;
import com.ils.sfc.common.step.AbortStepProperties;
import com.ils.sfc.common.step.ClearQueueStepProperties;
import com.ils.sfc.common.step.CloseWindowStepProperties;
import com.ils.sfc.common.step.CollectDataStepProperties;
import com.ils.sfc.common.step.ControlPanelMessageStepProperties;
import com.ils.sfc.common.step.DeleteDelayNotificationStepProperties;
import com.ils.sfc.common.step.DialogMessageStepProperties;
import com.ils.sfc.common.step.EnableDisableStepProperties;
import com.ils.sfc.common.step.InputStepProperties;
import com.ils.sfc.common.step.LimitedInputStepProperties;
import com.ils.sfc.common.step.PauseStepProperties;
import com.ils.sfc.common.step.PostDelayNotificationStepProperties;
import com.ils.sfc.common.step.PrintFileStepProperties;
import com.ils.sfc.common.step.PrintWindowStepProperties;
import com.ils.sfc.common.step.QueueMessageStepProperties;
import com.ils.sfc.common.step.RawQueryStepProperties;
import com.ils.sfc.common.step.ReviewDataStepProperties;
import com.ils.sfc.common.step.SaveDataStepProperties;
import com.ils.sfc.common.step.SelectInputStepProperties;
import com.ils.sfc.common.step.SetQueueStepProperties;
import com.ils.sfc.common.step.ShowQueueStepProperties;
import com.ils.sfc.common.step.ShowWindowStepProperties;
import com.ils.sfc.common.step.SimpleQueryStepProperties;
import com.ils.sfc.common.step.TimedDelayStepProperties;
import com.ils.sfc.common.step.YesNoStepProperties;
import com.ils.sfc.designer.browser.IlsBrowserFrame;
import com.ils.sfc.designer.recipeEditor.RecipeDataBrowser;
import com.ils.sfc.util.IlsSfcCommonUtils;
import com.ils.sfc.util.IlsSfcNames;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.designable.IDesignTool;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.model.DesignerModuleHook;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;
import com.inductiveautomation.sfc.client.api.ClientStepRegistryProvider;
import com.inductiveautomation.sfc.client.ui.StepComponent;
import com.inductiveautomation.sfc.designer.SFCDesignerHook;
import com.inductiveautomation.sfc.designer.api.StepConfigFactory;
import com.inductiveautomation.sfc.designer.api.StepConfigRegistry;
import com.inductiveautomation.sfc.designer.workspace.SFCWorkspace;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;
import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;

public class IlsSfcDesignerHook extends AbstractDesignerModuleHook implements DesignerModuleHook {
	private DesignerContext context = null;
	private final LoggerEx log;
	private IlsBrowserFrame browser = null;
	private SFCWorkspace sfcWorkspace;
	private JPopupMenu stepPopup;
	private SFCDesignerHook iaSfcHook;
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
        // enclosing step uses IA editor
	};
	
	public IlsSfcDesignerHook() {
		log = LogUtil.getLogger(getClass().getPackage().getName());
	}
		
	@Override
	public List<DockableFrame> getFrames() {
		// Add a frame for our custom chart browser
       	List<DockableFrame> frames = new ArrayList<>();
       	browser = new IlsBrowserFrame(context);
       	browser.setInitMode(DockContext.STATE_AUTOHIDE);
       	browser.setInitSide(DockContext.DOCK_SIDE_WEST);
       	browser.setInitIndex(1);
       	frames.add(browser);
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
        log.debug("starting up...");
   	// register step factories. this is duplicated in IlsSfcClientHook.
		iaSfcHook = (SFCDesignerHook)context.getModule(SFCModule.MODULE_ID);
		initializeRecipeData();
		
		ClientStepRegistry stepRegistry =  ((ClientStepRegistryProvider)iaSfcHook).getStepRegistry();
		for(ClientStepFactory clientStepFactory: AbstractIlsStepUI.clientStepFactories) {
			stepRegistry.register(clientStepFactory);
		}
		    	
		// register the config factories (ie the editors)
		IlsStepEditor.Factory editorFactory = new IlsStepEditor.Factory();
    	StepConfigRegistry configRegistry = (StepConfigRegistry) context.getModule(SFCModule.MODULE_ID);
    	for(String factoryId: editorFactoryIds) {
    		configRegistry.register(factoryId, editorFactory);
    	}
      	
       	// These steps are extensions of IA steps and use the same editor
       	StepConfigFactory encFactory = iaSfcHook.getConfigFactory(EnclosingStepProperties.FACTORY_ID);
 }

	private void initializeRecipeData() {
		RecipeDataManager.setContext(new RecipeDataManager.Context() {
			public long createResourceId() {
				try {
					return context.newResourceId();
				} catch (Exception e) {
					log.error("Error creating resource id", e);
					return 0;
				}
			}
			
			public Project getGlobalProject() {
				return context.getGlobalProject().getProject();
			}

			public boolean isClient() {
				return false;
			}
			
		});
		RecipeDataManager.setStepRegistry(iaSfcHook.getStepRegistry());
		initializeStepPopup();
	}

	private void initializeStepPopup() {
		stepPopup = new JPopupMenu();
		
		JMenuItem createRecipeDataItem = new JMenuItem("Edit Recipe Data");
		createRecipeDataItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StepComponent stepComponent =  (StepComponent) stepPopup.getInvoker();
				UUID stepId = (UUID)IlsSfcCommonUtils.getStepPropertyValue(stepComponent.getElement(), "id");				
				RecipeData recipeData = RecipeDataManager.getData();
				if(recipeData.isInitialized()) {
					RecipeDataBrowser browser = new RecipeDataBrowser(context.getFrame(), stepId.toString());
					browser.setVisible(true);
				}
				else {					
					JOptionPane.showMessageDialog(stepComponent, "Recipe Data cannot be edited due to an initialization error");
				}
			}

		});
		stepPopup.add(createRecipeDataItem);

		// TODO: remove this debug item when no longer needed:
		JMenuItem compileItem = new JMenuItem("Compile Chart Structure");
		compileItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				IlsSfcChartStructureCompiler compiler = new IlsSfcChartStructureCompiler(
					context.getGlobalProject().getProject(), iaSfcHook.getStepRegistry());
				IlsSfcChartStructureMgr structureMgr = compiler.compile();
				for(String msg: compiler.getMessages()) {
					System.out.println(msg);
				}
			}

		});
		stepPopup.add(compileItem);

		JMenuItem clearItem = new JMenuItem("Clear Recipe Data");
		clearItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RecipeDataManager.clear();
			}

		});
		stepPopup.add(clearItem);

		JMenuItem reloadItem = new JMenuItem("Reload Recipe Data");
		reloadItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RecipeDataManager.loadData();
			}

		});
		stepPopup.add(reloadItem);

		sfcWorkspace = iaSfcHook.getWorkspace();		
		final IDesignTool tool = sfcWorkspace.getCurrentTool();
		ClassLoader cl = tool.getClass().getClassLoader();
		Class<?>[] interfaces = { IDesignTool.class, IDesignTool.ToolbarInitializer.class };
		IDesignTool wrapper = (IDesignTool)Proxy.newProxyInstance(cl, interfaces, 
			new InvocationHandler() {
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {				
					Object result = method.invoke(tool, args);
						if(method.getName().equals("onPopupTrigger")) {		
						for(JComponent comp: sfcWorkspace.getSelectedItems()) {
							if(comp instanceof StepComponent) {
								stepPopup.show(comp, 0, 0);
							}
						}
					}
					return result;
				}			
		});
		sfcWorkspace.setCurrentTool(wrapper);
	}
			
	private void printComponents(JComponent parent, int level) {
		for(Component child: parent.getComponents()) {
			if(child instanceof JComponent) {
				for(int i = 0; i < level; i++) {
					System.out.print("   ");
				}
				System.out.println(child.getClass().getSimpleName());
				printComponents((JComponent)child, level + 1);
			}
		}		
	}

	@Override
	public void shutdown() {	
	}

}
