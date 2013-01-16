package data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import locations.*;

public class DataParser {
	private enum Phase {
	    PROPERTIES,
	    NODE_COORD,
	    DEMAND,
	    DEPOT
	}
	
	
	private int dimCount, truckCapacity; 
	private Phase currentPhase = Phase.PROPERTIES;
	private ArrayList<DropPoint> dropPoints = new ArrayList<DropPoint>();
	private ArrayList<Depot> depots = new ArrayList<Depot>();
	
	public DataParser() {
		
	}
	
	public void parseFile(String fileName) {
		for (String line : readFile(fileName)) {
			parseLine(line);
		}
	}
	
	public ArrayList<DropPoint> getDropPointsList() {
            return dropPoints;
        }
        
	public ArrayList<Depot> getDepotsList() { 
            return depots;
        }
	
	private void parseLine(String line) {
		line = line.toUpperCase().trim();
		if (line.contains("EDGE_WEIGHT_TYPE")) {
			String type = line.split(":")[1].trim();
			if (!type.equalsIgnoreCase("EUC_2D"))
				System.out.println("ERROR: Wrong edge weight type : " + type);
			return;
		}
		
		if (line.contains("TYPE")) {
			String type = line.split(":")[1].trim();
			if (!type.equalsIgnoreCase("CVRP"))
				System.out.println("ERROR: Wrong file type : " + type);
			return;
		}
		
		if (line.contains("DIMENSION")) {
			try { 
				dimCount = Integer.parseInt(line.split(":")[1].trim()); 
			} catch (NumberFormatException numForEx) {
				System.out.println("ERROR : can't convert to int : " + line.split(":")[1].trim());
			}
			return;
		}
		
		if (line.contains("CAPACITY")) {
			try { 
				truckCapacity = Integer.parseInt(line.split(":")[1].trim());
			} catch (NumberFormatException numForEx) {
				System.out.println("ERROR : can't convert to int : " + line.split(":")[1].trim());
			}
			return;
		}
		
		if (line.contains("NODE_COORD_SECTION")) {
			currentPhase =  Phase.NODE_COORD;
			return;
		}

		if (line.contains("DEMAND_SECTION")) {
			currentPhase =  Phase.DEMAND;
			return;
		}
		
		if (line.contains("DEPOT_SECTION")) {
			currentPhase =  Phase.DEPOT;
			return;
		}
		
		if (line.contains("EOF")) {
			System.out.println("End of file is reached.");
			return;
		}
		
		if (currentPhase == Phase.NODE_COORD) {
			int nodeNumber = Integer.parseInt(line.split(" ")[0].trim());
			int nodeX = Integer.parseInt(line.split(" ")[1].trim());
			int nodeY = Integer.parseInt(line.split(" ")[2].trim());

//			//Check a node number.
//			if (nodeNumber > dimCount) {
//				System.out.println("ERROR : Too big a node number ." + line + " . " + line.split(" ")[0] + " > " + dimCount);
//				return;
//			}
			dropPoints.add(new DropPoint(nodeX, nodeY));	
			return;
		}
		
		if (currentPhase == Phase.DEMAND) {
			int nodeNumber = Integer.parseInt(line.split(" ")[0].trim());
			int demand = Integer.parseInt(line.split(" ")[1].trim());
			dropPoints.get(nodeNumber-1).setDemandAmount(demand);
			
			return;
		}
		
		if (currentPhase == Phase.DEPOT) {
			int nodeNumber = Integer.parseInt(line.trim());
			if (nodeNumber < 1) return;

			//Supose that all nodes goes in ascending linear order.
			depots.add(new Depot(dropPoints.get(nodeNumber-1).getX(), dropPoints.get(nodeNumber-1).getY()));
			//It's not a dropPoint so remove it from the list.
			dropPoints.remove(nodeNumber-1);
			return;
		}
		
	}

	private List<String> readFile(String fileName) {
		List<String> lines = new ArrayList<String>();
		BufferedReader br = null;
		try {
			String line;
			br = new BufferedReader(new FileReader(fileName));
			while ((line = br.readLine()) != null) {
				lines.add(line.toUpperCase().trim());
			}
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: Input file " + fileName + " not found");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return lines;
	}
}
