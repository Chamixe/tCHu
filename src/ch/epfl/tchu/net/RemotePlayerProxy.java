package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * this class represents the proxy of a distant player and so acts as a player
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public final class RemotePlayerProxy implements Player {

    private final BufferedWriter bw;
    private final BufferedReader br;

    /**
     * The contructor of the proxy
     * @param socket what the proxy uses to communicate with the client by exchanging textual messages
     */
    public RemotePlayerProxy(Socket socket) {
        try {
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII));
            br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String readMessage() {
        try {
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendMessage(String chain){
        try {
            bw.write(chain);
            bw.write("\n");
            bw.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        String message = MessageId.INIT_PLAYERS.name();
        String ownIdSerialized = Serdes.serPlayerId.serialize(ownId);
        String playerNamesSerialized = Serdes.serString.serialize(playerNames.get(PlayerId.PLAYER_1)) + ","
                + Serdes.serString.serialize(playerNames.get(PlayerId.PLAYER_2));
        sendMessage(String.join(" ", List.of(message, ownIdSerialized, playerNamesSerialized)));
    }

    @Override
    public void receiveInfo(String info) {
        sendMessage(MessageId.RECEIVE_INFO.name() + " " + Serdes.serString.serialize(info));
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String messageSerialized = MessageId.UPDATE_STATE.name();
        String newStateSerialized = Serdes.serPublicGameState.serialize(newState);
        String ownStateSerialized = Serdes.serPlayerState.serialize(ownState);
        sendMessage(String.join(" ", List.of(messageSerialized, newStateSerialized, ownStateSerialized)));
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        String messageSerialized = MessageId.SET_INITIAL_TICKETS.name();
        String ticketsSerialized = Serdes.serBagTicket.serialize(tickets);
        sendMessage(String.join(" ", List.of(messageSerialized, ticketsSerialized)));
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(MessageId.CHOOSE_INITIAL_TICKETS.name());
        return Serdes.serBagTicket.deserialize(readMessage());
    }

    @Override
    public TurnKind nextTurn() {
        sendMessage(MessageId.NEXT_TURN.name());
        return Serdes.serTurnKind.deserialize(readMessage());
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        String messageSerialized = MessageId.CHOOSE_TICKETS.name();
        String optionsSerialized = Serdes.serBagTicket.serialize(options);
        sendMessage(String.join(" ", List.of(messageSerialized, optionsSerialized)));
        return Serdes.serBagTicket.deserialize(readMessage());
    }

    @Override
    public int drawSlot() {
        sendMessage(MessageId.DRAW_SLOT.name());
        return Serdes.serInt.deserialize(readMessage());
    }

    @Override
    public Route claimedRoute() {
        sendMessage(MessageId.ROUTE.name());
        return Serdes.serRoute.deserialize(readMessage());
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(MessageId.CARDS.name());
        return Serdes.serBagCard.deserialize(readMessage());
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        String messageSerialized = MessageId.CHOOSE_ADDITIONAL_CARDS.name();
        String optionsSerialized = Serdes.serListBagCard.serialize(options);
        sendMessage(String.join(" ", List.of(messageSerialized, optionsSerialized)));
        return Serdes.serBagCard.deserialize(readMessage());
    }
}
