package com.ils.sfc.common;

public interface IlsGatewayScriptsIF {
	void enqueueMessage(String queueName, String message, String status);
	
	void clearMessageQueue(String queueName);
	
	void echoMessage(String message, String messageType, String clientId);
}
