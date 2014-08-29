package com.ils.sfc.gateway;

import java.util.Properties;

import org.python.core.PyDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ils.sfc.common.IlsGatewayScriptsIF;
import com.ils.sfc.common.IlsSfcIO;
import com.ils.sfc.common.IlsSfcIOIF.MessageStatus;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

public class IlsGatewayScripts implements IlsGatewayScriptsIF{
	private static final Logger logger = LoggerFactory.getLogger(IlsSfcIO.class);
	private IlsSfcIO ilsSfcIo;
	
	public IlsGatewayScripts(GatewayContext context) {
		ilsSfcIo = new IlsSfcIO(context);
	}

	@Override
	public void enqueueMessage(String queueName, String message,
			String statusString) {
		MessageStatus status = MessageStatus.valueOf(statusString);
		if(status == null) {
			status = MessageStatus.Info;
			logger.error("bad status for enqueueMessage: " + statusString);
		}
		ilsSfcIo.enqueueMessage(queueName, message, status);
	}

	@Override
	public void clearMessageQueue(String queueName) {
		ilsSfcIo.clearMessageQueue(queueName);		
	}

	@Override
	public void echoMessage(String project, String messageHandler, PyDictionary payload) {
		ilsSfcIo.sendMessage(project, messageHandler, payload, new Properties());
	}
}
