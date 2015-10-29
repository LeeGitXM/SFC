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
import com.ils.sfc.designer.recipeEditor.RecipeEditorFrame;
import com.ils.sfc.designer.search.IlsSfcSearchProvider;
import com.ils.sfc.designer.stepEditor.IlsStepEditor;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
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
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;
import com.inductiveautomation.sfc.client.api.ClientStepRegistryProvider;
import com.inductiveautomation.sfc.designer.SFCDesignerHook;
import com.inductiveautomation.sfc.designer.api.StepConfigRegistry;
import com.jidesoft.docking.DockableFrame;

public class IlsSfcDesignerHook extends AbstractDesignerModuleHook implements DesignerModuleHook, ProjectChangeListener {
	private static final String INTERFACE_MENU_TITLE  = "External Interface Configuration";
	private DesignerContext context = null;
	private final LoggerEx log;
	//private SFCWorkspace sfcWorkspace;
	//private JPopupMenu stepPopup;
	private SFCDesignerHook iaSfcHook;
	private ChartStructureManager structureManager = null;
	private IlsSfcSearchProvider searchProvider = null;
	private RecipeEditorFrame recipeEditorFrame;
	private RecipeDataCleaner recipeDataCleaner;
	
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
    	
    	if( menuExists(context.getFrame(),INTERFACE_MENU_TITLE) ) return super.getModuleMenu();
    	
        MenuBarMerge merge = new MenuBarMerge(SFCModule.MODULE_ID);  
        merge.addSeparator();

        Action interfaceAction = new AbstractAction(INTERFACE_MENU_TITLE) {
            private static final long serialVersionUID = 5374667367733312464L;
            public void actionPerformed(ActionEvent ae) {
                SwingUtilities.invokeLater(new DialogRunner());
            }
        };

        JMenuMerge controlMenu = new JMenuMerge(WellKnownMenuConstants.VIEW_MENU_NAME);
        controlMenu.add(interfaceAction);
        merge.add(WellKnownMenuConstants.VIEW_MENU_LOCATION, controlMenu);
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
		
		// Register steps
		ClientStepRegistry stepRegistry =  ((ClientStepRegistryProvider)iaSfcHook).getStepRegistry();
		for(ClientStepFactory clientStepFactory: AbstractIlsStepUI.clientStepFactories) {
			stepRegistry.register(clientStepFactory);
		}
				
		recipeDataCleaner = new RecipeDataCleaner(context, stepRegistry);
		context.addProjectChangeListener(recipeDataCleaner);
		    	
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
 	}
	
	@Override
	public void shutdown() {	
		context.removeProjectChangeListener(this);
		context.removeProjectChangeListener(recipeDataCleaner);
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
    
    public static void main(String[] args) {
    	try {
    	for(Class clazz: AllSteps.propertyClasses) {
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
