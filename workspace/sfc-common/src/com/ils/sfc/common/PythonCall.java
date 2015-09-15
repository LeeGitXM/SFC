package com.ils.sfc.common;

import org.python.core.CompileMode;
import org.python.core.CompilerFlags;
import org.python.core.Py;
import org.python.core.PyCode;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyStringMap;

import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.ScopeContext;

/** An object that can call a particular method in Python. 
 *  Also holds static objects for particular calls. */
public class PythonCall {
	private static final LoggerEx logger = LogUtil.getLogger(PythonCall.class.getName());
	private static final String RESULT_NAME = "pyCallResult";
	private final String methodName; // package + method name
	private final String[] argNames; // args to the method, if any
	private final Class<?> returnType;	// is null if no return value
	private PyCode compiledCode;	// cached compiled code
	private static PyStringMap localMap;

	private static ScriptManager scriptMgr;
	private static final String[] stepArgs = new String[]{"scopeContext", "stepProperties"};
	private static final String STEPS_PKG = "ils.sfc.gateway.steps.";

	public static final PythonCall MONITOR_PV = new PythonCall(STEPS_PKG + "monitorPV", 
			null, stepArgs);

	public static final PythonCall MANUAL_DATA_ENTRY = new PythonCall(STEPS_PKG + "manualDataEntry", 
			null, stepArgs);

	public static final PythonCall MONITOR_DOWNLOAD = new PythonCall(STEPS_PKG + "monitorDownload", 
			null, stepArgs);

	public static final PythonCall QUEUE_INSERT = new PythonCall(STEPS_PKG + "queueInsert", 
		null, stepArgs);

	public static final PythonCall CLEAR_QUEUE = new PythonCall(STEPS_PKG + "clearQueue", 
		null, stepArgs);

	public static final PythonCall SET_QUEUE = new PythonCall(STEPS_PKG + "setQueue", 
		null, stepArgs);

	public static final PythonCall SHOW_QUEUE = new PythonCall(STEPS_PKG + "showQueue", 
		null, stepArgs);

	public static final PythonCall GET_QUEUE_NAMES = new PythonCall("ils.queue.commons.getQueueNames", 
			PyList.class, new String[]{"db"});

	public static final PythonCall SAVE_QUEUE = new PythonCall(STEPS_PKG + "saveQueue", 
			PyList.class, stepArgs );

	public static final PythonCall YES_NO = new PythonCall(STEPS_PKG + "yesNo", 
			null, stepArgs);

	public static final PythonCall CANCEL = new PythonCall(STEPS_PKG + "cancel", 
			null, stepArgs);

	public static final PythonCall PAUSE = new PythonCall(STEPS_PKG + "pause", 
			null, stepArgs);

	public static final PythonCall CONTROL_PANEL_MESSAGE = new PythonCall(STEPS_PKG + "controlPanelMessage", 
			null, stepArgs);

	public static final PythonCall TIMED_DELAY = new PythonCall(STEPS_PKG + "timedDelay", 
			null, stepArgs);

	public static final PythonCall INITIALIZE_UNITS = new PythonCall("ils.common.units.lazyInitialize", 
			null,  new String[]{"database"} );
	
	public static final PythonCall OTHER_UNITS = new PythonCall("ils.common.units.unitsOfSameType", 
			PyList.class,  new String[]{"unit"} );

	public static final PythonCall GET_UNIT_TYPES = new PythonCall("ils.common.units.getUnitTypes", 
			PyList.class,  new String[]{} );

	public static final PythonCall GET_TYPE_OF_UNIT = new PythonCall("ils.common.units.getTypeOfUnit", 
			String.class,  new String[]{"unitName"} );

	public static final PythonCall GET_UNITS_OF_TYPE = new PythonCall("ils.common.units.getUnitsOfType", 
			PyList.class,  new String[]{"unitName"} );

	public static final PythonCall CONVERT_UNITS = new PythonCall("ils.common.units.convert", 
			Double.class,  new String[]{"fromUnit", "toUnit", "value"} );

	public static final PythonCall DELETE_DELAY_NOTIFICATION = new PythonCall(STEPS_PKG + "deleteDelayNotifications", 
			PyList.class, stepArgs );

	public static final PythonCall POST_DELAY_NOTIFICATION = new PythonCall(STEPS_PKG + "postDelayNotification", 
			PyList.class,  stepArgs );

	public static final PythonCall ENABLE_DISABLE = new PythonCall(STEPS_PKG + "enableDisable", 
			PyList.class, stepArgs );

	public static final PythonCall SELECT_INPUT = new PythonCall(STEPS_PKG + "selectInput", 
			PyList.class, stepArgs );

	public static final PythonCall GET_LIMITED_INPUT = new PythonCall(STEPS_PKG + "getLimitedInput", 
			PyList.class, stepArgs );

	public static final PythonCall DIALOG_MESSAGE = new PythonCall(STEPS_PKG + "dialogMessage", 
			PyList.class, stepArgs );

	public static final PythonCall COLLECT_DATA = new PythonCall(STEPS_PKG + "collectData", 
			PyList.class, stepArgs );

	public static final PythonCall GET_INPUT = new PythonCall(STEPS_PKG + "getInput", 
			PyList.class, stepArgs );

	public static final PythonCall RAW_QUERY = new PythonCall(STEPS_PKG + "rawQuery", 
			PyList.class, stepArgs );

	public static final PythonCall SIMPLE_QUERY = new PythonCall(STEPS_PKG + "simpleQuery", 
			PyList.class, stepArgs );

	public static final PythonCall SAVE_DATA = new PythonCall(STEPS_PKG + "saveData", 
			PyList.class, stepArgs );

	public static final PythonCall PRINT_FILE = new PythonCall(STEPS_PKG + "printFile", 
			PyList.class, stepArgs );

	public static final PythonCall PRINT_WINDOW = new PythonCall(STEPS_PKG + "printWindow", 
			PyList.class, stepArgs );

	public static final PythonCall SHOW_WINDOW = new PythonCall(STEPS_PKG + "showWindow", 
			PyList.class, stepArgs );

	public static final PythonCall CLOSE_WINDOW = new PythonCall(STEPS_PKG + "closeWindow", 
			PyList.class, stepArgs );

	public static final PythonCall REVIEW_DATA = new PythonCall(STEPS_PKG + "reviewData", 
			PyList.class, stepArgs );

	public static final PythonCall CONFIRM_CONTROLLERS = new PythonCall(STEPS_PKG + "confirmControllers", 
			PyList.class, stepArgs );

	public static final PythonCall WRITE_OUTPUT = new PythonCall(STEPS_PKG + "writeOutput", 
			PyList.class, stepArgs );

	public static final PythonCall HANDLE_STEP_ERROR = new PythonCall("ils.sfc.gateway.util." + "handleUnexpectedGatewayError", 
			PyList.class,  new String[]{"chartProps", "msg"} );

	public static final PythonCall SEND_CHART_STATUS = new PythonCall("ils.sfc.gateway.util." + "sendChartStatus", 
			PyList.class,  new String[]{"projectName", "payload"} );

	public static final PythonCall SEND_CURRENT_OPERATION = new PythonCall("ils.sfc.gateway.util." + "sendCurrentOperation", 
			PyList.class,  new String[]{"projectName", "payload"} );

	public static final PythonCall INVOKE_STEP = new PythonCall("ils.sfc.gateway.steps." + "invokeStep", 
		PyList.class,  new String[]{"chartProperties", "stepProperties", "methodName"} );

	public static final PythonCall TEST_QUERY = new PythonCall("ils.sfc.client.util." + "testQuery", 
			PyList.class,  new String[]{"query", "database"} );

	public static final PythonCall RECIPE_DATA_EXISTS = new PythonCall("ils.sfc.common.recipe." + "recipeDataTagExists", 
			Object.class,  new String[]{"provider", "path"} );

	public static final PythonCall GET_RECIPE_DATA = new PythonCall("ils.sfc.common.recipe." + "getRecipeData", 
			Object.class,  new String[]{"provider", "path"} );

	public static final PythonCall GET_RECIPE_DATA_BATCH = new PythonCall("ils.sfc.common.recipe." + "getRecipeDataBatch", 
			Object.class,  new String[]{"provider", "paths"} );

	public static final PythonCall SET_RECIPE_DATA = new PythonCall("ils.sfc.common.recipe." + "setRecipeData", 
			null,  new String[]{"provider", "path", "value", "synchronous"} );

	public static final PythonCall CREATE_RECIPE_DATA = new PythonCall("ils.sfc.common.recipe." + "createRecipeDataTag", 
			null,  new String[]{"provider", "folder", "rdName", "rdType", "valueType"} );

	public static final PythonCall DELETE_RECIPE_DATA = new PythonCall("ils.sfc.common.recipe." + "deleteRecipeDataTag", 
			null,  new String[]{"provider", "fullPath"} );

	public static final PythonCall DO_NOTHING = new PythonCall("ils.sfc.common.util." + "doNothing", 
			null,  new String[0] );

	public PythonCall(String methodName, Class<?> returnType, String...args) {
		this.methodName = methodName;
		this.argNames = args;
		this.returnType = returnType;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	/** Execute this method and return the result. */
	public Object exec(Object...argValues) throws JythonExecException {
		//System.out.println("python exec: " + methodName);
		if(compiledCode == null) {
			//System.out.println("   first call; compiling method");
			compileCode();
		}
		PyStringMap globalsMap = scriptMgr.getGlobals();
		for(int i = 0; i < argNames.length; i++) {
			localMap.__setitem__(argNames[i], Py.java2py(argValues[i]));
		}
		try {
			scriptMgr.runCode(compiledCode, localMap, globalsMap);
			if (returnType != null) {
				PyObject pyResult = localMap.__getitem__(RESULT_NAME);
				Object result = pyResult.__tojava__(returnType);
				return result;
			}
			else {
				return null;
			}
		}
		catch(JythonExecException ex) {
			if(this != HANDLE_STEP_ERROR) {  // avoid recursion
				Throwable lowestCause = getLowestCause(ex);
				String msg1 = lowestCause.getMessage();
				String msg2 = ex.toString();
				String msg = msg1 + "\n\n" + msg2;
				boolean isStepCode = argNames.length > 0 && "scopeContext".equals(argNames[0]);
				if(isStepCode) {
					ScopeContext scopeContext = (ScopeContext)argValues[0];
					HANDLE_STEP_ERROR.exec(scopeContext.getChartScope(), msg);
				}
				logger.error("Error invoking script : " + msg, ex);					
			}
			else {
				logger.error("Couldn't invoke handleUnexpectedError script : " + ex.toString(), ex);
			}
			return null;
		}

	}

	private Throwable getLowestCause(Throwable ex) {
		if( ex.getCause() == null || ex.getCause().equals(ex)) {
			return ex;
		}
		else {
			return getLowestCause(ex.getCause());
		}		
	}

	/** Compile and cache code to call this method. */
	private void compileCode() {
		StringBuffer buf = new StringBuffer();
		
		int dotIndex = methodName.lastIndexOf(".");
		if(dotIndex != -1) {
			buf.append("import ");
			buf.append(methodName.substring(0, dotIndex));
			buf.append("; ");
		}
		buf.append(RESULT_NAME);
		buf.append(" = ");
		buf.append(methodName);
		buf.append("(");
		for(int i = 0; i < argNames.length; i++) {
			if(i > 0) buf.append(',');
			buf.append(argNames[i]);
		}
		buf.append(')');
		String script = buf.toString();
		compiledCode = Py.compile_flags(script, "ils", CompileMode.exec, CompilerFlags.getCompilerFlags());		
	}

	public static void setScriptMgr(ScriptManager scriptManager) {
		scriptMgr = scriptManager;
		localMap = scriptMgr.createLocalsMap();
	}
	
	public static String[] toArray(Object o) {
		PyList pylist = (PyList)o;
		String[] array = new String[pylist.size()];
		for(int i = 0; i < pylist.size(); i++) {
			array[i] = (String)pylist.get(i);
		}
		return array;
	}
}
