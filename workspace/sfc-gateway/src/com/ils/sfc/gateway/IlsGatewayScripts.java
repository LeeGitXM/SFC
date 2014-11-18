package com.ils.sfc.gateway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.python.core.PyDictionary;
import org.python.core.PyList;

import com.google.common.base.Optional;
import com.inductiveautomation.sfc.ChartManager;
import com.inductiveautomation.sfc.ChartStateEnum;
import com.inductiveautomation.sfc.rpc.ChartStatus;
import com.inductiveautomation.sfc.scripting.ChartInfo;

/** Handles receiving and caching SFC-related responses from clients.
 *  This was implemented in Java because I couldn't get a persistent global in Jython */
// TODO: should responses be persisted, so they survive a gateway restart??
// TODO: if messages are multicast, multiple responses could complicate things,
// or at least inflate memory if not "claimed"
public class IlsGatewayScripts {	
	private static Map<String,PyDictionary> repliesById = Collections.synchronizedMap(
		new HashMap<String,PyDictionary>());
	private static ChartManager chartManager = ChartManager.get();
	private static String sfcProjectName = null;
	private static String sfcDatabaseName = null;
	
	public static void setSfcProjectInfo(String project, String database) {
		sfcProjectName = project;
		sfcDatabaseName = database;
	}
	
	public static String getSfcProjectName() {
		return sfcProjectName;
	}

	public static String getSfcDatabaseName() {
		return sfcDatabaseName;
	}

	public static String getChartState(UUID uuid) {
		Optional<ChartStatus> opt = chartManager.getChartStatus(uuid, false);
		if(opt.get() != null) {
			ChartStatus chartStatus = opt.get();
			ChartStateEnum chartState = chartStatus.getChartState();
			return chartState.toString();
		}
		else {
			return null;
		}
	}
	
	public static List<ChartInfo> getRunningCharts() {
		return chartManager.getRunningCharts();
	}
	
	public static PyDictionary getResponse(String id) {
		PyDictionary reply = repliesById.get(id);
		if(reply != null) {
			repliesById.remove(id);
		}
		return reply;
	}
	
	public static void setResponse(String id, PyDictionary payload) {
		repliesById.put(id, payload);
	}
	
}
