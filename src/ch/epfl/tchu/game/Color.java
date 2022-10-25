package ch.epfl.tchu.game;

import java.util.List;

/**
 * The colors of the cards of the game
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public enum Color {

    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE;
    /**
     * a list of all the colors possible
     */
    public static final List <Color> ALL = List.of(Color.values());
    /**
     * the number of colors available
     */
    public static final int COUNT = ALL.size();

}
