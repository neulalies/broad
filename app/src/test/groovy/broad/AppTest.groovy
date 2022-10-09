package broad

import spock.lang.Specification

class AppTest extends Specification {

    def "showRoutes runs"() {
        setup:
        def app = new App()

        when:
        app.showRoutes()

        then:
        notThrown Exception
    }

    def "showRouteStats runs"() {
        setup:
        def app = new App()

        when:
        app.showRouteStats()

        then:
        notThrown Exception
    }

}
