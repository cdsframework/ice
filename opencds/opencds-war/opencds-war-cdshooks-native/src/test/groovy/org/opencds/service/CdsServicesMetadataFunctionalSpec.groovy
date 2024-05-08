package org.opencds.service

import org.opencds.hooks.lib.json.JsonUtil
import org.opencds.hooks.model.discovery.Services
import org.opencds.hooks.model.json.CdsHooksJsonUtil
import org.opencds.service.util.OpencdsHooksClient
import spock.lang.Specification

class CdsServicesMetadataFunctionalSpec extends Specification {

    static JsonUtil jsonUtil
    static OpencdsHooksClient client

    def setupSpec() {
        jsonUtil = new CdsHooksJsonUtil()
        client = new OpencdsHooksClient()

    }

    def 'get cds-services metadata'() {
        when:
        String result = client.cdsServices()

        and:
        Services services = jsonUtil.fromJson(result, Services.class)

        then:
        notThrown(Exception)
        result == '{"services":[{"hook":"patient-view","title":"Test CDS Service","description":"Test CDS Service","id":"cds-hooks-test","prefetch":{"patient":"Patient/{{context.patientId}}"}}]}'

        and:
        services
        services.services
        services.services.size() == 1
        services.services[0].hook == 'patient-view'
        services.services[0].title == 'Test CDS Service'
        services.services[0].description == 'Test CDS Service'
        services.services[0].id == 'cds-hooks-test'
        services.services[0].prefetch
        services.services[0].prefetch.containsKey('patient')
        services.services[0].prefetch.get('patient') == 'Patient/{{context.patientId}}'
    }
}
