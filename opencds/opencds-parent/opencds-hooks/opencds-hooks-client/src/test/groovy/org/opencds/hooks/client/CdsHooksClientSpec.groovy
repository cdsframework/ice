package org.opencds.hooks.client

import spock.lang.Specification

class CdsHooksClientSpec extends Specification {

    CdsHooksClient client

    def setup() {
        client = new CdsHooksClient()
    }

    def 'test client'() {
        given:
        /*
         * what does a CDS Call need?
         * - which hook we're calling:
         *   - hook (get from cds-services)
         *   - hookInstance
         * - FHIR Server info (if any):
         *   - fhirServer
         *   - fhirAuthorization
         * - user
         * - context
         *   - what context info
         * - prefetch resources
         *   - list of resources
         */
        true

        when:
        true

        then:
        true
    }
}
