package com.ils.sfc.designer;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.ils.sfc.common.PythonCall;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

/** A generic unit chooser. initUI() must be called prior to use; the reason this
 *  is not in the constructor is that the system may not be ready to load units
 *  at initialization time.
 */
@SuppressWarnings("serial")
public class UnitChooserPanel extends JPanel {
	private static LoggerEx logger = LogUtil.getLogger(UnitChooserPanel.class.getName());
	private JComboBox<String> typesCombo = new JComboBox<String>();
	private JComboBox<String> unitsCombo = new JComboBox<String>();
	
	public UnitChooserPanel() {
		initUI();
		typesCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getUnitsForType();
			}
		});
	}

	private void initUI() {
		setLayout(new GridBagLayout());
		GridBagConstraints con = new GridBagConstraints();

		DesignerUtil.setConstraints(con, EAST, NONE, 1, 1, 0, 0, new Insets(2, 0, 2, 5), 0, 0);
		add(new JLabel("Type:", SwingConstants.RIGHT), con);
		DesignerUtil.setConstraints(con, WEST, BOTH, 1, 1, 1, 0, new Insets(2, 5, 2, 0), 0, 0);
		add(typesCombo, con);

		DesignerUtil.setConstraints(con, EAST, NONE, 1, 1, 0, 1, new Insets(2, 0, 2, 5), 0, 0);
		add(new JLabel("Units:", SwingConstants.RIGHT), con);
		DesignerUtil.setConstraints(con, WEST, BOTH, 1, 1, 1, 1, new Insets(2, 5, 2, 0), 0, 0);
		add(unitsCombo, con);
	}

	/** Load the unit types. */
	public void initTypes() {
		try {
			String[] unitTypes = PythonCall.toArray(PythonCall.GET_UNIT_TYPES.exec());
			for(String unitType: unitTypes) {
				typesCombo.addItem(unitType);
			}
		} catch (JythonExecException e) {
			logger.error("Error initializing unit types", e);
		}
		typesCombo.setSelectedIndex(0);
	}

	public void setUnit(String unitName) throws JythonExecException {
		String unitType = (String) PythonCall.GET_TYPE_OF_UNIT.exec(unitName);
		typesCombo.setSelectedItem(unitType);
		if(unitType != null) {
			unitsCombo.setSelectedItem(unitName);
		}
	}
	
	public String getSelectedType() {
		return (String) typesCombo.getSelectedItem();
	}

	public String getSelectedUnits() {
		return (String) unitsCombo.getSelectedItem();
	}

	private void getUnitsForType() {
		String selectedType = (String) typesCombo.getSelectedItem();
		try {
			Object[] args = {selectedType};
			String[] units = PythonCall.toArray(PythonCall.GET_UNITS_OF_TYPE.exec(args));
			unitsCombo.removeAllItems();
			for(String unit: units) {
				unitsCombo.addItem(unit);
			}
			if(units.length > 0) {
				unitsCombo.setSelectedIndex(0);
			}
			else {
				logger.error("No units for type: " + selectedType);
			}
		} catch (JythonExecException e) {
			logger.error("Error getting units for type " + selectedType, e);
		}
	}
	

}
