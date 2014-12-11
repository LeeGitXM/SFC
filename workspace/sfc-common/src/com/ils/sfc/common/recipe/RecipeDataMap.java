package com.ils.sfc.common.recipe;

import java.util.HashMap;
import java.util.Map;

/** An extension of Map that supports getting hierarchical data via dot-separated
 *  key references.
 */
@SuppressWarnings("serial")
public class RecipeDataMap extends HashMap<String,Object> {
	
	/** Get the last map in the path by following all of the keys except the last. 
	 * @throws RecipeDataException */
	@SuppressWarnings("unchecked")
	private Map<String,Object> pathGetMap(String path, String[] keys, boolean create) throws RecipeDataException {
		Map<String,Object> map = this;
		for(int i = 0; i < keys.length - 1; i++ ) {
			String key = keys[i];
			Object value = map.get(key);
			if(value == null) {
				if(create) {
					HashMap<String,Object> newMap = new HashMap<String,Object>();
					map.put(key, newMap);
					map = newMap;
				}
				else {
					throwPathDoesNotExist(path);
				}
			}
			else {
				if(value instanceof Map) {
					map = (Map<String,Object>)value;
				}
				else {
					throwPathDoesNotExist(path);
				}			
			}
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public Object pathGet(String path) throws RecipeDataException {
		String[] keys = splitPath(path);
		Map<String,Object> map = pathGetMap(path, keys, false);
		String lastKey = keys[keys.length - 1];
		if(map.containsKey(lastKey)) {
			return map.get(lastKey);
		}
		else {
			throwPathDoesNotExist(path);
			return null; // keep the compiler happy
		}
	}
	
	private void throwPathDoesNotExist(String path) throws RecipeDataException {
		throw new RecipeDataException("object " + path + " does not exist");
	}
	
	private String[] splitPath(String path) {
		return path.split("\\.");
	}
	
	@SuppressWarnings("unchecked")
	public void pathPut(String path, Object value, boolean create) throws RecipeDataException {
		String[] keys = splitPath(path);
		Map<String,Object> map = pathGetMap(path, keys, create);
		String lastKey = keys[keys.length - 1];
		if(map.containsKey(lastKey) || create) {
			map.put(lastKey, value);
		}
		else {
			throwPathDoesNotExist(path);
		}
	}
}
