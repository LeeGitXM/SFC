package com.ils.sfc.designer.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ils.sfc.designer.TagBrowser;
import com.ils.sfc.designer.propertyEditor.ValueHolder;

/** A wrapper for an Ignition tag browser so the user can browse tags instead
 *  of manually typing in the tag path. 
 */
@SuppressWarnings("serial")
public class TagBrowserPanel extends ValueHoldingEditorPanel  {
	private final ButtonPanel buttonPanel = new ButtonPanel(true, false, false, false, false,  false);
	protected TagBrowser tagBrowser;
	private boolean initialized;
	
	public TagBrowserPanel(PanelController controller, int index) {
		super(controller, index);
		this.myIndex = index;
		buttonPanel.getAcceptButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {accept();}			
		});
	}

	@Override
	public void activate(ValueHolder valueHolder) {	
		// the tag browser's internal icons are not added until very late,
		// so to avoid errors we need to delay creating it until the last minute
		if(!initialized) {
			add(buttonPanel, BorderLayout.NORTH);
			tagBrowser = new TagBrowser(panelController.getContext());
			add(tagBrowser, BorderLayout.CENTER);
			validate();
			initialized = true;
		}
		super.activate(valueHolder);
	}

	@Override
	public Object getValue() {
		return tagBrowser.getTagPath();
	}

	@Override
	void setValue(Object value) {
		// in theory we could set the tag browser to select the current value,
		// but at the moment we do nothing...
	}
}

