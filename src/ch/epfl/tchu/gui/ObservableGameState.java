package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the state of the game visible by the player
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public final class ObservableGameState {

    private final IntegerProperty percentageTicketsLeft = new SimpleIntegerProperty(0);
    private final IntegerProperty percentageCardsLeft = new SimpleIntegerProperty(0);
    private final List<ObjectProperty<Card>> faceUpCards = createFaceUpCards();
    private final List<ObjectProperty<PlayerId>> routeOwners = createRouteOwners();

    private final List<IntegerProperty> ticketCount = createInitialIntegerProperties();
    private final List<IntegerProperty> cardsCount = createInitialIntegerProperties();
    private final List<IntegerProperty> carsCount = createInitialIntegerProperties();
    private final List<IntegerProperty> constructionPoints = createInitialIntegerProperties();

    private final ObservableList<Ticket> tickets = FXCollections.observableArrayList();
    private final List<IntegerProperty> cardsTypeCount = createCardsTypeCount();
    private final List<BooleanProperty> canTakeRoute = createCanTakeRoute();

    private final PlayerId playerId;
    private PlayerState playerState;

    /**
     * Constructor of the observable game state
     * @param playerId the owner of this visible game state
     */
    public ObservableGameState(PlayerId playerId) {
        this.playerId = playerId;
    }

    private static List<ObjectProperty<Card>> createFaceUpCards() {
        List<ObjectProperty<Card>> list = new ArrayList<>();
        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++) {
            list.add(new SimpleObjectProperty<>(null));
        }
        return list;
    }

    private static List<ObjectProperty<PlayerId>> createRouteOwners(){
        List<ObjectProperty<PlayerId>> list = new ArrayList<>();
        for (int i = 0; i < ChMap.routes().size(); i++) {
            list.add(new SimpleObjectProperty<>(null));
        }
        return list;
    }

    private static List<IntegerProperty> createCardsTypeCount() {
        List<IntegerProperty> list = new ArrayList<>();
        for (int i = 0; i < Card.COUNT; i++) {
            list.add(new SimpleIntegerProperty(0));
        }
        return list;
    }

    private static List<BooleanProperty> createCanTakeRoute(){
        List<BooleanProperty> list = new ArrayList<>();
        for (int i = 0; i < ChMap.routes().size(); i++) {
            list.add(new SimpleBooleanProperty(false));
        }
        return list;
    }

    private static List<IntegerProperty> createInitialIntegerProperties(){
        List<IntegerProperty> list = new ArrayList<>();
        for (int i = 0; i < PlayerId.COUNT; i++) list.add(new SimpleIntegerProperty(0));
        return list;
    }

    private static int calculatePercentage(int value, int total){
        return value * 100 / total;
    }

    /**
     * Updates the state of the game visible by the player using the arguments
     * @param gameState the public game state
     * @param playerState the player state of the ObservableGameState
     */
    public void setState(PublicGameState gameState, PlayerState playerState) {

        this.playerState = playerState;

        //sets the percentages
        percentageTicketsLeft.set(calculatePercentage(gameState.ticketsCount(), ChMap.tickets().size()));
        percentageCardsLeft.set(calculatePercentage(gameState.cardState().deckSize(), Constants.TOTAL_CARDS_COUNT));

        //sets the faceUpCards
        for (int slot : Constants.FACE_UP_CARD_SLOTS){
            Card newCard = gameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }

        //sets the routes' owners and if the player can take them
        for (Route route : ChMap.routes()){
            int index = ChMap.routes().indexOf(route);

            if(gameState.playerState(playerId).routes().contains(route))
                routeOwners.get(index).set(playerId);
            else {
                if(gameState.playerState(playerId.next()).routes().contains(route))
                    routeOwners.get(index).set(playerId.next());
                else routeOwners.get(index).set(null);
            }

            canTakeRoute.get(index)
                    .set(playerState.canClaimRoute(route) && routeOwners.get(index).get() == null && canTakeDoubledRoute(route, gameState.claimedRoutes()));

        }

        //sets the tickets count, the cards count, the cars count and the construction points of both players
        for (PlayerId id : PlayerId.ALL) {
            int index = PlayerId.ALL.indexOf(id);
            ticketCount.get(index).set(gameState.playerState(id).ticketCount());
            cardsCount.get(index).set(gameState.playerState(id).cardCount());
            carsCount.get(index).set(gameState.playerState(id).carCount());
            constructionPoints.get(index).set(gameState.playerState(id).claimPoints());
        }

        //sets the
        tickets.setAll(playerState.tickets().toList());

        for (int i = 0; i < Card.ALL.size(); i++) {
            Card card = Card.ALL.get(i);
            cardsTypeCount.get(i).set(playerState.cards().countOf(card));
        }


    }

    private boolean canTakeDoubledRoute(Route route, List<Route> claimedRoutes){
        for (Route claimedRoute : claimedRoutes) {
            if(route.stations().equals(claimedRoute.stations())) return false;
        }
        return true;
    }

    /**
     * The player can draw tickets if the tickets percentage isn't 0
     * @return if the player can draw tickets
     */
    public boolean canDrawTickets() {
        return percentageTicketsLeft.get() != 0;
    }

    /**
     * The player can draw cards if the cards percentage isn't 0
     * @return if the player can draw cards
     */
    public boolean canDrawCards() {
        return percentageCardsLeft.get() != 0;
    }

    /**
     * Determines a list of the cards the player can use to capture a route
     * @param route the route the player wants to capture
     * @return the list
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route){
        return playerState.possibleClaimCards(route);
    }

    /**
     *
     * @return the property of the percentage of tickets left
     */
    public ReadOnlyIntegerProperty percentageTicketsLeftProperty() {
        return percentageTicketsLeft;
    }

    /**
     *
     * @return the property of them percentage of cards left
     */
    public ReadOnlyIntegerProperty percentageCardsLeftProperty() {
        return percentageCardsLeft;
    }

    /**
     *
     * @param slot the slot of the card to get
     * @return the property of the faceUpCard at the given slot
     */
    public ReadOnlyObjectProperty<Card> getFaceUpCards(int slot) {
        return faceUpCards.get(slot);
    }

    /**
     *
     * @param slot the position of the route in the ChMap list of routes
     * @return the owner of the route corresponding to the slot
     */
    public ReadOnlyObjectProperty<PlayerId> getRouteOwners(int slot) {
        return routeOwners.get(slot);
    }

    /**
     *
     * @param slot the ordinal of the player
     * @return the property of the count of tickets of the given player
     */
    public ReadOnlyIntegerProperty ticketCountProperty(int slot) {
        return ticketCount.get(slot);
    }

    /**
     *
     * @param slot the ordinal of the player
     * @return the property of the count of cards of the given player
     */
    public ReadOnlyIntegerProperty cardsCountProperty(int slot) {
        return cardsCount.get(slot);
    }

    /**
     *
     * @param slot the ordinal of the player
     * @return the property of the count of cars of the given player
     */
    public ReadOnlyIntegerProperty carsCountProperty(int slot) {
        return carsCount.get(slot);
    }

    /**
     *
     * @param slot the ordinal of the player
     * @return the property of the construction points of the given player
     */
    public ReadOnlyIntegerProperty constructionPointsProperty(int slot) {
        return constructionPoints.get(slot);
    }

    /**
     *
     * @return the player's tickets
     */
    public ObservableList<Ticket> getTickets() {
        return tickets;
    }

    /**
     *
     * @param slot the ordinal of the card
     * @return the count of the card
     */
    public ReadOnlyIntegerProperty getCardsTypeCount(int slot) {
        return cardsTypeCount.get(slot);
    }

    /**
     *
     * @param slot the ordinal of the route
     * @return if the player can take the route
     */
    public ReadOnlyBooleanProperty getCanTakeRoute(int slot) {
        return canTakeRoute.get(slot);
    }

    /**
     *
     * @return the Id of the owner of this class
     */
    public PlayerId getPlayerId() {
        return playerId;
    }
}
