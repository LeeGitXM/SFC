package com.ils.sfc.step;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.sfc.api.Chart;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ChartElement;
import com.inductiveautomation.sfc.api.PyChartScope;

public class StubChartContext implements ChartContext {
	private StubChart chart = new StubChart();
	
	@Override
	public Chart getChart() {
		return chart;
	}

	@Override
	public PyChartScope getChartScope() {
		return chart.getChartScope();
	}

	@Override
	public ChartElement getElement(UUID arg0) {
		return null;
	}

	@Override
	public ExecutorService getExecutorService() {
		return null;
	}

	@Override
	public GatewayContext getGatewayContext() {
		return null;
	}

	@Override
	public ScriptManager getScriptManager() {
		return null;
	}

	@Override
	public void putElement(UUID arg0, ChartElement arg1) {}
};
