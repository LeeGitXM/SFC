/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */
package com.ils.sfc.designer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import com.ils.sfc.client.step.*;
import com.ils.sfc.common.recipe.RecipeData;
import com.ils.sfc.common.recipe.RecipeDataManager;
import com.ils.sfc.common.chartStructure.IlsSfcChartStructureCompiler;
import com.ils.sfc.common.chartStructure.IlsSfcChartStructureMgr;
import com.ils.sfc.common.step.*;
import com.ils.sfc.designer.browser.IlsBrowserFrame;
import com.ils.sfc.designer.recipeEditor.RecipeDataBrowser;
import com.ils.sfc.util.IlsSfcCommonUtils;
import com.ils.sfc.util.IlsSfcNames;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.designable.IDesignTool;
import com.inductiveautomation.ignition.designer.designable.tools.SelectionTool;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.model.DesignerModuleHook;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.api.elements.StepElement;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;
import com.inductiveautomation.sfc.client.api.ClientStepRegistryProvider;
import com.inductiveautomation.sfc.client.ui.StepComponent;
import com.inductiveautomation.sfc.designer.SFCDesignerHook;
import com.inductiveautomation.sfc.designer.api.StepConfigFactory;
import com.inductiveautomation.sfc.designer.api.StepConfigRegistry;
import com.inductiveautomation.sfc.designer.workspace.SFCWorkspace;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;
import com.inductiveautomation.sfc.uimodel.ChartUIModel;
import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;

public class IlsSfcDesignerHook extends AbstractDesignerModuleHook implements DesignerModuleHook {
	private DesignerContext context = null;
	private final LoggerEx log;
	private IlsBrowserFrame browser = null;
	private SFCWorkspace sfcWorkspace;
	private JPopupMenu stepPopup;
	private SFCDesignerHook iaSfcHook;

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
    	// register step factories. this is duplicated in IlsSfcClientHook.
		iaSfcHook = (SFCDesignerHook)context.getModule(SFCModule.MODULE_ID);
		RecipeDataManager.setDesignerContext(context);
		RecipeDataManager.setGlobalProject(context.getGlobalProject().getProject());
		RecipeDataManager.setStepRegistry(iaSfcHook.getStepRegistry());
		initializeStepPopup();
		
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
		stepRegistry.register(SaveDataStepUI.FACTORY);
		stepRegistry.register(IlsEnclosingStepUI.FACTORY);
		stepRegistry.register(PrintFileStepUI.FACTORY);
		stepRegistry.register(PrintWindowStepUI.FACTORY);
		stepRegistry.register(CloseWindowStepUI.FACTORY);
		stepRegistry.register(ShowWindowStepUI.FACTORY);
		stepRegistry.register(ProcedureStepUI.FACTORY);
		stepRegistry.register(OperationStepUI.FACTORY);
		stepRegistry.register(PhaseStepUI.FACTORY);
		    	
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
       	configRegistry.register(SaveDataStepProperties.FACTORY_ID, editorFactory);
       	configRegistry.register(PrintFileStepProperties.FACTORY_ID, editorFactory);
       	configRegistry.register(PrintWindowStepProperties.FACTORY_ID, editorFactory);
       	configRegistry.register(CloseWindowStepProperties.FACTORY_ID, editorFactory);
       	configRegistry.register(ShowWindowStepProperties.FACTORY_ID, editorFactory);
       	StepConfigFactory encFactory = iaSfcHook.getConfigFactory(EnclosingStepProperties.FACTORY_ID);
        configRegistry.register(ProcedureStepProperties.FACTORY_ID, encFactory);       	
        configRegistry.register(OperationStepProperties.FACTORY_ID, encFactory);       	
        configRegistry.register(PhaseStepProperties.FACTORY_ID, encFactory);       	
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
