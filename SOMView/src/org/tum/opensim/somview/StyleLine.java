/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tum.opensim.somview;

import java.awt.BasicStroke;

/**
 * Contains some predefined strokes
 */
public class StyleLine {
    
    //Some examples of different linetypes
    public static final BasicStroke SIMPLE = new BasicStroke(2.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f);
    public static final BasicStroke DASHED_SHORT = new BasicStroke(2.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, new float[]{5.0f, 2.0f}, 0.0f );
    public static final BasicStroke DASHED_LONG = new BasicStroke(2.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, new float[]{10.0f, 4.0f}, 0.0f );
    public static final BasicStroke DOTS = new BasicStroke(2.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, new float[]{1.0f, 2.0f}, 0.0f );
    
    /**
    * Provides all the predefined strokes
    * @return predefined strokes as stroke array
    **/
    public static BasicStroke[] getAllLineTypes()
    {
        BasicStroke[] strokes = new BasicStroke[4];
        strokes[0] = SIMPLE;
        strokes[1] = DASHED_SHORT;
        strokes[2] = DASHED_LONG;
        strokes[3] = DOTS;
        return strokes;
    }
    
            
}