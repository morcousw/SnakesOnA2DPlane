package entities;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Keyboard;

import physics.Vector;

public class Snake extends AbstractMovableEntity {

	private boolean printHeading = true;
	private boolean printRadar = false;
	private double range;
	public Snake(double x, double y, double width, double height, double heading) {
		super(x, y, width, height, heading);
		range = 50;
	}

	public void update(int delta) {
		super.update(delta);
	}
	
	public void render() {
		glColor3f(0.1f, 0.2f, 0.3f);
		glBegin(GL_TRIANGLE_FAN);
		{
            glVertex2d(x,y);
        	for(int j =0; j <= 360; j++){
        		glVertex2f((float)(x + Math.sin(j) * width), (float)(y + Math.cos(j) * height));
        	}
        	glEnd(); 
		}
		glEnd();
		
		if (printHeading) {
			glBegin(GL_LINES);
            glVertex2d(x, y);
            glVertex2f((float)(x + Math.cos(Math.toRadians(heading)) * (width + range)), (float)(y - Math.sin(Math.toRadians(heading)) * (height + range)));
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
						(float) (x + ((width + range) * Math.cos(i * twicePi / lineAmount))),
						(float) (y + ((height + range) * Math.sin(i * twicePi / lineAmount))));
			}
			glEnd();
		}
		
		glColor3f(1.0f, 0f, 0f);
		glBegin(GL_POINTS);
		glVertex2d(x, y);
		glEnd();
	}
	
	public void toggleHeading() {
		printHeading = !printHeading;
	}
	
	public void toggleRadar() {
		printRadar = !printRadar;
	}
	
	public boolean intersects(Entity other){
		double a;
		//Circle colliding with the right side of the rectangle
		a = Math.sqrt(Math.pow(width, 2) - Math.pow(other.getX() + other.getWidth() - x, 2)) + y;
		if (a <= other.getY() + other.getHeight() && a >= other.getY() && x - width >= other.getX() && x - width <= other.getX() + other.getWidth())
			return true;
		
		//Circle colliding with the left side of the rectangle
		a = Math.sqrt(Math.pow(width, 2) - Math.pow(other.getX() - x, 2)) + y;
		if (a <= other.getY() + other.getHeight() && a >= other.getY() && x + width >= other.getX() && x + width <= other.getX() + other.getWidth())
			return true;
		
		//Circle colliding with the bottom side of the rectangle
		a = Math.sqrt(Math.pow(height, 2) - Math.pow(other.getY() + other.getHeight() - y, 2)) + x;
		if (a <= other.getX() + other.getWidth() && a >= other.getX() && y - height >= other.getY() && y - height <= other.getY() + other.getHeight())
			return true;
		
		//Circle colliding with the top side of the rectangle
		a = Math.sqrt(Math.pow(height, 2) - Math.pow(other.getY() - y, 2)) + x;
		if (a <= other.getX() + other.getWidth() && a >= other.getX() && y + height >= other.getY() && y + height <= other.getY() + other.getHeight())
			return true;
		
		//If the center of the circle is within the bounds of the rectangle
		if (x > other.getX() && x < other.getX() + other.getWidth() && y > other.getY() && y < other.getY() + other.getHeight())
			return true;
		
		return false;
	}
}