import java.util.ArrayList;

public class Belief {

	public static final int PARTICLE_COUNT = 1000;

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

		for (Particle here : particles) {
			result.setX (result.getX()  + here.getX());
			result.setY (result.getY()  + here.getY());
			result.setVx(result.getVx() + here.getVx());
			result.setVy(result.getVy() + here.getVy());
		}

		result.setX (result.getX()  / (double)PARTICLE_COUNT);
		result.setY (result.getY()  / (double)PARTICLE_COUNT);
		result.setVx(result.getVx() / (double)PARTICLE_COUNT);
		result.setVy(result.getVy() / (double)PARTICLE_COUNT);

		return result;
	}

	public ArrayList<Particle> getParticles() {
		return particles;
	}

	public Environment getEnvironment() {
		return environment;
	}
}
