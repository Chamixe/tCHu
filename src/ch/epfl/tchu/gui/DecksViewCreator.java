package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;


import java.util.ArrayList;
import java.util.List;

/**
 * The DecksViewCreator class contains two public methods,
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
final class DecksViewCreator {
    private static final int OUTSIDE_RECTANGLE_WIDTH = 60;
    private static final int OUTSIDE_RECTANGLE_HEIGHT = 90;
    private static final int INSIDE_AND_TRAINIMAGE_RECTANGLE_WIDTH = 40;
    private static final int INSIDE_AND_TRAINIMAGE_RECTANGLE_HEIGHT = 70;
    private static final int BACKGROUND_AND_FOREGROUND_RECTANGLE_WIDTH = 50;
    private static final int BACKGROUND_AND_FOREGROUND_RECTANGLE_HEIGHT = 5;



    private DecksViewCreator() {}


    /**
     * this method, named createHandView, takes as argument the state of the observable game and returns the view of the hand,
     * @param observableGameState the state of the observable game
     * @return the view of the hand
     */
    public static Node createHandView(ObservableGameState observableGameState) {
        //Initialize handView
        HBox handView = new HBox();
        handView.getStylesheets().addAll("decks.css", "colors.css");

        //Create the tickets view
        ListView<Ticket> listView = new ListView<>(observableGameState.getTickets());
        listView.setId("tickets");
        handView.getChildren().add(listView);

        //Crates the handPane
        HBox handPane = new HBox();
        handPane.setId("hand-pane");
        handView.getChildren().add(handPane);

        //Creates the player's cards in the handPane
        for (Card card : Card.ALL) {
            int index = Card.ALL.indexOf(card);
            ReadOnlyIntegerProperty count = observableGameState.getCardsTypeCount(index);
            Text text = createTextCount(count);
            List<Node> nodes = rectanglesOfPane();
            nodes.add(text);
            StackPane pane = paneOfHandCard(nodes, card);
            pane.visibleProperty().bind(Bindings.greaterThan(count, 0));
            handPane.getChildren().add(pane);
        }

        return handView;
    }

    private static List<Node> rectanglesOfPane() {
        Rectangle outside = new Rectangle(OUTSIDE_RECTANGLE_WIDTH, OUTSIDE_RECTANGLE_HEIGHT);
        outside.getStyleClass().add("outside");
        Rectangle inside = new Rectangle(INSIDE_AND_TRAINIMAGE_RECTANGLE_WIDTH, INSIDE_AND_TRAINIMAGE_RECTANGLE_HEIGHT);
        inside.getStyleClass().addAll("inside", "filled");
        Rectangle trainImage = new Rectangle(INSIDE_AND_TRAINIMAGE_RECTANGLE_WIDTH, INSIDE_AND_TRAINIMAGE_RECTANGLE_HEIGHT);
        trainImage.getStyleClass().add("train-image");
        List<Node> nodeList = new ArrayList<>();
        nodeList.add(outside);
        nodeList.add(inside);
        nodeList.add(trainImage);
        return nodeList;
    }

    private static StackPane paneOfHandCard(List<Node> nodes, Card card) {
        StackPane stackPane = new StackPane();
        stackPane.getStyleClass().add("card");
        if (card != null) {
            stackPane.getStyleClass().add(card == Card.LOCOMOTIVE ? "NEUTRAL" : card.color().name());
        }
        stackPane.getChildren().addAll(nodes);
        return stackPane;
    }

    private static Text createTextCount(ReadOnlyIntegerProperty count) {
        Text text = new Text();
        text.getStyleClass().add("count");
        text.textProperty().bind(Bindings.convert(count));
        text.visibleProperty().bind(Bindings.greaterThan(count, 1));
        return text;
    }

    /**
     * this method, named createCardsView, takes as arguments the observable game state and two properties each containing an action manager: the first contains the one managing the drawing of tickets, the second contains the one managing the drawing of cards.
     * @param observableGameState the observable game state
     * @param ticketsHandler a property
     * @param cardsHandler a property
     * @return the view of the ticket deck, the card deck, and the faceUpCards.
     */
    public static Node createCardsView(ObservableGameState observableGameState,
                                       ObjectProperty<ActionHandlers.DrawTicketsHandler> ticketsHandler,
                                       ObjectProperty<ActionHandlers.DrawCardHandler> cardsHandler) {
        //Initialisation
        VBox box = new VBox();
        box.setId("card-pane");
        box.getStylesheets().addAll("decks.css", "colors.css");

        //create tickets button
        ReadOnlyIntegerProperty ticketsPercentage = observableGameState.percentageTicketsLeftProperty();
        Button ticketsButton = new Button(StringsFr.TICKETS);
        createButton(ticketsButton, ticketsPercentage);
        ticketsButton.disableProperty().bind(ticketsHandler.isNull());
        ticketsButton.setOnAction(e -> ticketsHandler.get().onDrawTickets());
        box.getChildren().add(ticketsButton);

        //create faceUpCards
        for (int i : Constants.FACE_UP_CARD_SLOTS) {
            ReadOnlyObjectProperty<Card> cardProperty = observableGameState.getFaceUpCards(i);
            List<Node> nodes = rectanglesOfPane();
            StackPane pane = paneOfHandCard(nodes, cardProperty.get());
            pane.setOnMouseClicked(e -> cardsHandler.get().onDrawCards(i));
            pane.disableProperty().bind(cardsHandler.isNull());
            observableGameState.getFaceUpCards(i).addListener((o,v,nv) ->{
                if(v != null)
                    box.getChildren().get(i+1).getStyleClass().set(1, nv == Card.LOCOMOTIVE ? "NEUTRAL" : nv.color().name());
                else
                    box.getChildren().get(i+1).getStyleClass().add(nv == Card.LOCOMOTIVE ? "NEUTRAL" : nv.color().name());
            });
            box.getChildren().add(pane);
        }


        //Create cards button
        ReadOnlyIntegerProperty cardsPercentage = observableGameState.percentageCardsLeftProperty();
        Button cardsButton = new Button(StringsFr.CARDS);
        createButton(cardsButton, cardsPercentage);
        cardsButton.disableProperty().bind(cardsHandler.isNull());
        cardsButton.setOnAction(e -> cardsHandler.get().onDrawCards(Constants.DECK_SLOT));
        box.getChildren().add(cardsButton);


        return box;
    }

    private static void createButton(Button button, ReadOnlyIntegerProperty percentage) {
        button.getStyleClass().add("gauged");
        Group group = new Group();
        button.setGraphic(group);
        Rectangle background = new Rectangle(BACKGROUND_AND_FOREGROUND_RECTANGLE_WIDTH, BACKGROUND_AND_FOREGROUND_RECTANGLE_HEIGHT);
        Rectangle foreground = new Rectangle(BACKGROUND_AND_FOREGROUND_RECTANGLE_WIDTH, BACKGROUND_AND_FOREGROUND_RECTANGLE_HEIGHT);
        background.getStyleClass().add("background");
        foreground.getStyleClass().add("foreground");
        group.getChildren().addAll(background, foreground);
        foreground.widthProperty().bind(percentage.multiply(50).divide(100));
    }
}

