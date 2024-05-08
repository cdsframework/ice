package org.opencds.hooks.model.feedback;

import java.util.List;

public class FeedbackList {
    private List<Feedback> feedback;

    public List<Feedback> getFeedback() {
        return feedback;
    }

    public void setFeedback(List<Feedback> feedback) {
        this.feedback = feedback;
    }

    public boolean isNullOrEmpty() {
        return feedback == null || feedback.isEmpty();
    }

    @Override
    public String toString() {
        return "FeedbackList{" +
                "feedbackList=" + feedback +
                '}';
    }
}
