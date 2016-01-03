/**
 *   (c) 2015  ILS Automation. All rights reserved.
 *  
 */
package com.ils.sfc.common;

import java.util.ArrayList;
import java.util.List;

import com.ils.common.persistence.ToolkitProperties;
import com.inductiveautomation.ignition.client.gateway_interface.GatewayConnectionManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;



/**
 *  This class is a common point for managing requests to the gateway dealing .
 *  It is designed for use by Java code in the designer as well as Python scripting. 
 *  It provides a way to request/set properties in persistent (HSQLdb) storage,
 *  among other things.
 *  
 *  Each request is relayed to the Gateway scope via an RPC call.
 */
public class IlsSfcRequestHandler {
	private final static String TAG = "IlsSfcRequestHandler";
	private final LoggerEx log;

	/**
	 * Constructor adds common attributes that are needed to generate unique keys to identify
	 * blocks and connectors.
	 */
	public IlsSfcRequestHandler()  {
		log = LogUtil.getLogger(getClass().getPackage().getName());
	}

	/**
	 * @return the path of a chart given its resource Id
	 */
	public String getChartPath(long resourceId)  {
		String path = "";
		try {
			path = (String)GatewayConnectionManager.getInstance().getGatewayInterface().moduleInvoke(
					IlsSfcModule.MODULE_ID, "getChartPath",new Long(resourceId));
		}
		catch(Exception ge) {
			log.infof("%s.getChartPath: GatewayException (%s:%s)",TAG,ge.getClass().getName(),ge.getMessage());
		};
		return path;
	}
	
	/**
	 * Find the database associated with the sequential function charts.
	 * 
	 * @param isIsolated true if this the chart is in an isolation (test) state.
	 * @return name of the database for production or isolation mode, as appropriate.
	 */
	public String getDatabaseName(boolean isIsolated) {
		String dbName = "";
		if( isIsolated ) dbName = getToolkitProperty(ToolkitProperties.TOOLKIT_PROPERTY_ISOLATION_DATABASE);
		else dbName = getToolkitProperty(ToolkitProperties.TOOLKIT_PROPERTY_DATABASE);
		return dbName;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getDatasourceNames() {
		List<String> names = new ArrayList<>();
		try {
			names = (List<String>)GatewayConnectionManager.getInstance().getGatewayInterface().moduleInvoke(
					IlsSfcModule.MODULE_ID, "getDatasourceNames");
		}
		catch(Exception ge) {
			log.infof("%s.getDatasourceNames: GatewayException (%s)",TAG,ge.getMessage());
		}
		return names;
	}
	/**
	 * Find the tag provider associated with the sequential function charts.
	 * 
	 * @param isIsolated true if this the chart is in an isolation (test) state.
	 * @return name of the tag provider for production or isolation mode, as appropriate.
	 */
	public String getProviderName(boolean isIsolated)  {
		String providerName = "";
		if( isIsolated ) providerName = getToolkitProperty(ToolkitProperties.TOOLKIT_PROPERTY_ISOLATION_PROVIDER);
		else providerName = getToolkitProperty(ToolkitProperties.TOOLKIT_PROPERTY_PROVIDER);
		return providerName;
	}
	/**
	 * Acquire a value from the HSQL database table associated with the toolkit. A
	 * empty string is returned if the string is not found, null if an exception is thrown.
	 * @param propertyName name of the property for which a value is to be returned
	 * @return the value of the specified property.
	 */
	public String getToolkitProperty(String propertyName) {
		String result = null;
		//log.infof("%s.getToolkitProperty ... %s",TAG,propertyName);
		try {
			result = (String)GatewayConnectionManager.getInstance().getGatewayInterface().moduleInvoke(
					IlsSfcModule.MODULE_ID, "getToolkitProperty",propertyName);
			log.tracef("%s.getToolkitProperty ... %s = %s",TAG,propertyName,result.toString());
		}
		catch(Exception ge) {
			log.infof("%s.getToolkitProperty: GatewayException (%s:%s)",TAG,ge.getClass().getName(),ge.getMessage());
		}
		return result;
	}

	/**
	 * Get the clock rate factor. For non-isolation mode the value is fixed at 1.0.
	 * This method is provided as a hook for test frameworks.
	 * @param isIsolated. True if the system is currently in ISOLATION mode.
	 * @return the amount to speed up or slow down the clock. A value greater
	 *         than one represents a test speedup.
	 */
	public double getTimeFactor(boolean isIsolated) {
		double factor = 1.0;
		if( isIsolated ) {
			String value = getToolkitProperty(ToolkitProperties.TOOLKIT_PROPERTY_ISOLATION_TIME);
			try {
				factor = Double.parseDouble(value);
			}
			catch( NumberFormatException nfe) {
				log.warnf("%s.getTimeFactor: stored value (%s), not a double. Using 1.0",TAG,value);
			}
		}
		return 1/factor;
	}

	/**
	 * Set a clock rate factor for isolation mode only. We set in the BLT module
	 * as well. If that module is not present, then we simply ignore the exception.
	 * @param factor the amount to speed up or slow down time values. A value less
	 *        than one represents a speedup of the test.
	 */
	public void setTimeFactor(double factor) {
		log.infof("%s.setTimeFactor ... %s",TAG,String.valueOf(factor));
		if( factor<=0.0 ) factor = 1.0;
		
		try {
			GatewayConnectionManager.getInstance().getGatewayInterface().moduleInvoke(
					IlsSfcModule.MODULE_ID, "setTimeFactor",new Double(factor));
		}
		catch(Exception ge) {
			log.infof("%s.setTimeFactor: GatewayException (%s:%s)",TAG,ge.getClass().getName(),ge.getMessage());
		}
		try {
			GatewayConnectionManager.getInstance().getGatewayInterface().moduleInvoke(
					IlsSfcModule.BLT_MODULE_ID, "setTimeFactor",new Double(factor));
		}
		catch(Exception ignore) {}
	}
	
	/**
	 * Save a value into the HSQL database table associated with the toolkit. The 
	 * table contains name-value pairs, so any name is allowable. We also execute
	 * this method on behalf of the BLT-module in case there are any side-effects
	 * of saving particular parameters.
	 * @param propertyName name of the property for which a value is to be set
	 * @param the new value of the property.
	 */
	public void setToolkitProperty(String propertyName,String value) {
		log.tracef("%s.setToolkitProperty ... %s=%s",TAG,propertyName,value);
		try {
			GatewayConnectionManager.getInstance().getGatewayInterface().moduleInvoke(
					IlsSfcModule.MODULE_ID, "setToolkitProperty",propertyName,value);
		}
		catch(Exception ge) {
			log.infof("%s.setToolkitProperty: GatewayException (%s:%s)",TAG,ge.getClass().getName(),ge.getMessage());
		}
		try {
			GatewayConnectionManager.getInstance().getGatewayInterface().moduleInvoke(
					IlsSfcModule.BLT_MODULE_ID, "setToolkitProperty",propertyName,value);
		}
		catch(Exception ignore) {}
	}
}
