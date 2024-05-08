package org.opencds.hooks.model.json

import org.opencds.hooks.model.feedback.Feedback
import org.opencds.hooks.model.feedback.FeedbackList
import org.opencds.hooks.model.feedback.AcceptedSuggestion
import org.opencds.hooks.model.request.CdsRequest
import org.opencds.hooks.model.request.WritableCdsRequest
import spock.lang.Specification

class CdsHooksJsonUtilSpec extends Specification {
    private static final String FEEDBACK_ACCEPTED = 'src/test/resources/feedback/accept.json'
    private static final String FEEDBACK_OVERRIDE_NO_REASONS = 'src/test/resources/feedback/override-no-reasons.json'
    private static final String FEEDBACK_OVERRIDE_WITH_REASONS = 'src/test/resources/feedback/override-with-reasons.json'

    static CdsHooksJsonUtil util

    def setupSpec() {
        util = new CdsHooksJsonUtil();
    }

    def 'test feedback - accepted suggestions'() {
        given:
        String data = new File(FEEDBACK_ACCEPTED).text

        when:
        FeedbackList fl = util.fromJson(data, FeedbackList)
        println fl

        then:
        notThrown(Exception)
    }

    def 'test feedback - override with no reasons'() {
        given:
        String data = new File(FEEDBACK_OVERRIDE_NO_REASONS).text

        when:
        FeedbackList fl = util.fromJson(data, FeedbackList)
        println fl

        then:
        notThrown(Exception)
    }

    def 'test feedback - override with reasons'() {
        given:
        String data = new File(FEEDBACK_OVERRIDE_WITH_REASONS).text

        when:
        FeedbackList fl = util.fromJson(data, FeedbackList)
        println fl

        then:
        notThrown(Exception)
    }

    def 'test serializing Feedback - accepted'() {
        given:
        FeedbackList fl = new FeedbackList(feedback: [
                new Feedback(card: UUID.randomUUID().toString(), outcome: 'accepted', acceptedSuggestions: [ new AcceptedSuggestion(id: UUID.randomUUID().toString()) ])
        ])

        when:
        String json = util.toJson(fl)

        then:
        json
    }

    def 'test serializing CdsRequest'() {
        given:
        CdsRequest request = new WritableCdsRequest()
        request.setHook('test')

        when:
        String json = util.toJson(request)

        then:
        notThrown(Exception)
        json
    }
}
