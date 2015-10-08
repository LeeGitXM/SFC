package com.ils.sfc.designer.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ils.sfc.designer.BasicUnitChooserPanel;
import com.ils.sfc.designer.propertyEditor.ValueHolder;

/** An editor for creating a Recipe Data object. */
@SuppressWarnings("serial")
public class UnitChooserPanel extends ValueHoldingEditorPanel {
	private BasicUnitChooserPanel basicPanel = new BasicUnitChooserPanel();
	private ButtonPanel buttonPanel = new ButtonPanel(true, false, false, false, false, false);
	private boolean initialized;
	
	public UnitChooserPanel(PanelController controller, int index) {
		super(controller, index);
		initUI();
		buttonPanel.getAcceptButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { accept(); }		
		});
	}
	
	private void initUI() {
		setLayout(new BorderLayout());
		add(buttonPanel, BorderLayout.NORTH);
		add(basicPanel, BorderLayout.CENTER);

	}

	@Override
	public void activate(ValueHolder valueHolder) {
		if(!initialized) {
			basicPanel.initTypes();
			initialized = true;
		}
		super.activate(valueHolder);
	}

	@Override
	public void setValue(Object unitName) {
		basicPanel.setUnits((String)unitName);
	}

	@Override
	public Object getValue() {
		return basicPanel.getSelectedUnits();
	}
	
}
