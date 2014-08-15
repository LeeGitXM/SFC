package com.ils.sfc;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.inductiveautomation.ignition.client.util.gui.HeaderLabel;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.designer.api.AbstractStepEditor;
import com.inductiveautomation.sfc.designer.api.ElementEditor;
import com.inductiveautomation.sfc.designer.api.StepConfigFactory;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;
import com.inductiveautomation.sfc.uimodel.ChartUIModel;

import net.miginfocom.swing.MigLayout;

public class MessageQueueStepEditor extends AbstractStepEditor implements
		MessageQueueStepProperties {

	private static final long serialVersionUID = 1L;

	private final DesignerContext context;

	private JSpinner spinner;

	public MessageQueueStepEditor(DesignerContext context, ChartUIModel chartModel) {
		super(new MigLayout(), chartModel);
		this.context = context;

		spinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));

		add(HeaderLabel.forKey("words.name"), "wrap");
		add(name, "gapleft 2px");
		add(new HeaderLabel("Here is a property"), "wrap");
		add(spinner, "gapleft 2px");

	}

	@Override
	public void setElement(ChartUIElement element) {
		super.setElement(element);

		spinner.setValue(element.getOrDefault(EXAMPLE_PROPERTY));
	}

	@Override
	public void commitEdit() {
		super.commitEdit();
		
		element.set(EXAMPLE_PROPERTY, ((Number)spinner.getValue()).intValue());
	}

	public static class DesignerStepEditorFactory implements StepConfigFactory {

		private final DesignerContext context;
		MessageQueueStepEditor editor;

		public DesignerStepEditorFactory(DesignerContext context) {
			this.context = context;
		}

		@Override
		public ElementEditor createConfigUI(ChartUIModel model,
				ChartUIElement element) {
			if (editor == null || editor.model != model) {
				editor = new MessageQueueStepEditor(context, model);
			}
			editor.setElement(element);
			return editor;
		}

	}
}
