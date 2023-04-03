import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Main {
	public static final Random RANDOM = new Random();

	private static Belief belief;
	private static Environment environment;

	private static double actualPositionX = 0.0;
	private static double actualPositionY = 0.0;
	private static double actualVelocityX = 0.02;
	private static double actualVelocityY = 0.0125;

	/*
	 * Entry Point
	 */
	public static void main(String[] args) {

		environment = new Environment();
		belief = new Belief(environment, 0, 0);

		boolean once = true;

		JFrame frame = new JFrame("Monte Carlo");
		frame.setContentPane(new JComponent() {
			@Override
			public void paintComponent(Graphics g) {
				BeliefRenderer.drawImageOfBelief(
					actualPositionX, actualPositionY,
					actualVelocityX, actualVelocityY,
					belief, environment,
					(int)actualPositionX, (int)actualPositionY, g
				);
			}
		});
		frame.setFocusable(true);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(BeliefRenderer.RENDER_SIZE, BeliefRenderer.RENDER_SIZE);

		while (true) {

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

			// we can only poll the environment every 4 ticks (limitation)
			//if (i % 4 == 0) {
			environment.resetLoadedChunks();
			for (int y = 0; y < 3; y++) {
				for (int x = 0; x < 3; x++) {
					environment.setLoadedChunk(chunkX + x - 1, chunkY + y - 1);
				}
			}
			//}

			// update our current belief
			belief.update();

			// write out info
			frame.repaint();

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// update position
			actualPositionX += actualVelocityX;
			actualPositionY += actualVelocityY;
		}
	}
}
