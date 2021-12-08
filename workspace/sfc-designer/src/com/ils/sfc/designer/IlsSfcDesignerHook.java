/**
 *   (c) 2014-2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.designer;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import com.ils.sfc.client.step.AbstractIlsStepUI;
import com.ils.sfc.common.IlsClientScripts;
import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.chartStructure.ChartStructureCompiler;
import com.ils.sfc.common.chartStructure.ChartStructureCompilerV2;
import com.ils.sfc.common.chartStructure.ChartStructureManager;
import com.ils.sfc.common.chartStructure.RecipeDataConverter;
import com.ils.sfc.common.chartStructure.RecipeDataMigrator;
import com.ils.sfc.common.chartStructure.SimpleHierarchyAnalyzer;
import com.ils.sfc.common.step.AllSteps;
import com.ils.sfc.designer.exim.ExportSelectionDialog;
import com.ils.sfc.designer.exim.ImportSelectionDialog;
import com.ils.sfc.designer.runner.ChartRunner;
import com.ils.sfc.designer.search.IlsSfcSearchProvider;
import com.ils.sfc.designer.stepEditor.IlsStepEditor;
import com.inductiveautomation.ignition.common.Dataset;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.modules.ModuleInfo;
import com.inductiveautomation.ignition.common.modules.ModuleInfo.ModuleDependency;
import com.inductiveautomation.ignition.common.project.ChangeOperation.CreateResourceOperation;
import com.inductiveautomation.ignition.common.project.ChangeOperation.DeleteResourceOperation;
import com.inductiveautomation.ignition.common.project.ChangeOperation.ModifyResourceOperation;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.ProjectListener;
import com.inductiveautomation.ignition.common.project.ProjectResourceListener;
import com.inductiveautomation.ignition.common.project.resource.ProjectResource;
import com.inductiveautomation.ignition.common.project.resource.ResourcePath;
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
import com.inductiveautomation.ignition.gateway.project.ResourceFilter;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;
import com.inductiveautomation.sfc.client.api.ClientStepRegistryProvider;
import com.inductiveautomation.sfc.designer.SFCDesignerHook;
import com.inductiveautomation.sfc.designer.api.StepConfigRegistry;
import com.inductiveautomation.sfc.designer.workspace.SfcDesignableContainer;
import com.inductiveautomation.sfc.designer.workspace.SfcWorkspace;
import com.jidesoft.docking.DockableFrame;

import system.ils.sfc.common.Constants;

public class IlsSfcDesignerHook extends AbstractDesignerModuleHook implements DesignerModuleHook, ProjectResourceListener {
	private final static String CLSS = "IlsSfcDesignerHook";
	private static final String SFC_SUBMENU_TITLE  = "SFC Extensions";
	private static final String EXPORT_MENU_TITLE  = "Export";
	private static final String IMPORT_MENU_TITLE  = "Import";
	private static final String INTERFACE_MENU_TITLE  = "External Interface Configuration";
	private static final String SHOW_ANCESTOR_TITLE = "Show Chart Ancestor";
	private static final String SHOW_OPERATION_TITLE = "Operation";
	private static final String SHOW_PHASE_TITLE = "Phase";
	private static final String SHOW_PROCEDURE_TITLE             = "Procedure";
	private static final String SHOW_SUPERIOR_TITLE              = "Superior";
	private static final String START_MENU_PRODUCTION_TITLE      = "Start Chart (production)";
	private static final String START_MENU_ISOLATION_TITLE       = "Start Chart (isolation)";
	private static final String RECIPE_DATA_INTERNALIZE = "Internalize Recipe Data for Export";
	private static final String RECIPE_DATA_STORE = "Store Internal Recipe Data into Database";
	private static final String RECIPE_DATA_INITIALIZE = "Initialize Internal Recipe Data";
	private DesignerContext context = null;
	private Project project = null;

	private final LoggerEx log;
	//private JPopupMenu stepPopup;
	private SFCDesignerHook iaSfcHook;
	private final List<DockableFrame> frames;
	private ChartStructureManager structureManager = null;
	private ChartStructureCompilerV2 structureCompilerV2 = null;
	private RecipeDataMigrator recipeDataMigrator = null;
	private IlsSfcSearchProvider searchProvider = null;
	/*	private RecipeEditorFrame recipeEditorFrame; */
	private Map <Long, ProjectResource> addedResourceMap;
	private Map <Long, ProjectResource> changedResourceMap;
	private List <String> deletedResourceList;

	private static ClientStepRegistry stepRegistry;
	private static SfcWorkspace sfcWorkspace;

	public IlsSfcDesignerHook() {
		log = LogUtil.getLogger(getClass().getPackage().getName());
		LogUtil.getLogger("com.ils.sfc.python.structureManager");
		this.frames = new ArrayList<>();
		this.changedResourceMap = new HashMap<>();
		this.deletedResourceList = new ArrayList<>();
		this.addedResourceMap = new HashMap<>();
	}

	@Override
	public List<DockableFrame> getFrames() { return frames; }

	@Override
	public void initializeScriptManager(ScriptManager mgr) {
		super.initializeScriptManager(mgr);
		mgr.addStaticFields("system.ils.sfc", Constants.class);
		mgr.addScriptModule("system.ils.sfc", IlsClientScripts.class);
		// Initialize units. Since this is a lazy initialization, 
		Object[] args = {null};
		try {
			PythonCall.INITIALIZE_UNITS.exec(args);
			Dataset ds = (Dataset)PythonCall.GET_UNITS.exec(args);
			String[] choices = new String[ds.getRowCount()+1];
			int row=0;
			while(row<ds.getRowCount()) {
				// First column in the dataset is the name
				choices[row+1] = ds.getValueAt(row, 0).toString(); 
				row++;
			}
			IlsProperty.setChoices(IlsProperty.UNITS, choices);
		} 
		catch (JythonExecException jee) {
			log.errorf("Error initializing units in Designer hook (%s)",jee.getMessage());
		}
		catch (Exception ex) {
			log.errorf("Exception initializing units in Designer hook (%s)",ex.getMessage());
		}
	}

	// Insert a menu to allow control of database and tag provider.
	// If the menu already exists, do nothing
	@Override
	public MenuBarMerge getModuleMenu() {

		// ----------------------- Menu to launch recipe data importer and exporter (temporary until real fix)  -----------------------------
		Action internalizeRecipeDataAction = new AbstractAction(RECIPE_DATA_INTERNALIZE) {
			private static final long serialVersionUID = 5374887347733312464L;
			public void actionPerformed(ActionEvent ae) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						RecipeDataConverter recipeDataConverter = new RecipeDataConverter(project, context);
						recipeDataConverter.internalize();
					}
				}).start();
			}
		};

		Action storeInternalRecipeDataAction = new AbstractAction(RECIPE_DATA_STORE) {
			private static final long serialVersionUID = 5374487347733312464L;
			public void actionPerformed(ActionEvent ae) {

				new Thread(new Runnable() {
					@Override
					public void run() {
						RecipeDataConverter recipeDataConverter = new RecipeDataConverter(project, context);
						recipeDataConverter.storeToDatabase();
					}
				}).start();
			}
		};

		Action initializeInternalRecipeDataAction = new AbstractAction(RECIPE_DATA_INITIALIZE) {
			private static final long serialVersionUID = 5374487347733312464L;
			public void actionPerformed(ActionEvent ae) {

				new Thread(new Runnable() {
					@Override
					public void run() {
						RecipeDataConverter recipeDataConverter = new RecipeDataConverter(project, context);
						recipeDataConverter.initialize();
					}
				}).start();
			}
		};

		// ----------------------- Menus to start current chart -----------------------------
		Action executeIsolationAction = new AbstractAction(START_MENU_ISOLATION_TITLE) {
			private static final long serialVersionUID = 5374887367733312464L;
			public void actionPerformed(ActionEvent ae) {
				SFCDesignerHook iaSfcHook = (SFCDesignerHook)context.getModule(SFCModule.MODULE_ID);
				Thread runner = new Thread(new ChartRunner(context,iaSfcHook.getWorkspace(),true));
				runner.start();
			}
		};

		Action executeProductionAction = new AbstractAction(START_MENU_PRODUCTION_TITLE) {
			private static final long serialVersionUID = 5374667367733312464L;
			public void actionPerformed(ActionEvent ae) {
				SFCDesignerHook iaSfcHook = (SFCDesignerHook)context.getModule(SFCModule.MODULE_ID);
				Thread runner = new Thread(new ChartRunner(context,iaSfcHook.getWorkspace(),false));
				runner.start();
			}
		};

		Action executeShowOperation = new AbstractAction(SHOW_OPERATION_TITLE) {
			private static final long serialVersionUID = 4029901359528539761L;

			public void actionPerformed(ActionEvent ae) {
				// Show operation for currently displayed chart
				SwingUtilities.invokeLater(new ShowAncestor(Constants.OPERATION));
			}
		};

		Action executeShowPhase = new AbstractAction(SHOW_PHASE_TITLE) {
			private static final long serialVersionUID = 4029901359528539762L;

			public void actionPerformed(ActionEvent ae) {
				// Show phase for currently displayed chart
				SwingUtilities.invokeLater(new ShowAncestor(Constants.PHASE));
			}
		};

		Action executeShowProcedure = new AbstractAction(SHOW_PROCEDURE_TITLE) {
			private static final long serialVersionUID = 4029901359528539762L;

			public void actionPerformed(ActionEvent ae) {
				// Show phase for currently displayed chart
				SwingUtilities.invokeLater(new ShowAncestor(Constants.GLOBAL));
			}
		};

		Action executeShowSuperior = new AbstractAction(SHOW_SUPERIOR_TITLE) {
			private static final long serialVersionUID = 4029901359528539763L;

			public void actionPerformed(ActionEvent ae) {
				// Show superior for currently displayed chart
				SwingUtilities.invokeLater(new ShowAncestor(Constants.SUPERIOR));

			}
		};
		// ----------------------- export/Import Actions -----------------------------------
		Action executeExportAction = new AbstractAction(EXPORT_MENU_TITLE) {
			private static final long serialVersionUID = 5384887367733312464L;
			public void actionPerformed(ActionEvent ae) {
				SwingUtilities.invokeLater(new ShowExportDialog());
			}
		};
		Action executeImportAction = new AbstractAction(IMPORT_MENU_TITLE) {
			private static final long serialVersionUID = 5394887367733312464L;
			public void actionPerformed(ActionEvent ae) {
				SwingUtilities.invokeLater(new ShowImportDialog());
			}
		};
		// ----------------------- Menus to zoom current chart -----------------------------

		JMenuMerge toolsMenu = new JMenuMerge(WellKnownMenuConstants.TOOLS_MENU_NAME);

		JMenu sfcMenu = new JMenu(SFC_SUBMENU_TITLE);
		toolsMenu.add(sfcMenu);

		sfcMenu.addSeparator();

		sfcMenu.add(executeIsolationAction);
		sfcMenu.add(executeProductionAction);

		sfcMenu.addSeparator();

		sfcMenu.add(internalizeRecipeDataAction);
		sfcMenu.add(storeInternalRecipeDataAction);
		sfcMenu.add(initializeInternalRecipeDataAction);

		sfcMenu.addSeparator();

		JMenu showMenu = new JMenu(SHOW_ANCESTOR_TITLE);
		sfcMenu.add(showMenu);
		showMenu.add(executeShowOperation);
		showMenu.add(executeShowPhase);
		showMenu.add(executeShowProcedure);
		showMenu.add(executeShowSuperior);

		/* We want this if they are running BLT or SFC so both modules try and add it, this attempts to only add it if it doesn't already exist.  */
		if( !menuExists(context.getFrame(),INTERFACE_MENU_TITLE) ) {
			Action interfaceAction = new AbstractAction(INTERFACE_MENU_TITLE) {
				private static final long serialVersionUID = 5374667367733312464L;
				public void actionPerformed(ActionEvent ae) {
					SwingUtilities.invokeLater(new DialogRunner());
				}
			};
			sfcMenu.addSeparator();
			sfcMenu.add(interfaceAction);
		}

		sfcMenu.addSeparator();

		sfcMenu.add(executeImportAction);
		sfcMenu.add(executeExportAction);

		MenuBarMerge merge = new MenuBarMerge(SFCModule.MODULE_ID);  
		merge.add(WellKnownMenuConstants.TOOLS_MENU_LOCATION, toolsMenu);
		return merge;
	}

	@Override
	public void startup(DesignerContext ctx, LicenseState activationState) throws Exception {
		this.context = ctx;
		this.project = context.getProject();
		DesignerUtil.context = ctx;
		log.info("IlsSfcDesignerHook.startup...");
		iaSfcHook = (SFCDesignerHook)context.getModule(SFCModule.MODULE_ID);
		/*		recipeEditorFrame = new RecipeEditorFrame(ctx, iaSfcHook.getWorkspace()); */
		sfcWorkspace = iaSfcHook.getWorkspace();
		/*      	iaSfcHook.getWorkspace().getInnerWorkspace().addDesignableWorkspaceListener(recipeEditorFrame); */

		/*      	frames.add(recipeEditorFrame); */

		// Register steps
		stepRegistry =  ((ClientStepRegistryProvider)iaSfcHook).getStepRegistry();
		for(ClientStepFactory clientStepFactory: AbstractIlsStepUI.clientStepFactories) {
			stepRegistry.register(clientStepFactory);
		}

		// Listen to changes on the global project PETE
		context.getProject().addProjectResourceListener(this);

		// register the step config factories (ie the editors)
		IlsStepEditor.Factory editorFactory = new IlsStepEditor.Factory(context);
		StepConfigRegistry configRegistry = (StepConfigRegistry) context.getModule(SFCModule.MODULE_ID);
		for(String factoryId: AllSteps.editorFactoryIds) {
			configRegistry.register(factoryId, editorFactory);
		} 
		PythonCall.setContext(ctx);
		IlsClientScripts.setContext(context);
		// Provide a central repository for the structure of the charts

		// Commented out on 8/1/17 to unwind the old structure manager.
		// Removing this eliminated all of the chart analysis that would happen when opening the designer.  I verified that I could still run charts 
		// From Designer using the Run Production menu and correctly access recipe data.
		//    	structureManager = new ChartStructureManager(context.getGlobalProject().getProject(),stepRegistry);
		//		AbstractIlsStepDelegate.setStructureManager(structureManager);

		// Pete's Structure compiler - instantiate this once in the beginning because the step registry is static.
		structureCompilerV2 = new ChartStructureCompilerV2(context.getProject(), stepRegistry);

		// Pete's Recipe Data Migrator - instantiate this once in the beginning because the step registry is static.
		recipeDataMigrator = new RecipeDataMigrator(context.getProject(), stepRegistry);

		searchProvider = new IlsSfcSearchProvider(context, project);
		context.registerSearchProvider(searchProvider);

		new Thread(new ModuleWatcher(context)).start();             // Watch for modules to start
	}

	public ChartStructureCompilerV2 getStructureCompiler() {
		return structureCompilerV2;
	}

	public static SfcWorkspace getSfcWorkspace() {
		return sfcWorkspace;
	}

	public static ClientStepRegistry getStepRegistry() {
		return stepRegistry;
	}

	@Override
	public void shutdown() {	
		log.info("IlsSfcDesignerHook.shutdown...");
		context.getProject().removeProjectResourceListener(this);
		/*		frames.remove(recipeEditorFrame); */
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

	/**
	 * Display a popup error dialog
	 * Run in a separate thread, as a modal dialog in-line here will freeze the UI.
	 */
	private class ExceptionDialogRunner implements Runnable {
		String msg = "";

		public void setMsg(String errMsg) { this.msg = errMsg;};


		public void run() {
			ExceptionDialog errorDlg = new ExceptionDialog(context, msg);
			errorDlg.pack();
			errorDlg.setVisible(true);
		}
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
					Collection<ModuleDependency> dependencies = minfo.getDependencies();
					for(ModuleDependency dep:dependencies) {
						if( dep.getModuleId().equals(SFCModule.MODULE_ID) ) {
							log.infof("%s.MainMenuWatcher ...%s depends on %s",CLSS,minfo.getName(),SFCModule.MODULE_ID);
						}
					}
					// Don't really know how to wait until module is ready. We just assume it
					// works by letting whatever calls startup() finish.
					try { Thread.sleep( 2000 ); }
					catch (InterruptedException ignore) {}
				}
				ready = true;
			}
		}
	}
	/**
	 * Show a chart superior to the one currently selected. Run in a separate thread.
	 */
	private class ShowAncestor implements Runnable {
		private String level = Constants.GLOBAL;

		public ShowAncestor(String lvl) {
			this.level = lvl;
		}

		public void run() {
			log.infof("%s.run: ShowAncestor %s...",CLSS,level);
			// We get the path to the open chart via the workspace.
			SfcWorkspace workspace = iaSfcHook.getWorkspace();
			SfcDesignableContainer tab = workspace.getSelectedContainer();
			if( tab!=null ) {
				String chartPath = workspace.getSelectedContainer().getResourcePath().getFolderPath();
				SimpleHierarchyAnalyzer hierarchyAnalyzer = new SimpleHierarchyAnalyzer(context.getProject(),stepRegistry);
				hierarchyAnalyzer.analyze();
				List<String> paths = hierarchyAnalyzer.getParentPathsForEnclosingScope(chartPath,level);
				// Now that we have the paths, display them
				for(String path:paths) {
					Long resid = hierarchyAnalyzer.getChartResourceForPath(path);
					ResourcePath resPath = new ResourcePath(workspace.getSelectedContainer().getResourcePath().getResourceType(), path);
					if( resid!=null ) workspace.openChart(resPath);
				}
			}
			else {
				// No chart is open.
			}

		}
	}
	/**
	 * Show a file chooser and import charts from the chosen .sproj file.
	 */
	private class ShowImportDialog implements Runnable {
		private String level = Constants.GLOBAL;

		public ShowImportDialog() {
		}

		public void run() {
			log.infof("%s.run: ShowImportDialog %s...",CLSS,level);
			// We get the path to the open chart via the workspace.
			JRootPane root = context.getRootPaneContainer().getRootPane();
			ImportSelectionDialog dialog = new ImportSelectionDialog(root);
			dialog.pack();
			dialog.setVisible(true);   // Returns when dialog is closed
			File input = dialog.getFilePath();
			if( input!=null ) {
				String path = input.getAbsolutePath();
			}
		}
	}
	/**
	 * Show a dialog that allows the user to choose which charts are to be exported. 
	 */
	private class ShowExportDialog implements Runnable {
		private String level = Constants.GLOBAL;
		public ShowExportDialog() {
		}

		public void run() {
			log.infof("%s.run: ShowExportDialog %s...",CLSS,level);
			// We get the path to the open chart via the workspace.
			SfcWorkspace workspace = iaSfcHook.getWorkspace();
			// We get the path to the open chart via the workspace.
			JRootPane root = context.getRootPaneContainer().getRootPane();
			ExportSelectionDialog dialog = new ExportSelectionDialog(root,context,stepRegistry);
			dialog.pack();
			dialog.setVisible(true);   // Returns when dialog is closed
		}
	}

	public void projectUpdated() {

		try {
				structureCompilerV2.syncDatabase(deletedResourceList, addedResourceMap, changedResourceMap);

				changedResourceMap.clear();
				addedResourceMap.clear();
				deletedResourceList.clear();

		} catch (Exception ex) {
			log.errorf("%s: Error Updating Project (Project updated): %s", CLSS, ex.getLocalizedMessage());

			ExceptionDialogRunner dlg = new ExceptionDialogRunner();
			dlg.setMsg(ex.getLocalizedMessage());
			SwingUtilities.invokeLater(dlg);
		}
	}

	@Override
	public void resourcesCreated(String arg0, List<CreateResourceOperation> arg1) {
		for(CreateResourceOperation op : arg1) {
			// Store the resource into a map that will be acted on when the project is Saved 
					if( op.getResource().getResourceType().getTypeId().equals(ChartStructureCompiler.CHART_RESOURCE_TYPE) ) {
						String chartPath = op.getResource().getFolderPath();
						log.infof("SFC chart: %s, id: %d, has been modified (modification type: %s, resource type: %s)", chartPath, op.getResourceId().hashCode(), op.getOperationType().toString(), op.getResource().getResourceType());
					
						// This is called as soon as a chart is created.  Literally, as soon as user presses create chart in the project resource tree.
						// The Updated case will be called when the Save button is pressed or when the new cart is renamed

						// CJL  At this point we should check the resource IDs and see if it is a copy, in which case the UUIDs should be updated
						log.tracef("...inserting it into the addedResourceMap!");
						addedResourceMap.put((long)op.getResourceId().hashCode(), op.getResource());
					}
		}
		
		projectUpdated();
	}

	@Override
	public void resourcesDeleted(String arg0, List<DeleteResourceOperation> arg1) {
		for(DeleteResourceOperation op : arg1) {
			// Store the resource into a map that will be acted on when the project is Saved 
					if( op.getResourceId().getResourceType().getTypeId().equals(ChartStructureCompiler.CHART_RESOURCE_TYPE) ) {
						String chartPath = op.getResourceId().getFolderPath();
						log.infof("SFC chart: %s, id: %d, has been modified (modification type: %s, resource type: %s)", chartPath, op.getResourceId().hashCode(), op.getOperationType().toString(), op.getResourceId().getResourceType());


						log.tracef("...inserting it into the deletedResourceMap!");
						deletedResourceList.add(String.valueOf(op.getResourceId().hashCode()));
					}
		}

		projectUpdated();
	}

	@Override
	public void resourcesModified(String arg0, List<ModifyResourceOperation> arg1) {
		for(ModifyResourceOperation op : arg1) {
		// Store the resource into a map that will be acted on when the project is Saved 
				if( op.getResource().getResourceType().getTypeId().equals(ChartStructureCompiler.CHART_RESOURCE_TYPE) ) {
					String chartPath = op.getResource().getFolderPath();
					log.infof("SFC chart: %s, id: %d, has been modified (modification type: %s, resource type: %s)", chartPath, op.getResourceId().hashCode(), op.getOperationType().toString(), op.getResource().getResourceType());

						// This is the updated case - when a chart is renamed this is called for the new and the old name.
						log.tracef("...inserting it into the changedResourceMap!");
						changedResourceMap.put((long)op.getResource().getResourceId().hashCode(), op.getResource());
				}
		}

		projectUpdated();
	}
}
