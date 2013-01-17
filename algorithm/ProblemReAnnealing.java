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
    double p_absoluteTemperature;
    double p_temperatureFactor;
    
    double temperatureO;
    double temperatureC;
    
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
        this.temperatureO = 100;
        this.temperatureC = 100;
        
        runAlgorithm();
    }
    
    /*
     * Run algorithm with custom parameters
     */
    public void run(double temp, double absTemp, double coolRate){
        this.p_absoluteTemperature = absTemp;
        this.p_temperatureFactor = coolRate;
        
        // Set original and current temperature
        this.temperatureO = temp;
        this.temperatureC = temp;
        
        runAlgorithm();
    }
    
    private void runAlgorithm(){
        // Initialize fitness historical data
        fitness = new ArrayList<Double>();  
      
        int iter = 0;
        
        // While temperature higher than absolute temperature
        this.temperatureC = getCurrentTemperature(0);
        while(this.temperatureC > this.p_absoluteTemperature){
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
            this.temperatureC = getCurrentTemperature(iter);
        }
        
        return;
    }
    
    private boolean isStateSelected(Problem.ProblemState state){
        double dC = this.state.getTotalDistance();
        double dN = state.getTotalDistance();
        
        // If new state better than current one, accept it
        if(dN <= dC){
            return true;
        }
        
        // Calculate probability of acceptance
        double pow = (dN - dC) / this.temperatureC;
        double p = 1 / (1 + Math.exp(pow));

        return this.randGen.nextDouble() <= p;
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
        if(time == 0) return this.temperatureO;
        
        double pow = this.p_temperatureFactor * Math.pow(time, 1 / this.p_stateSpaceSize);
        double temp = this.temperatureO * Math.exp(-pow);
        
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
