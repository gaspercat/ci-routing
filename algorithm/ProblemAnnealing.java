/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import data.*;
import locations.*;

/**
 *
 * @author gaspercat
 */
public class ProblemAnnealing extends Problem{
    public ProblemAnnealing(Distances dists, int maxTours){
        super(dists, maxTours);
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
        
    }
    
    private ProblemState nextState(){
    
    }
    
    private ProblemState operatorMoveFromTour(){
        ProblemState ret = new ProblemState(this.state);
        
        // TODO: Implement operator
        
        return ret;
    }
}
