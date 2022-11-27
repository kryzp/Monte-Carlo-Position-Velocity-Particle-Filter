import java.util.ArrayList;

public class WeightedPool<T> {

	private final ArrayList<Pair<Double, T>> items = new ArrayList<Pair<Double, T>>();
	private double total = 0.0;

	void add(T item, double weight) {
		total += weight;
		items.add(new Pair<Double, T>(weight, item));
	}

	public T getOne() {
		double value = Main.RANDOM.nextDouble() * total;
		for (Pair<Double, T> item : items) {
			double weight = item.first;
			value -= weight;
			if (value <= 0.0) {
				return item.second;
			}
		}
		return null;
	}
}
