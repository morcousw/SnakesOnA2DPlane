package snakesOnA2DPlane;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import map.MapNode;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.lwjgl.*;

import physics.StopWatch;
import physics.Vector;

import entities.AbstractEntity;
import entities.Agent;
import entities.Box;
import entities.SnakeBody;
import entities.SnakeHead;
import entities.Agent.AgentType;

import static org.lwjgl.opengl.GL11.*;

public class SnakesOnA2DPlane {
	public static final int DISPLAY_HEIGHT = 760;
	public static final int DISPLAY_WIDTH = 1420;

	public static final int GRID_UNIT_DIM = 15;

	// Timing Variables
	private long lastFrame;
	private boolean isRunning = true;
	private boolean isShowGrid = false;
	
	PrintStream out;
	
	Random r = new Random();

	SnakeHead s;
	ArrayList<Box> obstacles;
	ArrayList<Agent> agents;

	private boolean isSomethingSelected = false;
	
	//A* Stuff
	boolean isSearching = false;
	Agent aStarTarget;
	MapNode[][] map;
	MapNode startingPoint;
	MapNode targetPoint;
	ArrayList<MapNode> openList = new ArrayList<MapNode>();
	ArrayList<MapNode> closedList = new ArrayList<MapNode>();
	boolean isPathGenerated = false;

	StopWatch sw = new StopWatch();
	
	public SnakesOnA2DPlane() {
		setUpDisplay();
		setUpOpenGL();
		setUpEntities();
		setUpTimer();
		
		try {
			out = new PrintStream(new File("consoledump.out"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while (isRunning && !Display.isCloseRequested()) {
			int delta = getDelta();
			render();
			update(delta);
			input();
			Display.update();
			Display.sync(60);
		}
		
		
		
		Display.destroy();
	}

	private void setUpDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH,
					DISPLAY_HEIGHT));
			Display.setFullscreen(true);
			Display.setTitle("Snakes On a 2D Plane");
			Display.setResizable(false);
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	private void setUpOpenGL() {
		// Initialize code OpenGL
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, DISPLAY_WIDTH, DISPLAY_HEIGHT, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_TEXTURE_2D);
	}

	private void setUpEntities() {
		s = new SnakeHead(100, 150, 10, 10, 90);

		obstacles = new ArrayList<Box>();

		agents = new ArrayList<Agent>();
	}

	private long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	private int getDelta() {
		long currentTime = getTime();
		int delta = (int) (currentTime - lastFrame);
		lastFrame = getTime();
		return delta;
	}

	private void setUpTimer() {
		lastFrame = getTime();
	}

	private void render() {
		glClear(GL_COLOR_BUFFER_BIT);
		// Render all objects here
		
		if (isShowGrid)
		{
			for (int i = 0; i < DISPLAY_HEIGHT / GRID_UNIT_DIM; i++)
			{
				for (int j = 0; j < DISPLAY_WIDTH / GRID_UNIT_DIM; j++)
				{
					if (!map[i][j].isWalkable)
						GL11.glColor3f(255, 0, 0);
					else
						GL11.glColor3f(0, 255, 0);
					
					if (map[i][j] == this.startingPoint)
						GL11.glColor3f(0, 0, 255);
					
					if (map[i][j] == this.targetPoint)
						GL11.glColor3f(0, 255, 255);
					
					int lx = j * GRID_UNIT_DIM;
					int rx = (j + 1) * GRID_UNIT_DIM;
					int ty = i * GRID_UNIT_DIM;
					int by = (i + 1) * GRID_UNIT_DIM;
					
					GL11.glBegin(GL11.GL_LINE_LOOP);
					glVertex2f(lx, ty);
					glVertex2f(rx, ty);
					glVertex2f(rx, by);
					glVertex2f(lx, by);
					GL11.glEnd();
				}
			}
		}
		
		if (isPathGenerated) {
			MapNode temp = startingPoint;
			while (temp != targetPoint) {
				GL11.glColor3f(255, 255, 255);
				
				int lx = temp.x * GRID_UNIT_DIM;
				int rx = (temp.x + 1) * GRID_UNIT_DIM;
				int ty = temp.y * GRID_UNIT_DIM;
				int by = (temp.y + 1) * GRID_UNIT_DIM;
				
				GL11.glBegin(GL11.GL_QUADS);
				glVertex2f(lx, ty);
				glVertex2f(rx, ty);
				glVertex2f(rx, by);
				glVertex2f(lx, by);
				GL11.glEnd();
				
				temp = temp.finalChild;
			}
		}
		
		s.render();
		for (Box box : obstacles) {
			box.render();
		}

		for (Agent agent : agents) {
			agent.render();
		}
	}

	private void update(int delta) {
		// Call update function for each object here
		s.update(delta);
		
		for (Box box : obstacles) {
			box.update(delta);
		}
		
		for (Agent a : agents) 
		{
			if (s.isEatCookie(a))
			{
				if (a.getType() == AgentType.ADD_ONE_PART) {
					s.numParts++;
					s.addPart();
					System.out.println("You ate a cookie! Now your length is: " + s.numParts + " parts.");
				}
				else if (a.getType() == AgentType.ADD_TWO_PARTS) {
					s.numParts += 2;
					s.addPart();
					System.out.println("You ate a cookie! Now your length is: " + s.numParts + " parts.");
				}
				else if (a.getType() == AgentType.GO_FASTER){
					s.speedUp();
					System.out.println("GO GO GO !");
				} else if (a.getType() == AgentType.GO_SLOWER) {
					s.slowDown();
					System.out.println("Slow down tiger...");
				}
				
				agents.remove(a);
				
				//Break out of loop to avoid co-modification
				break;
			}
		}
		
		if(isSearching)
		{
			boolean doneWithAStar = false;
			
			while(!doneWithAStar)
				doneWithAStar = performAStar();
			
		}
		
		SnakeBody b = s.checkForSelfCollision();
		if (b != null)
		{
			System.out.println("CRASH!");
			s.removePart(b);
		}
	}
	
	public boolean performAStar() {
		if (isSearching) {
			if (openList.size() == 0)
			{
				sw.start();
				openList.add(startingPoint);
			}
			
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
			
			//Assign adjacent nodes
			out.println(curNode.toString());
			if (curNode != targetPoint)
			{
				
				for (MapNode m : getAdjacentNodes(curNode))
				{
					
					out.println("\t" + m.toString());
					
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
				sw.stop();
				System.out.println("Elapsed map node: " + sw.getElapsedTime());
				return true;
			}
			
		}
		return false;
	}

	public void generatePath() {
		MapNode curNode = targetPoint;
		System.out.println("Generating Path");
		System.out.println("Start: " + startingPoint.toString());
		System.out.println("End: " + targetPoint.toString());
		while (curNode != startingPoint)
		{
			System.out.println(curNode.toString());
			curNode.parent.finalChild = curNode;
			curNode = curNode.parent;
		}		
		
		isPathGenerated = true;
		System.out.println("Path Generated.");
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
		int yUB = DISPLAY_HEIGHT / GRID_UNIT_DIM; //y upper bound
		int xUB = DISPLAY_WIDTH / GRID_UNIT_DIM; //y lower bound
		
		if (y >= 0 && y < yUB && x >= 0 && x < xUB && map[y][x] != null)
			return true;
		
		return false;
	}

	private void input() {
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			s.moveUp(0, DISPLAY_WIDTH, 0, DISPLAY_HEIGHT);
			for (Box box : obstacles)
				if (s.intersects(box))
					s.moveDown(0, DISPLAY_WIDTH, 0, DISPLAY_HEIGHT);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			s.moveDown(0, DISPLAY_WIDTH, 0, DISPLAY_HEIGHT);
			for (Box box : obstacles)
				if (s.intersects(box))
					s.moveUp(0, DISPLAY_WIDTH, 0, DISPLAY_HEIGHT);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			s.decreaseHeading();
		else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			s.increaseHeading();

		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				switch (Keyboard.getEventKey()) {
				case Keyboard.KEY_A:
					//A*
					if (!isSearching)
					{
						Agent a = new Agent(Mouse.getX(), DISPLAY_HEIGHT
								- Mouse.getY() - 1);
						agents.add(a);
						openList = new ArrayList<MapNode>();
						closedList = new ArrayList<MapNode>();
						aStarTarget = a;
						gridify();
						isShowGrid = true;
						isSearching = true;
						isPathGenerated = false;
						out.println("StartingPoint: " + startingPoint.toString() + "\nTargetPoint: " + targetPoint.toString());
					}
					
					break;
				case Keyboard.KEY_C:
					//New Agent at Mouse Position
					Agent c = new Agent(Mouse.getX(), DISPLAY_HEIGHT
							- Mouse.getY() - 1);
					agents.add(c);
					break;
				case Keyboard.KEY_F:
					if (isPathGenerated)
						s.followPathToTarget(startingPoint, aStarTarget.getX(), aStarTarget.getY());
					break;
				case Keyboard.KEY_G:
					isPathGenerated = false;
					isSearching = false;
					isShowGrid = false;
					break;
				case Keyboard.KEY_H:
					//Toggle Heading
					s.toggleHeading();
					break;
				case Keyboard.KEY_O:
					//Drop Obstacle
					Box b = new Box(r.nextInt(DISPLAY_WIDTH),
							r.nextInt(DISPLAY_HEIGHT), r.nextInt(100) + 50,
							r.nextInt(100) + 50);
					obstacles.add(b);
					break;
				case Keyboard.KEY_P:
					//Pie Slices
					s.togglePieSlices();
					break;
				case Keyboard.KEY_R:
					//Radar
					s.toggleRadar();
					break;
				case Keyboard.KEY_S:
					//Seek
					Agent k = new Agent(Mouse.getX(), DISPLAY_HEIGHT
							- Mouse.getY() - 1);
					agents.add(k);
					s.seek(k.getX(), k.getY());
					break;
				case Keyboard.KEY_W:
					//Wall Feelers
					if (s.isWallSensorActivated()) {
						s.turnOffWallSensors();
					} else {
						s.turnOnWallSensors();

						for (Box box : obstacles)
							s.feelForWalls(box);
					}
					break;
				}
			}
		}

		for (Box box : obstacles) {
			if (Mouse.isButtonDown(0)
					&& box.inBounds(Mouse.getX(), DISPLAY_HEIGHT - Mouse.getY()
							- 1) && !isSomethingSelected) {
				isSomethingSelected = true;
				box.selected = true;
			}

			if (Mouse.isButtonDown(1)) {
				box.selected = false;
				isSomethingSelected = false;
			}

			if (box.selected) {
				box.setLocation(
						Mouse.getX() - .5 * box.getWidth(),
						DISPLAY_HEIGHT - Mouse.getY() - 1 - .5
								* box.getHeight());
			}
		}

		for (Agent agent : agents) {
			s.seesAgent(agent);
			s.adjacencySense(agent);
		}
	}

	public void gridify() {
		// Split the map into a grid of size numHoriz * numVert
		int numVert = DISPLAY_HEIGHT / GRID_UNIT_DIM;
		int numHoriz = DISPLAY_WIDTH / GRID_UNIT_DIM;
		
		int xStartPos, yStartPos;
		int xEndPos, yEndPos;

		// Create an array to store the values of each square
		// NOTE: It is an integer array where value = 0 means it is not
		// walkable, value = 1 means it is walkable
		map = new MapNode[numVert][numHoriz];

		for (int i = 0; i < numVert; i++) {
			for (int j = 0; j < numHoriz; j++) {
				map[i][j] = new MapNode(j, i);
				
				//Check if there is a wall in this square. If there is, mark it not walkable
				for (Box b : obstacles)
				{
					//The position of this box is:
					xStartPos = GRID_UNIT_DIM * j;
					xEndPos = xStartPos + GRID_UNIT_DIM;
					yStartPos = GRID_UNIT_DIM * i;
					yEndPos = yStartPos + GRID_UNIT_DIM;
					
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
		
		this.startingPoint = findClosestNode(s);
		if (aStarTarget != null)
			this.targetPoint = findClosestNode(aStarTarget);
	}
	
	public MapNode findClosestNode(AbstractEntity a) {
		int xPos = ((int)a.getX() / GRID_UNIT_DIM) % (DISPLAY_WIDTH/GRID_UNIT_DIM);
		int yPos = ((int)a.getY() / GRID_UNIT_DIM) % (DISPLAY_HEIGHT/GRID_UNIT_DIM);
		return map[yPos][xPos];
	}

	public boolean between(double x, int a, int b) {
		return (x <= b && x >= a);
	}
	
	public static void main(String[] args) {
		new SnakesOnA2DPlane();
	}

}
