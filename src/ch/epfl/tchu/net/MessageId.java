package ch.epfl.tchu.net;

/**
 * The enumeration of the types of messages the server can send to the client
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public enum MessageId {
    INIT_PLAYERS,
    RECEIVE_INFO,
    UPDATE_STATE,
    SET_INITIAL_TICKETS,
    CHOOSE_INITIAL_TICKETS,
    NEXT_TURN, CHOOSE_TICKETS,
    DRAW_SLOT,
    ROUTE,
    CARDS,
    CHOOSE_ADDITIONAL_CARDS
}
