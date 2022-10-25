package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;

/**
 * The instantiable class GraphicalPlayerAdapter, from the ch.epfl.tchu.gui package, aims to adapt (in the sense of the Adapter pattern) an instance of GraphicalPlayer into a value of type Player. It therefore implements the Player interface.
 *
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public final class GraphicalPlayerAdapter implements Player {
    private GraphicalPlayer graphicalPlayer;
    private final BlockingQueue<SortedBag<Ticket>> ticketsBagBlockingQueue = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<SortedBag<Card>> cardBagBlockingQueue = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Route> routeBlockingQueue = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<TurnKind> turnKindBlockingQueue = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Integer> cardSlotBlockingQueue = new ArrayBlockingQueue<>(1);


    /**
     * initPlayers builds, on the JavaFX thread, the instance of the GraphicalPlayer graphics player that it adapts; this instance is stored in a graphicalPlayer attribute so that it can be used by the other methods,
     *
     * @param ownId the id of the player
     * @param playerNames the names of the player
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId, playerNames));
    }

    /**
     * receiveInfo calls, on the JavaFX thread, the method of the same name of the graphics player,
     *
     * @param info the info to be received
     */
    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    /**
     * updateState calls, on the JavaFX thread, the setState method of the graphics player,
     *
     * @param newState the public game state to update
     * @param ownState the player's state to update
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> graphicalPlayer.setState(newState, ownState));

    }

    /**
     * setInitialTicketChoice calls, on the JavaFX thread, the chooseTickets method of the graphic player, to ask him to choose his initial tickets, passing him a choice handler which stores the player's choice in a blocking queue,
     *
     * @param initialTickets the initial tickets
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> initialTickets) {
        runLater(() -> graphicalPlayer.chooseTickets(initialTickets, ticketsBagBlockingQueue::add));
    }

    /**
     * chooseInitialTickets blocks while waiting for the queue also used by setInitialTicketChoice to contain a value, then returns it,
     *
     * @return the value contained in the queue also used by setInitialTicketChoice,
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        try {
            return ticketsBagBlockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * nextTurn calls, on the JavaFX thread, the startTurn method of the graphic player, passing it action managers which place the type of turn chosen, as well as any "arguments" of the action in blocking queues, then blocks while waiting for a value to be placed in the queue containing the type of tour, which it removes and returns,
     *
     * @return the turn kind,
     */
    @Override
    public TurnKind nextTurn() {
        //create the handlers
        ActionHandlers.ClaimRouteHandler routeHandler = (route, cardSortedBag) -> {
            routeBlockingQueue.add(route);
            if (cardSortedBag != null)
                cardBagBlockingQueue.add(cardSortedBag);
            turnKindBlockingQueue.add(TurnKind.CLAIM_ROUTE);
        };
        ActionHandlers.DrawCardHandler cardHandler = slot -> {
            cardSlotBlockingQueue.add(slot);
            turnKindBlockingQueue.add(TurnKind.DRAW_CARDS);
        };
        ActionHandlers.DrawTicketsHandler ticketsHandler = () -> turnKindBlockingQueue.add(TurnKind.DRAW_TICKETS);

        //placing the handlers on the JavaFx thread
        runLater(() -> graphicalPlayer.startTurn(routeHandler, cardHandler, ticketsHandler));
        try {
            return turnKindBlockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * chooseTickets connects the actions performed by setInitialTicketChoice and chooseInitialTickets,
     *
     * @param options the options from which to chooose the tickets
     * @return chosen tickets,
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        BlockingQueue<SortedBag<Ticket>> ticketBagBlockingQueue = new ArrayBlockingQueue<>(1);
        runLater(() -> graphicalPlayer.chooseTickets(options, ticketBagBlockingQueue::add));
        try {
            return ticketBagBlockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * drawSlot tests whether the queue containing the card slots contains a value; if this is the case, it means that drawSlot is called for the first time of the turn, and that the manager installed by nextTurn has placed the location of the first card drawn in this queue, which therefore suffices to return; otherwise, it means that drawSlot is called for the second time of the turn, so that the player draws his second card, and we must therefore call, on the JavaFX thread, the drawCard method of the graphic player, before blocking while waiting for the manager that one passes to him place the location of the drawn card in the queue, which is then extracted and returned,
     *
     * @return the location of the drawn card,
     */
    @Override
    public int drawSlot() {
        if (cardSlotBlockingQueue.isEmpty()) {
            runLater(() -> graphicalPlayer.drawCard(cardSlotBlockingQueue::add));
        }
        try {
            return cardSlotBlockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * claimedRoute extracts and returns the first element of the queue containing the routes, which will have been placed there by the handler passed to startTurn by nextTurn,
     *
     * @return the first element of the queue containing the routes
     */
    @Override
    public Route claimedRoute() {
        try {
            return routeBlockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * initialClaimCards is similar to claimedRoute but uses the queue containing multisets of cards,
     *
     * @return the first element of the queue containing the cards
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        try {
            return cardBagBlockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * chooseAdditionalCards calls, on the JavaFX thread, the method of the same name of the graphic player then blocks while waiting for an element to be placed in the queue containing the multisets of cards, which it returns.
     *
     * @param options the options from which to choose the cards
     * @return element placed in the queue containing the multisets of cards
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        runLater(() -> graphicalPlayer.chooseAdditionalCards(options, cardBagBlockingQueue::add));
        try {
            return cardBagBlockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}