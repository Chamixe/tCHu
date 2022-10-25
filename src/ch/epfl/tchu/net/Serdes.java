package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The class having the Serdes allowing the different objects to be serialized and deserialized
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public final class Serdes {

    /**
     * Serialization and deserialization of an Integer
     */
    public static final Serde<Integer> serInt = Serde.of(i -> i.toString(), Integer::parseInt);

    /**
     * Serialization and deserialization of a String
     */
    public static final Serde<String> serString = Serde.of(bChain -> Base64.getEncoder().encodeToString(bChain.getBytes(StandardCharsets.UTF_8)),
            dChain -> new String(Base64.getDecoder().decode(dChain), StandardCharsets.UTF_8));

    /**
     * Serialization and deserialization of a PlayerId
     */
    public static final Serde<PlayerId> serPlayerId = Serde.oneOf(PlayerId.ALL);

    /**
     * Serialization and deserialization of a TurnKind
     */
    public static final Serde<Player.TurnKind> serTurnKind = Serde.oneOf(Player.TurnKind.ALL);

    /**
     * Serialization and deserialization of a Card
     */
    public static final Serde<Card> serCard = Serde.oneOf(Card.ALL);

    /**
     * Serialization and deserialization of a Route
     */
    public static final Serde<Route> serRoute = Serde.oneOf(ChMap.routes());

    /**
     * Serialization and deserialization of a Ticket
     */
    public static final Serde<Ticket> serTicket = Serde.oneOf(ChMap.tickets());

    /**
     * Serialization and deserialization of a List of String
     */
    public static final Serde<List<String>> serListString = Serde.listOf(serString, ",");

    /**
     * Serialization and deserialization of a List of Card
     */
    public static final Serde<List<Card>> serListCard = Serde.listOf(serCard, ",");

    /**
     * Serialization and deserialization of a List of Route
     */
    public static final Serde<List<Route>> serListRoute = Serde.listOf(serRoute, ",");

    /**
     * Serialization and deserialization of a Bag of Card
     */
    public static final Serde<SortedBag<Card>> serBagCard = Serde.bagOf(serCard, ",");

    /**
     * Serialization and deserialization of a Bag of Ticket
     */
    public static final Serde<SortedBag<Ticket>> serBagTicket = Serde.bagOf(serTicket, ",");

    /**
     * Serialization and deserialization of a List of Bag of Card
     */
    public static final Serde<List<SortedBag<Card>>> serListBagCard = Serde.listOf(serBagCard, ";");

    /**
     * Serialization and deserialization of a PublicCardState
     */
    public static final Serde<PublicCardState> serPublicCardState = Serde.of(pcs -> {
        StringBuilder sb = new StringBuilder();
        sb.append(serListCard.serialize(pcs.faceUpCards()) + ";");
        sb.append(serInt.serialize(pcs.deckSize()) + ";");
        sb.append(serInt.serialize(pcs.discardsSize()));
        return sb.toString();
    }, chain -> {
        String[] attributes = chain.split(Pattern.quote(";"), -1);
        List<Card> cardList = serListCard.deserialize(attributes[0]);
        int deckSize = serInt.deserialize(attributes[1]);
        int discardsSize = serInt.deserialize(attributes[2]);
        return new PublicCardState(cardList, deckSize, discardsSize);
    });

    /**
     * Serialization and deserialization of a PublicPlayerState
     */
    public static final Serde <PublicPlayerState> serPublicPlayerState = Serde.of(pps-> {
                StringBuilder sb = new StringBuilder();
                sb.append(serInt.serialize(pps.ticketCount()) + ";");
                sb.append(serInt.serialize(pps.cardCount()) + ";");
                sb.append(serListRoute.serialize(pps.routes()));
                return sb.toString();
            }, chain -> {
                String[] attributes = chain.split(Pattern.quote(";"), -1);
                int ticketCount = serInt.deserialize(attributes[0]);
                int cardCount = serInt.deserialize(attributes[1]);
                List<Route> routes = serListRoute.deserialize(attributes[2]);
                return new PublicPlayerState(ticketCount, cardCount, routes);
            }
    );

    /**
     * Serialization and deserialization of a PlayerState
     */
    public static final Serde<PlayerState> serPlayerState = Serde.of(ps ->{
        StringBuilder sb = new StringBuilder();
        sb.append(serBagTicket.serialize(ps.tickets()) + ";");
        sb.append(serBagCard.serialize(ps.cards()) + ";");
        sb.append(serListRoute.serialize(ps.routes()));
        return sb.toString();
    }, chain -> {
        String[] attributes = chain.split(Pattern.quote(";"), -1);
        SortedBag<Ticket> tickets = serBagTicket.deserialize(attributes[0]);
        SortedBag<Card> cards = serBagCard.deserialize(attributes[1]);
        List<Route> routes = serListRoute.deserialize(attributes[2]);
        return new PlayerState(tickets, cards, routes);
    });

    /**
     * Serialization and deserialization of a PublicGameState
     */
    public static final Serde<PublicGameState> serPublicGameState = Serde.of(pgs -> {
                StringBuilder sb = new StringBuilder();
                sb.append(serInt.serialize(pgs.ticketsCount()) + ":");
                sb.append(serPublicCardState.serialize(pgs.cardState()) + ":");
                sb.append(serPlayerId.serialize(pgs.currentPlayerId()) + ":");
                sb.append(serPublicPlayerState.serialize(pgs.playerState(PlayerId.PLAYER_1)) + ":");
                sb.append(serPublicPlayerState.serialize(pgs.playerState(PlayerId.PLAYER_2)) + ":");
                sb.append(pgs.lastPlayer() != null ? (serPlayerId.serialize(pgs.lastPlayer()) + ":") : "");
                return sb.toString();}
            , chain -> {
                String [] attributes = chain.split(Pattern.quote(":"), -1);
                int ticketsCount = serInt.deserialize(attributes[0]);
                PublicCardState publicCardState = serPublicCardState.deserialize(attributes[1]);
                PlayerId currentPlayerId = serPlayerId.deserialize(attributes[2]);
                PublicPlayerState statePlayer1 = serPublicPlayerState.deserialize(attributes[3]);
                PublicPlayerState statePlayer2 = serPublicPlayerState.deserialize(attributes[4]);
                PlayerId lastPlayer = !attributes[5].equals("") ? serPlayerId.deserialize(attributes[5]) : null;
                return new PublicGameState(ticketsCount, publicCardState, currentPlayerId,
                        Map.of(PlayerId.PLAYER_1, statePlayer1, PlayerId.PLAYER_2, statePlayer2), lastPlayer);
            }
    );

}