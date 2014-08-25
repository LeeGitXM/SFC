package com.ils.sfc.step;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A singleton that encapsulates all IO for a SFC. */
public class IlsSfcIO implements IlsSfcIOIF {
	private static final Logger logger = LoggerFactory.getLogger(IlsSfcIO.class);
	
	private Connection dbConnection;
	
	
	public void enqueueMessage(String messageQueueId, String message) {
		// TODO: create a timestamp and store the message
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
