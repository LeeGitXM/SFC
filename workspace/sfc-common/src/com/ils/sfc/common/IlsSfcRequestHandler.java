/**
 *   (c) 2015  ILS Automation. All rights reserved.
 *  
 */
package com.ils.sfc.common;

import java.util.ArrayList;
import java.util.List;

import com.inductiveautomation.ignition.client.gateway_interface.GatewayConnectionManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;



/**
 *  This class is a common point for managing requests to the gateway dealing.
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
	 * Set a clock rate factor. This must NOT be exercised in a production environment.
	 * This is a hook for testing only.
	 * @param factor the amount to speed up or slow down the clock.
	 */
	public void setTimeFactor(Double factor) {
		log.infof("%s.setTimeFactor ... %s",TAG,String.valueOf(factor));
		try {
			GatewayConnectionManager.getInstance().getGatewayInterface().moduleInvoke(
					IlsSfcModule.MODULE_ID, "setTimeFactor",factor);
		}
		catch(Exception ge) {
			log.infof("%s.setTimeFactor: GatewayException (%s:%s)",TAG,ge.getClass().getName(),ge.getMessage());
		}
	}
	
	/**
	 * Save a value into the HSQL database table associated with the toolkit. The 
	 * table contains name-value pairs, so any name is allowable.
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
	}
}
