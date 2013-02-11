import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.*;
import org.lwjgl.*;

import physics.Vector;

import entities.Snake;

import static org.lwjgl.opengl.GL11.*;

public class SnakesOnA2DPlane {
	public static final int DISPLAY_HEIGHT = 480;
	public static final int DISPLAY_WIDTH = 640;
	
	//Timing Variables
	private long lastFrame;
	private boolean isRunning = true;
	
	Snake s;
	
	
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
			Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT));
			Display.setTitle("Snakes On a 2D Plane");
			Display.setResizable(true);
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	private void setUpOpenGL() {
		//Initialize code OpenGL
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, DISPLAY_WIDTH, DISPLAY_HEIGHT, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_TEXTURE_2D);
	}
	
	private void setUpEntities() {
		s = new Snake(15, 15, 30, 30, 0);
	}
	
	private long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	private int getDelta() { 
		long currentTime = getTime();
		int delta = (int)(currentTime - lastFrame);
		lastFrame = getTime();
		return delta;
	}
	
	private void setUpTimer() {
		lastFrame = getTime();
	}
	
	private void render() {
		glClear(GL_COLOR_BUFFER_BIT);
		//Render all objects here
		s.render();
	}
	
	private void update(int delta) {
		//Call update function for each object here
		s.update(delta);
	}
	
	private void input() {
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			s.decreaseHeading(1);
		else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			s.increaseHeading(1);
		
		if (Keyboard.isKeyDown(Keyboard.KEY_UP))
			s.moveUp();
		else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			s.moveDown();
	}
	
	public static void main(String[] args) {
		new SnakesOnA2DPlane();
	}

}
