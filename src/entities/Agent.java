package entities;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static org.lwjgl.opengl.GL11.glVertex2f;

public class Agent extends AbstractEntity {

	public Agent(double x, double y) {
		super(x, y, 15, 15);
	}

	public void render() {
        glColor3f(0.2f, 0.3f, 0.4f);
        glBegin(GL_TRIANGLE_FAN);
        glVertex2d(x,y);
    	for(int j =0; j <= 360; j++){
    		glVertex2f((float)(x + Math.sin(j) * 5), (float)(y + Math.cos(j) * 5));
    	}
    	glEnd(); 
	}

	public void update(int delta) {
		
	}


}
