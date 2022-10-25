package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The public state of the game visible by all
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public class PublicGameState {
    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;

    /**
     * The Constructor of a public game state
     * @param ticketsCount the number of tickets in the game
     * @param cardState the public card state of the game
     * @param currentPlayerId the current player
     * @param playerState the map of each player to his public state
     * @param lastPlayer the id of the last player to play
     * @throws IllegalArgumentException if ticketsCount is negative or if there isn't the right amount of playerstates
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId,PublicPlayerState> playerState, PlayerId lastPlayer) {
        Preconditions.checkArgument(ticketsCount>=0 && playerState.entrySet().size()==PlayerId.COUNT);
        this.ticketsCount = ticketsCount;
        this.cardState = Objects.requireNonNull(cardState);
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
        this.playerState = Map.copyOf(Objects.requireNonNull(playerState));
        this.lastPlayer = lastPlayer;
    }

    /**
     *
     * @return the count of the tickets in the game
     */
    public int ticketsCount() {
        return ticketsCount;
    }

    /**
     *
     * @return if there are still tickets in the game
     */
    public boolean canDrawTickets() {
        return (ticketsCount != 0);
    }

    /**
     *
     * @return the public card state of the game
     */
    public PublicCardState cardState() {
        return cardState;
    }

    /**
     *
     * @return if a player can draw cards
     */
    public boolean canDrawCards() {
        return ((cardState.deckSize() + cardState.discardsSize()) >= Constants.FACE_UP_CARDS_COUNT);
    }

    /**
     *
     * @return the id of the current player
     */
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    /**
     *
     * @param playerId the player whose player state we return
     * @return the public player state of playerId
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     *
     * @return the public player state of the current player
     */
    public PublicPlayerState currentPlayerState() {
        return playerState.get(currentPlayerId);
    }

    /**
     *
     * @return A list of all the routes that have already been claimed
     */
    public List<Route> claimedRoutes() {
        List<Route> claimedRoutes = new ArrayList<>();
        for (Map.Entry<PlayerId, PublicPlayerState> routes  : playerState.entrySet()) {
            for (Route route: routes.getValue().routes()) {
                claimedRoutes.add(route);
            }
        }
        return claimedRoutes;
    }

    /**
     *
     * @return the last player to play the game
     */
    public PlayerId lastPlayer() {
        return lastPlayer;
    }
}
