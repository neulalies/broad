package broad.trip

import broad.mbta.MbtaStop
import broad.mbta.MbtaRoute

/**
 * Builds and holds a Map graph of the stops and routes in the MBTA subway system.
 * Can also plan a trip between any two stops.
 */
class TripPlanner {

    /** A quick lookup of stop nodes by id. */
    static Map<String, StopNode> nodeById

    /**
     * Given a Map of MbtaRoute keys with Lists of MbtaStops for values, create
     * a connected graph of StopNodes representing the system, and a Map of those nodes by ID.
     */
    static void buildGraph(Map stopsByRoute) {
        // has duplicates of transfer stops (one for each route they serve)
        List allStops = stopsByRoute.collectMany { MbtaRoute route, List stops ->
            stops
        }

        // duplicates removed, but lost route data
        nodeById = allStops.collectEntries { MbtaStop stop ->
            StopNode node = new StopNode(stop)
            return [stop.id, node]
        }

        // ids of stops which straddle multiple routes (transfer stops)
        Set<String> transferStopIds = allStops.countBy { MbtaStop stop ->
            stop.id
        }.findResults { String stopId, int count ->
            count > 1 ? stopId : null
        } as Set

        stopsByRoute.each { MbtaRoute route, List stops ->
            // Configure the route object
            StopNode firstStop = nodeById[stops.first().id]
            StopNode lastStop = nodeById[stops.last().id]
            List transferStopIdsForRoute = stops*.id.intersect(transferStopIds)
            List<StopNode> transferStops = transferStopIdsForRoute.collect { nodeById[it] }
            Route _route = new Route(route, firstStop, lastStop, transferStops)

            // add route indices to stops
            stops.eachWithIndex { MbtaStop stop, int index ->
                String stopId = stop.id
                StopNode node = nodeById[stopId]
                node.indexByRoute[_route] = index
            }
        }
    }

    /**
     * Finds a Trip representing a fairly sane path between two provided stops.
     */
    static Trip planTrip(MbtaStop origin, MbtaStop destination) {
        StopNode originNode = nodeById[origin.id]
        StopNode destinationNode = nodeById[destination.id]

        List<Trip> trips = [new Trip(originNode, destinationNode)]
        Trip completeTrip
        while (!completeTrip) {
            trips = trips.collectMany { it.expand() }
            // trips.eachWithIndex { Trip trip, int i ->
            //     println("${i}~~{trip.moveDescriptions.join(' | ')}~~X")
            // }
            completeTrip = trips.find { it.arrived }
        }

        return completeTrip
    }

}
