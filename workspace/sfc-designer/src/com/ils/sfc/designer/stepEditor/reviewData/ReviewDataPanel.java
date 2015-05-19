package com.ils.sfc.designer.stepEditor.reviewData;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.ReviewDataConfig;
import com.ils.sfc.designer.ButtonPanel;
import com.ils.sfc.designer.EditorPane;
import com.ils.sfc.designer.stepEditor.StepEditorController;
import com.inductiveautomation.ignition.client.script.ClientDBUtilities;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

@SuppressWarnings("serial")
public class ReviewDataPanel extends JPanel implements EditorPane {
	private static LoggerEx logger = LogUtil.getLogger(ReviewDataPanel.class.getName());
	private final StepEditorController controller;
	private ButtonPanel buttonPanel = new ButtonPanel(true, true, true, false, false, false);
	private ReviewDataTableModel tableModel;
	private JTable table;
	private PropertyValue<String> pvalue;
	private ReviewDataConfig config;
	private JPanel tablePanel;
	
	public ReviewDataPanel(StepEditorController controller) {
		super(new BorderLayout());
		this.controller = controller;
		add(buttonPanel, BorderLayout.NORTH);
		buttonPanel.getAcceptButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doAccept();}
		});
		buttonPanel.getAddButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doAdd();}
		});
		buttonPanel.getRemoveButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doRemove();}
		});
	}

	@Override
	public void activate() {
		controller.slideTo(StepEditorController.REVIEW_DATA);
	}

	private void doAccept() {
		try {
			String json = config.toJSON();
			System.out.println("setting " + pvalue.getProperty().getName() + " config " + json);
			controller.getPropertyEditor().getPropertyEditor().setSelectedValue(json);
			controller.getPropertyEditor().activate();
		} 
		catch(Exception e ) {
			logger.error("Error serializing review data config");
		}
	}

	private void doAdd() {
		tableModel.addRow();
	}
	
	private void doRemove() {
		int selectedRow = table.getSelectedRow();
		tableModel.removeSelectedRow(selectedRow);
	}

	public void setConfig(PropertyValue<String> pvalue) {
		// Changing to a different config 
		if(config != null) {
			doAccept();
		}
		this.pvalue = pvalue;
		String json = (String) pvalue.getValue();
		if(json != null && json.length() > 0) {
			try {
				config = ReviewDataConfig.fromJSON(json);
			} catch (Exception e) {
				config = new ReviewDataConfig();
				logger.error("Error deserializing review data config");
			} 
		}
		else {
			config = new ReviewDataConfig();
		}
		boolean showAdvice = pvalue.getProperty().equals(IlsProperty.PRIMARY_REVIEW_DATA_WITH_ADVICE) ||
				pvalue.getProperty().equals(IlsProperty.SECONDARY_REVIEW_DATA_WITH_ADVICE);
		tableModel = new ReviewDataTableModel(showAdvice);
		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDefaultEditor(Object.class, new ReviewDataCellEditor());
		table.setDefaultRenderer(Object.class, new ReviewDataCellRenderer());
		table.setRowHeight(20);
		table.setRowMargin(3);	
		table.setShowGrid(true);
		tableModel.setConfig(config);
		if(tablePanel != null) remove(tablePanel);
		tablePanel = new JPanel(new BorderLayout());	
		tablePanel.setBorder(new EmptyBorder(10,10,10,10));
		add(tablePanel, BorderLayout.CENTER);
		JScrollPane scroll = new JScrollPane(table);
		scroll.setBorder(
			new CompoundBorder(
				new EmptyBorder(10,10,0,10), 
				new LineBorder(Color.black)));
		tablePanel.add(scroll, BorderLayout.CENTER);
	}
	
}
