package com.ils.sfc.common;

import com.inductiveautomation.ignition.client.model.ClientContext;
import com.inductiveautomation.ignition.common.script.JythonExecException;

/**
 * Expose features of the ILS-SFC module to python scripts.
 */
public class IlsClientScripts {	
	private static ClientContext context = null;
	private static IlsSfcRequestHandler requestHandler = new IlsSfcRequestHandler();

	public static void setContext(ClientContext ctx) { context = ctx; }
	/**
	 * @return the path of a chart given its resource Id
	 */
	public static String getChartPath(long resourceId)  {
		String path = requestHandler.getChartPath(resourceId);
		return path;
	}
	/**
	 * Find the database associated with the sequential function charts.
	 * 
	 * @param isIsolation true if this the chart is in an isolation (test) state.
	 * @return name of the database for production or isolation mode, as appropriate.
	 */
	public static String getDatabaseName(boolean isIsolation)  {
		String dbName = requestHandler.getDatabaseName(isIsolation);
		return dbName;
	}
	
	/**
	 * Find the tag provider associated with the sequential function charts.
	 * 
	 * @param isIsolation true if this the chart is in an isolation (test) state.
	 * @return name of the tag provider for production or isolation mode, as appropriate.
	 */
	public static String getProviderName(boolean isIsolation)  {
		String providerName = requestHandler.getProviderName(isIsolation);
		return providerName;
	}
	
	public static double getTimeFactor(boolean isIsolation) {
		return requestHandler.getTimeFactor(isIsolation);
	}
	
	/**
	 * Set a clock rate factor. This will change timing for isolation mode only.
	 * This method is provided as a hook for test frameworks.
	 * @param factor the amount to speed up or slow down time values. A value less
	 *        than one represents a speedup.
	 */
	public static void setTimeFactor(double factor) {
		requestHandler.setTimeFactor(factor);
	}
	
	/** Time the overhead for calling java-to-python */
	public static void timePythonCalls() {
		int numCalls = 10000;
		Object[] args = {};
		try {
			PythonCall.DO_NOTHING.exec(args);  // call once to compile
			long startMillis = System.currentTimeMillis();
			long dummyTotal = 0;
			for(int i = 0; i < numCalls; i++) {
				Integer rval = (Integer)PythonCall.DO_NOTHING.exec(args);
				// try to stop the compiler from optimizing this out by
				// doing some fake work:
				dummyTotal += rval;
			}
			long elapsedMillis = System.currentTimeMillis() - startMillis;
			double millisPerCall = elapsedMillis / (double)numCalls;
			System.out.println(Integer.toString(numCalls) + " python calls took " + elapsedMillis + " ms; (" + millisPerCall + " ms per call)" + " junk count " + dummyTotal);
		} catch (JythonExecException e) {
			e.printStackTrace();
		}
	}
	
}
