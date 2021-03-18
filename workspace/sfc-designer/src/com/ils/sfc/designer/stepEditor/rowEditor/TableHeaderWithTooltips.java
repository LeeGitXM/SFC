package com.ils.sfc.designer.stepEditor.rowEditor;

import java.awt.event.MouseEvent;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

public class TableHeaderWithTooltips extends JTableHeader {
    String[] tooltips;

    public TableHeaderWithTooltips(TableColumnModel columnModel, String[] columnTooltips) {
      super(columnModel);//do everything a normal JTableHeader does
      this.tooltips = columnTooltips;//plus extra data
    }

    public String getToolTipText(MouseEvent e) {
        java.awt.Point p = e.getPoint();
        int index = columnModel.getColumnIndexAtX(p.x);
        int realIndex = columnModel.getColumn(index).getModelIndex();
        return this.tooltips[realIndex];
    }
}