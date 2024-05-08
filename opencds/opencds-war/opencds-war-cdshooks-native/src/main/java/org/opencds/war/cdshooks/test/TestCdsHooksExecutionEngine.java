package org.opencds.war.cdshooks.test;

import org.opencds.hooks.engine.api.CdsHooksEvaluationContext;
import org.opencds.hooks.engine.api.CdsHooksExecutionEngine;
import org.opencds.hooks.model.request.CdsRequest;
import org.opencds.hooks.model.response.Card;
import org.opencds.hooks.model.response.CdsResponse;
import org.opencds.hooks.model.response.Indicator;
import org.opencds.hooks.model.response.Source;
import org.opencds.hooks.model.response.Suggestion;

public class TestCdsHooksExecutionEngine implements CdsHooksExecutionEngine {
    @Override
    public CdsResponse evaluate(CdsRequest cdsRequest, CdsHooksEvaluationContext evaluationContext) {
        CdsResponse response = new CdsResponse();
        Card card = new Card();
        card.setSummary("summary for test-cdshooks");
        card.setIndicator(Indicator.INFO);
        Source source = new Source();
        source.setLabel("source for test-cdshooks");
        card.setSource(source);
        Suggestion suggestion = new Suggestion();
        suggestion.setLabel("suggestion for test-cdshooks");
        card.addSuggestion(suggestion);
        response.addCard(card);
        return response;
    }
}
