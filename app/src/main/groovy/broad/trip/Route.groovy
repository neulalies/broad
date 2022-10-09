package broad.trip

import broad.mbta.MbtaRoute
import broad.mbta.MbtaStop

class Route {

    /** The MBTA route id. */
    String id

    /** The readable name. */
    String name

    /** The first stop on the line. */
    StopNode bottom

    /** The last stop on the line. */
    StopNode top

    /** All stops on this route which touch other routes. */
    Set<StopNode> transferStops

    Route(MbtaRoute route, StopNode firstStop, StopNode lastStop, Collection<StopNode> transferStops) {
        id = route.id
        name = route.name
        bottom = firstStop
        top = lastStop
        this.transferStops = transferStops as Set
    }

}
