package com.ils.sfc.designer.stepEditor;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ils.sfc.designer.IlsSfcDesignerHook;
import com.inductiveautomation.ignition.common.config.Property;
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
	private static final Logger logger = LoggerFactory.getLogger(IlsStepEditor.class);
	public final static String ROOT_HELP_PATH = "http://localhost:8088/main/system/moduledocs/com.ils.sfc/SFCUsersGuide.html#";
	private static final ResourceBundle rb = ResourceBundle.getBundle("com.ils.sfc.designer.designer");  // designer.properties
	private StepEditorController stepEditorController;

	protected IlsStepEditor(ChartUIModel chartModel, DesignerContext context) {
		super(new BorderLayout(), chartModel);
		// TODO: this is cryptic--can we encapsulate it in a better place?
		long resourceId = IlsSfcDesignerHook.getSfcWorkspace().getSelectedContainer().getResourceId();
		String chartPath = context.getGlobalProject().getProject().getFolderPath(resourceId);
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
		DesignerContext context;
		IlsStepEditor editor;

		public Factory(DesignerContext context) {
			this.context = context;
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
		 * Display context-sensitive help in a browser window 
		 */
		public class HelpAction extends AbstractAction {
			private static final long serialVersionUID = 1L;
			private final ChartUIElement element;
			
			public HelpAction(ChartUIElement e)  {	
				super(rb.getString("Editor.Menu.Help"));
				element = e;
			}
			
			// Display a browser pointing to the help text for the block
			public void actionPerformed(final ActionEvent e) {
				Desktop desktop=Desktop.getDesktop();
				String address = ROOT_HELP_PATH+getFactoryId(element);
				logger.info(String.format("%s.HelpAction: Address is: %s",CLSS,address)); 
				try {

					URI url = new URI(address);
					desktop.browse(url);
				}
				catch(URISyntaxException use) {
					logger.info(String.format("%s.HelpAction: Illegal URI: %s (%s)",CLSS,address,use.getLocalizedMessage())); 
				}
				catch(IOException ioe) {
					logger.info(String.format("%s.HelpAction: Exception posting browser (%s)",CLSS,ioe.getLocalizedMessage())); 
				}
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

