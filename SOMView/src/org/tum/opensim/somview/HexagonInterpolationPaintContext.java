package org.tum.opensim.somview;

import java.awt.Color;
import java.awt.PaintContext;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.List;

/**
 * Paintcontext that draws hexagons in an interpolated fashion
 */
public class HexagonInterpolationPaintContext implements PaintContext{

    //Positions and colors of the neighboring hexagons
    // in case a neighbor does not exist the corresponding arrayelement is Null
    private PointAndColor points[];
    //Center point of the hexagon that is drawn with this paintcontext
    private PointAndColor hexCenter;
    //radius around the center of the hexagon drawn with this paintcontext that is
    //affected by interpolation
    private double centerInterpolationRadius;
    //values at which contourlines are drawn
    private List<Double> contourLineDists;
    //base thickness of the contourlines
    private double cLineThickness;
    //compensation value in xdirection for device coordinates
    private int compensateX;
    
    /**
    * Constructor of the HexagonInterpolationPaintContext
    *
    * @param points Positions and colors of the neighboring hexagons.
    *               In case a neighbor does not exist
    *               => the corresponding array element is Null
    * @param hexCenter position of the center of the hexagon that is 
    *                  affected by this paintcontext
    * @param compensateX compensation value in x-direction for device coordinates
    * @param centerInterpolationRadius radius around the center of the hexagon drawn 
    *                                  with this paintcontext that is
    *                                  affected by interpolation
    * @param contourLineDists values at which contourlines will be drawn
    * @param cLineThickness base-thickness of the contourlines
    **/
    public HexagonInterpolationPaintContext(PointAndColor points[], PointAndColor hexCenter, 
            int compensateX,
            double centerInterpolationRadius,
            List<Double> contourLineDists, double cLineThickness) {
        this.points = points;
        this.hexCenter = hexCenter;
        this.centerInterpolationRadius = centerInterpolationRadius;
        this.contourLineDists = contourLineDists;
        this.cLineThickness = cLineThickness;
        this.compensateX = compensateX;
    }
    
    
    public void dispose() {
    }

    public ColorModel getColorModel() {
        return ColorModel.getRGBdefault();
    }

    /**
    * Draw the interpolated version of a umatrix hexagon
    * Using the neighbors of the hexagon to create a smoothed appearance
    **/
    public Raster getRaster(int x, int y, int w, int h) {
        
        //Create a writable raster that is compatible to the current color model
        WritableRaster raster = getColorModel().createCompatibleWritableRaster(w, h);
        
        //pixels of this hexagon
        int[] data = new int[w * h * 4];
        
        //compansate x-direction device coordinates
        x -= compensateX;
        
        //Compute the distance between the centerpoint and all the
        //centerpoints of the neighboring hexagons
        Double distsToCenter[] = new Double[points.length];
        for(int p = 0; p < points.length; p++)
        {
            if(points[p] == null)
            {
                distsToCenter[p] = null;
            }else{
                distsToCenter[p] = points[p].point.distance(hexCenter.point);
            }
        }
        
        //For every Pixel: Interpolate
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                Double distsToPoint[] = new Double[points.length];
                double normFactor = 0.0;
                for(int p = 0; p < points.length; p++)
                {
                    //Don't take "null" neighbors into account
                    if(points[p] == null)
                    {
                        distsToPoint[p] = null;
                    }else{
                        //Compute the ratios between the distance of the
                        //pixel to the heyagon center and the distances between the
                        //hexagon center and the respective neighbor
                        //center        pixel         neighbor
                        //   |-----------o-----------------|
                        //   |_____________________________| center to neighbor
                        //   |___________| center to pixel
                        double dist = points[p].point.distance(x+i,y+j);
                        distsToPoint[p] = Math.max(1.0 - (dist / distsToCenter[p]),0.0);
                        normFactor += distsToPoint[p];
                    }
                }
                
                
                int base = (j * w + i) * 4;
                double distanceVal = 0.0;
                //initilize the colors
                data[base + 0] = 0;
                data[base + 1] = 0;
                data[base + 2] = 0;
                data[base + 3] = 255;

                double centerToPointDist = Math.max(1.0 - (hexCenter.point.distance(x+i,y+j)/centerInterpolationRadius), 0.0f);
                normFactor += centerToPointDist;
                
                
                //Interpolate between the colors of the neighbors
                //using the previously computed ratios
                for(int p = 0; p < points.length; p++)
                {
                    if(points[p] != null)
                    {
                        distsToPoint[p] /= normFactor;
                        data[base + 0] += (int)(distsToPoint[p].doubleValue()
                                * (double)points[p].color.getRed());
                        data[base + 1] += (int)(distsToPoint[p].doubleValue()
                                * (double)points[p].color.getGreen());
                        data[base + 2] += (int)(distsToPoint[p].doubleValue()
                                * (double)points[p].color.getBlue());
                        //interpolate distance
                        distanceVal += (distsToPoint[p].doubleValue()
                                * points[p].value);
                    }
                }
                //Also take the color of this hexagon into account
                //use the distance from the hexagon center to the current pixel 
                //for interpolation
                centerToPointDist /= normFactor;
                data[base + 0] += (int)(centerToPointDist
                        * (double)hexCenter.color.getRed());
                data[base + 1] += (int)(centerToPointDist
                        * (double)hexCenter.color.getGreen());
                data[base + 2] += (int)(centerToPointDist
                        * (double)hexCenter.color.getBlue());
                distanceVal += centerToPointDist * hexCenter.value;

                //Finally, draw the contourlines
                //iterate over all the countourline value and draw pixels black
                //if their corresponding distance value is within a certain range
                //to a contourline value
                //This range is determined by the contourline thickness
                for(Double cLine : this.contourLineDists)
                {
                    if (distanceVal < cLine.doubleValue() + this.cLineThickness/2.0
                            && distanceVal > cLine.doubleValue() - this.cLineThickness/2.0)
                    {
                        //=> the current pixel lies on a contour line
                        //=> draw it in black
                        data[base + 0] = 0;
                        data[base + 1] = 0;
                        data[base + 2] = 0;
                    }
                }
            }
            
        }
        //Set the color values that we just computed as pixels of the raster
        raster.setPixels(0, 0, w, h, data);
        return raster;
    }
    

    /**
    * Only contains a point, a color and some double value
    * (struct like use)
    **/
    static public class PointAndColor{
        public Point2D point;
        public Color color;
        public double value;
        public PointAndColor(Point2D point, Color color, double value)
        {
            this.point = point;
            this.color = color;
            this.value = value;
        }
    }
}
