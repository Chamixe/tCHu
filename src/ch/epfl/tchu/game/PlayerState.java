package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * The player state that is not public
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public final class PlayerState extends PublicPlayerState {

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;

    /**
     * The constructor of the state of the player
     * @param tickets a sorted bag of his tickets
     * @param cards a sorted bag of his cards
     * @param routes the list of his routes
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size() , routes);
        this.tickets = SortedBag.of(tickets);
        this.cards = SortedBag.of(cards);
    }

    /**
     * Constructs the state of a player at the beginning of the game
     * @param initialCards the cards he begins with
     * @return the state of the player at the start of the game
     * @throws IllegalArgumentException if he doesn't have the right amount of cards at the start
     */
    public static PlayerState initial(SortedBag<Card> initialCards){
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(),initialCards, List.of());
    }

    /**
     *
     * @return a sorted bag of his tickets
     */
    public SortedBag<Ticket> tickets(){
        return tickets;
    }

    /**
     * Constructs the state of the player with the additional tickets
     * @param newTickets the tickets to add
     * @return the new state of the player
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets){
        return new PlayerState(tickets.union(newTickets), cards, routes());
    }

    /**
     *
     * @return a sorted bag of the cards of the player
     */
    public SortedBag<Card> cards(){
        return cards;
    }

    /**
     * Constructs the state of the player with an additional card
     * @param card the additional card
     * @return the new state of the player
     */
    public PlayerState withAddedCard(Card card){
        return new PlayerState(tickets, cards.union(SortedBag.of(card)), routes());
    }

    /*
     * Constructs the state of the player with additional cards
     * @param additionalCards the cards to give to the player
     * @return the new state of the player
     *
    public PlayerState withAddedCards(SortedBag<Card> additionalCards){
        return new PlayerState(tickets, cards.union(additionalCards), routes());
    }*/

    /**
     * Checks if the player can claim a certain route
     * @param route the route he wants to claim
     * @return if the player can claim it
     */
    public boolean canClaimRoute(Route route){
        if(carCount() >= route.length()){
            for(SortedBag<Card> possibleCards : route.possibleClaimCards()){
                if(cards.contains(possibleCards)) return true;
            }
        }
        return false;
    }

    /**
     * Calculates what cards the player can use to capture a certain route
     * @param route the route he wants to capture
     * @return a list of the sorted bags of card he can use to capture the route
     * @throws IllegalArgumentException if his carCount is smaller that the length of the route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route){
        Preconditions.checkArgument(carCount() >= route.length());
        List<SortedBag<Card>> possibleCards = new ArrayList<>();
        for(SortedBag<Card> posCard : route.possibleClaimCards()){
            if(cards.contains(posCard)) possibleCards.add(posCard);
        }
        return possibleCards;
    }

    /**
     * Calculates what cards he can add
     * @param additionalCardsCount the number of cards tp add
     * @param initialCards the cards he originally has
     * @return a list of the sorted bags of cards he can use
     * @throws IllegalArgumentException if the additionalCardsCount isn't of the right size
     * @throws IllegalArgumentException if the initialCards are empty or too small
     * @throws IllegalArgumentException if the drawnCards aren't of the right size
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards){
        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <=Constants.ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(!initialCards.isEmpty() && initialCards.toSet().size() <= 2);
        SortedBag<Card> cardsRemoved = cards.difference(initialCards);
        SortedBag.Builder<Card> cardsPossibleBuilder = new SortedBag.Builder<>();
        for(Card card : cardsRemoved.toList()){
            if(initialCards.contains(card) || card == Card.LOCOMOTIVE) cardsPossibleBuilder.add(card);
        }
        SortedBag<Card> cardsPossible = cardsPossibleBuilder.build();
        List<SortedBag<Card>> possibleCards = new ArrayList<>();
        if(additionalCardsCount <= cardsPossible.size()) {
            possibleCards = new ArrayList<>(cardsPossible.subsetsOfSize(additionalCardsCount));
            possibleCards.sort(Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));
        }
        return possibleCards;
    }

    /**
     *
     * @param route the route he has won
     * @param claimCards the cards he used to claim the route
     * @return the player state of the player with the route and without the cards
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards){
        List<Route> newRoutes = new ArrayList<>(routes());
        newRoutes.add(route);
        return new PlayerState(tickets, cards.difference(claimCards), newRoutes);
    }

    /**
     *
     * @return the points the player wins thanks to his tickets
     */
    public int ticketPoints(){
        int idMax = 0;
        for(Route route : routes()){
            if(route.station1().id() > idMax) idMax = route.station1().id();
            if(route.station2().id() > idMax) idMax = route.station2().id();
        }
        StationPartition.Builder s = new StationPartition.Builder(idMax + 1);
        for(Route route : routes()){
            s.connect(route.station1(), route.station2());
        }
        StationPartition partition = s.build();
        int points = 0;
        for(Ticket ticket : tickets){
            points += ticket.points(partition);
        }
        return points;
    }

    /**
     *
     * @return the total points the player has without bonuses
     */
    public int finalPoints(){
        return claimPoints() + ticketPoints();
    }

}