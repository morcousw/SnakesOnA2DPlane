package entities;

import physics.Vector;


public abstract class AbstractMovingEntity extends AbstractEntity implements MovingEntity {

	public AbstractMovingEntity(double x, double y, double width, double height) {
		this(x, y, width, height, 90, new Vector(1, 1));
	}

	public AbstractMovingEntity(double x, double y, double width, double height, double xVel, double yVel)
	{
		this (x, y, width, height, 90, new Vector(xVel, yVel));
	}
	
	public AbstractMovingEntity(double x, double y, double width, double height, double angle, Vector velocity)
	{
		super(x, y, width, height);
		this.angle = angle;
		this.velocity = velocity;
	}
	private Vector velocity;
	private double angle;

	public void update(int delta) {
		x += Math.cos(Math.toRadians(angle)) * velocity.getMagnitude() * delta;
		y -= Math.sin(Math.toRadians(angle)) * velocity.getMagnitude() * delta;
	}

	public Vector getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector v) {

	}
	
	public double getAngle() {
		return angle;
	}
	
	public void setAngle(double angle) {
		this.angle = angle;
	}
	
	public void decrementAngle(double angle) {
		this.angle -= angle;
	}
	
	public void incrementAngle(double angle) {
		this.angle += angle;
	}
	
	public void accelerate(double speed) {
		velocity = new Vector((velocity.getMagnitude() + speed)*Math.cos(Math.toRadians(angle)), (velocity.getMagnitude() + speed)*Math.sin(Math.toRadians(angle)));
	}
	
	public void deccelerate(double speed) {
		velocity = new Vector((velocity.getMagnitude() - speed)*Math.cos(Math.toRadians(angle)), (velocity.getMagnitude() - speed)*Math.sin(Math.toRadians(angle)));
	}
}
