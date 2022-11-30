import java.io.IOException;
import java.util.Random;

public class Main {
	public static final Random RANDOM = new Random();

	private static Belief belief;

	/*
	 * Entry Point
	 */
	public static void main(String[] args) throws IOException {

		Environment environment = new Environment();
		belief = new Belief(environment, 0, 0);

		int renderProgress = 0;

		double actualPositionX = 0.0;
		double actualPositionY = 0.0;
		double actualVelocityX = 0.2;
		double actualVelocityY = 0.125;

		BeliefRenderer.clearOutputFolder();

		boolean once = true;

		for (int i = 0; i < 512; i++) {

			int chunkX = (int)actualPositionX;
			int chunkY = (int)actualPositionY;

			// artificial stoppers
			{
				if (chunkX == 0 && chunkY == 16) {
					break;
				}

				if (chunkX == 13 && chunkY == 8 && once) {
					actualVelocityX = -actualVelocityX;
					once = false;
				}
			}

			// periodically remove all known loaded chunks from environment
			if (i % 8 == 0) {
				environment.resetLoadedChunks();
			}

			// we can only poll the environment every 4 ticks (limitation)
			if (i % 4 == 0) {
				environment.setLoadedChunk(chunkX - 1, chunkY - 1);
				environment.setLoadedChunk(chunkX + 0, chunkY - 1);
				environment.setLoadedChunk(chunkX + 1, chunkY - 1);
				environment.setLoadedChunk(chunkX - 1, chunkY + 0);
				environment.setLoadedChunk(chunkX + 0, chunkY + 0);
				environment.setLoadedChunk(chunkX + 1, chunkY + 0);
				environment.setLoadedChunk(chunkX - 1, chunkY + 1);
				environment.setLoadedChunk(chunkX + 0, chunkY + 1);
				environment.setLoadedChunk(chunkX + 1, chunkY + 1);
			}

			// update our current belief
			belief.update();

			// get the current prediction of the belief
			Particle prediction = belief.getPredictedParticle();

			// write out info
			{
				double errorX  = Math.abs(prediction.getX()  - actualPositionX);
				double errorY  = Math.abs(prediction.getY()  - actualPositionY);
				double errorVx = Math.abs(prediction.getVx() - actualVelocityX);
				double errorVy = Math.abs(prediction.getVy() - actualVelocityY);

				System.out.format("Error           : %.2f, %.2f | %.2f, %.2f%n", errorX, errorY, errorVx, errorVy);
				System.out.format("Error Total Pos : %f (%f far away from true position)%n", (errorX + errorY), Math.sqrt((errorX * errorX) + (errorY * errorY)));
				System.out.format("Error Total Vel : %f%n%n", errorVx + errorVy);

				BeliefRenderer.drawImageOfBelief(
					actualPositionX, actualPositionY,
					actualVelocityX, actualVelocityY,
					chunkX, chunkY,
					renderProgress
				);

				renderProgress++;
			}

			// update position
			actualPositionX += actualVelocityX;
			actualPositionY += actualVelocityY;
		}
	}

	public static Belief getBelief() {
		return belief;
	}
}
