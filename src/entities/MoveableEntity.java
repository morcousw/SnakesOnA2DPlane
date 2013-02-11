package entities;

public interface MoveableEntity extends Entity {
	public double getDX();
	public double getDY();
	public double getHeading();
	public void setDX(double dx);
	public void setDY(double dy);
	public void setHeading(double heading);
	public void increaseHeading();
	public void decreaseHeading();
	public void moveUp(double minX, double maxX, double minY, double maxY);
	public void moveDown(double minX, double maxX, double minY, double maxY);
}
