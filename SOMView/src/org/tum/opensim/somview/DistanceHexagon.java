package org.tum.opensim.somview;

import java.awt.geom.GeneralPath;

/**
 * Distance hexagon class - corresponds to the distance between two 
 * neighboring nodes in the SOM
 */
public class DistanceHexagon extends Hexagon{
    
    
    public DistanceHexagon(GeneralPath path, SOMMap map) 
    {
        super(path, map);
    }
    
    @Override
    public String toString()
    {
        
        return "distance: "+super.distance;
        
    }
}
