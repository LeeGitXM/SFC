package com.ils.sfc.designer.recipeEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultTreeModel;

import com.ils.sfc.designer.recipeEditor.RecipeDataBrowser.MapTreeNode;

import static java.awt.GridBagConstraints.*;
import static com.ils.sfc.designer.recipeEditor.RecipeDataTypes.*;

@SuppressWarnings("serial")
public class CreateRecipeDataDialog extends JDialog {
	private JTextField keyField = new JTextField();
	private JTextField labelField = new JTextField();
	private JTextArea descriptionArea = new JTextArea();
	private JTextArea adviceArea = new JTextArea();
	private JTextArea helpArea = new JTextArea();
	private JComboBox<String> typeCombo = new JComboBox<String>(RECIPE_DATA_TYPES);
	
	static class Attribute {
		public String key;
		public Object defaultValue;
		
		public Attribute(String key, Object defaultValue) {
			super();
			this.key = key;
			this.defaultValue = defaultValue;
		}				
	}
	
	private MapTreeNode selectedNode;	
	private int TEXT_FIELD_COLUMNS = 15;
	private DefaultTreeModel treeModel;
	
	public CreateRecipeDataDialog(DefaultTreeModel treeModel, MapTreeNode selectedNode) {
		this.treeModel = treeModel;
		this.selectedNode = selectedNode;
		JPanel mainPanel = new JPanel();
		getContentPane().add(mainPanel);
		mainPanel.setLayout(new BorderLayout());
		JPanel innerPanel = new JPanel(new GridBagLayout());
		mainPanel.add(innerPanel);
		GridBagConstraints con = new GridBagConstraints();

		setConstraints(con, EAST, NONE, 1, 1, 0, 0, new Insets(2, 0, 2, 5), 0, 0);
		innerPanel.add(new JLabel("Type:", SwingConstants.RIGHT), con);
		setConstraints(con, WEST, BOTH, 1, 1, 1, 0, new Insets(2, 5, 2, 0), 0, 0);
		innerPanel.add(typeCombo, con);
		typeCombo.setSelectedIndex(0);

		setConstraints(con, EAST, NONE, 1, 1, 0, 1, new Insets(2, 0, 2, 5), 0, 0);
		innerPanel.add(new JLabel("Key:", SwingConstants.RIGHT), con);
		setConstraints(con, WEST, BOTH, 1, 1, 1, 1, new Insets(2, 5, 2, 0), 0, 0);
		keyField.setColumns(TEXT_FIELD_COLUMNS);
		innerPanel.add(keyField, con);

		setConstraints(con, EAST, NONE, 1, 1, 0, 2, new Insets(2, 0, 2, 5), 0, 0);
		innerPanel.add(new JLabel("Label:", SwingConstants.RIGHT), con);
		setConstraints(con, WEST, BOTH, 1, 1, 1, 2, new Insets(2, 5, 2, 0), 0, 0);
		labelField.setColumns(TEXT_FIELD_COLUMNS);
		innerPanel.add(labelField, con);

		setConstraints(con, EAST, BOTH, 1, 1, 0, 3, new Insets(2, 0, 2, 5), 0, 0);
		innerPanel.add(new JLabel("Description:", SwingConstants.RIGHT), con);
		setConstraints(con, WEST, BOTH, 1, 1, 1, 3, new Insets(2, 5, 2, 0), 0, .5);
		descriptionArea.setBorder(new LineBorder(Color.black, 1));
		descriptionArea.setLineWrap(true);
		JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
		descriptionScroll.setPreferredSize(new Dimension(50,30));
		//descriptionScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		innerPanel.add(descriptionScroll, con);

		setConstraints(con, EAST, BOTH, 1, 1, 0, 4, new Insets(2, 0, 2, 5), 0, 0);
		innerPanel.add(new JLabel("Advice:", SwingConstants.RIGHT), con);
		setConstraints(con, WEST, BOTH, 1, 1, 1, 4, new Insets(2, 5, 2, 0), 0, .5);
		adviceArea.setBorder(new LineBorder(Color.black, 1));
		adviceArea.setLineWrap(true);
		JScrollPane adviceScroll = new JScrollPane(adviceArea);
		adviceScroll.setPreferredSize(new Dimension(50,30));
		//descriptionScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		innerPanel.add(adviceScroll, con);

		setConstraints(con, EAST, BOTH, 1, 1, 0, 5, new Insets(2, 0, 2, 5), 0, 0);
		innerPanel.add(new JLabel("Help:", SwingConstants.RIGHT), con);
		setConstraints(con, WEST, BOTH, 1, 1, 1, 5, new Insets(2, 5, 2, 0), 0, .5);
		helpArea.setLineWrap(true);
		helpArea.setBorder(new LineBorder(Color.black, 1));
		JScrollPane helpScroll = new JScrollPane(helpArea);
		helpScroll.setPreferredSize(new Dimension(50,30));
		//helpScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		innerPanel.add(helpScroll, con);
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		JButton okButton = new JButton("OK");
		buttonPanel.add(okButton);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doOk();}			
		});
		JButton cancelButton = new JButton("Cancel");
		buttonPanel.add(cancelButton);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doCancel();}			
		});
		
		setModal(true);
		setSize(300,300);
		setTitle("Create Recipe Data");
	}
	
	private void doOk() {
		String type = (String)typeCombo.getSelectedItem();
		String key = keyField.getText().trim();
		String label = labelField.getText().trim();
		String description = descriptionArea.getText().trim();
		String advice = adviceArea.getText().trim();
		String help = helpArea.getText().trim();
		
		if(key.isEmpty()) {
			error("Key is required");
			return;
		}
		if(key.indexOf(' ') >= 0) {
			error("Key cannot contain spaces");
			return;
		}
		
		MapTreeNode newNode = addAttribute(selectedNode, key, new HashMap<String,Object>());
		addAttribute(newNode, "type", type);
		addAttribute(newNode, "label", label);
		addAttribute(newNode, "description", description);
		addAttribute(newNode, "advice", advice);
		addAttribute(newNode, "help", help);
		
		if(type.equals(GROUP)) {
			newNode.setContentsEditable(true);
			addAttribute(newNode, key, new HashMap<String,Object>());
		}
		else if(type.equals(INPUT)) {
			addUnits(newNode);
			addIOAttributes(newNode);
		}
		else if(type.equals(OUTPUT)) {
			addUnits(newNode);
			addIOAttributes(newNode);	
			addOutputAttributes(newNode);
		}
		else if(type.equals(OUTPUT_RAMP )) {
			addUnits(newNode);			
		}
		else if(type.equals(MATRIX)) {
			addUnits(newNode);			
			addValue(newNode);			
		}
		else if(type.equals(QUANTITY_ARRAY)) {
			addUnits(newNode);			
			addValue(newNode);			
		}
		else if(type.equals(SQC)) {
			addUnits(newNode);			
			for(Attribute att: SQC_ATTRIBUTES) {
				addAttribute(newNode, att.key, att.defaultValue);
			}			
		}
		else if(type.equals(VALUE_ARRAY )) {
			addUnits(newNode);			
			addValue(newNode);			
		}
		else if(type.equals(SINGLE_VALUE)) {
			addUnits(newNode);			
			addValue(newNode);			
		}
		else if(type.equals(QUANTITY_LIST)) {
			addValue(newNode);						
		}
		else if(type.equals(SEQUENCE)) {
			addValue(newNode);			
		}
		else if(type.equals(TEXT_LIST)) {
			addValue(newNode);						
		}
		
		dispose();
	}
	
	private void addUnits(MapTreeNode parentNode) {
		addAttribute(parentNode, "units", "");
	}

	private void addValue(MapTreeNode parentNode) {
		addAttribute(parentNode, "value", "");
	}

	private void addIOAttributes(MapTreeNode parentNode) {
		for(Attribute att: IO_ATTRIBUTES) {
			addAttribute(parentNode, att.key, att.defaultValue);
		}
	}

	private MapTreeNode addAttribute(MapTreeNode parentNode, String name, Object value) {
		parentNode.getMap().put(name, value);
		MapTreeNode childNode = new MapTreeNode(name, value);
		treeModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
		return childNode;
	}
	
	private void addOutputAttributes(MapTreeNode parentNode) {
		for(Attribute att: OUTPUT_ATTRIBUTES) {
			addAttribute(parentNode, att.key, att.defaultValue);
		}
	}

	private void error(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Invalid Data", JOptionPane.WARNING_MESSAGE);		
	}

	private void doCancel() {
		dispose();
	}
	
	private void setConstraints(GridBagConstraints con, int anchor, int fill,
		int gridheight, int gridwidth, int gridx, int gridy, Insets insets, double weightx, double weighty ) {
		con.anchor = anchor;
		con.fill = fill;
		con.gridheight = gridheight;
		con.gridwidth = gridwidth;
		con.gridx = gridx;
		con.gridy = gridy;
		con.insets = insets;
		con.weightx = weightx;
		con.weighty = weighty;
	}
	
}
