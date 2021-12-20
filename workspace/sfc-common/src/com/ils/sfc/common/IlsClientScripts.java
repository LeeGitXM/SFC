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
	public static String getProjectDatabaseName(String projectName,boolean isIsolation)  {
		String dbName = requestHandler.getProjectDatabaseName(projectName,isIsolation);
		return dbName;
	}
	
	/**
	 * Find the path to the user-lib directory under the current running Ignition.
	 * 
	 * @return path to the directory1.
	 */
	public static String getUserLibPath()  {
		String userLibPath = requestHandler.getUserLibPath();
		return userLibPath;
	}
	
	/**
	 * Find the tag provider associated with the sequential function charts.
	 * 
	 * @param isIsolation true if this the chart is in an isolation (test) state.
	 * @return name of the tag provider for production or isolation mode, as appropriate.
	 */
	public static String getProjectProviderName(String projectName,boolean isIsolation)  {
		String providerName = requestHandler.getProjectProviderName(projectName,isIsolation);
		return providerName;
	}
	
	public static double getProjectTimeFactor(String projectName,boolean isIsolation) {
		return requestHandler.getProjectTimeFactor(projectName,isIsolation);
	}
	
	/**
	 * Set a clock rate factor. This will change timing for isolation mode only.
	 * This method is provided as a hook for test frameworks.
	 * @param factor the amount to speed up or slow down time values. A value less
	 *        than one represents a speedup.
	 */
	public static void setProjectTimeFactor(String projectName,double factor) {
		requestHandler.setProjectTimeFactor(projectName,factor);
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
