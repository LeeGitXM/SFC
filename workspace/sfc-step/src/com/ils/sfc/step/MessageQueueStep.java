package com.ils.sfc.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ils.sfc.common.MessageQueueStepProperties;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.ils.sfc.util.IlsSfcIOIF;

public class MessageQueueStep extends IlsAbstractChartStep implements MessageQueueStepProperties {
	private static final Logger logger = LoggerFactory.getLogger(MessageQueueStep.class);

	public MessageQueueStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	protected void onStart() {
		logger.debug("MessageQueueStep onStart(); message: " + getMessage());
		String queueName = getCurrentMessageQueue();
		getIO().enqueueMessage(queueName, getMessage(), IlsSfcIOIF.MessageStatus.valueOf(getStatus()));
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
		return getDefinition().getProperties().getOrDefault(MessageQueueStepProperties.MESSAGE_PROPERTY);
	}
	
	public void setMessage(String message) {
		getDefinition().getProperties().set(MESSAGE_PROPERTY, message);
	}

	public String getStatus() {
		return getDefinition().getProperties().getOrDefault(MessageQueueStepProperties.STATUS_PROPERTY);
	}
	
	public void setStatus(String status) {
		getDefinition().getProperties().set(STATUS_PROPERTY, status);
	}
}
