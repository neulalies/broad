package broad.mbta

import groovyx.net.http.ContentType
import groovy.json.JsonSlurper
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import groovyx.net.http.URIBuilder

/**
 * Configures a request to a provided MBTA API endpoint, allowing fetches of the resource.
 * @see <a href="https://api-v3.mbta.com/docs/swagger/index.html">MBTA API</a>
 */
class MbtaRequest {

	private static final String ROOT_URL = 'https://api-v3.mbta.com'

	private static final String API_KEY = '8935723061a54d10812d2597aa36ccef'

	private final String url

	MbtaRequest(Map _query = [:], String _path) {
		URIBuilder uriBuilder = new URIBuilder(ROOT_URL)
        uriBuilder.with {
			scheme = 'https'
            path = _path
            query = _query + [api_key: API_KEY]
        }
        url = uriBuilder.toString()
	}

	/**
	 * Requests the JSON resource. Result may be a List or a Map.
	 * @return List or Map of the JSON response
	 */
	def fetch() {
        try {
            HttpResponseDecorator response = new RESTClient(url).get(contentType: ContentType.JSON)
            if (!response.success) {
                throw new RuntimeException("Failed to connect to MBTA server")
            }
            def json = response.getData()
            return json.data
        } catch (Exception e) {
            println "ERROR: Could not connect to the MBTA server at https://api-v3.mbta.com/ ."
            println "We regret to have failed to serve you. Please try again later."
            System.exit(0)
        }
    }

}
