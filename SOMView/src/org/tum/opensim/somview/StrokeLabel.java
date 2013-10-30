/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tum.opensim.somview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JLabel;

/**
 * Label that displays a stroke
 */
public class StrokeLabel extends JLabel{
    private BasicStroke s;
    /**
    * Constructor
    * @param s stroke that will be drawn in the label
    **/
    StrokeLabel(BasicStroke s)
    {
        super("" + s);
        this.s = s;
    }

    @Override
    protected void paintComponent(Graphics g) {
        
        if(g instanceof Graphics2D)
        {
            //Set background and paint color
            //and draw the stroke
            Graphics2D g2d = (Graphics2D) g;
            g2d.setBackground(Color.white);
            g2d.setColor(Color.black);
            g2d.setStroke(this.s);
            g2d.drawLine(0, (int)((double)this.getHeight()/2.0), this.getWidth(), (int)((double)this.getHeight()/2.0));
        }else{
            super.paintComponent(g);
        }       
    }
    
    
}
