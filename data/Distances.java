/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.util.ArrayList;

import locations.Location;

/**
 *
 * @author gaspercat
 */
public class Distances {
    private ArrayList<Location> locations;
    private ArrayList<ArrayList<Double>> distances;
    private double mean_distance;
    private double max_distance;
    
    public Distances(){
        this.locations = new ArrayList<Location>();
        this.distances = new ArrayList<ArrayList<Double>>();
        this.mean_distance = 0;
        this.max_distance = 0;
    }
    
    /**
     * Build a distances matrix. If useEucludean is false, the distances will be
     * set to zero until they are manually set. If not, euclidean distances will
     * be obtained from the location coordinates.
     * @param locations List of locations, including depot
     * @param useEuclidean If true, use euclidean distances
     */
    public Distances(ArrayList<Location> locations, boolean useEuclidean){
        this.locations = locations;
        this.distances = new ArrayList<ArrayList<Double>>();
        
        for(int i=0;i<locations.size();i++){
            ArrayList<Double> dists = new ArrayList<Double>();
                    
            for(int j=0;j<locations.size();j++){
                double dist = 0;
                if(useEuclidean){
                    dist = locations.get(i).distanceTo(locations.get(j));
                }
                dists.add(new Double(dist));
            }
            
            this.distances.add(dists);
        }
        
        calculateMeanDistance();
    }
    
    public Distances(ArrayList<Location> locations, ArrayList<ArrayList<Double>> distances){
        this.locations = locations;
        this.distances = distances;
    }
    
    public ArrayList<Location> getLocations(){
        return (ArrayList<Location>)this.locations.clone();
    }
    
    public double getDistance(Location orig, Location dest){
        int oInd = this.locations.indexOf(orig);
        if(oInd == -1) return -1;
        
        int dInd = this.locations.indexOf(dest);
        if(dInd == -1) return -1;
        
        return this.distances.get(oInd).get(dInd).doubleValue();
    }
    
    public double getMaxDistance(){
        return this.max_distance;
    }
    
    public double getMeanDistance(){
        return this.mean_distance;
    }
    
    /*
     * Set the distance between two points (directed)
     * @param orig Defines the origin location
     * @param dest Defines the destination location
     * @param dist Defines the distance from origin to destination
     */
    public void setDistance(Location orig, Location dest, double dist){
        int oInd = locations.indexOf(orig);
        if(oInd == -1) oInd = this.addLocation(orig);
        
        int dInd = locations.indexOf(dest);
        if(oInd == -1) dInd = this.addLocation(dest);
        
        if(oInd == -1){
            this.addLocation(orig);
        }
        
        distances.get(oInd).set(dInd, new Double(dist));
        calculateMeanDistance();
    }
    
    private int addLocation(Location loc){
        this.locations.add(loc);
        
        for(int i=0;i<this.distances.size();i++){
            this.distances.get(i).add(new Double(0));
        }
        
        ArrayList<Double> dists = new ArrayList<Double>();
        for(int i=0;i<this.locations.size();i++){
            dists.add(new Double(0));
        }
        this.distances.add(dists);
        
        calculateMeanDistance();
        return this.locations.size() - 1;
    }
    
    private void calculateMeanDistance(){
        double distance = 0;
        this.max_distance = 0;
        
        int nlocs = this.locations.size();
        for(int i=0;i<nlocs;i++){
            for(int j=0;j<nlocs;j++){
                double tdistance = this.distances.get(i).get(j).doubleValue();
                if(this.max_distance < tdistance){
                    this.max_distance = tdistance;
                }
                
                distance += tdistance;
            }
        }
        
        this.mean_distance = distance / (nlocs * nlocs);
    }
}
