package com.ils.sfc.step;

import com.inductiveautomation.sfc.api.Chart;
import com.inductiveautomation.sfc.api.CompletionListener;
import com.inductiveautomation.sfc.api.ExecutionQueue;
import com.inductiveautomation.sfc.api.PyChartScope;

public class StubChart implements Chart {
	PyChartScope scope = new PyChartScope();
	
	@Override
	public void abort(Throwable arg0, Runnable arg1) {}

	@Override
	public void cancel(Runnable arg0) {}

	@Override
	public void pause(Runnable arg0) {}

	@Override
	public void resume(Runnable arg0) {}

	@Override
	public void start(Runnable arg0) {}

	@Override
	public void addCompletionListener(CompletionListener<Chart> arg0) {}

	@Override
	public PyChartScope getChartScope() {return scope;}

	@Override
	public ExecutionQueue getExecutionQueue() {return new ExecutionQueue(null);}

	@Override
	public void removeCompletionListener(CompletionListener<Chart> arg0) {}

}
