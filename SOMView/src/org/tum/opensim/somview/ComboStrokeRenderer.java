package org.tum.opensim.somview;

import java.awt.BasicStroke;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Renderer for listelements that displays basic strokes (@BasicStroke)
 */
public class ComboStrokeRenderer extends DefaultListCellRenderer{

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if(value instanceof BasicStroke)
        {
            //Return a new label that displays basic strokes
            return new StrokeLabel((BasicStroke) value);
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
    
}
