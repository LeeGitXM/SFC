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
import javax.swing.border.LineBorder;

import com.ils.sfc.common.recipe.RecipeDataManager;
import com.ils.sfc.common.recipe.ReviewDataConfig;

public class ReviewDataEditorDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private String stepId;
	private ReviewDataConfig config;
	private JButton okButton = new JButton("OK");
	private JButton cancelButton = new JButton("Cancel");
	private JButton addButton = new JButton("+");
	private JButton removeButton = new JButton("-");
	private ReviewDataTableModel tableModel = new ReviewDataTableModel();
	private JTable table = new JTable(tableModel);
	
	public ReviewDataEditorDialog(JFrame frame, String stepId) {
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
		
		if(config != null) {
			tableModel.setConfig(config);
		}
		else {
			JOptionPane.showConfirmDialog(frame, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		JPanel panel = new JPanel(new BorderLayout());
		getContentPane().add(panel);
		
		JPanel tablePanel = new JPanel(new BorderLayout());	
		tablePanel.setBorder(new LineBorder(Color.black));
		panel.add(tablePanel, BorderLayout.CENTER);
		tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

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

		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(okButton);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doOK();}
		});		
		buttonPanel.add(cancelButton);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doCancel();}
		});
		panel.add(buttonPanel, BorderLayout.SOUTH);
		setTitle("Review Data");
		this.setSize(400,200);
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

