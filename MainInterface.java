import java.util.ArrayList;
import java.io.FileWriter;
import java.io.PrintWriter;

import locations.Location;
import locations.Depot;
import locations.DropPoint;

import data.Distances;
import data.DataParser;

import algorithm.ProblemAnnealing;
import algorithm.ProblemReAnnealing;

public class MainInterface {
        static double capacity;
        static double minDemand;
    
        static ArrayList<Depot> depots;
        static ArrayList<DropPoint> dropPoints;
        static ArrayList<Distances> clusters;
        
	public static void main(String[] args) {
            // Calculate error surfaces 
            //loadResource("inputData/eil33_select.vrp");
            //double[] ct_annealing   = calculateErrorSurfaceAnnealing();
            //double[] ct_reannealing = calculateErrorSurfaceReAnnealing();
            
            // Calculate historical mean values
            loadResource("inputData/eilA76.vrp");
            calculateVectorOfResults(0.988, 100, 8.4, 100, 200);
	}
        
        private static double[] calculateErrorSurfaceAnnealing(){
            // Prepare output file
            FileWriter outFile;
            try{
                outFile = new FileWriter("surf_anneal.txt");
            }catch(Exception e){
                System.out.println("ERROR! Couldn't open surf_anneal.txt");
                return new double[]{-1,-1};
            }
            PrintWriter out = new PrintWriter(outFile);
            
            double[] c = new double[]{0.99, 0.999, 0.9999, 0.99999};
            double[] t = new double[]{1, 10, 100, 1000};
            double[][] surf = new double[c.length][t.length];
            
            double best_error = -1;
            double best_c = 0;
            double best_t = 0;
            
            out.println();
            out.println("Error surface for Annealing:");
            
            for(int ci=0;ci<c.length;ci++){
                for(int ti=0;ti<t.length;ti++){
                    surf[ci][ti] = 0;
                    
                    for(int i=0;i<31;i++){
                        for(Distances cluster: clusters){
                            ProblemAnnealing pAnnealing = new ProblemAnnealing(cluster, capacity, minDemand);
                            pAnnealing.run(t[ti], 0.001, c[ci]);
                            surf[ci][ti] += pAnnealing.getResult().getFitnessValue();
                        }
                    }
                    
                    surf[ci][ti] /= 31;
                    out.print(surf[ci][ti]+((ti<t.length-1) ? "," : ""));
                    
                    if(best_error < 0 || best_error > surf[ci][ti]){
                        best_error = surf[ci][ti];
                        best_c = c[ci];
                        best_t = t[ti];
                    }
                }
                
                if(ci<c.length-1){
                    out.println(';');
                }
            }
            
            out.close();
            return new double[]{best_c, best_t};
        }
        
        private static double[] calculateErrorSurfaceReAnnealing(){
            // Prepare output file
            FileWriter outFile;
            try{
                outFile = new FileWriter("surf_reanneal.txt");
            }catch(Exception e){
                System.out.println("ERROR! Couldn't open surf_reanneal.txt");
                return new double[]{-1,-1};
            }
            PrintWriter out = new PrintWriter(outFile);
            
            double[] c = new double[]{7.5, 7.75, 8, 8.25, 8.5, 8.75, 9, 9.25, 9.75, 10};
            double[] t = new double[]{1, 10, 100, 1000, 10000, 100000};
            double[][] surf = new double[c.length][t.length];
            
            double best_error = -1;
            double best_c = 0;
            double best_t = 0;
            
            out.println();
            out.println("Error surface for Re-Annealing:");
            
            for(int ci=0;ci<c.length;ci++){
                for(int ti=0;ti<t.length;ti++){
                    surf[ci][ti] = 0;
                    
                    for(int i=0;i<31;i++){
                        for(Distances cluster: clusters){
                            ProblemReAnnealing pReAnnealing = new ProblemReAnnealing(cluster, capacity, minDemand);
                            pReAnnealing.run(t[ti], 0.001, c[ci]);
                            surf[ci][ti] += pReAnnealing.getResult().getFitnessValue();
                        }
                    }
                    
                    surf[ci][ti] /= 31;
                    out.print(surf[ci][ti]+((ti<t.length-1) ? "," : ""));
                    
                    if(best_error < 0 || best_error > surf[ci][ti]){
                        best_error = surf[ci][ti];
                        best_c = c[ci];
                        best_t = t[ti];
                    }
                }
                
                if(ci<c.length-1){
                    out.println(';');
                }
            }
            
            out.close();
            return new double[]{best_c, best_t};
        }
        
        private static void calculateVectorOfResults(double sa_c, double sa_t, double ra_c, double ra_t, int nvals){
            FileWriter outFile;
            PrintWriter out;
            
            double[] results_reannealing = new double[nvals];
            double[] results_annealing = new double[nvals];
            
            for(int it=0;it<nvals;it++){
                results_annealing[it] = 0;
                for(Distances cluster: clusters){
                    ProblemAnnealing pAnnealing = new ProblemAnnealing(cluster, capacity, minDemand);
                    pAnnealing.run(sa_t, 0.001, sa_c);
                    results_annealing[it] += pAnnealing.getResult().getFitnessValue();
                }
                
                results_reannealing[it] = 0;
                for(Distances cluster: clusters){
                    ProblemReAnnealing pReAnnealing = new ProblemReAnnealing(cluster, capacity, minDemand);
                    pReAnnealing.run(ra_t, 0.001, ra_c);
                    results_reannealing[it] += pReAnnealing.getResult().getFitnessValue();
                }
            }
            
            try{
                outFile = new FileWriter("results_anneal.txt");
                out = new PrintWriter(outFile);
                
                out.println("ANNEALING: Multiple runs results");
                for(int i=0;i<nvals;i++){
                    out.print((results_annealing[i])+",");
                }

                out.close();
            }catch(Exception e){
                System.out.println("ERROR! Couldn't open results_anneal.txt");
            }
            
            try{
                outFile = new FileWriter("results_reanneal.txt");
                out = new PrintWriter(outFile);
                
                out.println("RE-ANNEALING: Multiple runs results");
                for(int i=0;i<nvals;i++){
                    out.print((results_reannealing[i])+",");
                }

                out.close();
            }catch(Exception e){
                System.out.println("ERROR! Couldn't open results_reanneal.txt");
            }
        }
        
        /*
         * Calculate the mean value for each instant of time of the algorithm,
         * where the internal variable nvals defines the number of iterations to
         * take into account.
         */
        private static void calculateHistoricalMeanValues(double sa_c, double sa_t, double ra_c, double ra_t, int nruns){
            FileWriter outFile;
            PrintWriter out;
            
            int niterations = 1000;
            
            double[] historic_reannealing = new double[niterations];
            double[] historic_annealing = new double[niterations];
            
            double[][] historic_ci_annealing = new double[nruns][niterations];
            double[][] historic_ci_reannealing = new double[nruns][niterations];
            
            for(int i=0;i<niterations;i++){
                historic_reannealing[i] = 0;
                historic_annealing[i] = 0;
            }
            
            for(int it=0;it<nruns;it++){
                // Run problem for annealing
                for(Distances cluster: clusters){
                    
                    ProblemAnnealing pAnnealing = new ProblemAnnealing(cluster, capacity, minDemand);
                    pAnnealing.run(sa_t, 0.001, sa_c);
                    
                    // Computation of historical menan values
                    // ********************************************
                    
                    ArrayList<Double> sampled = pAnnealing.sampleHistoricFitness(niterations, niterations);
                    for(int i=0;i<sampled.size();i++){
                        historic_annealing[i] += sampled.get(i).doubleValue();
                        historic_ci_annealing[it][i] = sampled.get(i).doubleValue();
                    }
                }

                // Run problem for re-annealing
                for(Distances cluster: clusters){
                    ProblemReAnnealing pReAnnealing = new ProblemReAnnealing(cluster, capacity, minDemand);
                    pReAnnealing.run(ra_t, 0.001, ra_c);
                    
                    // Computation of historical menan values & CI
                    // ********************************************
                    
                    ArrayList<Double> sampled = pReAnnealing.sampleHistoricFitness(niterations, niterations);
                    for(int i=0;i<sampled.size();i++){
                        historic_reannealing[i] += sampled.get(i).doubleValue();
                        historic_ci_reannealing[it][i] = sampled.get(i).doubleValue();
                    }
                }
            }
            
            // Printing of historical menan values, annealing
            // ********************************************
            
            try{
                outFile = new FileWriter("means_anneal.txt");
                out = new PrintWriter(outFile);
                
                out.println("ANNEALING: " + niterations + "-sample mean progress");
                for(int i=0;i<niterations;i++){
                    out.println((historic_annealing[i] / nruns)+"");
                }

                out.close();
            }catch(Exception e){
                System.out.println("ERROR! Couldn't open means_anneal.txt");
            }
            
            // Printing of historical menan values, re-annealing
            // ********************************************
            
            try{
                outFile = new FileWriter("means_reanneal.txt");
                out = new PrintWriter(outFile);
                
                out.println("RE-ANNEALING: " + niterations + "-sample mean progress");
                for(int i=0;i<niterations;i++){
                    out.println((historic_reannealing[i] / nruns)+"");
                }

                out.close();
            }catch(Exception e){
                System.out.println("ERROR! Couldn't open means_reanneal.txt");
            }
            
            // Computation of CI for the historical values
            // ********************************************
            
            double fhistoric_ci_reannealing[] = new double[niterations];
            double fhistoric_ci_annealing[] = new double[niterations];
            for(int i=0;i<niterations;i++){
                // Get mean value
                double mean_reannealing = 0;
                double mean_annealing = 0;
                for(int j=0;j<nruns;j++){
                    mean_reannealing += historic_ci_reannealing[j][i];
                    mean_annealing += historic_ci_annealing[j][i];
                }
                mean_reannealing /= nruns;
                mean_annealing /= nruns;
                
                // Calculate stdev
                double stdev_reannealing = 0;
                double stdev_annealing = 0;
                for(int j=0;j<nruns;j++){
                    double val1 = historic_ci_reannealing[j][i] - mean_reannealing;
                    stdev_reannealing += Math.pow(historic_ci_reannealing[j][i] - mean_reannealing, 2);
                    stdev_annealing += Math.pow(historic_ci_annealing[j][i] - mean_annealing, 2);
                }
                stdev_reannealing = Math.sqrt(stdev_reannealing / (nruns - 1));
                stdev_annealing = Math.sqrt(stdev_annealing / (nruns - 1));
                
                // Calculate CI
                fhistoric_ci_reannealing[i] = 1.96 * stdev_reannealing / Math.sqrt(nruns);
                fhistoric_ci_annealing[i] = 1.96 * stdev_annealing / Math.sqrt(nruns);
            }
            
            // Printing of CI for the historical values, annealing
            // ********************************************
            
            try{
                outFile = new FileWriter("ci_anneal.txt");
                out = new PrintWriter(outFile);

                out.println("ANNEALING: " + niterations + "-sample 95% CI variation progress");
                for(int i=0;i<niterations;i++){
                    out.println(fhistoric_ci_annealing[i]+"");
                }
                out.println("");

                out.close();
            }catch(Exception e){
                System.out.println("ERROR! Couldn't open ci_anneal.txt");
            }
            
            // Printing of CI for the historical values, re-annealing
            // ********************************************
            
            try{
                outFile = new FileWriter("ci_reanneal.txt");
                out = new PrintWriter(outFile);

                out.println("RE-ANNEALING: " + niterations + "-sample 95% CI variation progress");
                for(int i=0;i<niterations;i++){
                    out.println(fhistoric_ci_reannealing[i]+"");
                }
                out.println("");

                out.close();
            }catch(Exception e){
                System.out.println("ERROR! Couldn't open ci_reanneal.txt");
            }
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
        
        private static void loadResource(String resource){
            // Parse drop points and depots for parametes estimation set
            DataParser parser = new DataParser();
            parser.parseFile(resource);
            
            // Save parsed values
            capacity = parser.getTruckCapacity();
            minDemand = parser.getMinDemand();
            depots = parser.getDepotsList();
            dropPoints = parser.getDropPointsList();

            // Cluster locations by proximity
            clusters = clusterLocations(depots, dropPoints);
        }
}