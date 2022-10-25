package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The infos of the game in french
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public final class Info {

    private final String name;

    /**
     * The constructor of the class
     * @param name the name of the player
     */
    public Info(String name) {
        this.name = name;
    }

    /**
     * @param card the card we want to get the name of
     * @param count the number of cards
     * @return the french name of the given card if |count| = 1
     */
    public static String cardName(Card card, int count) {
        StringBuilder tempCardName = new StringBuilder();
        switch (card) {
            case BLACK:
                tempCardName.append(StringsFr.BLACK_CARD);
                break;
            case BLUE:
                tempCardName.append(StringsFr.BLUE_CARD);
                break;
            case GREEN:
                tempCardName.append(StringsFr.GREEN_CARD);
                break;
            case ORANGE:
                tempCardName.append(StringsFr.ORANGE_CARD);
                break;
            case RED:
                tempCardName.append(StringsFr.RED_CARD);
                break;
            case VIOLET:
                tempCardName.append(StringsFr.VIOLET_CARD);
                break;
            case WHITE:
                tempCardName.append(StringsFr.WHITE_CARD);
                break;
            case YELLOW:
                tempCardName.append(StringsFr.YELLOW_CARD);
                break;
            case LOCOMOTIVE:
                tempCardName.append(StringsFr.LOCOMOTIVE_CARD);
                break;
            default:
                throw new IllegalArgumentException();
        }
        tempCardName.append(StringsFr.plural(count));
        return tempCardName.toString();
    }

    /**
     * @param playerNames the players' names
     * @param points the points they have
     * @return a message to inform that the two players have ended the game ex aequo
     */
    public static String draw(List<String> playerNames, int points) {
        StringBuilder allPlayers = new StringBuilder();
        List<String> tempNames = new ArrayList<>(playerNames);
        tempNames.remove(playerNames.size() - 1);
        allPlayers.append(String.join(", ", tempNames));
        allPlayers.append(StringsFr.AND_SEPARATOR + playerNames.get(playerNames.size() - 1));
        return String.format(StringsFr.DRAW, allPlayers.toString(), points);
    }

    /**
     * @return a message to inform who is going to play first
     */
    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, name);
    }

    /**
     * @param count the number of tickets the player keeps
     * @return a message to inform that the player has kept the given count tickets
     */
    public String keptTickets(int count) {
        return String.format(StringsFr.KEPT_N_TICKETS, name, count, StringsFr.plural(count));
    }

    /**
     * @return a message to inform that the current player can play
     */
    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, name);
    }

    /**
     * @param count the number of tickets the player drew
     * @return a message to inform that the player has drawn the given number of tickets
     */
    public String drewTickets(int count) {
        return String.format(StringsFr.DREW_TICKETS, name, count, StringsFr.plural(count));
    }

    /**
     * @param card the card the player drew
     * @return a message to inform that the player has drawn a card "blind", i.e. from the top of the draw pile
     */
    public String drewBlindCard(Card card) {
        return String.format(StringsFr.DREW_BLIND_CARD, name);
    }

    /**
     * @param card the card the player drew
     * @return the message declaring that the player has drawn the face-up card given
     */
    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, name, cardName(card, 1));
    }

    /**
     * @param route the route that is claimed
     * @param cards the cards used to claim it
     * @return the message stating that the player has seized the given route using the given cards
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return String.format(StringsFr.CLAIMED_ROUTE, name, routeName(route), cardsName(cards));
    }

    /**
     * @param route the route the player attempts to claim
     * @param initialCards the cards which he wants to use
     * @return the message stating that the player wishes to seize the given tunnel route initially using the given cards
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, name, routeName(route), cardsName(initialCards));
    }

    /**
     * @return a message to inform that the player has drawn a card "blind", i.e. from the top of the draw pile
     */
    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, name);
    }

    /**
     * @param drawnCards the cards drawn
     * @param additionalCost the cards he has to add
     * @return the message stating that the player has drawn the three additional cards given, and that they involve an additional cost of the number of cards given
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        StringBuilder string = new StringBuilder();
        string.append(String.format(StringsFr.ADDITIONAL_CARDS_ARE, cardsName(drawnCards)));
        if (additionalCost == 0) string.append(String.format(StringsFr.NO_ADDITIONAL_COST));
        else
            string.append(String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost)));
        return string.toString();
    }

    /**
     * @param route the route the player couldn't claim
     * @return the message stating that the player could not (or wanted) to seize the given tunnel
     */
    public String didNotClaimRoute(Route route) {
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, name, routeName(route));
    }

    /**
     * @param carCount the number of cars the player has at the end
     * @return the message declaring that the player has only the given number (and less than or equal to 2) of wagons, and that the last turn therefore begins (LAST_TURN_BEGINS)
     */
    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, name, carCount, StringsFr.plural(carCount));
    }

    /**
     * @param longestTrail the longest trail
     * @return the message stating that the player obtains the end-of-game bonus thanks to the given path, which is the longest, or one of the longest
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        return String.format(StringsFr.GETS_BONUS, name, nameTrail(longestTrail));
    }

    /**
     * @param points the winner's points
     * @param loserPoints the loser's points
     * @return the message declaring that the player wins the game with the number of points given, his opponent having only obtained loserPoints
     */
    public String won(int points, int loserPoints) {
        return String.format(StringsFr.WINS, name, points, StringsFr.plural(points), loserPoints, StringsFr.plural(loserPoints));
    }

    private static String routeName(Route route) {
        return route.station1().name() + StringsFr.EN_DASH_SEPARATOR + route.station2().name();
    }

    private static String cardsName(SortedBag<Card> cards) {
        StringBuilder string = new StringBuilder();
        List<Card> listSortedCards = new ArrayList<>(cards.toSet());
        Collections.sort(listSortedCards);
        Card firstCard = listSortedCards.get(0);
        Card lastCard = listSortedCards.get(listSortedCards.size() - 1);
        string.append(cards.countOf(firstCard) + " " + cardName(firstCard, cards.countOf(firstCard)));
        if (cards.toSet().size() > 1) {
            for (Card card : listSortedCards.subList(1, listSortedCards.size() - 1)) {
                string.append(", " + cards.countOf(card) + " " + cardName(card, cards.countOf(card)));
            }
            string.append(StringsFr.AND_SEPARATOR + cards.countOf(lastCard) + " " + cardName(lastCard, cards.countOf(lastCard)));
        }
        return string.toString();
    }

    private static String nameTrail(Trail trail) {
        return trail.station1().toString() + StringsFr.EN_DASH_SEPARATOR + trail.station2().toString();
    }
}