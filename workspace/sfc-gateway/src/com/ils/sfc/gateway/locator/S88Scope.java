package com.ils.sfc.gateway.locator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ils.common.JavaToPython;
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
	private final JavaToPython j2p = new JavaToPython();
	private final S88PollTask pollTask;
	private final ScheduledExecutorService executor;
	
	public S88Scope(PyChartScope chartScope,PyChartScope stepScope,String identifier, String fullKey) {
		this.chartScope = chartScope;
		this.stepScope = stepScope;
		this.identifier = identifier;
		this.fullKey = fullKey;
		log.infof("Instantiating my very own S88Scope object");
        this.pollTask = new S88PollTask(chartScope,stepScope,fullKey);
        this.executor = Executors.newSingleThreadScheduledExecutor();
	}

	/**
	 * When there are no more observers, shut down the executor.
	 */
	@Override
	public void removeScopeObserver(ScopeObserver observer) {
		super.removeScopeObserver(observer);
		if( observers.isEmpty() ) {
			executor.shutdown();
		}
	}
	
	@Override
	public boolean containsKey(Object key) {
		log.infof("CONTAINS KEY? %s", key.toString());
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
		// Build the key path one section at a time.  A key with only a single element is illegal.
		String key = keyObj.toString();
		if( !fullKey.isEmpty()) {
			key = fullKey + "." + key;
			try{ 
				log.infof("Key: %s", key);
				log.infof("Identifier: %s", identifier);

				Object value  = PythonCall.S88_GET.exec(chartScope,stepScope,key,identifier);
				log.infof("****  S88Get worked ****");
				pollTask.setKey(key);
				executor.scheduleAtFixedRate(pollTask, 5, 3, TimeUnit.SECONDS);  // Every 5 seconds
				PyChartScope result = new PyChartScope();
				result.put(key, value);
				return result;
			}
			catch(Exception ex) {
				log.errorf("EXCEPTION: Fetching %s (%s)", key, ex.getMessage());
			}
		}
		return new S88Scope(chartScope, stepScope, identifier, key);
	}
	//================================= Execution Task ========================================
	/** 
	 * Run periodically to read the recipe data value..
	 */
	private class S88PollTask implements Runnable{
		private String key = "";
		private final PyChartScope chartScope;
		private final PyChartScope stepScope;
		private final String identifier;
		/**
		 * Constructor 
		 */
		public S88PollTask(PyChartScope cScope,PyChartScope sScope,String ident) {
			this.chartScope = cScope;
			this.stepScope  = sScope;
			this.identifier = ident;
		}
		
		public void setKey(String fullkey) { this.key=fullkey; }
		/**
		 * Execute a single read.
		 */
		public void run() {
			try {
				Object value  = PythonCall.S88_GET.exec(chartScope,stepScope,key,identifier);
				for(ScopeObserver observer:observers) {
					observer.observe(key, j2p.objectToPy(value));
				}
			}
			catch(Exception ex) {
				log.errorf("EXCEPTION: Running S88 %s (%s)", key, ex.getMessage());
			}
		}
	}
}

