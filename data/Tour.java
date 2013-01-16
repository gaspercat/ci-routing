/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.util.ArrayList;
import java.util.Random;

import locations.Location;

/**
 *
 * @author gaspercat
 */
public class Tour {
    private static final Random randGen = new Random();
    
    Distances dists;
    Location depot;
    ArrayList<Location> waypoints;
    
    public Tour(Tour tour){
        this.dists = tour.dists;
        this.depot = tour.depot;
        this.waypoints = (ArrayList<Location>)tour.waypoints.clone();
    }
    
    public Tour(Distances dists, Location depot){
        this.dists = dists;
        this.depot = depot;
        this.waypoints = new ArrayList<Location>();
    }
    
    public Tour(Distances dists, Location depot, ArrayList<Location> waypoints){
        this.dists = dists;
        this.depot = depot;
        this.waypoints = waypoints;
    }
    
    public void addWaypoint(Location location){
        waypoints.add(location);
    }
    
    public void addWaypoint(int pos, Location location){
        if(pos < 0 || pos > waypoints.size()) return;
        waypoints.add(pos, location);
    }
    
    public Location delWaypoint(){
        int idx = randGen.nextInt(this.waypoints.size());
        Location ret = this.waypoints.remove(idx);
        return ret;
    }
    
    public Location delWaypoint(int idx){
        Location ret = this.waypoints.remove(idx);
        return ret;
    }
    
    public int getNumWaypoints(){
        return this.waypoints.size();
    }
    
    public double getTotalDistance(){
        if(waypoints.size() == 0) return 0;
        
        double dist = dists.getDistance(depot, waypoints.get(0));
        for(int i=0;i<waypoints.size()-1;i++){
            dist = dist + dists.getDistance(waypoints.get(i), waypoints.get(i+1));
        }
        dist = dists.getDistance(waypoints.get(waypoints.size()-1), depot);
        
        return dist;
    }
    
    public boolean isEmpty(){
        return this.waypoints.size() == 0;
    }
    
    public void swapWaypoints(int i1, int i2){
        Location w1 = this.waypoints.get(i1);
        Location w2 = this.waypoints.get(i2);
        
        this.waypoints.remove(i1);
        this.waypoints.add(i1, w2);
        
        this.waypoints.remove(i2);
        this.waypoints.add(i2, w1);
    }
    
    public Tour clone(){
        return new Tour(this);
    }
    
    public void clear(){
        waypoints.clear();
    }
}
