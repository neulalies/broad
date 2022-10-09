package broad

import broad.mbta.MbtaRequest
import broad.mbta.MbtaRoute
import broad.mbta.MbtaStop
import broad.trip.StopNode
import broad.trip.Trip
import broad.trip.TripPlanner

import java.util.Scanner

class App {

    private static final String OUTPUT_RESULT_DECORATION = '  ~ '
    private static final String OUTPUT_MESSAGE_DECORATION = '\n  '

    /**
     * The application entry point.
     */
    static void main(String[] args) {
        new App().with {
            printMessage("Welcome to Lily's infallible MBTA subway guide (version 0.1)! Caveat emptor.")
            showRoutes()
            showRouteStats()
            doTripPlanner()
        }
    }

    /**
     * Display all light and heavy rail routes in the MBTA transit network.
     */
    void showRoutes() {
        List routes = getRoutes()
        printMessage("Rail routes in the MBTA network:")
        routes.each { MbtaRoute route ->
            printResult(route.name)
        }
    }

    /**
     * Display longest and shortest routes, and list junction stops.
     */
    void showRouteStats() {
        Map stopsByRoute = getStopsByRoute()

        Map.Entry longestRouteStops = stopsByRoute.max { Map.Entry a, Map.Entry b ->
            a.value.size() <=> b.value.size()
        }
        Map.Entry shortestRouteStops = stopsByRoute.min { Map.Entry a, Map.Entry b ->
            a.value.size() <=> b.value.size()
        }

        Map routesByTransferStop = getRoutesByTransferStop()

        printMessage("Longest route:")
        printResult("${longestRouteStops.key.name} (${longestRouteStops.value.size()} stops)")
        printMessage("Shortest route:")
        printResult("${shortestRouteStops.key.name} (${shortestRouteStops.value.size()} stops)")
        printMessage("Transfer stops:")
        routesByTransferStop.each { MbtaStop stop, List routes ->
            printResult("${stop.name}: ${routes*.name.join(', ')}")
        }
    }

    /**
     * Prompt user for start and end stops, then display a travel plan.
     */
    void doTripPlanner() {
        printMessage("Plan a rail trip in sunny Boston!")
        printMessage("Where are you starting?")
        MbtaStop origin = readInputStop()
        printMessage("Okay, you're starting from ${origin.name}. Where are you headed?")
        MbtaStop destination = readInputStop()
        if (origin == destination) {
            printMessage("You're already there!")
            return
        }
        printMessage("Planning a trip from ${origin.name} to ${destination.name}...")
        TripPlanner.buildGraph(getStopsByRoute())
        Trip trip = TripPlanner.planTrip(origin, destination)
        if (!trip) {
            printMessage("I'm sorry; I couldn't find a path between those stops.")
        } else {
            showTrip(trip)
        }
    }

    private void showTrip(Trip trip) {
        printResult("Starting from ${trip.moves[0].origin.name}...")
        trip.getMoveDescriptions().each { String description ->
            printResult(description)
        }
        printResult("You'll arrive at ${trip.destination.name} after traveling ${trip.countStops()} stops!")
    }

    private MbtaStop readInputStop() {
        // TODO: seems to add a weird "80% Executing" line before input prompt; odd bug!
        Scanner scanner = new Scanner(System.in)
        MbtaStop stop
        while (!stop) {
            String input = scanner.nextLine()
            if (input == 'Q' || input == 'q') {
                printMessage("Goodbye~")
                System.exit(0)
            }
            if (input == 'L' || input == 'l' || input.toLowerCase() == 'list') {
                showAllStops()
                continue
            }
            stop = findStop(input)
            if (!stop) {
                printMessage("I don't recognize that stop because I'm inadequate. Please type a stop name, or type 'L' or 'list' for a list..")
            }
        }
        return stop
    }

    // WARNING: the stop here will not necessarily have the right route (if it is a transfer stop).
    private MbtaStop findStop(String name) {
        return getStopById().find { String id, MbtaStop stop ->
            name == stop.name
        }?.value
    }

    private void showAllStops() {
        // ASSUMPTION! default ordering is the real order of stops on each route
        getStopsByRoute().each { MbtaRoute route, List stops ->
            printResult("${route.name}:")
            stops.each { MbtaStop stop ->
                printResult("~ ${stop.name}")
            }
        }
    }

    private List<MbtaRoute> getRoutes() {
        return new MbtaRequest('/routes')
            .fetch()
            .findResults { Map route ->
                if (MbtaRoute.isValid(route)) {
                    return new MbtaRoute(route)
                }
            }
    }

    private List<MbtaStop> getStops(MbtaRoute route) {
        return new MbtaRequest('/stops', 'filter[route]': route.id)
            .fetch()
            .findResults { Map stop ->
                if (MbtaStop.isValid(stop)) {
                    return new MbtaStop(stop, route)
                }
            }
    }

    private Map<MbtaRoute, List<MbtaStop>> getStopsByRoute() {
        return getRoutes()
            .collectEntries { MbtaRoute route ->
                [route, getStops(route)]
            }
    }

    private Map<MbtaStop, List<MbtaRoute>> getRoutesByTransferStop() {
        return getStopsByRoute()
            .collectMany { MbtaRoute route, List stops ->
                // flatten to a simple list of stops
                stops
            }.groupBy { MbtaStop stop ->
                // group stops which are duplicates
                stop
            }.findAll { MbtaStop stop, List stops ->
                // filter to route,stop pairs which share a stop with others
                stops.size() > 1
            }.collectEntries { MbtaStop stop, List copies ->
                // reformat to a Map of [stop: List<route>]
                List routes = copies.collect { MbtaStop copy ->
                    copy.route
                }
                [stop, routes]
            }
    }

    private Map<String, MbtaStop> getStopById() {
        return getStopsByRoute()
            .collectMany { MbtaRoute route, List stops ->
                stops
            }.collectEntries { MbtaStop stop ->
                [stop.id, stop]
            }
    }

    private void printResult(String text) {
        println "${OUTPUT_RESULT_DECORATION}${text}"
    }

    private void printMessage(String text) {
        println "${OUTPUT_MESSAGE_DECORATION}${text}"
    }

}
