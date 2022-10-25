package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * CardBagStringConverter is a class which inherits from StringConverter <SortedBag <Card>> and defines concrete versions of its two abstract methods as well.
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
final class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

    /**
     * turns multiset of cards into chains
     * @param cards the cards to transform to a chain
     * @return a String chain
     */
    @Override
    public String toString(SortedBag<Card> cards) {
        List<String> cardsString = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (Card card : cards.toSet()) {
            int count = cards.countOf(card);
            cardsString.add(count + " " + Info.cardName(card, count));
        }
        sb.append(String.join(", ", cardsString.subList(0, cardsString.size() - 1)));
        if(cards.toSet().size() > 1)
            sb.append(StringsFr.AND_SEPARATOR + cardsString.get(cardsString.size()-1));
        else sb.append(cardsString.get(0));
        return sb.toString();
    }

    /**
     * simply throws an exception of type UnsupportedOperationException because it is never used in this context.
     * @param string don't use it
     * @return an exception of type UnsupportedOperationException
     */
    @Override
    public SortedBag<Card> fromString(String string) {
        throw new UnsupportedOperationException();
    }

}