import java.util.ArrayList;

public class Belief {

	public static final int PARTICLE_COUNT = 1024;
	public static final int RANDOM_PARTICLE_COUNT = 1; // lower values lead to a more precise velocity and position, but increase the risk of losing the target when it rapidly diverges from a linear path. higher values increase uncertainty in exact position and velocity but decrease the chance of losing the target entirely. the value must be changed experimentally.

	private final Environment environment;

	ArrayList<Particle> particles = new ArrayList<Particle>();

	public Belief(Environment env, int initChunkX, int initChunkY) {
		this.environment = env;
		for (int i = 0; i < PARTICLE_COUNT; i++) {
			this.particles.add(getRandomParticle(initChunkX, initChunkY));
		}
	}

	/*
	 * Updates the belief based on the current knowledge
	 * of the environment
	 */
	public void update() {
		WeightedPool<Particle> weighted = new WeightedPool<Particle>();

		// update all particles and add them to weighted list based on how probable they are
		for (int i = 0; i < PARTICLE_COUNT; i++) {
			var particle = new Particle(particles.get(i));
			particle.move();
			weighted.add(particle, particle.getWeight(environment));
		}

		// clear current particles
		particles.clear();

		// add weighted particles into our current particle list
		for (int i = 0; i < PARTICLE_COUNT - RANDOM_PARTICLE_COUNT; i++) {
			Particle particle = new Particle(weighted.getOne());
			particle.mutate();
			particles.add(particle);
		}

		// add completely random particles
		for (int i = 0; i < RANDOM_PARTICLE_COUNT; i++) {
			Particle particle = new Particle(weighted.getOne());
			particles.add(getRandomParticle(particle.getChunkX(), particle.getChunkY()));
		}
	}

	/*
	 * Generates a randomized particle
	 */
	private Particle getRandomParticle(int chunkX, int chunkY) {
		Particle result = new Particle(Particle.EMPTY);

		// randomize position
		result.setX(chunkX + Main.RANDOM.nextDouble());
		result.setY(chunkY + Main.RANDOM.nextDouble());

		// randomize direction
		double theta  = Main.RANDOM.nextDouble() * 2.0 * Math.PI;

		// randomize speed
		double radius = Main.RANDOM.nextDouble(0.0, 0.1);

		// convert polar to cartesian
		result.setVx(radius * Math.cos(theta));
		result.setVy(radius * Math.sin(theta));

		return result;
	}

	/*
	 * Computes a weighted mean particle with weights making
	 * more likely particles more probable and have more effect on the mean
	 */
	public Particle getPredictedParticle() {
		Particle result = new Particle(Particle.EMPTY);
		double weightSum = 0.0;

		for (Particle here : particles) {
			double weight = here.getWeight(environment);
			double dx  = weight * here.getX();
			double dy  = weight * here.getY();
			double dvx = weight * here.getVx();
			double dvy = weight * here.getVy();
			result.setX (result.getX()  + dx );
			result.setY (result.getY()  + dy );
			result.setVx(result.getVx() + dvx);
			result.setVy(result.getVy() + dvy);
			weightSum += weight;
		}

		result.setX (result.getX()  / weightSum);
		result.setY (result.getY()  / weightSum);
		result.setVx(result.getVx() / weightSum);
		result.setVy(result.getVy() / weightSum);

		return result;
	}

	public ArrayList<Particle> getParticles() {
		return particles;
	}

	public Environment getEnvironment() {
		return environment;
	}
}
