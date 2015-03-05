package com.ils.sfc.common;

import com.inductiveautomation.ignition.client.model.ClientContext;

/**
 * Provide additional functionality for python scripts
 */
public class IlsClientScripts {	
	private static ClientContext context = null;

	public static void setContext(ClientContext ctx) { context = ctx; }
	
	/**
	 * Find the database associated with a specified project. This requires 
	 * that a Gateway context. NOTE: There is no default defined for the global project.
	 * 
	 * @return name of the default database for the current project
	 */
	public String getDefaultDatabaseName()  {
		String dbName = context.getDefaultDatasourceName();
		return dbName;
	}
	
}
