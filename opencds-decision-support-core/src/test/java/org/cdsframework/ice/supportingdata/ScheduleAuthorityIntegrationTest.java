package org.cdsframework.ice.supportingdata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.cdsframework.fhir.CodeSystem;
import org.cdsframework.fhir.CodeSystemConcept;
import org.cdsframework.fhir.CodeSystemConceptProperty;
import org.cdsframework.fhir.Coding;
import org.cdsframework.fhir.Identifier;
import org.cdsframework.ice.config.CdsEngineProperties;
import org.cdsframework.ice.service.DoseStatus;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.RecommendationStatus;
import org.cdsframework.ice.service.ScheduleAuthority;
import org.cdsframework.ice.service.SupportingDataService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ScheduleAuthorityIntegrationTest
{

    private static final String CDS_VERSION = "1.0.0";
    private static final String SCHEDULE_AUTHORITY_OID = "2.16.840.1.113883.3.795.12.100.12";
    private static final String COMMON_KNOWLEDGE_MODULE = "org.cdsframework^ICE^1.0.0";
    private static final String KNOWLEDGE_MODULE = "org.nyc.cir^ICE^1.0.0";

    private CodeSystem createCodeSystem(final String name, final String oid, final List<CodeSystemConcept> concepts)
    {
        return new CodeSystem(name, List.of(new Identifier(null, null, "urn:ietf:rfc:3986", "urn:oid:" + oid)), null, oid, name,
                CDS_VERSION, null, null, null, concepts);
    }

    private CodeSystem createBaseDataCodeSystem(final String name, final String oid, final BaseData... baseData)
    {
        final List<CodeSystemConcept> concepts = Stream.of(baseData)
                .map(BaseData::getCdsListItemName)
                .filter(java.util.Objects::nonNull)
                .map(itemName -> itemName.substring(itemName.indexOf('.') + 1))
                .map(code -> new CodeSystemConcept(code, code, List.of()))
                .toList();
        return createCodeSystem(name, oid, concepts);
    }

    private CodeSystem createScheduleAuthorityCodeSystem()
    {
        return createCodeSystem("SUPPORTED_SCHEDULE_AUTHORITY", SCHEDULE_AUTHORITY_OID,
                List.of(new CodeSystemConcept("ACIP_CDC", "ACIP/CDC Display", List.of()),
                        new CodeSystemConcept("AAP", "AAP Display", List.of())));
    }

    private CodeSystem createVaccineGroupCodeSystem()
    {
        final Coding acipCoding = new Coding(SCHEDULE_AUTHORITY_OID, null, "ACIP_CDC", "ACIP/CDC Display");
        final CodeSystemConceptProperty property = new CodeSystemConceptProperty("scheduleAuthority", acipCoding, null, null, null);
        return createCodeSystem("VACCINE_GROUP_CONCEPT", "2.16.840.1.113883.3.795.12.100.1",
                List.of(new CodeSystemConcept("DTP", "DTP Vaccine Group", List.of(property))));
    }

    private Map<String, CodeSystem> createCodeSystems()
    {
        return Map.of("SUPPORTED_SCHEDULE_AUTHORITY", createScheduleAuthorityCodeSystem(), "VACCINE_GROUP_CONCEPT",
                createVaccineGroupCodeSystem(), "EVALUATION_STATUS_CONCEPT",
                createBaseDataCodeSystem("EVALUATION_STATUS_CONCEPT", "2.16.840.1.113883.3.795.12.100.301", DoseStatus.values()),
                "EVALUATION_REASON_CONCEPT",
                createBaseDataCodeSystem("EVALUATION_REASON_CONCEPT", "2.16.840.1.113883.3.795.12.100.302",
                        BaseDataEvaluationReason.values()), "RECOMMENDATION_STATUS_CONCEPT",
                createBaseDataCodeSystem("RECOMMENDATION_STATUS_CONCEPT", "2.16.840.1.113883.3.795.12.100.303",
                        RecommendationStatus.values()), "RECOMMENDATION_REASON_CONCEPT",
                createBaseDataCodeSystem("RECOMMENDATION_REASON_CONCEPT", "2.16.840.1.113883.3.795.12.100.304",
                        BaseDataRecommendationReason.values()), "SUPPLEMENTAL_EVALUATION_REASON_CONCEPT",
                createCodeSystem("SUPPLEMENTAL_EVALUATION_REASON_CONCEPT", "2.16.840.1.113883.3.795.12.100.305",
                        List.of(new CodeSystemConcept("TEST", "Test", List.of()))), "SUPPLEMENTAL_RECOMMENDATION_REASON_CONCEPT",
                createCodeSystem("SUPPLEMENTAL_RECOMMENDATION_REASON_CONCEPT", "2.16.840.1.113883.3.795.12.100.306",
                        List.of(new CodeSystemConcept("TEST", "Test", List.of()))));
    }

    @Test
    void initializesAndAssociatesScheduleAuthorities() throws InconsistentConfigurationException
    {
        final SupportingDataService supportingDataService = Mockito.mock(SupportingDataService.class);
        final Map<String, CodeSystem> codeSystems = createCodeSystems();

        final CdsEngineProperties.KnowledgeBaseDefinition commonModule =
                Mockito.mock(CdsEngineProperties.KnowledgeBaseDefinition.class);
        Mockito.when(commonModule.codeSystems()).thenReturn(codeSystems);

        Mockito.when(supportingDataService.getSupportingKnowledgeBaseByKmId(Mockito.anyString())).thenReturn(commonModule);
        Mockito.when(supportingDataService.extractCodeSystemOid(Mockito.any(CodeSystem.class))).thenAnswer(invocation ->
        {
            final CodeSystem codeSystem = invocation.getArgument(0, CodeSystem.class);
            return codeSystem.identifier().getFirst().value().substring("urn:oid:".length());
        });

        final ICESupportingDataConfiguration configuration =
                new ICESupportingDataConfiguration(COMMON_KNOWLEDGE_MODULE, List.of(KNOWLEDGE_MODULE), supportingDataService);

        final SupportedScheduleAuthorities scheduleAuthorities = configuration.getSupportedScheduleAuthorities();
        assertNotNull(scheduleAuthorities);
        assertFalse(scheduleAuthorities.isEmpty());

        final Optional<ScheduleAuthority> acip = scheduleAuthorities.getScheduleAuthority("SUPPORTED_SCHEDULE_AUTHORITY.ACIP_CDC");
        assertTrue(acip.isPresent());
        assertEquals("SUPPORTED_SCHEDULE_AUTHORITY.ACIP_CDC", acip.get().getCode());
        assertEquals("ACIP/CDC Display", acip.get().getDisplayName());

        final SupportedVaccineGroups vaccineGroups = configuration.getSupportedVaccineGroups();
        final LocallyCodedVaccineGroupItem dtpItem = vaccineGroups.getVaccineGroupItem("VACCINE_GROUP_CONCEPT.DTP");
        assertNotNull(dtpItem);
        assertTrue(dtpItem.getCopyOfScheduleAuthorityCdsListItemNames().contains("ACIP_CDC"));
    }
}
