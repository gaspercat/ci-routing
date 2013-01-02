/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import java.util.ArrayList;

import data.*;
import locations.*;

/**
 *
 * @author gaspercat
 */
public abstract class Problem {
    protected class ProblemState{
        ArrayList<Tour> tours;
        
        public ProblemState(ProblemState state){
            this.tours = new ArrayList();
            for(int i=0;i<state.tours.size();i++){
                this.tours.add(state.tours.get(i).clone());
            }
        }
        
        public ProblemState(int maxTours, Distances dists, Depot depot){
            this.tours = new ArrayList<Tour>();
            for(int i=0;i<maxTours;i++){
                this.tours.add(new Tour(dists, depot));
            }
        }
    }
    
    protected Depot depot;
    protected ArrayList<DropPoint> dropPoints;
    protected Distances dists;
    
    protected int maxTours;
    protected ProblemState state;
    
    public Problem(Distances dists, int maxTours){
        this.dists = dists;
        
        // Get depot & waypoints
        ArrayList<Location> locations = dists.getLocations();
        this.dropPoints = new ArrayList<DropPoint>();
        for(int i=0;i<locations.size();i++){
            if(locations.get(i) instanceof Depot){
                this.depot = (Depot)locations.get(i);
            }else{
                this.dropPoints.add((DropPoint)locations.get(i));
            }
        }
        
        // Set max tours and initialize state
        this.maxTours = maxTours;
        this.state = new ProblemState(maxTours, dists, depot);
    }
}
