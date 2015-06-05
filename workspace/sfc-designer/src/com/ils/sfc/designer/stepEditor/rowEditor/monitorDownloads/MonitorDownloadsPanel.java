package com.ils.sfc.designer.stepEditor.rowEditor.monitorDownloads;

import com.ils.sfc.common.rowconfig.RowConfig;
import com.ils.sfc.designer.stepEditor.StepEditorController;
import com.ils.sfc.designer.stepEditor.rowEditor.RowEditorPanel;
import com.inductiveautomation.ignition.common.config.PropertyValue;

@SuppressWarnings("serial")
public class MonitorDownloadsPanel extends RowEditorPanel {

	public MonitorDownloadsPanel(StepEditorController controller,
			int index) {
		super(controller, index, false);
	}

	public void setConfig(PropertyValue<String> selectedPropertyValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RowConfig getConfig() {
		return null;
	}}

