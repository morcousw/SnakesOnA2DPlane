import static org.lwjgl.opengl.GL11.*;

import java.util.Random;

import entities.AbstractEntity;
import entities.AbstractMovableEntity;


public class Box extends AbstractMovableEntity {
	
	private float colorRed, colorGreen, colorBlue;
	public boolean selected;
	
	public Box(double x, double y, double width, double height) {
		super(x, y, width, height);
		
		Random r = new Random();
		colorRed = r.nextFloat();
		colorGreen = r.nextFloat();
		colorBlue = r.nextFloat();
		
		selected = false;
	}

	public void render() {
		glColor3f(colorRed, colorGreen, colorBlue);
		
		//Renders a quadrilateral
		glBegin(GL_QUADS);
			glVertex2i((int)x, (int)y);
			glVertex2i((int)(x + width), (int)y);
			glVertex2i((int)(x + width), (int)(y + height));
			glVertex2i((int)x, (int)(y + height));
		glEnd();
	}
	
	public void update() {
		if (selected)
		{
			System.out.println("I'm selected");
		}
	}
	
	public boolean inBounds(int mouseX, int mouseY) {
		if (mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height)
			return true;
		return false;
	}

	public void changeColor() {
		Random r = new Random();
		colorRed = r.nextFloat();
		colorBlue = r.nextFloat();
		colorGreen = r.nextFloat();
	}
}
