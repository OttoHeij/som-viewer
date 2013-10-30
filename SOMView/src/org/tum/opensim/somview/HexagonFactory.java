/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tum.opensim.somview;

import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;


/**
 * Factory class that produces hexagons
 */
public class HexagonFactory {

    private double tile_height;
    private double tile_width;
    private double triangle_height;
    private double vertical_offset;

    /**
    * Contructor
    * @param s hexagon size - radius of the inscribed circle
    **/
    public HexagonFactory(int s){
        tile_width = s* Math.sqrt(3);
        tile_height = s * 2;
        triangle_height = s /2;
        vertical_offset = s * 3 / 2;
    }

    /**
    * Given the center of a hexagon, this method
    * will return a @see GeneralPath object that represents a hexagon
    *
    * @param xi x-position of the center
    * @param yi y-position of the center
    * @return hexagon shaped path centered at the specified position
    **/
    public GeneralPath computeHexagonPositions(int xi, int yi)
    {
        // an offset for the x-coordinate has to be set 
        // according to the line in the hexagon grid
        double x_offset = 0;
        switch(yi % 4)
        {
            case 1: x_offset = tile_width / 2; 
            break;
            case 2: x_offset = tile_width;
            break;
            case 3: x_offset = tile_width / 2;
            break;
            default:
                x_offset = 0;
                break;
        }
        
        //xoffset has a basic value in order to get borders in the image
        x_offset += getHexagonWidth();
        double y_offset = getHexagonHeight();
        
        double xleft = x_offset + xi * tile_width;
        double yuppermid = y_offset + triangle_height + yi * vertical_offset;
        double xmid = x_offset + tile_width / 2 + xi * tile_width;
        double ytop = y_offset + yi * vertical_offset;
        double xright = x_offset + (xi + 1) * tile_width;
        double ylowermid = y_offset + (yi + 1) * vertical_offset;
        double ybottom = y_offset + tile_height + yi * vertical_offset;

        
        
        GeneralPath path = new GeneralPath(Path2D.WIND_NON_ZERO,6);
        path.moveTo(xleft, yuppermid);
        path.lineTo(xmid, ytop);
        path.lineTo(xright, yuppermid);
        path.lineTo(xright, ylowermid);
        path.lineTo(xmid, ybottom);
        path.lineTo(xleft, ylowermid);
        path.lineTo(xleft, yuppermid);

        return path;
    }

    /**
    * Getter for the height of the hexagon
    * @return height of the hexagon
    **/
    public double getHexagonHeight() {
        return this.tile_height - this.triangle_height;
    }
    
    /**
    * Getter for the width of the hexagon
    * @return width of the hexagon
    **/
    public double getHexagonWidth() {
        return tile_width;
    }
}
