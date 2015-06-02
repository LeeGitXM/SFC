package com.ils.sfc.designer.panels;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import com.ils.sfc.designer.ColumnSelectionAdapter;
import com.inductiveautomation.ignition.designer.model.DesignerContext;

/** A superclass for sliding panes in editors */
@SuppressWarnings("serial")
public abstract class EditorPanel extends JPanel {
	public static java.awt.Color background = new java.awt.Color(238,238,238);	
	protected PanelController panelController; 
	protected int myIndex;	// index of this panel in the controller
	protected int returnIndex;  // index of panel to return to after accept action
	
	protected EditorPanel(PanelController controller, int index) {
		super(new BorderLayout());
		this.panelController = controller;
		myIndex = index;
	}

	protected DesignerContext getContext() {
		return panelController.getContext();
	}
	
	/** become visible 
	   subclasses should call super.activate() after doing any necessary preparation */
	public void activate(int returnIndex) {
		this.returnIndex = returnIndex;
		panelController.slideTo(myIndex);
	}
	
	/** Get the index of this panel in the controller. */
	public int getIndex() {
		return myIndex;		
	}
	
	/** Accept changes--at this level, just returns to invoking panel. */
	public void accept() {
		panelController.slideTo(returnIndex);
		returnIndex = -1;
	}
	
	/** Create the typical table for an editor panel (holds multi-row configuration step property). */
	protected JPanel createTablePanel(TableModel tableModel, JPanel tablePanel,
		TableCellEditor cellEditor, TableCellRenderer cellRenderer) {
		final JTable table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//table.setCellSelectionEnabled(true);
		table.getColumnModel().addColumnModelListener(new ColumnSelectionAdapter() {
			 public void columnSelectionChanged(ListSelectionEvent e) {
				columnSelected(table.getSelectedColumn());
			 }
		});
		table.setRowHeight(20);
		table.setRowMargin(3);	
		table.setShowGrid(true);
		JScrollPane scroll = new JScrollPane(table);
		scroll.setBorder(
			new CompoundBorder(
				new EmptyBorder(10,10,0,10), 
				new LineBorder(Color.black)));
		if(tablePanel != null) remove(tablePanel);
		tablePanel = new JPanel(new BorderLayout());	
		tablePanel.add(scroll, BorderLayout.CENTER);
		tablePanel.setBorder(new EmptyBorder(10,10,10,10));
		add(tablePanel, BorderLayout.CENTER);		
		table.setDefaultEditor(Object.class, cellEditor);
		table.setDefaultRenderer(Object.class, cellRenderer);
		return tablePanel;
	}
	
	/** Subclasses can override to handle column selection. */
	protected void columnSelected(int column) {}
		
}
