package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * the state of a game
 *
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */

public final class GameState extends PublicGameState {

    private final Map<PlayerId, PlayerState> playerState;
    private final Deck<Ticket> tickets;
    private final CardState cardState;

    /**
     * that constructs a gameState
     *
     * @param tickets
     * @param cardState
     * @param currentPlayerId
     * @param playerState
     * @param lastPlayer
     */
    private GameState(Deck<Ticket> tickets, CardState cardState, PlayerId currentPlayerId, Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(tickets.size(), cardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);
        this.playerState = Map.copyOf(playerState);
        this.tickets = Objects.requireNonNull(tickets);
        this.cardState = Objects.requireNonNull(cardState);
    }

    /**
     * @param tickets
     * @param rng
     * @return an initial gameState with initial tickets
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        Deck<Card> cardDeck = Deck.of(Constants.ALL_CARDS, rng);
        Deck<Ticket> ticketDeck = Deck.of(tickets, rng);
        Map<PlayerId, PlayerState> newMap = new EnumMap<>(PlayerId.class);
        for (PlayerId player : PlayerId.ALL) {
            newMap.put(player, PlayerState.initial(cardDeck.topCards(Constants.INITIAL_CARDS_COUNT)));
            cardDeck = cardDeck.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
        }
        PlayerId currentPlayer = PlayerId.ALL.get(rng.nextInt(PlayerId.COUNT));
        return new GameState(ticketDeck, CardState.of(cardDeck), currentPlayer, newMap, null);
    }


    /**
     * @param playerId
     * @return the state of a player given his playerId
     */
    public PlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * @return the state of the current player
     */
    public PlayerState currentPlayerState() {
        return playerState.get(currentPlayerId());
    }

    /**
     * @param count
     * @return the top count tickets of the deck
     */
    public SortedBag<Ticket> topTickets(int count) {
        Preconditions.checkArgument(0 <= count && count <= ticketsCount());
        return tickets.topCards(count);
    }

    /**
     * @param count
     * @return a new gameState without the count top tickets
     */
    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(0 <= count && count <= ticketsCount());
        return new GameState(tickets.withoutTopCards(count), cardState, currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * @return the topCard of the deck
     */
    public Card topCard() {
        Preconditions.checkArgument(cardState != null && !cardState.isDeckEmpty());
        return cardState.topDeckCard();
    }

    /**
     * @return a gameState with a new deck without the topCard
     */
    public GameState withoutTopCard() {
        Preconditions.checkArgument(cardState != null && !cardState.isDeckEmpty());
        return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * @param discardedCards
     * @return a new gameState with cards added to the discard
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(tickets, cardState.withMoreDiscardedCards(discardedCards), currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * @param rng
     * @return a new gameState with the deck recreated from the discard if it is empty
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        if (cardState.isDeckEmpty()) {
            return new GameState(tickets, cardState.withDeckRecreatedFromDiscards(rng), currentPlayerId(), playerState, lastPlayer());
        } else {
            return this;
        }
    }

    /**
     * @param playerId
     * @param chosenTickets
     * @return a state identical to the receiver but in which the given tickets have been added to the hand of the given player
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(playerState.get(playerId).tickets().isEmpty());
        Map<PlayerId, PlayerState> newMap = new EnumMap<>(playerState);
        newMap.put(playerId, newMap.get(playerId).withAddedTickets(chosenTickets));
        return new GameState(tickets, cardState, currentPlayerId(), newMap, lastPlayer());
    }

    /**
     * @param drawnTickets
     * @param chosenTickets
     * @return a state identical to the receiver, but in which the current player has drawn the tickets drawnTickets from the top of the deck, and chosen to keep those contained in chosenTicket
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        Map<PlayerId, PlayerState> newMap = new EnumMap<>(PlayerId.class);
        for (PlayerId playerId : PlayerId.ALL) {
            if (playerId == currentPlayerId()) {
                newMap.put(playerId, playerState.get(playerId).withAddedTickets(chosenTickets));
            } else {
                newMap.put(playerId, playerState.get(playerId));
            }
        }
        return new GameState(tickets.withoutTopCards(Constants.IN_GAME_TICKETS_COUNT), cardState, currentPlayerId(), newMap, lastPlayer());
    }

    /**
     * @param slot
     * @return an identical state to the receiver except that the face-up card at the given location has been placed in the hand of the current player,
     * and replaced by the one at the top of the pickaxe
     */
    public GameState withDrawnFaceUpCard(int slot) {
        //Preconditions.checkArgument(canDrawCards());
        Map<PlayerId, PlayerState> newMap = new EnumMap<>(playerState);
        newMap.put(currentPlayerId(), playerState.get(currentPlayerId()).withAddedCard(cardState.faceUpCard(slot)));
        CardState newCardState = cardState.withDrawnFaceUpCard(slot);
        return new GameState(tickets, newCardState, currentPlayerId(), newMap, lastPlayer());
    }

    /**
     * @return an identical state to the receiver except that the top card of the draw pile has been placed in the hand of the current player
     */
    public GameState withBlindlyDrawnCard() {
        //Preconditions.checkArgument(canDrawCards());
        Map<PlayerId, PlayerState> newMap = new EnumMap<>(playerState);
        newMap.put(currentPlayerId(), playerState.get(currentPlayerId()).withAddedCard(cardState.topDeckCard()));
        return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId(), newMap, lastPlayer());
    }

    /**
     * @param route
     * @param cards
     * @return a state identical to the receiver but in which the current player has seized the given route by means of the given cards
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        Map<PlayerId, PlayerState> newMap = new EnumMap<>(PlayerId.class);
        for (PlayerId playerId : PlayerId.ALL) {
            if (playerId == currentPlayerId())
                newMap.put(playerId, playerState.get(playerId).withClaimedRoute(route, cards));
            else
                newMap.put(playerId, playerState.get(playerId));
        }
        return new GameState(tickets, cardState.withMoreDiscardedCards(cards), currentPlayerId(), newMap, lastPlayer());
    }

    /**
     * @return true iff the last round starts
     */
    public boolean lastTurnBegins() {
        return playerState.get(currentPlayerId()).carCount() <= 2 && lastPlayer() == null;
    }

    /**
     * @return an identical state to the receiver except that the current player is the one following the current current player
     */
    public GameState forNextTurn() {
        PlayerId lastPlayerId = lastTurnBegins() ? currentPlayerId() : lastPlayer();
        return new GameState(tickets, cardState, currentPlayerId().next(), playerState, lastPlayerId);


    }

}