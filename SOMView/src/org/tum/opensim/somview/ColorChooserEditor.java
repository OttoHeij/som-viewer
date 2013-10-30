/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tum.opensim.somview;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import javax.swing.AbstractCellEditor;
import javax.swing.CellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author dani
 */
public class ColorChooserEditor extends AbstractCellEditor implements TableCellEditor {

  private JLabel delegate;

  //Current Color
  Color savedColor;
  
  public static TableCellEditor getColorChooserEditor(){
      return new ColorChooserEditor();
  }
  
  public ColorChooserEditor() {
    super();
    //Use a label to display the current color
    this.delegate = new JLabel(" ");
    //The color is displayed by a icon that is drawn to a label
    this.delegate.setIcon(new ColorIcon(this.delegate.getWidth(), 
            this.delegate.getHeight(), savedColor));
    delegate.addMouseListener(new MouseAdapter() 
    {
            @Override
            public void mouseClicked(MouseEvent e) {
                //If the user clicks on the color label -> show a color chooser dialog
                Color color = JColorChooser.showDialog(delegate, "Color Chooser", savedColor);
                ColorChooserEditor.this.changeColor(color);
                ColorChooserEditor.this.stopCellEditing();
            }
    });
    //Initialize the color with white
    savedColor = Color.red;
    changeColor(savedColor);
  }

  /**
  * Returns the table cell color
  * @returns the color that was chosen for this table cell
  **/
  public Object getCellEditorValue() {
    return savedColor;
  }

  /**
  * Changes the label color
  * @param color the new color
  **/
  private void changeColor(Color color) {
    if (color != null) {
      savedColor = color;
      delegate.setBackground(color);
      this.delegate.setIcon(new ColorIcon(this.delegate.getWidth(), 
            this.delegate.getHeight(), savedColor));
      delegate.repaint();
    }
  }

  /**
  * Get the editor component of the table cell.
  * In this case, this is a label
  *
  * @return label that is the editor of this cell 
  **/
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
      int row, int column) {
    changeColor((Color) value);
    return delegate;
  }
}

/**
* A unicolored icon
**/
class ColorIcon implements Icon
{
    private int width, height;
    private Color color;
    /**
    * Constructor
    * 
    * @param width width of the icon
    * @param height height of the icon
    * @param color color the icon will be drawn with
    **/
    public ColorIcon(int width, int height, Color color)
    {
        this.width = width;
        this.height = height;
        this.color = color;
    }
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    @Override
    public int getIconWidth() {
        return this.width;
    }

    @Override
    public int getIconHeight() {
        return this.height;
    }
    
}