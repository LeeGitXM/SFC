/**
 *   (c) 2013-2016  ILS Automation. All rights reserved.
 *  
 */
package com.ils.sfc.designer.search;

import com.inductiveautomation.ignition.common.project.resource.ResourcePath;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.designer.SFCDesignerHook;


/**
 *  The locator is designed to navigate to the named path in the navigation tree.
 *  This is as a consequence of double-clicking the icon in the find/replace window.
 */
public class ChartLocator {
	private static String TAG = "ChartLocator";
	private final LoggerEx log;
	private final DesignerContext context;

	/**
	 * The handler
	 */
	public ChartLocator(DesignerContext ctx) {
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.context = ctx;
	}

	/**
	 * Display a chart given its resourceId.
	 */
	public void locate(ResourcePath chartPath) {
		SFCDesignerHook hook = (SFCDesignerHook)context.getModule(SFCModule.MODULE_ID);
		hook.getWorkspace().openChart(chartPath);
	}
}
