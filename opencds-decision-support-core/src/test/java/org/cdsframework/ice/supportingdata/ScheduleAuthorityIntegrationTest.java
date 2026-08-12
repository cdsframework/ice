package org.cdsframework.ice.supportingdata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.cdsframework.cds.supportingdata.SupportedCdsLists;
import org.cdsframework.cds.supportingdata.SupportingData;
import org.cdsframework.fhir.CodeSystem;
import org.cdsframework.fhir.CodeSystemConcept;
import org.cdsframework.fhir.CodeSystemConceptProperty;
import org.cdsframework.fhir.Coding;
import org.cdsframework.fhir.Identifier;
import org.cdsframework.ice.config.CdsEngineProperties;
import org.cdsframework.ice.supportingdata.ICEConceptType;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.ScheduleAuthority;
import org.cdsframework.ice.service.SupportingDataService;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ScheduleAuthorityIntegrationTest {

    private static final String CDS_VERSION = "1.0.0";
    private static final String SCHEDULE_AUTHORITY_OID = "2.16.840.1.113883.3.795.12.100.12";

    private CodeSystem createScheduleAuthorityCodeSystem() {
        CodeSystemConcept acip = new CodeSystemConcept("ACIP_CDC", "ACIP/CDC Display", List.of());
        CodeSystemConcept aap = new CodeSystemConcept("AAP", "AAP Display", List.of());

        return new CodeSystem("SUPPORTED_SCHEDULE_AUTHORITY",
                List.of(new Identifier(null, null, "urn:ietf:rfc:3986", "urn:oid:" + SCHEDULE_AUTHORITY_OID)),
                null, SCHEDULE_AUTHORITY_OID, "SUPPORTED_SCHEDULE_AUTHORITY", CDS_VERSION,
                null, null, null, List.of(acip, aap));
    }

    private CodeSystem createVaccineGroupCodeSystem() {
        Coding acipCoding = new Coding(SCHEDULE_AUTHORITY_OID, null, "ACIP_CDC", "ACIP/CDC Display");
        CodeSystemConceptProperty prop = new CodeSystemConceptProperty("scheduleAuthority", acipCoding, null, null, null);

        CodeSystemConcept dtp = new CodeSystemConcept("DTP", "DTP Vaccine Group", List.of(prop));

        return new CodeSystem("VACCINE_GROUP_CONCEPT",
                List.of(new Identifier(null, null, "urn:ietf:rfc:3986", "urn:oid:2.16.840.1.113883.3.795.12.100.1")),
                null, "2.16.840.1.113883.3.795.12.100.1", "VACCINE_GROUP_CONCEPT", CDS_VERSION,
                null, null, null, List.of(dtp));
    }

    @Test
    public void testScheduleAuthorityInitializationAndAssociation() throws InconsistentConfigurationException {
        SupportingDataService supportingDataService = Mockito.mock(SupportingDataService.class);
        
        CodeSystem saCS = createScheduleAuthorityCodeSystem();
        CodeSystem vgCS = createVaccineGroupCodeSystem();

        CdsEngineProperties.ModuleCanonicalDefinition commonModule = Mockito.mock(CdsEngineProperties.ModuleCanonicalDefinition.class);
        Mockito.when(commonModule.codeSystems()).thenReturn(java.util.Map.of(
            "SUPPORTED_SCHEDULE_AUTHORITY", saCS,
            "VACCINE_GROUP_CONCEPT", vgCS
        ));

        Mockito.when(supportingDataService.getSupportingKnowledgeModuleByKmId(Mockito.anyString())).thenReturn(commonModule);
        Mockito.when(supportingDataService.extractCodeSystemOid(saCS)).thenReturn(SCHEDULE_AUTHORITY_OID);
        Mockito.when(supportingDataService.extractCodeSystemOid(vgCS)).thenReturn("2.16.840.1.113883.3.795.12.100.1");

        ICESupportingDataConfiguration config = new ICESupportingDataConfiguration("common", List.of("km1"), supportingDataService);

        // Verify SupportedScheduleAuthorities
        SupportedScheduleAuthorities ssa = config.getSupportedScheduleAuthorities();
        assertNotNull(ssa);
        assertFalse(ssa.isEmpty());
        
        Optional<ScheduleAuthority> acip = ssa.getScheduleAuthority("ACIP_CDC");
        assertTrue(acip.isPresent());
        assertEquals("ACIP_CDC", acip.get().getCode());
        assertEquals("ACIP/CDC Display", acip.get().getDisplayName());

        // Verify SupportedVaccineGroups association
        SupportedVaccineGroups svg = config.getSupportedVaccineGroups();
        LocallyCodedVaccineGroupItem dtpItem = svg.getVaccineGroupItem("DTP");
        assertNotNull(dtpItem);
        
        java.util.Collection<String> saCodes = dtpItem.getCopyOfScheduleAuthorityCdsListItemNames();
        assertNotNull(saCodes);
        assertTrue(saCodes.contains("ACIP_CDC"));
    }
}
