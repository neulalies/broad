package broad.mbta

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Model a route in the MBTA system. Parses route object received from MBTA API.
 */
@EqualsAndHashCode(includes=['id'])
@ToString
class MbtaRoute {

    /**
     * MBTA transit route types.
     * @see <a href="https://github.com/google/transit/blob/master/gtfs/spec/en/reference.md#routestxt">GTFS route_type</a>
     */
    static enum RouteType {
        LIGHT_RAIL(0),
        HEAVY_RAIL(1)

        /** The GTFS route_type value. */
        int index

        RouteType(int index) {
            this.index = index
        }

        /** Create a RouteType given a GTFS value. */
        static RouteType of(int index) {
            return values().find { it.index == index }
        }
    }

    /**
     * Asserts that a route response is one of the supported types (light or heavy rail).
     */
    static boolean isValid(Map resource) {
        return RouteType.of(resource.attributes.type) != null
    }

    /** The MBTA route id. */
    String id

    /** The MBTA name. */
	String name

    /** The route type. */
	RouteType type

    MbtaRoute(Map resource) {
        id = resource.id
        name = resource.attributes.long_name
        type = RouteType.of(resource.attributes.type)
    }

}
