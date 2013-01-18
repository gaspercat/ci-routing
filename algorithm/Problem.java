/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import java.util.ArrayList;

import data.*;
import java.util.Random;
import locations.*;

/**
 *
 * @author gaspercat
 */
public abstract class Problem {
    protected static final int SAMPLING_INTERVAL = 20;
    
    protected Random randGen;
    
    public class ProblemState{
        ArrayList<Tour> tours;
        Depot depot;
        Distances dists;
        
        public ProblemState(ProblemState state){
            this.depot = state.depot;
            this.dists = state.dists;
            
            this.tours = new ArrayList();
            for(int i=0;i<state.tours.size();i++){
                this.tours.add(state.tours.get(i).clone());
            }
        }
        
        public ProblemState(Distances dists, Depot depot){
            this.depot = depot;
            this.dists = dists;
            
            this.tours = new ArrayList<Tour>();
            this.tours.add(new Tour(dists, depot));
        }
        
        public void randomizeSolution(){
            ArrayList<Location> locs = this.dists.getLocations();
            
            // Erase tours content
            for(Tour t: this.tours){
                t.clear();
            }
            
            // Assign waypoints to tours randomly
            while(!locs.isEmpty()){
                Location loc = locs.get(randGen.nextInt(locs.size()));
                locs.remove(loc);
                
                if(loc instanceof DropPoint){
                    this.tours.get(0).addWaypoint(loc);
                }
            }
        }
        
        public double getTotalDistance(){
            double dist = 0;
            
            for(Tour tour: this.tours){
                dist += tour.getTotalDistance();
            }
            
            return dist;
        }
        
        /*
         * Get a random tour of the state.
         * @param beEmpty If true select a tour with no drop points
         * @param beFull If true select a tour with two or more drop points
         */
        public Tour getRandomTour(boolean beEmpty, boolean beFull){
            if(beEmpty && !hasEmptyTour()) return null;
            Tour ret = null;
            
            while(ret == null){
                int idx = randGen.nextInt(this.tours.size());
                Tour tret = this.tours.get(idx);
                
                if(!beEmpty || tret.isEmpty()){
                    if(!beFull || tret.getNumWaypoints() >= 2){
                        ret = tret;
                    }
                }
            }
            
            return ret;
        }
        
        public Tour getNonemptyTour(){
            Tour ret = null;
            
            while(ret == null){
                int idx = randGen.nextInt(this.tours.size());
                Tour tret = this.tours.get(idx);
                
                if(!tret.isEmpty()){
                    ret = tret;
                }
            }
            
            return ret;
        }
        
        public boolean hasEmptyTour(){
            for(Tour t: this.tours){
                if(t.isEmpty()) return true;
            }
            
            return false;
        }
    }
    
    protected Depot depot;
    protected ArrayList<DropPoint> dropPoints;
    protected Distances dists;
    
    protected ProblemState state;
    protected ProblemState bestState;	//Current best state.
    protected double temperature;
    protected double p_absoluteTemperature;
    
    protected ArrayList<Double> fitness;
    
    public Problem(Distances dists){
        this.randGen = new Random();
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
        this.state = initialState();
        this.bestState = initialState();
    }
    
    /*
     * Get the result of the algorithm
     * @return Result of the execution
     */
    public ProblemState getResult(){
        return this.bestState;
    }
    
    /*
     * Method for sampling n evenly-spaced values of the fitness of the problem 
     * over the optimization process (over time)
     * @param n Number of samples to take
     * @return n samples of the fitness
     */
    public ArrayList<Double> sampleHistoricFitness(int n){
        if(this.fitness == null ||this.fitness.size() < n){
            return null;
        }
        
        ArrayList<Double> ret = new ArrayList<Double>();
        
        float freq = this.fitness.size() / n;
        for(int i=1;i<=n;i++){
            int idx = Math.round(i*freq-1);
            ret.add(this.fitness.get(idx));
        }
        
        return ret;
    }
    
    // ** Calculate permutation of the solution (next state)
    // ***************************************************************
    
    protected ProblemState initialState(){
        ProblemState state = new ProblemState(this.dists, this.depot);
        state.randomizeSolution();
        
        return state;
    }
    
    protected ProblemState nextState(){
        ProblemState ret = null;
        
        while(ret == null){
            int val = randGen.nextInt(3);
            switch(val){
                case 0:
                    ret = operatorSwapTourMembers();
                    break;
                case 1:
                    ret = operatorMoveTourMember();
                    break;
                case 2:
                    ret = operatorMoveFromTour();
                    break;
            }
        }
        
        return ret;
    }
    
    protected ProblemState operatorSwapTourMembers(){
        // Clone current solution
        ProblemState ret = new ProblemState(this.state);
        
        // Select affected tour
        Tour tour = ret.getRandomTour(false, true);
        if(tour == null) return null;
        
        // Select two random members of the tour and swap
        int m1 = randGen.nextInt(tour.getNumWaypoints());
        int m2 = randGen.nextInt(tour.getNumWaypoints());
        tour.swapWaypoints(m1, m2);
        
        return ret;
    }
    
    protected ProblemState operatorMoveTourMember(){
        // Clone current solution
        ProblemState ret = new ProblemState(this.state);
        
        // Select affected tour
        Tour tour = ret.getRandomTour(false, true);
        if(tour == null) return null;
        
        // Remove a tour member and insert to a random position
        Location memb = tour.delWaypoint();
        int ins = randGen.nextInt(tour.getNumWaypoints() + 1);
        tour.addWaypoint(ins, memb);
        
        return ret;
    }
    
    protected ProblemState operatorMoveFromTour(){        
        // Clone current solution
        ProblemState ret = new ProblemState(this.state);
        
        // Select origin & destination tours
        Tour oTour = ret.getNonemptyTour();
        Tour dTour = ret.getRandomTour(false, false);
        if(oTour == null) return null;
        
        // Select two random members of the tour and swap
        Location memb = oTour.delWaypoint();
        dTour.addWaypoint(randGen.nextInt(dTour.getNumWaypoints() + 1), memb);
        
        return ret;
    }
}
