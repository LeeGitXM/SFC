package com.ils.sfc.common.oldRecipe;

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
		// note that we skip the last key in the array...
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
					// There's a problem with the type of the value--should be a map,
					// but it isn't
					throw new RecipeDataException("the key " + key + " in path " + path + " is not a dictionary; value: " + value);
				}			
			}
		}
		return map;
	}
	
	/** Get a value from a nested hierarchy of dictionaries. */
	public Object pathGet(String path) throws RecipeDataException {
		if(path == null) return this;
		String[] keys = splitPath(path);
		Map<String,Object> map = pathGetMap(path, keys, false);
		String lastKey = keys[keys.length - 1];
		if(map.containsKey(lastKey)) {
			Object value = map.get(lastKey);
			return value;
		}
		else {
			throwPathDoesNotExist(path);
			return null; // keep the compiler happy
		}
	}

	/** Put a value into a nested hierarchy of dictionaries. If "create" is true any
	 *  missing parts of the hierarchy implied by the path will be created. */
	public void pathPut(String path, Object value, boolean create) throws RecipeDataException {
		String[] keys = splitPath(path);
		Map<String,Object> map = pathGetMap(path, keys, create);
		String lastKey = keys[keys.length - 1];
		if(map.containsKey(lastKey) || create) {
//System.out.println("creating key " + lastKey + " for path " + path);

			map.put(lastKey, value);
		}
		else {
			throwPathDoesNotExist(path);
		}
	}
	
	private void throwPathDoesNotExist(String path) throws RecipeDataException {
		throw new RecipeKeyException("key " + path + " does not exist");
	}
	
	/** Split a dot-separated path reference into the individual keys. */
	private String[] splitPath(String path) {
		return path.split("\\.");
	}
	
}
