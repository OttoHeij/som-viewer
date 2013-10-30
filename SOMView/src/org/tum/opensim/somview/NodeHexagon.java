package org.tum.opensim.somview;

import java.awt.geom.GeneralPath;
import java.util.TreeMap;


/**
 * A NodeHexagon is a Hexagon object that represents a node in the SOM.
 */
public class NodeHexagon extends Hexagon implements Comparable<NodeHexagon> {
    
     /**
     * This TreeMap maintains the number of connections that this hexagon has to other nodehexagons.
     */
    private TreeMap<Hexagon,Integer> connections;
    
     /**
     * If the Hexagon is a node, this is the vector it represents.
     */
    private double[] vector;
    
    
    /**
     * The constructor for the Hexagon class.
     * @param x array of 6 x coordinates
     * @param y array of 6 y coordinates
     */
    public NodeHexagon(GeneralPath path, SOMMap map) {
        
        super(path, map);
        
        // instantiate connections map
        connections = new TreeMap<Hexagon,Integer>();
    }
    
    /**
     * Increments the counter for the connection to another hexagon.
     * and add the node hexagon to the list of neighboring node hexagons
     * @param connection neighboring node hexagon
     */
    public void addConnection(NodeHexagon connection){
        if(connections.containsKey(connection))
            connections.put(connection,new Integer(connections.get(connection).intValue()+1));
        else
            connections.put(connection,new Integer(1));
    }
    
    @Override
    public int compareTo(NodeHexagon o) {
        double thisdist=0;
        double otherdist = 0;
        
        for(int i=0;i<vector.length;i++)
        {
            thisdist += vector[i];
            otherdist += vector[i];
        }
        
        if(thisdist<otherdist) return -1;
        else if(thisdist==otherdist) return 0;
        else return 1;
    }
  
    /**
     * Getter for connections.
     * @return the TreeMap with the counts for all connected Hexagons.
     */
    public TreeMap<Hexagon, Integer> getConnections() {
        return connections;
    }
    
     /**
     * Getter for the node vector.
     * @return the value vector corresponding to this node hexagon
     */
    public double[] getVector() {
        return vector;
    }
    
    
    public void setVector(double[] vector) {
        this.vector = vector;
    }
    
    @Override
    public String toString()
    {
  
        String vec = "";
        for(int i=0;i<vector.length;i++)
        {
            vec += vector[i] + "; ";
        }
        return vec;
     
    }
}
