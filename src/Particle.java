public class Particle {

	public static final double VELOCITY_ANGLE_CHANGE = Math.PI / 32.0;
	public static final double VELOCITY_MAGNITUDE_CHANGE = 0.015f;
	public static final double POSITION_CHANGE = 0.075;

	public static final Particle EMPTY = new Particle(0.0, 0.0, 0.0, 0.0);

	private double x;
	private double y;
	private double vx;
	private double vy;

	public Particle(double x, double y, double vx, double vy)
	{
		this.x  = x;
		this.y  = y;
		this.vx = vx;
		this.vy = vy;
	}

	public Particle(Particle other)
	{
		this.x  = other.x;
		this.y  = other.y;
		this.vx = other.vx;
		this.vy = other.vy;
	}

	public void mutate() {
		double theta  = Math.atan2(vy, vx);
		double radius = Math.sqrt((vx * vx) + (vy * vy));

		theta  += Main.RANDOM.nextDouble(-VELOCITY_ANGLE_CHANGE, VELOCITY_ANGLE_CHANGE);//VELOCITY_ANGLE_CHANGE * ((2.0 * val) - 1.0);
		radius += Main.RANDOM.nextDouble(-VELOCITY_MAGNITUDE_CHANGE, VELOCITY_MAGNITUDE_CHANGE);//Main.RANDOM.nextDouble(1.0 - VELOCITY_MAGNITUDE_CHANGE, 1.0 + VELOCITY_MAGNITUDE_CHANGE);

		this.vx = radius * Math.cos(theta);
		this.vy = radius * Math.sin(theta);

		this.x += POSITION_CHANGE * (Main.RANDOM.nextDouble() - 0.5);
		this.y += POSITION_CHANGE * (Main.RANDOM.nextDouble() - 0.5);
	}

	public double getWeight() {
		boolean sensor = Main.getEnvironment().isLoadedChunkAt((int)x, (int)y);

		// check if chunk is loaded, if it is there is a high probability that this particle is in the right spot
		if (sensor == true) {
			return 0.95;
		}

		return 0.05;
	}

	public void move() {
		x += vx;
		y += vy;
	}

	public boolean inBounds() {
		return (
			(x >= 0f && y >= 0f) &&
			(x < Environment.SIZE && y < Environment.SIZE)
		);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getVx() {
		return vx;
	}

	public double getVy() {
		return vy;
	}

	public void setX(double v) {
		this.x = v;
	}

	public void setY(double v) {
		this.y = v;
	}

	public void setVx(double v) {
		this.vx = v;
	}

	public void setVy(double v) {
		this.vy = v;
	}
}
