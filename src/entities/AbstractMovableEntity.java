package entities;

public abstract class AbstractMovableEntity extends AbstractEntity implements MoveableEntity {

	protected double dx, dy, dh, maxDX, maxDY, maxDH;
	protected double heading;
	
	protected boolean printHeading = true;
	protected boolean printRadar = false;
	protected boolean printPieSlices = false;
	protected boolean printWallSensors = false;
	
	public AbstractMovableEntity(double x, double y, double width, double height) {
		this(x, y, width, height, 0);
	}
	
	public AbstractMovableEntity(double x, double y, double width, double height, double heading) {
		super(x, y, width, height);
		this.dx = 5;
		this.dy = 5;
		this.dh = 5;
		this.maxDX = 8;
		this.maxDY = 8;
		this.maxDH = 8;
		this.heading = heading;
	}

	public void update(int delta) {

	}
	
	public void toggleHeading() {
		printHeading = !printHeading;
		System.out.println("Heading: " + heading);
	}

	public void toggleRadar() {
		printRadar = !printRadar;
	}
	
	public void togglePieSlices() {
		printPieSlices = !printPieSlices;
		printRadar = printPieSlices;
	}
	
	public void turnOnWallSensors() {
		printWallSensors = true;
	}
	
	public boolean isWallSensorActivated() {
		return printWallSensors;
	}
	
	public void turnOffWallSensors() {
		printWallSensors = false;
		resetWallSensors();
	}
	
	public void resetWallSensors() {
		return;
	}

	public double getDX() {
		return dx;
	}

	public double getDY() {
		return dy;
	}
	
	public double getDH() {
		return dh;
	}
	
	public double getHeading() {
		return heading;
	}

	public void setDX(double dx) {
		if (dx > maxDX)
			this.dx = maxDX;
		else if (dx == 0)
			this.dx = 1;
		else
			this.dx = Math.abs(dx);
	}

	public void setDY(double dy) {
		if (dy > maxDY)
			this.dy = maxDY;
		else if (dy == 0)
			this.dy = 1;
		else this.dy = Math.abs(dy);
	}
	
	public void setDH(double dh) {
		if (dh > maxDH)
			this.dh = maxDH;
		else if (dy == 0)
			this.dh = 1;
		else
			this.dh = Math.abs(dh);
	}
	
	public void setHeading(double heading) {
		this.heading = heading;
	}
	
	public void increaseHeading() {
		this.heading += dh;
		this.heading %= 360;
		if (printHeading)
			;//System.out.println("Heading: " + heading);
	}
	
	public void decreaseHeading() {
		this.heading -= dh;
		while (this.heading < 0)
			this.heading += 360;
		
		if (printHeading)
			;//System.out.println("Heading: " + heading);
	}
	
	public void moveUp(double minX, double maxX, double minY, double maxY) {
		this.x += Math.cos(Math.toRadians(heading)) * dx;
		this.y -= Math.sin(Math.toRadians(heading)) * dy;
		
		if (this.x < minX)
			this.x = maxX;
		else if (this.x > maxX)
			this.x = minX;
		
		if (this.y < minY)
			this.y = maxY;
		else if (this.y > maxY)
			this.y = minY;
		
		//System.out.println("(x,y): (" + x + ", " + y + ")");
		turnOffWallSensors();
	}
	
	public void moveDown(double minX, double maxX, double minY, double maxY) {
		this.x -= Math.cos(Math.toRadians(heading)) * dx;
		this.y += Math.sin(Math.toRadians(heading)) * dy;
		
		if (this.x < minX)
			this.x = maxX;
		else if (this.x > maxX)
			this.x = minX;
		
		if (this.y < minY)
			this.y = maxY;
		else if (this.y > maxY)
			this.y = minY;
		
		//System.out.println("(x,y): (" + x + ", " + y + ")");
		
		turnOffWallSensors();
	}
	
	public boolean intersects(Entity other) {
		double a;
		// Circle colliding with the right side of the rectangle
		a = Math.sqrt(Math.pow(width, 2)
				- Math.pow(other.getX() + other.getWidth() - x, 2))
				+ y;
		if (a <= other.getY() + other.getHeight() && a >= other.getY()
				&& x - width >= other.getX()
				&& x - width <= other.getX() + other.getWidth())
			return true;

		// Circle colliding with the left side of the rectangle
		a = Math.sqrt(Math.pow(width, 2) - Math.pow(other.getX() - x, 2)) + y;
		if (a <= other.getY() + other.getHeight() && a >= other.getY()
				&& x + width >= other.getX()
				&& x + width <= other.getX() + other.getWidth())
			return true;

		// Circle colliding with the bottom side of the rectangle
		a = Math.sqrt(Math.pow(height, 2)
				- Math.pow(other.getY() + other.getHeight() - y, 2))
				+ x;
		if (a <= other.getX() + other.getWidth() && a >= other.getX()
				&& y - height >= other.getY()
				&& y - height <= other.getY() + other.getHeight())
			return true;

		// Circle colliding with the top side of the rectangle
		a = Math.sqrt(Math.pow(height, 2) - Math.pow(other.getY() - y, 2)) + x;
		if (a <= other.getX() + other.getWidth() && a >= other.getX()
				&& y + height >= other.getY()
				&& y + height <= other.getY() + other.getHeight())
			return true;

		// If the center of the circle is within the bounds of the rectangle
		if (x > other.getX() && x < other.getX() + other.getWidth()
				&& y > other.getY() && y < other.getY() + other.getHeight())
			return true;
		
		return false;
	}
}
