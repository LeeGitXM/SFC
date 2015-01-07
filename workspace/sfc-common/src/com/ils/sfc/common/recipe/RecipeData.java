package com.ils.sfc.common.recipe;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ils.sfc.common.chartStructure.IlsSfcChartStructureCompiler;
import com.ils.sfc.common.chartStructure.IlsSfcChartStructureMgr;
import com.ils.sfc.common.chartStructure.IlsSfcStepStructure;
import com.ils.sfc.common.step.OperationStepProperties;
import com.ils.sfc.common.step.PhaseStepProperties;
import com.ils.sfc.common.step.ProcedureStepProperties;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

/**
 * A class that holds recipe data for an entire site (i.e. all SFC charts)
 * 
 * Questions/Issues:
 *    -is path access (key.key.val) just for within recipe data, or can chart 
 *       step hierarchies be accessed in the same way ?      
 *    -is Named scope data global (vs in the scope of a graph)?
 *    -need some examples of cross-graph references
 *    
*/
public class RecipeData {
	private static LoggerEx logger = LogUtil.getLogger(RecipeData.class.getName());
	private Map<String,RecipeDataMap> dataByStepId = new HashMap<String,RecipeDataMap>();
	private RecipeDataMap globalNamedData = new RecipeDataMap();
	@JsonIgnore
	private IlsSfcChartStructureCompiler chartStructureCompiler; // not serialized
	@JsonIgnore
	private IlsSfcChartStructureMgr structureMgr;   // not serialized

	/** Set a recipe value given a path from a step, and a scope. If "create" is true,
	 *  intermediate dictionaries will be created as needed; otherwise any missing
	 *  data will cause a RecipeDataException to be thrown. */
	public void set(RecipeScope scope, String stepId, String path, Object value, boolean create) throws RecipeDataException {
		if(scope == RecipeScope.Local ) setAtLocalScope(stepId, path, value, create);
		else if(scope == RecipeScope.Previous) setAtPreviousScope(stepId, path, value, create);
		else if(scope == RecipeScope.Superior) setAtSuperiorScope(stepId, path, value, create);
		else if(scope == RecipeScope.Phase) setAtPhaseScope(stepId, path, value, create);
		else if(scope == RecipeScope.Operation) setAtOperationScope(stepId, path, value, create);
		else if(scope == RecipeScope.UnitProcedure) setAtProcedureScope(stepId, path, value, create);
		else if(scope == RecipeScope.Named) setAtNamedScope(path, value, create);
	}

	public IlsSfcChartStructureMgr getStructureMgr() {
		return structureMgr;
	}
	
	/** for UNIT TESTING ONLY!! */
	void setStructureManager(IlsSfcChartStructureMgr mgr) {
		structureMgr = mgr;
	}
	
	@JsonIgnore
	public boolean isInitialized() {
		return structureMgr != null;
	}
	
	public void compileStructure() {
		structureMgr = chartStructureCompiler.compile();
	}
	
	/** Get a recipe value given a path from a step, and a scope. */
	public Object get(RecipeScope scope, String stepId, String path) throws RecipeDataException {
		Object value = null;
		if(scope == RecipeScope.Local ) value = getAtLocalScope(stepId, path);
		else if(scope == RecipeScope.Previous) value = getAtPreviousScope(stepId, path);
		else if(scope == RecipeScope.Superior) value = getAtSuperiorScope(stepId, path);
		else if(scope == RecipeScope.Phase) value = getAtPhaseScope(stepId, path);
		else if(scope == RecipeScope.Operation) value = getAtOperationScope(stepId, path);
		else if(scope == RecipeScope.UnitProcedure) value = getAtProcedureScope(stepId, path);
		else if(scope == RecipeScope.Named) value = getAtNamedScope(path);
		else throw new RecipeDataException("bad Scope: " + scope);
		if(value instanceof Object) {
			return (Object) value;
		}
		else {
			throw new RecipeDataException("bad value type: " + (value != null ? value.getClass().getName() : "null"));
		}
	}
	
	private RecipeDataMap getOrCreateStepData(IlsSfcStepStructure step) {
		RecipeDataMap result = dataByStepId.get(step.getId());
		if(result == null) {
			result = new RecipeDataMap();
			dataByStepId.put(step.getId(), result);
		}
		return result;
	}
	
	private void setDataValue(IlsSfcStepStructure step, String path, Object value, boolean create) throws RecipeDataException {
		getOrCreateStepData(step).pathPut(path, value, create);
	}

	private Object getDataValue(IlsSfcStepStructure step, String path) throws RecipeDataException {
		return getOrCreateStepData(step).pathGet(path);
	}

	/** Get recipe data for a step, creating an empty dictionary if it doesn't exist. */
	public RecipeDataMap getStepData(String stepId) {
		if(!dataByStepId.containsKey(stepId)) {
			IlsSfcStepStructure step =structureMgr.getStepWithId(stepId);
			dataByStepId.put(stepId, getOrCreateStepData(step));
		}
		return dataByStepId.get(stepId);
	}
	
	/** Set method for Local scope. */
	public void setAtLocalScope(String stepId, String path, Object value, boolean create) throws RecipeDataException {
		IlsSfcStepStructure step = structureMgr.getStepWithId(stepId);
		setDataValue(step, path, value, create);
	}
	
	/** Set method for Previous scope. */
	public void setAtPreviousScope(String stepId, String path, Object value, boolean create)  throws RecipeDataException {
		IlsSfcStepStructure step = structureMgr.getStepWithId(stepId);
		IlsSfcStepStructure prevStep = step.getPrevious();
		if(prevStep == null) {
			throw new RecipeDataException("step " + step.getName() + " does not have a previous step");
		}
		setDataValue(prevStep, path, value, create);
	}
	
	/** Set method for Superior scope. */
	public void setAtSuperiorScope(String stepId, String path, Object value, boolean create)  throws RecipeDataException {
		IlsSfcStepStructure step = structureMgr.getStepWithId(stepId);
		IlsSfcStepStructure parentStep = step.getParent();
		if(parentStep == null) {
			throw new RecipeDataException("step " + step.getName() + " does not have an enclosing (superior) step");
		}
		setDataValue(parentStep, path, value, create);		
	}
	
	/** Set method for Phase scope. */
	public void setAtPhaseScope(String stepId, String path, Object value, boolean create)  throws RecipeDataException {
		IlsSfcStepStructure step = structureMgr.getStepWithId(stepId);
		IlsSfcStepStructure phaseStep = step.findParentWithFactoryId(PhaseStepProperties.FACTORY_ID);
		if(phaseStep == null) {
			throw new RecipeDataException("step " + step.getName() + " does not have an enclosing Phase");
		}
		setDataValue(phaseStep, path, value, create);		
    }

	/** Get name of the operation that encloses the given step. */
	public String getOperationName(String stepId)  throws RecipeDataException {
		IlsSfcStepStructure step = structureMgr.getStepWithId(stepId);
		IlsSfcStepStructure operationStep = step.findParentWithFactoryId(OperationStepProperties.FACTORY_ID);
		if(operationStep != null) {
			return operationStep.getName();
		}
		else {
			throw new RecipeDataException("step " + step.getName() + " does not have an enclosing Operation");
		}
		
	}
	
	/** Set method for Operation scope. */
	public void setAtOperationScope(String stepId, String path, Object value, boolean create)  throws RecipeDataException {
		IlsSfcStepStructure step = structureMgr.getStepWithId(stepId);
		IlsSfcStepStructure operationStep = step.findParentWithFactoryId(OperationStepProperties.FACTORY_ID);
		if(operationStep == null) {
			throw new RecipeDataException("step " + step.getName() + " does not have an enclosing Operation");
		}
		setDataValue(operationStep, path, value, create);		
	}
	
	/** Set method for UnitProcedure scope. */
	public void setAtProcedureScope(String stepId, String path, Object value, boolean create)  throws RecipeDataException {
		IlsSfcStepStructure step = structureMgr.getStepWithId(stepId);
		IlsSfcStepStructure procedureStep = step.findParentWithFactoryId(ProcedureStepProperties.FACTORY_ID);
		if(procedureStep == null) {
			throw new RecipeDataException("step " + step.getName() + " does not have an enclosing Procedure");
		}
		setDataValue(procedureStep, path, value, create);		
	}
	
	/** Set method for global Named scope. */
	public void setAtNamedScope(String path, Object value, boolean create)  throws RecipeDataException {
		globalNamedData.pathPut(path, value, create);
	}

	/** Get method for Local scope. */
	public Object getAtLocalScope(String stepId, String path) throws RecipeDataException {
		IlsSfcStepStructure step = structureMgr.getStepWithId(stepId);
		return getDataValue(step, path);
	}
	
	/** Get method for Previous scope. */
	public Object getAtPreviousScope(String stepId, String path)  throws RecipeDataException {
		IlsSfcStepStructure step = structureMgr.getStepWithId(stepId);
		IlsSfcStepStructure prevStep = step.getPrevious();
		if(prevStep == null) {
			throw new RecipeDataException("step " + step.getName() + " does not have a previous step");
		}
		return getDataValue(prevStep,path);
	}
	
	/** Get method for Superior scope. */
	public Object getAtSuperiorScope(String stepId, String path)  throws RecipeDataException {
		IlsSfcStepStructure step = structureMgr.getStepWithId(stepId);
		IlsSfcStepStructure parentStep = step.getParent();
		if(parentStep == null) {
			throw new RecipeDataException("step " + step.getName() + " does not have an enclosing (superior) step");
		}
		return getDataValue(parentStep, path);		
	}
	
	/** Get method for Phase scope. */
	public Object getAtPhaseScope(String stepId, String path)  throws RecipeDataException {
		IlsSfcStepStructure step = structureMgr.getStepWithId(stepId);
		IlsSfcStepStructure phaseStep = step.findParentWithFactoryId(PhaseStepProperties.FACTORY_ID);
		if(phaseStep == null) {
			throw new RecipeDataException("step " + step.getName() + " does not have an enclosing Phase");
		}
		return getDataValue(phaseStep, path);		
    }

	/** Get method for Operation scope. */
	public Object getAtOperationScope(String stepId, String path)  throws RecipeDataException {
		IlsSfcStepStructure step = structureMgr.getStepWithId(stepId);
		IlsSfcStepStructure operationStep = step.findParentWithFactoryId(OperationStepProperties.FACTORY_ID);
		if(operationStep == null) {
			throw new RecipeDataException("step " + step.getName() + " does not have an enclosing Operation");
		}
		return getDataValue(operationStep, path);		
	}
	
	/** Get method for Procedure scope. */
	public Object getAtProcedureScope(String stepId, String path)  throws RecipeDataException {
		IlsSfcStepStructure step = structureMgr.getStepWithId(stepId);
		IlsSfcStepStructure procedureStep = step.findParentWithFactoryId(ProcedureStepProperties.FACTORY_ID);
		if(procedureStep == null) {
			throw new RecipeDataException("step " + step.getName() + " does not have an enclosing Procedure");
		}
		return getDataValue(procedureStep, path);		
	}
	
	/** Get method for Named scope. */
	public Object getAtNamedScope(String path)  throws RecipeDataException {
		return globalNamedData.pathGet(path);
	}
	
	/** Serialize this object into bytes. */
	public byte[] serialize() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		String json = mapper.writeValueAsString(this);
		byte[] bytes = json.getBytes();
		return bytes;
	}

	/** Deserialize an instance from bytes. */
	public static RecipeData deserialize(byte[] bytes) throws JsonParseException, JsonMappingException, IOException { 
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		RecipeData recipeData = mapper.readValue(bytes, RecipeData.class);
		return recipeData;
	}

	public RecipeData copy() {
		try {
			// use serialization to do a deep copy (not too efficient, but convenient).
			RecipeData copy =  deserialize(serialize());
			copy.structureMgr = structureMgr;
			return copy;
		} catch (Exception e) {
			logger.error("error making copy of recipe data", e);
			return null;
		} 
	}
	
	public void setCompiler(IlsSfcChartStructureCompiler compiler) {
		chartStructureCompiler = compiler;
	}

	public RecipeDataMap getNamedData() {
		return globalNamedData;
	}

	/** Only used by JSON de-serialization. */
	public void setNamedData(RecipeDataMap map) {
		globalNamedData = map;
	}

	public Map<String, RecipeDataMap> getDataByStepId() {
		return dataByStepId;
	}

	public void setDataByStepId(Map<String, RecipeDataMap> dataByStepId) {
		this.dataByStepId = dataByStepId;
	}

	public static void main(String[] args) {
		RecipeData data = new RecipeData();
		try {
			System.out.println(new String(data.serialize()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
