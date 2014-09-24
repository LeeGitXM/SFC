package com.ils.sfc.designer;

import javax.swing.JTextField;

import com.ils.sfc.common.TestStepProperties;
import com.inductiveautomation.ignition.client.util.gui.HeaderLabel;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.designer.api.AbstractStepEditor;
import com.inductiveautomation.sfc.designer.api.ElementEditor;
import com.inductiveautomation.sfc.designer.api.StepConfigFactory;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;
import com.inductiveautomation.sfc.uimodel.ChartUIModel;

import net.miginfocom.swing.MigLayout;

public class TestStepEditor extends AbstractStepEditor implements
		TestStepProperties {

	private static final long serialVersionUID = 1L;

	private final DesignerContext context;

	private JTextField textField;

	public TestStepEditor(DesignerContext context, ChartUIModel chartModel) {
		super(new MigLayout(), chartModel);
		this.context = context;

		textField = new JTextField();

		add(HeaderLabel.forKey("words.name"), "wrap");
		add(name, "gapleft 2px");
		add(new HeaderLabel("Here is a property"), "wrap");
		add(textField, "gapleft 2px");
	}

	@Override
	public void setElement(ChartUIElement element) {
		super.setElement(element);

		textField.setText(element.getOrDefault(MESSAGE_PROPERTY));
	}

	@Override
	public void commitEdit() {
		super.commitEdit();
		
		element.set(MESSAGE_PROPERTY, textField.getText());
	}

	public static class DesignerStepEditorFactory implements StepConfigFactory {

		private final DesignerContext context;
		TestStepEditor editor;

		public DesignerStepEditorFactory(DesignerContext context) {
			this.context = context;
		}

		@Override
		public ElementEditor createConfigUI(ChartUIModel model,
				ChartUIElement element) {
			if (editor == null || editor.model != model) {
				editor = new TestStepEditor(context, model);
			}
			editor.setElement(element);
			return editor;
		}

	}
}
