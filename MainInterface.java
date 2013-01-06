import java.util.ArrayList;

import locations.Depot;
import locations.DropPoint;
import data.DataParser;

public class MainInterface {
	public static void main(String[] args) {
		DataParser parser = new DataParser();
		parser.parseFile("inputData/eil22.vrp");		
		
		ArrayList<DropPoint> dropPoints = parser.getDropPointsList();
		ArrayList<Depot> depots = parser.getDepotsList();
	}
}