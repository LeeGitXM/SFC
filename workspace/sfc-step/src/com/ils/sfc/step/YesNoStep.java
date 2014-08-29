package com.ils.sfc.step;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ils.sfc.common.YesNoStepProperties;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class YesNoStep extends IlsAbstractChartStep implements YesNoStepProperties {
	private static final Logger logger = LoggerFactory.getLogger(YesNoStep.class);

	public YesNoStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	protected void onStart() {
		logger.debug("YesNoStep onStart(); message: " + getMessage());
		Map<String,Object> args = new HashMap<String,Object>();
		args.put("message", "test message");
		getIO().sendMessage("robscratch", "yesNoHandler", args, new Properties());
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
