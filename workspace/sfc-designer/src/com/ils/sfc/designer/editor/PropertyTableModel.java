package com.ils.sfc.designer.editor;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ils.sfc.util.IlsProperty;
import com.ils.sfc.util.IlsSfcNames;
import com.ils.sfc.util.PythonCall;
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
	private static final Logger logger = LoggerFactory.getLogger(PropertyTableModel.class);
 
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
        	return pRow.getDisplayLabel();
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
			if( !IlsProperty.ignoreProperties.contains(name) &&
				!name.endsWith(UNIT_SUFFIX)) {
				PropertyValue<?> unitValueOrNull = propsByName.get(name + UNIT_SUFFIX);
				PropertyRow newRow = new PropertyRow(pValue, unitValueOrNull);
				rows.add(newRow);
	
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
		
		sortRows();
		fireTableStructureChanged();
	}

	/** sort the rows in the order that the property array was declared */
	private void sortRows() {
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
