package org.cdsframework.ice.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.dto.AdministrativeGender;
import org.cdsframework.ice.dto.Bundle;
import org.cdsframework.ice.dto.BundleEntry;
import org.cdsframework.ice.dto.CodeableConcept;
import org.cdsframework.ice.dto.Coding;
import org.cdsframework.ice.dto.Extension;
import org.cdsframework.ice.dto.FhirResource;
import org.cdsframework.ice.dto.GuidanceResponse;
import org.cdsframework.ice.dto.Identifier;
import org.cdsframework.ice.dto.Immunization;
import org.cdsframework.ice.dto.ImmunizationEvaluation;
import org.cdsframework.ice.dto.ImmunizationRecommendation;
import org.cdsframework.ice.dto.ImmunizationRecommendationDateCriterion;
import org.cdsframework.ice.dto.ImmunizationRecommendationRecommendation;
import org.cdsframework.ice.dto.Narrative;
import org.cdsframework.ice.dto.Observation;
import org.cdsframework.ice.dto.OperationOutcome;
import org.cdsframework.ice.dto.Parameters;
import org.cdsframework.ice.dto.ParametersParameter;
import org.cdsframework.ice.dto.Patient;
import org.cdsframework.ice.dto.Reference;
import org.cdsframework.ice.supportingdata.ICEConceptType;
import org.omg.dss.DataRequirementItemData;
import org.omg.dss.EntityIdentifier;
import org.omg.dss.EvaluateAtSpecifiedTime;
import org.omg.dss.EvaluationRequest;
import org.omg.dss.EvaluationResponse;
import org.omg.dss.FinalKMEvaluationResponse;
import org.omg.dss.InteractionIdentifier;
import org.omg.dss.ItemIdentifier;
import org.omg.dss.KMEvaluationRequest;
import org.omg.dss.KMEvaluationResultData;
import org.omg.dss.SemanticPayload;
import org.opencds.vmr.v1_0.schema.AdministrableSubstance;
import org.opencds.vmr.v1_0.schema.CD;
import org.opencds.vmr.v1_0.schema.CDSContext;
import org.opencds.vmr.v1_0.schema.CDSInput;
import org.opencds.vmr.v1_0.schema.CDSOutput;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson;
import org.opencds.vmr.v1_0.schema.II;
import org.opencds.vmr.v1_0.schema.INT;
import org.opencds.vmr.v1_0.schema.IVLTS;
import org.opencds.vmr.v1_0.schema.ObservationResult;
import org.opencds.vmr.v1_0.schema.RelatedClinicalStatement;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal;
import org.opencds.vmr.v1_0.schema.TS;
import org.opencds.vmr.v1_0.schema.VMR;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.dataformat.xml.XmlMapper;

@Slf4j
@Component
public class VmrConversionComponent
{
    private record TimeInterval(LocalDate low,
                                LocalDate high)
    {
    }

    private record RequestContext(LocalDate assessmentDate,
                                  String moduleCanonical,
                                  Patient patient,
                                  List<Immunization> immunizations,
                                  List<Observation> observations)
    {
    }

    private record SeriesSelectionInfo(String seriesCode,
                                       String seriesDisplay,
                                       String selectionTypeCode)
    {
    }

    private record SeasonInfo(String seasonCode,
                              String seasonDisplay,
                              String seasonCodeSystem)
    {
    }

    private record ProtocolContext(SeriesSelectionInfo seriesSelectionInfo,
                                   SeasonInfo seasonInfo,
                                   String series,
                                   CodeableConcept seriesDoses,
                                   List<Extension> extensions)
    {
    }

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter tzFormat = DateTimeFormatter.ofPattern("Z");
    private static final String OID_URN_PREFIX = "urn:oid:";
    private static final String ASSESSMENT_DATE_PARAM = "assessmentDate";
    private static final String MODULE_PARAM = "module";
    private static final String PATIENT_PARAM = "patient";
    private static final String IMMUNIZATION_PARAM = "immunization";
    private static final String OBSERVATION_PARAM = "observation";
    private static final String DATA_PARAM = "data";
    private static final String EVALUATION_PARAM = "evaluation";
    private static final String RECOMMENDATION_PARAM = "recommendation";
    private static final String GUIDANCE_RESPONSE_PARAM = "guidanceResponse";
    private static final String OPERATION_OUTCOME_PARAM = "operationOutcome";
    private static final String DURATION_MS_PARAM = "durationMs";
    private static final String ENGINE_VERSION_PARAM = "engineVersion";
    private static final String RESOURCE_TYPE_IMMUNIZATION_EVALUATION = "ImmunizationEvaluation";
    private static final String RESOURCE_TYPE_IMMUNIZATION_RECOMMENDATION = "ImmunizationRecommendation";
    private static final String RESOURCE_TYPE_GUIDANCE_RESPONSE = "GuidanceResponse";
    private static final String RESOURCE_TYPE_PARAMETERS = "Parameters";
    private static final String RESOURCE_TYPE_OPERATION_OUTCOME = "OperationOutcome";
    private static final String STATUS_COMPLETED = "completed";
    private static final String STATUS_SUCCESS = "success";
    private static final String STATUS_GENERATED = "generated";
    private static final String OUTCOME_CODE_INFORMATIONAL = "informational";
    private static final String OUTCOME_CODE_WARNING = "warning";
    private static final String OUTCOME_CODE_PROCESSING = "processing";
    private static final String OUTCOME_SEVERITY_INFORMATION = "information";
    private static final String OUTCOME_SEVERITY_ERROR = "error";
    private static final String REFERENCE_PREFIX_PATIENT = "Patient/";
    private static final String REFERENCE_PREFIX_IMMUNIZATION = "Immunization/";
    private static final String KM_DATA_SUFFIX = "Data";
    private static final String ENTITY_ID_SCOPING_ENTITY_VMR = "org.opencds.vmr";
    private static final String ENTITY_ID_BUSINESS_ID_VMR = "VMR";
    private static final String ENTITY_ID_VERSION_VMR = "1.0";
    private static final String XML_ROOT_CDS_INPUT = "cdsInput";
    private static final String ITEM_ID_CDS_PAYLOAD = "cdsPayload";
    private static final String ADMIN_GENDER_CODE_MALE = "M";
    private static final String ADMIN_GENDER_CODE_FEMALE = "F";
    private static final String ADMIN_GENDER_CODE_UNKNOWN = "UN";
    private static final String V3_ADMINISTRATIVE_GENDER_OID = "2.16.840.1.113883.5.1";
    private static final String CVX_OID = "2.16.840.1.113883.12.292";
    private static final String FHIR_CVX_SYSTEM = "http://hl7.org/fhir/sid/cvx";
    private static final String SERIES_SELECTION_TYPE_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.501";
    private static final String SELECTION_CONTEXT_EXTENSION_URL =
            "http://terminology.cdsframework.org/fhir/StructureDefinition/immunization-selection-context";
    private static final String SELECTION_CONTEXT_CHILD_URL_SELECTED_SERIES = "selectedSeries";
    private static final String SELECTION_CONTEXT_CHILD_URL_SERIES_SELECTION_TYPE = "seriesSelectionType";
    private static final String SELECTION_CONTEXT_CHILD_URL_SELECTED_SEASON = "selectedSeason";
    private static final String SERIES_DISPLAY_OPTIONS_FOCUS_CODE = "SERIES_DISPLAY_OPTIONS";
    private static final String SERIES_DISPLAY_OPTIONS_FOCUS_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.500";
    private static final String SERIES_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.10";
    private static final String VACCINE_GROUP_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.1";
    private static final String NUMBER_OF_DOSES_REMAINING_FOCUS_CODE = "NUMBER_OF_DOSES_REMAINING";
    private static final String SEASON_OBSERVATION_FOCUS_CODE = "SEASON";
    private static final String SEASON_OBSERVATION_FOCUS_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.11";
    private static final String DISEASE_IMMUNITY_SOURCE_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.8";
    private static final String RECOMMENDATION_REASON_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.6";
    private static final String EVALUATION_REASON_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.3";
    private static final String DISEASE_IMMUNITY_REASON_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.9";
    private static final String CLIENT_LANGUAGE_CODE = "en";
    private static final String CLIENT_LANGUAGE_DISPLAY = "English";
    private static final String CLIENT_LANGUAGE_CODE_SYSTEM = "2.16.840.1.113883.6.99";
    private static final String TEMPLATE_ID_CDS_INPUT = "2.16.840.1.113883.3.795.11.1.1";
    private static final String TEMPLATE_ID_EVALUATED_PERSON = "2.16.840.1.113883.3.795.11.2.1.1";
    private static final String TEMPLATE_ID_OBSERVATION_RESULT = "2.16.840.1.113883.3.795.11.6.3.1";
    private static final String TEMPLATE_ID_SUBSTANCE_ADMINISTRATION_EVENT = "2.16.840.1.113883.3.795.11.9.1.1";
    private static final String EVALUATED_PERSON_ID_ROOT = "2.16.840.1.113883.3.795.12.100.11";
    private static final String IMMUNIZATION_ID_ROOT = "2.16.840.1.113883.3.795.12.100.10";
    private static final String SUBSTANCE_ADMINISTRATION_GENERAL_PURPOSE_CODE = "384810002";
    private static final String SUBSTANCE_ADMINISTRATION_GENERAL_PURPOSE_CODE_SYSTEM = "2.16.840.1.113883.6.5";
    private static final Coding DATE_CRITERION_DUE =
            Coding.builder().system("http://loinc.org").code("30980-7").display("Date vaccine due").build();
    private static final Coding DATE_CRITERION_OVERDUE =
            Coding.builder().system("http://loinc.org").code("59778-1").display("Date when overdue for immunization").build();
    private static final Coding DATE_CRITERION_EARLIEST =
            Coding.builder().system("http://loinc.org").code("30981-5").display("Earliest date to give").build();
    private static final Coding DATE_CRITERION_LATEST =
            Coding.builder().system("http://loinc.org").code("59777-3").display("Latest date to give immunization").build();
    private static final DatatypeFactory DATATYPE_FACTORY = DatatypeFactory.newDefaultInstance();

    private static TimeInterval toTimeInterval(final IVLTS ivlts)
    {
        return new TimeInterval(parseVmrDate(ivlts.getLow()), parseVmrDate(ivlts.getHigh()));
    }

    private static String formatVmrDate(final LocalDate date)
    {
        return date.format(dateFormat);
    }

    private static LocalDate parseVmrDate(final String date)
    {
        if (date == null || date.length() <= 7)
            return null;

        try
        {
            return LocalDate.parse(date.substring(0, 8), dateFormat);
        }
        catch (final DateTimeParseException e)
        {
            log.error("Failed to parse date: {}", date, e);
        }

        return null;
    }

    private final XmlMapper xmlMapper = XmlMapper.xmlBuilder()
            .defaultUseWrapper(false)
            .findAndAddModules()
            .changeDefaultPropertyInclusion(i -> i.withValueInclusion(JsonInclude.Include.NON_NULL))
            .build();
    private final SupportingDataService supportingDataService;
    private final IceProperties iceProperties;
    private final KnowledgeModuleIdResolver knowledgeModuleIdResolver;
    private final Map<String, Boolean> outputSeriesContextByKm;
    private final II templateIdCdsInputPrototype;
    private final II templateIdEvaluatedPersonPrototype;
    private final II templateIdObservationResultPrototype;
    private final II templateIdSubstanceAdministrationEventPrototype;
    private final CD clientLanguageCdPrototype;
    private final CD substanceAdministrationGeneralPurposeCdPrototype;

    public VmrConversionComponent(final SupportingDataService supportingDataService, final IceProperties iceProperties,
            final KnowledgeModuleIdResolver knowledgeModuleIdResolver)
    {
        this.supportingDataService = supportingDataService;
        this.iceProperties = iceProperties;
        this.knowledgeModuleIdResolver = knowledgeModuleIdResolver;
        this.outputSeriesContextByKm = buildOutputSeriesContextByKm();
        this.templateIdCdsInputPrototype = createIi(TEMPLATE_ID_CDS_INPUT, null);
        this.templateIdEvaluatedPersonPrototype = createIi(TEMPLATE_ID_EVALUATED_PERSON, null);
        this.templateIdObservationResultPrototype = createIi(TEMPLATE_ID_OBSERVATION_RESULT, null);
        this.templateIdSubstanceAdministrationEventPrototype = createIi(TEMPLATE_ID_SUBSTANCE_ADMINISTRATION_EVENT, null);
        this.clientLanguageCdPrototype = createCd(CLIENT_LANGUAGE_CODE, CLIENT_LANGUAGE_CODE_SYSTEM, CLIENT_LANGUAGE_DISPLAY);
        this.substanceAdministrationGeneralPurposeCdPrototype =
                createCd(SUBSTANCE_ADMINISTRATION_GENERAL_PURPOSE_CODE, SUBSTANCE_ADMINISTRATION_GENERAL_PURPOSE_CODE_SYSTEM, null);
    }

    private CodeableConcept toFhirCodeableConcept(final String kmId, final CD cd)
    {
        return supportingDataService.getCodeableConcept(kmId, cd.getCode(), cd.getDisplayName(), cd.getCodeSystem(),
                cd.getOriginalText());
    }

    public EvaluateAtSpecifiedTime convertToEvaluateAtSpecifiedTime(final Parameters parameters)
    {
        final RequestContext requestContext = parseRequestContext(parameters);
        final String kmId = knowledgeModuleIdResolver.deriveKmIdFromModuleCanonical(requestContext.moduleCanonical());
        final EntityIdentifier kmEntityIdentifier = knowledgeModuleIdResolver.parseKmEntityIdentifier(kmId);
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(Date.from(requestContext.assessmentDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        final byte[] payload = createPayload(getCdsInput(kmId, requestContext));
        if (log.isDebugEnabled())
            log.debug("payload: {}", new String(payload, StandardCharsets.UTF_8));

        return createEvaluateAtSpecifiedTime(createInteractionIdentifier(UUID.randomUUID().toString(),
                        DATATYPE_FACTORY.newXMLGregorianCalendar(new GregorianCalendar()), kmEntityIdentifier.getScopingEntityId()),
                DATATYPE_FACTORY.newXMLGregorianCalendar(calendar),
                createEvaluationRequest(getTimezone(), List.of(createKmEvaluationRequest(kmEntityIdentifier)),
                        List.of(createDataRequirementItemData(createItemIdentifier(
                                createEntityIdentifier(kmEntityIdentifier.getScopingEntityId(),
                                        "%s%s".formatted(kmEntityIdentifier.getBusinessId(), KM_DATA_SUFFIX),
                                        kmEntityIdentifier.getVersion())), createSemanticPayload(
                                createEntityIdentifier(ENTITY_ID_SCOPING_ENTITY_VMR, ENTITY_ID_BUSINESS_ID_VMR,
                                        ENTITY_ID_VERSION_VMR), List.of(payload))))));
    }

    public Parameters convertToParametersResponse(final EvaluationResponse evaluateAtSpecifiedTimeResponse,
            final Parameters requestParameters, final LocalDateTime requestDateTime)
    {
        final RequestContext requestContext = parseRequestContext(requestParameters);
        final String kmId = knowledgeModuleIdResolver.deriveKmIdFromModuleCanonical(requestContext.moduleCanonical());
        return extractCdsOutputs(evaluateAtSpecifiedTimeResponse).stream()
                .map(cdsOutput -> createParametersResponse(kmId, cdsOutput, requestContext, requestDateTime))
                .max(Comparator.comparingInt(this::forecastEntryCount))
                .orElseGet(() -> createErrorParametersResponse(OUTCOME_CODE_PROCESSING,
                        "No forecast payload was returned by the evaluation engine.", requestDateTime));
    }

    private Map<String, Boolean> buildOutputSeriesContextByKm()
    {
        final Map<String, IceProperties.KnowledgeModuleProperties> knowledgeModules =
                Optional.ofNullable(iceProperties).map(IceProperties::getKnowledgeModules).orElse(Map.of());
        if (knowledgeModules.isEmpty())
            return Map.of();

        final Map<String, Boolean> outputFlags = new HashMap<>();
        knowledgeModules.forEach((kmId, kmProps) -> outputFlags.put(kmId,
                kmProps != null && (Boolean.TRUE.equals(kmProps.outputSeriesInformation()) || Boolean.TRUE.equals(
                        kmProps.outputNumberOfDosesRemaining()))));
        return Map.copyOf(outputFlags);
    }

    private List<CDSOutput> extractCdsOutputs(final EvaluationResponse evaluateAtSpecifiedTimeResponse)
    {
        return Optional.ofNullable(evaluateAtSpecifiedTimeResponse)
                .map(EvaluationResponse::getFinalKMEvaluationResponse)
                .stream()
                .flatMap(Collection::stream)
                .map(FinalKMEvaluationResponse::getKmEvaluationResultData)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(KMEvaluationResultData::getData)
                .filter(Objects::nonNull)
                .map(SemanticPayload::getBase64EncodedPayload)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(this::tryParseCdsOutputPayload)
                .flatMap(Optional::stream)
                .toList();
    }

    private Optional<CDSOutput> tryParseCdsOutputPayload(final byte[] payload)
    {
        try
        {
            return Optional.of(parseCdsOutputPayload(payload));
        }
        catch (final RuntimeException e)
        {
            log.debug("Skipping semantic payload that is not CDSOutput XML: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private int forecastEntryCount(final Parameters parameters)
    {
        return (int) Optional.ofNullable(parameters)
                .map(Parameters::parameter)
                .stream()
                .flatMap(Collection::stream)
                .map(ParametersParameter::name)
                .filter(name -> EVALUATION_PARAM.equals(name) || RECOMMENDATION_PARAM.equals(name))
                .count();
    }

    private CDSOutput parseCdsOutputPayload(final byte[] payload)
    {
        try (final InputStream bIn = new ByteArrayInputStream(payload))
        {
            try (final Reader reader = new InputStreamReader(bIn))
            {
                return xmlMapper.readValue(reader, CDSOutput.class);
            }
        }
        catch (final IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private Parameters createParametersResponse(final String kmId, final CDSOutput cdsOutput, final RequestContext requestContext,
            final LocalDateTime requestDateTime)
    {
        final String patientId = Optional.ofNullable(requestContext.patient())
                .map(Patient::identifier)
                .map(this::extractPrimaryIdentifierValue)
                .orElse(null);
        final Reference patientReference =
                patientId == null ? null : Reference.builder().reference(REFERENCE_PREFIX_PATIENT + patientId).build();

        final Optional<EvaluatedPerson> outputPatient = Optional.ofNullable(cdsOutput.getVmrOutput()).map(VMR::getPatient);

        final List<ImmunizationEvaluation> immunizationEvaluations =
                buildImmunizationEvaluations(kmId, outputPatient, patientReference);
        final List<ImmunizationRecommendation> immunizationRecommendations =
                buildImmunizationRecommendations(kmId, outputPatient, patientReference, requestContext.assessmentDate());

        final int durationMs = (int) Math.max(0, ChronoUnit.MILLIS.between(requestDateTime, LocalDateTime.now()));
        final String engineVersion = getEngineVersion();

        final GuidanceResponse guidanceResponse = GuidanceResponse.builder()
                .resourceType(RESOURCE_TYPE_GUIDANCE_RESPONSE)
                .text(createNarrative("Immunization forecast guidance response generated successfully."))
                .status(STATUS_SUCCESS)
                .subject(patientReference)
                .occurrenceDateTime(requestDateTime.atZone(ZoneId.systemDefault()).toInstant().toString())
                .moduleCanonical(requestContext.moduleCanonical())
                .build();

        final List<OperationOutcome.Issue> issues = new ArrayList<>();
        issues.add(createOperationOutcomeIssue(OUTCOME_CODE_INFORMATIONAL,
                "Forecast completed successfully using ICE %s.".formatted(engineVersion), OUTCOME_SEVERITY_INFORMATION));
        if (immunizationEvaluations.isEmpty() && immunizationRecommendations.isEmpty())
            issues.add(createOperationOutcomeIssue(OUTCOME_CODE_WARNING,
                    "No evaluation or recommendation resources were produced. Ensure the request includes a patient birthDate and relevant immunization/observation input data.",
                    OUTCOME_CODE_PROCESSING));
        final OperationOutcome operationOutcome = createOperationOutcome(issues);

        final Parameters.ParametersBuilder responseBuilder = Parameters.builder()
                .resourceType(RESOURCE_TYPE_PARAMETERS)
                .parameter(ParametersParameter.builder()
                        .name(ASSESSMENT_DATE_PARAM)
                        .valueDate(requestContext.assessmentDate().toString())
                        .build())
                .parameter(ParametersParameter.builder().name(DURATION_MS_PARAM).valueInteger(durationMs).build())
                .parameter(ParametersParameter.builder().name(ENGINE_VERSION_PARAM).valueString(engineVersion).build())
                .parameter(ParametersParameter.builder().name(GUIDANCE_RESPONSE_PARAM).resource(guidanceResponse).build())
                .parameter(ParametersParameter.builder().name(OPERATION_OUTCOME_PARAM).resource(operationOutcome).build());

        immunizationEvaluations.forEach(immunizationEvaluation -> responseBuilder.parameter(
                ParametersParameter.builder().name(EVALUATION_PARAM).resource(immunizationEvaluation).build()));

        immunizationRecommendations.forEach(immunizationRecommendation -> responseBuilder.parameter(
                ParametersParameter.builder().name(RECOMMENDATION_PARAM).resource(immunizationRecommendation).build()));

        return responseBuilder.build();
    }

    private List<ImmunizationEvaluation> buildImmunizationEvaluations(final String kmId,
            final Optional<EvaluatedPerson> outputPatient, final Reference patientReference)
    {
        return outputPatient.map(EvaluatedPerson::getClinicalStatements)
                .map(EvaluatedPerson.ClinicalStatements::getSubstanceAdministrationEvents)
                .map(EvaluatedPerson.ClinicalStatements.SubstanceAdministrationEvents::getSubstanceAdministrationEvent)
                .stream()
                .flatMap(Collection::stream)
                .flatMap(substanceAdministrationEvent -> toEvaluations(kmId, substanceAdministrationEvent))
                .map(eval -> ImmunizationEvaluation.builder()
                        .resourceType(RESOURCE_TYPE_IMMUNIZATION_EVALUATION)
                        .text(createNarrative(buildEvaluationNarrative(eval)))
                        .id(eval.id())
                        .identifier(eval.identifier())
                        .status(eval.status() == null ? STATUS_COMPLETED : eval.status())
                        .patient(patientReference)
                        .date(eval.date())
                        .authority(eval.authority())
                        .targetDisease(eval.targetDisease())
                        .immunizationEvent(eval.immunizationEvent())
                        .doseStatus(eval.doseStatus())
                        .doseStatusReason(eval.doseStatusReason())
                        .extension(eval.extension())
                        .description(eval.description())
                        .series(eval.series())
                        .doseNumber(eval.doseNumber())
                        .seriesDoses(eval.seriesDoses())
                        .build())
                .sorted(Comparator.comparing(
                        eval -> Optional.ofNullable(eval.targetDisease()).map(CodeableConcept::text).orElse(""),
                        Comparator.nullsLast(String::compareTo)))
                .toList();
    }

    private List<ImmunizationRecommendation> buildImmunizationRecommendations(final String kmId,
            final Optional<EvaluatedPerson> outputPatient, final Reference patientReference, final LocalDate assessmentDate)
    {
        final List<SubstanceAdministrationProposal> proposals = outputPatient.map(EvaluatedPerson::getClinicalStatements)
                .map(EvaluatedPerson.ClinicalStatements::getSubstanceAdministrationProposals)
                .map(EvaluatedPerson.ClinicalStatements.SubstanceAdministrationProposals::getSubstanceAdministrationProposal)
                .stream()
                .flatMap(Collection::stream)
                .toList();
        if (proposals.isEmpty())
            return List.of();

        final List<ImmunizationRecommendationRecommendation> nestedRecommendations = proposals.stream()
                .map(substanceAdministrationProposal -> toRecommendation(kmId, substanceAdministrationProposal))
                .sorted(Comparator.comparing(rec -> rec.targetDisease().stream().map(CodeableConcept::text).findFirst().orElse(""),
                        Comparator.nullsLast(String::compareTo)))
                .toList();

        final String recommendationId = proposals.stream()
                .map(SubstanceAdministrationProposal::getId)
                .map(this::selectIiExtensionOrRoot)
                .filter(this::hasText)
                .findFirst()
                .orElse(null);
        final String narrative = nestedRecommendations.isEmpty()
                                 ? null
                                 : nestedRecommendations.size() == 1
                                   ? buildRecommendationNarrative(nestedRecommendations.getFirst())
                                   : "Recommendations generated: %d entries.".formatted(nestedRecommendations.size());
        return List.of(ImmunizationRecommendation.builder()
                .resourceType(RESOURCE_TYPE_IMMUNIZATION_RECOMMENDATION)
                .id(recommendationId)
                .text(createNarrative(narrative))
                .patient(patientReference)
                .date(assessmentDate)
                .recommendation(nestedRecommendations)
                .build());
    }

    private RequestContext parseRequestContext(final Parameters parameters)
    {
        final List<ParametersParameter> params = Optional.ofNullable(parameters).map(Parameters::parameter).orElse(List.of());

        final LocalDate assessmentDate = params.stream()
                .filter(parameter -> ASSESSMENT_DATE_PARAM.equals(parameter.name()))
                .map(parameter -> hasText(parameter.valueDate())
                                  ? parseIsoLocalDate(parameter.valueDate())
                                  : hasText(parameter.valueDateTime()) ? parseIsoLocalDate(parameter.valueDateTime()) : null)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Parameters must contain assessmentDate"));

        final String moduleCanonical = params.stream()
                .filter(parameter -> MODULE_PARAM.equals(parameter.name()))
                .map(ParametersParameter::valueCanonical)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Parameters must contain module valueCanonical"));

        final Bundle dataBundle = params.stream()
                .filter(parameter -> DATA_PARAM.equals(parameter.name()))
                .map(ParametersParameter::resource)
                .filter(Bundle.class::isInstance)
                .map(Bundle.class::cast)
                .findFirst()
                .orElse(null);

        final List<FhirResource> bundleResources = Optional.ofNullable(dataBundle)
                .map(Bundle::entry)
                .stream()
                .flatMap(Collection::stream)
                .map(BundleEntry::resource)
                .filter(Objects::nonNull)
                .toList();

        final List<Patient> patientResources = params.stream()
                .filter(parameter -> PATIENT_PARAM.equals(parameter.name()))
                .map(ParametersParameter::resource)
                .filter(Patient.class::isInstance)
                .map(Patient.class::cast)
                .toList();
        final List<Patient> patientBundleResources =
                bundleResources.stream().filter(Patient.class::isInstance).map(Patient.class::cast).toList();

        final Patient patient = Stream.concat(patientResources.stream(), patientBundleResources.stream())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Parameters must contain a Patient resource"));

        final List<Immunization> immunizationBundleResources =
                bundleResources.stream().filter(Immunization.class::isInstance).map(Immunization.class::cast).toList();
        final List<Immunization> immunizations = Stream.concat(params.stream()
                .filter(parameter -> IMMUNIZATION_PARAM.equals(parameter.name()))
                .map(ParametersParameter::resource)
                .filter(Immunization.class::isInstance)
                .map(Immunization.class::cast), immunizationBundleResources.stream()).toList();

        final List<Observation> observationBundleResources =
                bundleResources.stream().filter(Observation.class::isInstance).map(Observation.class::cast).toList();
        final List<Observation> observations = Stream.concat(params.stream()
                .filter(parameter -> OBSERVATION_PARAM.equals(parameter.name()))
                .map(ParametersParameter::resource)
                .filter(Observation.class::isInstance)
                .map(Observation.class::cast), observationBundleResources.stream()).toList();

        return new RequestContext(assessmentDate, moduleCanonical, patient, immunizations, observations);
    }

    private ImmunizationRecommendationRecommendation toRecommendation(final String kmId,
            final SubstanceAdministrationProposal substanceAdministrationProposal)
    {
        final ImmunizationRecommendationRecommendation.ImmunizationRecommendationRecommendationBuilder recommendationBuilder =
                ImmunizationRecommendationRecommendation.builder();
        final List<RelatedClinicalStatement> relatedClinicalStatements =
                getRelatedClinicalStatements(substanceAdministrationProposal);

        recommendationBuilder.vaccineCode(Optional.ofNullable(substanceAdministrationProposal.getSubstance())
                .map(AdministrableSubstance::getSubstanceCode)
                .map(cd -> toFhirCodeableConcept(kmId, cd))
                .map(this::withTextFromCodingDisplay)
                .filter(this::isCvxCodeableConcept)
                .stream()
                .toList());

        final Optional<ObservationResult> observationResult = findPrimaryObservationResultFromStatements(relatedClinicalStatements);

        final CodeableConcept targetDisease = observationResult.map(ObservationResult::getObservationFocus)
                .map(focus -> buildTargetDiseaseCodeableConcept(kmId, focus))
                .orElse(null);
        recommendationBuilder.targetDisease(Optional.ofNullable(targetDisease).stream().toList());

        final CodeableConcept forecastStatus = observationResult.map(ObservationResult::getObservationValue)
                .map(ObservationResult.ObservationValue::getConcept)
                .map(cd -> toFhirCodeableConcept(kmId, cd))
                .map(this::withTextFromCodingDisplay)
                .orElse(null);
        recommendationBuilder.forecastStatus(forecastStatus);

        final List<CodeableConcept> forecastReasons = observationResult.map(ObservationResult::getInterpretation)
                .stream()
                .flatMap(Collection::stream)
                .map(cd -> toFhirCodeableConcept(kmId, cd))
                .map(this::withTextFromCodingDisplay)
                .toList();
        recommendationBuilder.forecastReason(forecastReasons);

        final ProtocolContext protocolContext =
                extractProtocolContext(kmId, relatedClinicalStatements, shouldOutputSeriesContext(kmId));
        recommendationBuilder.series(protocolContext.series());
        recommendationBuilder.seriesDoses(protocolContext.seriesDoses());
        recommendationBuilder.description(Optional.ofNullable(
                        buildProtocolDetailsDescription(protocolContext.seriesSelectionInfo(), protocolContext.seasonInfo(),
                                protocolContext.series(), protocolContext.seriesDoses()))
                .orElseGet(() -> buildRecommendationDescriptionFallback(forecastStatus, forecastReasons)));
        recommendationBuilder.extension(protocolContext.extensions());

        final List<ImmunizationRecommendationDateCriterion> dateCriteria = new ArrayList<>();
        Optional.ofNullable(substanceAdministrationProposal.getProposedAdministrationTimeInterval())
                .map(VmrConversionComponent::toTimeInterval)
                .ifPresent(proposedTimeInterval ->
                {
                    if (proposedTimeInterval.low() != null)
                        dateCriteria.add(createDateCriterion(DATE_CRITERION_DUE, proposedTimeInterval.low()));
                    if (proposedTimeInterval.high() != null)
                        dateCriteria.add(createDateCriterion(DATE_CRITERION_OVERDUE, proposedTimeInterval.high()));
                });

        Optional.ofNullable(substanceAdministrationProposal.getValidAdministrationTimeInterval())
                .map(VmrConversionComponent::toTimeInterval)
                .ifPresent(validTimeInterval ->
                {
                    if (validTimeInterval.low() != null)
                        dateCriteria.add(createDateCriterion(DATE_CRITERION_EARLIEST, validTimeInterval.low()));
                    if (validTimeInterval.high() != null)
                        dateCriteria.add(createDateCriterion(DATE_CRITERION_LATEST, validTimeInterval.high()));
                });
        recommendationBuilder.dateCriterion(dateCriteria);

        return recommendationBuilder.build();
    }

    private Optional<String> extractCodeableConceptValueText(final CodeableConcept codeableConcept)
    {
        final Optional<String> fromText =
                Optional.ofNullable(codeableConcept).map(CodeableConcept::text).map(String::trim).filter(text -> !text.isEmpty());
        if (fromText.isPresent())
            return fromText;

        final Optional<String> fromDisplay = Optional.ofNullable(codeableConcept)
                .map(CodeableConcept::coding)
                .stream()
                .flatMap(Collection::stream)
                .map(Coding::display)
                .filter(this::hasText)
                .map(String::trim)
                .filter(display -> !display.isEmpty())
                .findFirst();
        if (fromDisplay.isPresent())
            return fromDisplay;

        return Optional.ofNullable(codeableConcept)
                .map(CodeableConcept::coding)
                .stream()
                .flatMap(Collection::stream)
                .map(Coding::code)
                .filter(this::hasText)
                .map(String::trim)
                .filter(code -> !code.isEmpty())
                .findFirst();
    }

    private Optional<Extension> createSelectionContextExtension(final String kmId, final SeriesSelectionInfo seriesSelectionInfo,
            final SeasonInfo seasonInfo)
    {
        final List<Extension> nestedExtensions = new ArrayList<>(3);

        if (seriesSelectionInfo != null)
        {
            nestedExtensions.add(Extension.builder()
                    .url(SELECTION_CONTEXT_CHILD_URL_SELECTED_SERIES)
                    .valueCodeableConcept(CodeableConcept.builder()
                            .coding(Coding.builder()
                                    .system(supportingDataService.toFhirCodeSystemUrl(kmId, SERIES_CODE_SYSTEM))
                                    .code(seriesSelectionInfo.seriesCode())
                                    .display(seriesSelectionInfo.seriesDisplay())
                                    .build())
                            .text(seriesSelectionInfo.seriesDisplay())
                            .build())
                    .build());

            final String selectionTypeDisplay = Optional.ofNullable(
                            supportingDataService.getSeriesSelectionTypeDisplayName(kmId, seriesSelectionInfo.selectionTypeCode()))
                    .orElseThrow(() -> new IllegalStateException(
                            "Missing display for series selection type '%s' in SERIES_DISPLAY_SELECTION_TYPE".formatted(
                                    seriesSelectionInfo.selectionTypeCode())));
            nestedExtensions.add(Extension.builder()
                    .url(SELECTION_CONTEXT_CHILD_URL_SERIES_SELECTION_TYPE)
                    .valueCodeableConcept(CodeableConcept.builder()
                            .coding(Coding.builder()
                                    .system(supportingDataService.toFhirCodeSystemUrl(kmId, SERIES_SELECTION_TYPE_CODE_SYSTEM))
                                    .code(seriesSelectionInfo.selectionTypeCode())
                                    .display(selectionTypeDisplay)
                                    .build())
                            .text(selectionTypeDisplay)
                            .build())
                    .build());
        }

        if (seasonInfo != null)
        {
            nestedExtensions.add(Extension.builder()
                    .url(SELECTION_CONTEXT_CHILD_URL_SELECTED_SEASON)
                    .valueCodeableConcept(CodeableConcept.builder()
                            .coding(Coding.builder()
                                    .system(supportingDataService.toFhirCodeSystemUrl(kmId, seasonInfo.seasonCodeSystem()))
                                    .code(seasonInfo.seasonCode())
                                    .display(seasonInfo.seasonDisplay())
                                    .build())
                            .text(seasonInfo.seasonDisplay())
                            .build())
                    .build());
        }

        if (nestedExtensions.isEmpty())
            return Optional.empty();

        return Optional.of(Extension.builder().url(SELECTION_CONTEXT_EXTENSION_URL).extension(nestedExtensions).build());
    }

    private ImmunizationRecommendationDateCriterion createDateCriterion(final Coding criterionCoding, final LocalDate date)
    {
        if (criterionCoding == null || !hasText(criterionCoding.system()) || !hasText(criterionCoding.code()))
            throw new IllegalArgumentException("Date criterion coding is not configured");
        final String display = hasText(criterionCoding.display()) ? criterionCoding.display() : criterionCoding.code();
        return ImmunizationRecommendationDateCriterion.builder()
                .code(CodeableConcept.builder()
                        .coding(Coding.builder()
                                .system(criterionCoding.system())
                                .code(criterionCoding.code())
                                .display(display)
                                .build())
                        .text(display)
                        .build())
                .value(toFhirDate(date))
                .build();
    }

    private Stream<ImmunizationEvaluation> toEvaluation(final String kmId, final String extension,
            final SubstanceAdministrationEvent substanceAdministrationEvent)
    {
        final List<RelatedClinicalStatement> relatedClinicalStatements = getRelatedClinicalStatements(substanceAdministrationEvent);
        final CodeableConcept doseNumber = Optional.ofNullable(substanceAdministrationEvent)
                .map(SubstanceAdministrationEvent::getDoseNumber)
                .map(INT::getValue)
                .map(this::toNumericCodeableConcept)
                .orElse(null);

        final Optional<ObservationResult> observationResult = findPrimaryObservationResultFromStatements(relatedClinicalStatements);

        final CodeableConcept targetDisease = observationResult.map(ObservationResult::getObservationFocus)
                .map(focus -> buildTargetDiseaseCodeableConcept(kmId, focus))
                .orElse(null);

        final CodeableConcept doseStatus = observationResult.map(ObservationResult::getObservationValue)
                .map(ObservationResult.ObservationValue::getConcept)
                .map(cd -> toFhirCodeableConcept(kmId, cd))
                .map(this::withTextFromCodingDisplay)
                .orElse(null);

        final List<CodeableConcept> doseStatusReasons = observationResult.map(ObservationResult::getInterpretation)
                .stream()
                .flatMap(Collection::stream)
                .map(cd -> toFhirCodeableConcept(kmId, cd))
                .map(this::withTextFromCodingDisplay)
                .toList();

        final ProtocolContext protocolContext =
                extractProtocolContext(kmId, relatedClinicalStatements, shouldOutputSeriesContext(kmId));
        final Reference immunizationEventReference =
                !hasText(extension) ? null : Reference.builder().reference(REFERENCE_PREFIX_IMMUNIZATION + extension).build();
        final String evaluationDate = Optional.ofNullable(substanceAdministrationEvent)
                .map(SubstanceAdministrationEvent::getAdministrationTimeInterval)
                .map(VmrConversionComponent::toTimeInterval)
                .map(TimeInterval::low)
                .map(this::toFhirDate)
                .orElse(null);

        return Stream.of(ImmunizationEvaluation.builder()
                .id(extension)
                .status(STATUS_COMPLETED)
                .date(evaluationDate)
                .targetDisease(targetDisease)
                .immunizationEvent(immunizationEventReference)
                .doseStatus(doseStatus)
                .doseStatusReason(doseStatusReasons)
                .extension(protocolContext.extensions())
                .description(Optional.ofNullable(
                                buildProtocolDetailsDescription(protocolContext.seriesSelectionInfo(), protocolContext.seasonInfo(),
                                        protocolContext.series(), protocolContext.seriesDoses()))
                        .orElseGet(() -> buildEvaluationDescriptionFallback(doseStatus, doseStatusReasons)))
                .series(protocolContext.series())
                .doseNumber(doseNumber)
                .seriesDoses(protocolContext.seriesDoses())
                .build());
    }

    private Stream<ImmunizationEvaluation> toEvaluations(final String kmId,
            final SubstanceAdministrationEvent substanceAdministrationEvent)
    {
        final String sourceEventId = Optional.ofNullable(substanceAdministrationEvent)
                .map(SubstanceAdministrationEvent::getId)
                .map(this::selectIiExtensionOrRoot)
                .orElse(null);
        final List<RelatedClinicalStatement> relatedClinicalStatements = getRelatedClinicalStatements(substanceAdministrationEvent);

        final List<ImmunizationEvaluation> relatedEvaluations = relatedClinicalStatements.stream()
                .map(RelatedClinicalStatement::getSubstanceAdministrationEvent)
                .filter(Objects::nonNull)
                .flatMap(relatedEvent -> toEvaluation(kmId, sourceEventId, relatedEvent))
                .toList();
        if (!relatedEvaluations.isEmpty())
            return relatedEvaluations.stream();

        return findPrimaryObservationResultFromStatements(relatedClinicalStatements).map(
                _ -> toEvaluation(kmId, sourceEventId, substanceAdministrationEvent)).orElseGet(Stream::empty);
    }

    private byte[] createPayload(final CDSInput cdsInput)
    {
        try (final ByteArrayOutputStream bOut = new ByteArrayOutputStream())
        {
            xmlMapper.writer().withRootName(XML_ROOT_CDS_INPUT).writeValue(bOut, cdsInput);
            return bOut.toByteArray();
        }
        catch (final IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private EvaluateAtSpecifiedTime createEvaluateAtSpecifiedTime(final InteractionIdentifier interactionId,
            final XMLGregorianCalendar specifiedTime, final EvaluationRequest evaluationRequest)
    {
        final EvaluateAtSpecifiedTime evaluateAtSpecifiedTime = new EvaluateAtSpecifiedTime();

        evaluateAtSpecifiedTime.setInteractionId(interactionId);
        evaluateAtSpecifiedTime.setSpecifiedTime(specifiedTime);
        evaluateAtSpecifiedTime.setEvaluationRequest(evaluationRequest);
        return evaluateAtSpecifiedTime;
    }

    private EvaluationRequest createEvaluationRequest(final String clientTimeZoneOffset,
            final List<KMEvaluationRequest> kmEvaluationRequest, final List<DataRequirementItemData> dataRequirementItemData)
    {
        final EvaluationRequest request = new EvaluationRequest();
        request.setClientLanguage(CLIENT_LANGUAGE_CODE);
        request.setClientTimeZoneOffset(clientTimeZoneOffset);
        if (kmEvaluationRequest != null)
            request.getKmEvaluationRequest().addAll(kmEvaluationRequest);
        if (dataRequirementItemData != null)
            request.getDataRequirementItemData().addAll(dataRequirementItemData);
        return request;
    }

    private CDSInput getCdsInput(final String kmId, final RequestContext requestContext)
    {
        return createCdsInput(List.of(copyIi(templateIdCdsInputPrototype)), createCdsContext(copyCd(clientLanguageCdPrototype)),
                createVmr(List.of(copyIi(templateIdCdsInputPrototype)), createEvaluatedPerson(kmId, requestContext)));
    }

    private String getTimezone()
    {
        return ZonedDateTime.now().format(tzFormat);
    }

    private DataRequirementItemData createDataRequirementItemData(final ItemIdentifier driId, final SemanticPayload data)
    {
        final DataRequirementItemData itemData = new DataRequirementItemData();
        itemData.setDriId(driId);
        itemData.setData(data);
        return itemData;
    }

    private KMEvaluationRequest createKmEvaluationRequest(final EntityIdentifier kmId)
    {
        final KMEvaluationRequest request = new KMEvaluationRequest();
        request.setKmId(kmId);
        return request;
    }

    private SemanticPayload createSemanticPayload(final EntityIdentifier informationModelSSId,
            final List<byte[]> base64EncodedPayload)
    {
        final SemanticPayload payload = new SemanticPayload();
        payload.setInformationModelSSId(informationModelSSId);
        if (base64EncodedPayload != null)
            payload.getBase64EncodedPayload().addAll(base64EncodedPayload);
        return payload;
    }

    private ItemIdentifier createItemIdentifier(final EntityIdentifier containingEntityId)
    {
        final ItemIdentifier itemIdentifier = new ItemIdentifier();
        itemIdentifier.setContainingEntityId(containingEntityId);
        itemIdentifier.setItemId(ITEM_ID_CDS_PAYLOAD);
        return itemIdentifier;
    }

    private EvaluatedPerson createEvaluatedPerson(final String kmId, final RequestContext requestContext)
    {
        final EvaluatedPerson evaluatedPerson = new EvaluatedPerson();

        evaluatedPerson.getTemplateId().add(copyIi(templateIdEvaluatedPersonPrototype));
        final String patientIdentifier = Optional.ofNullable(requestContext.patient())
                .map(Patient::identifier)
                .map(this::extractPrimaryIdentifierValue)
                .orElse(null);
        evaluatedPerson.setId(createIi(EVALUATED_PERSON_ID_ROOT, patientIdentifier));

        final EvaluatedPerson.Demographics demographics = new EvaluatedPerson.Demographics();
        evaluatedPerson.setDemographics(demographics);

        final TS birthTime = new TS();
        birthTime.setValue(formatVmrDate(Objects.requireNonNull(requestContext.patient()).birthDate()));
        demographics.setBirthTime(birthTime);
        final String sex = toVmrAdministrativeGenderCode(Optional.of(requestContext.patient()).map(Patient::gender).orElse(null));
        if (hasText(sex))
            demographics.setGender(createCd(sex, V3_ADMINISTRATIVE_GENDER_OID, sex));
        final EvaluatedPerson.ClinicalStatements clinicalStatements = new EvaluatedPerson.ClinicalStatements();
        evaluatedPerson.setClinicalStatements(clinicalStatements);
        clinicalStatements.setObservationResults(new EvaluatedPerson.ClinicalStatements.ObservationResults());

        clinicalStatements.getObservationResults()
                .getObservationResult()
                .addAll(Optional.ofNullable(requestContext.observations())
                        .stream()
                        .flatMap(Collection::stream)
                        .map(observation -> createObservationResult(kmId, observation))
                        .toList());

        clinicalStatements.setSubstanceAdministrationEvents(createSubstanceAdministrationEvents(
                Optional.ofNullable(requestContext.immunizations())
                        .stream()
                        .flatMap(Collection::stream)
                        .map(immunization -> createSubstanceAdministrationEvent(kmId, immunization))
                        .toList()));

        return evaluatedPerson;
    }

    private EvaluatedPerson.ClinicalStatements.SubstanceAdministrationEvents createSubstanceAdministrationEvents(
            final List<SubstanceAdministrationEvent> substanceAdministrationEvent)
    {
        final EvaluatedPerson.ClinicalStatements.SubstanceAdministrationEvents substanceAdministrationEvents =
                new EvaluatedPerson.ClinicalStatements.SubstanceAdministrationEvents();
        if (substanceAdministrationEvent != null)
            substanceAdministrationEvents.getSubstanceAdministrationEvent().addAll(substanceAdministrationEvent);
        return substanceAdministrationEvents;
    }

    private EntityIdentifier createEntityIdentifier(final String scopingEntityId, final String businessId, final String version)
    {
        final EntityIdentifier entityIdentifier = new EntityIdentifier();
        entityIdentifier.setScopingEntityId(scopingEntityId);
        entityIdentifier.setBusinessId(businessId);
        entityIdentifier.setVersion(version);
        return entityIdentifier;
    }

    private InteractionIdentifier createInteractionIdentifier(final String interactionId, final XMLGregorianCalendar submissionTime,
            final String scopingEntityId)
    {
        final InteractionIdentifier interactionIdentifier = new InteractionIdentifier();
        interactionIdentifier.setScopingEntityId(scopingEntityId);
        interactionIdentifier.setInteractionId(interactionId);
        interactionIdentifier.setSubmissionTime(submissionTime);
        return interactionIdentifier;
    }

    private CDSInput createCdsInput(final List<II> templateId, final CDSContext cdsContext, final VMR vmrInput)
    {
        final CDSInput cdsInput = new CDSInput();
        if (templateId != null)
            cdsInput.getTemplateId().addAll(templateId);
        cdsInput.setCdsContext(cdsContext);
        cdsInput.setVmrInput(vmrInput);
        return cdsInput;
    }

    private CDSContext createCdsContext(final CD cdsSystemUserPreferredLanguage)
    {
        final CDSContext cdsContext = new CDSContext();
        cdsContext.setCdsSystemUserPreferredLanguage(cdsSystemUserPreferredLanguage);
        return cdsContext;
    }

    private VMR createVmr(final List<II> templateId, final EvaluatedPerson patient)
    {
        final VMR vmr = new VMR();
        if (templateId != null)
            vmr.getTemplateId().addAll(templateId);
        vmr.setPatient(patient);
        return vmr;
    }

    private II createIi(final String root, final String extension)
    {
        final II ii = new II();
        ii.setRoot(root);
        ii.setExtension(extension);
        return ii;
    }

    private II copyIi(final II prototype)
    {
        return createIi(prototype.getRoot(), prototype.getExtension());
    }

    private String selectIiExtensionOrRoot(final II identifier)
    {
        if (identifier == null)
            return null;
        return StringUtils.hasText(identifier.getExtension()) ? identifier.getExtension() : identifier.getRoot();
    }

    private boolean hasText(final String value)
    {
        return StringUtils.hasText(value);
    }

    private CD createCd(final String code, final String codeSystem, final String displayName)
    {
        final CD cd = new CD();
        cd.setCode(code);
        cd.setCodeSystem(codeSystem);
        cd.setDisplayName(displayName);
        return cd;
    }

    private CD copyCd(final CD prototype)
    {
        return createCd(prototype.getCode(), prototype.getCodeSystem(), prototype.getDisplayName());
    }

    private CD createCd(final String kmId, final CodeableConcept codeableConcept)
    {
        final CD cd = new CD();
        if (codeableConcept != null && !ObjectUtils.isEmpty(codeableConcept.coding()))
        {
            codeableConcept.coding().stream().findFirst().ifPresent((coding) ->
            {
                cd.setCode(coding.code());
                cd.setCodeSystem(supportingDataService.toRequiredInternalCodeSystemOid(kmId, coding.system()));
                cd.setDisplayName(hasText(coding.display()) ? coding.display() : codeableConcept.text());
            });
        }
        else
        {
            log.warn("Unable to convert codeable concept to CD: {}", codeableConcept);
        }

        return cd;
    }

    private ObservationResult createObservationResult(final String kmId, final Observation observation)
    {
        final ObservationResult observationResult = new ObservationResult();
        observationResult.getTemplateId().add(copyIi(templateIdObservationResultPrototype));
        observationResult.setId(createIi(UUID.randomUUID().toString(), null));
        observationResult.setObservationEventTime(Optional.ofNullable(observation.effectiveDateTime())
                .map(this::parseIsoLocalDate)
                .map(VmrConversionComponent::formatVmrDate)
                .map((d) -> createIvlts(d, d))
                .orElse(null));
        observationResult.setObservationFocus(createCd(kmId, observation.code()));
        observationResult.getInterpretation()
                .addAll(Optional.ofNullable(observation.interpretation())
                        .stream()
                        .flatMap(Collection::stream)
                        .map(codeableConcept -> createObservationInterpretationCd(kmId, codeableConcept))
                        .toList());
        final ObservationResult.ObservationValue observationValue = new ObservationResult.ObservationValue();
        observationResult.setObservationValue(observationValue);
        observationValue.setConcept(createObservationValueCd(kmId, observation.valueCodeableConcept()));
        return observationResult;
    }

    private CD createObservationValueCd(final String kmId, final CodeableConcept valueCodeableConcept)
    {
        final CD cd = createCd(kmId, valueCodeableConcept);
        if (!hasText(cd.getCode()) || !hasText(cd.getCodeSystem()))
            return cd;

        if (supportingDataService.isCodeInCodeSystem(kmId, ICEConceptType.DISEASE_IMMUNITY_SOURCE.getIceConceptTypeValue(),
                cd.getCode()) && (RECOMMENDATION_REASON_CODE_SYSTEM.equals(cd.getCodeSystem())
                || EVALUATION_REASON_CODE_SYSTEM.equals(cd.getCodeSystem())))
            cd.setCodeSystem(DISEASE_IMMUNITY_SOURCE_CODE_SYSTEM);

        return cd;
    }

    private CD createObservationInterpretationCd(final String kmId, final CodeableConcept interpretationCodeableConcept)
    {
        final CD cd = createCd(kmId, interpretationCodeableConcept);
        if (!hasText(cd.getCode()))
            return cd;

        if (supportingDataService.isCodeInCodeSystem(kmId, ICEConceptType.DISEASE_IMMUNITY_REASON.getIceConceptTypeValue(),
                cd.getCode()))
            cd.setCodeSystem(DISEASE_IMMUNITY_REASON_CODE_SYSTEM);

        return cd;
    }

    private IVLTS createIvlts(final String high, final String low)
    {
        final IVLTS ivlts = new IVLTS();
        ivlts.setHigh(high);
        ivlts.setLow(low);
        return ivlts;
    }

    private SubstanceAdministrationEvent createSubstanceAdministrationEvent(final String kmId, final Immunization immunization)
    {
        final SubstanceAdministrationEvent substanceAdministrationEvent = new SubstanceAdministrationEvent();
        substanceAdministrationEvent.getTemplateId().add(copyIi(templateIdSubstanceAdministrationEventPrototype));
        substanceAdministrationEvent.setSubstanceAdministrationGeneralPurpose(
                copyCd(substanceAdministrationGeneralPurposeCdPrototype));
        final String immunizationIdentifier = extractPrimaryIdentifierValue(immunization.identifier());
        substanceAdministrationEvent.setId(createIi(IMMUNIZATION_ID_ROOT,
                hasText(immunizationIdentifier) ? immunizationIdentifier : UUID.randomUUID().toString()));
        substanceAdministrationEvent.setAdministrationTimeInterval(Optional.ofNullable(immunization.occurrenceDateTime())
                .map(this::parseIsoLocalDate)
                .map(VmrConversionComponent::formatVmrDate)
                .map((d) -> createIvlts(d, d))
                .orElse(null));
        substanceAdministrationEvent.setSubstance(createAdministrableSubstance(createIi(UUID.randomUUID().toString(), null),
                createCd(kmId, immunization.vaccineCode())));
        return substanceAdministrationEvent;
    }

    private AdministrableSubstance createAdministrableSubstance(final II id, final CD substanceCode)
    {
        final AdministrableSubstance administrableSubstance = new AdministrableSubstance();
        administrableSubstance.setId(id);
        administrableSubstance.setSubstanceCode(substanceCode);
        return administrableSubstance;
    }

    private Optional<ObservationResult> findPrimaryObservationResultFromStatements(
            final Collection<RelatedClinicalStatement> relatedClinicalStatements)
    {
        final List<ObservationResult> observationResults = Optional.ofNullable(relatedClinicalStatements)
                .stream()
                .flatMap(Collection::stream)
                .map(RelatedClinicalStatement::getObservationResult)
                .filter(Objects::nonNull)
                .toList();

        return observationResults.stream()
                .filter(observationResult -> Optional.ofNullable(observationResult)
                        .map(ObservationResult::getObservationFocus)
                        .map(CD::getCodeSystem)
                        .filter(VACCINE_GROUP_CODE_SYSTEM::equals)
                        .isPresent())
                .findFirst()
                .or(() -> observationResults.stream()
                        .filter(observationResult -> !isSeriesDisplayObservationResult(observationResult))
                        .findFirst()
                        .or(() -> observationResults.stream().findFirst()));
    }

    private boolean isSeriesDisplayObservationResult(final ObservationResult observationResult)
    {
        final String focusCodeSystem = Optional.ofNullable(observationResult)
                .map(ObservationResult::getObservationFocus)
                .map(CD::getCodeSystem)
                .orElse(null);
        return hasText(focusCodeSystem) && focusCodeSystem.equals(SERIES_SELECTION_TYPE_CODE_SYSTEM);
    }

    private String toVmrAdministrativeGenderCode(final AdministrativeGender administrativeGender)
    {
        if (administrativeGender == null)
            return null;
        return switch (administrativeGender)
        {
            case MALE -> ADMIN_GENDER_CODE_MALE;
            case FEMALE -> ADMIN_GENDER_CODE_FEMALE;
            case OTHER, UNKNOWN -> ADMIN_GENDER_CODE_UNKNOWN;
        };
    }

    private boolean isCvxCodeableConcept(final CodeableConcept codeableConcept)
    {
        if (codeableConcept == null)
            return false;

        return Optional.ofNullable(codeableConcept.coding())
                .stream()
                .flatMap(Collection::stream)
                .map(Coding::system)
                .filter(Objects::nonNull)
                .map(String::trim)
                .anyMatch(system -> FHIR_CVX_SYSTEM.equalsIgnoreCase(system) || CVX_OID.equals(system) || (OID_URN_PREFIX
                        + CVX_OID).equalsIgnoreCase(system));
    }

    private String extractPrimaryIdentifierValue(final List<Identifier> identifiers)
    {
        return Optional.ofNullable(identifiers)
                .stream()
                .flatMap(Collection::stream)
                .map(Identifier::value)
                .filter(this::hasText)
                .findFirst()
                .orElse(null);
    }

    private CodeableConcept withTextFromCodingDisplay(final CodeableConcept concept)
    {
        if (concept == null)
            return null;
        final String display = Optional.ofNullable(concept.coding())
                .stream()
                .flatMap(Collection::stream)
                .map(Coding::display)
                .filter(this::hasText)
                .findFirst()
                .orElse(null);
        if (!hasText(display))
            return concept;
        if (display.equals(concept.text()))
            return concept;
        return CodeableConcept.builder().coding(concept.coding()).text(display).build();
    }

    private CodeableConcept toNumericCodeableConcept(final Integer value)
    {
        if (value == null)
            return null;
        return CodeableConcept.builder().text(String.valueOf(value)).build();
    }

    private Optional<CodeableConcept> extractSeriesDosesFromRelatedClinicalStatements(
            final List<RelatedClinicalStatement> relatedClinicalStatements)
    {
        return findSeriesDisplayOptionsObservation(relatedClinicalStatements).stream()
                .flatMap(this::streamObservationResultTree)
                .filter(observationResult -> Optional.ofNullable(observationResult)
                        .map(ObservationResult::getObservationFocus)
                        .map(CD::getCode)
                        .filter(NUMBER_OF_DOSES_REMAINING_FOCUS_CODE::equals)
                        .isPresent())
                .map(this::extractObservationValueAsCodeableConcept)
                .flatMap(Optional::stream)
                .findFirst();
    }

    private Optional<CodeableConcept> extractObservationValueAsCodeableConcept(final ObservationResult observationResult)
    {
        final Optional<CodeableConcept> integerValue = Optional.ofNullable(observationResult)
                .map(ObservationResult::getObservationValue)
                .map(ObservationResult.ObservationValue::getInteger)
                .map(INT::getValue)
                .map(this::toNumericCodeableConcept);
        if (integerValue.isPresent())
            return integerValue;

        final String textValue = Optional.ofNullable(observationResult)
                .map(ObservationResult::getObservationValue)
                .map(ObservationResult.ObservationValue::getText)
                .map(org.opencds.vmr.v1_0.schema.ST::getValue)
                .map(String::trim)
                .orElse(null);
        if (hasText(textValue))
            return Optional.of(CodeableConcept.builder().text(textValue).build());

        final CD conceptValue = Optional.ofNullable(observationResult)
                .map(ObservationResult::getObservationValue)
                .map(ObservationResult.ObservationValue::getConcept)
                .orElse(null);
        if (conceptValue == null || (!hasText(conceptValue.getCode()) && !hasText(conceptValue.getDisplayName())))
            return Optional.empty();

        final Optional<Coding> coding = (!hasText(conceptValue.getCode()) && !hasText(conceptValue.getCodeSystem()) && !hasText(
                conceptValue.getDisplayName()))
                                        ? Optional.empty()
                                        : Optional.of(Coding.builder()
                                                      .code(conceptValue.getCode())
                                                      .system(conceptValue.getCodeSystem())
                                                      .display(conceptValue.getDisplayName())
                                                      .build());

        final CodeableConcept.CodeableConceptBuilder conceptBuilder =
                CodeableConcept.builder().text(Optional.ofNullable(conceptValue.getDisplayName()).orElse(conceptValue.getCode()));
        coding.ifPresent(conceptBuilder::coding);
        return Optional.of(conceptBuilder.build());
    }

    private Optional<SeriesSelectionInfo> extractSeriesSelectionInfo(final String kmId,
            final List<RelatedClinicalStatement> relatedClinicalStatements)
    {
        return findSeriesDisplayOptionsObservation(relatedClinicalStatements).stream()
                .flatMap(this::streamObservationResultTree)
                .filter(this::isSeriesSelectionTypeObservation)
                .map(observationResult -> toSeriesSelectionInfo(kmId, observationResult))
                .flatMap(Optional::stream)
                .findFirst();
    }

    private ProtocolContext extractProtocolContext(final String kmId,
            final List<RelatedClinicalStatement> relatedClinicalStatements, final boolean outputSeriesContext)
    {
        if (!outputSeriesContext)
            return new ProtocolContext(null, null, null, null, List.of());

        final SeriesSelectionInfo seriesSelectionInfo = extractSeriesSelectionInfo(kmId, relatedClinicalStatements).orElse(null);
        final SeasonInfo seasonInfo = extractSeasonInfo(relatedClinicalStatements).orElse(null);
        final String series = Optional.ofNullable(seriesSelectionInfo)
                .map(SeriesSelectionInfo::seriesDisplay)
                .filter(this::hasText)
                .orElse(Optional.ofNullable(seriesSelectionInfo).map(SeriesSelectionInfo::seriesCode).orElse(null));
        final CodeableConcept seriesDoses = extractSeriesDosesFromRelatedClinicalStatements(relatedClinicalStatements).orElse(null);
        final List<Extension> extensions = createSelectionContextExtension(kmId, seriesSelectionInfo, seasonInfo).stream().toList();
        return new ProtocolContext(seriesSelectionInfo, seasonInfo, series, seriesDoses, extensions);
    }

    private List<RelatedClinicalStatement> getRelatedClinicalStatements(
            final SubstanceAdministrationProposal substanceAdministrationProposal)
    {
        return Optional.ofNullable(substanceAdministrationProposal)
                .map(SubstanceAdministrationProposal::getRelatedClinicalStatement)
                .orElse(List.of());
    }

    private List<RelatedClinicalStatement> getRelatedClinicalStatements(
            final SubstanceAdministrationEvent substanceAdministrationEvent)
    {
        return Optional.ofNullable(substanceAdministrationEvent)
                .map(SubstanceAdministrationEvent::getRelatedClinicalStatement)
                .orElse(List.of());
    }

    private CodeableConcept buildTargetDiseaseCodeableConcept(final String kmId, final CD observationFocus)
    {
        if (observationFocus == null)
            return null;

        final CodeableConcept focusConcept = withTextFromCodingDisplay(toFhirCodeableConcept(kmId, observationFocus));
        final List<CodeableConcept> mappedDiseases = Optional.ofNullable(observationFocus.getCode())
                .map(code -> supportingDataService.getDiseasesForVaccineGroup(kmId, code))
                .orElse(List.of());

        final Stream<CodeableConcept> conceptSource =
                mappedDiseases.isEmpty() ? Optional.ofNullable(focusConcept).stream() : mappedDiseases.stream();

        final List<Coding> codings = conceptSource.map(CodeableConcept::coding)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(Collectors.toMap(
                        coding -> "%s|%s".formatted(Optional.ofNullable(coding.system()).orElse(""),
                                Optional.ofNullable(coding.code()).orElse("")), coding -> coding, (left, _) -> left,
                        LinkedHashMap::new), map -> map.values().stream().toList()));

        final String displayText =
                codings.stream().map(Coding::display).filter(this::hasText).distinct().collect(Collectors.joining(", "));
        final String codeText =
                codings.stream().map(Coding::code).filter(this::hasText).distinct().collect(Collectors.joining(", "));
        final String text = hasText(displayText)
                            ? displayText
                            : hasText(codeText)
                              ? codeText
                              : Optional.ofNullable(focusConcept)
                                .map(CodeableConcept::text)
                                .orElse(Optional.ofNullable(observationFocus.getDisplayName()).orElse(observationFocus.getCode()));

        if (codings.isEmpty() && !hasText(text))
            return null;

        return CodeableConcept.builder().coding(codings).text(text).build();
    }

    private Optional<SeasonInfo> extractSeasonInfo(final List<RelatedClinicalStatement> relatedClinicalStatements)
    {
        return findSeriesDisplayOptionsObservation(relatedClinicalStatements).stream()
                .flatMap(this::streamObservationResultTree)
                .filter(this::isSeasonFocusObservation)
                .map(this::toSeasonInfo)
                .flatMap(Optional::stream)
                .findFirst();
    }

    private Optional<ObservationResult> findSeriesDisplayOptionsObservation(
            final List<RelatedClinicalStatement> relatedClinicalStatements)
    {
        return streamObservationResults(relatedClinicalStatements).filter(this::isSeriesDisplayOptionsFocusObservation).findFirst();
    }

    private Stream<ObservationResult> streamObservationResults(final List<RelatedClinicalStatement> relatedClinicalStatements)
    {
        return Optional.ofNullable(relatedClinicalStatements)
                .stream()
                .flatMap(Collection::stream)
                .map(RelatedClinicalStatement::getObservationResult)
                .filter(Objects::nonNull)
                .flatMap(this::streamObservationResultTree);
    }

    private Stream<ObservationResult> streamObservationResultTree(final ObservationResult observationResult)
    {
        return Stream.concat(Stream.of(observationResult),
                streamObservationResults(observationResult.getRelatedClinicalStatement()));
    }

    private boolean isSeriesSelectionTypeObservation(final ObservationResult observationResult)
    {
        return Optional.ofNullable(observationResult)
                .map(ObservationResult::getObservationFocus)
                .filter(observationFocus -> SERIES_SELECTION_TYPE_CODE_SYSTEM.equals(observationFocus.getCodeSystem()))
                .isPresent();
    }

    private boolean isSeriesDisplayOptionsFocusObservation(final ObservationResult observationResult)
    {
        return Optional.ofNullable(observationResult)
                .map(ObservationResult::getObservationFocus)
                .filter(observationFocus -> SERIES_DISPLAY_OPTIONS_FOCUS_CODE.equals(observationFocus.getCode())
                        && SERIES_DISPLAY_OPTIONS_FOCUS_CODE_SYSTEM.equals(observationFocus.getCodeSystem()))
                .isPresent();
    }

    private boolean isSeasonFocusObservation(final ObservationResult observationResult)
    {
        return Optional.ofNullable(observationResult)
                .map(ObservationResult::getObservationFocus)
                .filter(observationFocus -> SEASON_OBSERVATION_FOCUS_CODE.equals(observationFocus.getCode())
                        && SEASON_OBSERVATION_FOCUS_CODE_SYSTEM.equals(observationFocus.getCodeSystem()))
                .isPresent();
    }

    private Optional<SeriesSelectionInfo> toSeriesSelectionInfo(final String kmId, final ObservationResult observationResult)
    {
        final CD selectionType = Optional.ofNullable(observationResult).map(ObservationResult::getObservationFocus).orElse(null);
        final CD selectedSeries = Optional.ofNullable(observationResult)
                .map(ObservationResult::getObservationValue)
                .map(ObservationResult.ObservationValue::getConcept)
                .orElse(null);

        if (selectionType == null || !hasText(selectionType.getCode()) || selectedSeries == null || !hasText(
                selectedSeries.getCode()))
            return Optional.empty();

        final String seriesCode = selectedSeries.getCode();
        final String seriesDisplay = Optional.ofNullable(supportingDataService.getSupportedSeriesDisplayName(kmId, seriesCode))
                .orElse(Optional.ofNullable(selectedSeries.getDisplayName()).orElse(seriesCode));
        return Optional.of(new SeriesSelectionInfo(seriesCode, seriesDisplay, selectionType.getCode()));
    }

    private Optional<SeasonInfo> toSeasonInfo(final ObservationResult observationResult)
    {
        final CD season = Optional.ofNullable(observationResult)
                .map(ObservationResult::getObservationValue)
                .map(ObservationResult.ObservationValue::getConcept)
                .orElse(null);
        if (season == null || !hasText(season.getCode()))
            return Optional.empty();

        final String seasonCode = season.getCode();
        final String seasonDisplay = Optional.ofNullable(season.getDisplayName()).orElse(seasonCode);
        final String seasonCodeSystem = season.getCodeSystem();
        return Optional.of(new SeasonInfo(seasonCode, seasonDisplay, seasonCodeSystem));
    }

    private String buildProtocolDetailsDescription(final SeriesSelectionInfo seriesSelectionInfo, final SeasonInfo seasonInfo,
            final String series, final CodeableConcept seriesDoses)
    {
        final List<String> segments = new ArrayList<>();

        final String seriesDisplay =
                Optional.ofNullable(seriesSelectionInfo).map(SeriesSelectionInfo::seriesDisplay).orElse(series);
        if (hasText(seriesDisplay))
            segments.add("Series: %s".formatted(seriesDisplay));

        final String seasonDisplay = Optional.ofNullable(seasonInfo).map(SeasonInfo::seasonDisplay).orElse(null);
        if (hasText(seasonDisplay))
            segments.add("Season: %s".formatted(seasonDisplay));

        final String recommendedDoses = extractCodeableConceptValueText(seriesDoses).orElse(null);
        if (hasText(recommendedDoses))
            segments.add("Recommended number of doses for immunity: %s".formatted(recommendedDoses));

        if (segments.isEmpty())
            return null;

        return String.join(". ", segments) + ".";
    }

    private boolean shouldOutputSeriesContext(final String kmId)
    {
        return Boolean.TRUE.equals(outputSeriesContextByKm.get(kmId));
    }

    private String buildRecommendationDescriptionFallback(final CodeableConcept forecastStatus,
            final List<CodeableConcept> forecastReasons)
    {
        final String status = extractCodeableConceptValueText(forecastStatus).orElse("unknown");
        final String reason = Optional.ofNullable(forecastReasons)
                .stream()
                .flatMap(Collection::stream)
                .map(this::extractCodeableConceptValueText)
                .flatMap(Optional::stream)
                .findFirst()
                .orElse(null);
        if (!hasText(reason))
            return "Recommendation status: %s.".formatted(status);
        return "Recommendation status: %s. Primary reason: %s.".formatted(status, reason.replaceAll("\\.+$", ""));
    }

    private String buildEvaluationDescriptionFallback(final CodeableConcept doseStatus,
            final List<CodeableConcept> doseStatusReasons)
    {
        final String status = extractCodeableConceptValueText(doseStatus).orElse("unknown");
        final String reason = Optional.ofNullable(doseStatusReasons)
                .stream()
                .flatMap(Collection::stream)
                .map(this::extractCodeableConceptValueText)
                .flatMap(Optional::stream)
                .findFirst()
                .orElse(null);
        if (!hasText(reason))
            return "Evaluation status: %s.".formatted(status);
        return "Evaluation status: %s. Primary reason: %s.".formatted(status, reason.replaceAll("\\.+$", ""));
    }

    private Narrative createNarrative(final String text)
    {
        if (!hasText(text))
            return null;
        return Narrative.builder()
                .status(STATUS_GENERATED)
                .div("<div xmlns=\"http://www.w3.org/1999/xhtml\">%s</div>".formatted(HtmlUtils.htmlEscape(text)))
                .build();
    }

    private String buildEvaluationNarrative(final ImmunizationEvaluation evaluation)
    {
        if (evaluation == null)
            return null;
        final String targetDisease =
                Optional.ofNullable(evaluation.targetDisease()).map(CodeableConcept::text).orElse("unknown disease");
        final String status = Optional.ofNullable(evaluation.doseStatus()).map(CodeableConcept::text).orElse("unknown");
        return "Evaluation for %s with dose status %s.".formatted(targetDisease, status);
    }

    private String buildRecommendationNarrative(final ImmunizationRecommendationRecommendation recommendation)
    {
        if (recommendation == null)
            return null;
        final String targetDisease =
                recommendation.targetDisease().stream().findFirst().map(CodeableConcept::text).orElse("unknown disease");
        final String status = Optional.ofNullable(recommendation.forecastStatus()).map(CodeableConcept::text).orElse("unknown");
        return "Recommendation for %s with forecast status %s.".formatted(targetDisease, status);
    }

    private String toFhirDate(final LocalDate localDate)
    {
        if (localDate == null)
            return null;
        return localDate.toString();
    }

    public OperationOutcome.Issue createOperationOutcomeIssue(final String code, final String detailText, final String severity)
    {
        return OperationOutcome.Issue.builder()
                .severity(severity)
                .code(code)
                .details(CodeableConcept.builder().text(detailText).build())
                .build();
    }

    public Parameters createErrorParametersResponse(final String outcomeCode, final String detailText,
            final LocalDateTime requestDateTime)
    {
        final int durationMs = (int) Math.max(0, ChronoUnit.MILLIS.between(requestDateTime, LocalDateTime.now()));
        final OperationOutcome operationOutcome =
                createOperationOutcome(List.of(createOperationOutcomeIssue(outcomeCode, detailText, OUTCOME_SEVERITY_ERROR)));
        return Parameters.builder()
                .resourceType(RESOURCE_TYPE_PARAMETERS)
                .parameter(ParametersParameter.builder().name(DURATION_MS_PARAM).valueInteger(durationMs).build())
                .parameter(ParametersParameter.builder().name(ENGINE_VERSION_PARAM).valueString(getEngineVersion()).build())
                .parameter(ParametersParameter.builder().name(OPERATION_OUTCOME_PARAM).resource(operationOutcome).build())
                .build();
    }

    private OperationOutcome createOperationOutcome(final List<OperationOutcome.Issue> issues)
    {
        return OperationOutcome.builder()
                .resourceType(RESOURCE_TYPE_OPERATION_OUTCOME)
                .text(createNarrative("Operational messages for the immunization forecast request."))
                .issue(issues)
                .build();
    }

    private String getEngineVersion()
    {
        return Optional.ofNullable(iceProperties).map(IceProperties::getIceVersion).orElse(null);
    }

    private LocalDate parseIsoLocalDate(final String value)
    {
        if (value == null || value.isBlank())
            return null;
        try
        {
            return LocalDate.parse(value);
        }
        catch (final DateTimeParseException e)
        {
            try
            {
                return OffsetDateTime.parse(value).toLocalDate();
            }
            catch (final DateTimeParseException ignored)
            {
                try
                {
                    return LocalDateTime.parse(value).toLocalDate();
                }
                catch (final DateTimeParseException nested)
                {
                    log.error("Failed to parse ISO local date/dateTime: {}", value, nested);
                    return null;
                }
            }
        }
    }
}
