package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.ServerSocket;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * The main program of the server of the game
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public final class ServerMain extends Application {

    public final static String PLAYER_ADA = "Ada";
    public final static String PLAYER_CHARLES = "Charles";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Determines the names of the players
        List<String> parameters = getParameters().getRaw();
        String firstPlayerName = PLAYER_ADA;
        String secondPlayerName = PLAYER_CHARLES;
        if (parameters.size() >= 2) {
            firstPlayerName = parameters.get(0);
            secondPlayerName = parameters.get(1);
        }

        //Waits for a connection from the client and creates the players
        Player secondPlayer = new RemotePlayerProxy(new ServerSocket(5108).accept());
        Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, firstPlayerName, PlayerId.PLAYER_2, secondPlayerName);
        Player firstPlayer = new GraphicalPlayerAdapter();
        Map<PlayerId, Player> playerIds = Map.of(PlayerId.PLAYER_1, firstPlayer, PlayerId.PLAYER_2, secondPlayer);

        //Starts the game
        new Thread( () -> Game.play(playerIds, playerNames, SortedBag.of(ChMap.tickets()), new Random())).start();
    }
}
