import java.util.ArrayList;

import locations.Location;
import locations.Depot;
import locations.DropPoint;

import data.Distances;
import data.DataParser;

import algorithm.ProblemAnnealing;

public class MainInterface {
	public static void main(String[] args) {
            DataParser parser = new DataParser();

            // Parse drop points and depots
            parser.parseFile("inputData/eil22.vrp");
            ArrayList<Depot> depots = parser.getDepotsList();
            ArrayList<DropPoint> dropPoints = parser.getDropPointsList();

            // Cluster locations by proximity
            ArrayList<Distances> clusters = clusterLocations(depots, dropPoints);

            // Run problem for annealing
            double aDistance = 0;
            for(Distances cluster: clusters){
                ProblemAnnealing pAnnealing = new ProblemAnnealing(cluster, 4);
                pAnnealing.run();
                // TODO: Show result
            }
            
            System.out.println("Annealing total distance:" + aDistance);

            // Run problem for re-annealing
            double rDistance = 0;
            for(Distances cluster: clusters){
                //ProblemReAnnealing pReAnnealing = new ProblemReAnnealing(cluster, 4);
                // TODO: Show result
            }
            
            System.out.println("Reannealing total distance:" + rDistance);
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