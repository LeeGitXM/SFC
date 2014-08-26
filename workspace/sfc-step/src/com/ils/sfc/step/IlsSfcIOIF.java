package com.ils.sfc.step;

/** An interface for handling all IO from ILS SFC steps. */
public interface IlsSfcIOIF {
	/** The key to use to find an instance in the chart scope. */
	static final String SCOPE_KEY = "IlsSfcIO"; 

	/** Release any resources. */
	void close();

	/** Place a message in the given queue. */
	void enqueueMessage(String messageQueueId, String message);

}
