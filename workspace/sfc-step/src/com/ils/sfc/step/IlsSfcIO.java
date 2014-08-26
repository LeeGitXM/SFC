package com.ils.sfc.step;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inductiveautomation.ignition.gateway.datasource.SRConnection;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

/** A singleton that encapsulates all IO for a SFC. */
public class IlsSfcIO implements IlsSfcIOIF {
	private static final Logger logger = LoggerFactory.getLogger(IlsSfcIO.class);
	private GatewayContext gatewayContext;
	
	public IlsSfcIO(GatewayContext gatewayContext) {
		this.gatewayContext = gatewayContext;
	}
		
	public void enqueueMessage(String dataSource, String queueName, String message) {
		gatewayContext.getScriptManager().runFunction(arg0, arg1);
	}
	
	private IlsSfcIO(String dbDriverClass, String dbUrl, String dbUser, String dbPassword) throws SQLException {
		dbConnection = getDbConnection(dbDriverClass, dbUrl, dbUser, dbPassword);
	}
	
	private static Connection getDbConnection(String dbDriverClass, String dbUrl, String dbUser, String dbPassword) throws SQLException {
	    Properties connectionProps = new Properties();
	    connectionProps.put("user", dbUser);
	    connectionProps.put("password", dbPassword);
	    return DriverManager.getConnection(dbUrl, connectionProps);
	}
	
	/** Close all resources and free the instance for GC. */
	public void closeInstance() {
		try {
			if(dbConnection != null) dbConnection.close();
		} catch (SQLException e) {}

	}
	
	/** Close all resources and free the instance for GC. */
	public void close() {
	}

}
