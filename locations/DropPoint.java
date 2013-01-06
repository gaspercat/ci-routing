/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package locations;

/**
 *
 * @author gaspercat
 */
public class DropPoint extends Location{
	private int demand, currentDemand;
	
	public DropPoint (int x, int y) { super (x, y);}
	
	public void setDemandAmount(int value) {
		demand = value;
		currentDemand = value;
	}
	
	public int getCurrentDemand() { return currentDemand;}
	
	public void deliverGoods(int value) { currentDemand -= value;}
    
}
