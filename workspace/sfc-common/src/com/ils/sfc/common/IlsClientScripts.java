package com.ils.sfc.common;

import com.inductiveautomation.ignition.client.model.ClientContext;

/**
 * Provide additional functionality for python scripts
 */
public class IlsClientScripts {	
	private static ClientContext context = null;
	private static IlsSfcRequestHandler requestHandler = new IlsSfcRequestHandler();

	public static void setContext(ClientContext ctx) { context = ctx; }
	
	/**
	 * Find the database associated with the sequential function charts.
	 * 
	 * @param isIsolation true if this the chart is in an isolation (test) state.
	 * @return name of the database for production or isolation mode, as appropriate.
	 */
	public String getDatabaseName(boolean isIsolation)  {
		String dbName = requestHandler.getDatabaseName(isIsolation);
		return dbName;
	}
	
	/**
	 * Find the tag provider associated with the sequential function charts.
	 * 
	 * @param isIsolation true if this the chart is in an isolation (test) state.
	 * @return name of the tag provider for production or isolation mode, as appropriate.
	 */
	public String getProviderName(boolean isIsolation)  {
		String providerName = requestHandler.getProviderName(isIsolation);
		return providerName;
	}
	
	/**
	 * Set a clock rate factor. This will change timing for isolation mode only.
	 * This method is provided as a hook for test frameworks.
	 * @param factor the amount to speed up or slow down the clock.
	 */
	public static void setTimeFactor(double factor) {
		requestHandler.setTimeFactor(factor);
	}
	
	
}
