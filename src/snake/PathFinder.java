package snake;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import snakesOnA2DPlane.SnakesOnA2DPlane;

import static org.lwjgl.opengl.GL11.*;

import entities.Agent;

import map.MapNode;

public class PathFinder {

	private MapNode startingPoint;
	private MapNode targetPoint;
	private ArrayList<MapNode> openList;
	private ArrayList<MapNode> closedList;
	public boolean isSearching;
	public boolean isPathGenerated;
	public boolean isShowPath;
	
	private SnakeHead s;
	private Agent aStarTarget;
	
	public PathFinder(SnakeHead s, Agent aStarTarget) {
		this.s = s;
		this.aStarTarget = aStarTarget;
		
		openList = new ArrayList<MapNode>();
		closedList = new ArrayList<MapNode>();
		
		this.startingPoint = s.map.findClosestNode(s);
		if (aStarTarget != null)
			this.targetPoint = s.map.findClosestNode(aStarTarget);
		
		isSearching = false;
		isPathGenerated = false;
	}
	
	public void performAStar() {
		isSearching = true;
		
		boolean isDoneWithAStar = false;
		
		while (!isDoneWithAStar) {
			isDoneWithAStar = aStarStep();
		}
		
		aStarStep();
		s.followPathToTarget(startingPoint, aStarTarget.getX(), aStarTarget.getY());
	}
	
	public boolean aStarStep() {
		if (isSearching) {
			if (openList.size() == 0)
				openList.add(startingPoint);

			//Select current node	
			MapNode curNode = null;
			
			int minFScore = Integer.MAX_VALUE;
			for (MapNode m : openList)
			{
				if (m.f < minFScore)
				{
					curNode = m;
					minFScore = m.f;
				}
			}
			
			openList.remove(curNode);
			closedList.add(curNode);
		
			if (curNode != targetPoint)
			{
				
				for (MapNode m : s.map.getAdjacentNodes(curNode))
				{
					
					if (m.isWalkable && !closedList.contains(m))
					{				
						//Add adjacent nodes that are walkable to the open list
						//If the node is already in the open list, check to see if current path is better than previous path
						//Path Scoring
						if (!openList.contains(m))
						{
							m.parent = curNode;
							m.g = m.calculateG();
							m.h = m.calculateH(targetPoint.x, targetPoint.y); 
							m.f = m.calculateF();
							openList.add(m);
						} 	else {
							if (m.calculateG() < m.g)
							{
								m.parent = curNode;
								m.g = m.calculateG();
								m.f = m.calculateF();
							}
						}
					}
					
				}
			}
			
			if (closedList.contains(targetPoint) || openList.size() == 0)
			{
				isSearching = false;
				generatePath();				
				return true;
			}
		}
		return false;
	}
	
	public void generatePath() {
		MapNode curNode = targetPoint;
		System.out.println("Generating Path");
		//System.out.println("Start: " + startingPoint.toString());
		//System.out.println("End: " + targetPoint.toString());
		while (curNode != startingPoint)
		{
			//System.out.println(curNode.toString());
			if (curNode != null && curNode.parent != null)
			{
				curNode.parent.finalChild = curNode;
				curNode = curNode.parent;
			} 
			else
				break;
		}		
		
		isPathGenerated = true;
		System.out.println("Path Generated.");
	}
	
	public void render() {
		if (isPathGenerated && isShowPath) {
			MapNode temp = startingPoint;
			while (temp != targetPoint) {
				GL11.glColor3f(255, 255, 255);
				
				int lx = temp.x * SnakesOnA2DPlane.GRID_UNIT_DIM;
				int rx = (temp.x + 1) * SnakesOnA2DPlane.GRID_UNIT_DIM;
				int ty = temp.y * SnakesOnA2DPlane.GRID_UNIT_DIM;
				int by = (temp.y + 1) * SnakesOnA2DPlane.GRID_UNIT_DIM;
				
				GL11.glBegin(GL11.GL_QUADS);
				glVertex2f(lx, ty);
				glVertex2f(rx, ty);
				glVertex2f(rx, by);
				glVertex2f(lx, by);
				GL11.glEnd();
				
				temp = temp.finalChild;
			}
		}
	}
}
