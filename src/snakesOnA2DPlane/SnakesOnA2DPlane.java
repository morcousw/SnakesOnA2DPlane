package snakesOnA2DPlane;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.lwjgl.*;

import physics.StopWatch;
import snake.SnakeBody;
import snake.SnakeHead;

import entities.Agent;
import entities.Box;
import entities.Agent.AgentType;

import static org.lwjgl.opengl.GL11.*;

public class SnakesOnA2DPlane {
	public static final int DISPLAY_HEIGHT = 760;
	public static final int DISPLAY_WIDTH = 1420;

	public static final int GRID_UNIT_DIM = 30;

	// Timing Variables
	private long lastFrame;
	private boolean isRunning = true;

	Random r = new Random();

	SnakeHead s;
	ArrayList<SnakeHead> opponentSnakes = new ArrayList<SnakeHead>();
	public static ArrayList<Box> obstacles;
	ArrayList<Agent> agents;

	private boolean isSomethingSelected = false;

	StopWatch sw = new StopWatch();

	public SnakesOnA2DPlane() {
		setUpDisplay();
		setUpOpenGL();
		setUpEntities();
		setUpTimer();

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
		opponentSnakes.add(s);

		SnakeHead c = new SnakeHead(DISPLAY_WIDTH - 100, 150, 10, 10, 90);
		opponentSnakes.add(c);

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

		for (SnakeHead c : opponentSnakes)
			if (c.pf != null)
				c.pf.render();

		for (SnakeHead c : opponentSnakes)
			c.render();

		for (Box box : obstacles) {
			box.render();
		}

		for (Agent agent : agents) {
			agent.render();
		}
	}

	private void update(int delta) {
		// Call update function for each object here
		for (SnakeHead c : opponentSnakes)
			c.update(delta);

		for (Box box : obstacles) {
			box.update(delta);
		}

		for (Agent a : agents) {
			for (SnakeHead c : opponentSnakes) {
				if (c.testEatCookie(a)) {
					
					if (a.getType() == AgentType.DROP_OBSTACLE)
					{
						this.addRandomObstacle();
					}
					
					
					agents.remove(a);
					addRandomCookie();
					
					for (SnakeHead d : opponentSnakes)
					{	
						d.isSeeking = false;
						if (d.isPathFinding)
							d.performAStar(agents.get(agents.size() - 1));
					}

					// Break out of loop to avoid co-modification
					break;
				}
			}
		}

		SnakeBody b;

		for (SnakeHead c : opponentSnakes) {
			b = c.checkForSelfCollision();
			if (b != null)
				c.removePart(b);

			for (SnakeHead d : opponentSnakes) {
				if (!c.equals(d)) {
					b = c.checkForCollisionWithSnake(d);
					if (b != null)
						d.removePart(b);
				}
			}
		}
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
				case Keyboard.KEY_F:
					if (agents.isEmpty())
						agents.add(new Agent(Mouse.getX(), DISPLAY_HEIGHT
								- Mouse.getY() + 1));

					for (SnakeHead c : opponentSnakes)
						c.performAStar(agents.get(agents.size() - 1));

					break;
				case Keyboard.KEY_C:
					// New Agent at Mouse Position
					addRandomCookie();
					break;
				/*
				 * case Keyboard.KEY_H: //Toggle Heading s.toggleHeading();
				 * break;
				 */
				case Keyboard.KEY_O:
					// Drop Obstacle
					addRandomObstacle();
					break;
				case Keyboard.KEY_P:
					for (SnakeHead c : opponentSnakes)
						c.pf.isShowPath = true;

					break;
				case Keyboard.KEY_S:
					SnakeHead c = new SnakeHead(100, 100, 10, 10, 90);
					opponentSnakes.add(c);
					break;
					
				/* Pie Slices
				 * case Keyboard.KEY_P: //Pie Slices s.togglePieSlices(); break;
				 * 
				 * Toggle Radar
				 * case Keyboard.KEY_R: //Radar s.toggleRadar(); break; 
				 * 
				 * Seek
				 * case Keyboard.KEY_S: //Seek Agent k = new Agent(Mouse.getX(), DISPLAY_HEIGHT - Mouse.getY() - 1); agents.add(k); s.seek(k.getX(), k.getY()); break; 
				 * 
				 * Wall Feelers
				 * case Keyboard.KEY_W: if (s.isWallSensorActivated()) { s.turnOffWallSensors(); } else { s.turnOnWallSensors(); for (Box box : obstacles) { s.feelForWalls(box); } } break;
				 */
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

		/*
		 * for (Agent agent : agents) { s.seesAgent(agent);
		 * s.adjacencySense(agent); }
		 */
	}

	private void addRandomCookie() {
		// Need to ensure that the cookie added does not intersect any of the
		// obstacles
		Agent a = new Agent(r.nextDouble() * SnakesOnA2DPlane.DISPLAY_WIDTH, r.nextDouble()
				* SnakesOnA2DPlane.DISPLAY_HEIGHT);
		for (Box b : obstacles) {
			if (a.intersects(b)) {
				addRandomCookie();
				return;
			}
		}

		agents.add(a);
	}

	public void addRandomObstacle() {
		Box b = new Box(r.nextInt(DISPLAY_WIDTH), r.nextInt(DISPLAY_HEIGHT),
				r.nextInt(100) + 50, r.nextInt(100) + 50);

		for (Agent a : agents) {
			if (a.intersects(b)) {
				addRandomObstacle();
				return;
			}
		}

		obstacles.add(b);

		for (SnakeHead c : opponentSnakes) {
			c.map.gridify();
			c.map.isShowGrid = false;
			if (c.isPathFinding)
				c.performAStar(c.curTarget);
		}
	}

	public static void main(String[] args) {
		new SnakesOnA2DPlane();
	}

}
