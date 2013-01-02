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
public class Tour {
    Distances dists;
    Location depot;
    ArrayList<Location> waypoints;
    
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
    
    public void addWaypoint(int pos, Location location){
        if(pos < 0 || pos >= waypoints.size()) return;
        waypoints.add(pos, location);
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
}
