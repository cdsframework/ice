package org.opencds.common.structures;

import java.net.URI;
import java.time.LocalDate;

import lombok.Builder;

@Builder
public record EvaluationRequestDataItem(String focalPersonId,
                                        LocalDate evalTime,
                                        String clientLanguage,
                                        String clientTimeZoneOffset,
                                        String externalFactModelSSId,
                                        String inputItemName,
                                        String inputContainingEntityId,
                                        String interactionId,
                                        URI serverUri)
{
}
