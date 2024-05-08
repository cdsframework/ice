package org.opencds.hooks.model.feedback;

import java.util.List;

public class Feedback {
    private String card;
    private String outcome;
    private List<AcceptedSuggestion> acceptedSuggestions;
    private OverrideReason overrideReason;
    private String outcomeTimestamp;

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public List<AcceptedSuggestion> getAcceptedSuggestions() {
        return acceptedSuggestions;
    }

    public void setAcceptedSuggestions(List<AcceptedSuggestion> acceptedSuggestions) {
        this.acceptedSuggestions = acceptedSuggestions;
    }

    public OverrideReason getOverrideReason() {
        return overrideReason;
    }

    public void setOverrideReason(OverrideReason overrideReason) {
        this.overrideReason = overrideReason;
    }

    public String getOutcomeTimestamp() {
        return outcomeTimestamp;
    }

    public void setOutcomeTimestamp(String outcomeTimestamp) {
        this.outcomeTimestamp = outcomeTimestamp;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "card='" + card + '\'' +
                ", outcome='" + outcome + '\'' +
                ", acceptedSuggestions=" + acceptedSuggestions +
                ", overrideReason=" + overrideReason +
                ", outcomeTimestamp='" + outcomeTimestamp + '\'' +
                '}';
    }
}
