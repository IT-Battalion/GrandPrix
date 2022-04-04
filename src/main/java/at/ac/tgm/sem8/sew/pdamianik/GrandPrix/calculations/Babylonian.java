package at.ac.tgm.sem8.sew.pdamianik.GrandPrix.calculations;

public class Babylonian implements Task {
	private static final int S = 2;
	private final int iterations = (int) (Math.random() * 694200000) + 1;

	@Override
	public void execute() {
		double x = iterations * 10 * 10d;
		for (int i = 0; i < iterations; i++) {
			x = 1d / 2 * (x + S / x);
		}
	}
}
