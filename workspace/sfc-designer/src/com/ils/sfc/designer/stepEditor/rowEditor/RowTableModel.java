package com.ils.sfc.designer.stepEditor.rowEditor;

import javax.swing.table.AbstractTableModel;

import com.ils.sfc.common.rowconfig.RowConfig;

@SuppressWarnings("serial")
public abstract class RowTableModel extends AbstractTableModel {
	protected RowConfig rowConfig;
	private ErrorHandler errorHandler;
	
	public interface ErrorHandler {
		public void handleError(String msg);
	}
	
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public void setConfig(RowConfig config) {
		this.rowConfig = config;		
	}
	
	public void addRow() {
		rowConfig.addRow();
		fireTableStructureChanged();
	}

	public void removeSelectedRow(int row) {
		if(row == -1) return;
		rowConfig.removeRow(row);
		fireTableStructureChanged();
	}
	
	public abstract String[] getColumnNames();

	public String getColumnName(int col) {
        return  getColumnNames()[col];
    }

	@Override
    public int getColumnCount() { return getColumnNames().length; }

	@Override
	public int getRowCount() {
		return rowConfig.getRowCount();
	}
	
    public boolean isCellEditable(int row, int col) { 
    	return true;
    }
 
}
