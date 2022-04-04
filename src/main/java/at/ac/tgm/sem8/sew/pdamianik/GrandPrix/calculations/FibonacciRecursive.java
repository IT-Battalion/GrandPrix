package at.ac.tgm.sem8.sew.pdamianik.GrandPrix.calculations;

public class FibonacciRecursive implements Task {
	private final int iterations = (int) (Math.random() * 40) + 1;

	@Override
	public void execute() {
		iteration(iterations);
	}

	private int iteration(int i) {
		if (i == 0 || i == 1) return i;
		return iteration(i - 2) + iteration(i - 1);
	}
}
