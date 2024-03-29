/**
 *   (c) 2017  ILS Automation. All rights reserved.
 *  
 */
package com.ils.sfc.gateway.locator;

import java.io.IOException;

import com.ils.common.watchdog.Watchdog;
import com.ils.common.watchdog.WatchdogObserver;
import com.ils.common.watchdog.WatchdogTimer;
import com.ils.sfc.common.IlsSfcModule;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.gateway.IlsSfcGatewayHook;
import com.inductiveautomation.ignition.common.TypeUtilities;
import com.inductiveautomation.ignition.common.sqltags.model.Tag;
import com.inductiveautomation.ignition.common.sqltags.model.TagPath;
import com.inductiveautomation.ignition.common.sqltags.parser.TagPathParser;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.sqltags.TagProvider;
import com.inductiveautomation.sfc.api.MonitoredScopeLifecycle;
import com.inductiveautomation.sfc.api.PyChartScope;

/** A wrapper around a chart scope that actually goes out to recipe data
 *  tags to get the value.
 *  
 *  NOTE: We cannot use the Ignition ExecutionManager to run our polling
 *        thread because it conflicts with script managers and tag subscribers.
 */

@SuppressWarnings("serial")
public class S88Scope extends PyChartScope implements WatchdogObserver,MonitoredScopeLifecycle {
	private static final String CLSS = "S88Scope";
	private static final double POLL_INTERVAL = 5.0;
	private static final double INITIAL_POLL_INTERVAL = 0.0;
	private final LoggerEx log = LogUtil.getLogger(getClass().getPackage().getName());
	private static boolean DEBUG = false;
	private final GatewayContext context;
	private final PyChartScope stepScope;
	private final PyChartScope chartScope;
	private boolean running = false;  // Until proved otherwise
	private final String identifier;
	private String key;
	private final Watchdog dog;
	private final WatchdogTimer timer;
	
	public S88Scope(GatewayContext ctx,PyChartScope chartScope,PyChartScope stepScope,String identifier, String key) {
		log.infof("%s: In S88Scope with identifier: <%s> and key: <%s>", CLSS, identifier, key);
		this.context = ctx;
		this.chartScope = chartScope;
		this.stepScope = stepScope;
		this.identifier = identifier;
		if( key==null ) key="";
		this.key = key;
		IlsSfcGatewayHook hook = (IlsSfcGatewayHook)context.getModule(IlsSfcModule.MODULE_ID);
		this.timer = hook.getTimer();
		this.dog = new Watchdog(key,this);
		dog.setActive(false);
		if(DEBUG || log.isDebugEnabled() ) log.infof("%s: Constructing new scope for %s",CLSS,key);
	}

	
	/**
	 * Since supportsSubScopes is false, this returns the complete key. 
	 * If you return false here, "NOT_FOUND" quality will ultimately be 
	 * returned by the expression.		
	 */
	@Override
	public boolean containsKey(Object key) {
		if(DEBUG || log.isDebugEnabled() ) log.infof("%s.containsKey %s ?",CLSS, key);
		
	    if(!TypeUtilities.equalsIgnoreCase(this.key, key.toString())){
	        this.key = key.toString();
	        startWatchDogIfReady();
	        evaluate();
	    }
	    return true;	
	}
	
	
	/**
	* This is a new function that causes keys like "a.b.c" to be sent in their entirety. Note they still won't include the identifier part, like "prior".
	**/
	//@Override
	public boolean supportsSubScopes() {
		return false;
	}

	
	
	/***************
	* MonitoredScopeLifecycle functions. These are called based on when the transition starts/stops.
	****************/
	@Override
	public void start() {
		this.running = true;
		if(DEBUG || log.isDebugEnabled() ) log.infof("%s.start: Starting %s............................",CLSS,key);
		evaluate();
	}

	@Override
	public void stop() {
		this.running = false;
		if(DEBUG || log.isDebugEnabled() )log.infof("%s.stop: Stopping %s............................",CLSS,key);
		timer.removeWatchdog(dog);
	}	
	
	//================================= Watchdog Evaluation Method ========================================
	/** 
	 * Run periodically to read the recipe data value. If successful, it "pets" the dog
	 * to trigger another cycle.
	 */
	@Override
	public void evaluate() {
		if( key.isEmpty()) return;
		try {
			Object val  = PythonCall.S88_GET.exec(chartScope,stepScope,key,identifier);
			if(DEBUG || log.isDebugEnabled() ) log.infof("%s.evaluate: Returned .................. %s.%s=%s",CLSS,identifier,key,val.toString());
			setVariable(key,val);
			// Re-stroke the dog
			dog.setSecondsDelay(POLL_INTERVAL);
			if( running ) {
				timer.updateWatchdog(dog);  // pet dog
			}
		}
		catch(Exception ex) {
			log.errorf("S88PollTask: EXCEPTION: Running S88 %s.%s (%s)", identifier,key, ex.getMessage());
		}
	}
	
	/**
	 * Start the watch dog if it is not already started and the key is set
	 */
	protected void startWatchDogIfReady(){
		if(running && !key.isEmpty() && !dog.isActive()) {
			if(DEBUG || log.isDebugEnabled() ) log.infof("%s.startWatchDogIfReady: started dog for %s.%s",CLSS,identifier,key);
			dog.setSecondsDelay(INITIAL_POLL_INTERVAL);
			timer.updateWatchdog(dog); 
		}
	}
}