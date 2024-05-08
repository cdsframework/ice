package org.opencds.hooks.model.feedback;

public class OverrideReason {
    private String code;
    private String system;
    private String userComment;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    @Override
    public String toString() {
        return "OverrideReason{" +
                "code='" + code + '\'' +
                ", system='" + system + '\'' +
                ", userComment='" + userComment + '\'' +
                '}';
    }
}
