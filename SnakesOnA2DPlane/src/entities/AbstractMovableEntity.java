package entities;

public abstract class AbstractMovableEntity extends AbstractEntity implements MoveableEntity {

	protected double dx, dy;
	protected double heading;
	
	public AbstractMovableEntity(double x, double y, double width, double height) {
		this(x, y, width, height, 90);
	}
	
	public AbstractMovableEntity(double x, double y, double width, double height, double heading) {
		super(x, y, width, height);
		this.dx = 1;
		this.dy = 1;
		this.heading = heading;
	}

	public void update(int delta) {

	}

	public double getDX() {
		return dx;
	}

	public double getDY() {
		return dy;
	}
	
	public double getHeading() {
		return heading;
	}

	public void setDX(double dx) {
		this.dx = dx;
	}

	public void setDY(double dy) {
		this.dy = dy;
	}
	
	public void setHeading(double heading) {
		this.heading = heading;
	}
	
	public void increaseHeading(double heading) {
		this.heading += heading;
	}
	
	public void decreaseHeading(double heading) {
		this.heading -= heading;
	}
	
	public void moveUp() {
		this.x += Math.cos(Math.toRadians(heading)) * dx;
		this.y -= Math.sin(Math.toRadians(heading)) * dy;
	}
	
	public void moveDown() {
		this.x += Math.cos(Math.toRadians(heading)) * dx;
		this.y += Math.sin(Math.toRadians(heading)) * dy;
	}
}
