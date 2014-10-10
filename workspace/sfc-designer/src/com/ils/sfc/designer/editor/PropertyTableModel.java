package com.ils.sfc.designer.editor;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.python.core.PyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ils.sfc.common.IlsSfcNames;
import com.ils.sfc.util.IlsSfcCommonUtils;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.ignition.common.config.BasicDescriptiveProperty;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

@SuppressWarnings("serial")
public class PropertyTableModel extends AbstractTableModel {
	private static final String UNIT_SUFFIX = "Unit";
	private static final String[] columnNames = {"Property", "Value", "Units"};
	private List<PropertyRow> rows = new ArrayList<PropertyRow>();
	private boolean hasChanged;
	private ChartUIElement element;
	private static final Set<String> ignoreProperties = new HashSet<String>();
	private static final Logger logger = LoggerFactory.getLogger(PropertyTableModel.class);

	static {
		ignoreProperties.add("location");
		ignoreProperties.add("location-adjustment");
		ignoreProperties.add("id");
		ignoreProperties.add("type");
		ignoreProperties.add("factory-id");
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
    
    public PropertyRow getRowObject(int i) {
    	return rows.get(i);
    }
    
    public void setProperties(List<PropertyRow> newPropertyValues) {
    	hasChanged = false;
    	rows.clear();
    	Map<String,List<PropertyRow>> propertiesByCategory = new HashMap<String,List<PropertyRow>>();
    	for(PropertyRow row: newPropertyValues) {
    		if(row.getCategory() == null) {
    			rows.add(row);
    		}
    		else {
    			String category = row.getCategory();
    			List<PropertyRow> categoryProperties = propertiesByCategory.get(category);
    			if(categoryProperties == null) {
    				categoryProperties = new ArrayList<PropertyRow>();
    				propertiesByCategory.put(category, categoryProperties);
    			}
    			categoryProperties.add(row);
    		}
    	}
    	for(String category: propertiesByCategory.keySet()) {
    		// TODO: reflect the property name and isCategory property:
    		PropertyRow newRow = new PropertyRow(null, null);
    		rows.add(newRow);
    		for(PropertyRow propValue: propertiesByCategory.get(category)) {
        		rows.add(propValue);   			
    		}
    	}
		this.fireTableDataChanged();
	}

	public int getRowCount() { return rows.size(); }
    
    public int getColumnCount() { return columnNames.length; }
    
    public Object getValueAt(int row, int col) {
    	PropertyRow pRow = rows.get(row);
        if(col == 0) {
        	return pRow.getName();
        }
        else if(col == 1) {
        	return pRow.getValue();
        }
        else if(col == 2) {
        	return pRow.getUnitName();
        }
        else {
        	return ""; // won't happen, but keep the compiler happy
        }
    }
    
    public boolean isCellEditable(int row, int col) { 
    	PropertyRow rowObj = getRowObject(row);
    	return (col == 1 && !rowObj.isCategory()) ||
    		(col == 2 && rowObj.getUnitPropertyValue() != null);
    }
    
    public void setValueAt(Object value, int row, int col) {
    	PropertyRow pRow = rows.get(row);

    	try {
    		if(col == 1) {
				pRow.setValueFormatted((String)value);
				element.set(pRow.getPropertyValue());
    		}
    		else if(col == 2) {
				pRow.setUnitValueFormatted((String)value);
				element.set(pRow.getUnitPropertyValue());    			
    		}
	    	hasChanged = true;
	        fireTableCellUpdated(row, col);
		} catch (ParseException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Illegal Value", JOptionPane.WARNING_MESSAGE);
		}
    }

	public void setElement(ChartUIElement element) {
		this.element = element;		
		rows.clear();
		Map<String,PropertyValue<?>> propsByName = new HashMap<String,PropertyValue<?>>();
		for(PropertyValue<?> pValue: element) {
			propsByName.put(pValue.getProperty().getName(), pValue);
		}
		for(PropertyValue<?> pValue: element) {
			String name = pValue.getProperty().getName();
			if( !ignoreProperties.contains(name) &&
				!name.endsWith(UNIT_SUFFIX)) {
				PropertyValue<?> unitValueOrNull = propsByName.get(name + UNIT_SUFFIX);
				PropertyRow newRow = new PropertyRow(pValue, unitValueOrNull);
				rows.add(newRow);
	
				// add choices where appropriate
				if(pValue.getProperty().getName().equals(IlsSfcNames.RECIPE_LOCATION)) {
					newRow.setChoices(IlsSfcNames.RECIPE_LOCATION_CHOICES);
				}
				
				// add unit choices if present
				if(unitValueOrNull != null) {
					String unit = unitValueOrNull.getValue().toString();
					Object[] unitChoices = null;
					try {
						unitChoices = PythonCall.toArray(
							PythonCall.OTHER_UNITS.exec(unit));
					} catch (JythonExecException e) {
						logger.error("Exception getting units", e);
					}
					newRow.setUnitChoices(unitChoices);
				}
				
			}
		}
		fireTableStructureChanged();
	}
}
