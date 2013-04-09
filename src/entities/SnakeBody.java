package entities;
import static org.lwjgl.opengl.GL11.*;
import physics.Vector;

public class SnakeBody extends AbstractMovableEntity{
	
	AbstractMovableEntity parent;
	
	public SnakeBody(double x, double y, double width, double height, AbstractMovableEntity parent) {
		super(x, y, width, height);
		this.parent = parent;
	}

	public void render() {
		glColor3f(0.1f, 0.2f, 0.9f);
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
					* (width + 20)),
					(float) (y - Math.sin(Math.toRadians(heading))
							* (height + 20)));
			glEnd();
		}
	}
	
	public void update(int delta) {
		Vector parentToMe = new Vector(this.parent.getX() - this.x, this.parent.getY() - this.y);
		Vector heading = new Vector(parentToMe.getX()/parentToMe.getMagnitude(), parentToMe.getY()/parentToMe.getMagnitude());
		this.heading = heading.getHeading();
		if (Vector.distanceFormula(this.x, this.y, this.parent.getX(), this.parent.getY()) >= this.getWidth() + this.parent.getWidth())
		{
			this.x += heading.getX() * this.dx;
			this.y += heading.getY() * this.dy;
		}
	}
}
