package org.opencds.service

import org.opencds.service.util.OpencdsHooksClient
import spock.lang.Specification

class CorsFunctionalSpec extends Specification {
    static OpencdsHooksClient client = new OpencdsHooksClient()

    def 'call cds-service endpoint'() {
        when:
        var result = client.options()
        println result

        then:
        notThrown(Exception)
        result.get("Access-Control-Allow-Origin") == ['*']
        result.get("Access-Control-Allow-Headers") == ['origin, content-type, accept, authorization']
        result.get("Access-Control-Allow-Methods") == ['GET, POST, OPTIONS']
    }
}
