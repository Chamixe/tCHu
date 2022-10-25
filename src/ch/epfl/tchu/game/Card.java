package ch.epfl.tchu.game;

import java.util.List;

/**
 * The different cards of the game
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public enum Card {

    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null);

    private final Color color;

    Card (Color color) {
        this.color = color;
    }

    /**
     * A list of all the cards of the game
     */
    public final static List <Card> ALL = List.of(Card.values());

    /**
     * The number of cards of the game
     */
    public final static int COUNT = ALL.size();

    /**
     * A list of all the cars of the game(all the cards except the locomotive)
     */
    public final static List <Card> CARS = List.of(BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE);

    /**
     * Checks the card associated with the color
     * @param color the color the car is associated with
     * @return the card associated with the color
     */
    public static Card of(Color color) {
        for(Card card : CARS) {
            if(card.color == color) return card;
        }
        return null;
    }

    /**
     *
     * @return the color of the card
     */
    public Color color() {
        return color;
    }

}
