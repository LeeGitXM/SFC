package com.ils.sfc.gateway;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.python.core.PyDictionary;

/** Keeps track of client requests so we can match up replies with requests. */
public class IlsRequestResponseManager {
	private Map<String,PyDictionary> repliesById = Collections.synchronizedMap(
			new HashMap<String,PyDictionary>());

	public PyDictionary getResponse(String id) {
		PyDictionary reply = repliesById.get(id);
		if(reply != null) {
			repliesById.remove(id);
		}
		return reply;
	}
	
	public void setResponse(String id, PyDictionary payload) {
		repliesById.put(id, payload);
	}
}
