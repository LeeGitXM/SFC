package com.ils.sfc.designer;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

/** A convenience class to extend if you just want to get column selection.
 *  Usage: 		
 *  table.getColumnModel().addColumnModelListener(new ColumnSelectionAdapter() {
		public void columnSelectionChanged(ListSelectionEvent e) {
			// whatever...
		}
	});
 * @author rforbes
 *
 */
public class ColumnSelectionAdapter implements TableColumnModelListener {

	@Override
	public void columnAdded(TableColumnModelEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void columnRemoved(TableColumnModelEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void columnMoved(TableColumnModelEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void columnMarginChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void columnSelectionChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
