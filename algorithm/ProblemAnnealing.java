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
    double p_coolingRate;
    int    p_tIterations;
        
    public ProblemAnnealing(Distances dists, double mCapacity, double minDemand){
        super(dists, mCapacity, minDemand);
        this.randGen = new Random();
    }
    
    /*
     * Run algorithm with defaut parameters
     */
    public void run(){
        this.p_absoluteTemperature = 0.001;
        this.p_coolingRate = 0.9999;
        this.p_tIterations = 50;
        this.temperature = 100;
        
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
            boolean selected = false;
            //while(!selected){
            //for(int i=0;i<this.p_tIterations;i++){
                // Select next state
                ProblemState next = nextState();
                if(isStateSelected(next)){
                    selected = true;
                    this.state = next;
                }

                // Lower temperature
                this.temperature *= p_coolingRate;
            //}
            
            // Sample data when needed
            if(iter % SAMPLING_INTERVAL == 0){
                double fvalue = this.state.getFitnessValue();
                this.fitness.add(new Double(fvalue));
            }
            iter++;
        }
        
        System.out.println(iter);
        return;
    }
    
    protected boolean isStateSelected(Problem.ProblemState state){
        double dC = this.state.getFitnessValue();
        double dN = state.getFitnessValue();
        
        if (dN < bestState.getFitnessValue())
        	bestState = state;
        
        // If new state better than current one, accept it
        if(dN <= dC){
            return true;
        }
        
        // Calculate probability of acceptance
        double pow = (dN - dC) / this.temperature;
        double p = 1 / (1 + Math.exp(pow));

        return this.randGen.nextDouble() <= p;
    }
}
