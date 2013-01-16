/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import java.util.Random;

import data.*;
import java.util.ArrayList;
import locations.*;

/**
 *
 * @author gaspercat
 */
public class ProblemAnnealing extends Problem{
    double p_absoluteTemperature;
    double p_coolingRate;
    int    p_tIterations;
    
    double temperature;
    
    public ProblemAnnealing(Distances dists, int maxTours){
        super(dists, maxTours);
        this.randGen = new Random();
    }
    
    /*
     * Run algorithm with defaut parameters
     */
    public void run(){
        this.p_absoluteTemperature = 0.001;
        this.p_coolingRate = 0.99;
        this.p_tIterations = 20;
        this.temperature = 10000;
        
        runAlgorithm();
    }
    
    /*
     * Run algorithm with custom parameters
     */
    public void run(double temp, double absTemp, double coolRate, int tIter){
        this.p_absoluteTemperature = absTemp;
        this.p_coolingRate = coolRate;
        this.p_tIterations = tIter;
        this.temperature = temp;
        
        runAlgorithm();
    }
    
    public void runAlgorithm(){
        // Initialize fitness historical data
        fitness = new ArrayList<Double>();  
      
        int iter = 0;
        
        // While temperature higher than absolute temperature
        while(this.temperature > this.p_absoluteTemperature){
            // Make t iterations at this temperature
            for(int i=0;i<this.p_tIterations;i++){
                // Select next state
                ProblemState next = nextState();
                if(isStateSelected(next)){
                    this.state = next;
                }

                // Lower temperature
                this.temperature *= p_coolingRate;
            }
            
            // Sample data when needed
            iter++;
            if(iter % SAMPLING_INTERVAL == 0){
                double fvalue = this.state.getTotalDistance();
                this.fitness.add(new Double(fvalue));
            }
        }
        
        return;
    }
    
    private boolean isStateSelected(ProblemState state){
        double dC = this.state.getTotalDistance();
        double dN = state.getTotalDistance();
        
        // If new state better than current one, accept it
        if(dC <= dN){
            return true;
        }
        
        // Calculate probability of acceptance
        double p = Math.exp((dC - dN) / this.temperature);
        return this.randGen.nextDouble() <= p;
    }
    
    // ** Calculate permutation of the solution (next state)
    // ***************************************************************
    
    private ProblemState nextState(){
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
    
    private ProblemState operatorSwapTourMembers(){
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
    
    private ProblemState operatorMoveTourMember(){
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
    
    private ProblemState operatorMoveFromTour(){        
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
