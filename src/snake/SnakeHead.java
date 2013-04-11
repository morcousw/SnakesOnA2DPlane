package snake;

import static org.lwjgl.opengl.GL11.*;
import java.util.Random;
import java.util.ArrayList;
import map.MapGrid;
import map.MapNode;
import physics.Vector;
import entities.AbstractMovableEntity;
import entities.Agent;
import entities.Box;
import entities.Entity;
import entities.Agent.AgentType;

import snakesOnA2DPlane.SnakesOnA2DPlane;

public class SnakeHead extends AbstractMovableEntity {

	private double range;
	private double wallSensorRadius = 0;

	private ArrayList<Vector> wallsInRange = new ArrayList<Vector>();
	private ArrayList<SnakeBody> bodyParts = new ArrayList<SnakeBody>();
	private double seekTargetX;
	private double seekTargetY;
	public boolean isSeeking = false;
	public Agent curTarget;
	public PathFinder pf;
	//Path Following Variables
	private boolean isFollowingPath = false;
	public boolean isPathFinding = false;
	public MapGrid map; 
	private MapNode curNode;
	
	private float randr, randg, randb;
	
	public int numParts = 1;
	
	public SnakeHead(double x, double y, double width, double height, double heading) {
		super(x, y, width, height, heading);
		range = width*2;
		map = new MapGrid();
		
		Random r = new Random();
		randr = r.nextFloat();
		randg = r.nextFloat();
		randb = r.nextFloat();
	}

	public void update(int delta) {
		adjustParts();
		for (SnakeBody s : bodyParts)
		{
			s.update(delta);
			for (Box o : SnakesOnA2DPlane.obstacles)
				if (s.intersects(o))
					System.out.println("INTERSECT");
			
		}
		
		if (isSeeking)
		{
			Vector desiredPos = new Vector(seekTargetX - this.x, seekTargetY - this.y);
			this.x += (desiredPos.getX() / desiredPos.getMagnitude()) * this.dx;
			this.y += (desiredPos.getY() / desiredPos.getMagnitude()) * this.dy;	
			
			//TODO: change the heading so that it matches the desiredPos
			//System.out.println("X: " + this.x + " " + seekTargetX);
			//System.out.println("Y: " + this.y + " " + seekTargetY);
			if (Math.abs(this.x - seekTargetX) < this.dx && Math.abs(this.y - seekTargetY) < this.dy)
			{
				isSeeking = false;
				isPathFinding = false;
			}
			
		} else if (isFollowingPath) {
			Vector desiredPos = new Vector(curNode.getXCoord() - this.x, curNode.getYCoord() - this.y);
			this.x += (desiredPos.getX() / desiredPos.getMagnitude()) * this.dx;
			this.y += (desiredPos.getY() / desiredPos.getMagnitude()) * this.dy;	
			
			//TODO: change the heading so that it matches the desiredPos
			//System.out.println("X: " + this.x + " " + curNode.getXCoord());
			//System.out.println("Y: " + this.y + " " + curNode.getYCoord());
			if (Math.abs(this.x - curNode.getXCoord()) < this.dx && Math.abs(this.y - curNode.getYCoord()) < this.dy)
			{
				if (curNode.finalChild == null)
				{
					//We are at the target node. Seek to it so it's exact..
					isFollowingPath = false;
					isSeeking = true;
				}
				else
					curNode = curNode.finalChild;
			}
		} else {
			super.update(delta);
		}
	}

	public void render() {
		map.render();
		if (pf != null)
			pf.render();
		for (SnakeBody s : bodyParts)
			s.render();
		
		glColor3f(randr, randg, randb);
		glBegin(GL_TRIANGLE_FAN);
		{
			glVertex2d(x, y);
			for (int j = 0; j <= 360; j++) {
				glVertex2f((float) (x + Math.sin(j) * width),
						(float) (y + Math.cos(j) * height));
			}
			glEnd();
		}
		glEnd();

		if (printHeading) {
			glBegin(GL_LINES);
			glVertex2d(x, y);
			glVertex2f((float) (x + Math.cos(Math.toRadians(heading))
					* (width + range)),
					(float) (y - Math.sin(Math.toRadians(heading))
							* (height + range)));
			glEnd();
		}

		if (printRadar) {
			int i;
			int lineAmount = 100; // # of triangles used to draw circle
			// GLfloat radius = 0.8f; //radius
			float twicePi = (float) (2.0f * Math.PI);
			glBegin(GL_LINE_LOOP);
			for (i = 0; i <= lineAmount; i++) {
				glVertex2f(
						(float) (x + ((width + range) * Math.cos(i * twicePi
								/ lineAmount))),
						(float) (y + ((height + range) * Math.sin(i * twicePi
								/ lineAmount))));
			}
			glEnd();
		}

		if (printPieSlices) {
			glColor3f(.2f, .9f, .2f);
			glLineWidth(1);
			glBegin(GL_LINES);
			glVertex2f((float) (x - Math.cos(Math.toRadians(heading - 45))
					* (width + range)),
					(float) (y + Math.sin(Math.toRadians(heading - 45))
							* (height + range)));
			glVertex2f((float) (x + Math.cos(Math.toRadians(heading - 45))
					* (width + range)),
					(float) (y - Math.sin(Math.toRadians(heading - 45))
							* (height + range)));
			glEnd();
			glLineWidth(1);
			glBegin(GL_LINES);
			glVertex2f((float) (x - Math.cos(Math.toRadians(heading + 45))
					* (width + range)),
					(float) (y + Math.sin(Math.toRadians(heading + 45))
							* (height + range)));
			glVertex2f((float) (x + Math.cos(Math.toRadians(heading + 45))
					* (width + range)),
					(float) (y - Math.sin(Math.toRadians(heading + 45))
							* (height + range)));
			glEnd();
		}
		
		if (printWallSensors) {
			for (Vector v : wallsInRange)
			{
				glColor3f(1f, 0f, 1f);
				glBegin(GL_LINES);
				glVertex2d(x, y);
				glVertex2d(v.getX(), v.getY());
				glEnd();
			}
		}
	}

	public boolean seesAgent(Entity agent) {
		if (Math.hypot(agent.getX() - x, agent.getY() - y) < range + width) {
			// System.out.println(Math.hypot(box.x-userx, box.y-usery));
			double relDegree = 0;
			if (agent.getX() - x > 0 && agent.getY() - y < 0)
				relDegree = (-Math.toDegrees(Math.asin((agent.getY() - y)
						/ Math.hypot(agent.getX() - x, agent.getY() - y))));
			else if (agent.getX() - x < 0 && agent.getY() - y < 0)
				relDegree = (90 + (90 - (-Math.toDegrees(Math.asin((agent
						.getY() - y)
						/ Math.hypot(agent.getX() - x, agent.getY() - y))))));
			else if (agent.getX() - x < 0 && agent.getY() - y > 0)
				relDegree = (180 + (Math.toDegrees(Math.asin((agent.getY() - y)
						/ Math.hypot(agent.getX() - x, agent.getY() - y)))));
			else if (agent.getX() - x > 0 && agent.getY() - y > 0)
				relDegree = (270 + (90 - (Math.toDegrees(Math.asin((agent
						.getY() - y)
						/ Math.hypot(agent.getX() - x, agent.getY() - y))))));

			if ((relDegree <= heading + 45 && relDegree >= heading - 45)
					|| (relDegree >= 315 && heading <= 45 - (360 - relDegree))) {
				if (printPieSlices)
					System.out.println("Activated " + String.format("%.2f",((heading + 720) % 360))
							+ " " + relDegree);
				return true;
			}
		}
		return false;
	}

	public boolean adjacencySense(Entity agent) {
		double distance = Math.hypot(agent.getX() - x, y - agent.getY())
				- width;
		if (distance <= range) {
			if (printRadar)
				System.out.println("There is an agent " + distance
						+ " units away from you.");
			return true;
		}
		return false;
	}

	public void feelForWalls(Entity other) {
		wallSensorRadius = width;
		double coordsX, coordsY, a;
		while (true) {
			if (wallSensorRadius > SnakesOnA2DPlane.DISPLAY_WIDTH
					&& wallSensorRadius > SnakesOnA2DPlane.DISPLAY_HEIGHT)
				return;

			// Circle colliding with the right side of the rectangle
			a = Math.sqrt(Math.pow(wallSensorRadius, 2)
					- Math.pow(other.getX() + other.getWidth() - x, 2))
					+ y;
			if (a <= other.getY() + other.getHeight() && a >= other.getY()
					&& x - wallSensorRadius >= other.getX()
					&& x - wallSensorRadius <= other.getX() + other.getWidth()) {
				coordsX = other.getX() + other.getWidth();
				coordsY = a;
				break;
			}

			// Circle colliding with the left side of the rectangle
			a = Math.sqrt(Math.pow(wallSensorRadius, 2)
					- Math.pow(other.getX() - x, 2))
					+ y;
			if (a <= other.getY() + other.getHeight() && a >= other.getY()
					&& x + wallSensorRadius >= other.getX()
					&& x + wallSensorRadius <= other.getX() + other.getWidth()) {
				coordsX = other.getX();
				coordsY = a;
				break;
			}

			// Circle colliding with the bottom side of the rectangle
			a = Math.sqrt(Math.pow(wallSensorRadius, 2)
					- Math.pow(other.getY() + other.getHeight() - y, 2))
					+ x;
			if (a <= other.getX() + other.getWidth() && a >= other.getX()
					&& y - wallSensorRadius >= other.getY()
					&& y - wallSensorRadius <= other.getY() + other.getHeight()) {
				coordsX = a;
				coordsY = other.getY() + other.getHeight();
				break;
			}

			// Circle colliding with the top side of the rectangle
			a = Math.sqrt(Math.pow(wallSensorRadius, 2)
					- Math.pow(other.getY() - y, 2))
					+ x;
			if (a <= other.getX() + other.getWidth() && a >= other.getX()
					&& y + wallSensorRadius >= other.getY()
					&& y + wallSensorRadius <= other.getY() + other.getHeight()) {
				coordsX = a;
				coordsY = other.getY();
				break;
			}

			wallSensorRadius++;
		}

		Vector v = new Vector(coordsX, coordsY);
		wallsInRange.add(v);

		
		double relDegree = 0;
		if (coordsX - x > 0 && coordsY - y < 0)
			relDegree = (- Math.toDegrees(Math.asin((coordsY - y)
					/ Math.hypot(coordsX - x, coordsY - y))));
		else if (coordsX - x < 0 && coordsY - y < 0)
			relDegree = (90 + (90 - (-Math.toDegrees(Math.asin((coordsY - y)
					/ Math.hypot(coordsX - x, coordsY - y))))));
		else if (coordsX - x < 0 && coordsY - y > 0)
			relDegree = (180 + (Math.toDegrees(Math.asin((coordsY - y)
					/ Math.hypot(coordsX - x, coordsY - y)))));
		else if (coordsX - x > 0 && coordsY - y > 0)
			relDegree = (270 + (90 - (Math.toDegrees(Math.asin((coordsY - y)
					/ Math.hypot(coordsX - x, coordsY - y))))));
		
		relDegree = (360 + heading - relDegree) % 360;
		String direction;
		if (relDegree > 180)
		{
			direction = "counter-clockwise";
			relDegree = 360 - relDegree;
		}
		else
			direction = "clockwise";
		relDegree = relDegree > 180 ? 360 - relDegree : relDegree;
		System.out.println("Wall Intersection " + (wallSensorRadius - width - 1) + " units away. At a heading of " + String.format("%.2f", relDegree) + " degrees " + direction + ".");
	}
	
	public void resetWallSensors() {
		wallsInRange.clear();
	}
	
	public void seek(double targetX, double targetY)
	{
		isSeeking = true;
		seekTargetX = targetX;
		seekTargetY = targetY;
	}
	
	public void followPathToTarget(MapNode startingPoint, double targetX, double targetY)
	{
		this.isFollowingPath = true;
		this.curNode = startingPoint;
		seekTargetX = targetX;
		seekTargetY = targetY;
	}
	
	public boolean isEatCookie(Agent agent) {
		if (Vector.distanceFormula(this.x, this.y, agent.getX(), agent.getY()) < agent.getHeight() + this.height)
			return true;
		
		return false;
	}
	
	public void addPart() {
		AbstractMovableEntity a;
		if (bodyParts.isEmpty())
			a = this;
		else
			a = bodyParts.get(bodyParts.size() - 1);
		
		SnakeBody b = new SnakeBody(a.getX(), a.getY(), a.getWidth(), a.getHeight(), a);
		b.toggleHeading();
		bodyParts.add(b);
	}
	
	private void adjustParts() {
		for (SnakeBody s: bodyParts)
		{
			s.setDX(this.getDX());
			s.setDY(this.getDY());
		}
		
	}
	
	public void speedUp() {
		this.setDX(this.getDX() + 2);
		this.setDY(this.getDY() + 2);
		this.setDH(this.getDH() + 2);
		for (SnakeBody s : bodyParts)
		{
			s.setDX(this.getDX());
			s.setDY(this.getDY());
			s.setDH(this.getDH());
		}
	}
	
	public void slowDown() {
		this.setDX(this.getDX() - 2);
		this.setDY(this.getDY() - 2);
		this.setDH(this.getDH() - 2);
		
		for (SnakeBody s : bodyParts)
		{
			s.setDX(this.getDX());
			s.setDY(this.getDY());
			s.setDH(this.getDH());
		}
	}
	
	public SnakeBody checkForSelfCollision() {
		for (SnakeBody s : bodyParts)
		{
			if (Vector.distanceFormula(s.getX(), s.getY(), this.getX(), this.getY()) <= s.getWidth() + this.getWidth() && s.parent != this)
				return s;
		}
		
		return null;
	}
	
	public void removePart(SnakeBody b) {
		int i = bodyParts.size() - 1;
		while (bodyParts.get(i) != b)
		{
			bodyParts.remove(i);
			i--;
		}
		bodyParts.remove(b);
	}
	
	public void performAStar(Agent t) {
		isPathFinding = true;
		curTarget = t;
		map.gridify();
		pf = new PathFinder(this, t);
		pf.performAStar();
		pf.isShowPath = false;
	}

	public SnakeBody checkForCollisionWithSnake(SnakeHead c) {
		for (SnakeBody b : c.bodyParts)
		{
			if (Vector.distanceFormula(b.getX(), b.getY(), this.getX(), this.getY()) <= b.getWidth() + this.getWidth() + 2)
				return b;
		}
		return null;
	}

	public boolean testEatCookie(Agent a) {
		if (isEatCookie(a))
		{
			if (a.getType() == AgentType.ADD_ONE_PART) {
				addPart();
			}
			else if (a.getType() == AgentType.ADD_TWO_PARTS) {
				addPart();
				addPart();
			}
			else if (a.getType() == AgentType.GO_FASTER){
				speedUp();
				System.out.println("GO GO GO !");
			} else if (a.getType() == AgentType.GO_SLOWER) {
				slowDown();
				System.out.println("Slow down tiger...");
			} else if (a.getType() == AgentType.CUT_IN_HALF) {
				
			}
			
			return true;
		} 
		
		return false;
		
	}
}
