package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**
 * The ClientMain class of the package contains the main program for the tCHu client. Since this is a JavaFX application, this class inherits from Application, and its main method as usual does nothing but call launch.
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public final class ClientMain extends Application {

    private static final String LOCAL_HOST = "0.tcp.eu.ngrok.io";
    private static final int PORT = 16888;


    public static void main(String[] args) {
        launch(args);
    }

    /**
     * the start method of Application is responsible for starting the client by analyzing the arguments passed to the program in order to determine the host name and the server port number, creating a remote client — an instance of RemotePlayerClient - associated with a graphics player — an instance of GraphicalPlayerAdapter, starting the thread managing network access, which does nothing other than execute the run method of the client created previously.
     * @param primaryStage don't use it
     */
    @Override
    public void start(Stage primaryStage) {
        List<String> parameters = getParameters().getRaw();
        String host = LOCAL_HOST;
        int port = PORT;
        if (parameters.size() >= 2) {
            host =  parameters.get(0);
            port = Integer.parseInt(parameters.get(1));
        }
        GraphicalPlayerAdapter playerAdapter = new GraphicalPlayerAdapter();
        RemotePlayerClient client = new RemotePlayerClient(playerAdapter, host,port);
        new Thread(client::run).start();
    }
}