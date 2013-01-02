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
    ArrayList<Location> locations;
    ArrayList<ArrayList<Double>> distances;    
    
    public Distances(){
        this.locations = new ArrayList<Location>();
        this.distances = new ArrayList<ArrayList<Double>>();
    }
    
    public Distances(ArrayList<Location> locations){
        this.locations = locations;
        this.distances = new ArrayList<ArrayList<Double>>();
        
        for(int i=0;i<locations.size();i++){
            ArrayList<Double> dists = new ArrayList<Double>();
                    
            for(int j=0;j<locations.size();j++){
                dists.add(new Double(0));
            }
            
            this.distances.add(dists);
        }
    }
    
    public Distances(ArrayList<Location> locations, ArrayList<ArrayList<Double>> distances){
        this.locations = locations;
        this.distances = distances;
    }
    
    
    public double getDistance(Location orig, Location dest){
        int oInd = this.locations.indexOf(orig);
        if(oInd == -1) return -1;
        
        int dInd = this.locations.indexOf(dest);
        if(dInd == -1) return -1;
        
        return this.distances.get(oInd).get(dInd).doubleValue();
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
        
        return this.locations.size() - 1;
    }
}
