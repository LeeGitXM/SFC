package com.ils.sfc.designer.runner;

import com.ils.sfc.common.PythonCall;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.designer.workspace.SfcDesignableContainer;
import com.inductiveautomation.sfc.designer.workspace.SfcWorkspace;

public class ChartRunner implements Runnable {
	private final static String CLSS = "ChartRunner";
	private final String projectName;
	private final boolean isolationMode;
	private final LoggerEx log;
	private final SfcWorkspace workspace;
	
	/**
	 * Constructor
	 */
	public ChartRunner(DesignerContext ctx, SfcWorkspace wksp, String pName, boolean isIsolation) {
		this.projectName = pName;
		this.isolationMode = isIsolation;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.workspace = wksp;
	}
		
	// ============================= Runnable ========================
	public void run() {
		String chartRunId = null;
		SfcDesignableContainer tab = workspace.getSelectedContainer();
		if( tab!=null ) {
			String chartPath = workspace.getSelectedContainer().getResourcePath().getFolderPath();
			log.infof("%s:running %s in project %s (Isolation: %s)...", CLSS, chartPath, projectName, isolationMode);
			
			// Single call to Python to do the whole shebang 
			try {
				log.infof("%s:Calling Python to run chart %s!", CLSS, chartPath);
				chartRunId = (String) PythonCall.RUN_CHART.exec(chartPath, isolationMode);
				log.infof("%s:...back from Python with %s!", CLSS, chartRunId);
			} 
			catch (JythonExecException jee) {
				log.errorf("%s: Error calling the Python chart runner for %s (%s)", CLSS, chartPath, jee.getMessage());
			}
			catch (Exception ex) {
				log.errorf("%s: Exception calling the Python chart runner for %s (%s)", CLSS, chartPath, ex.getMessage());
			}
		}
	}
}
