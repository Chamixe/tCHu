package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * a Road that links two stations
 *
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public final class Route {
    public enum Level {OVERGROUND, UNDERGROUND}

    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;


    /**
     * constructs a road with the following parameters
     *
     * @param id       the name of the road
     * @param station1 one of the two stations of the road
     * @param station2 the other  station of the  road
     * @param length   the length of the road
     * @param level    the level of the road (OVERGROUND or UNDERGROUND)
     * @param color    the color of the road
     * @throws NullPointerException the name or the first station or the second station or the level is empty
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        Preconditions.checkArgument((!station1.equals(station2)) && ((length >= Constants.MIN_ROUTE_LENGTH) && (length <= Constants.MAX_ROUTE_LENGTH)));
        this.id = Objects.requireNonNull(id);
        this.station1 = Objects.requireNonNull(station1);
        this.station2 = Objects.requireNonNull(station2);
        this.length = length;
        this.level = Objects.requireNonNull(level);
        this.color = color;
    }

    /**
     * @return the name of the road
     */
    public String id() {
        return id;
    }

    /**
     * @return one of the two stations of the road
     */
    public Station station1() {
        return station1;
    }

    /**
     * @return the other  station of the  road
     */
    public Station station2() {
        return station2;
    }

    /**
     * @return the length of the road
     */
    public int length() {
        return length;
    }

    /**
     * @return the level of the road (OVERGROUND or UNDERGROUND)
     */
    public Level level() {
        return level;
    }

    /**
     * @return the color of the road
     */
    public Color color() {
        return color;
    }

    /**
     * @return the two stations connected by the road
     */
    public List<Station> stations() {
        return List.of(station1, station2);
    }

    /**
     * @param station
     * @return for a given station the station that is on the other side of the road
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(station == station1 || station == station2);
        if (station == station1) return station2;
        else return station1;
    }

    /**
     * @return all possible sets of cards that we can use to take a road
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        List<SortedBag<Card>> cards = new ArrayList<>();
        if (level == Level.OVERGROUND) {
            for (Card card : Card.CARS) {
                if (color == null || card.color() == color) cards.add(SortedBag.of(length, card));
            }
        } else {
            for (int i = 0; i < length; i++) {
                for (Card card : Card.CARS) {
                    if (card.color() == color || color == null)
                        cards.add(SortedBag.of(length - i, card, i, Card.LOCOMOTIVE));
                }
            }
            cards.add(SortedBag.of(length, Card.LOCOMOTIVE));
        }
        return cards;
    }

    /**
     * @param claimCards
     * @param drawnCards
     * @return for a given set of drawn cards and a given set of cards played the additional number of cards to play to seize an UNDERGROUND road
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(level == Level.UNDERGROUND);
        int additionalCards = 0;
        for (Card cardDrawn : drawnCards.toList()) {
            if (claimCards.contains(cardDrawn) || cardDrawn == Card.LOCOMOTIVE) ++additionalCards;
        }
        return additionalCards;
    }

    /**
     * @return number of points that the player gets when he gets the road in function of it's length
     */
    public int claimPoints() {
        return Constants.ROUTE_CLAIM_POINTS.get(length);
    }
}