package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * the state of cards of the game
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */

public final class CardState extends PublicCardState {

    private final Deck deck;
    private final SortedBag<Card> discard;

    /**
     * @param faceUpCards
     * @param deck
     * @param discard
     */
    private CardState(List<Card> faceUpCards, Deck deck, SortedBag<Card> discard) {
        super(faceUpCards, deck.size(), discard.size());
        this.deck = deck;
        this.discard = discard;
    }

    /**
     * @param deck
     * @return a CardState with a given deck of cards
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);
        return new CardState(deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList(), deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT), SortedBag.of());
    }

    /**
     * @param slot
     * @return a CardState with new faceUpCards and without topCard
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(!isDeckEmpty());
        List<Card> newCards = new ArrayList<>(faceUpCards());
        newCards.set(Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT), (Card) deck.topCard());
        return new CardState(newCards, deck.withoutTopCard(), discard);
    }

    /**
     * @param rng
     * @return a new CardState with a new deck recreated from the discard
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(deck.isEmpty());
        return new CardState(faceUpCards(), Deck.of(discard, rng), SortedBag.of());
    }

    /**
     * @param additionalDiscards
     * @return a new CardState with new cards  added to the discard
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        return new CardState(faceUpCards(), deck, discard.union(additionalDiscards));
    }

    /**
     * @return the topCard of the deck
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!deck.isEmpty());
        return (Card) deck.topCard();
    }

    /**
     * @return a new  CardState without the topCardof the deck which is added to the discard
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!deck.isEmpty());
        return new CardState(faceUpCards(), deck.withoutTopCard(), discard);
    }

}