package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 * The state of a player known by all(the number of his tickets, the number of his cards, and the
 * routes he owns)
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public class PublicPlayerState {

    private final int ticketCount, cardCount;
    private final List<Route> routes;
    int carCount;
    int claimPoints;



    /**
     * The constructor
     * @param ticketCount the number of tickets the player has
     * @param cardCount the number of cards the player has
     * @param routes the routes he has captured
     * @throws IllegalArgumentException if the ticketCount or the cardCount is negative
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        Preconditions.checkArgument(ticketCount >= 0);
        Preconditions.checkArgument(cardCount >= 0);
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = List.copyOf(routes);
        this.carCount = Constants.INITIAL_CAR_COUNT - totalForCarCount();
        this.claimPoints = totalForClaimPoints();
    }

    private int totalForCarCount() {
        int total = 0;
        for(Route route : routes){
            total += route.length();
        }
        return total;
    }

    private int totalForClaimPoints() {
        int constructionPoints = 0;
        for(Route route : routes){
            constructionPoints += route.claimPoints();
        }
        return constructionPoints;
    }


    /**
     *
     * @return the count of his tickets
     */
    public int ticketCount(){
        return ticketCount;
    }

    /**
     *
     * @return the count of his cards
     */
    public int cardCount(){
        return cardCount;
    }

    /**
     *
     * @return the routes he has
     */
    public List<Route> routes(){
        return routes;
    }

    /**
     *
     * @return the number of cars he has thanks to his routes
     */
    public int carCount(){
        return carCount;
    }

    /**
     *
     * @return the points he wins thanks to his routes
     */
    public int claimPoints(){
        return claimPoints;
    }

}
