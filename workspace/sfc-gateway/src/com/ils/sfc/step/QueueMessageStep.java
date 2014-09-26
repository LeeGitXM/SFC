package com.ils.sfc.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ils.sfc.common.QueueMessageStepProperties;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.ils.sfc.util.PythonCall;

public class QueueMessageStep extends IlsAbstractChartStep implements QueueMessageStepProperties {
	private static final Logger logger = LoggerFactory.getLogger(QueueMessageStep.class);

	public QueueMessageStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		logger.trace("QueueMessageStep activateStep()");
		try {
			String queue = (String)getObjectFromScopeTree(MESSAGE_QUEUE_KEY);
			String status = getStringProperty(STATUS_PROPERTY);
			String message = getStringProperty(MESSAGE_PROPERTY);
			PythonCall.QUEUE_INSERT.exec(queue, status, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deactivateStep() {
		logger.trace("QueueMessageStep deactivateStep()");
	}

}
