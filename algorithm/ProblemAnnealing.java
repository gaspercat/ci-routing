/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import java.util.Random;

import data.*;
import locations.*;

/**
 *
 * @author gaspercat
 */
public class ProblemAnnealing extends Problem{
    double temperature;
    Random randGen;
    
    public ProblemAnnealing(Distances dists, int maxTours){
        super(dists, maxTours);
        
        this.temperature = 20000;
        this.randGen = new Random();
    }
    
    public void run(){
        boolean selected = false;
        while(!selected){
            ProblemState next = nextState();
            selected = isStateSelected(next);
            if(selected) this.state = next;
        }
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
    
    private ProblemState nextState(){
    
    }
    
    private ProblemState operatorMoveFromTour(){
        ProblemState ret = new ProblemState(this.state);
        
        // TODO: Implement operator
        
        return ret;
    }
}
