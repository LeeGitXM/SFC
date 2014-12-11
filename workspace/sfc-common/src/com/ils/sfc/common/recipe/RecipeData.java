package com.ils.sfc.common.recipe;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A class wrapping a dictionary that holds recipe data for an entire site.
 * 
 * Questions/Issues:
 *    -is path access (key.key.val) just for within recipe data, or can chart 
 *       step hierarchies be accessed in the same way ?      
 *    -is Named scope data global (vs in the scope of a graph)?
 *    -need some examples of cross-graph references
 *    -how do we get the name of a graph?
 *    
 * Todo:
 *    -(re)compile a graph and update the recipe data, preserving any pre-existing data
 *       and removing any zombies
 *    -put serialization code in this class
 */
public class RecipeData {
	public static final String ENCLOSING_FACTORY_ID = "enclosing-step";
	public static final String PROCEDURE_FACTORY_ID = "com.ils.procedureStep";
	public static final String OPERATION_FACTORY_ID = "com.ils.operationStep";
	public static final String PHASE_FACTORY_ID = "com.ils.phaseStep";

	public static enum Scope {
		Local,
		Previous,
		Superior,
		Phase,
		Operation,
		UnitProcedure,
		Named
	};

	private Map<String, StepData> stepsById = new HashMap<String, StepData>();
	private RecipeDataMap globalData = new RecipeDataMap();
	
	public void addStep(StepData step) {
		stepsById.put(step.getId(), step);
	}
	
	public void removeStep(String stepId) {
		stepsById.remove(stepId);
	}
	
	public boolean stepExists(String stepId) {
		return stepsById.containsKey(stepId);
	}
	
	StepData getStep(String id) throws RecipeDataException {
		if(stepsById.containsKey(id)) {
			return stepsById.get(id);
		}
		else {
			throw new RecipeDataException("no step for id " + id);
		}
	}
	
	StepData getParentWithFactoryId(String stepId, String factoryId) throws RecipeDataException {
		StepData step = getStep(stepId);
		if(factoryId.equals(step.getFactoryId())) {
			return step;
		}
		else if(step.getParentId() != null) {
			return getParentWithFactoryId(step.getParentId(), factoryId);
		}
		else {
			throw new RecipeDataException("no parent for factory id " + factoryId);
		}
	}

	public void set(Scope scope, String stepId, String path, Object value, boolean create) throws RecipeDataException {
		if(scope == Scope.Local ) setLocal(stepId, path, value, create);
		else if(scope == Scope.Previous) setPrevious(stepId, path, value, create);
		else if(scope == Scope.Superior) setSuperior(stepId, path, value, create);
		else if(scope == Scope.Phase) setPhase(stepId, path, value, create);
		else if(scope == Scope.Operation) setOperation(stepId, path, value, create);
		else if(scope == Scope.UnitProcedure) setProcedure(stepId, path, value, create);
		else if(scope == Scope.Named) setNamed(path, value, create);
	}

	public Object get(Scope scope, String stepId, String path) throws RecipeDataException {
		if(scope == Scope.Local ) return getLocal(stepId, path);
		else if(scope == Scope.Previous) return getPrevious(stepId, path);
		else if(scope == Scope.Superior) return getSuperior(stepId, path);
		else if(scope == Scope.Phase) return getPhase(stepId, path);
		else if(scope == Scope.Operation) return getOperation(stepId, path);
		else if(scope == Scope.UnitProcedure) return getProcedure(stepId, path);
		else if(scope == Scope.Named) return getNamed(path);
		else throw new RecipeDataException("bad Scope: " + scope);
	}

	public void setLocal(String stepId, String path, Object value, boolean create) throws RecipeDataException {
		getStep(stepId).set(path, value, create);
	}
	
	public void setPrevious(String stepId, String path, Object value, boolean create)  throws RecipeDataException {
		StepData step = getStep(stepId);
		StepData prevStep = getStep(step.getPreviousStepId());
		prevStep.set(path, value, create);
	}
	
	public void setSuperior(String stepId, String path, Object value, boolean create)  throws RecipeDataException {
		StepData step = getStep(stepId);
		StepData parentStep = getStep(step.getParentId());
		parentStep.set(path, value, create);		
	}
	
	public void setPhase(String stepId, String path, Object value, boolean create)  throws RecipeDataException {
		getParentWithFactoryId(stepId, PHASE_FACTORY_ID).set(path, value, create);		
    }

	public void setOperation(String stepId, String path, Object value, boolean create)  throws RecipeDataException {
		getParentWithFactoryId(stepId, OPERATION_FACTORY_ID).set(path, value, create);				
	}
	
	public void setProcedure(String stepId, String path, Object value, boolean create)  throws RecipeDataException {
		getParentWithFactoryId(stepId, PROCEDURE_FACTORY_ID).set(path, value, create);				
	}
	
	public void setNamed(String path, Object value, boolean create)  throws RecipeDataException {
		globalData.pathPut(path, value, create);
	}

	public Object getLocal(String stepId, String path) throws RecipeDataException {
		return getStep(stepId).get(path);
	}
	
	public Object getPrevious(String stepId, String path)  throws RecipeDataException {
		StepData step = getStep(stepId);
		StepData prevStep = getStep(step.getPreviousStepId());
		return prevStep.get(path);
	}
	
	public Object getSuperior(String stepId, String path)  throws RecipeDataException {
		StepData step = getStep(stepId);
		StepData parentStep = getStep(step.getParentId());
		return parentStep.get(path);		
	}
	
	public Object getPhase(String stepId, String path)  throws RecipeDataException {
		return getParentWithFactoryId(stepId, PHASE_FACTORY_ID).get(path);		
    }

	public Object getOperation(String stepId, String path)  throws RecipeDataException {
		return getParentWithFactoryId(stepId, OPERATION_FACTORY_ID).get(path);				
	}
	
	public Object getProcedure(String stepId, String path)  throws RecipeDataException {
		return getParentWithFactoryId(stepId, PROCEDURE_FACTORY_ID).get(path);				
	}
	
	public Object getNamed(String path)  throws RecipeDataException {
		return globalData.pathGet(path);
	}
	
	public byte[] serialize() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(this);
		byte[] bytes = json.getBytes();
		return bytes;
	}

	public static RecipeData deserialize(byte[] bytes) throws JsonParseException, JsonMappingException, IOException { 
		ObjectMapper mapper = new ObjectMapper();
		RecipeData recipeData = mapper.readValue(bytes, RecipeData.class);
		return recipeData;
	}
}
