package ch.epfl.tchu.game;

import java.util.List;

/**
 * The ids of the two players
 *
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public enum PlayerId {

    PLAYER_1,
    PLAYER_2;
    /**
     * The list of all players
     */
    public static final List<PlayerId> ALL = List.of(PlayerId.values());
    /**
     * The number of all players
     */
    public static final int COUNT = ALL.size();

    /**
     * @return The other player
     */
    public PlayerId next() {
        return this.equals(PLAYER_1) ? PLAYER_2 : PLAYER_1;
    }
}