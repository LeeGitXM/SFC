package com.ils.sfc.designer.editor;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

@SuppressWarnings("serial")
public class PropertyTableModel extends AbstractTableModel {
	private static final String[] columnNames = {"Property", "Value", "Units"};
	private List<PropertyRow> rows = new ArrayList<PropertyRow>();
	private boolean hasChanged;
	private ChartUIElement element;
	private static final Set<String> ignoreProperties = new HashSet<String>();
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
    	return col == 1 && !getRowObject(row).isCategory();
    }
    
    public void setValueAt(Object value, int row, int col) {
    	PropertyRow pRow = rows.get(row);
		System.out.println("set value " + value);

    	try {
			pRow.setValueFormatted((String)value);
			element.set(pRow.getPropertyValue());
	    	hasChanged = true;
	        fireTableCellUpdated(row, col);
		} catch (ParseException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Illegal Value", JOptionPane.WARNING_MESSAGE);
		}
    }

	public void setElement(ChartUIElement element) {
		this.element = element;		
		rows.clear();
		System.out.println("b4 set rows");
		for(PropertyValue<?> value: element) {
			if(!ignoreProperties.contains(value.getProperty().getName())){
				rows.add(new PropertyRow(value, null));
			}
		}
		System.out.println("after set rows");
		fireTableStructureChanged();
		System.out.println("after fire");
	}
}
