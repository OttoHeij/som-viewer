package org.tum.opensim.somview;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.opensim.view.motions.MotionTimeChangeEvent;
import org.opensim.view.motions.MotionsDB;

/**
 * The UMatrix is a JPanel that shows a umatrix.
 */
public class UMatrix extends javax.swing.JPanel implements Observer, TableModelListener {

    public static final int GREYSCALE_COLOR_MODEL = 0;
    public static final int COLORED_COLOR_MODEL = 1;
    
    
    /**
     *  specifies whether or not black dots should be displayed on nodes
     */
    private boolean displayDots;
    
    /**
     *  quality specifies the pixel size of each hexagon
     */
    private int quality = 15;

    /**
     * The scale variable scales the buffered image to allow for zooming.
     */
    private double zoomScale = 1.0;
   
    /**
     * This variable allows the user to scale the hit count circle to a reasonable size.
     */
    private double hitCountScaling = 1.0;
    
    /**
     * The normScale is the ratio of zooming scale to quality. Using the normScale instead of the scale
     * means no zooming occurs when the quality is changed.
     */
    private double normedZoomScale = zoomScale/this.quality;
    
    /** 
     * Specifies whether the uMatrix is shown in a greyscale or 
     * a color map.
     */
    private int colorModel;

    /**
     * a two-dimensional array which contains all hexagons in the umatrix
     */ 
    private Hexagon[][] hexagons;

    /**
     *  the hexagon factory for this umatrix
     */
    private HexagonFactory hexFactory;
    
    /**
     *  specifies whether the hexagon for a node should stay white or interpolate the value of its neighbors
     */
    private boolean interpolateDistanceNodes;
    
    /**
     *  the associated som for which the umatrix is shown
     */
    private SOMMap som;
    
    /**
     * This variable is needed for the synchronization of trajectories and movement.
     */
    private double currentTime;

    /**
     *  a list of trajectories that can be shown on top of the umatrix
     */
    private LinkedList<SOMTrajectory> trajectories;
  
    
    /**
     * Observers - will be notified if anything important in the uMatrix changes.
     * For example: new SOM was loaded
     */
    private List<UMatrixObserver> observers;
    
  
    /**
     * Progress event listeners
     * Will be notified if the umatrix is working on anything
     * that might take longer
     */
    private List<ProgressListener> progressListeners;
    
    
    //################
    //# contour line related variables
    //################
    
    /**
     * Values at which the contourlines will be painted
     */
    private List<Double> contourLineDists;
    /**
     * Thickness of the contourlines
     */
    private double cLineThickness;
    
    /**
     * State of the contourlines
     */
    private boolean contourlinesActive;
    
    /**
     * Buffer for the umatrix => only draw the buffer at every redraw and 
     * rerender the umatrix only when necessary
     */
    private BufferedImage bufferUMatrix;
    
    /**
     * Buffer for the trajectories => only draw the buffer at every redraw and 
     * rerender the trajectories only when necessary
     */
    private BufferedImage bufferTrajectories;
    
    //Buffer that holds the final version of the visualization
    //Reason: We have to apply an affine transform for zooming.
    //Doing that every paint cycle is computationally very expensive
    //=> buffer the final, scaled image and only update it if necessary
    //For every redraw of the uMatrix panel, only use this combined Buffer object
    /**
    * Contains the combination of umatrix and trajectory buffer
    **/
    private BufferedImage bufferCombine;
    
    /**
     * The constructor for the umatrix form.
     */
    public UMatrix() {
        //Initialize contour line variables
        cLineThickness = 1.0;
        contourLineDists = new LinkedList<Double>();
        currentTime = 0;
        contourlinesActive = false;
        
        //Initialize the umatrix observer list
        observers = new LinkedList<UMatrixObserver>();
        
        //Initialize the progress listener list
        progressListeners = new LinkedList<ProgressListener>();
        
        //Register this class as observer of the motion in 
        // OpenSim -> important for synchronization
        MotionsDB.getInstance().addObserver(this);
        //Initalize the GUI components
        initComponents();
        //Initialize the hexagon factory with some default hexagon size
        hexFactory = new HexagonFactory(quality);
    }
   
    /**
     * Will notify all observers watching this uMatrix
     */
    private void notifyObservers()
    {
        for (UMatrixObserver observer : observers)
        {
            observer.update();
        }
    }
    
    /**
     * Notify all the progress listeners
     * @param e the progress event they should be know about
     */
    protected void notifyProgressListeners(ProgressEvent e)
    {
        for (ProgressListener p : progressListeners)
        {
            p.progressUpdate(e);
        }
    }
    
    /**
     * Add a listener that will be notified about computation 
     * progress
     * @param listener 
     */
    public void addProgressListener(ProgressListener listener)
    {
        this.progressListeners.add(listener);
    }
    
    /**
     * Remove a progress listener from the list of listeners
     * @param listener 
     */
    public void removeProgressListener(ProgressListener listener)
    {
        this.progressListeners.remove(listener);
    }
    
    /**
     * If a SOMMap object is attached, this method creates a map of 
     * the hexagons, which is typically twice the width and heigth of the
     * original SOM.
     */
    public void createHexagonMap() {
        // if no cod file was opened a hex map cannot be created
        if (som == null) {
            return;
        }

        hexagons = new Hexagon[som.getY() * 2 - 1][som.getX() * 2 - 1];
        
        //For every hexagon in the map
        for (int x = 0; x < som.getX() * 2 - 1; x++) {
            for (int y = 0; y < som.getY() * 2 - 1; y++) {
                //Create the hexagon using the factory
                GeneralPath path = hexFactory.computeHexagonPositions(x, y);
               
                //Distinguish between node and distance hexagons
                if (som.getDistances()[y][x] == 0) {
                    // hexagon is a SOM node
                    hexagons[y][x] = new NodeHexagon(path, som);
                    
                    // for possible implementation of a feature that shows vector values in a status bar
                    ((NodeHexagon)hexagons[y][x]).setVector(som.getMap()[(y / 2)][(x / 2)]);
                    if (interpolateDistanceNodes || contourlinesActive) {
                        hexagons[y][x].setDistance(interpolateDistance(y, x));
                    } else {
                        hexagons[y][x].setDistance(0);
                    }
                    
                }
                else {
                    // hexagon is distance hexagon
                    hexagons[y][x] = new DistanceHexagon(path, som);
                    hexagons[y][x].setDistance(som.getDistances()[y][x]);
                }
                
            }
        }
        //For each hexagon: remember its neighbors -> important for interpolation
        setNeighbors();
    }
    
    /**
     * This will set the neighbors for each hexagon
     */
    private void setNeighbors()
    {
        for(int x=0;x<som.getX()*2-1;x++)
        {
            for(int y=0;y<som.getY()*2-1;y++)
            {
                //NOTE: Some of the following branches
                //could be merged
                //Since this function is only called when
                //the umatrix is recreated completely,
                //we let those branches seperate 
                //for code clarity
                
                //Every one of the following branches represents one neighbor
                
                Hexagon currHex =  hexagons[y][x];
                //Left, upper neighbor
                Hexagon luN = null;
                if(y-1 >= 0)
                {
                    if(hexagons[y-1][x].getCenter().getX() < currHex.getCenter().getX())
                    {
                        luN = hexagons[y-1][x];
                    }else if(x-1 >= 0)
                    {
                        luN = hexagons[y-1][x-1];
                    }
                }
                currHex.addNeighbor(luN);
                
                //Left, lower neighbor
                Hexagon llN = null;
                if(y+1 < hexagons.length)
                {
                    if(hexagons[y+1][x].getCenter().getX() < currHex.getCenter().getX())
                    {
                        llN = hexagons[y+1][x];
                    }else if(x-1 >= 0)
                    {
                        llN = hexagons[y+1][x-1];
                    }
                }
                currHex.addNeighbor(llN);
                
                //Right, upper neighbor
                Hexagon ruN = null;
                if(y-1 >= 0)
                {
                    if(hexagons[y-1][x].getCenter().getX() > currHex.getCenter().getX())
                    {
                        ruN = hexagons[y-1][x];
                    }else if(x+1 < hexagons[0].length)
                    {
                        ruN = hexagons[y-1][x+1];
                    }
                }
                currHex.addNeighbor(ruN);
                
                //Right, lower neighbor
                Hexagon rlN = null;
                if(y+1 < hexagons.length)
                {
                    if(hexagons[y+1][x].getCenter().getX() > currHex.getCenter().getX())
                    {
                        rlN = hexagons[y+1][x];
                    }else if(x+1 < hexagons[0].length)
                    {
                        rlN = hexagons[y+1][x+1];
                    }
                }
                currHex.addNeighbor(rlN);
                
                //Left neighbor
                Hexagon lN = null;
                if(x-1 >= 0)
                {
                    lN = hexagons[y][x-1];
                }
                currHex.addNeighbor(lN);
                
                //Right neighbor
                Hexagon rN = null;
                if(x+1 < hexagons[0].length)
                {
                    rN = hexagons[y][x+1];
                }
                currHex.addNeighbor(rN);
            }
        }
    }

    /**
     * This method finds the hexagon, which contains a point on the panel.
     * It was intended, to allow for clicks on the uMatrix which would 
     * return the vector values of the clicked hexagon.
     * @param click The point.
     * @return the hexagon.
     */
    private Hexagon findHexagonContainingPoint(Point click) {

        // use the y coordinate to find the containing row of hexagons
        Hexagon lowermostHex = hexagons[hexagons.length - 1][0];
        double farYCoords = lowermostHex.getCenter().getY();
        int approxHexRow = (int)(click.y / farYCoords);
        double yError = hexagons[approxHexRow][0].getCenter().getY() - click.getY();

        // correct while distance is greater than half the hexagon size
        while (Math.abs(yError) > quality) {
            if (yError > 0) {
                approxHexRow--;
            } else {
                approxHexRow++;
            }
            yError = hexagons[approxHexRow][0].getCenter().getY() - click.getY();
        }

        // use the x coordinate to find the exact hexagon
        Hexagon rightmostHex = hexagons[approxHexRow][hexagons[0].length - 1];
        double farXCoords = rightmostHex.getCenter().getX();
        int approxHexColumn = (int) (click.x / farXCoords);

        double xdist = hexagons[approxHexRow][approxHexColumn].getCenter().getX() - click.getX();

        // correct while distance is greater than half the hexagon size
        while (Math.abs(xdist) > quality * Math.sqrt(3) / 2) {
            if (xdist > 0) {
                approxHexColumn--;
            } else {
                approxHexColumn++;
            }
            xdist = hexagons[approxHexRow][approxHexColumn].getCenter().getX() - click.getX();
        }

        // fine corrections

        if (hexagons[approxHexRow][approxHexColumn].contains(click)) {
            return hexagons[approxHexRow][approxHexColumn];
        } else {
            for (int x_off = -1; x_off <= 1; x_off++) {
                for (int y_off = -1; y_off <= 1; y_off++) {
                    if (approxHexRow + y_off > 0 && approxHexColumn + x_off > 0) {
                        if (hexagons[approxHexRow + y_off][approxHexColumn + x_off].contains(click)) {
                            return hexagons[approxHexRow + y_off][approxHexColumn + x_off];
                        }
                    }
                }
            }
        }

        return null;


    }

    /**
     * Getter for the hexagon size
     * @return hexagon size
     */
    public int getQuality() {
        return quality;
    }
    
    /**
    * Getter for the color model this umatrix is drawn with
    * @return color model this hexagon is drawn with
    **/
    public int getColorMode() {
        return colorModel;
    }
    
    /**
     * This method interpolates the distance value of a hexagon indexed with y and x.
     *
     * @param y
     * @param x
     * @return 
     */
    private double interpolateDistance(int y, int x) {

        double sum = 0;
        int count = 0;

        if (x > 0) {
            // left
            sum += som.getDistances()[y][x - 1];
            count++;
        }
        if (x < som.getX() - 1) {
            // right
            sum += som.getDistances()[y][x + 1];
            count++;
        }
        // top
        if (y > 0) {

            if (y % 4 == 0) {
                if (x > 0) {
                    sum += som.getDistances()[y - 1][x - 1];
                    count++;
                }
                sum += som.getDistances()[y - 1][x];
                count++;
            } else if (y % 4 == 2) {
                if (x < som.getX() - 1) {
                    sum += som.getDistances()[y - 1][x + 1];
                    count++;
                }
                sum += som.getDistances()[y - 1][x];
                count++;
            }

        }

        // bottom
        if (y < som.getY() - 1) {
            if (y % 4 == 2) {
                if (x > 0) {
                    sum += som.getDistances()[y + 1][x - 1];
                    count++;
                }
                sum += som.getDistances()[y + 1][x];
                count++;
            } else if (y % 4 == 0) {
                if (x < som.getX() - 1) {
                    sum += som.getDistances()[y + 1][x + 1];
                    count++;
                }
                sum += som.getDistances()[y + 1][x];
                count++;
            }
        }

        if (count > 0) {
            return sum / count;
        } else {
            return 0;
        }
    }
    
    /**
     * Getter for the trajectories.
     * @return 
     */
    public LinkedList<SOMTrajectory> getTrajectories() {
        return trajectories;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setMaximumSize(new java.awt.Dimension(1000000000, 1000000000));
        setPreferredSize(new java.awt.Dimension(1000, 1000));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 586, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 403, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Getter for display dots.
     * @return 
     */
    public boolean isDisplayDots() {
        return displayDots;
    }
    

    /**
     * Getter for interpolateNodeDistances
     * @return the value of interpolateNodeDisatnaces
     */
    public boolean isInterpolateNodeDistances() {
        return interpolateDistanceNodes;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
 
    /**
     * Refreshes the buffer where the static uMatrix background is stored in
     * The actual painting of the uMatrix in the paintComponent therefore is now just
     * drawing this BufferedImage buffer to the respective Graphics object
     */
    public void refreshUMatrixImageBuffer()
    {
        // if no cod file was opened, nothing will be displayed
        if (som == null) {
            return;
        }
        
        //Notify the progress listeners that a umatrix rerender has been 
        //triggered
        notifyProgressListeners(new ProgressEvent(this, 
                "Rerendering umatrix - this may take some time", true));
        
        Graphics2D g = (Graphics2D) (bufferUMatrix.getGraphics());
        
        
        g.setBackground(new Color(0.f,0.f,0.f,0.f));
        g.clearRect(0, 0, bufferUMatrix.getWidth(), bufferUMatrix.getHeight());
        // perform for every hexagon
        for (int x = 0; x < som.getX() * 2 - 1; x++) {
            for (int y = 0; y < som.getY() * 2 - 1; y++) {
                Hexagon hexagon = hexagons[y][x];

                if (hexagon.getDistance() == 0) {
                    // if there is no distance value, fill hexagon with white
                    g.setColor(Color.white);
                    g.fill(hexagon);
                } else {
                    
                    // if there is a distance value, fill accordingly
                    double brightness = hexagon.getDistance() / som.getMaxDistance();
                    switch(colorModel)
                    {
                        case UMatrix.GREYSCALE_COLOR_MODEL: g.setColor(Color.getHSBColor(1.f, 0.f, 1.f - (float) brightness));
                            break;
                        case UMatrix.COLORED_COLOR_MODEL: g.setColor(Color.getHSBColor((1.f - (float) brightness) * 0.708f, 1.f, 1.f));
                            break;
                        default: g.setColor(Color.RED);
                            break;
                    }
                    //Draw contourlines
                    if(contourlinesActive)
                    {
                        g.setPaint(new HexagonInterpolationPaint(hexagon.getNeighborPoints(),
                                new HexagonInterpolationPaintContext.PointAndColor(hexagon.getCenter(), 
                                        som.getHexagonColor(hexagon), hexagon.getDistance()),
                                0,
                                this.getQuality()*1.75,
                                this.contourLineDists, this.cLineThickness));
                    }
                    g.fill(hexagon);
                }
                if (displayDots && hexagon instanceof NodeHexagon) {
                    
                    // display black dots if this is a nodehexagon and displaydots is turned on
                    Point2D center = hexagon.getCenter();
                    g.setColor(Color.black);
                    g.setPaint(Color.black);
                    float dotRadius = (float)this.quality / 2.5f;
                    Ellipse2D.Double centerDot = new Ellipse2D.Double(
                            center.getX() - (int)(dotRadius/2.f),
                            center.getY() - (int)(dotRadius/2.f),
                            (int) dotRadius, (int) dotRadius);
                    g.fill(centerDot);
                }
            }
        }
        //Since something with the uMatrix might have changed
        //we have to redraw the buffer that contains the final combination
        //of all the buffers
        refreshCombinedImageBuffer();
        //Notify the progress listeners that a umatrix rendering is finished
        notifyProgressListeners(new ProgressEvent(this, 
                "Rendering of the umatrix done", false));
    }

    
     /**
     * Refreshes the buffer where the static trajectories are stored in
     * Separating the drawing of the static uMatrix background and the 
     * tajectories that might change constantly will increase the rendering speed
     */
    public void refreshTrajectoriesImageBuffer()
    {
        // if no cod file was opened, nothing will be displayed
        if (som == null) {
            return;
        }
        //Get the graphics object of the trajectory image buffer
        Graphics2D g = (Graphics2D) (bufferTrajectories.getGraphics());
        //Set back with alpha=0 color and clear the buffers background
        g.setBackground(new Color(0.f,0.f,0.f,0.f));
        g.clearRect(0, 0, bufferTrajectories.getWidth(), bufferTrajectories.getHeight());
        
        
        
        // If there are trajectories, try displaying them
        if (trajectories != null) {

            // perform for every trajectory
            for(SOMTrajectory trajectory: trajectories)
            {
                //If trajectory should be displayed ..
                if(trajectory.display)
                {
                    int currentTimeStep = -1;
                    // if the trajectory is synchronized to a motion, get the current time step of the motion
                    if(trajectory.displayNSync)
                    {
                        currentTimeStep = (int)(currentTime * (trajectory.getBmus().length-1));
                    }
                    
                    // set the drawing color to the color specified for the trajectory
                    g.setColor(trajectory.getColor());

                    // the offset here prevents multiple trajectories from overlapping
                    Float offset = trajectory.getOffset();
                            
                    //scale this offset with the quality level to make it independent of quality
                    offset *= this.quality;

                    int[][] hitCounts;
                    
                    // if the trajectory is synchronized with a motion, size
                    // the hit count circles only to the current hit count,
                    // else use the final hit count
                    if(trajectory.displayNSync)
                        hitCounts = trajectory.getIncompleteCountsAt(currentTimeStep);
                    else
                        hitCounts = trajectory.getHitCounts();
                        
                    int[][] bestMatchingUnits = trajectory.getBmus();

                    Hexagon current = null;
                    Hexagon last = null;
                    
                    // if the trajectory is displayed in sync with the motion
                    // stop at the current time step, else iterate over the
                    // whole trajectory
                    int iEnd;
                    if(trajectory.displayNSync)
                        iEnd = currentTimeStep+1;
                    else
                        iEnd = bestMatchingUnits.length;

                    for(int i=0;i<iEnd;i++)
                    {
                        
                        BasicStroke trajStroke = trajectory.getStroke();
                        int strokeWidth = (int)(((float)(trajStroke.getLineWidth()))*((float)(this.quality)));
                        
                        // The stroke used for drawing the "hit count" circles of trajectories.
                        BasicStroke circleStroke = new BasicStroke(strokeWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
                        
                        // Iterate over all the points of this trajectory:
                        // Draw circles on every best matching unit, sized
                        // according to how often they were hit (hitCount).
                        // Draw lines between consecutive best matching units.
                        
                        int x = bestMatchingUnits[i][0];
                        int y = bestMatchingUnits[i][1];

                        last = current;
                        current = hexagons[y*2][x*2];

                        double size = (double)hitCounts[y][x];
                        //Scale it with quality to make it independent of quality
                        size *= ((double) this.quality) / 2.0;
                        size *= hitCountScaling;
                        // draw circle (size according to how often it was hit)
                        g.setStroke(circleStroke);
                        Ellipse2D.Double hitCountCircle =
                                new Ellipse2D.Double(current.getCenter().getX() - size + offset,
                                        current.getCenter().getY() - size + offset, 2 * size + 1, 2 * size + 1);
                        g.draw(hitCountCircle);

                        //In case this is not the start of the trajectory
                        //connect the previous point with this one via a line
                        if(last!=null && last!=current)
                            {
                            BasicStroke drawStroke = new BasicStroke(
                                    strokeWidth,
                                    trajStroke.getEndCap(),
                                    trajStroke.getLineJoin(),trajStroke.getMiterLimit(),
                                    trajStroke.getDashArray(),trajStroke.getDashPhase());
                            g.setStroke(drawStroke);
                            Line2D.Double trajLine =
                                    new Line2D.Double(last.getCenter().getX() + offset,
                                        last.getCenter().getY() + offset,
                                        current.getCenter().getX() + offset,
                                        current.getCenter().getY() + offset);
                            g.draw(trajLine);
                        }

                    }
                }
            }
        }
        // We changed the trajectory image buffer and thus have to redraw 
        // the buffer that contains the final combination
        // of all buffers.
        refreshCombinedImageBuffer();
    }
    
    private void adjustBufferCombinedSize()
    {
        //In case buffercombine has not been initialized yet,
        //initialize it
        if(bufferCombine == null)
        {
            bufferCombine = new BufferedImage(10,
                10, BufferedImage.TYPE_INT_ARGB);
            bufferCombine.createGraphics();
        }
        
        //In case the size of the bufferd image is not big enough anymore:
        //Set the size to 1.6 * preferred size
        //If the size is twice as big as it has to be:
        //decrease it to preferred size or buffered size
        //=> Lazy size update
        if(getPreferredSize().getWidth() > bufferCombine.getWidth())
        {
            notifyProgressListeners(new ProgressEvent(this, 
                "updating buffers", true));
            bufferCombine = new BufferedImage((int)(getPreferredSize().getWidth() * 1.6),
                (int)(getPreferredSize().getHeight() * 1.6), BufferedImage.TYPE_INT_ARGB);
            bufferCombine.createGraphics();
            notifyProgressListeners(new ProgressEvent(this, 
                "updating buffers - finished", false));
        }else if(getPreferredSize().getWidth() * 2 <= bufferCombine.getWidth())
        {
            notifyProgressListeners(new ProgressEvent(this, 
                "updating buffers", true));
            double width = getPreferredSize().getWidth();
            double height = getPreferredSize().getHeight();
            bufferCombine = new BufferedImage((int)(width),
                (int)(height), BufferedImage.TYPE_INT_ARGB);
            bufferCombine.createGraphics();
            notifyProgressListeners(new ProgressEvent(this, 
                "updating buffers - finished", false));
        }
    }
    
    /*
     * This method combines the different image buffers.
     */
    private void refreshCombinedImageBuffer()
    {
        //If no some loaded, do nothing
        if(som == null)
            return;
        //Get the graphics object of the trajectory image buffer
        Graphics2D g = (Graphics2D) (bufferCombine.getGraphics());
        g.setBackground(new Color(0.f,0.f,0.f,0.f));
        g.clearRect(0, 0, bufferCombine.getWidth(), bufferCombine.getHeight());
        
        //The trajectories as well as the uMatrix background each have their own 
        //buffer image object
        //In the next step, these images are simply drawn to this umatrix panel
        //For that, they are scaled according to the zoomlevel that can be adjusted
        //in the settings
        AffineTransform at = AffineTransform.getScaleInstance(normedZoomScale, normedZoomScale);
        g.drawRenderedImage(bufferUMatrix, at);      
        //Draw the Trajectories on top of the uMatrix background
        g.drawRenderedImage(bufferTrajectories, at);      
    }
    
    /**
     * Renders uMatrix (including trajectories) to a buffered image.
     * That way, it can be saved to disk as a PNG
     */
    public BufferedImage renderUMatrixImage()
    {
        //Clone the uMatrix background image buffer
        ColorModel cm = this.bufferUMatrix.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = this.bufferUMatrix.copyData(null);
        BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
        //Draw the trajectories on top
        //Get the graphics object of the result image
        Graphics2D g = (Graphics2D) (result.getGraphics());
        //Draw the Trajectories on top of the uMatrix background
        g.drawImage(bufferTrajectories,0,0,null);  
        //Return the copy of the combined image buffer
        return result;
    }
    
    @Override
    protected void paintComponent(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        super.paintComponent(g);

        // if no cod file was opened, nothing will be displayed
        if (som == null) {
            return;
        }

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        //Draw the buffer that contains a combination of the trajectory and 
        //the uMatrixbuffer
        g.drawImage(bufferCombine, 0, 0, this);
    }

    
    /**
     * Switch between black and white uMatrix and colored uMatrix. 
     * @param colorModel 
     */
    public void setColorMode(int colorModel)
    {
        this.colorModel = colorModel;
        refreshUMatrixImageBuffer();
        this.invalidate();
        this.repaint();
        
    }

    /**
     * Setter for displayDots
     * @param displayDots 
     */
    public void setDisplayDots(boolean displayDots) {
        this.displayDots = displayDots;
        refreshUMatrixImageBuffer();
    }

    /**
     * Setter for quality 
     * - the umatrix will be recreated completely with the new quality
     * @param quality 
     */
    public void setQuality(int quality) {
        if(this.quality == quality)
        {
            //If the quality didn't change anyways
            //no need to rerender...
            return;
        }
        this.quality = quality;
        this.normedZoomScale = this.zoomScale/this.quality;
        hexFactory = new HexagonFactory(this.quality);
        //Create  new buffers with correct size
        bufferUMatrix = new BufferedImage((int)getBufferDimensions().getWidth(),
                (int)getBufferDimensions().getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        bufferUMatrix.createGraphics();
        bufferTrajectories = new BufferedImage((int)getBufferDimensions().getWidth(),
                (int)getBufferDimensions().getHeight(), 
                BufferedImage.TYPE_INT_ARGB);
        bufferTrajectories.createGraphics();
        //Resize the combine image buffer is necessary
        adjustBufferCombinedSize();
        //Recreate the hexagon map
        createHexagonMap();
        
        //And render the new umatrix to the buffers
        refreshUMatrixImageBuffer();
        refreshTrajectoriesImageBuffer();
    }

    /**
     * Setter for interpolateNodeDistances
     * @param interpolateNodeDistances 
     */
    public void setInterpolateNodeDistances(boolean interpolateNodeDistances) {
        this.interpolateDistanceNodes = interpolateNodeDistances;
    }

    /**
     * Setter for SOMMap
     * @param map 
     */
    public void setMap(SOMMap map) {
        //Assign map
        som = map;
        //Create the image buffer objects
        bufferUMatrix = new BufferedImage((int)getBufferDimensions().getWidth(),
                (int)getBufferDimensions().getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        bufferUMatrix.createGraphics();
        bufferTrajectories = new BufferedImage((int)getBufferDimensions().getWidth(),
                (int)getBufferDimensions().getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        bufferTrajectories.createGraphics();
        bufferCombine = new BufferedImage((int)getBufferDimensions().getWidth(),
                (int)getBufferDimensions().getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        bufferCombine.createGraphics();
        //(Re-)Initialize the contourline stuff
        this.contourLineDists.clear();
        this.cLineThickness = 
                (som.getMaxDistance() - som.getMinDistance()) / 40.0;
        
        //Create the hexagon map and render the umatrix + trajectories
        createHexagonMap();
        refreshUMatrixImageBuffer();
        refreshTrajectoriesImageBuffer();
        notifyObservers();
    }
    
    /**
     * Getter for SOMMap
     * @return map
     */
    public SOMMap getMap()
    {
        return som;
    }

    /**
     * Setter for trajectories
     * @param trajectories 
     */
    public void setTrajectories(LinkedList<SOMTrajectory> trajectories) {
        this.trajectories = trajectories;
    }

    
    /**
    * Returns the dimensions of the buffer objects
    **/
    private Dimension getBufferDimensions()
    {
        if(hexFactory==null || som==null)
            return new Dimension(10,10);
        double width = hexFactory.getHexagonWidth() * (som.getX() * 2);
        double height = hexFactory.getHexagonHeight() * (som.getY() * 2);
        
        //Border frame
        width += 2.0 * hexFactory.getHexagonWidth();
        height += 2.0 * hexFactory.getHexagonHeight();
        
        return new Dimension((int)Math.ceil(width),(int)Math.ceil(height));
    }
    
    @Override
    public Dimension getPreferredSize() {
        //Get the buffer dimensions
        Dimension buffImgDim = getBufferDimensions();
        //Adapt them according to the zoom level
        Dimension prefSize = 
                new Dimension((int)(buffImgDim.getWidth() * normedZoomScale),
                (int)(buffImgDim.getHeight() * normedZoomScale));
        return prefSize;
    }
  


    @Override
    public void update(Observable o, Object arg) {
        //Will be called if the motion state changes in OpenSim
        if(arg instanceof MotionTimeChangeEvent)
        {
            MotionTimeChangeEvent m = (MotionTimeChangeEvent) arg;
            currentTime = m.getTime();
        }
         refreshTrajectoriesImageBuffer();
         this.repaint();
    }
    
    /**
    * Getter for the values where the contourlines will be drawn
    * @return values where the contourlines will be drawn
    **/
    public List<Double> getContourLineDists() {
        return this.contourLineDists;
    }
    
    /**
    * Setter for the values where the contourlines will be drawn
    * @param contourLineDists values where the contourlines will be drawn
    **/
    public void setContourLineDists(List<Double> contourLineDists) {
        this.contourLineDists = contourLineDists;
        refreshUMatrixImageBuffer();
        this.repaint();
    }
    
    /**
    * Add a contourline to the list of contourlines
    * @param value value where the contourline will be drawn
    **/
    public void addContourline(Double value)
    {
        this.contourLineDists.add(value);
        refreshUMatrixImageBuffer();
        this.repaint();
    }
    
    /**
    * Remove a contourline
    * @param position position in the list of contourlines
    **/
    public void removeContourLine(int position)
    {
        this.contourLineDists.remove(position);
        refreshUMatrixImageBuffer();
        this.repaint();
    }
    
    /**
    * Remove contourline by value
    * @param value value of the contourline that should be deleted
    **/
    public void removeContourline(Double value)
    {
        this.contourLineDists.remove(value);
        refreshUMatrixImageBuffer();
        this.repaint();
    }

    /**
    * Getter for the contourline thickness
    * @return contourline thickness
    **/
    public double getcLineThickness() {
        return cLineThickness;
    }
    
    /**
    * Setter for the contourline thickness
    * @param cLineThickness new contourline thickness
    **/
    public void setcLineThickness(double cLineThickness) {
        this.cLineThickness = cLineThickness;
        refreshUMatrixImageBuffer();
        this.repaint();
    }
    
    /**
    * Add an observer to this umatrix
    * - will be notified if something important about the umatrix changes
    * (for example a new som is loaded)
    * @param observer observer of this umatrix
    **/ 
    public void addObserver(UMatrixObserver observer)
    {
        this.observers.add(observer);
        observer.update();
    }
    
    /**
    * Remove an observer
    * @param observer observer that should be removed and no longer be notified
    **/
    public void removeObserver(UMatrixObserver observer)
    {
        this.observers.remove(observer);
    }

    /**
     * Are the contourlines currently active for drawing?
     * @return the contourlinesActive
     */
    public boolean isContourlinesActive() {
        return contourlinesActive;
    }

    /**
     * Set the contourlines active for drawing
     * @param contourlinesActive the contourlinesActive to set
     */
    public void setContourlinesActive(boolean contourlinesActive) {
        this.contourlinesActive = contourlinesActive;
    }

    /**
    * Change the zoomlevel of this umatrix
    **/
    public void setZoomScale(double scale)
    {
        this.zoomScale = scale;
        this.normedZoomScale = zoomScale / this.quality;
        
        adjustBufferCombinedSize();
        
        this.refreshCombinedImageBuffer();
    }

    @Override
    public void tableChanged(TableModelEvent e){
       this.refreshTrajectoriesImageBuffer();
       this.repaint();
       this.invalidate();
    }
 
   /**
    * Set the spacing between the trajectories
    **/
    public void setHitCountScale(int scale) {
        this.hitCountScaling = (double)scale/(double)20;
        this.refreshTrajectoriesImageBuffer();
    }
}


