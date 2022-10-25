package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * The deck of the game
 *
 * @param <C>
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public final class Deck<C extends Comparable<C>> {

    private final List<C> cards;

    /**
     * creates a deck with cards of the game
     *
     * @param cards
     */
    private Deck(List<C> cards) {
        this.cards = List.copyOf(cards);
    }

    /**
     * @param cards
     * @param rng
     * @param <C>
     * @return a new shuffled deck with given cards
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        List<C> mams = cards.toList();
        Collections.shuffle(mams, rng);
        return new Deck<>(mams);
    }

    /**
     * @return the size of a given deck
     */
    public int size() {
        return cards.size();
    }

    /**
     * @return true if the deck is empty
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * @return the topCard of the deck
     */
    public C topCard() {
        Preconditions.checkArgument(!isEmpty());
        return cards.get(0);
    }

    /**
     * @return a new  deck without the topCard
     */
    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(!isEmpty());
        return new Deck<>(cards).withoutTopCards(1);
    }

    /**
     * @param count
     * @return the count topCards of the deck
     */
    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(count > Constants.DECK_SLOT && count <= cards.size());
        return SortedBag.of(cards.subList(0, count));
    }

    /**
     * @param count
     * @return a new deck without the count topCards of the deck
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(count > Constants.DECK_SLOT && count <= cards.size());
        return new Deck<>(cards.subList(count,cards.size()));
    }
}