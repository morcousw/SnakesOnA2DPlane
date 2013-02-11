package entities;

import physics.Vector;

public interface MovingEntity extends Entity {
	public Vector getVelocity();
	public void setVelocity(Vector v);
	public double getAngle();
	public void setAngle(double angle);
	public void decrementAngle(double angle);
	public void incrementAngle(double angle);
	public void accelerate(double speed);
	public void deccelerate(double speed);
}
