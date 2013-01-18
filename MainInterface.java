import java.util.ArrayList;

import locations.Location;
import locations.Depot;
import locations.DropPoint;

import data.Distances;
import data.DataParser;

import algorithm.ProblemAnnealing;
import algorithm.ProblemReAnnealing;

public class MainInterface {
	public static void main(String[] args) {
            DataParser parser = new DataParser();

            // Parse drop points and depots
            parser.parseFile("inputData/eil33.vrp");
            double capacity = parser.getTruckCapacity();
            double minDemand = parser.getMinDemand();
            ArrayList<Depot> depots = parser.getDepotsList();
            ArrayList<DropPoint> dropPoints = parser.getDropPointsList();

            // Cluster locations by proximity
            ArrayList<Distances> clusters = clusterLocations(depots, dropPoints);

            // EXECUTION OF THE ALGORITHMS
            // ***************************************************
            
            double[] fitness_values = new double[31];
            
            double c_annealing = 0.9999;
            double c_reannealing = 7.5;
            
            for(int it=0;it<31;it++){
                // Run problem for annealing
                /*for(Distances cluster: clusters){
                    ProblemAnnealing pAnnealing = new ProblemAnnealing(cluster, capacity, minDemand);
                    pAnnealing.run(100, 0.001, c_annealing);
                    
                    double cost = pAnnealing.getResult().getFitnessValue();
                    
                    //System.out.println("C-value: " + c_annealing);
                    //System.out.println("Penalty: " + pAnnealing.getResult().getTotalPenalty());
                    //System.out.println("Distance: " + cost);
                    
                    fitness_values[it] = cost;
                }*/

                // Run problem for re-annealing
                for(Distances cluster: clusters){
                    ProblemReAnnealing pReAnnealing = new ProblemReAnnealing(cluster, capacity, minDemand);
                    pReAnnealing.run(100, 0.001, c_reannealing);
                    
                    double cost = pReAnnealing.getResult().getTotalDistance();

                    //System.out.println("C-value: " + c_reannealing);
                    //System.out.println("Penalty: " + pReAnnealing.getResult().getTotalPenalty());
                    //System.out.println("Distance: " + rDistance);
                    
                    fitness_values[it] = cost;
                }
            }
            
            System.out.println("Cost values:");
            for(int ii=0;ii<31;ii++){
                System.out.print(fitness_values[ii] + ",");
            }
            System.out.println("");
	}
        
        /*
         * Cluster drop points and depots according to proximity and create the
         * corresponding distances matrixes using the euclidean distance.
         * @param depots List of depots
         * @param dropPoints List of drop points
         * @return A list of distances matrixes for each cluster
         */
        private static ArrayList<Distances> clusterLocations(ArrayList<Depot> depots, ArrayList<DropPoint> dropPoints){
            ArrayList<ArrayList<Location>> clusters = new ArrayList<ArrayList<Location>>();
            
            // Initialize lists of clustered drop points
            for(Depot d: depots){
                ArrayList<Location> list = new ArrayList<Location>();
                list.add(d);
                clusters.add(list);
            }
            
            // Assign drop points to nearest depot
            for(DropPoint p: dropPoints){
                // Find nearest depot
                double best_dist = -1;
                int idx = -1;
                for(int i=0;i<depots.size();i++){
                    double dist = depots.get(i).distanceTo(p);
                    if(best_dist < 0 || best_dist > dist){
                        best_dist = dist;
                        idx = i;
                    }
                }
                
                // Assign to best depot
                clusters.get(idx).add(p);
            }
            
            // Build distances matrix of each cluster
            ArrayList<Distances> dists = new ArrayList<Distances>();
            for(ArrayList<Location> cluster: clusters){
                Distances cdists = new Distances(cluster, true);
                dists.add(cdists);
            }
            
            // Return distances matrix of each cluster
            return dists;
        }
}