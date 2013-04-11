package map;

import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import snakesOnA2DPlane.SnakesOnA2DPlane;
import entities.AbstractEntity;
import entities.Box;

public class MapGrid {
	public MapNode[][] map;
	public boolean isShowGrid;
	private int numVert;
	private int numHoriz;
	
	public MapGrid() {
		isShowGrid = false;
		// Split the map into a grid of size numHoriz * numVert
		numVert = SnakesOnA2DPlane.DISPLAY_HEIGHT / SnakesOnA2DPlane.GRID_UNIT_DIM;
		numHoriz = SnakesOnA2DPlane.DISPLAY_WIDTH / SnakesOnA2DPlane.GRID_UNIT_DIM;
		// Create an array to store the values of each square
		// NOTE: It is an integer array where value = 0 means it is not
		// walkable, value = 1 means it is walkable
		map = new MapNode[numVert][numHoriz];
		for (int i = 0; i < numVert; i++) {
			for (int j = 0; j < numHoriz; j++) {
				map[i][j] = new MapNode(j, i);
			}
		}
	}
	
	public void gridify() {
		int xStartPos, yStartPos;
		int xEndPos, yEndPos;

		for (int i = 0; i < numVert; i++) {
			for (int j = 0; j < numHoriz; j++) {				
				//Check if there is a wall in this square. If there is, mark it not walkable
				for (Box b : SnakesOnA2DPlane.obstacles)
				{
					//The position of this box is:
					xStartPos = SnakesOnA2DPlane.GRID_UNIT_DIM * j;
					xEndPos = xStartPos + SnakesOnA2DPlane.GRID_UNIT_DIM;
					yStartPos = SnakesOnA2DPlane.GRID_UNIT_DIM * i;
					yEndPos = yStartPos + SnakesOnA2DPlane.GRID_UNIT_DIM;
					
					//After 20 minutes and a whiteboard...
					if (!map[i][j].isWalkable
						||(between(b.getX(), xStartPos, xEndPos) && between(b.getY(), yStartPos, yEndPos))
						||(between(b.getX(), xStartPos, xEndPos) && between(b.getY() + b.getHeight(), yStartPos, yEndPos))
						||(between(b.getX() + b.getWidth(), xStartPos, xEndPos) && between(b.getY(), yStartPos, yEndPos))
						||(between(b.getX() + b.getWidth(), xStartPos, xEndPos) && between(b.getY() + b.getHeight(), yStartPos, yEndPos))
						||((between(b.getX(), xStartPos, xEndPos) || between(b.getX() + b.getWidth(), xStartPos, xEndPos)) && b.getY() < yStartPos && b.getY() + b.getHeight() > yEndPos)
						||((between(b.getY(), yStartPos, yEndPos) || between(b.getY() + b.getHeight(), yStartPos, yEndPos)) && b.getX() < xStartPos && b.getX() + b.getWidth() > xEndPos)
						||(b.getX() < xStartPos && (b.getX() + b.getWidth() > xEndPos) && b.getY() < yStartPos && (b.getY() + b.getHeight() > yEndPos))
					) {
						map[i][j].isWalkable = false;
					} else {
						map[i][j].isWalkable = true;
					}
				}
			}	
		}
	}
	
	public MapNode findClosestNode(AbstractEntity a) {
		int xPos = ((int)a.getX() / SnakesOnA2DPlane.GRID_UNIT_DIM) % (SnakesOnA2DPlane.DISPLAY_WIDTH/SnakesOnA2DPlane.GRID_UNIT_DIM);
		int yPos = ((int)a.getY() / SnakesOnA2DPlane.GRID_UNIT_DIM) % (SnakesOnA2DPlane.DISPLAY_HEIGHT/SnakesOnA2DPlane.GRID_UNIT_DIM);
		return map[yPos][xPos];
	}

	public boolean between(double x, int a, int b) {
		return (x <= b && x >= a);
	}
	
	public ArrayList<MapNode> getAdjacentNodes(MapNode m) {
		ArrayList<MapNode> adjacentNodes = new ArrayList<MapNode>();
			
		if (checkBounds(m.y - 1, m.x - 1)) adjacentNodes.add(map[m.y - 1][m.x - 1]);
		if (checkBounds(m.y - 1, m.x)) adjacentNodes.add(map[m.y - 1][m.x]);
		if (checkBounds(m.y - 1, m.x + 1)) adjacentNodes.add(map[m.y - 1][m.x + 1]);
		if (checkBounds(m.y + 1, m.x - 1)) adjacentNodes.add(map[m.y + 1][m.x - 1]);
		if (checkBounds(m.y + 1, m.x)) adjacentNodes.add(map[m.y + 1][m.x]);
		if (checkBounds(m.y + 1, m.x + 1)) adjacentNodes.add(map[m.y + 1][m.x + 1]);
		if (checkBounds(m.y, m.x - 1)) adjacentNodes.add(map[m.y][m.x - 1]);
		if (checkBounds(m.y, m.x + 1)) adjacentNodes.add(map[m.y][m.x + 1]);
		
		return adjacentNodes;
	}
	
	public boolean checkBounds(int y, int x) {
		int yUB = SnakesOnA2DPlane.DISPLAY_HEIGHT / SnakesOnA2DPlane.GRID_UNIT_DIM; //y upper bound
		int xUB = SnakesOnA2DPlane.DISPLAY_WIDTH / SnakesOnA2DPlane.GRID_UNIT_DIM; //y lower bound
		
		if (y >= 0 && y < yUB && x >= 0 && x < xUB && map[y][x] != null)
			return true;
		
		return false;
	}
	
	public void render() {
		if (isShowGrid)
		{
			for (int i = 0; i < SnakesOnA2DPlane.DISPLAY_HEIGHT / SnakesOnA2DPlane.GRID_UNIT_DIM; i++)
			{
				for (int j = 0; j < SnakesOnA2DPlane.DISPLAY_WIDTH / SnakesOnA2DPlane.GRID_UNIT_DIM; j++)
				{
					if (!map[i][j].isWalkable)
						GL11.glColor3f(255, 0, 0);
					else
						GL11.glColor3f(0, 255, 0);
					
					int lx = j * SnakesOnA2DPlane.GRID_UNIT_DIM;
					int rx = (j + 1) * SnakesOnA2DPlane.GRID_UNIT_DIM;
					int ty = i * SnakesOnA2DPlane.GRID_UNIT_DIM;
					int by = (i + 1) * SnakesOnA2DPlane.GRID_UNIT_DIM;
					
					GL11.glBegin(GL11.GL_LINE_LOOP);
					glVertex2f(lx, ty);
					glVertex2f(rx, ty);
					glVertex2f(rx, by);
					glVertex2f(lx, by);
					GL11.glEnd();
				}
			}
		}
	}
}
