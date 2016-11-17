package com.ils.sfc.common.recipe.objects;

import com.ils.sfc.common.IlsProperty;
import com.inductiveautomation.ignition.common.Dataset;

import system.ils.sfc.common.Constants;

import static com.ils.sfc.common.IlsSfcCommonUtils.isEmpty;

/**
superiorClass: sequence (the symbol S88-RECIPE-DATA-WITH-UNITS)
attributes:sequence (structure (ATTRIBUTE-NAME: the symbol VAL,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol S88-MATRIX,
    ATTRIBUTE-INITIAL-ITEM-CLASS: the symbol S88-MATRIX))
 */
public class Matrix extends DataWithUnits {
	
	public Matrix() {
		addProperty(IlsProperty.ROWS); // # of rows
		addProperty(IlsProperty.ROW_KEY);
		//addProperty(IlsProperty.ROW_KEYED);
		addProperty(IlsProperty.COLUMNS);  // # of columns
		addProperty(IlsProperty.COLUMN_KEY);
		//addProperty(IlsProperty.COLUMN_KEYED);
		addProperty(IlsProperty.JSON_MATRIX);
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
		String matrixJSON = (String)getValue(IlsProperty.JSON_MATRIX);
		String type = (String)getValue(IlsProperty.VALUE_TYPE);
		if( type.equals(Constants.BOOLEAN)) {
			
		}
		else if( type.equals(Constants.DATE_TIME)) {
			
		}
		else if( type.equals(Constants.FLOAT)) {
	
		}
		else if( type.equals(Constants.INT)) {
	
		}
		else {    // Default to String
	
		}
		Dataset dataset = null;
		try {
			dataset = createDataset(matrixJSON);
		}
		catch(Exception e) {
			return matrixJSON + " is not a valid JSON matrix";
		}
		
		// Check rows
		String rowKey = (String)getValue(IlsProperty.ROW_KEY);
		boolean rowIsKeyed = !isEmpty(rowKey);
		int datasetRowCount = dataset.getRowCount();
		
		if(rowIsKeyed) {
			int keyLength = getIndexSize(rowKey);
			if(keyLength != datasetRowCount) {
				return "Row key length " + keyLength + "is different than # of rows in value";
			}
		}
		else {
			Integer rows = (Integer)getValue(IlsProperty.ROWS);
			if(rows == null || rows.intValue() != datasetRowCount) {
				return "Rows property " + rows + "is different than # of rows in value";
			}
		}

		// Check columns
		String colKey = (String)getValue(IlsProperty.COLUMN_KEY);
		boolean colIsKeyed = !isEmpty(colKey);
		int datasetColCount = dataset.getColumnCount();
		
		if(colIsKeyed) {
			int keyLength = getIndexSize(colKey);
			if(keyLength != datasetColCount) {
				return "col key length " + keyLength + "is different than # of cols in value";
			}
		}
		else {
			Integer cols = (Integer)getValue(IlsProperty.COLUMNS);
			if(cols == null || cols.intValue() != datasetColCount) {
				return "columns property " + cols + "is different than # of cols in value";
			}
		}
		
		return null; // OK
	}
	
}
