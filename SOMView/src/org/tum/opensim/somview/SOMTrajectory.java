/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tum.opensim.somview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.LinkedList;
import javax.swing.JOptionPane;

/**
 * Class the represents a trajectory that can be drawn ontop of the umatrix
 */
public class SOMTrajectory {
    
 
	// ========== STATIC METHODS ============= //
	
    /**
     * This method reads a .dat file and interprets groups of vectors
     * as trajectories that can be displayed on top of the umatrix.
     * The .data files need to be compatible to the format as specified in the
     * @see <a href="http://www.cis.hut.fi/somtoolbox/package/papers/techrep.pdf">SOMToolbox documentation</a>
     * @param file
     * @param vecdim
     * @param som
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static LinkedList<SOMTrajectory> readFile(File file, int vecdim, SOMMap som) throws FileNotFoundException, IOException
    {
        // initialize streams
        FileInputStream fstream = new FileInputStream(file);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        // initialize linked lists
        LinkedList<SOMTrajectory> trajectories = new LinkedList<SOMTrajectory>();
        LinkedList<double[]> singleTrajectoryVectors = new LinkedList<double[]>();
       
        SOMTrajectory singleTrajectory = null;
        String line;
        
        String last_label = "";
        // read lines
        while((line = br.readLine()) != null)
        {
            // ignore comment lines
            if(!line.startsWith("#"))
            {
                if(line.replaceAll(" ", "").replaceAll("\t", "").equals(""))
                {
                    //In case the line is empty, skip it
                    continue;
                }
                String[] tokens = line.trim().split(" ");

                // if there is only one token, the dimensionality is defined
                if(tokens.length==1){
                    int dim = Integer.parseInt(tokens[0]);
                    if(dim!=vecdim)
                    {
                        JOptionPane.showMessageDialog(null,"Expecting a trajectory file "
                                + "of dimensionality "+som.getDim()+" to match the dimensionality of the SOM."
                                + "\n This trajectory file specifies a dimensionality of "+dim+". ","Dimensinonality specification error",JOptionPane.ERROR_MESSAGE
                                );
                        return null;
                    }
                    
                }
                else if(tokens.length<som.getDim()){
                    JOptionPane.showMessageDialog(null,"Expecting a trajectory file "
                                + "of dimensionality "+som.getDim()+" to match the dimensionality of the SOM."
                                + "The current line only contains "+tokens.length+" tokens.","Vector dimensionality error",JOptionPane.ERROR_MESSAGE
                                );
                        return null;
                }
                else
                {
                    String label = "";
                    for(int i=vecdim;i<tokens.length-1;i++){
                        label += tokens[i];
                    }
                    label = label.trim();
                    
                    // if the last label is different from this label, then this is a new trajectory
                    if(!last_label.equals(label))                            
                    {
                        last_label = label;
                        // if this is not the first trajectory, add the last trajectory to the list
                        if(singleTrajectory!=null)
                        {
                            // complete single trajectory object
                            singleTrajectory.setTrajectory(new double[singleTrajectoryVectors.size()][vecdim]);
                            singleTrajectoryVectors.toArray(singleTrajectory.getTrajectory());

                            // add single trajectory object to list of trajectories
                            trajectories.add(singleTrajectory);

                            singleTrajectory.setOffset(0.0f);
                            singleTrajectory.computeBMUsAndHitCounts(som);
                        }

                        // create new trajectory and add label
                        singleTrajectory = new SOMTrajectory(som);
                        singleTrajectoryVectors = new LinkedList<double[]>();

                        
                        singleTrajectory.label = label.trim();

                    }

                    // always add the vectors
                    double[] currentvector = new double[vecdim];
                    for(int i=0;i<vecdim;i++)
                    {
                        try{
                                currentvector[i] = Double.parseDouble(tokens[i]);
                            }
                            catch(NumberFormatException e){
                                JOptionPane.showMessageDialog(null,tokens[i]+ " is not a double value.","Parse exception",JOptionPane.ERROR_MESSAGE);
                                return null;
                            }

                    }
                    singleTrajectoryVectors.add(currentvector);


                }

            }
        }
        
        // handle last trajectory
        if(singleTrajectory!=null)
        {
            // complete single trajectory object
            singleTrajectory.setTrajectory(new double[singleTrajectoryVectors.size()][vecdim]);
            singleTrajectoryVectors.toArray(singleTrajectory.getTrajectory());

            // add single trajectory object to list of trajectories
            trajectories.add(singleTrajectory);
            float offset = (float)(trajectories.indexOf(singleTrajectory));

            singleTrajectory.setOffset(0.0f);
            singleTrajectory.computeBMUsAndHitCounts(som);
        }

        //Close the input stream
        in.close();

        return trajectories;

    }
    
    // =================== OBJECT ======================= //
    
    private int[][] bmus;
    private int[][] hitCounts;
    private String label;

    private SOMMap som;
    private double[][] path;
    
    private Color guiColor;
    private BasicStroke guiStroke;
    
    public boolean display;
    public boolean displayNSync;
    
    //The trajectoriy will be shifted by this offset
    //to prevent overlapping
    private Float offset;
    

    /**
    * Constructor, instanciating a new SOM Trajectory 
    * based on a @see SOMMap
    **/
    public SOMTrajectory(SOMMap som)
    {
        this.som = som;
        lastIncompleteTrajectoryAskedFor = Integer.MIN_VALUE;
        
        int r = (int)(Math.random() * 255.0);
        int b = (int)(Math.random() * 255.0);
        int g = (int)(Math.random() * 255.0);
        guiColor = new Color(r,g,b);
        guiStroke = StyleLine.SIMPLE;
        this.setThickness(new Float(0.5f));
        this.offset = 0.2f;
        displayNSync = false;
    }
    
    /**
     * Computes the best-matching units for the trajectory and
     * creates a map of how often a node was the best matching unit.
     *
     * @param som 
     */
    private void computeBMUsAndHitCounts(SOMMap som)
    {
        bmus = new int[path.length][2];
        for(int i=0;i<bmus.length;i++)
        {
            bmus[i] = som.getBMU(path[i]);
        }
        
        
        hitCounts = new int[som.getY()][som.getX()];
        for(int i=0;i<this.bmus.length;i++)
        {
            hitCounts[bmus[i][1]][bmus[i][0]]++;
        }
  
    }

    /**
    * @return the trajectory path length
    **/
    public int getLength()
    {
        if(path!=null)
            return path.length;
        return -1;
    }
    
    /**
    * Getter for the best matching units in the som that are covered by this trajectory
    * @return units of the som covered by this trajectory
    **/
    public int[][] getBmus() {
        return bmus;
    }
    
    /**
    * @return color this trajectory will be drawn with
    **/
    public Color getColor() {
        return guiColor;
    }
    
    /**
    * @return stroke this trajectory will be drawn with
    **/
    public BasicStroke getStroke() {
        return guiStroke;
    }
    
    /**
    * @return line width of this trajectory
    **/
    public Float getLineWidth()
    {
        return this.guiStroke.getLineWidth();
    }
    
    /**
    * Returns an array that specifies for each BMU how often it was hit
    * @return how often each BMU was hit
    **/
    public int[][] getHitCounts() {
        return hitCounts;
    }
    
    /**
    * @return label of this trajectory as read from the .data file
    **/
    public String getLabel() {
        return label;
    }

    /**
    * @return the path corresponding to this trajectory - array of value vectors
    **/
    public double[][] getTrajectory() {
        return path;
    }
    
    /**
    * set the path 
    * @param trajectory array of the value vectors of this trajectory: trajectory[pos in path][component of vector]
    **/
    public void setTrajectory(double[][] trajectory)
    {
        this.path = trajectory;
    }
    
    private int lastIncompleteTrajectoryAskedFor;
    private int[][] incompleteCounts;
    
    /**
     * This method is meant for when a trajectory is shown synchronized
     * with a movement. It returns the hit counts for only the first part
     * of the movement up to index i. If the counter was only incremented by 1 or 
     * not incremented since the last call of this method, the old counter array
     * can be reused. Otherwise the counter array is recomputed.
     * @param j position to which the trajectory should be synchronized
     */
    public int[][] getIncompleteCountsAt(int j){
        
        if(j==lastIncompleteTrajectoryAskedFor)
        {
            // do nothing
        }
        else if(j==lastIncompleteTrajectoryAskedFor+1)
        {
            // just increment once
            if(j<this.getLength())
                incompleteCounts[bmus[j][1]][bmus[j][0]]++;
        }
        else
        {
            incompleteCounts = new int[som.getY()][som.getX()];

            int endOfLoop = java.lang.Math.min(j,this.getLength());
            for(int i=0;i<=endOfLoop;i++)
            {
                incompleteCounts[bmus[i][1]][bmus[i][0]]++;
            }    
        }
        
        // update lastInc. index and return
        lastIncompleteTrajectoryAskedFor = j;
        return incompleteCounts;
        
    }

    /**
    * Setter for the trajectory color
    **/
    void setColor(Color color) {
        this.guiColor = color;
    }
    
    /**
    * Setter for the stroke the trajectory will be drawn with
    **/
    void setStroke(BasicStroke stroke) {
        
        BasicStroke tmpStroke = new BasicStroke(this.guiStroke.getLineWidth(), 
                stroke.getEndCap(), stroke.getLineJoin(),
                stroke.getMiterLimit(),stroke.getDashArray(),
                stroke.getDashPhase());
        this.guiStroke = tmpStroke;
    }
    
    /**
    * Setter for the line thickness of the trajectory
    **/
    void setThickness(Float thickness)
    {
        //Negative or null value don't make any sense
        if(thickness.floatValue() <= 0.f)
            return;
        BasicStroke tmpStroke = new BasicStroke(thickness, 
                this.guiStroke.getEndCap(), this.guiStroke.getLineJoin(),
                this.guiStroke.getMiterLimit(),this.guiStroke.getDashArray(),
                this.guiStroke.getDashPhase());
        this.guiStroke = tmpStroke;
    }

    /**
     * @return the offset
     */
    public Float getOffset() {
        return offset;
    }

    /**
     * @param offset the offset to set
     */
    public void setOffset(Float offset) {
        this.offset = offset;
    }
}
