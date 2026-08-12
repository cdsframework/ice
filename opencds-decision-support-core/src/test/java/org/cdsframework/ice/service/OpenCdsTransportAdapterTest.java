package org.cdsframework.ice.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.cdsframework.ice.service.conversion.OpenCdsTransportAdapter;
import org.junit.jupiter.api.Test;
import org.omg.dss.EntityIdentifier;

class OpenCdsTransportAdapterTest
{
    @Test
    void createEvaluateAtSpecifiedTimeSupportsUtcMidnightAssessmentDates()
    {
        final EntityIdentifier kmEntityIdentifier = new EntityIdentifier();
        kmEntityIdentifier.setScopingEntityId("org.nyc.cir");
        kmEntityIdentifier.setBusinessId("ICE");
        kmEntityIdentifier.setVersion("1.0.0");

        final var evaluateAtSpecifiedTime = assertDoesNotThrow(
                () -> OpenCdsTransportAdapter.createEvaluateAtSpecifiedTime(kmEntityIdentifier, LocalDate.of(2026, 7, 1), "+0000",
                        new byte[] { 1, 2, 3 }));

        assertTrue(evaluateAtSpecifiedTime.getSpecifiedTime().toXMLFormat().contains("T00:00:00"));
        assertTrue(evaluateAtSpecifiedTime.getInteractionId().getSubmissionTime().toXMLFormat().contains("T"));
    }
}
