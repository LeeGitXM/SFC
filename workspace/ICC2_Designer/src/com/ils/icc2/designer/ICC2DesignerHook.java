/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */
package com.ils.icc2.designer;


import com.ils.icc2.common.ApplicationRequestManager;
import com.ils.icc2.common.ApplicationScriptFunctions;
import com.ils.icc2.common.ICC2Properties;
import com.ils.icc2.designer.navtree.GeneralPurposeTreeNode;
import com.ils.icc2.designer.workspace.DiagramWorkspace;
import com.inductiveautomation.ignition.client.gateway_interface.GatewayConnectionManager;
import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.model.SaveContext;
import com.jidesoft.docking.DockingManager;

public class ICC2DesignerHook extends AbstractDesignerModuleHook  {
	private static final String TAG = "ICC2DesignerHook";
	public static String BLOCK_BUNDLE_NAME   = "block";        // Properties file is block.properties
	public static String HOOK_BUNDLE_NAME   = "designer";      // Properties file is designer.properties
	public static String PREFIX = ICC2Properties.BUNDLE_PREFIX; // Properties are accessed by this prefix

	private GeneralPurposeTreeNode rootNode;
	private DesignerContext context = null;
	private final LoggerEx log;
	private DiagramWorkspace workspace = null;
	private ApplicationRequestManager propertiesRequestHandler = null;
	
	// Register separate properties files for designer things and block things
	static {
		BundleUtil.get().addBundle(ICC2Properties.BUNDLE_PREFIX,ICC2DesignerHook.class,HOOK_BUNDLE_NAME);
		BundleUtil.get().addBundle(ICC2Properties.BLOCK_PREFIX,ICC2DesignerHook.class,BLOCK_BUNDLE_NAME);
	}
	
	public ICC2DesignerHook() {
		log = LogUtil.getLogger(getClass().getPackage().getName());
	}
	
	
	@Override
	public void initializeScriptManager(ScriptManager mgr) {
		super.initializeScriptManager(mgr);
		mgr.addScriptModule(ICC2Properties.APPLICATION_SCRIPT_PACKAGE,ApplicationScriptFunctions.class);
	}
	
	@Override
	public void startup(DesignerContext ctx, LicenseState activationState) throws Exception {
		this.context = ctx;
		propertiesRequestHandler = new ApplicationRequestManager();
		
		// Setup the block and connector workspace
		workspace = new DiagramWorkspace(context);
		context.registerResourceWorkspace(workspace);
		// Setup the NavTree
		rootNode = new GeneralPurposeTreeNode(context);
		context.getProjectBrowserRoot().addChild(rootNode);
		
		// Register the listener for notifications
		GatewayConnectionManager.getInstance().addPushNotificationListener(new NotificationListener());
	}
	
	public DiagramWorkspace getWorkspace() { return workspace; }

	@Override
	public void notifyProjectSaveStart(SaveContext save) {
		workspace.saveOpenDiagrams();
	}
	
	/**
	 * Iterate over all the dockable frames. Close any that are not useful.
	 */
	public void resetPanelsForBlockAndConnector() {
		DockingManager dockManager = context.getDockingManager();
		for(String name:dockManager.getAllFrameNames()) {
			if( name.equalsIgnoreCase("OPC Browser")            ||
				name.equalsIgnoreCase("DocEditor")              ||
				name.equalsIgnoreCase("QueryBrowser")           ||
				name.equalsIgnoreCase("Fill-and-Stroke")        ||
				name.equalsIgnoreCase("Palette - Collapsible")  ||
				name.equalsIgnoreCase("Palette - Tabbed")          ) {
				dockManager.hideFrame(name);
				log.infof("%s: Hiding frame=%s",TAG,name);
			}
			else {
				log.infof("%s: Leaving frame=%s",TAG,name);
			}
		}
	}

	public ApplicationRequestManager getPropertiesRequestHandler() { return propertiesRequestHandler; }
	@Override
	public String getResourceCategoryKey(Project project,ProjectResource resource) {
		// There is only one resource category that we are exporting
		if( resource.getResourceType().equalsIgnoreCase(ICC2Properties.DIAGRAM_RESOURCE_TYPE) ) {
			return PREFIX+".Export.Diagram.Category";
		}
		else { 
			return PREFIX+".Export.Generic.Category";   // Folders
		}
		
	}
	
	@Override
	public void shutdown() {	
	}
}
