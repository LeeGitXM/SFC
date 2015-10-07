/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */
package com.ils.sfc.browser;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;

import com.ils.sfc.browser.execute.ChartRunner;
import com.ils.sfc.browser.validation.ValidationDialog;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.modules.ModuleInfo;
import com.inductiveautomation.ignition.common.modules.ModuleInfo.ModuleDependency;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.model.DesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.menu.JMenuMerge;
import com.inductiveautomation.ignition.designer.model.menu.MenuBarMerge;
import com.inductiveautomation.ignition.designer.model.menu.WellKnownMenuConstants;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.designer.SFCDesignerHook;
import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;


public class SfcBrowserDesignerHook extends AbstractDesignerModuleHook implements DesignerModuleHook {
	private final static String TAG = "IlsSfcBrowserHook";
	private static final String START_MENU_TITLE      = "Start Chart";
	private static final String VALIDATION_MENU_TITLE = "Validate Charts";
	private DesignerContext context = null;
	private final LoggerEx log;
	private SfcBrowserFrame browser = null;
	private SFCDesignerHook iaSfcHook = null;

	
	public SfcBrowserDesignerHook() {
		log = LogUtil.getLogger(getClass().getPackage().getName());
	}
		
	@Override
	public List<DockableFrame> getFrames() {
		// Add a frame for our custom chart browser
       	List<DockableFrame> frames = new ArrayList<>();
       	browser = new SfcBrowserFrame(context);
       	browser.setInitMode(DockContext.STATE_AUTOHIDE);
       	browser.setInitSide(DockContext.DOCK_SIDE_WEST);
       	browser.setInitIndex(1);
       	frames.add(browser);
       	return frames;
	}
	
	// We would like to add the browser to the Standard SFC frames here, but the 
	// IA SFC Hook doesn't have its frames created yet, plus it's not clear that you
	// can augment another module's frames -- so we make our own frame.
	@Override
	public void startup(DesignerContext ctx, LicenseState activationState) throws Exception {
		this.context = ctx;
        log.infof("%s.startup...",TAG);
        new Thread(new ModuleWatcher(context)).start();             // Watch for modules to start
	}

	@Override
	public void shutdown() {
		iaSfcHook.getFrames().remove(browser);
	}
	// Insert a menu to allow control of database and tag provider.
    @Override
    public MenuBarMerge getModuleMenu() {

        MenuBarMerge merge = new MenuBarMerge(BrowserConstants.MODULE_ID);  // as suggested in javadocs
        
        // ----------------------- Menu to launch chart validator -----------------------------
        Action validateAction = new AbstractAction(VALIDATION_MENU_TITLE) {
            private static final long serialVersionUID = 5374667367733312464L;
            public void actionPerformed(ActionEvent ae) {
                SwingUtilities.invokeLater(new ValidationDialogRunner());
            }
        };
        JMenuMerge controlMenu = new JMenuMerge(WellKnownMenuConstants.VIEW_MENU_NAME);
        controlMenu.addSeparator();
        controlMenu.addSeparator();   // Sometimes this makes one show up. Have never seen this doubled.
        controlMenu.add(validateAction);
        merge.add(WellKnownMenuConstants.VIEW_MENU_LOCATION, controlMenu);
        
        // ----------------------- Menu to start current chart -----------------------------
        Action executeAction = new AbstractAction(START_MENU_TITLE) {
            private static final long serialVersionUID = 5374667367733312464L;
            public void actionPerformed(ActionEvent ae) {
            	SFCDesignerHook iaSfcHook = (SFCDesignerHook)context.getModule(SFCModule.MODULE_ID);
                Thread runner = new Thread(new ChartRunner(context,iaSfcHook.getWorkspace(),browser.getModel()));
                runner.start();
            }
        };
        JMenuMerge executeMenu = new JMenuMerge(WellKnownMenuConstants.TOOLS_MENU_NAME);
        executeMenu.addSeparator();
        executeMenu.add(executeAction);
        merge.add(WellKnownMenuConstants.TOOLS_MENU_LOCATION, executeMenu);
        return merge;
    }
	/**
	 * We are dependent on the Ignition SFC module, but don't know about other modules that
	 * also may be dependent on "com.inductiveautomation.sfc". In order for any custom chart
	 * classes to be registered, we need to wait on those modules also
	 */
	private class ModuleWatcher implements Runnable {
		private final DesignerContext ctx;
		public ModuleWatcher(DesignerContext dc) {
			this.ctx = dc;
		}
		public void run() {
			boolean ready = false;
			while( !ready ) {
				List<ModuleInfo> moduleInfos = ctx.getModules();
				for( ModuleInfo minfo:moduleInfos ) {
					Collection<ModuleDependency> dependencies = minfo.getDependencies().values();
					for(ModuleDependency dep:dependencies) {
						if( dep.getModuleId().equals(SFCModule.MODULE_ID) ) {
							log.infof("%s.MainMenuWatcher ...%s depends on %s",TAG,minfo.getName(),SFCModule.MODULE_ID);
						}
					}
					// Don't really know how to wait until module is ready. We just assume it
					// works by letting whatever calls startup() finish.
					try { Thread.sleep( 2000 ); }
					catch (InterruptedException ignore) {}
				}
				ready = true;
			}
			ctx.addProjectChangeListener(browser);
		}
	}
	
	/**
     * Display a popup dialog for configuration of dialog execution parameters.
     * Run in a separate thread, as a modal dialog in-line here will freeze the UI.
     */
    private class ValidationDialogRunner implements Runnable {

        public void run() {
            log.debugf("%s.Launching setup dialog...",TAG);
            ValidationDialog validator = new ValidationDialog(context,browser.getModel());
            validator.pack();
            validator.setVisible(true);
            browser.addChangeListener(validator);
        }
    }
}
