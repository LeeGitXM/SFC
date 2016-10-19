package com.ils.sfc.common.recipe.objects;

import static com.ils.sfc.common.IlsSfcCommonUtils.isEmpty;

import com.ils.sfc.common.IlsProperty;

/**
A class that lumps together all the sequence/list types in G2
 */
public class Array extends DataWithUnits {
	
	public Array() {
		//addProperty(IlsProperty.KEYED);
		addProperty(IlsProperty.LENGTH);
		addProperty(IlsProperty.ARRAY_KEY);
		addProperty(IlsProperty.JSON_LIST);
	}

	/** Get a label for this data, typically its name and some values of interest. */
	public String getLabel() {
		StringBuffer buf = new StringBuffer();
		addLabelValue(IlsProperty.KEY, buf);		
		addLabelValue(IlsProperty.CLASS, buf);		
		addLabelValue(IlsProperty.UNITS, buf);
		return buf.toString();
	}
	
	
	public String validate() {
		String json = (String)getValue(IlsProperty.JSON_LIST);
		Double[] array = null;
		try {
			array = createArray(json);
		}
		catch(Exception e) {
			return json + " is not a valid JSON array";
		}
		
		// Check rows
		String key = (String)getValue(IlsProperty.ARRAY_KEY);
		boolean isKeyed = !isEmpty(key);
		
		if(isKeyed) {
			int keyLength = getIndexSize(key);
			if(keyLength != array.length){
				return "Row key length " + keyLength + "is different than # of rows in value";
			}
		}
		else {
			Integer length = (Integer)getValue(IlsProperty.LENGTH);
			if(length == null || length.intValue() != array.length) {
				return "length property " + length + "is different than # of rows in value";
			}
		}
		
		return null; // OK
	}
}
