/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package locations;

/**
 *
 * @author gaspercat
 */
public abstract class Location {
    //Simple 2D euclidean coordinates.
    protected int x,y;

    public Location() {

    }
	
    public Location(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    public int getX() { return x;}
    public int getY() { return y;}
    
    public void setX(int x) { this.x = x;}
    public void setY(int y) { this.y = y;}
    
    public double distanceTo(Location loc){
        double dist = 0;
        
        dist += loc.x * this.x;
        dist += loc.y * this.y;
        return Math.sqrt(dist);
    }
}
