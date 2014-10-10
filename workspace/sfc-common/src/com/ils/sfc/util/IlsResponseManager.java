package com.ils.sfc.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.python.core.PyDictionary;

/** Handles receiving and caching SFC-related responses from clients.
 *  This was implemented in Java because I couldn't get a persistent global in Jython */
// TODO: should responses be persisted, so they survive a gateway restart??
// TODO: if messages are multicast, multiple responses could complicate things,
// or at least inflate memory if not "claimed"
public class IlsResponseManager {	
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
