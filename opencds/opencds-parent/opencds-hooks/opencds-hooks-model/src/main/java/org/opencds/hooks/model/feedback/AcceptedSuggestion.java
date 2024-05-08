package org.opencds.hooks.model.feedback;

public class AcceptedSuggestion {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Suggestion{" +
                "id='" + id + '\'' +
                '}';
    }
}
