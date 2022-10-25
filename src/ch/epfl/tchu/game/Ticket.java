package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

/**
 * Tickets required to be able to have a trip
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public final class Ticket implements Comparable<Ticket>{

	private final List<Trip> trips;

	/**
	 * Constructs a ticket with the trips
	 * @param trips trips available with the ticket
	 * @throws IllegalArgumentException if trips is empty or all the stations from don't
	 * have the same name
	 */
	public Ticket(List<Trip> trips) {
		Preconditions.checkArgument(!trips.isEmpty());
		for (int i = 1; i < trips.size() ; i++) {
			Preconditions.checkArgument(trips.get(i-1).from().name() == trips.get(i).from().name());
		}
		this.trips = new ArrayList<>(trips);
	}

	/**
	 * Constructs a ticket with a single trip
	 * @param from The start of the trip
	 * @param to The end of the trip
	 * @param points The number of points it gives you
	 */
	public Ticket(Station from, Station to, int points) {
		this(List.of(new Trip(from,to,points)));
	}

	/**
	 * Computes the text that will show
	 * @return the text it will show
	 */
	private static String computeText(List<Trip> trips) {
		String textFrom = trips.get(0).from().name();
		TreeSet<String> to = new TreeSet<>();

		for(Trip trip : trips) {
			to.add(trip.to().name() + " (" + trip.points() + ")");
		}
		String tempText;
		tempText = textFrom + " - ";
		if(to.size()>1){
			tempText += "{";
			tempText+=String.join(", ", to);
			tempText+="}";
		}
		else{
			tempText += to.first();
		}
		return tempText;
	}

	/**
	 * @return the text on the ticket
	 */
	public String text() {
		return computeText(trips);
	}


	/**
	 * Compares this ticket to another one so that it can order them alphabeticaly
	 * @param that the other ticket it is compared with
	 * @return 0 if they have the same name, -1 if this has the priority and 1 if
	 * that has the priority
	 */
	@Override
	public int compareTo(Ticket that) {
		return text().compareTo(that.text());
	}

	/**
	 * Calculates the number of points this ticket gives
	 * @param connectivity if 2 stations are connected
	 * @return the points the ticket will give
	 */
	public int points(StationConnectivity connectivity) {
		int tempPoints = trips.get(0).points(connectivity);
		for(Trip trip : trips){
			if(tempPoints < trip.points(connectivity)) tempPoints = trip.points(connectivity);
		}
		return tempPoints;
	}

	@Override
	public String toString() {
		return text();
	}
}
