package com.ils.sfc.step;

import com.ils.sfc.common.MessageQueueStepProperties.MessageStatus;

/** An interface for handling all IO from ILS SFC steps. */
public interface IlsSfcIOIF {
	/** The key to use to find an instance in the chart scope. */
	static final String SCOPE_KEY = "IlsSfcIO"; 
	
	/** Place a message in the given queue. */
	void enqueueMessage(String queueName, String message, MessageStatus status);
	
	/** Clear all messages from the given queue */
	void clearMessageQueue(String queueName);

}
