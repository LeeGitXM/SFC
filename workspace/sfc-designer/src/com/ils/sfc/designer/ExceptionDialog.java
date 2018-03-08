/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.designer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.inductiveautomation.ignition.designer.model.DesignerContext;

/**
 * Allow the user to define database connections and tag providers. This
 * applies to all projects globally. 
 */

public class ExceptionDialog extends JDialog {
	protected static final Dimension TEXT_SIZE  = new Dimension(625, 350);
//	private static final long serialVersionUID = 2112388376824434427L;
	private final int DIALOG_WIDTH = 650;
	private final int DIALOG_HEIGHT = 400;
	//private final IlsSfcRequestHandler requestHandler;
	private final String exceptionMsg;
	protected JTextField mainTimeFactorField;
	
	public ExceptionDialog(DesignerContext ctx, String msg) {
		super(ctx.getFrame());
		this.setTitle("SFC Error");
		this.exceptionMsg = msg;
//		this.rb = ResourceBundle.getBundle("com.ils.sfc.designer.designer");  // designer.properties
//		this.requestHandler = new IlsSfcRequestHandler();
		setModal(true);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.setPreferredSize(new Dimension(DIALOG_WIDTH,DIALOG_HEIGHT));
        initialize();  
	}
	
	private void initialize() {
		
		setLayout(new BorderLayout());
		JPanel internalPanel = new JPanel();
		JTextArea textArea = new JTextArea();
		textArea.setSize(TEXT_SIZE);
		textArea.setText(exceptionMsg);
//		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		internalPanel.add(textArea);
	
		
		add(internalPanel,BorderLayout.CENTER);
		
		// The OK button simply closes the dialog
		JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.SOUTH);
		JButton okButton = new JButton("OK");
		buttonPanel.add(okButton, "");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
	
	
}
