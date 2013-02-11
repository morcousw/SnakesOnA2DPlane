package entities;

import static org.lwjgl.opengl.GL11.*;

import physics.Vector;

public class Snake extends AbstractMovableEntity {

	public Snake(double x, double y, double width, double height, double heading) {
		super(x, y, width, height, heading);
	}

	public void update(int delta) {
		super.update(delta);
	}
	
	public void render() {
		glBegin(GL_QUADS);
		{
			glVertex2i((int)x, (int)y);
			glVertex2i((int)(x + width), (int)y);
			glVertex2i((int)(x + width), (int)(y + height));
			glVertex2i((int)x, (int)(y + height));
		}
		glEnd();
	}
}
