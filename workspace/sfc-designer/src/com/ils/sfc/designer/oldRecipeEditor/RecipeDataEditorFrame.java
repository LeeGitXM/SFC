package com.ils.sfc.designer.oldRecipeEditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.inductiveautomation.ignition.client.designable.DesignableContainer;
import com.inductiveautomation.ignition.client.util.gui.SlidingPane;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.designer.designable.DesignableWorkspaceListener;
import com.inductiveautomation.ignition.designer.model.ResourceWorkspaceFrame;
import com.inductiveautomation.sfc.client.ui.StepComponent;
import com.jidesoft.docking.DockContext;

public class RecipeDataEditorFrame extends com.jidesoft.docking.DockableFrame implements ResourceWorkspaceFrame, DesignableWorkspaceListener {
	private static String KEY = "ILS Recipe Editor";
	private static String TITLE = "Recipe Data";
	private SlidingPane slidingPane = new SlidingPane();
	
	public RecipeDataEditorFrame() {
		super(KEY);
       	setInitSide(DockContext.DOCK_SIDE_WEST);
       	setInitIndex(10);
       	setTitle(TITLE);
       	setTabTitle(TITLE);
       	setSideTitle(TITLE);
       	setContentPane(slidingPane);
	}
	
	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public boolean isInitiallyVisible() {
		return true;
	}

	public void itemSelectionChanged(List<JComponent> selectedComponents) {
		slidingPane.removeAll();
		slidingPane.invalidate();

		JComponent selectedComponent = selectedComponents.size() == 1 ? selectedComponents.get(0) : null;
		Component newComponent = null;
		if(selectedComponent instanceof StepComponent) {
			StepComponent stepComponent = (StepComponent) selectedComponent;
			BasicProperty<String> idProperty = new BasicProperty<String>("id", String.class);
			String stepId = stepComponent.getElement().get(idProperty);
			RecipeDataBrowser browser = new RecipeDataBrowser(stepId, slidingPane);
			slidingPane.add(browser);
		}
		else {
			// nothing?
		}
			
		slidingPane.validate();
		slidingPane.setVisible(true);
	}
	
	public void containerClosed(DesignableContainer arg0) {}
	public void containerOpened(DesignableContainer arg0) {}
	public void containerSelected(DesignableContainer arg0) {}

}
