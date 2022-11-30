import java.util.ArrayList;

public class Environment {

	public static final int SIZE = 16;

	private final ArrayList<Pair<Integer, Integer>> LOADED_CHUNKS;

	public Environment() {
		LOADED_CHUNKS = new ArrayList<Pair<Integer, Integer>>();
	}

	public void resetLoadedChunks() {
		LOADED_CHUNKS.clear();
	}

	public void setLoadedChunk(int x, int y) {
		LOADED_CHUNKS.add(new Pair<Integer, Integer>(x, y));
	}

	public boolean isLoadedChunkAt(int x, int y) {
		for (var c : LOADED_CHUNKS) {
			if (c.first() == x && c.second() == y) {
				return true;
			}
		}

		return false;
	}
}
