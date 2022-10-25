package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

/**
 * a Trail from a station to another
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */
public final class Trail {

    private final int length;
    private final Station stationStart;
    private final Station stationEnd;
    private final List<Route> routes;
    private static final Trail EMPTYTRAIL = new Trail(null, null, List.of(), 0);

    private Trail(Station stationStart, Station stationEnd, List<Route> routes, int length) {
        this.stationStart = stationStart;
        this.stationEnd = stationEnd;
        this.routes = routes;
        this.length = length;
    }

    /**
     * Calculates the longest trail possible with the available routes
     * @param routes a list of routes
     * @return the longest trail possible
     */
    public static Trail longest(List<Route> routes){
        if(routes.isEmpty()) return EMPTYTRAIL;
        List<Trail> trails = new ArrayList<>();
        Trail maxTrail = EMPTYTRAIL;
        for(Route route : routes){
            Trail firstTrail = new Trail(route.station1(), route.station2(), List.of(route), route.length());
            maxTrail = firstTrail.length > maxTrail.length ? firstTrail : maxTrail;
            Trail secondTrail = new Trail(route.station2(), route.station1(), List.of(route), route.length());
            maxTrail = secondTrail.length > maxTrail.length ? secondTrail : maxTrail;
            trails.add(firstTrail);
            trails.add(secondTrail);
        }
        while(!trails.isEmpty()){
            List<Trail> trails2 = new ArrayList<>();
            for(Trail trail : trails){
                for(Route route : routes){
                    if(!trail.routes.contains(route)){
                        if(route.station1() == trail.station2()) {
                                List <Route> newRoutes = new ArrayList<>(trail.routes);
                                newRoutes.add(route);
                                Trail newTrail = new Trail(trail.station1(), route.station2(), newRoutes, trail.length + route.length());
                                trails2.add(newTrail);
                                maxTrail = newTrail.length > maxTrail.length ? newTrail : maxTrail;
                        }
                        if(route.station2() == trail.station2()) {
                                 List <Route> newRoutes = new ArrayList<>(trail.routes);
                                 newRoutes.add(route);
                                 Trail newTrail = new Trail(trail.station1(), route.station1(), newRoutes, trail.length + route.length());
                                 trails2.add(newTrail);
                                 maxTrail = newTrail.length > maxTrail.length ? newTrail : maxTrail;
                        }
                    }
                }
                trails = trails2;
            }
        }
        return maxTrail;
    }

    /**
     *
     * @return the length of the trail
     */
    public int length(){
        return length;
    }

    /**
     *
     * @return the station at the start of the trail
     */
    public Station station1(){
        if(length == 0) return null;
        else return stationStart;
    }

    /**
     *
     * @return the station at the end of the trail
     */
    public Station station2(){
        if(length == 0) return null;
        else return stationEnd;
    }

    @Override
    public String toString(){
        if(this.equals(EMPTYTRAIL)) return "empty (0)";
        List<Station> stations = new ArrayList<>();
        stations.add(stationStart);
        for(Route route : routes){
            stations.add(route.stationOpposite(stations.get(routes.indexOf(route))));
        }
        List<String> stationNames = new ArrayList<>();
        for (Station station : stations){
            stationNames.add(station.name());
        }
        return String.join(" - ", stationNames) + " (" + length + ")";
    }
}
