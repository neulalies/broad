package broad.trip

/**
 * A partial or completed trip to a destination. Stores current location and a history of previous moves.
 * Implements breadth-first searching with `expand()`.
 */
class Trip {

    /** Current location of the trip cursor. */
    StopNode location

    /** Desired final stop of the trip. */
    StopNode destination

    /** Each step (move along a single route to a transfer station or destination) taken so far. */
    List<Move> moves = []

    Trip(StopNode location, StopNode destination) {
        this.location = location
        this.destination = destination
    }

    private Trip(StopNode location, StopNode destination, List<Move> moves) {
        this.location = location
        this.destination = destination
        this.moves = moves
    }

    private Trip withMove(Move move) {
        return new Trip(move.destination, this.destination, this.moves + move)
    }

    /**
     * List the trips possible after a single valid move from the current location.
     * A valid move is from the current location to the destination or a transfer stop
     * unvisited during this trip.
     */
    List<Trip> expand() {
        return getValidMoves().collect { withMove(it) }
    }

    /**
     * Determine if we are there yet.
     */
    boolean isArrived() {
        return location == destination
    }

    /**
     * Format a readable string describing each move.
     */
    List<String> getMoveDescriptions() {
        return moves.collect { it.describe() }
    }

    /**
     * Sum the total stops traveled.
     */
    int countStops() {
        return moves.sum { Math.abs(it.steps) }
    }

    private List<Move> getValidMoves() {
        if (isArrived()) {
            return []
        }

        // Which routes are as yet untraveled?
        Route currentRoute = moves ? moves.last().route : null
        Set availableRoutes = location.routes - currentRoute

        // If one of the routes open to us is shared with the destination, move to destination
        Collection routesInCommon = availableRoutes.intersect(destination.routes)
        if (routesInCommon) {
            return [new Move(location, destination, routesInCommon.first())]
        }

        // Which unvisited transfer stations can we reach on the open routes?
        List touchedStops = moves*.destination
        Set availableTransferStops = availableRoutes.collectMany { it.transferStops } as Set
        Set validTransferStops = availableTransferStops - touchedStops
        return validTransferStops.collect { StopNode target ->
            // Move to an open transfer station
            Route route = availableRoutes.intersect(target.routes).first()
            return new Move(location, target, route)
        }
    }

}
