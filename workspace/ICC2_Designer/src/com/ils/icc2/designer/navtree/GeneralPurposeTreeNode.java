/**
 *   (c) 2014  ILS Automation. All rights reserved.
 *  
 *  Based on sample code provided by Inductive Automation.
 */
package com.ils.icc2.designer.navtree;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ils.icc2.common.ApplicationRequestManager;
import com.ils.icc2.common.ICC2Properties;
import com.ils.icc2.common.UUIDResetHandler;
import com.ils.icc2.common.serializable.SerializableDiagram;
import com.ils.icc2.common.serializable.SerializableResourceDescriptor;
import com.ils.icc2.designer.ICC2DesignerHook;
import com.ils.icc2.designer.workspace.DiagramWorkspace;
import com.inductiveautomation.ignition.client.util.action.BaseAction;
import com.inductiveautomation.ignition.client.util.gui.ErrorUtil;
import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.model.ApplicationScope;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.UndoManager;
import com.inductiveautomation.ignition.designer.gui.IconUtil;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.navtree.model.AbstractNavTreeNode;
import com.inductiveautomation.ignition.designer.navtree.model.AbstractResourceNavTreeNode;
import com.inductiveautomation.ignition.designer.navtree.model.FolderNode;
import com.inductiveautomation.ignition.designer.navtree.model.ResourceDeleteAction;
/**
 * A folder in the designer scope to support the diagnostics toolkit diagram
 * layout. In addition to standard folders, folders can be of type "Application" or
 * "Family". These hold properties special to the Diagnostics Toolkit.  Menu options 
 * vary depending on folder type. Labels are likewise dependent.
 * 
 * Leaf nodes are of type DiagramNode.
 */
public class GeneralPurposeTreeNode extends FolderNode {
	private static final String TAG = "GeneralPurposeTreeNode";
	private static final String PREFIX = ICC2Properties.BUNDLE_PREFIX;  // Required for some defaults
	private final static String POPUP_TITLE = "IMPORT";
	private final LoggerEx log = LogUtil.getLogger(getClass().getPackage().getName());
	public StartAction startAction = new StartAction();
	public StopAction stopAction = new StopAction();
	private final DiagramWorkspace workspace; 
	private Icon expandedIcon = super.getExpandedIcon();
	
	/** 
	 * Create a new folder node representing the root folder
	 * @param ctx the designer context
	 */
	public GeneralPurposeTreeNode(DesignerContext ctx) {
		super(ctx, ICC2Properties.MODULE_ID, ApplicationScope.GATEWAY,ICC2Properties.ROOT_FOLDER_UUID);
		this.setName(ICC2Properties.ROOT_FOLDER_NAME);
		workspace = ((ICC2DesignerHook)ctx.getModule(ICC2Properties.MODULE_ID)).getWorkspace();
		setText(BundleUtil.get().getString(PREFIX+".RootFolderName"));
		setIcon(IconUtil.getIcon("folder_closed"));
	}

	/**
	 * This version of the constructor is used for all except the root. Create
	 * either a simple folder, an application or family container or a diagram holder.
	 * This all depends on the resource type.
	 * 
	 * @param context the designer context
	 * @param resource the project resource
	 * @param self UUID of the node itself
	 */
	public GeneralPurposeTreeNode(DesignerContext context,ProjectResource resource,UUID self) {
		super(context,resource.getModuleId(),resource.getApplicationScope(),self);
		this.resourceId = resource.getResourceId();
		setName(resource.getName());      // Also sets text for tree
		
		workspace = ((ICC2DesignerHook)context.getModule(ICC2Properties.MODULE_ID)).getWorkspace();
		
		ImageIcon icon = IconUtil.getIcon("folder_closed");    // Base icon.
		
		if(resource.getResourceType().equalsIgnoreCase(ICC2Properties.DIAGRAM_RESOURCE_TYPE)) {
			icon = IconUtil.getIcon("tag_tree");
		}
		setIcon(icon);
	}

	private boolean isRootFolder() {
		return getFolderId().equals(ICC2Properties.ROOT_FOLDER_UUID);
	}
	
	@Override
	public Icon getExpandedIcon() {
		return expandedIcon;
	}

	/**
	 * Create a child node because we've discovered a resource that matches this instance as a parent
	 * based on its content matching the our UUID.
	 */
	@Override
	protected AbstractNavTreeNode createChildNode(ProjectResource res) {
		log.infof("%s.createChildNode type:%s, level=%d", TAG,res.getResourceType(),getDepth());
		if (    ProjectResource.FOLDER_RESOURCE_TYPE.equals(res.getResourceType()))       {
			GeneralPurposeTreeNode node = new GeneralPurposeTreeNode(context, res, res.getDataAsUUID());
			log.infof("%s.createChildNode: (%s) %s->%s",TAG,res.getResourceType(),this.getName(),node.getName());
			return node;
		}
		else if (ICC2Properties.DIAGRAM_RESOURCE_TYPE.equals(res.getResourceType())) {
			DiagramNode node = new DiagramNode(context,res,workspace);
			log.infof("%s.createChildPanel: %s->%s",TAG,this.getName(),node.getName());
			return node;
		} 
		else {
			log.warnf("%s: Attempted to create a child of type %s (ignored)",TAG,res.getResourceType());
			throw new IllegalArgumentException();
		}
	}
	
	@Override
	public String getWorkspaceName() {
		return DiagramWorkspace.key;
	}
	
	@Override
	public boolean isEditActionHandler() {
		return isRootFolder();
	}
	/**
	 * Define the menu used for popups. This appears to be called only once for each node.
	 */
	@Override
	protected void initPopupMenu(JPopupMenu menu, TreePath[] paths,List<AbstractNavTreeNode> selection, int modifiers) {
		setupEditActions(paths, selection);
		
		if (isRootFolder()) { 
			ApplicationRequestManager handler = ((ICC2DesignerHook)context.getModule(ICC2Properties.MODULE_ID)).getPropertiesRequestHandler();
			
			DebugAction debugAction = new DebugAction();
			if( handler.isControllerRunning() ) {
				startAction.setEnabled(false);
			}
			else {
				stopAction.setEnabled(false);
			}
			menu.add(startAction);
			menu.add(stopAction);
			menu.addSeparator();
			menu.add(debugAction);
		}
		

		DiagramAction diagramAction = new DiagramAction();
		menu.add(diagramAction);
		ImportDiagramAction importAction = new ImportDiagramAction();

		menu.add(importAction);

		NewFolderAction newFolderAction = new NewFolderAction(context,ICC2Properties.MODULE_ID,ApplicationScope.DESIGNER,getFolderId(),this);
		menu.add(newFolderAction);
		menu.addSeparator();
		addEditActions(menu);	

		
	}
	
	/**
	 * Exclude cut and paste which are currently not supported.
	 */
	@Override
	protected void addEditActions(JPopupMenu menu)
    {
        menu.add(renameAction);
        menu.add(deleteAction);
    }
	
	private boolean siblings(List<AbstractNavTreeNode> nodes) {
		if (nodes == null || nodes.size() < 1) {
			return false;
		}
		int depth = nodes.get(0).getDepth();
		for (AbstractNavTreeNode node : nodes) {
			if (node.getDepth() != depth) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canDelete(List<AbstractNavTreeNode> selectedChildren) {
		return isEditActionHandler() && siblings(selectedChildren);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doDelete(List<? extends AbstractNavTreeNode> children,
			DeleteReason reason) {
		for (AbstractNavTreeNode node : children) {
			if (node instanceof DiagramNode) {
				((DiagramNode) node).closeAndCommit();
			}
		}

		ResourceDeleteAction delete = new ResourceDeleteAction(context,
				(List<AbstractResourceNavTreeNode>) children,
				reason.getActionWordKey(), (getDepth()==1? (PREFIX+".ApplicationNoun"):(PREFIX+".FamilyNoun")));
		if (delete.execute()) {
			UndoManager.getInstance().add(delete, GeneralPurposeTreeNode.class);
		}
	}

	@Override
	public void onSelected() {
		UndoManager.getInstance()
				.setSelectedContext(GeneralPurposeTreeNode.class);
	}
	
	
	/**
	 *  Serialize a diagram into JSON. 
	 * @param diagram to be serialized
	 */ 
	private String serializeDiagram(SerializableDiagram diagram) {
		String json = "";
		ObjectMapper mapper = new ObjectMapper();
		log.infof("%s: serializeDiagram creating json ... %s",TAG,(mapper.canSerialize(SerializableDiagram.class)?"true":"false"));
		try{ 
		    json = mapper.writeValueAsString(diagram);
		}
		catch(JsonProcessingException jpe) {
			log.warnf("%s: Unable to serialize diagram (%s)",TAG,jpe.getMessage());
		}
		log.infof("%s: serializeDiagram created json ... %s",TAG,json);
		return json;
	}
	
	
	// From the root node, recursively log the contents of the tree
	private class DebugAction extends BaseAction {
		private static final long serialVersionUID = 1L;
		public DebugAction()  {
			super(PREFIX+".Debug",IconUtil.getIcon("bug_yellow"));
		}

		public void actionPerformed(ActionEvent e) {
			log.info("============================ Resources (Designer) =========================");
			listProjectResources();
			log.info("============================ Resources (Gateway) ==========================");
			listControllerResources();
			log.info("===========================================================================");
		}
		/**
		 * Search the project for all resources. This is for debugging.
		 * We filter out those that are global (have no module) as these
		 * are system things that we don't care about for the moment.
		 */
		private void listProjectResources() {
			List <ProjectResource> resources = context.getProject().getResources();
			for( ProjectResource res : resources ) {
				if( res.getModuleId()==null || res.getModuleId().length()==0) continue;
				log.info("Res: "+res.getResourceId()+" "+res.getResourceType()+" "+res.getModuleId()+" ("+res.getName()+
						":"+res.getParentUuid()+")");
			}
		}
		
		/**
		 * Query the block controller in the Gateway. The resources that it knows
		 * about may, or may not, coincide with those in the Designer. 
		 */
		private void listControllerResources() {
			try {
				ApplicationRequestManager handler = ((ICC2DesignerHook)context.getModule(ICC2Properties.MODULE_ID)).getPropertiesRequestHandler();
				List <SerializableResourceDescriptor> descriptors = handler.queryControllerResources();
				for( SerializableResourceDescriptor descriptor : descriptors ) {
					log.info("Res: "+descriptor.getProjectId()+":"+descriptor.getResourceId()+" "+
							         descriptor.getType()+" ("+descriptor.getName()+")");
				}
			} 
			catch (Exception ex) {
				log.warnf("%s. startAction: ERROR: %s",TAG,ex.getMessage(),ex);
				ErrorUtil.showError(ex);
			}
			
		}
	}
	
	private void importDiagram(UUID parent,SerializableDiagram sd) {
		ObjectMapper mapper = new ObjectMapper();
		try{
			long newId = context.newResourceId();
			String json = mapper.writeValueAsString(sd);
			if(log.isTraceEnabled() ) log.trace(json);
			ProjectResource resource = new ProjectResource(newId,
					ICC2Properties.MODULE_ID, ICC2Properties.DIAGRAM_RESOURCE_TYPE,
					sd.getName(), ApplicationScope.GATEWAY, json.getBytes());
			resource.setParentUuid(parent);
			context.updateResource(resource);
			selectChild(newId);
		} 
		catch (Exception ex) {
			ErrorUtil.showError(String.format("ApplicationImportAction: Unhandled Exception (%s)",ex.getMessage()),POPUP_TITLE,ex,true);
		}
	}
	
	

	// Create a new diagram
    private class DiagramAction extends BaseAction {
    	private static final long serialVersionUID = 1L;
	    public DiagramAction()  {
	    	super(PREFIX+".NewDiagram",IconUtil.getIcon("folder_new"));  // preferences
	    }
	    
		public void actionPerformed(ActionEvent e) {
			try {
				final long newId = context.newResourceId();
				String newName = BundleUtil.get().getString(PREFIX+".NewDiagram.Default.Name");
				if( newName==null) newName = "New Diag";  // Missing string resource
				SerializableDiagram diagram = new SerializableDiagram();
				diagram.setName(newName);
				diagram.setResourceId(newId);
				diagram.setId(UUID.randomUUID());
				
				log.infof("%s: new diagram action ...",TAG);

				String json = serializeDiagram(diagram);
			
				log.debugf("%s: DiagramAction. json=%s",TAG,json);
				byte[] bytes = json.getBytes();
				log.debugf("%s: DiagramAction. create new %s resource %d (%d bytes)",TAG,ICC2Properties.DIAGRAM_RESOURCE_TYPE,
						newId,bytes.length);
				ProjectResource resource = new ProjectResource(newId,
						ICC2Properties.MODULE_ID, ICC2Properties.DIAGRAM_RESOURCE_TYPE,
						newName, ApplicationScope.GATEWAY, bytes);
				resource.setParentUuid(getFolderId());
				context.updateResource(resource);
				selectChild(newId);
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						workspace.open(newId);
					}
				});
				
			} 
			catch (Exception err) {
				ErrorUtil.showError(err);
			}
		}
	}
    
    
    
    private class ImportDiagramAction extends BaseAction {
    	private static final long serialVersionUID = 1L;
    	private final static String POPUP_TITLE = "Import Diagram";
	    public ImportDiagramAction()  {
	    	super(PREFIX+".ImportDiagram",IconUtil.getIcon("import1"));  // preferences
	    }
	    
		public void actionPerformed(ActionEvent e) {
			try {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						long newId;

						try {
							newId = context.newResourceId();
							String title = BundleUtil.get().getString(PREFIX+".Import.Application.DialogTitle");
							String label = BundleUtil.get().getString(PREFIX+".Import.Application.NameLabel");
							ImportDialog dialog = new ImportDialog(label,title);
							dialog.pack();
							dialog.setVisible(true);   // Returns when dialog is closed
							File input = dialog.getFilePath();
							
							if( input!=null ) {
								if( input.exists() && input.canRead()) {
									try {
										// Note: Requires Java 1.7
										byte[] bytes = Files.readAllBytes(input.toPath());
										// It would be nice to simply convert to a resource.
										// Unfortunately we have to replace all UUIDs with new ones
										ObjectMapper mapper = new ObjectMapper();
										SerializableDiagram sd = mapper.readValue(new String(bytes), SerializableDiagram.class);
										if( sd!=null ) {
											log.infof("%s:ImportDiagramAction imported diagram %s", TAG,sd.getName());
											UUIDResetHandler handler = new UUIDResetHandler(sd);
											handler.convertUUIDs();
											String json = mapper.writeValueAsString(sd);
											if(log.isInfoEnabled() ) log.info(json);
											ProjectResource resource = new ProjectResource(newId,
													ICC2Properties.MODULE_ID, ICC2Properties.DIAGRAM_RESOURCE_TYPE,
													sd.getName(), ApplicationScope.GATEWAY, json.getBytes());
											resource.setParentUuid(getFolderId());
											context.updateResource(resource);
											selectChild(newId);
										}
										else {
											ErrorUtil.showWarning(String.format("Failed to deserialize file (%s)",input.getAbsolutePath()),POPUP_TITLE);
										}
									}
									catch( FileNotFoundException fnfe) {
										// Should never happen, we just picked this off a chooser
										ErrorUtil.showWarning(String.format("File %s not found",input.getAbsolutePath()),POPUP_TITLE); 
									}
									catch( IOException ioe) {
										ErrorUtil.showWarning(String.format("IOException (%s)",ioe.getLocalizedMessage()),POPUP_TITLE); 
									}
									catch(Exception ex) {
										ErrorUtil.showError(String.format("Deserialization exception (%s)",ex.getMessage()),POPUP_TITLE,ex,true);
									}

								}
								else {
									ErrorUtil.showWarning(String.format("Selected file does not exist or is not readable: %s",input.getAbsolutePath()),POPUP_TITLE);
								}
							}  // Cancel
						} 
						catch (Exception ex) {
							ErrorUtil.showError(String.format("Unhandled Exception (%s)",ex.getMessage()),POPUP_TITLE,ex,true);
						}
						// No need to inform of success, we'll see the new diagram
					}
				});
			} 
			catch (Exception err) {
				ErrorUtil.showError(err);
			}
		}
	}
    // Start refers to a global startup of the Execution controller in the Gateway
    private class StartAction extends BaseAction {
    	private static final long serialVersionUID = 1L;
	    public StartAction()  {
	    	super(PREFIX+".StartExecution",IconUtil.getIcon("disk_play"));  // preferences
	    }
	    
		public void actionPerformed(ActionEvent e) {
			try {
				ApplicationRequestManager handler = ((ICC2DesignerHook)context.getModule(ICC2Properties.MODULE_ID)).getPropertiesRequestHandler();
				handler.startController();
				this.setEnabled(false);
				stopAction.setEnabled(true);
			} 
			catch (Exception ex) {
				log.warnf("%s: startAction: ERROR: %s",TAG,ex.getMessage(),ex);
				ErrorUtil.showError(ex);
			}
		}
	}
    private class StopAction extends BaseAction {
    	private static final long serialVersionUID = 1L;
	    public StopAction()  {
	    	super(PREFIX+".StopExecution",IconUtil.getIcon("disk_forbidden"));  // preferences
	    }
	    
		public void actionPerformed(ActionEvent e) {
			try {
				ApplicationRequestManager handler = ((ICC2DesignerHook)context.getModule(ICC2Properties.MODULE_ID)).getPropertiesRequestHandler();
				handler.stopController();
				this.setEnabled(false);
				startAction.setEnabled(true);
			}
			catch(Exception ex) {
				log.warnf("%s: stopAction: ERROR: %s",TAG,ex.getMessage(),ex);
				ErrorUtil.showError(ex);
			}
		}
	}
    
}
