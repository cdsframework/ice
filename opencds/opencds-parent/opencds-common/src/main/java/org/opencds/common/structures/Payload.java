package org.opencds.common.structures;

import java.util.Date;

public class Payload {
    private final String payload;
    private final Date evalTime;
    
    public Payload(String payload, Date evalTime) {
        this.payload = payload;
        this.evalTime = evalTime;
    }

    public String getPayload() {
        return payload;
    }

    public Date getEvalTime() {
        return evalTime;
    }
}
