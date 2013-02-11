package entities;

public interface Entity {
	public void render();
	public void update(int delta);
	public void setLocation(double x, double y);
	public void setX(double x);
	public void setY(double y);
	public void setWidth(double width);
	public void setHeight(double height);
	public double getX();
	public double getY();
	public double getHeight();
	public double getWidth();
	public boolean intersects(Entity other);
}
