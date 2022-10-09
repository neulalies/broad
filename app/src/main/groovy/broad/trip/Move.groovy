package broad.trip

/**
 * Represents a ride along a single line, from one stop to another, as part of a trip.
 */
class Move {

    /** The stop from which the move begins. */
    StopNode origin

    /** The stop at which the move ends. */
    StopNode destination

    /** The route along which the move occurs. */
    Route route

    /** How many stops are passed through during the move. Signed; negative steps represent travel toward the zero-index 'bottom'. */
    int steps

    Move(StopNode origin, StopNode destination, Route route) {
        this.origin = origin
        this.destination = destination
        this.route = route
        steps = destination.indexByRoute[route] - origin.indexByRoute[route]
    }

    /**
     * Describe the move readably.
     */
    String describe() {
        int countStops = Math.abs(steps)
        StopNode terminus = steps < 0 ? route.bottom : route.top
        return "Ride the ${route.name} toward ${terminus.name} to ${destination.name} (${countStops} stop${countStops == 1 ? '' : 's'})"
    }

}
