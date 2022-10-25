package ch.epfl.tchu;

/**
 * Preconditions
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public final class Preconditions {
	
	private Preconditions() { }

	/**
	 * Checks a condition
	 * @param shouldBeTrue is the boolean that we test
	 * @throws IllegalArgumentException if the boolean is false
	 */
	public static void checkArgument(boolean shouldBeTrue) {
		if(!shouldBeTrue) throw new IllegalArgumentException();
	}
	
}

