package org.opencds.common.structures;

import java.util.Date;

public class Payload {
    private final byte[] payload;
    private final Date evalTime;
    
    public Payload(byte[] payload, Date evalTime) {
        this.payload = payload;
        this.evalTime = evalTime;
    }

    public byte[] getPayload() {
        return payload;
    }

    public Date getEvalTime() {
        return evalTime;
    }
}
