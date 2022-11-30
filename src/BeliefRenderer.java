import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public final class BeliefRenderer {
    public static final int CHUNK_SIZE = 64;
    public static final int RENDER_SIZE = Environment.SIZE * CHUNK_SIZE;

    public static final String OUTPUT_PATH = "/Users/kryzp/Documents/Projects/montecarlo/output_images/";

    /*
     * Writes out a visualized image of the belief
     * out to a png file
     */
    public static void drawImageOfBelief(
        double actualPositionX, double actualPositionY,
        double actualVelocityX, double actualVelocityY,
        int chunkX, int chunkY,
        int renderProgress) {

        BufferedImage data = new BufferedImage(RENDER_SIZE, RENDER_SIZE, BufferedImage.TYPE_INT_RGB);
        var graphics = data.getGraphics();

        // background
        graphics.setColor(new Color(16, 16, 17));
        graphics.fillRect(0, 0, RENDER_SIZE, RENDER_SIZE);

        // draw grid
        graphics.setColor(new Color(40, 40, 45));
        for (int y = 0; y < CHUNK_SIZE; y++) {
            graphics.drawLine(0, y * CHUNK_SIZE, RENDER_SIZE, y * CHUNK_SIZE);
        }
        for (int x = 0; x < CHUNK_SIZE; x++) {
            graphics.drawLine(x * CHUNK_SIZE, 0, x * CHUNK_SIZE, RENDER_SIZE);
        }

        // draw chunks
        for (int y = 0; y < CHUNK_SIZE; y++) {
            for (int x = 0; x < CHUNK_SIZE; x++) {
                if (Main.getBelief().getEnvironment().isLoadedChunkAt(x, y)) {
                    graphics.drawLine(x * CHUNK_SIZE, y * CHUNK_SIZE, (x * CHUNK_SIZE) + CHUNK_SIZE, (y * CHUNK_SIZE) + CHUNK_SIZE);
                    graphics.drawLine((x * CHUNK_SIZE) + CHUNK_SIZE, y * CHUNK_SIZE, x * CHUNK_SIZE, (y * CHUNK_SIZE) + CHUNK_SIZE);
                }
            }
        }

        // draw last loaded chunk
        graphics.setColor(new Color(70, 170, 245));
        graphics.drawRect(chunkX * CHUNK_SIZE, chunkY * CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE);

        // draw particle positions (red)
        for (Particle particle : Main.getBelief().getParticles()) {
            int px = (int)(particle.getX() * (double)CHUNK_SIZE);
            int py = (int)(particle.getY() * (double)CHUNK_SIZE);

            if (particle.inBounds()) {
                data.setRGB(px, py, new Color(243, 35, 28).getRGB());
            }
        }

        // draw robot position (green)
        int rbx  = (int)(actualPositionX * (double)CHUNK_SIZE);
        int rby  = (int)(actualPositionY * (double)CHUNK_SIZE);
        int rbvx = (int)(actualVelocityX * (double)CHUNK_SIZE);
        int rbvy = (int)(actualVelocityY * (double)CHUNK_SIZE);
        graphics.setColor(new Color(40, 220, 70));
        graphics.fillRect(rbx-1, rby-1, 3, 3);
        graphics.setColor(new Color(30, 110, 90));
        graphics.drawLine(rbx, rby, rbx + rbvx, rby + rbvy);

        // draw average of particles (yellow)
        Particle avg = Main.getBelief().getPredictedParticle();
        int size1 = 4;
        int avgx  = (int)(avg.getX () * CHUNK_SIZE);
        int avgy  = (int)(avg.getY () * CHUNK_SIZE);
        int avgvx = (int)(avg.getVx() * CHUNK_SIZE);
        int avgvy = (int)(avg.getVy() * CHUNK_SIZE);
        graphics.setColor(new Color(255, 225, 55));
        graphics.fillRect(avgx - 2, avgy - 2, size1, size1);
        graphics.setColor(new Color(255, 90, 20));
        graphics.drawLine(avgx, avgy, avgx + avgvx, avgy + avgvy);

        // write to file
        File file = new File(OUTPUT_PATH + renderProgress + ".png");
        try {
            ImageIO.write(data, "png", file);
        } catch (Exception ignored) {
        }
    }

    /*
     * Clears the output_images folder
     */
    public static void clearOutputFolder() {
        File outputFolder = new File(OUTPUT_PATH);
        File[] files = outputFolder.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }
}
