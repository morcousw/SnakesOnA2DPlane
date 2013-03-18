package map;

import snakesOnA2DPlane.SnakesOnA2DPlane;

public class MapNode {
	public int g = 0, f, h;
	public int x, y;
	public boolean isWalkable;
	public MapNode parent;
	public MapNode finalChild;
	public MapNode(int x, int y) {
		this.x = x;
		this.y = y;
		isWalkable = true;
	}
	
	public int calculateF() {
		return g + h;
	}
	
	public int calculateG() {
		int temp = parent.g;
		
		if (parent.x == this.x || parent.y == this.y)
			temp += 10;
		else
			temp += 15;
		
		return temp;
	}
	
	public int calculateH(int targetX, int targetY) {
		return Math.abs(targetX - this.x) + Math.abs(targetY - this.y);
	}
	
	public void setValues (int f, int g, int h) {
		this.f = f;
		this.g = g;
		this.h = h;
	}
	
	public double getXCoord() {
		return x * SnakesOnA2DPlane.GRID_UNIT_DIM;
	}
	
	public double getYCoord() {
		return y * SnakesOnA2DPlane.GRID_UNIT_DIM;
	}
	
	public String toString() {
		return "map["+y+"]["+x+"]: g = " + g + ", f = " + f + ", h = " + h + (parent != null ?  ", Parent: " + parent.y + ", " + parent.x + "." : ".");
	}
}
