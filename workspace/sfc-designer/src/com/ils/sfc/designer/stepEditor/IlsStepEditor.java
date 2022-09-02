package com.ils.sfc.designer.stepEditor;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adbs.utils.Helpers;
import com.ils.sfc.common.IlsSfcRequestHandler;
import com.ils.sfc.designer.IlsSfcDesignerHook;
import com.inductiveautomation.factorypmi.application.script.builtin.ClientSystemUtilities;
import com.inductiveautomation.ignition.client.script.ClientNetUtilities;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.designer.api.AbstractStepEditor;
import com.inductiveautomation.sfc.designer.api.ElementEditor;
import com.inductiveautomation.sfc.designer.api.StepConfigFactory;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;
import com.inductiveautomation.sfc.uimodel.ChartUIModel;

/** A "bridge" class that allows us to plug our own SFC Step property editor into Ignition. */
@SuppressWarnings("serial")
public class IlsStepEditor  extends AbstractStepEditor {
	private static final String CLSS = "IlsStepEditor";	
	private final static String OS = System.getProperty("os.name").toLowerCase();
	
	public final static String ROOT_HELP_PATH = "http://%s:8088/main/system/moduledocs/com.ils.sfc/SFCUsersGuide_filtered.html#%s";
	public final static String USER_MANUAL_FILENAME = "/main/system/moduledocs/com.ils.sfc/SFCUsersGuide_filtered.html";
	
	private static final ResourceBundle rb = ResourceBundle.getBundle("com.ils.sfc.designer.designer");  // designer.properties
	private StepEditorController stepEditorController;
	private final IlsSfcRequestHandler requestHandler = new IlsSfcRequestHandler();
	private ClientNetUtilities netUtils;

	protected IlsStepEditor(ChartUIModel chartModel, DesignerContext context) {
		super(new BorderLayout(), chartModel);
		// TODO: this is cryptic--can we encapsulate it in a better place?
		String chartPath = IlsSfcDesignerHook.getSfcWorkspace().getSelectedContainer().getResourcePath().getFolderPath();
		stepEditorController = new StepEditorController(context, chartPath);
		add(stepEditorController.getSlidingPane());
	}

	@Override
	public void setElement(ChartUIElement element) {
		super.setElement(element);
		stepEditorController.setElement(element);
	}

	@Override
	public void commitEdit() {
		stepEditorController.commitEdit();
	}
	
	public <T> void set(Property<T> property, T value) {
		element.set(property, value);
		setElement(element);
	}

	public static class Factory implements StepConfigFactory {
		private DesignerContext context;
		private final IlsSfcRequestHandler requestHandler;
		private IlsStepEditor editor;

		public Factory(DesignerContext context) {
			this.context = context;
			this.requestHandler = new IlsSfcRequestHandler();
		}

		@Override
		public ElementEditor createConfigUI(ChartUIModel model,
				ChartUIElement element) {
			if (editor == null || editor.model != model) {
				editor = new IlsStepEditor(model, context);
			}
			editor.setElement(element);
			return editor;
		}

		@Override
		public void initPopupMenu(ChartUIModel model, ChartUIElement element, JPopupMenu popup) {
			HelpAction ha = new HelpAction(element);
			popup.add(ha);
			
		}
		
		/**
		 * Display context-sensitive help in a browser window. This is a nested class of Factory. 
		 */
		public class HelpAction extends AbstractAction {
			private final LoggerEx log = LogUtil.getLogger(getClass().getName());
			private static final long serialVersionUID = 1L;
			private final ChartUIElement element;
			//private static Helpers helper = new Helpers();
			private ClientNetUtilities netUtils = new ClientNetUtilities(context);
			
			public HelpAction(ChartUIElement e)  {	
				super(rb.getString("Editor.Menu.Help"));
				element = e;
			}
			
			// Display a browser pointing to the help text for the block
			public void actionPerformed(final ActionEvent e) {
				String gatewayHostname = ClientSystemUtilities.getGatewayAddress();
				log.tracef(String.format("Gateway Host: %s", gatewayHostname));
				
				String blockId = getFactoryId(element);
				String docName = String.format("%s.pdf", blockId.substring(blockId.lastIndexOf(".")+1, blockId.length()));
				log.tracef("Document Name: %s", docName);
				
				String address = String.format("%s%s%s", gatewayHostname, "/main/system/moduledocs/com.ils.sfc/", docName);
				log.tracef(String.format("%s.HelpAction(): Document address is: %s", CLSS, address));
				
				// This Helpers class is pretty handy, not exactly sure how this is working...
				Helpers.openURL(address);
			}
			
			private String getFactoryId(ChartUIElement element) {
				for(Property<?> prop:element.getProperties()) {
					if(prop.getName().equalsIgnoreCase("factory-id")) {
						Object val = element.getRawValueMap().get(prop);
						return val.toString();
					}
				}
				return null;
			}
		}
	}

}