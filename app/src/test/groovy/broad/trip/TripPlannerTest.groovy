package broad.trip

import broad.mbta.MbtaRoute
import broad.mbta.MbtaStop

import spock.lang.Specification

class TripPlannerTest extends Specification {

    def setup() {
        TripPlanner.buildGraph(mockStopsByRoute())
    }

    void "buildGraph creates a sane map of a system"() {
        expect:
        TripPlanner.nodeById.size() == 4
        TripPlanner.nodeById['Blahaj'].indexByRoute*.value == [1, 0]
        with(TripPlanner.nodeById['Capybara'].indexByRoute*.key.first()) {
            id == 'route0'
            bottom.id == 'Aardvark'
            top.id == 'Capybara'
            transferStops*.id == ['Blahaj']
        }
    }

    void "planTrip connects two stops on the same route"() {
        setup:
        MbtaRoute r0 = new MbtaRoute(id: 'route0', attributes: [long_name: 'Route Zero', type: 0])
        MbtaStop s0 = mockMbtaStop('Aardvark', r0)
        MbtaStop s1 = mockMbtaStop('Capybara', r0)

        expect:
        with(TripPlanner.planTrip(s0, s1).moves) {
            destination*.id == ['Capybara']
            route*.id == ['route0']
            steps == [2]
        }
    }

    void "planTrip connects two stops on different routes"() {
        setup:
        MbtaRoute r0 = new MbtaRoute(id: 'route0', attributes: [long_name: 'Route Zero', type: 0])
        MbtaStop s0 = mockMbtaStop('Capybara', r0)
        MbtaRoute r1 = new MbtaRoute(id: 'route1', attributes: [long_name: 'Route One', type: 1])
        MbtaStop s1 = mockMbtaStop('Dunkleosteus', r1)

        expect:
        with(TripPlanner.planTrip(s0, s1).moves) {
            destination*.id == ['Blahaj', 'Dunkleosteus']
            route*.id == ['route0', 'route1']
            steps == [-1, 1]
        }
    }

    void "planTrip connects two stops when one is a transfer"() {
        setup:
        MbtaRoute r0 = new MbtaRoute(id: 'route0', attributes: [long_name: 'Route Zero', type: 0])
        MbtaStop s0 = mockMbtaStop('Blahaj', r0)
        MbtaRoute r1 = new MbtaRoute(id: 'route1', attributes: [long_name: 'Route One', type: 1])
        MbtaStop s1 = mockMbtaStop('Dunkleosteus', r1)

        expect:
        with(TripPlanner.planTrip(s0, s1).moves) {
            destination*.id == ['Dunkleosteus']
            route*.id == ['route1']
            steps == [1]
        }
    }

    private Map mockStopsByRoute() {
        MbtaRoute r0 = new MbtaRoute(id: 'route0', attributes: [long_name: 'Route Zero', type: 0])
        MbtaRoute r1 = new MbtaRoute(id: 'route1', attributes: [long_name: 'Route One', type: 1])
        return [
            (r0): [
                mockMbtaStop('Aardvark', r0),
                mockMbtaStop('Blahaj', r0),
                mockMbtaStop('Capybara', r0),
            ],
            (r1): [
                mockMbtaStop('Blahaj', r1),
                mockMbtaStop('Dunkleosteus', r1),
            ],
        ]
    }

    private MbtaStop mockMbtaStop(String id, MbtaRoute route) {
        return new MbtaStop(id: id, attributes: [name: id], route)
    }

}
