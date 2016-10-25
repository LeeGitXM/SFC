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
	private final ButtonPanel buttonPanel = new ButtonPanel(true, false, false, false, false, true, false, false);
	protected TagBrowser tagBrowser;
	private boolean initialized;
	private String currentValue;
	
	public TagBrowserPanel(PanelController controller, int index) {
		super(controller, index);
		this.myIndex = index;
		buttonPanel.getAcceptButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {accept();}			
		});
		buttonPanel.getCancelButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {cancel();}			
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
		if(currentValue != null) {
			tagBrowser.setSelectedTagPath(currentValue);
		}
		currentValue = null;
		
		super.activate(valueHolder);
	}

	@Override
	public Object getValue() {
		String tagPath = tagBrowser.getTagPath();
		// strip off provider:
		if(tagPath != null && tagPath.startsWith("[")) {
			int rbIndex = tagPath.indexOf("]");
			if(rbIndex != -1) {
				tagPath = tagPath.substring(rbIndex+1, tagPath.length());
			}
		}
		return tagPath;
	}

	@Override
	public void setValue(Object value) {
		currentValue = (String) value;
	}
}

