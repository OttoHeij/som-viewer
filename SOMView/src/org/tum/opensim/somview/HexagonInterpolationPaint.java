package org.tum.opensim.somview;

import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.util.List;

/**
 * Paint class
 * Just needs to be set as paint for a @see Graphics2D
 * and the hexagons will be drawn in an interpolated, smoothed fashion (with contourlines)
 * This way, the map stays the same size and the user can easily switch between interpolated 
 * and non-interpolated
 */
public class HexagonInterpolationPaint implements Paint{

    private HexagonInterpolationPaintContext.PointAndColor points[];
    private HexagonInterpolationPaintContext.PointAndColor hexCenter;
    //Just some parameters for the contourlines and the interplation
        //Radius that the color of the hexagon that is drawn with this paint will 
        //affect
    private double centerInterpolationRadius;
        //values at which contourlines will be drawn
    private List<Double> contourLineDists;
        //base-thickness of the contourlines
    private double cLineThickness;
        //compensation value in x-direction for device coordinates
    private int compensateX;
    
    public HexagonInterpolationPaint(HexagonInterpolationPaintContext.PointAndColor points[], 
            HexagonInterpolationPaintContext.PointAndColor hexCenter,
            int compensateX,
            double centerInterpolationRadius,
            List<Double> contourLineDists, double cLineThickness)
    {
        this.points = points;
        this.hexCenter = hexCenter;
        this.centerInterpolationRadius = centerInterpolationRadius;
        this.contourLineDists = contourLineDists;
        this.cLineThickness = cLineThickness;
        this.compensateX = compensateX;
    }
    
    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
        return new HexagonInterpolationPaintContext(this.points,this.hexCenter, this.compensateX, this.centerInterpolationRadius,
                this.contourLineDists,this.cLineThickness);
    }

    public int getTransparency() {
        return OPAQUE;
    }

    
}
