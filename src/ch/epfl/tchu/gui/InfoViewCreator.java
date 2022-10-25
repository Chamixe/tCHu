package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

/**
 * The class allowing to create the view of the infos
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
final class InfoViewCreator {

    private InfoViewCreator(){}

    /**
     * This method creates the view of the infos of the game
     * @param playerId the id of the owner of the view
     * @param playerNames the names of the players
     * @param ogs the observable game state
     * @param texts the infos of what is going on in the game
     * @return the node of the view
     */
    public static Node createInfoView(PlayerId playerId, Map<PlayerId, String> playerNames,
                               ObservableGameState ogs, ObservableList<Text> texts) {

        //Initialize box
        VBox box = new VBox();
        box.getStylesheets().addAll("info.css", "colors.css");

        //Create Box stats
        VBox boxStats = new VBox();
        box.getChildren().add(boxStats);
        box.setId("player-stats");

        //Create player stats
        for (PlayerId id : PlayerId.ALL) {
            int slot = PlayerId.ALL.indexOf(id);
            TextFlow textFlow = new TextFlow();
            boxStats.getChildren().add(textFlow);
            textFlow.getStyleClass().add(id.name());

            Circle circle = new Circle(5);
            textFlow.getChildren().add(circle);
            circle.getStyleClass().add("filled");

            Text text = new Text();
            text.textProperty().bind(
                    Bindings.format(StringsFr.PLAYER_STATS,
                            playerNames.get(id),
                            ogs.ticketCountProperty(slot),
                            ogs.cardsCountProperty(slot),
                            ogs.carsCountProperty(slot),
                            ogs.constructionPointsProperty(slot)));
            textFlow.getChildren().add(text);

        }
        FXCollections.rotate(boxStats.getChildren(), playerId.ordinal());

        //Create separator
        Separator separator = new Separator();
        box.getChildren().add(separator);

        //Create game info text flow
        TextFlow textFlow = new TextFlow();
        box.getChildren().add(textFlow);
        textFlow.setId("game-info");
        Bindings.bindContent(textFlow.getChildren(), texts);


        return box;
    }
}
