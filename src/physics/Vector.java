package physics;

public class Vector {
	private double x, y;
	public Vector(double xVel, double yVel) {
		this.x= xVel;
		this.y = yVel;
	}
	
	public void setX(float x)
	{
		this.x = x;
	}
	
	public void setY(float y)
	{
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public float getMagnitude() {
		return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	
	public float getHeading() {
		return (float) Math.toDegrees(Math.atan(y/x));
	}
	
	public double relativeHeading(Vector v) {
		return Math.toDegrees(Math.acos((x * v.x + y * v.y)/(getMagnitude() * v.getMagnitude())));
	}
}
