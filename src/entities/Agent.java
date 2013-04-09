package entities;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.Random;

public class Agent extends AbstractEntity {
	
	public enum AgentType {
		GO_FASTER, GO_SLOWER, ADD_ONE_PART, ADD_TWO_PARTS
	};
	
	
	private AgentType type;

	public Agent(double x, double y) {
		super(x, y, 15, 15);

		Random generator = new Random();
		int ix = generator.nextInt(AgentType.values().length);
		this.type = AgentType.values()[ix];
	}

	public void render() {
		switch (type)
		{
			case ADD_ONE_PART:
				glColor3f(0f, 0f, 1f);
				break;
			case GO_FASTER:
				glColor3f(0f, 1.0f, 0f);
				break;
			case ADD_TWO_PARTS:
				glColor3f(0f, 1f, 1.0f);
				break;
			case GO_SLOWER:
				glColor3f(1.0f, 0f, 0f);
		}
		
        glBegin(GL_TRIANGLE_FAN);
        glVertex2d(x,y);
    	for(int j =0; j <= 360; j++){
    		glVertex2f((float)(x + Math.sin(j) * 5), (float)(y + Math.cos(j) * 5));
    	}
    	glEnd(); 
	}

	public void update(int delta) {

	}

	public AgentType getType() {
		return type;
	}
}
