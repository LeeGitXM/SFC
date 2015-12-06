package com.ils.sfc.gateway;

import java.util.Set;

import static java.util.Collections.synchronizedMap;
import static java.util.Collections.synchronizedSet;

import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

import org.python.core.PyDictionary;
import org.python.core.PyObject;
import org.python.core.PyString;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.PythonCall;
import com.inductiveautomation.ignition.common.Dataset;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.script.message.MessageDispatchManager;
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
	private Set<ClientInfo> clients = synchronizedSet(new HashSet<ClientInfo>());
	private MessageDispatchManager msgMgr;
	public static final String CLIENT_MSG_DISPATCHER = "sfcMessage";
	public static final String SESSION_UPDATE_HANDLER = "sfcSessionChanged";
	public static final String SESSIONS_UPDATE_HANDLER = "sfcSessionsChanged";
	
	public static class ClientInfo {
		String userName;
		String project;
		String clientId;
		Set<String> sessions = new HashSet<String>();
		
		public ClientInfo(String userName, String project, String clientId) {
			this.userName = userName;
			this.project = project;
			this.clientId = clientId;
		}
		
		@Override
		public boolean equals(Object o) {
			if(o == null || !(o instanceof ClientInfo)) return false;
			return clientId.equals(((ClientInfo)o).clientId);
		}
		
		@Override
		public int hashCode() {
			return clientId.hashCode();
		}
	}
	
	public IlsSfcSessionMgr(MessageDispatchManager msgMgr) {
		this.msgMgr = msgMgr;
	}
	
	public void addClient(ClientInfo client) {
		logger.infof("adding client %s", client.clientId);
		clients.add(client);
		notifySessionsChanged(client);
	}

	public void removeClient(String clientId) {
		logger.infof("removing client %s", clientId);
		ClientInfo client = getClient(clientId);
		clients.remove(client);
	}
		
	public void addSessionListener(String sessionId, String clientId) {
		ClientInfo client = getClient(clientId);
		logger.infof("adding listener %s for session %s", clientId, sessionId);
		client.sessions.add(sessionId);
		notifySessionChanged(sessionId, client);
	}

	public void removeSessionListener(String sessionId, String clientId) {
		ClientInfo client = getClient(clientId);
		logger.infof("removing listener %s for session %s", clientId, sessionId);
		client.sessions.remove(sessionId);
	}

	/** Get the session by its unique id. */
	public PyObject getSession(String sessionId) {
		return (PyObject) sessionsById.get(sessionId);
	}

	/**Add a session, and make the creator a listener. */
	public void addSession(PyObject session, String clientId) {
		String sessionId = getSessionId(session);
		logger.infof("adding session %s", sessionId);
		sessionsById.put(sessionId, session);
		addSessionListener(sessionId, clientId);  // this will send notification
	}

	/** Update an existing session. */
	public void updateSession(PyObject session) {
		String sessionId = getSessionId(session);
		logger.infof("updating session %s", sessionId);
		notifySessionChanged(sessionId);
	}

	/** Delete a session. */
	public void removeSession(String sessionId) {
		logger.infof("removing session %s", sessionId);
		PyObject session = getSession(sessionId);
		sessionsById.remove(sessionId);
		String runId = getRunId(session);
		sessionsByRunId.remove(runId);
		for(ClientInfo client: clients) {
			client.sessions.remove(sessionId);
		}
		notifySessionsChanged();
	}
	
	/** Get the names and ids of the (top level) charts associated with the sessions. */	
	private Dataset getSessionData() {
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

	private ClientInfo getClient(String clientId) {
		for(ClientInfo client: clients) {
			if(client.clientId.equals(clientId)) {
				return client;
			}
		}
		return null;
	}

	/** Notify all clients that a session has changed */
	private void notifySessionChanged(String sessionId) {
		for(ClientInfo client: clients) {
			if(client.sessions.contains(sessionId)) {
				notifySessionChanged(sessionId, client);
			}
		}
	}

	/** Notify a single client that a session has changed */
	private void notifySessionChanged(String sessionId, ClientInfo client) {
		PyObject session = getSession(sessionId);
		PyDictionary payload = new PyDictionary();
		payload.put(Constants.SESSION, session);
		sendMessageToClient(client.project, SESSION_UPDATE_HANDLER, client.clientId, payload);
	}

	/** Notify a all clients that the list of sessions has changed */
	private void notifySessionsChanged() {
		for(ClientInfo client: clients) {
			notifySessionsChanged(client);
		}
	}

	/** Notify a single client that the list of sessions has changed */
	private void notifySessionsChanged(ClientInfo client) {
		Dataset sessionData = getSessionData();
		PyDictionary payload = new PyDictionary();
		payload.put(Constants.SESSIONS, sessionData);
		sendMessageToClient(client.project, SESSIONS_UPDATE_HANDLER, client.clientId, payload);
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

	/** Send a message to the given client. */
	private void sendMessageToClient(String project, String handler, String clientId, PyDictionary payload) {
		payload.put(Constants.MESSAGE_ID, UUID.randomUUID().toString());
		payload.put(Constants.MESSAGE, handler);
		Properties properties = new Properties();
		properties.put(MessageDispatchManager.KEY_CLIENT_SESSION_ID, clientId);
		properties.put(MessageDispatchManager.KEY_SCOPE, MessageDispatchManager.SCOPE_CLIENT_ONLY);
		msgMgr.dispatch(project, CLIENT_MSG_DISPATCHER, payload, properties);
	}
		
	// Accessors for the Python session object:

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

	// Chart Listener methods:
	
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
		notifySessionChanged(sessionId);
		
		// If the chart ended, remove the session
		if(newChartState.isTerminal()) {
			removeSession(sessionId);
		}
		notifySessionsChanged();
	}

	@Override
	public void onElementStateChange(UUID arg0, UUID arg1,
			ElementStateEnum arg2, ElementStateEnum arg3) {
		// TODO Auto-generated method stub
		
	}

}
