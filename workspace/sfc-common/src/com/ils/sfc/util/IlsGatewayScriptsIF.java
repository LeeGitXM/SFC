package com.ils.sfc.util;

import org.python.core.PyDictionary;

public interface IlsGatewayScriptsIF {
	void enqueueMessage(String queueName, String message, String status);
	
	void clearMessageQueue(String queueName);
	
	void echoMessage(String project, String messageHandler, PyDictionary payload);
}
