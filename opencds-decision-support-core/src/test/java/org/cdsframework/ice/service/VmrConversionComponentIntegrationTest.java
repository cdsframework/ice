package org.cdsframework.ice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.config.IceSupportingDataProperties;
import org.cdsframework.ice.dto.AdministrativeGender;
import org.cdsframework.ice.dto.CodeSystem;
import org.cdsframework.ice.dto.CodeSystemConcept;
import org.cdsframework.ice.dto.CodeSystemContentModeEnum;
import org.cdsframework.ice.dto.Identifier;
import org.cdsframework.ice.dto.ImmunizationEvaluation;
import org.cdsframework.ice.dto.ImmunizationRecommendation;
import org.cdsframework.ice.dto.Parameters;
import org.cdsframework.ice.dto.ParametersParameter;
import org.cdsframework.ice.dto.Patient;
import org.cdsframework.ice.dto.PublicationStatusEnum;
import org.junit.jupiter.api.Test;
import org.omg.dss.EvaluationResponse;
import org.omg.dss.FinalKMEvaluationResponse;
import org.omg.dss.KMEvaluationResultData;
import org.omg.dss.SemanticPayload;
import org.opencds.vmr.v1_0.schema.CD;
import org.opencds.vmr.v1_0.schema.CDSOutput;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson;
import org.opencds.vmr.v1_0.schema.II;
import org.opencds.vmr.v1_0.schema.ObservationResult;
import org.opencds.vmr.v1_0.schema.RelatedClinicalStatement;
import org.opencds.vmr.v1_0.schema.ST;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal;
import org.opencds.vmr.v1_0.schema.VMR;
import org.springframework.core.io.ByteArrayResource;

import tools.jackson.dataformat.xml.XmlMapper;

class VmrConversionComponentIntegrationTest
{
    private static final String KM_ID = "org.nyc.cir^ICE^1.0.0";
    private static final String MODULE_CANONICAL = "http://nyc.gov/cir/PlanDefinition/ice-forecast|1.0.0";
    private static final String VACCINE_GROUP_OID = "2.16.840.1.113883.3.795.12.100.1";
    private static final String SERIES_OPTIONS_OID = "2.16.840.1.113883.3.795.12.100.500";
    private static final String SERIES_SELECTION_TYPE_OID = "2.16.840.1.113883.3.795.12.100.501";
    private static final String SERIES_OID = "2.16.840.1.113883.3.795.12.100.10";
    private static final String SEASON_OID = "2.16.840.1.113883.3.795.12.100.11";
    private static final String RECOMMENDATION_REASON_OID = "2.16.840.1.113883.3.795.12.100.6";
    private static final String SELECTION_CONTEXT_EXTENSION_URL =
            "http://terminology.cdsframework.org/fhir/StructureDefinition/immunization-selection-context";

    private static IceProperties createIceProperties(final boolean outputSeriesInformation,
            final boolean outputNumberOfDosesRemaining)
    {
        final IceProperties properties = new IceProperties();
        properties.setKnowledgeModules(Map.of(KM_ID,
                new IceProperties.KnowledgeModuleProperties(true, false, true, outputNumberOfDosesRemaining,
                        outputSeriesInformation, false, List.of(), List.of(), false, IceProperties.SupplementalTextMode.LEGACY,
                        new ByteArrayResource(new byte[0]))));
        return properties;
    }

    private static CodeSystem simpleCodeSystem(final String name, final String oid, final String url,
            final CodeSystemConcept... concepts)
    {
        final CodeSystem.CodeSystemBuilder builder = CodeSystem.builder()
                .name(name)
                .identifier(Identifier.builder().system("urn:ietf:rfc:3986").value("urn:oid:" + oid).build())
                .url(url)
                .status(PublicationStatusEnum.ACTIVE)
                .content(CodeSystemContentModeEnum.COMPLETE);
        if (concepts != null)
        {
            for (final CodeSystemConcept concept : concepts)
                builder.concept(concept);
        }
        return builder.build();
    }

    private static IceSupportingDataProperties createIceSupportingDataProperties()
    {
        final IceSupportingDataProperties properties = new IceSupportingDataProperties();
        properties.setKnowledgeModules(Map.of(KM_ID, new IceSupportingDataProperties.KnowledgeModule(Map.of(),
                Map.of("VACCINE_GROUP_CONCEPT", simpleCodeSystem("VACCINE_GROUP_CONCEPT", VACCINE_GROUP_OID,
                                "http://terminology.cdsframework.org/ice/vaccine-group",
                                CodeSystemConcept.builder().code("100").display("Test Vaccine Group").build()), "SUPPORTED_SERIES",
                        simpleCodeSystem("SUPPORTED_SERIES", SERIES_OID, "http://terminology.cdsframework.org/ice/series",
                                CodeSystemConcept.builder().code("ZOSTER_SERIES").display("Zoster Series").build()),
                        "SERIES_DISPLAY_SELECTION_TYPE",
                        simpleCodeSystem("SERIES_DISPLAY_SELECTION_TYPE", SERIES_SELECTION_TYPE_OID,
                                "http://terminology.cdsframework.org/ice/series-display-selection-type", CodeSystemConcept.builder()
                                        .code("SERIES_DISPLAY_UNAMBIGUOUS")
                                        .display("Series Selected for Display")
                                        .build()), "SUPPORTED_SEASON",
                        simpleCodeSystem("SUPPORTED_SEASON", SEASON_OID, "http://terminology.cdsframework.org/ice/season",
                                CodeSystemConcept.builder().code("SEASON_2026_2027").display("2026-2027 Season").build()),
                        "RECOMMENDATION_REASON_CONCEPT",
                        simpleCodeSystem("RECOMMENDATION_REASON_CONCEPT", RECOMMENDATION_REASON_OID,
                                "http://terminology.cdsframework.org/ice/recommendation-reason",
                                CodeSystemConcept.builder().code("RECOMMENDED").display("Recommended").build(),
                                CodeSystemConcept.builder().code("DUE_NOW").display("Due Now").build())),
                Map.of("2.16.840.1.113883.6.96", "http://snomed.info/sct", "2.16.840.1.113883.6.1", "http://loinc.org",
                        "2.16.840.1.113883.6.103", "http://hl7.org/fhir/sid/icd-9-cm", "2.16.840.1.113883.6.90",
                        "http://hl7.org/fhir/sid/icd-10-cm", "2.16.840.1.113883.6.3", "http://hl7.org/fhir/sid/icd-10",
                        "2.16.840.1.113883.3.795.12.100.4", "http://terminology.cdsframework.org/ice/unknown",
                        "2.16.840.1.113883.3.795.12.100.500", "http://terminology.cdsframework.org/ice/series-display-options"))));
        return properties;
    }

    private static Parameters createRequestParameters()
    {
        return Parameters.builder()
                .resourceType("Parameters")
                .parameter(ParametersParameter.builder().name("assessmentDate").valueDate("2026-04-09").build())
                .parameter(ParametersParameter.builder().name("module").valueCanonical(MODULE_CANONICAL).build())
                .parameter(ParametersParameter.builder()
                        .name("patient")
                        .resource(Patient.builder()
                                .identifier(
                                        Identifier.builder().system("http://nyc.gov/cir/identifier/patient-id").value("p1").build())
                                .birthDate(LocalDate.parse("1990-01-01"))
                                .gender(AdministrativeGender.FEMALE)
                                .build())
                        .build())
                .build();
    }

    private static CD createCd(final String code, final String codeSystem, final String display)
    {
        final CD cd = new CD();
        cd.setCode(code);
        cd.setCodeSystem(codeSystem);
        cd.setDisplayName(display);
        return cd;
    }

    private static RelatedClinicalStatement asRelatedObservation(final ObservationResult observationResult)
    {
        final RelatedClinicalStatement related = new RelatedClinicalStatement();
        related.setObservationResult(observationResult);
        return related;
    }

    private static ObservationResult createSeriesDisplayOptionsObservation()
    {
        final ObservationResult dosesRemaining = new ObservationResult();
        dosesRemaining.setObservationFocus(createCd("NUMBER_OF_DOSES_REMAINING", SERIES_OID, "Doses Remaining"));
        final ObservationResult.ObservationValue dosesValue = new ObservationResult.ObservationValue();
        final ST st = new ST();
        st.setValue("2");
        dosesValue.setText(st);
        dosesRemaining.setObservationValue(dosesValue);

        final ObservationResult seriesSelection = new ObservationResult();
        seriesSelection.setObservationFocus(createCd("SERIES_DISPLAY_UNAMBIGUOUS", SERIES_SELECTION_TYPE_OID, null));
        final ObservationResult.ObservationValue seriesSelectionValue = new ObservationResult.ObservationValue();
        seriesSelectionValue.setConcept(createCd("ZOSTER_SERIES", SERIES_OID, "Zoster Series"));
        seriesSelection.setObservationValue(seriesSelectionValue);
        seriesSelection.getRelatedClinicalStatement().add(asRelatedObservation(dosesRemaining));

        final ObservationResult season = new ObservationResult();
        season.setObservationFocus(createCd("SEASON", SEASON_OID, null));
        final ObservationResult.ObservationValue seasonValue = new ObservationResult.ObservationValue();
        seasonValue.setConcept(createCd("SEASON_2026_2027", SEASON_OID, "2026-2027 Season"));
        season.setObservationValue(seasonValue);

        final ObservationResult seriesOptions = new ObservationResult();
        seriesOptions.setObservationFocus(createCd("SERIES_DISPLAY_OPTIONS", SERIES_OPTIONS_OID, null));
        seriesOptions.getRelatedClinicalStatement().add(asRelatedObservation(seriesSelection));
        seriesOptions.getRelatedClinicalStatement().add(asRelatedObservation(season));
        return seriesOptions;
    }

    private static ObservationResult createPrimaryRecommendationObservation()
    {
        final ObservationResult primary = new ObservationResult();
        primary.setObservationFocus(createCd("100", VACCINE_GROUP_OID, "Test Vaccine Group"));
        final ObservationResult.ObservationValue value = new ObservationResult.ObservationValue();
        value.setConcept(createCd("RECOMMENDED", RECOMMENDATION_REASON_OID, "Recommended"));
        primary.setObservationValue(value);
        primary.getInterpretation().add(createCd("DUE_NOW", RECOMMENDATION_REASON_OID, "Due Now"));
        return primary;
    }

    private static SubstanceAdministrationProposal createProposalWithSeriesContext()
    {
        final SubstanceAdministrationProposal proposal = new SubstanceAdministrationProposal();
        final II id = new II();
        id.setExtension("rec-1");
        proposal.setId(id);
        proposal.getRelatedClinicalStatement().add(asRelatedObservation(createPrimaryRecommendationObservation()));
        proposal.getRelatedClinicalStatement().add(asRelatedObservation(createSeriesDisplayOptionsObservation()));
        return proposal;
    }

    private static SubstanceAdministrationProposal createSecondProposalWithSeriesContext()
    {
        final SubstanceAdministrationProposal proposal = new SubstanceAdministrationProposal();
        final II id = new II();
        id.setExtension("rec-2");
        proposal.setId(id);
        proposal.getRelatedClinicalStatement().add(asRelatedObservation(createPrimaryRecommendationObservation()));
        proposal.getRelatedClinicalStatement().add(asRelatedObservation(createSeriesDisplayOptionsObservation()));
        return proposal;
    }

    private static SubstanceAdministrationEvent createEventWithSeriesContext()
    {
        final SubstanceAdministrationEvent event = new SubstanceAdministrationEvent();
        final II id = new II();
        id.setExtension("imm-1");
        event.setId(id);
        event.getRelatedClinicalStatement().add(asRelatedObservation(createPrimaryRecommendationObservation()));
        event.getRelatedClinicalStatement().add(asRelatedObservation(createSeriesDisplayOptionsObservation()));
        return event;
    }

    private static EvaluationResponse createEvaluationResponseWithSeriesContext()
    {
        final CDSOutput cdsOutput = new CDSOutput();
        final VMR vmrOutput = new VMR();
        final EvaluatedPerson person = new EvaluatedPerson();
        final EvaluatedPerson.ClinicalStatements clinicalStatements = new EvaluatedPerson.ClinicalStatements();
        final EvaluatedPerson.ClinicalStatements.SubstanceAdministrationProposals proposals =
                new EvaluatedPerson.ClinicalStatements.SubstanceAdministrationProposals();
        proposals.getSubstanceAdministrationProposal().add(createProposalWithSeriesContext());
        clinicalStatements.setSubstanceAdministrationProposals(proposals);
        final EvaluatedPerson.ClinicalStatements.SubstanceAdministrationEvents events =
                new EvaluatedPerson.ClinicalStatements.SubstanceAdministrationEvents();
        events.getSubstanceAdministrationEvent().add(createEventWithSeriesContext());
        clinicalStatements.setSubstanceAdministrationEvents(events);
        person.setClinicalStatements(clinicalStatements);
        vmrOutput.setPatient(person);
        cdsOutput.setVmrOutput(vmrOutput);

        final XmlMapper xmlMapper = XmlMapper.xmlBuilder().defaultUseWrapper(false).findAndAddModules().build();
        final byte[] payload = xmlMapper.writeValueAsBytes(cdsOutput);

        final SemanticPayload semanticPayload = new SemanticPayload();
        semanticPayload.getBase64EncodedPayload().add(payload);
        final KMEvaluationResultData resultData = new KMEvaluationResultData();
        resultData.setData(semanticPayload);
        final FinalKMEvaluationResponse finalResponse = new FinalKMEvaluationResponse();
        finalResponse.getKmEvaluationResultData().add(resultData);
        final EvaluationResponse evaluationResponse = new EvaluationResponse();
        evaluationResponse.getFinalKMEvaluationResponse().add(finalResponse);
        return evaluationResponse;
    }

    private static EvaluationResponse createEvaluationResponseWithTwoRecommendations() throws Exception
    {
        final CDSOutput cdsOutput = new CDSOutput();
        final VMR vmrOutput = new VMR();
        final EvaluatedPerson person = new EvaluatedPerson();
        final EvaluatedPerson.ClinicalStatements clinicalStatements = new EvaluatedPerson.ClinicalStatements();
        final EvaluatedPerson.ClinicalStatements.SubstanceAdministrationProposals proposals =
                new EvaluatedPerson.ClinicalStatements.SubstanceAdministrationProposals();
        proposals.getSubstanceAdministrationProposal().add(createProposalWithSeriesContext());
        proposals.getSubstanceAdministrationProposal().add(createSecondProposalWithSeriesContext());
        clinicalStatements.setSubstanceAdministrationProposals(proposals);
        person.setClinicalStatements(clinicalStatements);
        vmrOutput.setPatient(person);
        cdsOutput.setVmrOutput(vmrOutput);

        final XmlMapper xmlMapper = XmlMapper.xmlBuilder().defaultUseWrapper(false).findAndAddModules().build();
        final byte[] payload = xmlMapper.writeValueAsBytes(cdsOutput);

        final SemanticPayload semanticPayload = new SemanticPayload();
        semanticPayload.getBase64EncodedPayload().add(payload);
        final KMEvaluationResultData resultData = new KMEvaluationResultData();
        resultData.setData(semanticPayload);
        final FinalKMEvaluationResponse finalResponse = new FinalKMEvaluationResponse();
        finalResponse.getKmEvaluationResultData().add(resultData);
        final EvaluationResponse evaluationResponse = new EvaluationResponse();
        evaluationResponse.getFinalKMEvaluationResponse().add(finalResponse);
        return evaluationResponse;
    }

    private static ImmunizationRecommendation firstRecommendation(final Parameters response)
    {
        return response.parameter()
                .stream()
                .filter(param -> "recommendation".equals(param.name()))
                .map(ParametersParameter::resource)
                .filter(ImmunizationRecommendation.class::isInstance)
                .map(ImmunizationRecommendation.class::cast)
                .findFirst()
                .orElseThrow();
    }

    private static List<ImmunizationRecommendation> recommendationResources(final Parameters response)
    {
        return response.parameter()
                .stream()
                .filter(param -> "recommendation".equals(param.name()))
                .map(ParametersParameter::resource)
                .filter(ImmunizationRecommendation.class::isInstance)
                .map(ImmunizationRecommendation.class::cast)
                .toList();
    }

    private static ImmunizationEvaluation firstEvaluation(final Parameters response)
    {
        return response.parameter()
                .stream()
                .filter(param -> "evaluation".equals(param.name()))
                .map(ParametersParameter::resource)
                .filter(ImmunizationEvaluation.class::isInstance)
                .map(ImmunizationEvaluation.class::cast)
                .findFirst()
                .orElseThrow();
    }

    @Test
    void doesNotEmitSeriesSeasonOrDosesWhenOutputFlagsAreDisabled()
    {
        final IceProperties iceProperties = createIceProperties(false, false);
        final SupportingDataService supportingDataService =
                new SupportingDataService(createIceSupportingDataProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent =
                new VmrConversionComponent(supportingDataService, iceProperties, new KnowledgeModuleIdResolver(iceProperties));

        final Parameters response = vmrConversionComponent.convertToParametersResponse(createEvaluationResponseWithSeriesContext(),
                createRequestParameters(), LocalDateTime.now());
        final ImmunizationRecommendation recommendation = firstRecommendation(response);
        final ImmunizationEvaluation evaluation = firstEvaluation(response);
        final var rec = recommendation.recommendation().getFirst();

        assertNull(rec.series());
        assertNull(rec.seriesDoses());
        assertTrue(rec.extension() == null || rec.extension().isEmpty());
        assertTrue(evaluation.extension().isEmpty());
        assertNotNull(rec.description());
        assertTrue(rec.description().startsWith("Recommendation status:"));
    }

    @Test
    void emitsSeriesSeasonAndDosesWhenOutputFlagIsEnabled()
    {
        final IceProperties iceProperties = createIceProperties(true, false);
        final SupportingDataService supportingDataService =
                new SupportingDataService(createIceSupportingDataProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent =
                new VmrConversionComponent(supportingDataService, iceProperties, new KnowledgeModuleIdResolver(iceProperties));

        final Parameters response = vmrConversionComponent.convertToParametersResponse(createEvaluationResponseWithSeriesContext(),
                createRequestParameters(), LocalDateTime.now());
        final ImmunizationRecommendation recommendation = firstRecommendation(response);
        final ImmunizationEvaluation evaluation = firstEvaluation(response);
        final var rec = recommendation.recommendation().getFirst();

        assertEquals("Zoster Series", rec.series());
        assertEquals("Zoster Series", evaluation.series());
        assertNotNull(rec.seriesDoses());
        assertEquals("2", rec.seriesDoses().text());
        assertEquals(1, rec.extension().size());
        final var contextExtension = rec.extension().getFirst();
        assertEquals(SELECTION_CONTEXT_EXTENSION_URL, contextExtension.url());
        assertEquals(3, contextExtension.extension().size());
        assertTrue(contextExtension.extension()
                .stream()
                .anyMatch(ext -> "selectedSeries".equals(ext.url()) && ext.valueCodeableConcept()
                        .coding()
                        .stream()
                        .anyMatch(coding -> "ZOSTER_SERIES".equals(coding.code()))));
        assertTrue(contextExtension.extension()
                .stream()
                .anyMatch(ext -> "seriesSelectionType".equals(ext.url()) && ext.valueCodeableConcept()
                        .coding()
                        .stream()
                        .anyMatch(coding -> "SERIES_DISPLAY_UNAMBIGUOUS".equals(coding.code()))));
        assertTrue(contextExtension.extension()
                .stream()
                .anyMatch(ext -> "selectedSeason".equals(ext.url()) && ext.valueCodeableConcept()
                        .coding()
                        .stream()
                        .anyMatch(coding -> "SEASON_2026_2027".equals(coding.code()))));
        assertEquals(1, evaluation.extension().size());
        assertEquals(SELECTION_CONTEXT_EXTENSION_URL, evaluation.extension().getFirst().url());
        assertTrue(rec.description().contains("Series: Zoster Series"));
        assertTrue(rec.description().contains("Season: 2026-2027 Season"));
    }

    @Test
    void emitsSingleRecommendationResourceWithNestedEntries() throws Exception
    {
        final IceProperties iceProperties = createIceProperties(true, false);
        final SupportingDataService supportingDataService =
                new SupportingDataService(createIceSupportingDataProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent =
                new VmrConversionComponent(supportingDataService, iceProperties, new KnowledgeModuleIdResolver(iceProperties));

        final Parameters response =
                vmrConversionComponent.convertToParametersResponse(createEvaluationResponseWithTwoRecommendations(),
                        createRequestParameters(), LocalDateTime.now());

        final List<ImmunizationRecommendation> recommendationResources = recommendationResources(response);
        assertEquals(1, recommendationResources.size());
        assertEquals(2, recommendationResources.getFirst().recommendation().size());
    }
}
