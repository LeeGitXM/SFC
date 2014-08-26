package com.ils.sfc.step;

import java.util.UUID;

import junit.framework.TestCase;

import com.ils.sfc.common.MessageQueueStepProperties;
import com.ils.sfc.step.IlsAbstractChartStep;
import com.ils.sfc.step.IlsSfcIOIF;
import com.ils.sfc.step.MessageQueueStep;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.sfc.definitions.StepDefinition;

/** Unit tests for the MessageQueueStep */
public class MessageQueueStepTestCase extends TestCase {
	private BasicPropertySet stepProperties = new BasicPropertySet();
	private StepDefinition definition = new StepDefinition(new UUID(0,0), stepProperties);
	private StubChartContext chartContext = new StubChartContext();
	private MessageQueueStep step = new MessageQueueStep(chartContext, definition);
	private StubIlsSfcIO stubIO = new StubIlsSfcIO();
	
	public void setUp() {
		chartContext.getChartScope().setVariable(IlsSfcIOIF.SCOPE_KEY, stubIO);
	}
	
	/** Test the onStart action--the message-queueing function. */
	public void testOnStart() {
		String message = "Hi, Rob";
		String queueId = "msqQueue";
		// define the message queue id for this step in the chart scope:
		chartContext.getChartScope().setVariable(IlsAbstractChartStep.MESSAGE_QUEUE_KEY, queueId);
		// set the step's message:
		stepProperties.set(MessageQueueStepProperties.MESSAGE_PROPERTY, message);
		// execute the step's function:
		step.onStart();
		// verify that the message was put in the proper queue:
		assertEquals(1, stubIO.getMessageQueue(queueId).size());
		assertEquals(message, stubIO.getMessageQueue(queueId).get(0));
	}

}
