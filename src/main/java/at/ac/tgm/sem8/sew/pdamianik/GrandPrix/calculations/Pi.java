package at.ac.tgm.sem8.sew.pdamianik.GrandPrix.calculations;

public class Pi implements Task {
	private final int iterations = (int) (Math.random() * 694200000) + 1;

	@Override
	public void execute() {
		int flippingOne = 1;
		int pi = 0;
		final int endCondition = iterations * 2 + 1;

		for (int i = 1; i < endCondition; i += 2) {
			pi += flippingOne / i;
			flippingOne = -flippingOne;
		}
	}
}
