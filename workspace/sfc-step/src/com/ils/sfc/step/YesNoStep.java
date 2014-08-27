package com.ils.sfc.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ils.sfc.common.YesNoStepProperties;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

import com.ils.sfc.common.IlsSfcIOIF;

public class YesNoStep extends IlsAbstractChartStep implements YesNoStepProperties {
	private static final Logger logger = LoggerFactory.getLogger(YesNoStep.class);

	public YesNoStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	protected void onStart() {
		logger.debug("YesNoStep onStart(); message: " + getMessage());
		String clientId = "??";
		String messageType = "??";
		getIO().sendMessage(getMessage(), messageType, clientId);
	}

	@Override
	protected void onPause() {

	}

	@Override
	protected void onResume() {

	}

	@Override
	protected void onStop() {

	}

	public String getMessage() {
		return getDefinition().getProperties().getOrDefault(YesNoStepProperties.MESSAGE_PROPERTY);
	}
	
	public void setMessage(String message) {
		getDefinition().getProperties().set(MESSAGE_PROPERTY, message);
	}

}
