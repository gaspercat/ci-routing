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
        p_stateSpaceSize = getStateSpaceSize();
    }
    
    /*
     * Run algorithm with defaut parameters
     */
    public void run(){
        this.p_absoluteTemperature = 0.001;
        this.p_temperatureFactor = 0.7;
        
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
            if(iter % SAMPLING_INTERVAL*1 == 0){
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
    
    private double getStateSpaceSize(){
        int nLocations = this.dropPoints.size();
        int nTours = this.maxTours;
        
        // Number of possible cut points & number of cuts
        int n = nLocations + 1;
        int k = nTours - 1;
        
        // Get number of permutations of the drop points and number of ways to
        // cluster a singe permutation (k-combination with repetitions)
        double pDrops = factorial(nLocations);
        double nDivides = factorial(n + k - 1) / (factorial(k) * factorial(n - 1));
                
        // Return final size of states space
        return Math.log(pDrops * nDivides);
    }
    
    private double getCurrentTemperature(int time){
        if(time == 0) return this.temperatureO;
        double pow = this.p_temperatureFactor * Math.pow(time, 1/p_stateSpaceSize);
        return this.temperatureO * Math.exp(-pow);
    }
    
    private double factorial(int n){
        double ret = 1;
        
        for(int i=1;i<=n;i++){
            ret *=i;
        }
        
        return ret;
    }
}
