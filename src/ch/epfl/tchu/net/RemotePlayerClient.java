package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * class representing the client of a distant player
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public final class RemotePlayerClient {
    private final Player player;
    private final Socket socket;

    /**
     * The constructor of the class
     * @param player the Player the Client represents
     * @param name the address of the server
     * @param port the port of the server
     */
    public RemotePlayerClient(Player player, String name, int port) {
        this.player = player;
        try {
            socket = new Socket(name, port);
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    private void deserializeMessage(MessageId message, String[] strings, BufferedWriter writer) throws IOException {
        String s = null;


        switch (message) {
            case INIT_PLAYERS:
                PlayerId ownId = Serdes.serPlayerId.deserialize(strings[1]);
                String[] names = strings[2].split(Pattern.quote(","), -1);
                for (int i = 0; i < names.length; i++) {
                    names[i] = Serdes.serString.deserialize(names[i]);
                }
                Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, names[0], PlayerId.PLAYER_2, names[1]);
                player.initPlayers(ownId, playerNames);
                break;

            case RECEIVE_INFO:
                String info = Serdes.serString.deserialize(strings[1]);
                player.receiveInfo(info);
                break;

            case UPDATE_STATE:
                PublicGameState newState = Serdes.serPublicGameState.deserialize(strings[1]);
                PlayerState ownState = Serdes.serPlayerState.deserialize(strings[2]);
                player.updateState(newState, ownState);
                break;

            case SET_INITIAL_TICKETS:
                SortedBag<Ticket> tickets = Serdes.serBagTicket.deserialize(strings[1]);
                player.setInitialTicketChoice(tickets);
                break;

            case CHOOSE_INITIAL_TICKETS:
                s = Serdes.serBagTicket.serialize(player.chooseInitialTickets());
                break;

            case NEXT_TURN:
                s = Serdes.serTurnKind.serialize(player.nextTurn());
                break;

            case CHOOSE_TICKETS:
                SortedBag<Ticket> options = Serdes.serBagTicket.deserialize(strings[1]);
                s = Serdes.serBagTicket.serialize(player.chooseTickets(options));
                break;

            case DRAW_SLOT:
                s = Serdes.serInt.serialize(player.drawSlot());
                break;

            case ROUTE:
                s = Serdes.serRoute.serialize(player.claimedRoute());
                break;

            case CARDS:
                s = Serdes.serBagCard.serialize(player.initialClaimCards());
                break;

            case CHOOSE_ADDITIONAL_CARDS:
                List<SortedBag<Card>> options1 = Serdes.serListBagCard.deserialize(strings[1]);
                s = Serdes.serBagCard.serialize(player.chooseAdditionalCards(options1));
                break;
        }

        if(s != null){
            writer.write(s);
            writer.write('\n');
            writer.flush();
        }

    }

    /**
     * This method does a cycle which waits for a message from the server, separates it with a delimiter, determines the type of message and
     * calls tbe corresponding method while sending back what the method returns if it does return something
     */
    public void run() {
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII));
            String chain = r.readLine();
            do {
                if (chain != null) {
                    String[] a = chain.split(Pattern.quote(" "), -1);
                    MessageId m = MessageId.valueOf(a[0]);
                    deserializeMessage(m, a, bw);
                }
                chain = r.readLine();
            } while (chain != null);
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }
}
