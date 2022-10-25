package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.lang.ref.PhantomReference;
import java.util.List;
import java.util.Objects;

/**
 * The visible card state of the game
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public class PublicCardState {

    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;

    /**
     * The constructor of this public card state
     * @param faceUpCards the List of the cards that are faceUp (we see what card it is)
     * @param deckSize the size of the deck
     * @param discardsSize the size of the discard
     * @throws IllegalArgumentException if the number of faceUpCards isn't 5 or if the deckSize or
     *  the DiscardSize is negative
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(deckSize >=0 && discardsSize>=0);
        this.faceUpCards = List.copyOf(faceUpCards);
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }

    /*
     *
     * @return the size of all cards in the game
     *
    public int totalSize(){
        return faceUpCards.size() + deckSize + discardsSize;
    }*/

    /**
     *
     * @return the List of the faceUpCards
     */
    public List<Card> faceUpCards(){
        return faceUpCards;
    }

    /**
     *
     * @param slot the slot of the faceUpCard to return
     * @return a certain faceUpCard
     */
    public Card faceUpCard(int slot){
        return faceUpCards.get(Objects.checkIndex(slot,Constants.FACE_UP_CARDS_COUNT));
    }

    /**
     *
     * @return the size of the deck
     */
    public int deckSize(){
        return deckSize;
    }

    /**
     *
     * @return if the deck is empty or not
     */
    public boolean isDeckEmpty(){
        return deckSize == 0;
    }

    /**
     *
     * @return the size of the discard
     */
    public int discardsSize(){
        return discardsSize;
    }
}
