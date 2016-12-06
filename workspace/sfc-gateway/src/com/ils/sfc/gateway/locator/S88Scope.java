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


	
	public S88Scope(PyChartScope chartScope,PyChartScope stepScope,String identifier) {
		this.chartScope = chartScope;
		this.stepScope = stepScope;
		this.identifier = identifier;

	}

	
	@Override
	public void removeScopeObserver(ScopeObserver observer) {

	}
	
	@Override
	public boolean containsKey(Object key) {
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
		String key = (String)keyObj;
		Object result = "ERROR";
		try{ 
			result = PythonCall.S88_GET.exec(chartScope,stepScope,key,identifier);
		}
		catch(Exception ex) {
			log.errorf("EXCEPTION: %s",ex.getMessage());
		}
		return result;
	}

}

