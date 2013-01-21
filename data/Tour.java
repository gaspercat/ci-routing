/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.util.ArrayList;
import java.util.Random;

import locations.Location;
import locations.DropPoint;

/**
 *
 * @author gaspercat
 */
public class Tour {
    private static final Random randGen = new Random();
    
    Distances dists;
    Location depot;
    ArrayList<DropPoint> waypoints;
    double mCapacity;
    
    public Tour(Tour tour){
        this.dists = tour.dists;
        this.depot = tour.depot;
        this.waypoints = (ArrayList<DropPoint>)tour.waypoints.clone();
        this.mCapacity = tour.mCapacity;
    }
    
    public Tour(Distances dists, Location depot, double mCapacity){
        this.dists = dists;
        this.depot = depot;
        this.waypoints = new ArrayList<DropPoint>();
        this.mCapacity = mCapacity;
    }
    
    public Tour(Distances dists, Location depot, ArrayList<DropPoint> waypoints, double mCapacity){
        this.dists = dists;
        this.depot = depot;
        this.waypoints = waypoints;
        this.mCapacity = mCapacity;
    }
    
    public void addWaypoint(DropPoint location){
        waypoints.add(location);
    }
    
    public void addWaypoint(int pos, DropPoint location){
        if(pos < 0 || pos > waypoints.size()) return;
        waypoints.add(pos, location);
    }
    
    public DropPoint delWaypoint(){
        int idx = randGen.nextInt(this.waypoints.size());
        DropPoint ret = this.waypoints.remove(idx);
        return ret;
    }
    
    public DropPoint delWaypoint(int idx){
        DropPoint ret = this.waypoints.remove(idx);
        return ret;
    }
    
    public int getNumWaypoints(){
        return this.waypoints.size();
    }
    
    public double getTotalDistance(){
        if(waypoints.size() == 0) return 0;
        
        double dist = dists.getDistance(depot, waypoints.get(0));
        for(int i=0;i<waypoints.size()-1;i++){
            dist += dists.getDistance(waypoints.get(i), waypoints.get(i+1));
        }
        dist += dists.getDistance(waypoints.get(waypoints.size()-1), depot);
        
        return dist;
    }
    
    public double getTotalPenalty(){
        if(waypoints.size() == 0) return 0;
        
        // Calculate weight penalty
        double dCapacity = 0;
        for(DropPoint wp: (ArrayList<DropPoint>)waypoints){
            dCapacity += wp.getCurrentDemand();
        }
        double wPenalty = (this.mCapacity < dCapacity) ? dCapacity - this.mCapacity : 0;
        
        return wPenalty;
    }
    
    public boolean isEmpty(){
        return this.waypoints.size() == 0;
    }
    
    public boolean isFull(){
        return this.waypoints.size() > 1;
    }
    
    public void swapWaypoints(int i1, int i2){
        DropPoint w1 = this.waypoints.get(i1);
        DropPoint w2 = this.waypoints.get(i2);
        
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
