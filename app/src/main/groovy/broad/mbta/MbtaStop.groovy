package broad.mbta

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Model a stop along a route in the MBTA system. Parses stop object received from MBTA API.
 */
@EqualsAndHashCode(includes=['id'])
@ToString
class MbtaStop {

    /**
     * Asserts that a stop response is a parent station.
     */
    static boolean isValid(Map resource) {
        !resource.relationships.facilities.parent_station?.data
    }

    /** The MBTA stop id. */
    String id

    /** The MBTA name. */
    String name

    /** The route on which this stop occurs. */
    MbtaRoute route

    MbtaStop(Map resource, MbtaRoute route) {
        id = resource.id
        name = resource.attributes.name
        this.route = route
    }

}
