package com.ils.sfc.util;

import org.python.core.CompileMode;
import org.python.core.CompilerFlags;
import org.python.core.Py;
import org.python.core.PyCode;
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

	public static final PythonCall QUEUE_INSERT = new PythonCall("ils.queue.message.insert", 
		null, new String[]{"queue", "status", "message"});
	
	public PythonCall(String methodName, Class<?> returnType, String...args) {
		this.methodName = methodName;
		this.argNames = args;
		this.returnType = returnType;
	}
	
	/** Execute this method and return the result. */
	public Object exec(Object...argValues) throws JythonExecException {
		if(compiledCode == null) {
			compileCode();
		}
		PyStringMap map = scriptMgr.createLocalsMap();
		for(int i = 0; i < argNames.length; i++) {
			map.__setitem__(argNames[i], Py.java2py(argValues[i]));
		}
		scriptMgr.runCode(compiledCode, map);
		PyObject pyResult = map.__getitem__(RESULT_NAME);
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
	
}
