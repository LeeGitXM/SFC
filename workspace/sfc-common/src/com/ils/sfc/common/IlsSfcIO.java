package com.ils.sfc.common;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ils.sfc.common.IlsSfcIOIF;
import com.inductiveautomation.ignition.gateway.datasource.Datasource;
import com.inductiveautomation.ignition.gateway.datasource.SRConnection;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

/** A singleton that encapsulates all IO for a SFC. */
public class IlsSfcIO implements IlsSfcIOIF {
	private static final Logger logger = LoggerFactory.getLogger(IlsSfcIO.class);
	private GatewayContext gatewayContext;
	private String dbProvider;
	
	public IlsSfcIO(GatewayContext gatewayContext) {
		this.gatewayContext = gatewayContext;
		// TODO: choose db provider more intelligently--which one is the default ??
		for(Datasource datasource: gatewayContext.getDatasourceManager().getDatasources()) {
			dbProvider = datasource.getName();
		}
		if(dbProvider == null) {
			logger.error("Error: no datasource in project");
		}
	}
		
	public void enqueueMessage(String queueName, String message, MessageStatus status) {
		SRConnection conn = null;
		try {
			conn = gatewayContext.getDatasourceManager().getConnection(dbProvider);
			Integer statusId = (Integer)conn.runScalarQuery("select id from QueueMessageStatus where MessageStatus = '" + status + "'");
			Integer queueId = (Integer)conn.runScalarQuery("select id from QueueMaster where QueueKey = '" + queueName + "'");
			conn.runUpdateQuery("insert into QueueDetail (QueueId, Timestamp, StatusId, Message) values (" + queueId + ", getdate(), " + statusId + ",'" + message + "')");
		}
		catch( Exception e) {
			logger.error("Error enqueueing message", e);
		}
		finally {
			try {
				if(conn != null) {conn.close();}
			}
			catch(SQLException e) {}
		}
	}
		
	public void clearMessageQueue(String queueName) {
		SRConnection conn = null;
		try {
			conn = gatewayContext.getDatasourceManager().getConnection(dbProvider);
			Integer queueId = (Integer)conn.runScalarQuery("select id from QueueMaster where QueueKey = '" + queueName + "'");
			conn.runUpdateQuery("delete from QueueDetail where QueueId = " + queueId);
		}
		catch( Exception e) {
			logger.error("Error enqueueing message", e);
		}
		finally {
			try {
				if(conn != null) {conn.close();}
			}
			catch(SQLException e) {}
		}
	}

}
