package com.ils.sfc.util;

import java.util.ArrayList;
import java.util.List;

import org.python.core.CompileMode;
import org.python.core.CompilerFlags;
import org.python.core.Py;
import org.python.core.PyCode;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyStringMap;

import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.script.ScriptManager;

/** An object that can call a particular method in Python. 
 *  Also holds static objects for particular calls. */
public class PythonCall {
	private static final String RESULT_NAME = "pyCallResult";
	private final String methodName; // package + method name
	private final String[] argNames; // args to the method, if any
	private final Class<?> returnType;	// is null if no return value
	private PyCode compiledCode;	// cached compiled code
	
	private static ScriptManager scriptMgr;
	private static final String[] stepArgs = new String[]{"chartScope", "stepProperties"};
	
	public static final PythonCall QUEUE_INSERT = new PythonCall("ils.sfc.steps.queueInsert", 
		null, stepArgs);

	public static final PythonCall CLEAR_QUEUE = new PythonCall("ils.sfc.steps.clearQueue", 
		null, stepArgs);

	public static final PythonCall SET_QUEUE = new PythonCall("ils.sfc.steps.setQueue", 
		null, stepArgs);

	public static final PythonCall SHOW_QUEUE = new PythonCall("ils.sfc.steps.showQueue", 
		null, stepArgs);

	public static final PythonCall YES_NO = new PythonCall("ils.sfc.steps.yesNo", 
			null, stepArgs);

	public static final PythonCall ABORT = new PythonCall("ils.sfc.steps.abort", 
			null, stepArgs);

	public static final PythonCall PAUSE = new PythonCall("ils.sfc.steps.pause", 
			null, stepArgs);

	public static final PythonCall CONTROL_PANEL_MESSAGE = new PythonCall("ils.sfc.steps.controlPanelMessage", 
			null, stepArgs);

	public static final PythonCall TIMED_DELAY = new PythonCall("ils.sfc.steps.timedDelay", 
			null, stepArgs);

	public static final PythonCall OTHER_UNITS = new PythonCall("ils.common.units.unitsOfSameType", 
			PyList.class,  new String[]{"unit"} );

	public static final PythonCall DELETE_DELAY_NOTIFICATION = new PythonCall("ils.sfc.steps.deleteDelayNotifications", 
			PyList.class, stepArgs );

	public static final PythonCall POST_DELAY_NOTIFICATION = new PythonCall("ils.sfc.steps.postDelayNotification", 
			PyList.class,  stepArgs );

	public static final PythonCall ENABLE_DISABLE = new PythonCall("ils.sfc.steps.enableDisable", 
			PyList.class, stepArgs );

	public static final PythonCall SELECT_INPUT = new PythonCall("ils.sfc.steps.selectInput", 
			PyList.class, stepArgs );

	public static final PythonCall GET_LIMITED_INPUT = new PythonCall("ils.sfc.steps.getLimitedInput", 
			PyList.class, stepArgs );

	public static final PythonCall DIALOG_MESSAGE = new PythonCall("ils.sfc.steps.dialogMessage", 
			PyList.class, stepArgs );

	public static final PythonCall COLLECT_DATA = new PythonCall("ils.sfc.steps.collectData", 
			PyList.class, stepArgs );

	public static final PythonCall GET_INPUT = new PythonCall("ils.sfc.steps.getInput", 
			PyList.class, stepArgs );

	public static final PythonCall RAW_QUERY = new PythonCall("ils.sfc.steps.rawQuery", 
			PyList.class, stepArgs );

	public static final PythonCall SIMPLE_QUERY = new PythonCall("ils.sfc.steps.simpleQuery", 
			PyList.class, stepArgs );

	public static final PythonCall SAVE_DATA = new PythonCall("ils.sfc.steps.saveData", 
			PyList.class, stepArgs );

	public static final PythonCall PRINT_FILE = new PythonCall("ils.sfc.steps.printFile", 
			PyList.class, stepArgs );

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
		if(compiledCode == null) {
			compileCode();
		}
		PyStringMap localMap = scriptMgr.createLocalsMap();
		PyStringMap globalsMap = scriptMgr.getGlobals();
		for(int i = 0; i < argNames.length; i++) {
			localMap.__setitem__(argNames[i], Py.java2py(argValues[i]));
		}
		scriptMgr.runCode(compiledCode, localMap, globalsMap);
		PyObject pyResult = localMap.__getitem__(RESULT_NAME);
		return returnType != null ? pyResult.__tojava__(returnType) : null;
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
