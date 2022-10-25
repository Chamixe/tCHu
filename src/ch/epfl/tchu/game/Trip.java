package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A trip to go from a station to another
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public final class Trip {

	private final Station stationFrom;
	private final Station stationTo;
	private final int points;

	/**
	 * Constructs a trip
	 * @param from the start of the trip
	 * @param to the end of the trip
	 * @param points the points it will give
	 * @throws IllegalArgumentException if the number of points is inferior or equal to 0
	 * @throws NullPointerException if the stations are null
	 */
	public Trip(Station from, Station to, int points) {
		Preconditions.checkArgument(points >0);
		stationFrom = Objects.requireNonNull(from);
		stationTo = Objects.requireNonNull(to);
		this.points = points;
	}

	/**
	 * Constructs all the trips from a list of stations to another
	 * @param from the start of the trips
	 * @param to the end of the trips
	 * @param points the points each trip will give
	 * @return the list of all the trips
	 */
	public static List<Trip> all(List<Station> from, List<Station> to, int points){
		Preconditions.checkArgument(!from.isEmpty());
		Preconditions.checkArgument(!to.isEmpty());
		Preconditions.checkArgument(points>0);
		List <Trip> allTrips = new ArrayList<Trip>();
		for(Station start : from) {
			for(Station end : to) {
				allTrips.add(new Trip(start, end, points));
			}
		}
		return allTrips;
	}

	/**
	 *
	 * @return the start of the trip
	 */
	public Station from() {
		return stationFrom;
	}

	/**
	 *
	 * @return the end of the trip
	 */
	public Station to() {
		return stationTo;
	}

	/**
	 *
	 * @return the points the trip will give
	 */
	public int points() {
		return points;
	}

	/**
	 * Gives a number of points depending if 2 stations are connected or not
	 * @param connectivity if 2 stations are connected
	 * @return the points if they are connected and the opposite if they are not
	 */
	public int points(StationConnectivity connectivity) {
		if(connectivity.connected(stationFrom, stationTo)) return points;
		else return -points;
	}
	
}
