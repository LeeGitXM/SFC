/**
 *   (c) 2017  ILS Automation. All rights reserved.
 *  
 */
package com.ils.sfc.gateway.locator;

import com.ils.common.JavaToPython;
import com.ils.common.watchdog.Watchdog;
import com.ils.common.watchdog.WatchdogObserver;
import com.ils.common.watchdog.WatchdogTimer;
import com.ils.sfc.common.IlsSfcModule;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.gateway.IlsSfcGatewayHook;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.sfc.api.PyChartScope;

/** A wrapper around a chart scope that actually goes out to recipe data
 *  tags to get the value.
 *  
 *  NOTE: We cannot use the Ignition ExecutionManager to run our polling
 *        thread because it conflicts with script managers and tag subscribers.
 */

@SuppressWarnings("serial")
public class S88Scope extends PyChartScope implements WatchdogObserver {
	private static final String CLSS = "S88Scope";
	private static final double POLL_INTERVAL = 5.0;
	private final LoggerEx log = LogUtil.getLogger(getClass().getPackage().getName());
	private final GatewayContext context;
	private final PyChartScope stepScope;
	private final PyChartScope chartScope;
	private final JavaToPython j2p = new JavaToPython();
	boolean supportsKey = true;  // Until proved otherwise
	private final String identifier;
	private final String baseKey;
	private String leafKey;
	private final Watchdog dog;
	private final WatchdogTimer timer;
	private Object value = null;
	
	public S88Scope(GatewayContext ctx,PyChartScope chartScope,PyChartScope stepScope,String identifier, String key) {
		this.context = ctx;
		this.chartScope = chartScope;
		this.stepScope = stepScope;
		this.identifier = identifier;
		this.baseKey = key;
		this.leafKey = "";
		IlsSfcGatewayHook hook = (IlsSfcGatewayHook)context.getModule(IlsSfcModule.MODULE_ID);
		this.timer = hook.getTimer();
		this.dog = new Watchdog(baseKey,this);
		log.infof("%s: Constructing new scope for %s",CLSS,baseKey);
	}

	/**
	 * When there are no more observers, shut down the executor.
	 */
	@Override
	public synchronized void removeScopeObserver(ScopeObserver observer) {
		super.removeScopeObserver(observer);
		if( observers.isEmpty() ) {
			log.infof("%s.%s: removeScopeObserver (%s.%s.%s)",CLSS,baseKey,identifier,baseKey,leafKey);
			timer.removeWatchdog(dog);	
		}
	}
	
	/**
	 * This appears to get called with a leaf. 
	 * We were not successful here calling S88Get.
	 */
	@Override
	public boolean containsKey(Object key) {
		set key , get value,add watchdog
		supportsKey = true;
		log.infof("%s.CONTAINS %s ?  (%s)", baseKey, key.toString(),(supportsKey?"TRUE":"FALSE"));
		return supportsKey;
		
	}
		
	@Override 
	/** Get a dictionary with the values of a recipe datum. 
	 *  If no hierarchy is involved, the given key will be the name of the datum,
	 *  and the returned map will have keys for each of the UDT members (including value).
	 *  If there is a hierarchy, the parent datums will correspond to tag folders. If a
	 *  folder is given as the key param, we return RecipeDataAccess object with the names of 
	 *  subfolders as the keys.
	 */
	public synchronized Object get(Object keyObj) {		
		// Build the key path one section at a time.  A key with only a single element is illegal.
		leafKey = keyObj.toString();
		if( baseKey.isEmpty()) {
			supportsKey = false;
			return new S88Scope(context,chartScope, stepScope, identifier, leafKey);
		}
		String key = baseKey + "." + leafKey;

		log.infof(">>>> %s.get: Key: %s", CLSS,key);
		try {
			value  = PythonCall.S88_GET.exec(chartScope,stepScope,key,identifier);
			log.infof("****  S88Get worked ****  %s for key %s", value.toString(), key);
			dog.setSecondsDelay(POLL_INTERVAL);
			timer.updateWatchdog(dog);  // pet dog
			log.infof("...returning  %s for key %s", value.toString(), key);
			return value;
		} 
		catch (JythonExecException jee) {
			log.errorf("S88Scope.get: EXCEPTION for %s (UNSUPPORTED)",key);
			value = null;
			supportsKey = false;
			return new S88Scope(context,chartScope, stepScope, identifier, key);
		}
	}
	
	//================================= Watchdog Evaluation Method ========================================
	/** 
	 * Run periodically to read the recipe data value..
	 */
	public void evaluate() {
		try {
			log.infof("%s.evaluate: Running a poll ............................ for %s.%s.%s",CLSS,identifier,baseKey,leafKey);
			String fullKey = String.format("%s.%s",baseKey,leafKey);
			Object val  = PythonCall.S88_GET.exec(chartScope,stepScope,fullKey,identifier);
			log.infof("%s.evaluate: Returned ............................ %s",CLSS,val.toString());
			S88Scope.this.value = val;
			for(ScopeObserver observer:observers) {
				observer.observe(fullKey, j2p.objectToPy(val));
				log.infof("S88PollTask: Got %s = %s", fullKey, val.toString());
			}

			log.infof("%s.evaluate: completing a poll ............................ for %s.%s.%s",CLSS,identifier,baseKey,leafKey);
		}
		catch(Exception ex) {
			log.errorf("S88PollTask: EXCEPTION: Running S88 %s (%s)", baseKey, ex.getMessage());
		}
		// Restroke the dog
		dog.setSecondsDelay(POLL_INTERVAL);
		timer.updateWatchdog(dog);  // pet dog
	}
}