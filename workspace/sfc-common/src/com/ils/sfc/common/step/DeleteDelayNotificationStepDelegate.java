package com.ils.sfc.common.step;

import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

/*
 * This is for the block on the Notification palette titled "Work Complete"
 */

public class DeleteDelayNotificationStepDelegate extends AbstractIlsStepDelegate implements
DeleteDelayNotificationStepProperties {

	protected DeleteDelayNotificationStepDelegate() {
		super(properties);
	}

	@Override
	public String getId() {
		return FACTORY_ID;
	}
	
	@Override
	public void validate(ChartUIElement element, ChartCompilationResults results) {
		// TODO: check stuff in element
		//results.addError(new CompilationError("bad stuff", element.getLocation()));
	}

}
