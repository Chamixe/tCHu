package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * this interface is intended solely to contain five nested functional interfaces representing different "action handlers".
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public interface ActionHandlers {

    /**
     * DrawTicketsHandler, whose abstract method, named onDrawTickets and taking no arguments, is called when the player wishes to draw tickets,
     */
    interface DrawTicketsHandler {
        void onDrawTickets();
    }

    /**
     *DrawCardHandler, whose abstract method, named onDrawCard and taking a slot number (0 to 4, or -1 for the draw pile), is called when the player wishes to draw a card from the given slot,
     */
    interface DrawCardHandler {
        void onDrawCards(int slot);
    }

    /**
     *ClaimRouteHandler, whose abstract method, named onClaimRoute and taking as argument a route and a multiset of maps, is called when the player wishes to seize the given route by means of the given (initial) maps,
     */
    interface ClaimRouteHandler {
        void onClaimRoute(Route route, SortedBag<Card> cards);
    }

    /**
     *ChooseTicketsHandler, whose abstract method, named onChooseTickets and taking a multiset of tickets as an argument, is called when the player has chosen to keep the tickets given following a ticket draw,
     */
    interface ChooseTicketsHandler {
        void onChooseTickets(SortedBag<Ticket> tickets);
    }

    /**
     *ChooseCardsHandler, therefore the abstract method, named onChooseCards and taking as argument a multiset of cards, is called when the player has chosen to use the given cards as initial or additional cards when taking possession of a route ; if they are additional cards, then the multiset may be empty, which means that the player gives up taking the tunnel.
     */
    interface ChooseCardsHandler {
        void onChooseCards(SortedBag<Card> cards);
    }
}

