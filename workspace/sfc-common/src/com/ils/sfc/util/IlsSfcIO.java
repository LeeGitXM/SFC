package com.ils.sfc.util;

import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import org.python.core.PyDictionary;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ils.sfc.util.IlsSfcIOIF;
import com.inductiveautomation.ignition.common.model.ApplicationScope;
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

	@Override
	public void sendMessage(String project, String messageHandler, Map<String,?> payload, 
		Properties filterParams) {
		try {
			PyObject[] pyObjects = new PyObject[2 * payload.size()];
			int index = 0;
			for(String key: payload.keySet()) {
				pyObjects[index++] = new PyString(key);
				Object value = payload.get(key);
				if(value instanceof String) {
					pyObjects[index++] = new PyString((String)value);
				}
				else if(value instanceof Integer) {
					pyObjects[index++] = new PyInteger((Integer)value);					
				}
				else {
					logger.error("unknown type for message: " + value.getClass().getName());
					index++;
				}
			}
			PyDictionary pyPayload = new PyDictionary(pyObjects);
			gatewayContext.getMessageDispatchManager().dispatch(project, messageHandler, pyPayload, filterParams);
		} catch (Exception e) {
			logger.error("Error sending message to client", e);
		}
	}

}
