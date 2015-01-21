package com.ils.sfc.designer.reviewDataEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.ils.sfc.common.recipe.RecipeDataManager;
import com.ils.sfc.common.recipe.ReviewDataConfig;

public class ReviewDataEditorDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private String stepId;
	private ReviewDataConfig config;
	private JButton okButton = new JButton("OK");
	private JButton cancelButton = new JButton("Cancel");
	private JButton addButton = new JButton("Add");
	private JButton removeButton = new JButton("Remove");
	private ReviewDataTableModel tableModel;
	private JTable table;
	
	public ReviewDataEditorDialog(ReviewDataConfig config, boolean showAdvice) {
		initUI(config, showAdvice);
	}

	private void initUI(ReviewDataConfig config, boolean showAdvice) {
		tableModel = new ReviewDataTableModel(showAdvice);
		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDefaultEditor(Object.class, new ReviewDataCellEditor());
		table.setDefaultRenderer(Object.class, new ReviewDataCellRenderer());
		table.setRowHeight(20);
		table.setRowMargin(3);	
		table.setShowGrid(true);
		tableModel.setConfig(config);
		JPanel panel = new JPanel(new BorderLayout());
		getContentPane().add(panel);
		
		JPanel tablePanel = new JPanel(new BorderLayout());	
		tablePanel.setBorder(new EmptyBorder(10,10,10,10));
		panel.add(tablePanel, BorderLayout.CENTER);
		JScrollPane scroll = new JScrollPane(table);
		scroll.setBorder(
			new CompoundBorder(
				new EmptyBorder(10,10,0,10), 
				new LineBorder(Color.black)));
		tablePanel.add(scroll, BorderLayout.CENTER);

		JPanel addRemovePanel = new JPanel(new FlowLayout());
		addRemovePanel.add(addButton);
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doAdd();}
		});		
		addRemovePanel.add(removeButton);
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doRemove();}
		});
		tablePanel.add(addRemovePanel, BorderLayout.SOUTH);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(okButton);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doOK();}
		});		
		buttonPanel.add(cancelButton);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doCancel();}
		});
		panel.add(buttonPanel, BorderLayout.SOUTH);
		setTitle("Configure Review Data");
		this.setSize(700,400);
	}
	
	public ReviewDataEditorDialog(JFrame frame, String stepId, boolean showAdvice) {
		super(frame, ModalityType.APPLICATION_MODAL);
		this.stepId = stepId;
		String errorMsg = null;
		try {
			config = RecipeDataManager.getData().getReviewDataConfig(stepId);
			if(config == null ) {
				errorMsg ="null configuration for stepId" + stepId;
			}
		}
		catch(Exception e) {
			errorMsg ="exception getting config for stepId" + stepId;
		}		
		if(errorMsg != null) {
			JOptionPane.showConfirmDialog(frame, errorMsg, "Configuration Error", JOptionPane.WARNING_MESSAGE);
		}
		initUI(config, showAdvice);
	}
	
	private void doOK() {
		RecipeDataManager.getData().setReviewDataConfig(stepId, config);
		this.dispose();		
	}
	
	private void doCancel() {
		this.dispose();		
	}

	private void doAdd() {
		tableModel.addRow();
	}
	
	private void doRemove() {
		int selectedRow = table.getSelectedRow();
		tableModel.removeSelectedRow(selectedRow);
	}

}

