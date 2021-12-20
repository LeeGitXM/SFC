/**
 *   (c) 2015-2021  ILS Automation. All rights reserved.
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
	private final static String CLSS = "IlsSfcRequestHandler";
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
					IlsSfcModule.MODULE_ID, "getChartPath", resourceId);
		}
		catch(Exception ge) {
			log.infof("%s.getChartPath: GatewayException (%s:%s)",CLSS,ge.getClass().getName(),ge.getMessage());
		};
		return path;
	}

	/**
	 * @return the hostname of the Gateway system
	 */
	public String getGatewayHostname()  {
		String path = "";
		try {
			path = (String)GatewayConnectionManager.getInstance().getGatewayInterface().moduleInvoke(
					IlsSfcModule.MODULE_ID, "getHostname");
		}
		catch(Exception ge) {
			log.infof("%s.getGatewayHostname: GatewayException (%s:%s)",CLSS,ge.getClass().getName(),ge.getMessage());
		};
		return path;
	}
	/**
	 * @return the path of a chart given its resource Id
	 */
	public String getUserLibPath()  {
		String path = "";
		try {
			path = (String)GatewayConnectionManager.getInstance().getGatewayInterface().moduleInvoke(
					IlsSfcModule.MODULE_ID, "getUserLibPath");
		}
		catch(Exception ge) {
			log.infof("%s.getUserLibPath: GatewayException (%s:%s)",CLSS,ge.getClass().getName(),ge.getMessage());
		};
		return path;
	}
	
	/**
	 * Find the database associated with the sequential function charts.
	 * 
	 * @param isIsolated true if this the chart is in an isolation (test) state.
	 * @return name of the database for production or isolation mode, as appropriate.
	 */
	public String getProjectDatabaseName(String projectName, boolean isIsolated) {
		String dbName = "";
		if( isIsolated ) dbName = getProjectToolkitProperty(projectName,ToolkitProperties.TOOLKIT_PROPERTY_ISOLATION_DATABASE);
		else dbName = getProjectToolkitProperty(projectName,ToolkitProperties.TOOLKIT_PROPERTY_DATABASE);
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
			log.infof("%s.getDatasourceNames: GatewayException (%s)",CLSS,ge.getMessage());
		}
		return names;
	}
	/**
	 * Find the tag provider associated with the sequential function charts.
	 * 
	 * @param isIsolated true if this the chart is in an isolation (test) state.
	 * @return name of the tag provider for production or isolation mode, as appropriate.
	 */
	public String getProjectProviderName(String projectName,boolean isIsolated)  {
		String providerName = "";
		if( isIsolated ) providerName = getProjectToolkitProperty(projectName,ToolkitProperties.TOOLKIT_PROPERTY_ISOLATION_PROVIDER);
		else providerName = getProjectToolkitProperty(projectName,ToolkitProperties.TOOLKIT_PROPERTY_PROVIDER);
		return providerName;
	}
	/**
	 * Acquire a value from the HSQL database table associated with the toolkit. A
	 * empty string is returned if the string is not found, null if an exception is thrown.
	 * @param propertyName name of the property for which a value is to be returned
	 * @return the value of the specified property.
	 */
	public String getProjectToolkitProperty(String projectName,String propertyName) {
		String result = null;
		//log.infof("%s.getToolkitProperty ... %s:%s",TAG,projectName,propertyName);
		try {
			result = (String)GatewayConnectionManager.getInstance().getGatewayInterface().moduleInvoke(
					IlsSfcModule.MODULE_ID, "getProjectToolkitProperty",projectName,propertyName);
			log.tracef("%s.getProjectToolkitProperty ... %s:%s = %s",CLSS,projectName,propertyName,result.toString());
		}
		catch(Exception ge) {
			log.infof("%s.getProjectToolkitProperty: GatewayException (%s:%s)",CLSS,ge.getClass().getName(),ge.getMessage());
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
	public double getProjectTimeFactor(String projectName,boolean isIsolated) {
		double factor = 1.0;
		if( isIsolated ) {
			String value = getProjectToolkitProperty(projectName,ToolkitProperties.TOOLKIT_PROPERTY_ISOLATION_TIME);
			try {
				factor = Double.parseDouble(value);
			}
			catch( NumberFormatException nfe) {
				log.warnf("%s.getProjectTimeFactor: stored value (%s), not a double. Using 1.0",CLSS,value);
			}
		}
		return 1/factor;
	}
	/**
	 * Retrieve the configured browser path from the ORM database HelpRecord. This is used for 
	 * context-sensitive help.
	 * @return the configured browser path (for Windows)
	 */
	public String getWindowsBrowserPath() {
		String result = null;
		try {
			result = (String)GatewayConnectionManager.getInstance().getGatewayInterface().moduleInvoke(
					IlsSfcModule.MODULE_ID, "getWindowsBrowserPath");
			log.tracef("%s.getWindowsBrowserPath ... %s",CLSS,result);
		}
		catch(Exception ge) {
			log.infof("%s.getWindowsBrowserPath: GatewayException (%s:%s)",CLSS,ge.getClass().getName(),ge.getMessage());
		}
		return result;
	}
	/**
	 * Set a clock rate factor for isolation mode only. We set in the BLT module
	 * as well. If that module is not present, then we simply ignore the exception.
	 * @param factor the amount to speed up or slow down time values. A value less
	 *        than one represents a speedup of the test.
	 */
	public void setProjectTimeFactor(String projectName,double factor) {
		log.infof("%s.setProjectTimeFactor ... %s:%s",CLSS,projectName,String.valueOf(factor));
		if( factor<=0.0 ) factor = 1.0;
		
		try {
			GatewayConnectionManager.getInstance().getGatewayInterface().moduleInvoke(
					IlsSfcModule.MODULE_ID, "setProjectTimeFactor",projectName,factor);
		}
		catch(Exception ge) {
			log.infof("%s.setProjectTimeFactor: GatewayException (%s:%s)",CLSS,ge.getClass().getName(),ge.getMessage());
		}
		try {
			GatewayConnectionManager.getInstance().getGatewayInterface().moduleInvoke(
					IlsSfcModule.BLT_MODULE_ID, "setProjectTimeFactor",projectName,factor);
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
	public void setProjectToolkitProperty(String projectName,String propertyName,String value) {
		log.tracef("%s.setProjectToolkitProperty ... %s:%s=%s",CLSS,projectName,propertyName,value);
		try {
			GatewayConnectionManager.getInstance().getGatewayInterface().moduleInvoke(
					IlsSfcModule.MODULE_ID, "setProjectToolkitProperty",projectName,propertyName,value);
		}
		catch(Exception ge) {
			log.infof("%s.setProjectToolkitProperty: GatewayException (%s:%s)",CLSS,ge.getClass().getName(),ge.getMessage());
		}
		try {
			GatewayConnectionManager.getInstance().getGatewayInterface().moduleInvoke(
					IlsSfcModule.BLT_MODULE_ID, "setProjectToolkitProperty",projectName,propertyName,value);
		}
		catch(Exception ignore) {}
	}
}
