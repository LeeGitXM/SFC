package com.ils.sfc.common.recipe.objects;

import static com.ils.sfc.common.IlsSfcCommonUtils.isEmpty;

import java.util.Date;

import com.ils.sfc.common.IlsProperty;

import system.ils.sfc.common.Constants;

/**
A class that lumps together all the sequence/list types in G2
 */
public class Array extends DataWithUnits {
	
	public Array() {
		//addProperty(IlsProperty.KEYED);
		addProperty(IlsProperty.LENGTH);
		addProperty(IlsProperty.ARRAY_KEY);
		addProperty(IlsProperty.JSON_LIST);
		addProperty(IlsProperty.VALUE_TYPE);
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
		
		String type = (String)getValue(IlsProperty.VALUE_TYPE);
		int arrayLength = 0;
		
		if( type.equals(Constants.BOOLEAN)) {
			Boolean[] array = null;
			try {
				array = createBooleanArray(json);
				arrayLength = array.length;
			}
			catch(Exception e) {
				return json + " is not a valid JSON array of booleans";
			}
		}
		else if( type.equals(Constants.DATE_TIME)) {
			Date[] array = null;
			try {
				array = createDateArray(json);
				arrayLength = array.length;
			}
			catch(Exception e) {
				return json + " is not a valid JSON array of dates";
			}
		}
		else if( type.equals(Constants.FLOAT)) {
			Double[] array = null;
			try {
				array = createDoubleArray(json);
				arrayLength = array.length;
			}
			catch(Exception e) {
				return json + " is not a valid JSON array of doubles";
			}
		}
		else if( type.equals(Constants.INT)) {
			Integer[] array = null;
			try {
				array = createIntegerArray(json);
				arrayLength = array.length;
			}
			catch(Exception e) {
				return json + " is not a valid JSON array of integers";
			}
		}
		else {    // Default to String
			String[] array = null;
			try {
				array = createStringArray(json);
				arrayLength = array.length;
			}
			catch(Exception e) {
				return json + " is not a valid JSON array of strings";
			}
		}
		
		// Check rows
		String key = (String)getValue(IlsProperty.ARRAY_KEY);
		boolean isKeyed = !isEmpty(key);
		
		if(isKeyed) {
			int keyLength = getIndexSize(key);
			if(keyLength != arrayLength){
				return "Row key length " + keyLength + "is different than # of rows in value";
			}
		}
		else {
			Integer length = (Integer)getValue(IlsProperty.LENGTH);
			if(length == null || length.intValue() != arrayLength) {
				return "length property " + length + "is different than # of rows in value";
			}
		}
		
		return null; // OK
	}
}
