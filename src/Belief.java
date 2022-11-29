import java.util.ArrayList;

public class Belief {

	public static final int PARTICLE_COUNT = 1024;

	private final Environment environment;

	ArrayList<Particle> particles = new ArrayList<Particle>();

	public Belief(Environment env, int initChunkX, int initChunkY) {
		this.environment = env;
		for (int i = 0; i < PARTICLE_COUNT; i++) {
			Particle part = new Particle(Particle.EMPTY);

			part.setX(initChunkX + Main.RANDOM.nextDouble());
			part.setY(initChunkY + Main.RANDOM.nextDouble());

			double theta  = Main.RANDOM.nextDouble() * Math.PI * 2.0;
			double radius = Main.RANDOM.nextDouble(0.25, 1.75);

			part.setVx(radius * Math.cos(theta));
			part.setVy(radius * Math.sin(theta));

			this.particles.add(part);
		}
	}

	public void update() {
		WeightedPool<Particle> weighted = new WeightedPool<Particle>();

		for (int i = 0; i < PARTICLE_COUNT; i++) {
			var particle = new Particle(particles.get(i));
			particle.move();
			weighted.add(particle, particle.getWeight(environment));
		}

		particles.clear();

		for (int i = 0; i < PARTICLE_COUNT; i++) {
			Particle particle = new Particle(weighted.getOne());
			particle.mutate();
			particles.add(particle);
		}
	}

	public Particle getPredictedParticle() {
		Particle result = new Particle(Particle.EMPTY);
		double weightSum = 0.0;

		for (Particle here : particles) {
			double weight = here.getWeight(environment);
			weightSum += weight;
			double dx  = weight * here.getX();
			double dy  = weight * here.getY();
			double dvx = weight * here.getVx();
			double dvy = weight * here.getVy();
			result.setX (result.getX()  + dx );
			result.setY (result.getY()  + dy );
			result.setVx(result.getVx() + dvx);
			result.setVy(result.getVy() + dvy);
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
