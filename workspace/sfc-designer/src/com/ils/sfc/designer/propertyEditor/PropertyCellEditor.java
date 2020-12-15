package com.ils.sfc.designer.propertyEditor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import com.ils.sfc.designer.DesignerUtil;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.designer.workspace.editors.ChartPathComboBox;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;

/***
 * This is used for the primary property editor for a step.
 * It is not used for a secondary property editor. 
 */

@SuppressWarnings("serial")
class PropertyCellEditor extends AbstractCellEditor implements TableCellEditor{
	private Component component;
	private PropertyCellComponentFactory factory = new PropertyCellComponentFactory();
	private final LoggerEx log = LogUtil.getLogger(getClass().getName());

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
		int row, int col) {
		PropertyTableModel model = (PropertyTableModel) table.getModel();
		PropertyRow rowObj = ((PropertyTableModel) table.getModel()).getRowObject(row);
		if(rowObj.getChoices() != null) {
			log.tracef("Making a combo box...");
			PropertyValue pv = rowObj.getPropertyValue();

			log.tracef("...the current value is: %s", pv.getValue().toString());
			
			JComboBox<Object> combo = new JComboBox<Object>(rowObj.getChoices());

			combo.setBackground(java.awt.Color.white);
			combo.setSelectedItem(pv.getValue().toString());
			combo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fireEditingStopped();
				}				
			});
			return component = combo;
		}
		else if(rowObj.getProperty().equals(EnclosingStepProperties.CHART_PATH)) {
			String currentPath = (String)rowObj.getValue();
			return component = DesignerUtil.getChartPathComboBox(currentPath);
		}
		else {
			int alignment = factory.getHorizontalAlignment(col);
			boolean valueIsEditable = col == 1 && model.isCellEditable(row, col);
			return component = factory.getComponentForValue(rowObj, alignment, valueIsEditable);
		}
	}

	@SuppressWarnings("unchecked")
	public Object getCellEditorValue() {
		if(component instanceof JTextField) {
			String val = ((JTextField)component).getText();
			return val;
		}
	    else if(component instanceof JCheckBox) {
	    	return Boolean.valueOf(((JCheckBox)component).isSelected()).toString();
	    }
	    else if(component instanceof ChartPathComboBox) {
	    	Object selectedItem = ((ChartPathComboBox)component).getSelectedItem();
	    	String selectedChartPath = ((ChartPathComboBox)component).getSelectedChartPath();
	    	return selectedChartPath;
	    }
	    else if(component instanceof JComboBox) {
	    	return ((JComboBox<Object>)component).getSelectedItem();
	    }
	    else {	    	
	    	return "?";
	    }
	}
	
}