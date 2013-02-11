package entities;

public interface MoveableEntity extends Entity {
	public double getDX();
	public double getDY();
	public double getHeading();
	public void setDX(double dx);
	public void setDY(double dy);
	public void setHeading(double heading);
	public void increaseHeading(double heading);
	public void decreaseHeading(double heading);
	public void moveUp();
	public void moveDown();
}
