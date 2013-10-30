/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tum.opensim.somview;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

/**
 * Hexagon superclass - will be inherited by node hexagon and distance hexagon.
 * Since it implements @see Shape , it can be draw with sub pixel accuracy 
 * to e.g. a Panel or any class using @see Graphics2D
 */
public abstract class Hexagon implements Shape
{
    /**
     * If this Hexagon is not a node, this value encodes the distance between two nodes. If the Hexagon is a node,
     * this value is the interpolated value of its neighbors.
     */
    protected double distance;
    
    
    /**
     * The GeneralPath contains the points and lines to outline the hexagon.
     */
    private GeneralPath path;

    /**
     * Neighbors of this hexagon
     */
    private Vector<Hexagon> neighbors;
    /**
     * Colors and positions -> used for interpolation
     */
    private Vector<HexagonInterpolationPaintContext.PointAndColor> pointsAndColors;
    /**
     * SOM map that this hexagon is part of
     */
    private SOMMap map;
    
    /**
     * The constructor for the Hexagon class.
     * @param path Path that corresponds to the hexagon-shape
     * @param map SOM that this hexagon is part of
     */
    public Hexagon(GeneralPath path, SOMMap map) {
        this.pointsAndColors =  new Vector<HexagonInterpolationPaintContext.PointAndColor>();
        this.neighbors = new Vector<Hexagon>();
        this.path = path;
        this.map = map;
    }
    
    //Some Methods that need to be implemented for the Shape class
    
    @Override
	public boolean contains(double x, double y) {
        return path.contains(x, y);
    }

    @Override
	public boolean contains(double x, double y, double w, double h) {
        return path.contains(x, y, w, h);
    }

    @Override
	public boolean contains(Point2D p) {
        return path.contains(p);
    }
    
    @Override
	public boolean contains(Rectangle2D r) {
        return path.contains(r);
    }
    
    @Override
	public Rectangle getBounds() {
        return path.getBounds();
    }
    
    @Override
	public Rectangle2D getBounds2D() {
        return path.getBounds2D();
    }
    
    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        return path.getPathIterator(at);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return path.getPathIterator(at, flatness);
    }


    @Override
    public boolean intersects(double x, double y, double w, double h) {
        return path.contains(x, y, w, h);
    }

    @Override
    public boolean intersects(Rectangle2D r) {
        return path.intersects(r);
    }
    
    //toString will be implemented by child classes
    @Override
    public abstract String toString();
    
    public Point2D getUpperLeftCorner()
    {
        return new Point2D.Double(this.getBounds2D().getMinX(),this.getBounds2D().getMinY());
    }
    
    /**
     * Returns the center point of the hexagon.
     * @return the center of this hexagon
     */
    public Point2D getCenter()
    {
        //Use the bounding box of this hexagon to determine its center
        return new Point2D.Double(this.getBounds2D().getCenterX(), (int)this.getBounds2D().getCenterY()) {};
    }
    
    /**
     * Getter for the hexagon value. This can either be the value 
     * of a node or the distance between two neighboring nodes.
     * @return the value of this hexagon
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Setter for the distance value.
     * @param d
     */
    void setDistance(double d) {
        distance = d;
    }
    
    /**
     * Adds a neighbor to the list of hexagon neighbors
     * @param h neighbor
     */
    public void addNeighbor(Hexagon h)
    {
        if(map == null)
            return;
        neighbors.add(h);
        if(h != null)
        {
            pointsAndColors.add(
                    new HexagonInterpolationPaintContext.PointAndColor(
                            h.getCenter(), 
                            map.getHexagonColor(h), 
                            h.getDistance()));
        }else
            pointsAndColors.add(null);
    }
    
    /**
    * Returns position and color values of the neighbors of this hexagon
    * @returns pos and color of neighbors
    **/
    public HexagonInterpolationPaintContext.PointAndColor[] getNeighborPoints()
    {
        return pointsAndColors.toArray(new HexagonInterpolationPaintContext.PointAndColor[pointsAndColors.size()]);
    }
}
