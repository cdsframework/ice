package org.opencds.common.structures;

import java.net.URI;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class EvaluationRequestDataItem
{
    protected String focalPersonId;
    protected Date evalTime;
    protected String clientLanguage;
    protected String clientTimeZoneOffset;
    protected String externalFactModelSSId;
    protected String inputItemName;
    protected String inputContainingEntityId;
    protected String interactionId;
    protected URI serverUri;
}
