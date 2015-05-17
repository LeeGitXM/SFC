package com.ils.sfc.designer.propertyEditor;

import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.PythonCall;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.script.JythonExecException;

@SuppressWarnings("serial")
public class PropertyTableModel extends AbstractTableModel {
	private static final String[] columnNames = {"Property", "Value"};
	static final int NAME_COLUMN = 0;
	static final int VALUE_COLUMN = 1;
	private List<PropertyRow> rows = new ArrayList<PropertyRow>();
	private boolean hasChanged;
	private BasicPropertySet propertyValues;
	private String stepId;
	private static final Logger logger = LoggerFactory.getLogger(PropertyTableModel.class);
	private ErrorHandler errorHandler;
	
	public interface ErrorHandler {
		public void handleError(String msg);
	}
	
	public String getColumnName(int col) {
        return columnNames[col];
    }
    
    public List<PropertyRow> getRows() {
    	return rows;
    }
    
    public boolean hasChanged() {
    	return hasChanged;
    }
    
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public PropertyRow getRowObject(int i) {
    	return rows.get(i);
    }

    public Class<?> getPropertyType(int rowIndex) {
    	PropertyRow row = rows.get(rowIndex);
    	return row.getProperty().getType();
    }
    
	public int getRowCount() { return rows.size(); }
    
    public int getColumnCount() { return columnNames.length; }
    
    public String getStepId() {
    	return stepId;
    }
    
    public Object getValueAt(int row, int col) {
    	PropertyRow pRow = rows.get(row);
    	Object value = null;
        if(col == NAME_COLUMN) {
        	value = pRow.getDisplayLabel();
        }
        else if(col == VALUE_COLUMN) {
        	value = pRow.getValue();
        }
        return value;
    }
    
    public boolean isCellEditable(int row, int col) { 
    	PropertyRow rowObj = getRowObject(row);
    	return col == VALUE_COLUMN && !rowObj.isCategory() && 
    		!IlsProperty.isSerializedObject(rowObj.getProperty());
    }
    
    public void setValueAt(Object value, int row, int col) {
    	PropertyRow pRow = rows.get(row);

		if(col == VALUE_COLUMN) {
			try {
				// It is a bit strange that the value is set twice
				// The reason for this is that the "real" value is
				// in the propertyValues collections, but the 
				// viewed value is in the pRow. These are two 
				// different values, basically because PropertyValues
				// are immutable
				pRow.setValueFormatted((String)value);
				propertyValues.set(pRow.getPropertyValue());
			}
			catch(ParseException e) {
				if(errorHandler != null) {
					errorHandler.handleError(e.getMessage());
				}
			}
		}
    	hasChanged = true;
        fireTableCellUpdated(row, col);
    }

    public BasicPropertySet getPropertyValues() {
    	return propertyValues;
    }
    
	public void setPropertyValues(BasicPropertySet propertyValues, boolean sortInternal) {
		this.propertyValues = propertyValues;		
		rows.clear();
		Map<String,PropertyValue<?>> propsByName = new HashMap<String,PropertyValue<?>>();
		for(PropertyValue<?> pValue: propertyValues) {
			propsByName.put(pValue.getProperty().getName(), pValue);
		}
		for(PropertyValue<?> pValue: propertyValues) {
			String name = pValue.getProperty().getName();
			if(name.equals("id")) {
				stepId = pValue.getValue().toString();
			}
			if( !IlsProperty.ignoreProperties.contains(name)) {
				PropertyRow newRow = new PropertyRow(pValue);
				rows.add(newRow);				
			}
		}
		
		if(sortInternal) {
			sortRowsInternal();
		}
		else{
			sortRowsAlphabetical();
		}
		fireTableStructureChanged();
	}

	private void sortRowsAlphabetical() {
		Collections.sort(rows, new Comparator<PropertyRow>() {
			public int compare(PropertyRow o1, PropertyRow o2) {
				BasicProperty<?> p1 = (BasicProperty<?>) o1.getProperty();
				BasicProperty<?> p2 = (BasicProperty<?>) o2.getProperty();
				return p1.getName().compareTo(p2.getName());
			}
			
		});
	}

	/** sort the rows in the order that the property array was declared */
	private void sortRowsInternal() {
		Collections.sort(rows, new Comparator<PropertyRow>() {
			public int compare(PropertyRow o1, PropertyRow o2) {
				if(!(o1.getProperty() instanceof IlsProperty)) {
					return -1; // put IA properties first
				}
				else if(!(o2.getProperty() instanceof IlsProperty)) {
					return 1;
				}
				else {
					IlsProperty<?> p1 = (IlsProperty<?>) o1.getProperty();
					IlsProperty<?> p2 = (IlsProperty<?>) o2.getProperty();
					return Integer.compare(p1.getSortOrder(), p2.getSortOrder());
				}
			}
			
		});
	}
}
