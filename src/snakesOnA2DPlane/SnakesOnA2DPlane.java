package snakesOnA2DPlane;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.lwjgl.*;

import physics.Vector;

import entities.Agent;
import entities.Box;
import entities.Snake;

import static org.lwjgl.opengl.GL11.*;

public class SnakesOnA2DPlane {
	public static final int DISPLAY_HEIGHT = 480;
	public static final int DISPLAY_WIDTH = 640;

	// Timing Variables
	private long lastFrame;
	private boolean isRunning = true;

	Random r = new Random();

	Snake s;
	ArrayList<Box> obstacles;
	ArrayList<Agent> agents;

	private boolean isSomethingSelected = false;

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
			Display.setTitle("Snakes On a 2D Plane");
			Display.setResizable(true);
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
		s = new Snake(100, 150, 20, 20, 90);

		obstacles = new ArrayList<Box>();

		Box a = new Box(200, 200, 100, 200);
		obstacles.add(a);

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
				case Keyboard.KEY_O:
					Box b = new Box(r.nextInt(DISPLAY_WIDTH),
							r.nextInt(DISPLAY_HEIGHT), r.nextInt(100) + 50,
							r.nextInt(100) + 50);
					obstacles.add(b);
					break;
				case Keyboard.KEY_C:
					Agent a = new Agent(Mouse.getX(), DISPLAY_HEIGHT
							- Mouse.getY() - 1);
					agents.add(a);
					break;
				case Keyboard.KEY_H:
					s.toggleHeading();
					break;
				case Keyboard.KEY_P:
					s.togglePieSlices();
					break;
				case Keyboard.KEY_R:
					s.toggleRadar();
					break;
				case Keyboard.KEY_W:
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

	public static void main(String[] args) {
		new SnakesOnA2DPlane();
	}

}
