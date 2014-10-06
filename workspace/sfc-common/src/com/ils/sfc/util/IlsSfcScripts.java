package com.ils.sfc.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.python.core.PyDictionary;

/** Java methods exposed as scripts in Ignition */
public class IlsSfcScripts {	
	private static Map<String,PyDictionary> repliesById = Collections.synchronizedMap(
		new HashMap<String,PyDictionary>());
	
	public static PyDictionary getResponse(String id) {
		PyDictionary reply = repliesById.get(id);
		if(reply != null) {
			repliesById.remove(id);
		}
		return reply;
	}
	
	public static void setResponse(String id, PyDictionary payload) {
		repliesById.put(id, payload);
	}
}
