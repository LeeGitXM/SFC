package com.ils.sfc.step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ils.sfc.step.IlsSfcIOIF;

/** A dummy version of IlsSfcIO for testing. */
public class StubIlsSfcIO implements IlsSfcIOIF {
	private Map<String,List<String>> messagesByQueueName = new  HashMap<String,List<String>>();
	
	/** Get the messages in the given queue (creates "queue" if not there). */
	public List<String> getMessageQueue(String queueId) {
		List<String> queue = messagesByQueueName.get(queueId);
		if(queue == null) {
			queue = new ArrayList<String>();
			messagesByQueueName.put(queueId, queue);
		}
		return queue;
	}
	
	@Override
	public void close() {
	}

	@Override
	public void enqueueMessage(String queueId, String message) {
		getMessageQueue(queueId).add(message);
	}

}
