package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * the class MapViewCreator has to create the view of the map,
 */
final class MapViewCreator {

    @FunctionalInterface
    public interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options, ActionHandlers.ChooseCardsHandler handler);
    }


    /**
     * contains a single public method, named for example createMapView and allowing to create the map view. It takes three arguments, which are
     * @param observableGameState the observable game state, of type ObservableGameState,
     * @param handlerProperty a property containing the action handler to use when the player wants to seize a route, of type ObjectProperty <ClaimRouteHandler>,
     * @param cardChooser a “card selector”, of the CardChooser type,
     * @return the view of the map,
     */
    public static Node createMapView(ObservableGameState observableGameState, ObjectProperty<ActionHandlers.ClaimRouteHandler> handlerProperty, CardChooser cardChooser) {
        Pane pane = new Pane();
        pane.getStylesheets().addAll("map.css", "colors.css");
        pane.getChildren().add(new ImageView());


        //Creation of the routes
        for (Route route : ChMap.routes()) {
            Group road = new Group();
            road.setId(route.id());
            road.getStyleClass().addAll(route.toString(), route.level().toString(), (route.color() == null) ? "NEUTRAL" : route.color().name());
            road.getStyleClass().add("route");


            //Creation of each route it depends of it's size
            for (int i = 1; i <= route.length(); i++) {
                Group box = new Group();
                box.setId(route.id() + "_" + i);

                Rectangle way = new Rectangle(36, 12);
                way.getStyleClass().addAll("track", "filled");

                Group car = new Group();
                car.getStyleClass().add("car");
                Rectangle car1 = new Rectangle(36, 12);
                car1.getStyleClass().add("filled");
                Circle car2 = new Circle(12, 6, 3), car3 = new Circle(24, 6, 3);
                car.getChildren().addAll(car1, car2, car3);
                box.getChildren().addAll(way, car);

                //add each box to the route
                road.getChildren().add(box);
            }

            //links and listeners
            observableGameState.getRouteOwners(ChMap.routes().indexOf(route)).addListener((o, v, nv) -> {
                String s = nv.name();
                if (v != nv) {
                    road.getStyleClass().add(s);
                }
            });
            road.disableProperty().bind(handlerProperty.isNull().or(observableGameState.getCanTakeRoute(ChMap.routes().indexOf(route)).not()));


            //event management
            road.setOnMouseClicked(e ->
            {
                List<SortedBag<Card>> a = observableGameState.possibleClaimCards(route);
                if (a.size() == 1)
                    handlerProperty.get().onClaimRoute(route, a.get(0));
                else {
                    if (a.size() > 1) {
                        ActionHandlers.ChooseCardsHandler chooseCardsH = chosenCards -> handlerProperty.get().onClaimRoute(route, chosenCards);
                        cardChooser.chooseCards(a, chooseCardsH);
                    }
                }
            });

            //add each route to the pane
            pane.getChildren().add(road);


        }

        return pane;
    }


}
