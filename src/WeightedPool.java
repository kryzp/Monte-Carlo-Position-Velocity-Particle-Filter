import java.util.ArrayList;

public class WeightedPool<T> {

	private final ArrayList<Pair<Double, T>> elements = new ArrayList<Pair<Double, T>>();
	private double total = 0.0;

	/*
	 * Adds an item along with a weight
	 * attached to said item to the elements
	 */
	void add(T item, double weight) {
		total += weight;
		elements.add(new Pair<Double, T>(weight, item));
	}

	/*
	 * Returns an item in the elements with its
	 * attached weight affecting the chance of
	 * getting it
	 */
	public T getOne() {
		double value = Main.RANDOM.nextDouble() * total;
		for (Pair<Double, T> item : elements) {
			double weight = item.first();
			value -= weight;
			if (value <= 0.0) {
				return item.second();
			}
		}
		return null;
	}
}
