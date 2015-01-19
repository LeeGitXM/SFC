package com.ils.sfc.client;

import com.inductiveautomation.ignition.client.gateway_interface.PushNotificationListener;
import com.inductiveautomation.ignition.client.model.ClientContext;
import com.inductiveautomation.ignition.common.gateway.messages.PushNotification;

/** This is a singleton that maintains the client-side state */
public class IlsSfcClientContext implements PushNotificationListener {
	private ClientContext clientContext;
	private static IlsSfcClientContext instance = new IlsSfcClientContext();
	private int msgCount = 0;
	
	private IlsSfcClientContext() {}

	public void setClientContext(ClientContext clientContext) {
		this.clientContext = clientContext;
	}

	public static IlsSfcClientContext getInstance() {
		return instance;
	}

	@Override
	public void receiveNotification(PushNotification notification) {
		// this will catch messages sent to Python--might be useful
		// JOptionPane.showMessageDialog(null, "got notification #" + ++msgCount + ": " + notification.getMessage().toString());		
	}

}
