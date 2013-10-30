/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tum.opensim.somview;

import java.awt.BasicStroke;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Table cell renderer that uses the @see StrokeLabel to display strokes
 */
public class StrokeTableCellRenderer extends DefaultTableCellRenderer{

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if(value instanceof BasicStroke)
        {
            //Return a stroke label
            return new StrokeLabel((BasicStroke) value);
        }
        //Somebody is using this label in the wrong way -> fall back to ancestor function
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
    
}
