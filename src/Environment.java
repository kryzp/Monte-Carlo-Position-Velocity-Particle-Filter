public class Environment {

	public static final int SIZE = 16;

	private final int[][] loadedChunks;

	public Environment() {
		loadedChunks = new int[SIZE][SIZE];
	}

	public void resetLoadedChunks() {
		for (int y = 0; y < SIZE; y++) {
			for (int x = 0; x < SIZE; x++) {
				loadedChunks[y][x] = 0;
			}
		}
	}

	public void setLoadedChunk(int x, int y) {
		if (isChunkOutOfBounds(x, y)) {
			return;
		}

		loadedChunks[y][x] = 1;
	}

	public boolean isChunkOutOfBounds(int x, int y) {
		return x < 0 || y < 0 || x >= SIZE || y >= SIZE;
	}

	public boolean isLoadedChunkAt(int x, int y) {
		if (isChunkOutOfBounds(x, y)) {
			return false;
		}

		return loadedChunks[y][x] == 1;
	}
}
