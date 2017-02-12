package com.ils.sfc.gateway.locator;

import com.ils.sfc.common.PythonCall;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.PyChartScope;

/** A wrapper around a chart scope that actually goes out to recipe data
 *  tags to get the value. */

@SuppressWarnings("serial")
public class S88Scope extends PyChartScope {
	private static LoggerEx log = LogUtil.getLogger(S88Scope.class.getName());
	private final PyChartScope stepScope;
	private final PyChartScope chartScope;
	private final String identifier;
	private final String fullKey;
	
	public S88Scope(PyChartScope chartScope,PyChartScope stepScope,String identifier, String fullKey) {
		this.chartScope = chartScope;
		this.stepScope = stepScope;
		this.identifier = identifier;
		this.fullKey = fullKey;
		log.infof("Instantiating my very own S88Scope object");
	}

	
	@Override
	public void removeScopeObserver(ScopeObserver observer) {

	}
	
	@Override
	public boolean containsKey(Object key) {
		log.infof("HELLO Key: %s", key.toString());
		return true;
	}
		
	@Override 
	/** Get a dictionary with the values of a recipe datum. 
	 *  If no hierarchy is involved, the given key will be the name of the datum,
	 *  and the returned map will have keys for each of the UDT members (including value).
	 *  If there is a hierarchy, the parent datums will correspond to tag folders. If a
	 *  folder is given as the key param, we return RecipeDataAccess object with the names of 
	 *  subfolders as the keys.
	 */
	public Object get(Object keyObj) {		
		// Build the tag path as far as the name of the UDT
		String key = keyObj.toString();
		if (!fullKey.isEmpty()) {
			key = fullKey + "." + key;
		}
		Object result = "ERROR";
		try{ 
			log.infof("Key: %s", key);
			log.infof("Identifier: %s", identifier);
			
			result = PythonCall.S88_GET.exec(chartScope,stepScope,key,identifier);
			log.infof("****  S88Get worked ****");
			return new PyChartScope();
			

		}
		catch(Exception ex) {
			log.errorf("EXCEPTION: %s",ex.getMessage());
			
		}
		log.infof("The key wasn't valid, returning a s88Scope");
		return new S88Scope(chartScope, stepScope, identifier, key);
	}

}

