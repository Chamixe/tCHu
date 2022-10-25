package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;
import java.util.Map;

/**
 * The instantiable GraphicalPlayer class represents the graphical interface of a tCHu player.
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public final class GraphicalPlayer {

    private final ObservableGameState observableGameState;
    private final ObjectProperty<ActionHandlers.DrawCardHandler> drawCardsHandler = new SimpleObjectProperty<>();
    private final ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTicketsHandler = new SimpleObjectProperty<>();
    private final ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHandler = new SimpleObjectProperty<>();
    private final ObservableList<Text> textsInfo = FXCollections.observableArrayList();
    private final Stage mainStage;


    /**
     * The constructor of GraphicalPlayer takes as arguments the identity of the player to which the instance corresponds, and the associative table of the names of the players. It builds the graphical interface,
     * @param playerId the identity of the player to which the instance corresponds
     * @param playerNames the associative table of the names of the players
     */
    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        assert Platform.isFxApplicationThread();

        observableGameState = new ObservableGameState(playerId);
        Node mapView = MapViewCreator.createMapView(observableGameState, claimRouteHandler, this::chooseClaimCards);
        Node cardsView = DecksViewCreator.createCardsView(observableGameState, drawTicketsHandler, drawCardsHandler);
        Node handView = DecksViewCreator.createHandView(observableGameState);
        Node infoView = InfoViewCreator.createInfoView(playerId, playerNames, observableGameState, textsInfo);

        BorderPane pane = new BorderPane(mapView, null, cardsView, handView, infoView);
        Scene scene = new Scene(pane);
        mainStage = new Stage();
        mainStage.setScene(scene);
        mainStage.setTitle(String.format("tCHu — %s", playerNames.get(playerId)));
        mainStage.show();
    }

    /**
     *
     * @return the observable game state of the player
     */
    public ObservableGameState getState(){
        return observableGameState;
    }

    /**
     * a method named setState, taking the same arguments as the setState method of ObservableGameState and doing nothing other than calling this method on the observable state of the player,
     * @param gameState the public game state
     * @param playerState the player state
     */
    public void setState(PublicGameState gameState, PlayerState playerState) {
        assert Platform.isFxApplicationThread();
        observableGameState.setState(gameState, playerState);
    }

    /**
     * a method named  receiveInfo, taking a message - of type String - and appending it to the bottom of the game progress information, which is presented in the lower part of the information view,
     * @param message a message - of type String -
     */
    public void receiveInfo(String message) {
        assert Platform.isFxApplicationThread();
        Text textMessage = new Text(message);
        if(textsInfo.size() == 5) textsInfo.remove(0);
        textsInfo.add(textMessage);
    }

    /**
     * a method named startTurn, which takes as arguments three action managers, one for each type of action that the player can perform during a turn, and which allows the player to perform one,
     * @param claimRouteH a claimRoute action manager,
     * @param cardHandler a card action manager,
     * @param ticketsHandler a ticket action manager,
     */
    public void startTurn(ActionHandlers.ClaimRouteHandler claimRouteH,
                          ActionHandlers.DrawCardHandler cardHandler,
                          ActionHandlers.DrawTicketsHandler ticketsHandler) {
        assert Platform.isFxApplicationThread();

        ActionHandlers.ClaimRouteHandler claimRouteH2 = (a, b) -> {
            drawTicketsHandler.set(null);
            drawCardsHandler.set(null);
            claimRouteHandler.set(null);
            claimRouteH.onClaimRoute(a, b);
        };

        ActionHandlers.DrawCardHandler cardH3 = (a) -> drawCard(cardHandler);

        ActionHandlers.DrawCardHandler cardH2 = (a) -> {
            drawTicketsHandler.set(null);
            drawCardsHandler.set(cardH3);
            claimRouteHandler.set(null);
            cardHandler.onDrawCards(a);
        };

        ActionHandlers.DrawTicketsHandler ticketH2 = () -> {
            drawTicketsHandler.set(null);
            drawCardsHandler.set(null);
            claimRouteHandler.set(null);
            ticketsHandler.onDrawTickets();
        };

        //Handlers are null if the action is impossible
        if (observableGameState.canDrawTickets()) drawTicketsHandler.set(ticketH2);

        if (observableGameState.canDrawCards()) drawCardsHandler.set(cardH2);

        claimRouteHandler.set(claimRouteH2);


    }

    /**
     * a method named chooseTickets, which takes as arguments a multiset containing five or three tickets that the player can choose and a ticket choice manager — of the ChooseTicketsHandler type -,allowing the player to make his choice; once this has been confirmed, the choice handler is called with this choice as an argument,
     * @param tickets a multiset containing five or three tickets that the player can choose
     * @param ticketsHandler a ticket choice manager
     */
    public void chooseTickets(SortedBag<Ticket> tickets, ActionHandlers.ChooseTicketsHandler ticketsHandler) {
        assert Platform.isFxApplicationThread();
        VBox vBox = new VBox();


        int ticketsToTake = tickets.size() - Constants.DISCARDABLE_TICKETS_COUNT;
        TextFlow textFlow = fromStringFlow(String.format(StringsFr.CHOOSE_TICKETS, ticketsToTake, StringsFr.plural(Constants.IN_GAME_TICKETS_COUNT)));

        ObservableList<Ticket> listTickets = FXCollections.observableList(tickets.toList());
        ListView<Ticket> listView = new ListView<>(listTickets);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ObservableList<Ticket> chosenTickets = listView.getSelectionModel().getSelectedItems();
        IntegerBinding selectedItemsSize = Bindings.size(chosenTickets);

        Button button = new Button(StringsFr.CHOOSE);
        button.disableProperty().bind(Bindings.greaterThan(ticketsToTake, selectedItemsSize));
        button.setOnAction(e -> {
            button.getScene().getWindow().hide();
            ticketsHandler.onChooseTickets(SortedBag.of(chosenTickets));
        });

        vBox.getChildren().addAll(List.of(textFlow, listView, button));

        Scene scene = createScene(vBox);

        createStage(scene, StringsFr.TICKETS_CHOICE);
    }

    /**
     * a method named drawCard, which takes as argument a card draw manager — of the DrawCardHandler type - and which allows the player to choose a wagon / locomotive card, either one of the five whose face is visible, or that of the top of the pickaxe; once the player has clicked on one of these cards, the manager is called with the player's choice; this method is intended to be called when the player has already drawn a first card and must now draw the second,
     * @param cardsHandler the draw cards handler
     */
    public void drawCard(ActionHandlers.DrawCardHandler cardsHandler) {
        assert Platform.isFxApplicationThread();
        ActionHandlers.DrawCardHandler cardH2 = (a) -> {
            drawTicketsHandler.set(null);
            drawCardsHandler.set(null);
            claimRouteHandler.set(null);
            cardsHandler.onDrawCards(a);
        };
        drawCardsHandler.set(cardH2);
    }

    /**
     *a method named chooseClaimCards, which takes as arguments a list of multiple sets of cards, which are the initial cards it can use to grab a route, and a card choice manager — of type ChooseCardsHandler -, and which opens a window allowing the player to make his choice; once this has been done and confirmed, the choice handler is called with the player's choice as an argument,
     * @param initialCards a list of multiple sets of cards,
     * @param chooseCardsHandler a card choice manager,
     */
    public void chooseClaimCards(List<SortedBag<Card>> initialCards, ActionHandlers.ChooseCardsHandler chooseCardsHandler){
        assert Platform.isFxApplicationThread();
        VBox vBox = new VBox();




        TextFlow textFlow = fromStringFlow(StringsFr.CHOOSE_CARDS);


        ListView<SortedBag<Card>> cardListView = createListViewBagCard(initialCards);
        ObservableList<SortedBag<Card>> chosenCard = cardListView.getSelectionModel().getSelectedItems();
        IntegerBinding selectedItemsSize = Bindings.size(cardListView.getSelectionModel().getSelectedItems());


        Button button = new Button(StringsFr.CHOOSE);
        button.disableProperty().bind(Bindings.notEqual(selectedItemsSize, 1));
        button.setOnAction(e -> {
            button.getScene().getWindow().hide();
            chooseCardsHandler.onChooseCards(chosenCard.get(0));
        });

        vBox.getChildren().addAll(textFlow, cardListView, button);

        Scene scene = createScene(vBox);

        createStage(scene, StringsFr.CARDS_CHOICE);
    }

    /**
     * a method, named for example chooseAdditionalCards, which takes as arguments a list of multisets of cards, which are the additional cards that it can use to seize a tunnel and a manager of choice of cards — of type ChooseCardsHandler -, and which opens a window allowing the player to make his choice; once this has been done and confirmed, the choice handler is called with the player's choice as an argument.
     * @param additionalCards a list of multisets of cards
     * @param cardsHandler a card choice manager
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> additionalCards, ActionHandlers.ChooseCardsHandler cardsHandler){
        assert Platform.isFxApplicationThread();
        VBox vBox = new VBox();



        TextFlow textFlow = fromStringFlow(StringsFr.CHOOSE_ADDITIONAL_CARDS);

        ListView<SortedBag<Card>> cardListView = createListViewBagCard(additionalCards);
        ObservableList<SortedBag<Card>> chosenCard = cardListView.getSelectionModel().getSelectedItems();
        IntegerBinding selectedItemsSize = Bindings.size(cardListView.getSelectionModel().getSelectedItems());


        Button button = new Button(StringsFr.CHOOSE);
        button.disableProperty().bind(Bindings.greaterThan(selectedItemsSize, 1));
        button.setOnAction(e -> {
            button.getScene().getWindow().hide();
            if(!chosenCard.isEmpty())
                cardsHandler.onChooseCards(chosenCard.get(0));
            else
                cardsHandler.onChooseCards(SortedBag.of());
        });

        vBox.getChildren().addAll(textFlow, cardListView, button);

        Scene scene = createScene(vBox);
        createStage(scene, StringsFr.CARDS_CHOICE);
    }

    private ListView<SortedBag<Card>> createListViewBagCard(List<SortedBag<Card>> cards) {
        ObservableList<SortedBag<Card>> cardsList = FXCollections.observableList(cards);
        ListView<SortedBag<Card>> cardListView = new ListView<>(cardsList);
        cardListView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));
        return cardListView;
    }

    private void createStage(Scene scene, String title) {
        assert Platform.isFxApplicationThread();
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.initOwner(mainStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setOnCloseRequest(Event::consume);
        stage.show();
    }

    private Scene createScene(VBox vbox) {
        assert Platform.isFxApplicationThread();
        Scene scene = new Scene(vbox);
        scene.getStylesheets().add("chooser.css");
        return scene;
    }

    private TextFlow fromStringFlow(String s) {
        Text text = new Text(s);
        return new TextFlow(text);
    }
}