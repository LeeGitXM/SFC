/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */
package com.ils.sfc.browser;

import java.util.ArrayList;
import java.util.List;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.model.DesignerModuleHook;
import com.inductiveautomation.sfc.designer.SFCDesignerHook;
import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;


public class IlsSfcBrowserHook extends AbstractDesignerModuleHook implements DesignerModuleHook {
	private final static String TAG = "IlsSfcBrowserHook";
	private DesignerContext context = null;
	private final LoggerEx log;
	private IlsBrowserFrame browser = null;
	private SFCDesignerHook iaSfcHook = null;

	
	public IlsSfcBrowserHook() {
		log = LogUtil.getLogger(getClass().getPackage().getName());
	}
		
	@Override
	public List<DockableFrame> getFrames() {
		// Add a frame for our custom chart browser
       	List<DockableFrame> frames = new ArrayList<>();
       	browser = new IlsBrowserFrame(context);
       	browser.setInitMode(DockContext.STATE_AUTOHIDE);
       	browser.setInitSide(DockContext.DOCK_SIDE_WEST);
       	browser.setInitIndex(1);
       	frames.add(browser);
       	return frames;
	}
	
	// We would like to add the browser to the Standard SFC frames here, but the 
	// IA SFC Hook doesn't have its frames created yet, plus it's not clear that you
	// can augment another module's frames.
	@Override
	public void startup(DesignerContext ctx, LicenseState activationState) throws Exception {
		this.context = ctx;
        log.infof("%s.startup...",TAG);
	}

	@Override
	public void shutdown() {
		iaSfcHook.getFrames().remove(browser);
	}

}
