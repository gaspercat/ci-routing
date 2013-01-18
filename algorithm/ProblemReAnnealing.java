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
public class ProblemReAnnealing extends Problem{
    double p_stateSpaceSize;
    double p_temperatureFactor;
    
    double temperatureInitial;
    
    public ProblemReAnnealing(Distances dists, int maxTours){
        super(dists, maxTours);
        this.randGen = new Random();
        
        // Set size of the states space
        p_stateSpaceSize = getProblemDimensionality();
    }
    
    /*
     * Run algorithm with defaut parameters
     */
    public void run(){
        this.p_absoluteTemperature = 0.25;
        this.p_temperatureFactor = 3;
        
        // Set original and current temperature
        this.temperatureInitial = 100;
        this.temperature = 100;
        
        runAlgorithm();
    }
    
    /*
     * Run algorithm with custom parameters
     */
    public void run(double temp, double absTemp, double coolRate){
        this.p_absoluteTemperature = absTemp;
        this.p_temperatureFactor = coolRate;
        
        // Set original and current temperature
        this.temperatureInitial = temp;
        this.temperature = temp;
        
        runAlgorithm();
    }
    
    private void runAlgorithm(){
        // Initialize fitness historical data
        fitness = new ArrayList<Double>();  
      
        int iter = 0;
        
        // While temperature higher than absolute temperature
        this.temperature = getCurrentTemperature(0);
        while(this.temperature > this.p_absoluteTemperature){
            // Select next state
            Problem.ProblemState next = nextState();
            if(isStateSelected(next)){
                this.state = next;
            }
            
            // Sample data when needed
            if(iter % SAMPLING_INTERVAL == 0){
                double fvalue = this.state.getTotalDistance();
                this.fitness.add(new Double(fvalue));
            }
            iter = iter + 1;
            
            // Lower temperature
            this.temperature = getCurrentTemperature(iter);
        }
        
        return;
    }
    
    private boolean stopCriterionMet(){
        int size = this.fitness.size();
        if(size <= 10){
            return false;
        }
        
        double tFitness = this.fitness.get(size - 1).doubleValue();
        for(int i=2;i<=10;i++){
            if(this.fitness.get(size - i).doubleValue() != tFitness){
                return false;
            }
        }
        
        return true;
    }
    
    private double getProblemDimensionality(){
        // Number of dimensions of the problem is equal o the number of locations
        // to visit that can be permutated plus the number of tours that can be
        // used minus one (read documentation for details)
        return this.dropPoints.size() + this.maxTours - 1;
    }
    
    private double getCurrentTemperature(int time){
        if(time == 0) return this.temperatureInitial;
        
        double pow = this.p_temperatureFactor * Math.pow(time, 1 / this.p_stateSpaceSize);
        double temp = this.temperatureInitial * Math.exp(-pow);
        
        return temp;
    }
    
    private double factorial(int n){
        double ret = 1;
        
        for(int i=1;i<=n;i++){
            ret *=i;
        }
        
        return ret;
    }
}
