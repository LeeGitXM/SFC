/**
 *   (c) 2015  ILS Automation. All rights reserved.
 *  
 */
package com.ils.sfc.gateway;

import java.util.ArrayList;
import java.util.List;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.gateway.persistence.ToolkitRecord;
import com.inductiveautomation.ignition.common.datasource.DatasourceStatus;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.datasource.Datasource;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

/**
 *  This handler provides is a common class for handling requests for block properties and control
 *  of the execution engine. The requests can be expected arrive both through the scripting interface
 *  and the RPC diispatcher.In general, the calls are made to update properties 
 *  in the block objects and to trigger their evaluation.
 *  
 *  
 *  This class is a singleton for easy access throughout the application.
 */
public class GatewayRequestHandler {
	private final static String TAG = "ControllerRequestHandler";
	private final LoggerEx log;
	private GatewayContext context = null;
	private static GatewayRequestHandler instance = null;

	/**
	 * Initialize with instances of the classes to be controlled.
	 */
	private GatewayRequestHandler() {
		log = LogUtil.getLogger(getClass().getPackage().getName());
	}
	/**
	 * Static method to create and/or fetch the single instance.
	 */
	public static GatewayRequestHandler getInstance() {
		if( instance==null) {
			synchronized(GatewayRequestHandler.class) {
				instance = new GatewayRequestHandler();
			}
		}
		return instance;
	}

	public void setContext(GatewayContext ctx) { this.context = ctx; }
	
	/**
	 * Find the database associated with the sequential function charts.
	 * 
	 * @param isIsolation true if this the chart is in an isolation (test) state.
	 * @return name of the database for production or isolation mode, as appropriate.
	 */
	public String getDatabaseName(boolean isIsolated) {
		String dbName = "";
		if( isIsolated ) dbName = getToolkitProperty(IlsProperty.TOOLKIT_PROPERTY_ISOLATION_DATABASE);
		else dbName = getToolkitProperty(IlsProperty.TOOLKIT_PROPERTY_DATABASE);
		return dbName;
	}
	
	/**
	 * A list of data sources is not available in client scope.
	 * @return
	 */
	public List<String> getDatasourceNames() {
		List<Datasource> sources = context.getDatasourceManager().getDatasources();
		List<String> result = new ArrayList<>();
		for( Datasource source:sources) {
			if(source.getStatus().equals(DatasourceStatus.VALID)) {
				result.add(source.getName());
			}
		}
		return result;
	}
	
	/**
	 * Find the tag provider associated with the sequential function charts.
	 * 
	 * @param isIsolation true if this the chart is in an isolation (test) state.
	 * @return name of the tag provider for production or isolation mode, as appropriate.
	 */
	public String getProviderName(boolean isIsolated)  {
		String providerName = "";
		if( isIsolated ) providerName = getToolkitProperty(IlsProperty.TOOLKIT_PROPERTY_ISOLATION_PROVIDER);
		else providerName = getToolkitProperty(IlsProperty.TOOLKIT_PROPERTY_PROVIDER);
		return providerName;
	}
	/**
	 * On a failure to find the property, an empty string is returned.
	 */
	public String getToolkitProperty(String propertyName) {
		String value = "";
		try {
			ToolkitRecord record = context.getPersistenceInterface().find(ToolkitRecord.META, propertyName);
			if( record!=null) value =  record.getValue();
		}
		catch(Exception ex) {
			log.warnf("%s.getToolkitProperty: Exception retrieving %s (%s),",TAG,propertyName,ex.getMessage());
		}
		return value;
	}
	/**
	 * Set a clock rate factor. This will change timing for isolation mode only.
	 * This method is provided as a hook for test frameworks.
	 * @param factor the amount to speed up or slow down the clock.
	 */
	public void setTimeFactor(double factor) {
		String value = String.valueOf(factor);
		setToolkitProperty(IlsProperty.TOOLKIT_PROPERTY_ISOLATION_TIME,value);
	}
	/**
	 * We have two types of properties of interest here. The first set is found in ScriptConstants
	 * and represents scripts used for external Python interfaces to Application/Family.
	 * The second category represents database and tag interfaces for production and isolation
	 * modes.
	 */
	public void setToolkitProperty(String propertyName, String value) {
		try {
			ToolkitRecord record = context.getPersistenceInterface().find(ToolkitRecord.META, propertyName);
			if( record==null) record = context.getPersistenceInterface().createNew(ToolkitRecord.META);
			if( record!=null) {
				record.setName(propertyName);
				record.setValue(value);
				context.getPersistenceInterface().save(record);
			}
			else {
				log.warnf("%s.setToolkitProperty: %s=%s - failed to create persistence record (%s)",TAG,propertyName,value,ToolkitRecord.META.quoteName);
			}
		}
		catch(Exception ex) {
			log.warnf("%s.setToolkitProperty: Exception setting %s=%s (%s),",TAG,propertyName,value,ex.getMessage());
		}
	}
}

