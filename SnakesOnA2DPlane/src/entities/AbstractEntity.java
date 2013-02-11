package entities;

import java.awt.Rectangle;

public abstract class AbstractEntity implements Entity {
	
	protected double x, y, width, height;
	protected Rectangle hitbox = new Rectangle();
	
	public AbstractEntity(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getX() {
		return x;
	}


	public double getY() {
		return y;
	}

	public double getHeight() {
		return height;
	}

	public double getWidth() {
		return width;
	}

	public boolean intersects(Entity other) {
		hitbox.setBounds((int)x, (int)y, (int)width, (int)height);
		return hitbox.intersects(other.getX(), other.getY(), other.getWidth(), other.getHeight());
	}
}
