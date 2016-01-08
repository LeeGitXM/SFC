package com.ils.sfc.gateway;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.python.core.PyDictionary;

/** Keeps track of client requests so we can match up replies with requests. */
public class IlsRequestResponseManager {
	private Map<String,PyDictionary> repliesById = Collections.synchronizedMap(
			new HashMap<String,PyDictionary>());
	private Map<String,String> stepIdsByRequestId = Collections.synchronizedMap(
			new HashMap<String,String>());
	
	/**
	 * Provide a way to clear requests that may have been left over from 
	 * a prior execution.
	 */
	public synchronized void clear() {
		stepIdsByRequestId.clear();
		repliesById.clear();
		
	}
	public synchronized PyDictionary getResponse(String id) {
		PyDictionary reply = repliesById.get(id);
		if(reply != null) {
			repliesById.remove(id);
		}
		return reply;
	}
	
	public synchronized void setResponse(String id, PyDictionary payload) {
		repliesById.put(id, payload);
		stepIdsByRequestId.remove(id);
	}
	
	public synchronized void addRequestId(String requestId, String stepId) {
		stepIdsByRequestId.put(requestId, stepId);
	}

	public synchronized Map<String, String> getStepIdsByRequestId() {
		return new HashMap<String, String>(stepIdsByRequestId);
	}

}
