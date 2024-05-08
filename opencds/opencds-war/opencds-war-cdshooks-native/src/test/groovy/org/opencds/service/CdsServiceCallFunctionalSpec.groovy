package org.opencds.service

import org.opencds.hooks.lib.json.JsonUtil
import org.opencds.hooks.model.context.WritableHookContext
import org.opencds.hooks.model.json.CdsHooksJsonUtil
import org.opencds.hooks.model.request.WritableCdsRequest
import org.opencds.hooks.model.response.CdsResponse
import org.opencds.service.util.OpencdsHooksClient
import spock.lang.Specification

class CdsServiceCallFunctionalSpec extends Specification {

    static JsonUtil jsonUtil
    static OpencdsHooksClient client

    def setupSpec() {
        jsonUtil = new CdsHooksJsonUtil()
        client = new OpencdsHooksClient()
    }

    def 'call cds-service endpoint'() {
        given:
        def request = new WritableCdsRequest()
        request.setHookInstance(UUID.randomUUID().toString())
        request.setHook('patient-view')

        def context = new WritableHookContext()
        context.add('userId', 'user123')
        context.add('patientId', '12345')
        context.add('encounterId', '54623')
        request.setContext(context)

        println jsonUtil.toJson(request)

        when:
        var result = client.call('cds-hooks-test', jsonUtil.toJson(request))
        println result

        then:
        notThrown(Exception)
        result.getRight() == '{"cards":[{"summary":"summary for test-cdshooks","indicator":"info","source":{"label":"source for test-cdshooks"},"suggestions":[{"label":"suggestion for test-cdshooks","isRecommended":false,"actions":[]}]}]}'

        when:
        CdsResponse response = jsonUtil.fromJson(result.getRight(), CdsResponse.class)

        then:
        notThrown(Exception)
        result.getLeft().get("Access-Control-Allow-Origin") == ['*']
        result.getLeft().get("Access-Control-Allow-Headers") == ['origin, content-type, accept, authorization']
        result.getLeft().get("Access-Control-Allow-Methods") == ['GET, POST, OPTIONS']
        response
        response.cards
        response.cards.size() == 1
        response.cards[0].summary == 'summary for test-cdshooks'
        response.cards[0].indicator
        response.cards[0].indicator.value == 'info'
        response.cards[0].source
        response.cards[0].source.label == 'source for test-cdshooks'
        response.cards[0].suggestions
        response.cards[0].suggestions.size() == 1
        response.cards[0].suggestions[0].label == 'suggestion for test-cdshooks'
        !response.cards[0].suggestions[0].recommended
        response.cards[0].suggestions[0].actions == []

    }
}
