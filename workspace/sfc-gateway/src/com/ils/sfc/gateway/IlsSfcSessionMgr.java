package com.ils.sfc.gateway;

import java.util.ArrayList;
import java.util.Collection;

import static java.util.Collections.synchronizedMap;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import org.python.core.PyDictionary;
import org.python.core.PyObject;
import org.python.core.PyString;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.PythonCall;
import com.inductiveautomation.ignition.common.Dataset;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.util.DatasetBuilder;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.ChartObserver;
import com.inductiveautomation.sfc.ChartStateEnum;
import com.inductiveautomation.sfc.ElementStateEnum;
import com.inductiveautomation.sfc.api.ChartContext;

/** This is the manager for the new thin client session design. 
 *  Eventually it will take over the functions of IlsDropBox and IlsChartObserver.
 *  
 *  Manage SFC sessions. A session is essentially the state associated with a single 
 *  run of a hierarchical SFC. In order to support dynamic disconnect and reconnect,
 *  a dynamic list of clients is maintained who listen for changes in the session
 *  state. The session is created before the chart is run, and persists after it ends.
 *  The information for a session is held in a ils.sfc.common.SfcSession Python 
 *  object, which appears here as a PyObject.
 */
public class IlsSfcSessionMgr implements ChartObserver {
	private static LoggerEx logger = LogUtil.getLogger(IlsSfcSessionMgr.class.getName());
	private Map<String, PyObject> sessionsById = synchronizedMap(new HashMap<String, PyObject>());
	private Map<String, PyObject> sessionsByRunId = synchronizedMap(new HashMap<String, PyObject>());
	private Map<String,List<String>> clientsBySessionId = synchronizedMap(new HashMap<String,List<String>>());
	
	/** Add a new session. */
	public void addSession(PyObject session, String clientId) {
		String sessionId = getSessionId(session);
		logger.infof("adding session for id %s", sessionId);
		sessionsById.put(sessionId, session);
		String chartName = getChartName(session);;
		List<String> clientIds = new ArrayList<String>();
		clientsBySessionId.put(sessionId, clientIds);
		addClient(sessionId, clientId);
	}

	/** Get the session by its unique id. */
	public PyObject getSession(String sessionId) {
		return (PyObject) sessionsById.get(sessionId);
	}

	/** Delete a session. */
	public void removeSession(String sessionId) {
		PyObject session = getSession(sessionId);
		sessionsById.remove(sessionId);
		String runId = getRunId(session);
		sessionsByRunId.remove(runId);
		clientsBySessionId.remove(sessionId);
	}
	
	/** Get the names and ids of the (top level) charts associated with the sessions. */
	
	public Dataset getSessionData() {
		DatasetBuilder builder = new DatasetBuilder();
		builder.colNames("Chart Name", "Id");
		builder.colTypes(String.class, String.class);
		for(PyObject session: sessionsById.values()) {
			String sessionId = getSessionId(session);
			String chartName = getChartName(session);
			builder.addRow(new Object[]{chartName, sessionId});
		}
		return builder.build();
	}
	
	/** Add the given client as a listener for the session associated with the
	 *  given chart name. CAUTION: nothing prevents there being multiple sessions
	 *  for the same chart; if that is the case the choice is arbitrary.
	 */
	public PyObject addClientForChart(String sessionId, String clientId) {
		PyObject session = getSession(sessionId);
		addClient(sessionId, clientId);
		return session;
	}
	
	/** Associate a (top-level) SFC chart run with a session. */
	private void associateChartRun(String sessionId, String runId) {
		logger.infof("associating runId %s with sessionId %s", runId,  sessionId);
		PyObject session = getSession(sessionId);
		sessionsByRunId.put(runId, session);
	}

	/** Get the session for the given (top-level) SFC chart run. */
	private PyObject getSessionForRun(String runId) {
		return sessionsByRunId.get(runId);
	}

	/** Add a client (as a change listener). */
	private void addClient(String sessionId, String clientId) {
		clientsBySessionId.get(sessionId).add(clientId);		
	}

	/** Notify all registered clients of a change to the session. */
	private void updateClientSessions(String sessionId) {
		List<String> clientIds = clientsBySessionId.get(sessionId);
		PyObject session = getSession(sessionId);
		try {
			PythonCall.UPDATE_SESSION.exec(session, clientIds);
		} catch (JythonExecException e) {
			logger.error("error updating client sessions", e);
		}
	}

	/** Get the unique id of the given session. */
	private String getSessionId(PyObject session) {
		return session.__getattr__("sessionId").toString();
	}

	/** Get the chart name of the given session. */
	private String getChartName(PyObject session) {
		return session.__getattr__("chartName").toString();
	}

	/** Get the chart run id of the given session. */
	private String getRunId(PyObject session) {
		return session.__getattr__("chartRunId").toString();
	}
	
	@Override
	public void onBeforeChartStart(ChartContext chartContext) {
		PyDictionary chartScope = chartContext.getChartScope();
		String sessionId = (String)chartScope.get("sessionId");
		if(sessionId != null) {
			String runId = (String)chartScope.get(Constants.INSTANCE_ID);
			PyObject session = getSession(sessionId);
			session.__setattr__("chartRunId", new PyString(runId));	

			associateChartRun(sessionId, runId);
		}
	}

	@Override
	public void onChartStateChange(UUID chartId, ChartStateEnum oldChartState,
			ChartStateEnum newChartState) {
		String runIdAsString = chartId.toString();
		PyObject session = getSessionForRun(runIdAsString);
		if(session == null) {
			//TODO: remove suppression when this is in production
			//logger.error("session not found for runId " + runIdAsString);
			return;
		}
		
		String sessionId = session.__getattr__("sessionId").toString();
		String chartStateString = newChartState.toString();
		logger.infof("setting chart state %s", chartStateString);
		session.__setattr__("chartStatus", new PyString(chartStateString));	
		updateClientSessions(sessionId);
	}

	@Override
	public void onElementStateChange(UUID arg0, UUID arg1,
			ElementStateEnum arg2, ElementStateEnum arg3) {
		// TODO Auto-generated method stub
		
	}

}
