package com.ils.sfc.step;

import java.util.UUID;

import junit.framework.TestCase;

import com.ils.sfc.common.TestStepProperties;
import com.ils.sfc.step.IlsAbstractChartStep;
import com.ils.sfc.step.TestStep;
import com.ils.sfc.util.IlsSfcIOIF;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.sfc.definitions.StepDefinition;

/** Unit tests for the Queue Steps */
public class QueueStepTestCase extends TestCase {
	private BasicPropertySet stepProperties = new BasicPropertySet();
	private StepDefinition definition = new StepDefinition(new UUID(0,0), stepProperties);
	private StubChartContext chartContext = new StubChartContext();
	private StubIlsSfcIO stubIO = new StubIlsSfcIO();
	
	public void setUp() {
		chartContext.getChartScope().setVariable(IlsSfcIOIF.SCOPE_KEY, stubIO);
	}
/*	
	// Test the onStart action--the message-queueing function. 
	public void testMessageQueueStepOnStart() {
		String message = "Hi, Rob";
		String queueId = "msqQueue";
		// define the message queue id for this step in the chart scope:
		chartContext.getChartScope().setVariable(IlsAbstractChartStep.MESSAGE_QUEUE_KEY, queueId);
		// set the step's message:
		stepProperties.set(MessageQueueStepProperties.MESSAGE_PROPERTY, message);
		// execute the step's function:
		TestStep messageQueueStep = new TestStep(chartContext, definition);
		messageQueueStep.onStart();
		// verify that the message was put in the proper queue:
		assertEquals(1, stubIO.getMessageQueue(queueId).size());
		assertEquals(message, stubIO.getMessageQueue(queueId).get(0));
	}

	// Test the onStart action--the message-queueing function. 
	public void testClearQueueStepOnStart() {
		String message = "Hi, Rob";
		String queueId = "msqQueue";
		// define the message queue id for this step in the chart scope:
		chartContext.getChartScope().setVariable(IlsAbstractChartStep.MESSAGE_QUEUE_KEY, queueId);
		stubIO.getMessageQueue(queueId).add(message);
		ClearQueueStep clearQueueStep = new ClearQueueStep(chartContext, definition);
		clearQueueStep.onStart();
		// verify that the queue is 0 size:
		assertEquals(0, stubIO.getMessageQueue(queueId).size());
	}

	// Test the onStart action--the message-queueing function. 
	public void testSetQueueStepOnStart() {
		String queueId = "msqQueue";
		// define the message queue id for this step in the chart scope:
		chartContext.getChartScope().setVariable(IlsAbstractChartStep.MESSAGE_QUEUE_KEY, queueId);
		SetQueueStep setQueueStep = new SetQueueStep(chartContext, definition);
		setQueueStep.setQueue(queueId);
		setQueueStep.onStart();
		// verify that the scope contains the queue
		assertEquals(queueId, setQueueStep.getCurrentMessageQueue());
	}
	*/
}
