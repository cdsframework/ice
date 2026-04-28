package org.cdsframework.cds.supportingdata;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.cdsframework.ice.dto.CodeSystem;
import org.cdsframework.ice.dto.CodeSystemConcept;
import org.cdsframework.ice.dto.CodeSystemConceptProperty;
import org.cdsframework.ice.dto.Coding;
import org.cdsframework.ice.dto.Identifier;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.supportingdata.SupplementalReasonSupport;
import org.junit.jupiter.api.Test;

class SupportedCdsListsSupplementalValidationTest
{
    private static final String CDS_VERSION = "org.nyc.cir^ICE^1.0.0";

    private static CodeSystem codeSystem(final String name, final String url, final CodeSystemConcept concept)
    {
        return new CodeSystem(name, List.of(new Identifier(null, null, "urn:ietf:rfc:3986", "urn:oid:" + url)), null, url, name,
                CDS_VERSION, null, null, null, List.of(concept));
    }

    private static CodeSystemConcept concept(final String code, final String display, final CodeSystemConceptProperty property)
    {
        return new CodeSystemConcept(code, display, property == null ? List.of() : List.of(property));
    }

    @Test
    void rejectsOutboundCodeOnSupplementalReasonCodeSystems()
    {
        final SupportedCdsLists supportedCdsLists = new SupportedCdsLists(List.of(CDS_VERSION));

        final Coding outboundCoding =
                new Coding("2.16.840.1.113883.3.795.12.100.3", null, SupplementalReasonSupport.SUPPLEMENTAL_TEXT_CODE, null);
        final CodeSystemConceptProperty outboundCode =
                new CodeSystemConceptProperty("outboundCode", outboundCoding, null, null, null);

        final CodeSystem supplementalEval =
                codeSystem(SupplementalReasonSupport.SUPPLEMENTAL_EVALUATION_REASON_CONCEPT, "2.16.840.1.113883.3.795.12.100.51",
                        concept("TEST_REASON", "Test reason", outboundCode));

        assertThrows(InconsistentConfigurationException.class,
                () -> supportedCdsLists.addSupportedCodeSystem(supplementalEval, "2.16.840.1.113883.3.795.12.100.51"));
    }

    @Test
    void validatesSupplementalReasonSupportingDataWhenComplete()
    {
        final SupportedCdsLists supportedCdsLists = new SupportedCdsLists(List.of(CDS_VERSION));

        supportedCdsLists.addSupportedCodeSystem(codeSystem("EVALUATION_REASON_CONCEPT", "2.16.840.1.113883.3.795.12.100.3",
                concept("SUPPLEMENTAL_TEXT", "Legacy eval supplemental", null)), "2.16.840.1.113883.3.795.12.100.3");
        supportedCdsLists.addSupportedCodeSystem(codeSystem("RECOMMENDATION_REASON_CONCEPT", "2.16.840.1.113883.3.795.12.100.6",
                concept("SUPPLEMENTAL_TEXT", "Legacy rec supplemental", null)), "2.16.840.1.113883.3.795.12.100.6");
        supportedCdsLists.addSupportedCodeSystem(
                codeSystem(SupplementalReasonSupport.SUPPLEMENTAL_EVALUATION_REASON_CONCEPT, "2.16.840.1.113883.3.795.12.100.51",
                        concept("COVID19_MIN_AGE", "supplemental eval", null)), "2.16.840.1.113883.3.795.12.100.51");
        supportedCdsLists.addSupportedCodeSystem(codeSystem(SupplementalReasonSupport.SUPPLEMENTAL_RECOMMENDATION_REASON_CONCEPT,
                        "2.16.840.1.113883.3.795.12.100.50", concept("CUSTOM_REC", "supplemental rec", null)),
                "2.16.840.1.113883.3.795.12.100.50");

        assertDoesNotThrow(supportedCdsLists::validateSupplementalReasonSupportingData);
    }

    @Test
    void failsValidationWhenSupplementalRecommendationCodeSystemMissing()
    {
        final SupportedCdsLists supportedCdsLists = new SupportedCdsLists(List.of(CDS_VERSION));

        supportedCdsLists.addSupportedCodeSystem(codeSystem("EVALUATION_REASON_CONCEPT", "2.16.840.1.113883.3.795.12.100.3",
                concept("SUPPLEMENTAL_TEXT", "Legacy eval supplemental", null)), "2.16.840.1.113883.3.795.12.100.3");
        supportedCdsLists.addSupportedCodeSystem(codeSystem("RECOMMENDATION_REASON_CONCEPT", "2.16.840.1.113883.3.795.12.100.6",
                concept("SUPPLEMENTAL_TEXT", "Legacy rec supplemental", null)), "2.16.840.1.113883.3.795.12.100.6");
        supportedCdsLists.addSupportedCodeSystem(
                codeSystem(SupplementalReasonSupport.SUPPLEMENTAL_EVALUATION_REASON_CONCEPT, "2.16.840.1.113883.3.795.12.100.51",
                        concept("COVID19_MIN_AGE", "supplemental eval", null)), "2.16.840.1.113883.3.795.12.100.51");

        assertThrows(InconsistentConfigurationException.class, supportedCdsLists::validateSupplementalReasonSupportingData);
    }
}
