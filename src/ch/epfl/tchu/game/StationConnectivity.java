package ch.epfl.tchu.game;

/**
 * Implements a method which checks if 2 stations are connected
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public interface StationConnectivity {
	/**
	 * Checks if 2 stations are connected
	 * @param s1 station 1
	 * @param s2 station 2
	 * @return true if they are connected, false if they are not
	 */
	public boolean connected (Station s1, Station s2);
}
