package com.ils.sfc.designer.panels;

import com.ils.sfc.designer.propertyEditor.ValueHolder;

/** An editor pane that supplies a single value to another panel. */
@SuppressWarnings("serial")
public abstract class ValueHoldingEditorPanel extends EditorPanel {
	protected ValueHolder valueHolder;
	
	protected ValueHoldingEditorPanel(PanelController controller, int index) {
		super(controller, index);
	}

	abstract public Object getValue();
	
	abstract public void setValue(Object value);
	
	@Override
	/** This implementation blocks subclasses from using the wrong form of activate() */
	public void activate(int returnIndex) {
		throw new IllegalArgumentException("use activate(ValueHolder)");
	}
	
	/** subclasses should extend this as needed. */
	public void activate(ValueHolder valueHolder) {
		super.activate(valueHolder.getIndex());
		this.valueHolder = valueHolder;		
	}

	@Override
	public void cancel() {
		//valueHolder.setValue(getValue());
		valueHolder = null;
		super.cancel();
	}
	
	@Override
	public void accept() {
		valueHolder.setValue(getValue());
		valueHolder = null;
		super.accept();
	}
}
