package org.tum.opensim.somview;


import java.awt.Color;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;


public class SOMMap {

	// ================ STATIC METHODS ================== //
    /**
     * computes the Euclidean distance between two vectors a and b represented
     * as double arrays
     * @param a first vector
     * @param b second vector
     * @return euclidian distance between a and b
     * @throws VectorException
     */
    public static double computeDistance(double[] a, double[] b) throws VectorException
    {
        if(a.length != b.length)
            throw new VectorException();

        double sum = 0;
        for(int i=0;i<a.length;i++){
            sum += Math.pow((a[i] - b[i]),2);
        }

        return Math.sqrt(sum);
    }
    
    /**
    * Reads the structure of this SOMMap from a file
    * Note: currently only hexagonal nodes are supported
    * 
    * The file has to be compatible with the .cod file format
    * as specified in the @see <a href="http://www.cis.hut.fi/somtoolbox/package/papers/techrep.pdf">SOMToolbox documentation</a>
    *
    **/
    public static SOMMap readFile(File file) throws FileNotFoundException, IOException
    {
       SOMMap som = new SOMMap();

        //Get a buffered reader to read the text from the given file
        FileInputStream fstream = new FileInputStream(file);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
       

        String line;
        
        int index = 0;
        double [][][] map = null;


        // Read lines
        while((line = br.readLine()) != null)
        {
            // comment lines are ignored
            if(!line.startsWith("#"))
            {

                String[] tokens = line.split(" ");
                // special treatment of first line
                if(som.getDim()==0)
                {
                    // handle the case where only the vector dimension is given
                    // in the first line
                    if(tokens.length==4 || tokens.length==5){
                        som.setDim(Integer.parseInt(tokens[0]));
                        som.setTopology(tokens[1]);
                        som.setX(Integer.parseInt(tokens[2]));
                        som.setY(Integer.parseInt(tokens[3]));
                        if(tokens.length==5)
                            som.setNeighborhood(tokens[4]);
                        else
                            som.setNeighborhood("bubble");
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "The specified *.cod file does not contain a header as expected. \n"
                                + "Expected a header of the following format: \n"
                                + "<vector dimensionality> <lattice type> <grid size in x-direction> <grid size in y-direction> <neighborhood function> \n"
                                + "The neighborhood function may be omitted as different neighborhood types are currently not"
                                + " supported.","Header incorrect",JOptionPane.ERROR_MESSAGE
                                );
                        return null;
                    }
                    // create a new map with the specified dimensions
                    map = new double[som.getY()][som.getX()][som.getDim()];
                }
                else
                {
                    if(tokens.length!=som.getDim())
                    {
                        JOptionPane.showMessageDialog(null,"The vector dimensionality of the data is not consistent"
                                + " with that specified in the header.","SOM dimensionality error",JOptionPane.ERROR_MESSAGE
                                );
                        return null;
                        
                    }
                    //Read in the values and store them locally
                    for(int i = 0; i < som.getDim(); i++)
                    {
                        try{
                            double a = Double.parseDouble(tokens[i]);
                            map[index / som.getX()][index % som.getX()][i] = a;
                        }
                        catch(NumberFormatException e){
                            JOptionPane.showMessageDialog(null,tokens[i]+ " is not a double value.","Parse exception",JOptionPane.ERROR_MESSAGE);
                            return null;
                        }

                        

                    }

                    index++;
                }

                
            }
        }
        som.setMap(map);

        //Close the input stream
        in.close();
        

        return som;
    }
    
    // ============== OBJECT =============== //
    
    private int dim;
    private double[][] distanceMap;
    private double[][][] map;
    private double maxdistance;

    private double mindistance;
    private String neighborhood_type;
    private String som_type;


    private int x_dim;

    private int y_dim;

    public SOMMap()
    {
        dim = 0;

    }

    public SOMMap(int vector_dim, String som_type, int x_dim, int y_dim, String neighborhood_type, double[][][] map) {
        this.dim = vector_dim;
        this.som_type = som_type;
        this.x_dim = x_dim;
        this.y_dim = y_dim;
        this.neighborhood_type = neighborhood_type;
        this.map = map;
    }

    /**
     * Creates a map containing the node distances (used for umatrix)
     * @return the distance map
     * @throws VectorException 
     */
    private double[][] computeDistanceMap() throws VectorException{
        double[][] distances = new double[y_dim*2-1][x_dim*2-1];
        maxdistance = 0;

        for(int x=0;x<x_dim*2-1;x++)
        {
            for(int y=0;y<y_dim*2-1;y++)
            {
                // horizontal distances
                if(y%2==0)
                {
                    if(x%2==0)
                    {
                       distances[y][x]=0;
                    }
                    else
                    {
                        distances[y][x]=computeDistance(map[y/2][(x-1)/2],map[y/2][(x+1)/2]);
                    }
                }
                // vertical distances
                else
                {
                    if(x%2==0)
                    {
                        distances[y][x]=computeDistance(map[(y-1)/2][x/2],map[(y+1)/2][x/2]);
                    }
                    else
                    {
                        if(y%2==1)
                            distances[y][x]=computeDistance(map[(y-1)/2][(x+1)/2],map[(y+1)/2][x/2]);
                        else
                            distances[y][x]=computeDistance(map[(y-1)/2][(x)/2],map[(y+1)/2][(x+1)/2]);
                    }
                }
                //Update the max and min distance if necessary
                if(distances[y][x]>maxdistance)
                    maxdistance = distances[y][x];

                if(distances[y][x]<mindistance)
                    mindistance = distances[y][x];


            }
        }
        return distances;
    }

    /**
    * Given a value vector - will return the node of this som that matches best
    * according to the euclidian distance (Best Matching Unit = BMU)
    *
    * @return x and y "coordinate" of the BMU in the som
    **/
    public int[] getBMU(double[] vector)
    {
        double mindist = Double.MAX_VALUE;
        int[] xy = new int[2];
        for(int x=0;x<x_dim;x++)
        {
            for(int y=0;y<y_dim;y++)
            {
                try
                {
                    double dist = computeDistance(map[y][x],vector);
                    if(dist<mindist)
                    {
                        mindist = dist;
                        xy[0] = x;
                        xy[1] = y;
                    }
                }
                catch(VectorException e)
                {
                    System.out.println(e.getMessage());
                }
                
            }
            
        }
        return xy;
        
    }

    /**
     * Will return the dimension of the node vectors
     * @return the dimension of value vectors of the nodes within this map
     */
    public int getDim() {
        return dim;
    }

    /**
    * Will return a map that contains all the distances between all neighboring nodes
    * @return the distance map
    **/
    public double[][] getDistances() {
        return distanceMap;
    }

    /**
     * Getter for the map containing all the value vectors of all the nodes
     * => 3 dimensional: x + y dimension of the map + 1 dimensional array for the value vector
     * Accessable like this: map[x][y][value vec component]
     * @return the map
     */
    public double[][][] getMap() {
        return map;
    }

    /**
    * Getter for the maximal distance between two neighboring nodes
    * @return maximal distance between two neighboring nodes
    **/
    public double getMaxDistance()
    {
        return maxdistance;
    }

    /**
    * Getter for the minimal distance between two neighboring nodes
    * @return minimal distance between two neighboring nodes
    **/
    public double getMinDistance()
    {
        return mindistance;
    }
    
    /**
    * Getter for the color this hexagon is painted with
    * @return hexagon color
    **/
    public Color getHexagonColor(Hexagon hexagon)
    {
        double brightness = hexagon.getDistance() / this.getMaxDistance();
        return Color.getHSBColor((1.f-(float)brightness) * 0.708f, 1.f, 1.f);
    }

    /**
     * @return the neighborhood_type
     */
    public String getNeighborhood() {
        return neighborhood_type;
    }

    /**
     * @return the som_type
     */
    public String getSom_type() {
        return som_type;
    }

    /**
     * Number of nodes in x direction
     * @return the x_dim
     */
    public int getX() {
        return x_dim;
    }

    /**
     * Number of nodes in y direction
     * @return the y_dim
     */
    public int getY() {
        return y_dim;
    }

    /**
     * Setter for the vector dimensions. (in contrast to the dimensions x and y of the som itself
     * @param vectorDimensions the vector dimensions
     */
    public void setDim(int vectorDimensions) {
        this.dim = vectorDimensions;
    }

    /**
     * Setter for the map
     * @param map the map to set
     */
    public void setMap(double[][][] map) {
        this.map = map;

        try {
            distanceMap = computeDistanceMap();
        } catch (VectorException ex) {
            Logger.getLogger(SOMMap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param neighborhood_type the neighborhood_type to set
     */
    public void setNeighborhood(String neighborhood_type) {
        this.neighborhood_type = neighborhood_type;
    }


    /**
     * @param som_type the som_type to set
     */
    public void setTopology(String som_type) {
        this.som_type = som_type;
    }

    /**
     * Set the number of nodes in x-direction
     * @param x_dim the x_dim to set
     */
    public void setX(int x_dim) {
        this.x_dim = x_dim;
    }
    
    /**
     * Set the number of nodes in y-direction
     * @param y_dim the y_dim to set
     */
    public void setY(int y_dim) {
        this.y_dim = y_dim;
    }

    
}
