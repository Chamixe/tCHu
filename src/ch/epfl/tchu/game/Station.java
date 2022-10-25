package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * The station where a trip will begin or end
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public final class Station {

	private final int id;
	private final String name;

	/**
	 * Constructs a station
	 * @param id the id of the station
	 * @param name its name
	 * @throws IllegalArgumentException if the id is under 0 or above 50
	 */
	public Station(int id, String name) {
		Preconditions.checkArgument(id>=0);
		this.id = id;
		this.name = name;
	}

	/**
	 *
	 * @return the id of the station
	 */
	public int id() {
		return id;
	}

	/**
	 *
	 * @return the name of the station
	 */
	public String name() {
		return name;
	}

	@Override
	public String toString(){
		return name;
	}
}
