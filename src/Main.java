import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Main {
	public static final int RENDER_RESOLUTION = 32;
	public static final int RENDER_SIZE = Environment.SIZE * RENDER_RESOLUTION;

	private static int renderProgress = 0;

	private static Belief belief;

	private static double actualPositionX;
	private static double actualPositionY;
	private static double actualVelocityX;
	private static double actualVelocityY;

	private static int chunkX;
	private static int chunkY;

	public static final Random RANDOM = new Random();

	public static void drawImageOfBelief() {

		BufferedImage data = new BufferedImage(RENDER_SIZE, RENDER_SIZE, BufferedImage.TYPE_INT_RGB);
		var graphics = data.getGraphics();

		// background
		graphics.setColor(new Color(16, 16, 17));
		graphics.fillRect(0, 0, RENDER_SIZE, RENDER_SIZE);

		// draw grid
		graphics.setColor(new Color(40, 40, 45));
		for(int y = 0; y < RENDER_RESOLUTION; y++) {
			graphics.drawLine(0, y * RENDER_RESOLUTION, RENDER_SIZE, y * RENDER_RESOLUTION);
		}
		for(int x = 0; x < RENDER_RESOLUTION; x++) {
			graphics.drawLine(x * RENDER_RESOLUTION, 0, x * RENDER_RESOLUTION, RENDER_SIZE);
		}

		// draw chunks
		for(int y = 0; y < RENDER_RESOLUTION; y++) {
			for(int x = 0; x < RENDER_RESOLUTION; x++) {
				if(belief.getEnvironment().isLoadedChunkAt(x, y)) {
					graphics.drawLine(x * RENDER_RESOLUTION, y * RENDER_RESOLUTION, (x * RENDER_RESOLUTION) + RENDER_RESOLUTION, (y * RENDER_RESOLUTION) + RENDER_RESOLUTION);
					graphics.drawLine((x * RENDER_RESOLUTION) + RENDER_RESOLUTION, y * RENDER_RESOLUTION, x * RENDER_RESOLUTION, (y * RENDER_RESOLUTION) + RENDER_RESOLUTION);
				}
			}
		}

		// draw last loaded chunk
		graphics.setColor(new Color(70, 170, 245));
		graphics.drawRect(chunkX * RENDER_RESOLUTION, chunkY * RENDER_RESOLUTION, RENDER_RESOLUTION, RENDER_RESOLUTION);

		// draw particle positions (red)
		for(Particle particle : belief.getParticles()) {
			int px = (int)(particle.getX() * (double)RENDER_RESOLUTION);
			int py = (int)(particle.getY() * (double)RENDER_RESOLUTION);

			if(particle.inBounds()) {
				data.setRGB(px, py, new Color(243, 35, 28).getRGB());
			}
		}

		// draw robot position (green)
		int rbx  = (int)(actualPositionX * (double)RENDER_RESOLUTION);
		int rby  = (int)(actualPositionY * (double)RENDER_RESOLUTION);
		int rbvx = (int)(actualVelocityX * (double)RENDER_RESOLUTION);
		int rbvy = (int)(actualVelocityY * (double)RENDER_RESOLUTION);
		graphics.setColor(new Color(40, 220, 70));
		graphics.fillRect(rbx-1, rby-1, 3, 3);
		graphics.setColor(new Color(30, 110, 90));
		graphics.drawLine(rbx, rby, rbx + rbvx, rby + rbvy);

		// draw average of particles (yellow)
		Particle avg = belief.getPredictedParticle();
		int size1 = 4;
		int avgx  = (int)(avg.getX () * RENDER_RESOLUTION);
		int avgy  = (int)(avg.getY () * RENDER_RESOLUTION);
		int avgvx = (int)(avg.getVx() * RENDER_RESOLUTION);
		int avgvy = (int)(avg.getVy() * RENDER_RESOLUTION);
		graphics.setColor(new Color(255, 225, 55));
		graphics.fillRect(avgx - 2, avgy - 2, size1, size1);
		graphics.setColor(new Color(255, 90, 20));
		graphics.drawLine(avgx, avgy, avgx + avgvx, avgy + avgvy);

		// write to file
		File file = new File("/Users/kryzp/Documents/Projects/montecarlo/output_images/" + renderProgress + ".png");
		try {
			ImageIO.write(data, "png", file);
		} catch (Exception ignored) {
		}

		renderProgress++;
	}

	private static void deleteOutputFolder() {
		File outputFolder = new File("/Users/kryzp/Documents/Projects/montecarlo/output_images");
		File[] files = outputFolder.listFiles();
		if (files != null) {
			for (File f : files) {
				f.delete();
			}
		}
	}

	public static void main(String[] args) throws IOException {

		double errorXAvg = 0.0;
		double errorYAvg = 0.0;
		double errorVxAvg = 0.0;
		double errorVyAvg = 0.0;

		final int ITERATIONS = 256;

		Environment environment = new Environment();
		belief = new Belief(environment, 0, 0);

		actualPositionX = 0.0;
		actualPositionY = 0.0;
		actualVelocityX = 0.2;
		actualVelocityY = 0.125;

		deleteOutputFolder();

		for (int i = 0; i < 512; i++) {

			actualPositionX += actualVelocityX;
			actualPositionY += actualVelocityY;

			chunkX = (int)actualPositionX;
			chunkY = (int)actualPositionY;

			if (environment.isChunkOutOfBounds(chunkX, chunkY) || (chunkX == 13 && chunkY == 8)) {
				break;
			}

			if (i % 4 == 0) {
				environment.resetLoadedChunks();
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

			belief.update();

			Particle prediction = belief.getPredictedParticle();
			double errorX = Math.abs(prediction.getX() - actualPositionX);
			double errorY = Math.abs(prediction.getY() - actualPositionY);
			double errorVx = Math.abs(prediction.getVx() - actualVelocityX);
			double errorVy = Math.abs(prediction.getVy() - actualVelocityY);

			errorXAvg += errorX;
			errorYAvg += errorY;
			errorVxAvg += errorVx;
			errorVyAvg += errorVy;

			System.out.format("Error : %.2f,  %.2f | %.2f, %.2f%n", errorX, errorY, errorVx, errorVy);

			drawImageOfBelief();
		}

//		System.out.format("Chunk Pos  : %.2f, %.2f%n", (float)chunkX, (float)chunkY);
//		System.out.format("Prediction : %.2f, %.2f | %.2f, %.2f%n", prediction.getX(), prediction.getY(), prediction.getVx(), prediction.getVy());
//		System.out.format("True Value : %.2f, %.2f | %.2f, %.2f%n", actualPositionX, actualPositionY, actualVelocityX, actualVelocityY);

		errorXAvg /= (double)ITERATIONS;
		errorYAvg /= (double)ITERATIONS;
		errorVxAvg /= (double)ITERATIONS;
		errorVyAvg /= (double)ITERATIONS;

		System.out.format("Error Average : %.5f, %.5f | %.5f, %.5f%n", errorXAvg, errorYAvg, errorVxAvg, errorVyAvg);
		System.out.format("Error Total   : %f", errorXAvg + errorYAvg + errorVxAvg + errorVyAvg);
	}

	public static Belief getBelief() {
		return belief;
	}
}
