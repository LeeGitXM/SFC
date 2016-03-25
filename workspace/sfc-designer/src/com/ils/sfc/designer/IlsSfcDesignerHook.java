/**
 *   (c) 2014-2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.designer;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import system.ils.sfc.common.Constants;

import com.ils.sfc.client.step.AbstractIlsStepUI;
import com.ils.sfc.common.IlsClientScripts;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.chartStructure.ChartStructureManager;
import com.ils.sfc.common.step.AllSteps;
import com.ils.sfc.designer.browser.SfcBrowserFrame;
import com.ils.sfc.designer.browser.execute.ChartRunner;
import com.ils.sfc.designer.browser.validation.ValidationDialog;
import com.ils.sfc.designer.recipeEditor.RecipeEditorFrame;
import com.ils.sfc.designer.search.IlsSfcSearchProvider;
import com.ils.sfc.designer.stepEditor.IlsStepEditor;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.modules.ModuleInfo;
import com.inductiveautomation.ignition.common.modules.ModuleInfo.ModuleDependency;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.ProjectChangeListener;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.model.DesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.menu.JMenuMerge;
import com.inductiveautomation.ignition.designer.model.menu.MenuBarMerge;
import com.inductiveautomation.ignition.designer.model.menu.WellKnownMenuConstants;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.api.elements.StepFactory;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;
import com.inductiveautomation.sfc.client.api.ClientStepRegistryProvider;
import com.inductiveautomation.sfc.designer.SFCDesignerHook;
import com.inductiveautomation.sfc.designer.api.StepConfigRegistry;
import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;

public class IlsSfcDesignerHook extends AbstractDesignerModuleHook implements DesignerModuleHook, ProjectChangeListener {
	private final static String TAG = "IlsSfcDesignerHook";
	private static final String INTERFACE_MENU_TITLE  = "External Interface Configuration";
	private static final String START_MENU_PRODUCTION_TITLE      = "Start Chart (production)";
	private static final String START_MENU_ISOLATION_TITLE       = "Start Chart (isolation)";
	private static final String VALIDATION_MENU_TITLE = "Validate Charts";
	private DesignerContext context = null;
	private final LoggerEx log;
	//private SFCWorkspace sfcWorkspace;
	//private JPopupMenu stepPopup;
	private SFCDesignerHook iaSfcHook;
	private SfcBrowserFrame browser = null;
	private final List<DockableFrame> frames;
	private ChartStructureManager structureManager = null;
	private IlsSfcSearchProvider searchProvider = null;
	private RecipeEditorFrame recipeEditorFrame;
	private RecipeDataCleaner recipeDataCleaner;
	
	public IlsSfcDesignerHook() {
		log = LogUtil.getLogger(getClass().getPackage().getName());
		this.frames = new ArrayList<>();
	}
		
	@Override
	public List<DockableFrame> getFrames() { return frames; }
	
	@Override
	public void initializeScriptManager(ScriptManager mgr) {
		super.initializeScriptManager(mgr);
		PythonCall.setScriptMgr(mgr);
		mgr.addStaticFields("system.ils.sfc", Constants.class);
		mgr.addScriptModule("system.ils.sfc", IlsClientScripts.class);
		// Initialize units. Since this is a lazy initialization, 
    	Object[] args = {null};
    	try {
			PythonCall.INITIALIZE_UNITS.exec(args);
		} catch (JythonExecException e) {
			log.error("Error initializing units in Designer");
		}
    }
	
	// Insert a menu to allow control of database and tag provider.
	// If the menu already exists, do nothing
    @Override
    public MenuBarMerge getModuleMenu() {
    	
        MenuBarMerge merge = new MenuBarMerge(SFCModule.MODULE_ID);  
 
        JMenuMerge viewMenu = new JMenuMerge(WellKnownMenuConstants.VIEW_MENU_NAME);
        viewMenu.addSeparator();
        viewMenu.addSeparator();
        
        if( !menuExists(context.getFrame(),INTERFACE_MENU_TITLE) ) {
        	Action interfaceAction = new AbstractAction(INTERFACE_MENU_TITLE) {
        		private static final long serialVersionUID = 5374667367733312464L;
        		public void actionPerformed(ActionEvent ae) {
        			SwingUtilities.invokeLater(new DialogRunner());
        		}
        	};
            viewMenu.add(interfaceAction);
        }
        // ----------------------- Menu to launch chart validator -----------------------------
        Action validateAction = new AbstractAction(VALIDATION_MENU_TITLE) {
            private static final long serialVersionUID = 5374667367733312464L;
            public void actionPerformed(ActionEvent ae) {
                SwingUtilities.invokeLater(new ValidationDialogRunner());
            }
        };

        viewMenu.add(validateAction);
        viewMenu.addSeparator();
        viewMenu.addSeparator();
        merge.add(WellKnownMenuConstants.VIEW_MENU_LOCATION, viewMenu);
        
        // ----------------------- Menus to start current chart -----------------------------
        Action executeIsolationAction = new AbstractAction(START_MENU_ISOLATION_TITLE) {
            private static final long serialVersionUID = 5374887367733312464L;
            public void actionPerformed(ActionEvent ae) {
            	SFCDesignerHook iaSfcHook = (SFCDesignerHook)context.getModule(SFCModule.MODULE_ID);
                Thread runner = new Thread(new ChartRunner(context,iaSfcHook.getWorkspace(),browser.getModel(),true));
                runner.start();
            }
        };
        Action executeProductionAction = new AbstractAction(START_MENU_PRODUCTION_TITLE) {
            private static final long serialVersionUID = 5374667367733312464L;
            public void actionPerformed(ActionEvent ae) {
            	SFCDesignerHook iaSfcHook = (SFCDesignerHook)context.getModule(SFCModule.MODULE_ID);
                Thread runner = new Thread(new ChartRunner(context,iaSfcHook.getWorkspace(),browser.getModel(),false));
                runner.start();
            }
        };
        JMenuMerge toolsMenu = new JMenuMerge(WellKnownMenuConstants.TOOLS_MENU_NAME);
        toolsMenu.addSeparator();
        toolsMenu.add(executeIsolationAction);
        toolsMenu.add(executeProductionAction);
        merge.add(WellKnownMenuConstants.TOOLS_MENU_LOCATION, toolsMenu);
        return merge;
    }
    
	@Override
	public void startup(DesignerContext ctx, LicenseState activationState) throws Exception {
		this.context = ctx;
		DesignerUtil.context = ctx;
        log.debug("IlsSfcDesignerHook.startup...");
		iaSfcHook = (SFCDesignerHook)context.getModule(SFCModule.MODULE_ID);
		recipeEditorFrame = new RecipeEditorFrame(ctx, iaSfcHook.getWorkspace());
      	iaSfcHook.getWorkspace().getInnerWorkspace().addDesignableWorkspaceListener(recipeEditorFrame);
		
      	frames.add(recipeEditorFrame); 
       	browser = new SfcBrowserFrame(context);
       	browser.setInitMode(DockContext.STATE_AUTOHIDE);
       	browser.setInitSide(DockContext.DOCK_SIDE_WEST);
       	browser.setInitIndex(1);
       	frames.add(browser);
       	
		// Register steps
		ClientStepRegistry stepRegistry =  ((ClientStepRegistryProvider)iaSfcHook).getStepRegistry();
		for(ClientStepFactory clientStepFactory: AbstractIlsStepUI.clientStepFactories) {
			stepRegistry.register(clientStepFactory);
		}
		recipeDataCleaner = new RecipeDataCleaner(context, stepRegistry);
		context.getGlobalProject().addProjectChangeListener(recipeDataCleaner);
		    	
		// register the step config factories (ie the editors)
		IlsStepEditor.Factory editorFactory = new IlsStepEditor.Factory(context);
    	StepConfigRegistry configRegistry = (StepConfigRegistry) context.getModule(SFCModule.MODULE_ID);
    	for(String factoryId: AllSteps.editorFactoryIds) {
    		configRegistry.register(factoryId, editorFactory);
    	} 
    	IlsClientScripts.setContext(context);
    	// Provide a central repository for the structure of the charts
    	structureManager = new ChartStructureManager(context.getGlobalProject().getProject(),stepRegistry);
    	searchProvider = new IlsSfcSearchProvider(context);
		context.registerSearchProvider(searchProvider);
		context.addProjectChangeListener(this);
		
		new Thread(new ModuleWatcher(context)).start();             // Watch for modules to start
 	}
	
	@Override
	public void shutdown() {	
		context.removeProjectChangeListener(this);
		context.removeProjectChangeListener(recipeDataCleaner);
		frames.remove(browser);
		frames.remove(recipeEditorFrame);
	}
	
	public ChartStructureManager getChartStructureManager() {return structureManager;}

	// Search the menu tree to see if the same menu has been added by another module
	private boolean menuExists(Frame frame,String title) {
		for(Component c:context.getFrame().getComponents() ) {
			if( c instanceof JRootPane ) {
				JRootPane root = (JRootPane)c;
				JMenuBar bar = root.getJMenuBar();
				if( bar!=null ) {
					int count = bar.getMenuCount();
					int index = 0;
					while( index<count) {
						JMenu menu = bar.getMenu(index);
						if( menu.getName().equalsIgnoreCase(WellKnownMenuConstants.VIEW_MENU_NAME)) {
							int nitems = menu.getItemCount();
							int jndex = 0;
							while(jndex<nitems ) {
								JMenuItem item = menu.getItem(jndex);
								if( item!=null ) {
									String name = item.getText();
									if( title.equalsIgnoreCase(name)) return true;
								}
								jndex++;
							}
							break;
						}
						index++;
					}
				}
			}
		}
		return false;
	}
	// =================================== Project Change Listener ========================
	// No matter what the change is, we re-compute the maps
	@Override
	public void projectResourceModified(ProjectResource res,ResourceModification modType) {
		structureManager.getCompiler().compile();
		
	}
	@Override
	public void projectUpdated(Project proj) {
		structureManager.getCompiler().compile();
	}
	
	
	/**
     * Display a popup dialog for configuration of dialog execution parameters.
     * Run in a separate thread, as a modal dialog in-line here will freeze the UI.
     */
    private class DialogRunner implements Runnable {

        public void run() {
            ExternalInterfaceConfigurationDialog setup = new ExternalInterfaceConfigurationDialog(context);
            setup.pack();
            setup.setVisible(true);
        }
    }
	/**
	 * We are dependent on the Ignition SFC module, but don't know about other modules that
	 * also may be dependent on "com.inductiveautomation.sfc". In order for any custom chart
	 * classes to be registered, we need to wait on those modules also
	 */
	private class ModuleWatcher implements Runnable {
		private final DesignerContext ctx;
		public ModuleWatcher(DesignerContext dc) {
			this.ctx = dc;
		}
		public void run() {
			boolean ready = false;
			while( !ready ) {
				List<ModuleInfo> moduleInfos = ctx.getModules();
				for( ModuleInfo minfo:moduleInfos ) {
					Collection<ModuleDependency> dependencies = minfo.getDependencies().values();
					for(ModuleDependency dep:dependencies) {
						if( dep.getModuleId().equals(SFCModule.MODULE_ID) ) {
							log.infof("%s.MainMenuWatcher ...%s depends on %s",TAG,minfo.getName(),SFCModule.MODULE_ID);
						}
					}
					// Don't really know how to wait until module is ready. We just assume it
					// works by letting whatever calls startup() finish.
					try { Thread.sleep( 2000 ); }
					catch (InterruptedException ignore) {}
				}
				ready = true;
			}
			ctx.addProjectChangeListener(browser);
		}
	}
	/**
     * Display a popup dialog for configuration of dialog execution parameters.
     * Run in a separate thread, as a modal dialog in-line here will freeze the UI.
     */
    private class ValidationDialogRunner implements Runnable {

        public void run() {
            log.debugf("%s.Launching setup dialog...",TAG);
            ValidationDialog validator = new ValidationDialog(context,browser.getModel());
            validator.pack();
            validator.setVisible(true);
            browser.addChangeListener(validator);
        }
    }
	// ===================================== Main ===================================
    public static void main(String[] args) {
    	try {
    	for(Class<?> clazz: AllSteps.propertyClasses) {
    		String className = clazz.getSimpleName();
    		for(Field field: clazz.getDeclaredFields()) {
        		if(field.getName().equals("properties")) {
        			Collection properties = (Collection)field.get(null);
        			for(Object o: properties) {
        				Property property = (Property) o;
        				System.out.println(property.getName());
        			}
        		}
    			
    		}
    	}
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    }
}
