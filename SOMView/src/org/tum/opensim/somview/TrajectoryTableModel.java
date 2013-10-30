/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tum.opensim.somview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.LinkedList;
import javax.swing.DefaultCellEditor;
import javax.swing.table.AbstractTableModel;

/**
 * Table model for the table that contains information and options
 * for the loaded trajectories
 */
public class TrajectoryTableModel extends AbstractTableModel {

    private static LinkedList<SOMTrajectory> trajectories;
    
    /**
    * Constructor
    * @param trajs list of trajectories this model will contain
    **/
    public TrajectoryTableModel(LinkedList<SOMTrajectory> trajs) {
        trajectories = trajs;
    }
    
    @Override
    public int getRowCount() {
        if(trajectories!=null)
        {
            return trajectories.size();
        }
        else
        {
            return 0;
        }
    }
    
    @Override
    public Class getColumnClass(int c) {
        //Just return the different classes
        switch(c)
        {
            //Display trajectory?
            case 0: return Boolean.class;
            //Trajectory label
            case 1: return String.class;
            //Sync trajectory?
            case 2: return Boolean.class;
            //Trajectory color
            case 3: return Color.class;
            //Stroke of line the trajectory will be drawn with
            case 4: return DefaultCellEditor.class;
            //Width of line the trajectory will be drawn with
            case 5: return Float.class;
            //Space between trajectories
            case 6: return Float.class;
                
            default: return String.class;
        }
        
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        //Every column except for the "label" column is editable
        if(columnIndex==1)
            return false;
        return true;
    }

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex)
        {
            case 0: return trajectories.get(rowIndex).display;
                
            case 1: return trajectories.get(rowIndex).getLabel();
            case 2: return trajectories.get(rowIndex).displayNSync;
            case 3: return trajectories.get(rowIndex).getColor();
            case 4: return trajectories.get(rowIndex).getStroke();
            case 5: return trajectories.get(rowIndex).getLineWidth();
            case 6: return trajectories.get(rowIndex).getOffset();
                
            default: return null;
                
        }
    }
    
    @Override
    public void setValueAt(Object a, int rowIndex, int columnIndex)
    {
        switch(columnIndex)
        {
            case 0: trajectories.get(rowIndex).display = (Boolean)a;
                break;
            case 2: trajectories.get(rowIndex).displayNSync = (Boolean)a;
                break;
            case 3: trajectories.get(rowIndex).setColor((Color) a);
                break;
            case 4: trajectories.get(rowIndex).setStroke((BasicStroke)a);
                break;
            case 5: trajectories.get(rowIndex).setThickness((Float)a);
                break;
            case 6: trajectories.get(rowIndex).setOffset((Float)a);
                break;
            default:
        }
        fireTableCellUpdated(rowIndex, columnIndex);
        
    }
    
}
