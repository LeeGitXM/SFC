
package com.ils.sfc.common;

import org.python.core.CompileMode;
import org.python.core.CompilerFlags;
import org.python.core.Py;
import org.python.core.PyCode;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyStringMap;

import com.inductiveautomation.ignition.common.Dataset;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.ScopeContext;

/** An object that can call a particular method in Python. 
 *  Also holds static singletons for particular calls. Each
 *  single */
public class PythonCall {
	private static final LoggerEx logger = LogUtil.getLogger(PythonCall.class.getName());
	private static final String RESULT_NAME = "pyCallResult";
	private final String methodName; // package + method name
	private final String[] argNames; // args to the method, if any
	private final Class<?> returnType;	// is null if no return value
	private PyCode compiledCode;	// cached compiled code

	private static ScriptManager scriptMgr;
	private static final String[] stepArgs = new String[]{"scopeContext", "stepProperties", "state"};
	private static final String STEPS_PKG = "ils.sfc.gateway.steps.";

	// steps:
	public static final PythonCall PROCEDURE = new PythonCall(STEPS_PKG + "procedure.activate", 
			Boolean.class, stepArgs);
	
	public static final PythonCall OPERATION = new PythonCall(STEPS_PKG + "operation.activate", 
		Boolean.class, stepArgs);
	
	public static final PythonCall MONITOR_PV = new PythonCall(STEPS_PKG + "monitorPV.activate", 
		Boolean.class, stepArgs);

	public static final PythonCall MANUAL_DATA_ENTRY = new PythonCall(STEPS_PKG + "manualDataEntry.activate", 
		Boolean.class, stepArgs);

	public static final PythonCall MONITOR_DOWNLOAD = new PythonCall(STEPS_PKG + "monitorDownload.activate", 
		Boolean.class, stepArgs);

	public static final PythonCall QUEUE_INSERT = new PythonCall(STEPS_PKG + "queueInsert.activate", 
		Boolean.class, stepArgs);

	public static final PythonCall CLEAR_QUEUE = new PythonCall(STEPS_PKG + "clearQueue.activate", 
		Boolean.class, stepArgs);

	public static final PythonCall SET_QUEUE = new PythonCall(STEPS_PKG + "setQueue.activate", 
		Boolean.class, stepArgs);

	public static final PythonCall SHOW_QUEUE = new PythonCall(STEPS_PKG + "showQueue.activate", 
		Boolean.class, stepArgs);

	public static final PythonCall SAVE_QUEUE = new PythonCall(STEPS_PKG + "saveQueue.activate", 
		Boolean.class, stepArgs );

	public static final PythonCall YES_NO = new PythonCall(STEPS_PKG + "yesNo.activate", 
		Boolean.class, stepArgs);

	public static final PythonCall CANCEL = new PythonCall(STEPS_PKG + "cancel.activate", 
		Boolean.class, stepArgs);

	public static final PythonCall PAUSE = new PythonCall(STEPS_PKG + "pause.activate", 
		Boolean.class, stepArgs);

	public static final PythonCall CONTROL_PANEL_MESSAGE = new PythonCall(STEPS_PKG + "controlPanelMsg.activate", 
		Boolean.class, stepArgs);

	public static final PythonCall TIMED_DELAY = new PythonCall(STEPS_PKG + "timedDelay.activate", 
		Boolean.class, stepArgs);

	public static final PythonCall DELETE_DELAY_NOTIFICATION = new PythonCall(STEPS_PKG + "deleteDelay.activate", 
		Boolean.class, stepArgs );

	public static final PythonCall POST_DELAY_NOTIFICATION = new PythonCall(STEPS_PKG + "postDelay.activate", 
		Boolean.class,  stepArgs );

	public static final PythonCall ENABLE_DISABLE = new PythonCall(STEPS_PKG + "enableDisable.activate", 
		Boolean.class, stepArgs );

	public static final PythonCall SELECT_INPUT = new PythonCall(STEPS_PKG + "selectInput.activate", 
		Boolean.class, stepArgs );

	public static final PythonCall GET_LIMITED_INPUT = new PythonCall(STEPS_PKG + "limitedInput.activate", 
		Boolean.class, stepArgs );

	public static final PythonCall DIALOG_MESSAGE = new PythonCall(STEPS_PKG + "dialogMsg.activate", 
		Boolean.class, stepArgs );

	public static final PythonCall COLLECT_DATA = new PythonCall(STEPS_PKG + "collectData.activate", 
		Boolean.class, stepArgs );

	public static final PythonCall GET_INPUT = new PythonCall(STEPS_PKG + "getInput.activate", 
		Boolean.class, stepArgs );

	public static final PythonCall RAW_QUERY = new PythonCall(STEPS_PKG + "rawQuery.activate", 
		Boolean.class, stepArgs );

	public static final PythonCall SIMPLE_QUERY = new PythonCall(STEPS_PKG + "simpleQuery.activate", 
		Boolean.class, stepArgs );

	public static final PythonCall SAVE_DATA = new PythonCall(STEPS_PKG + "saveData.activate", 
		Boolean.class, stepArgs );

	public static final PythonCall PRINT_FILE = new PythonCall(STEPS_PKG + "printFile.activate", 
		Boolean.class, stepArgs );

	public static final PythonCall PRINT_WINDOW = new PythonCall(STEPS_PKG + "printWindow.activate", 
		Boolean.class, stepArgs );

	public static final PythonCall SHOW_WINDOW = new PythonCall(STEPS_PKG + "showWindow.activate", 
		Boolean.class, stepArgs );

	public static final PythonCall CLOSE_WINDOW = new PythonCall(STEPS_PKG + "closeWindow.activate", 
		Boolean.class, stepArgs );

	public static final PythonCall REVIEW_DATA = new PythonCall(STEPS_PKG + "reviewData.activate", 
		Boolean.class, stepArgs );

	public static final PythonCall REVIEW_FLOWS = new PythonCall(STEPS_PKG + "reviewFlows.activate", 
		Boolean.class, stepArgs );

	public static final PythonCall CONFIRM_CONTROLLERS = new PythonCall(STEPS_PKG + "confirmControllers.activate", 
		Boolean.class, stepArgs );

	public static final PythonCall WRITE_OUTPUT = new PythonCall(STEPS_PKG + "writeOutput.activate", 
		Boolean.class, stepArgs );

	// units
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
	
	public static final PythonCall GET_UNITS = new PythonCall("ils.common.units.getUnits", 
			Dataset.class,  new String[]{} );
	
	// recipe data:
	public static final PythonCall GET_INDEXED_VALUE = new PythonCall("ils.sfc.gateway.recipe." + "getIndexedValue", 
			Object.class,  new String[]{"tagPath", "database", "rowIndexStr", "colIndexStr"} );

	public static final PythonCall RECIPE_DATA_EXISTS = new PythonCall("ils.sfc.common.recipe." + "recipeDataTagExists", 
			Object.class,  new String[]{"provider", "path"} );

	public static final PythonCall GET_RECIPE_DATA = new PythonCall("ils.sfc.common.recipe." + "getRecipeData", 
			Object.class,  new String[]{"provider", "path"} );

	public static final PythonCall GET_RECIPE_DATA_BATCH = new PythonCall("ils.sfc.common.recipe." + "getRecipeDataBatch", 
			Object.class,  new String[]{"provider", "paths"} );

	public static final PythonCall SET_RECIPE_DATA = new PythonCall("ils.sfc.common.recipe." + "setRecipeData", 
			null,  new String[]{"provider", "path", "value", "synchronous"} );
	
	public static final PythonCall CLEANUP_RECIPE_DATA = new PythonCall("ils.sfc.common.recipe." + "cleanupRecipeData", 
			null,  new String[]{"provider", "chartPath", "stepNames"} );

	public static final PythonCall CREATE_RECIPE_DATA = new PythonCall("ils.sfc.common.recipe." + "createRecipeDataTag", 
			null,  new String[]{"provider", "folder", "rdName", "rdType", "valueType"} );

	public static final PythonCall CREATE_GROUP_PROPERTY_TAG = new PythonCall("ils.sfc.common.recipe." + "createGroupPropertyTag", 
			null,  new String[]{"provider", "folder", "rdName"} );

	public static final PythonCall DELETE_RECIPE_DATA = new PythonCall("ils.sfc.common.recipe." + "deleteRecipeDataTag", 
			null,  new String[]{"provider", "fullPath"} );

	// misc:
	public static final PythonCall DO_NOTHING = new PythonCall("ils.sfc.common.util." + "doNothing", 
			null,  new String[0] );

	public static final PythonCall GET_QUEUE_NAMES = new PythonCall("ils.queue.commons.getQueueNames", 
			PyList.class, new String[]{"db"});

	public static final PythonCall HANDLE_STEP_ERROR = new PythonCall("ils.sfc.gateway.util." + "handleUnexpectedGatewayError", 
			PyList.class,  new String[]{"chartProps", "msg"} );

	public static final PythonCall GET_INDEX_NAMES = new PythonCall("ils.sfc.client.util." + "getIndexNames", 
			PyList.class,  new String[]{} );

	public static final PythonCall GET_KEY_SIZE = new PythonCall("ils.sfc.client.util." + "getKeySize", 
			Integer.class,  new String[]{"keyName"} );


	//public static final PythonCall INVOKE_STEP = new PythonCall("ils.sfc.gateway.steps." + "invokeStep", 
	//		PyList.class,  new String[]{"chartProperties", "stepProperties", "methodName"} );

	public static final PythonCall TEST_QUERY = new PythonCall("ils.sfc.client.util." + "testQuery", 
			PyList.class,  new String[]{"query", "isolationMode"} );

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
		PyStringMap localMap = scriptMgr.createLocalsMap();

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
