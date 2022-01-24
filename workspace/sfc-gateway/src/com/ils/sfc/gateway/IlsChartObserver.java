package com.ils.sfc.gateway;

import java.util.List;
import java.util.UUID;

import org.json.JSONObject;
import org.python.core.PyDictionary;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.recipe.objects.Data;
import com.inductiveautomation.ignition.common.config.PropertySet;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.ChartObserver;
import com.inductiveautomation.sfc.ChartStateEnum;
import com.inductiveautomation.sfc.ElementStateEnum;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.PyChartScope;
import com.inductiveautomation.sfc.api.elements.ChartElement;
import com.inductiveautomation.sfc.api.elements.StepElement;
import com.inductiveautomation.sfc.elements.StepContainer;

/** An observer that listens to SFC chart status changes and messages the client
 *  so that the ControlPanel status display (e.g.) can stay up to date. */
public class IlsChartObserver implements ChartObserver {
	private static LoggerEx logger = LogUtil.getLogger(IlsChartObserver.class.getName());

	@Override
	public synchronized void onBeforeChartStart(ChartContext chartContext) {

	}
	
	@Override
	public void onChartStateChange(UUID arg0, ChartStateEnum arg1,
			ChartStateEnum arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onElementStateChange(UUID arg0, UUID arg1,
			ElementStateEnum arg2, ElementStateEnum arg3) {
		// TODO Auto-generated method stub
		
	}

}