package org.tum.opensim.somview;

import java.awt.Color;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Table cell renderer that renders a cell with a specified color
 * 
 */
public class ColorRenderer extends DefaultTableCellRenderer{

    @Override
    protected void setValue(Object value) {
        if(value instanceof Color)
        {
            //Set fore- and background and repaint the cell
            Color color = (Color) value;
            this.setBackground(color);
            this.setForeground(color);
            this.repaint();
        }
    }
    
}
