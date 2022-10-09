package broad.trip

import broad.mbta.MbtaStop

import groovy.transform.ToString

/**
 * An internal representation of an MBTA stop.
 */
@ToString
class StopNode {

    /** The MBTA stop id. */
    String id

    /** The readable name. */
    String name

    /** The position of this stop along each of the routes it touches. */
    Map<Route, Integer> indexByRoute = [:]

    StopNode(MbtaStop stop) {
        id = stop.id
        name = stop.name
    }

    /**
     * List the routes accessible from this stop.
     */
    Set getRoutes() {
        return indexByRoute.keySet() as Set
    }
}
