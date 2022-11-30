public class Particle {

	public static final double VELOCITY_ANGLE_CHANGE = Math.PI / 32.0;
	public static final double VELOCITY_MAGNITUDE_CHANGE = 0.015f;
	public static final double POSITION_CHANGE = 0.075;

	public static final Particle EMPTY = new Particle(0.0, 0.0, 0.0, 0.0);

	private double ticksWithoutLoadedChunk;

	private double x;
	private double y;
	private double vx;
	private double vy;

	public Particle(double x, double y, double vx, double vy)
	{
		this.ticksWithoutLoadedChunk = 0.0;

		this.x  = x;
		this.y  = y;
		this.vx = vx;
		this.vy = vy;
	}

	public Particle(Particle other)
	{
		this.ticksWithoutLoadedChunk = 0;

		this.x  = other.x;
		this.y  = other.y;
		this.vx = other.vx;
		this.vy = other.vy;
	}

	/*
	 * Mutates the particles velocity and position
	 */
	public void mutate() {
		double angle     = Math.atan2(vy, vx);
		double magnitude = Math.sqrt((vx * vx) + (vy * vy));

		angle     += calcVelocityAngleMutate();
		magnitude += calcVelocityMagnitudeMutate();

		this.vx = magnitude * Math.cos(angle);
		this.vy = magnitude * Math.sin(angle);

		this.x += POSITION_CHANGE * (Main.RANDOM.nextDouble() - 0.5);
		this.y += POSITION_CHANGE * (Main.RANDOM.nextDouble() - 0.5);
	}

	/*
	 * Calculates how much to mutate the angle of velocity
	 */
	private double calcVelocityAngleMutate() {
		return Main.RANDOM.nextDouble(
			-VELOCITY_ANGLE_CHANGE - (ticksWithoutLoadedChunk / 1024.0),
			VELOCITY_ANGLE_CHANGE + (ticksWithoutLoadedChunk / 1024.0)
		);
	}

	/*
	 * Calculates how much to mutate the magnitude of velocity
	 */
	private double calcVelocityMagnitudeMutate() {
		return Main.RANDOM.nextDouble(
			-VELOCITY_MAGNITUDE_CHANGE - (ticksWithoutLoadedChunk / 1024.0),
			VELOCITY_MAGNITUDE_CHANGE + (ticksWithoutLoadedChunk / 1024.0)
		);
	}

	public double getWeight(Environment env) {
		boolean isOnLoadedChunk = env.isLoadedChunkAt((int)x, (int)y);

		// check if chunk is loaded, if it is there is a high probability that this particle is in the right spot
		if (isOnLoadedChunk) {
			ticksWithoutLoadedChunk = 0;
			return calcOnLoadedChunkWeight();
		}

		ticksWithoutLoadedChunk += 1.0;
		return calcNotOnLoadedChunkWeight();
	}

	/*
	 * Calculates the weight probability when the particle IS on a loaded chunk
	 */
	private double calcOnLoadedChunkWeight() {
		return 0.95 + (0.025 * Main.RANDOM.nextDouble());
	}

	/*
	 * Calculates the weight probability when the particle is NOT on a loaded chunk
	 *
	 * The fewer updates we receive on loaded chunks, the higher this probability
	 * must become to combat that and account for possibly loaded chunks
	 */
	private double calcNotOnLoadedChunkWeight() {
		return 0.075 + (ticksWithoutLoadedChunk / 512.0);
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

	public int getChunkX() {
		return (int)x;
	}

	public int getChunkY() {
		return (int)y;
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
