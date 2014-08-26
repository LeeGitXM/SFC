package com.ils.sfc.common;


/** An interface for handling all IO from ILS SFC steps. */
public interface IlsSfcIOIF {
	/** The key to use to find an instance in the chart scope. */
	static final String SCOPE_KEY = "IlsSfcIO"; 

	/** The possible statuses for a queued message */
	public enum MessageStatus {
		Info, Warning, Error
	};
	
	/** Place a message in the given queue. */
	void enqueueMessage(String queueName, String message, MessageStatus status);
	
	/** Clear all messages from the given queue */
	void clearMessageQueue(String queueName);

}
