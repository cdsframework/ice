package org.cdsframework.ice.service.conversion;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.cdsframework.fhir.Bundle;
import org.cdsframework.fhir.BundleEntry;
import org.cdsframework.fhir.CodeableConcept;
import org.cdsframework.fhir.Coding;
import org.cdsframework.fhir.Extension;
import org.cdsframework.fhir.FhirResource;
import org.cdsframework.fhir.Identifier;
import org.cdsframework.fhir.Immunization;
import org.cdsframework.fhir.ImmunizationEvaluation;
import org.cdsframework.fhir.ImmunizationRecommendation;
import org.cdsframework.fhir.ImmunizationRecommendationDateCriterion;
import org.cdsframework.fhir.ImmunizationRecommendationRecommendation;
import org.cdsframework.fhir.Narrative;
import org.cdsframework.fhir.Observation;
import org.cdsframework.fhir.OperationOutcome;
import org.cdsframework.fhir.Parameters;
import org.cdsframework.fhir.ParametersParameter;
import org.cdsframework.fhir.Patient;
import org.cdsframework.fhir.Reference;
import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.service.SupportingDataService;
import org.cdsframework.ice.supportingdata.ICEConceptType;
import org.omg.dss.EvaluateAtSpecifiedTime;
import org.omg.dss.EvaluationResponse;
import org.opencds.vmr.v1_0.schema.AdministrableSubstance;
import org.opencds.vmr.v1_0.schema.CD;
import org.opencds.vmr.v1_0.schema.CDSInput;
import org.opencds.vmr.v1_0.schema.CDSOutput;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson;
import org.opencds.vmr.v1_0.schema.II;
import org.opencds.vmr.v1_0.schema.INT;
import org.opencds.vmr.v1_0.schema.ObservationResult;
import org.opencds.vmr.v1_0.schema.RelatedClinicalStatement;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal;
import org.opencds.vmr.v1_0.schema.VMR;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.dataformat.xml.XmlMapper;

@Slf4j
@Component
public class VmrConversionComponent
{
    private static final DateTimeFormatter tzFormat = DateTimeFormatter.ofPattern("Z");
    private static final String OID_URN_PREFIX = "urn:oid:";
    private static final String ASSESSMENT_DATE_PARAM = "assessmentDate";
    private static final String KNOWLEDGE_BASE_PARAM = "knowledgeBase";
    private static final String PATIENT_PARAM = "patient";
    private static final String IMMUNIZATION_PARAM = "immunization";
    private static final String OBSERVATION_PARAM = "observation";
    private static final String SCHEDULE_FLAG_PARAM = "scheduleFlag";
    private static final String DATA_PARAM = "data";
    private static final String EVALUATION_PARAM = "evaluation";
    private static final String RECOMMENDATION_PARAM = "recommendation";
    private static final String STATUS_COMPLETED = "completed";
    private static final String STATUS_GENERATED = "generated";
    private static final String OUTCOME_CODE_PROCESSING = "processing";
    private static final String OUTCOME_SEVERITY_WARNING = "warning";
    private static final String OUTCOME_SEVERITY_ERROR = "error";
    private static final String REFERENCE_PREFIX_PATIENT = "Patient/";
    private static final String REFERENCE_PREFIX_IMMUNIZATION = "Immunization/";
    private static final String XML_ROOT_CDS_INPUT = "cdsInput";
    private static final String CVX_OID = "2.16.840.1.113883.12.292";
    private static final String FHIR_CVX_SYSTEM = "http://hl7.org/fhir/sid/cvx";
    private static final String CONTEXTUAL_CONDITION_OBSERVATION_FOCUS_PROPERTY = "observationFocus";
    private static final String CONTEXTUAL_CONDITION_OBSERVATION_VALUE_PROPERTY = "observationValue";
    private static final String SERIES_SELECTION_TYPE_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.501";
    private static final String SERIES_DISPLAY_OPTIONS_FOCUS_CODE = "SERIES_DISPLAY_OPTIONS";
    private static final String SERIES_DISPLAY_OPTIONS_FOCUS_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.500";
    private static final String VACCINE_GROUP_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.1";
    private static final String NUMBER_OF_DOSES_REMAINING_FOCUS_CODE = "NUMBER_OF_DOSES_REMAINING";
    private static final String SEASON_OBSERVATION_FOCUS_CODE = "SEASON";
    private static final String SEASON_OBSERVATION_FOCUS_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.11";
    private static final Coding DATE_CRITERION_DUE =
            Coding.builder().system("http://loinc.org").code("30980-7").display("Date vaccine due").build();
    private static final Coding DATE_CRITERION_OVERDUE =
            Coding.builder().system("http://loinc.org").code("59778-1").display("Date when overdue for immunization").build();
    private static final Coding DATE_CRITERION_EARLIEST =
            Coding.builder().system("http://loinc.org").code("30981-5").display("Earliest date to give").build();
    private static final Coding DATE_CRITERION_LATEST =
            Coding.builder().system("http://loinc.org").code("59777-3").display("Latest date to give immunization").build();
    private static final XmlMapper xmlMapper = XmlMapper.xmlBuilder()
            .defaultUseWrapper(false)
            .findAndAddModules()
            .changeDefaultPropertyInclusion(i -> i.withValueInclusion(JsonInclude.Include.NON_NULL))
            .build();

    private final SupportingDataService supportingDataService;
    private final IceProperties iceProperties;
    private final FhirToVmrInputAdapter fhirToVmrInputAdapter;
    private final SelectionContextExtensionBuilder selectionContextExtensionBuilder;
    private final VaccineGroupRulesArtifactExtensionBuilder vaccineGroupRulesArtifactExtensionBuilder;
    private final ScheduleAuthorityExtensionBuilder scheduleAuthorityExtensionBuilder;
    private final Map<String, Boolean> outputEarliestAndOverdueDatesByKm;
    private final Map<String, Boolean> outputEvaluationSeriesContextByKm;
    private final Map<String, Boolean> outputRecommendationSeriesContextByKm;
    private final Map<String, Boolean> outputNumberOfDosesRemainingByKm;
    private final Map<String, Boolean> outputVaccineGroupRulesArtifactByKm;
    private final Map<String, Boolean> outputScheduleAuthoritiesByKm;

    public VmrConversionComponent(final SupportingDataService supportingDataService, final IceProperties iceProperties,
            final FhirToVmrInputAdapter fhirToVmrInputAdapter,
            final SelectionContextExtensionBuilder selectionContextExtensionBuilder,
            final VaccineGroupRulesArtifactExtensionBuilder vaccineGroupRulesArtifactExtensionBuilder,
            final ScheduleAuthorityExtensionBuilder scheduleAuthorityExtensionBuilder)
    {
        this.supportingDataService = supportingDataService;
        this.iceProperties = iceProperties;
        this.fhirToVmrInputAdapter = fhirToVmrInputAdapter;
        this.selectionContextExtensionBuilder = selectionContextExtensionBuilder;
        this.vaccineGroupRulesArtifactExtensionBuilder = vaccineGroupRulesArtifactExtensionBuilder;
        this.scheduleAuthorityExtensionBuilder = scheduleAuthorityExtensionBuilder;
        outputEarliestAndOverdueDatesByKm = supportingDataService.getKnowledgeBasePropertiesByKmId()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue() != null && iceProperties.resolveOutputEarliestAndOverdueDates(entry.getValue())));
        outputEvaluationSeriesContextByKm = supportingDataService.getKnowledgeBasePropertiesByKmId()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue() != null && iceProperties.resolveOutputSeriesInformationForEvaluations(
                                entry.getValue())));
        outputRecommendationSeriesContextByKm = supportingDataService.getKnowledgeBasePropertiesByKmId()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue() != null && iceProperties.resolveOutputSeriesInformationForRecommendations(
                                entry.getValue())));
        outputNumberOfDosesRemainingByKm = supportingDataService.getKnowledgeBasePropertiesByKmId()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue() != null && iceProperties.resolveOutputNumberOfDosesRemaining(entry.getValue())));
        outputVaccineGroupRulesArtifactByKm = supportingDataService.getKnowledgeBasePropertiesByKmId()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue() != null && iceProperties.resolveOutputVaccineGroupRulesArtifact(
                                entry.getValue())));
        outputScheduleAuthoritiesByKm = supportingDataService.getKnowledgeBasePropertiesByKmId()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue() != null && iceProperties.resolveOutputScheduleAuthorities(entry.getValue())));
    }

    private CodeableConcept toFhirCodeableConcept(final String kmId, final CD cd)
    {
        return supportingDataService.getCodeableConcept(kmId, cd.getCode(), cd.getDisplayName(), cd.getCodeSystem(),
                cd.getOriginalText());
    }

    public EvaluateAtSpecifiedTime convertToEvaluateAtSpecifiedTime(final Parameters parameters)
    {
        return convertToEvaluateAtSpecifiedTime(parameters, false);
    }

    public EvaluateAtSpecifiedTime convertImmDsToEvaluateAtSpecifiedTime(final Parameters parameters)
    {
        return convertToEvaluateAtSpecifiedTime(parameters, true);
    }

    private EvaluateAtSpecifiedTime convertToEvaluateAtSpecifiedTime(final Parameters parameters,
            final boolean validateContextualConditions)
    {
        final RequestContext parsedRequestContext = parseRequestContext(parameters);
        final KnowledgeBaseContext knowledgeBaseContext = resolveKnowledgeBaseContext(parsedRequestContext);
        final RequestContext requestContext =
                applyRequestValidation(knowledgeBaseContext, parsedRequestContext, validateContextualConditions);

        final byte[] payload = createPayload(
                fhirToVmrInputAdapter.createCdsInput(knowledgeBaseContext.kmId(), requestContext.patient(),
                        requestContext.immunizations(), requestContext.observations(), requestContext.scheduleFlags()));
        if (log.isDebugEnabled())
            log.debug("payload: {}", new String(payload, StandardCharsets.UTF_8));

        return OpenCdsTransportAdapter.createEvaluateAtSpecifiedTime(knowledgeBaseContext.kmEntityIdentifier(),
                requestContext.assessmentDate(), ZonedDateTime.now().format(tzFormat), payload);
    }

    public Parameters convertToParametersResponse(final EvaluationResponse evaluateAtSpecifiedTimeResponse,
            final Parameters requestParameters, final LocalDateTime requestDateTime)
    {
        return convertToParametersResponse(evaluateAtSpecifiedTimeResponse, requestParameters, requestDateTime, false);
    }

    public Parameters convertImmDsToParametersResponse(final EvaluationResponse evaluateAtSpecifiedTimeResponse,
            final Parameters requestParameters, final LocalDateTime requestDateTime)
    {
        return convertToParametersResponse(evaluateAtSpecifiedTimeResponse, requestParameters, requestDateTime, true);
    }

    private Parameters convertToParametersResponse(final EvaluationResponse evaluateAtSpecifiedTimeResponse,
            final Parameters requestParameters, final LocalDateTime requestDateTime, final boolean validateContextualConditions)
    {
        final RequestContext parsedRequestContext = parseRequestContext(requestParameters);
        final KnowledgeBaseContext knowledgeBaseContext = resolveKnowledgeBaseContext(parsedRequestContext);
        final RequestContext requestContext =
                applyRequestValidation(knowledgeBaseContext, parsedRequestContext, validateContextualConditions);
        return OpenCdsResponseAdapter.extractCdsOutputs(evaluateAtSpecifiedTimeResponse)
                .stream()
                .map(cdsOutput -> createParametersResponse(knowledgeBaseContext, cdsOutput, requestContext, requestDateTime))
                .max(Comparator.comparingInt(this::forecastEntryCount))
                .orElseGet(() -> FhirParametersResponseAdapter.createErrorParametersResponse(OUTCOME_CODE_PROCESSING,
                        "No forecast payload was returned by the evaluation engine.", requestDateTime, getEngineVersion()));
    }

    private int forecastEntryCount(final Parameters parameters)
    {
        return Math.toIntExact(streamParameters(parameters).map(ParametersParameter::name)
                .filter(name -> EVALUATION_PARAM.equals(name) || RECOMMENDATION_PARAM.equals(name))
                .count());
    }

    private Parameters createParametersResponse(final KnowledgeBaseContext knowledgeBaseContext, final CDSOutput cdsOutput,
            final RequestContext requestContext, final LocalDateTime requestDateTime)
    {
        final String kmId = knowledgeBaseContext.kmId();
        final Reference patientReference = Optional.ofNullable(requestContext.patient())
                .map(Patient::identifier)
                .map(this::extractPrimaryIdentifierValue)
                .map(patientId -> Reference.builder().reference("%s%s".formatted(REFERENCE_PREFIX_PATIENT, patientId)).build())
                .orElse(null);

        final Optional<EvaluatedPerson> outputPatient = Optional.ofNullable(cdsOutput.getVmrOutput()).map(VMR::getPatient);

        final List<ImmunizationEvaluation> immunizationEvaluations =
                buildImmunizationEvaluations(kmId, outputPatient, patientReference);
        final List<ImmunizationRecommendation> immunizationRecommendations =
                buildImmunizationRecommendations(kmId, outputPatient, patientReference, requestContext.assessmentDate());
        return FhirParametersResponseAdapter.createForecastParametersResponse(knowledgeBaseContext.knowledgeBase(),
                patientReference, requestDateTime, getEngineVersion(), requestContext.validationIssues(), immunizationEvaluations,
                immunizationRecommendations);
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
                        .season(eval.season())
                        .doseNumber(eval.doseNumber())
                        .seriesDoses(eval.seriesDoses())
                        .build())
                .sorted(Comparator.comparing(eval -> targetDiseaseText(eval.targetDisease()),
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
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse(null);
        final String narrative = nestedRecommendations.isEmpty()
                                 ? null
                                 : nestedRecommendations.size() == 1
                                   ? buildRecommendationNarrative(nestedRecommendations.getFirst())
                                   : "Recommendations generated: %d entries.".formatted(nestedRecommendations.size());
        return List.of(ImmunizationRecommendation.builder()
                .id(recommendationId)
                .text(createNarrative(narrative))
                .patient(patientReference)
                .date(assessmentDate)
                .recommendation(nestedRecommendations)
                .build());
    }

    private KnowledgeBaseContext resolveKnowledgeBaseContext(final RequestContext requestContext)
    {
        final String knowledgeBaseCanonical = requestContext.knowledgeBaseCanonical();
        final String kmId = supportingDataService.getKmIdFromKnowledgeBaseUrl(knowledgeBaseCanonical);
        return new KnowledgeBaseContext(knowledgeBaseCanonical, kmId, supportingDataService.parseKmEntityIdentifier(kmId));
    }

    private RequestContext parseRequestContext(final Parameters parameters)
    {
        final List<ParametersParameter> params = streamParameters(parameters).toList();

        final LocalDate assessmentDate = params.stream()
                .filter(parameter -> ASSESSMENT_DATE_PARAM.equals(parameter.name()))
                .map(parameter -> StringUtils.hasText(parameter.valueDate())
                                  ? parseIsoLocalDate(parameter.valueDate())
                                  : StringUtils.hasText(parameter.valueDateTime())
                                    ? parseIsoLocalDate(parameter.valueDateTime())
                                    : null)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Parameters must contain assessmentDate"));

        final String knowledgeBaseCanonical = params.stream()
                .filter(parameter -> KNOWLEDGE_BASE_PARAM.equals(parameter.name()))
                .map(ParametersParameter::valueCanonical)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Parameters must contain knowledgeBase valueCanonical"));

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

        final List<String> scheduleFlags =
                params.stream().filter(parameter -> SCHEDULE_FLAG_PARAM.equals(parameter.name())).peek(parameter ->
                {
                    if (!StringUtils.hasText(parameter.valueCode()))
                        throw new IllegalArgumentException("scheduleFlag parameters must use valueCode");
                }).map(ParametersParameter::valueCode).map(String::trim).distinct().toList();

        return new RequestContext(assessmentDate, knowledgeBaseCanonical, patient, immunizations, observations, scheduleFlags,
                List.of());
    }

    private Stream<ParametersParameter> streamParameters(final Parameters parameters)
    {
        return Optional.ofNullable(parameters)
                .map(Parameters::parameter)
                .stream()
                .flatMap(Collection::stream)
                .flatMap(this::flattenParameterTree);
    }

    private Stream<ParametersParameter> flattenParameterTree(final ParametersParameter parameter)
    {
        if (parameter == null)
            return Stream.empty();

        return Stream.concat(Stream.of(parameter),
                Optional.ofNullable(parameter.part()).stream().flatMap(Collection::stream).flatMap(this::flattenParameterTree));
    }

    private RequestContext applyRequestValidation(final KnowledgeBaseContext knowledgeBaseContext,
            final RequestContext requestContext, final boolean validateContextualConditions)
    {
        final List<OperationOutcome.Issue> validationIssues = new ArrayList<>();
        final List<Immunization> validatedImmunizations = Optional.ofNullable(requestContext.immunizations())
                .stream()
                .flatMap(Collection::stream)
                .filter(immunization -> isSupportedImmunization(knowledgeBaseContext, immunization, validationIssues))
                .toList();
        final List<Observation> validatedObservations =
                validateContextualConditions ? Optional.ofNullable(requestContext.observations())
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(observation -> isSupportedObservation(knowledgeBaseContext, observation, validationIssues))
                        .toList() : requestContext.observations();
        final List<String> resolvedScheduleFlags = supportingDataService.validateScheduleFlagsForKmId(knowledgeBaseContext.kmId(),
                Stream.concat(supportingDataService.getConfiguredScheduleFlags().stream(),
                        Optional.ofNullable(requestContext.scheduleFlags()).stream().flatMap(Collection::stream)).toList());

        return new RequestContext(requestContext.assessmentDate(), requestContext.knowledgeBaseCanonical(),
                requestContext.patient(), validatedImmunizations, validatedObservations, resolvedScheduleFlags,
                List.copyOf(validationIssues));
    }

    private boolean isSupportedObservation(final KnowledgeBaseContext knowledgeBaseContext, final Observation observation,
            final List<OperationOutcome.Issue> validationIssues)
    {
        final Coding coding = Optional.ofNullable(observation)
                .map(Observation::code)
                .map(CodeableConcept::coding)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .orElse(null);
        if (coding != null)
        {
            final boolean hasObservationFocus = supportingDataService.getConceptPropertyCoding(knowledgeBaseContext.kmId(),
                    ICEConceptType.CONTEXTUAL_CONDITION.getIceConceptTypeValue(), coding.system(), coding.code(),
                    CONTEXTUAL_CONDITION_OBSERVATION_FOCUS_PROPERTY).isPresent();
            final boolean hasObservationValue = supportingDataService.getConceptPropertyCoding(knowledgeBaseContext.kmId(),
                    ICEConceptType.CONTEXTUAL_CONDITION.getIceConceptTypeValue(), coding.system(), coding.code(),
                    CONTEXTUAL_CONDITION_OBSERVATION_VALUE_PROPERTY).isPresent();
            if (hasObservationFocus && hasObservationValue)
                return true;
        }

        final String contextualConditionCode = coding != null && StringUtils.hasText(coding.code()) ? coding.code() : "<missing>";
        log.warn("Unsupported contextual-condition code '{}' was provided; observation was ignored.", contextualConditionCode);
        validationIssues.add(createOperationOutcomeIssue(buildUnsupportedContextualConditionWarning(contextualConditionCode),
                OUTCOME_SEVERITY_WARNING));
        return false;
    }

    private boolean isSupportedImmunization(final KnowledgeBaseContext knowledgeBaseContext, final Immunization immunization,
            final List<OperationOutcome.Issue> validationIssues)
    {
        final String kmId = knowledgeBaseContext.kmId();
        final Coding coding = Optional.ofNullable(immunization)
                .map(Immunization::vaccineCode)
                .map(CodeableConcept::coding)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .orElse(null);
        if (coding == null)
            return true;

        final String codeSystem = Optional.ofNullable(coding.system()).map(String::trim).orElse(null);
        if (!StringUtils.hasText(codeSystem))
        {
            validationIssues.add(createOperationOutcomeIssue(buildMissingCodeSystemMessage(immunization), OUTCOME_SEVERITY_ERROR));
            return false;
        }

        final String internalCodeSystemOid;
        try
        {
            internalCodeSystemOid = supportingDataService.toRequiredInternalCodeSystemOid(kmId, codeSystem);
        }
        catch (final IllegalArgumentException e)
        {
            validationIssues.add(createOperationOutcomeIssue(
                    buildUnsupportedCodeSystemMessage(knowledgeBaseContext.knowledgeBase(), immunization, codeSystem),
                    OUTCOME_SEVERITY_ERROR));
            return false;
        }

        final String cvxCode = Optional.ofNullable(coding.code()).map(String::trim).orElse(null);
        if (!CVX_OID.equals(internalCodeSystemOid))
            return true;

        final boolean isKnownCvxCode = StringUtils.hasText(cvxCode) && supportingDataService.isCodeInCodeSystem(kmId,
                ICEConceptType.VACCINE.getIceConceptTypeValue(), cvxCode);
        if (!isKnownCvxCode)
        {
            validationIssues.add(
                    createOperationOutcomeIssue(buildUnsupportedCvxWarning(immunization, cvxCode), OUTCOME_SEVERITY_WARNING));
            return false;
        }

        final boolean isSupportedCvxCode =
                supportingDataService.isCodeSupportedInCodeSystem(kmId, ICEConceptType.VACCINE.getIceConceptTypeValue(), cvxCode);
        if (isSupportedCvxCode)
            return true;

        validationIssues.add(
                createOperationOutcomeIssue(buildConfiguredUnsupportedCvxWarning(immunization, cvxCode), OUTCOME_SEVERITY_WARNING));
        return false;
    }

    private String buildMissingCodeSystemMessage(final Immunization immunization)
    {
        return "Missing vaccine code system for immunization '%s'; this immunization was not evaluated.".formatted(
                buildImmunizationIdentifier(immunization));
    }

    private String buildUnsupportedCodeSystemMessage(final String knowledgeBaseCanonical, final Immunization immunization,
            final String codeSystem)
    {
        final String canonicalRef =
                StringUtils.hasText(knowledgeBaseCanonical) ? knowledgeBaseCanonical : "<unknown-knowledge-base-canonical>";
        final String codeSystemRef = StringUtils.hasText(codeSystem) ? codeSystem : "<missing>";
        return "Unsupported code system '%s' for knowledgeBase '%s' on immunization '%s'; this immunization was not evaluated.".formatted(
                codeSystemRef, canonicalRef, buildImmunizationIdentifier(immunization));
    }

    private String buildUnsupportedCvxWarning(final Immunization immunization, final String cvxCode)
    {
        return "Unsupported CVX code '%s' was provided for immunization '%s'; this immunization was not evaluated.".formatted(
                StringUtils.hasText(cvxCode) ? cvxCode : "<missing>", buildImmunizationIdentifier(immunization));
    }

    private String buildConfiguredUnsupportedCvxWarning(final Immunization immunization, final String cvxCode)
    {
        return "CVX code '%s' is configured as not supported for immunization '%s'; this immunization was not evaluated.".formatted(
                StringUtils.hasText(cvxCode) ? cvxCode : "<missing>", buildImmunizationIdentifier(immunization));
    }

    private String buildUnsupportedContextualConditionWarning(final String contextualConditionCode)
    {
        return "Unsupported contextual-condition code '%s' was provided; observation was ignored.".formatted(
                contextualConditionCode);
    }

    private String buildImmunizationIdentifier(final Immunization immunization)
    {
        final String immunizationId = Optional.ofNullable(immunization)
                .map(Immunization::identifier)
                .map(this::extractPrimaryIdentifierValue)
                .orElse(null);
        return StringUtils.hasText(immunizationId) ? immunizationId : "<unknown-id>";
    }

    private ImmunizationRecommendationRecommendation toRecommendation(final String kmId,
            final SubstanceAdministrationProposal substanceAdministrationProposal)
    {
        final ImmunizationRecommendationRecommendation.ImmunizationRecommendationRecommendationBuilder recommendationBuilder =
                ImmunizationRecommendationRecommendation.builder();
        final List<RelatedClinicalStatement> relatedClinicalStatements =
                getRelatedClinicalStatements(substanceAdministrationProposal);

        recommendationBuilder.authority(shouldOutputScheduleAuthorities(kmId)
                                        ? scheduleAuthorityExtensionBuilder.build(relatedClinicalStatements)
                                        : List.of());

        recommendationBuilder.vaccineCode(Optional.ofNullable(substanceAdministrationProposal.getSubstance())
                .map(AdministrableSubstance::getSubstanceCode)
                .map(cd -> toFhirCodeableConcept(kmId, cd))
                .map(this::withTextFromCodingDisplay)
                .filter(this::isCvxCodeableConcept)
                .stream()
                .toList());

        final Optional<ObservationResult> observationResult = findPrimaryObservationResultFromStatements(relatedClinicalStatements);

        final List<CodeableConcept> targetDiseases = observationResult.map(ObservationResult::getObservationFocus)
                .map(focus -> buildTargetDiseaseCodeableConcepts(kmId, focus))
                .orElse(List.of());
        recommendationBuilder.targetDisease(targetDiseases);

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

        final ProtocolContext protocolContext = extractProtocolContext(kmId, relatedClinicalStatements,
                Boolean.TRUE.equals(outputRecommendationSeriesContextByKm.get(kmId)),
                Boolean.TRUE.equals(outputNumberOfDosesRemainingByKm.get(kmId)), true);
        recommendationBuilder.series(protocolContext.series());
        recommendationBuilder.seriesDoses(protocolContext.seriesDoses());
        recommendationBuilder.description(Optional.ofNullable(
                        buildProtocolDetailsDescription(protocolContext.seriesSelectionInfo(), protocolContext.seasonInfo(),
                                protocolContext.seriesDisplay(), protocolContext.seriesDosesRemaining()))
                .orElseGet(() -> buildRecommendationDescriptionFallback(forecastStatus, forecastReasons)));
        recommendationBuilder.extension(protocolContext.extensions());
        recommendationBuilder.season(protocolContext.season());
        recommendationBuilder.seriesDosesRemaining(protocolContext.seriesDosesRemaining());

        final List<ImmunizationRecommendationDateCriterion> dateCriteria = new ArrayList<>();
        Optional.ofNullable(substanceAdministrationProposal.getProposedAdministrationTimeInterval())
                .map(ConversionDateSupport::toTimeInterval)
                .ifPresent(proposedTimeInterval ->
                {
                    if (proposedTimeInterval.low() != null)
                        dateCriteria.add(createDateCriterion(DATE_CRITERION_DUE, proposedTimeInterval.low()));
                    if (Boolean.TRUE.equals(outputEarliestAndOverdueDatesByKm.get(kmId)) && proposedTimeInterval.high() != null)
                        dateCriteria.add(createDateCriterion(DATE_CRITERION_OVERDUE, proposedTimeInterval.high()));
                });

        Optional.ofNullable(substanceAdministrationProposal.getValidAdministrationTimeInterval())
                .map(ConversionDateSupport::toTimeInterval)
                .ifPresent(validTimeInterval ->
                {
                    if (Boolean.TRUE.equals(outputEarliestAndOverdueDatesByKm.get(kmId)) && validTimeInterval.low() != null)
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
                .filter(StringUtils::hasText)
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
                .filter(StringUtils::hasText)
                .map(String::trim)
                .filter(code -> !code.isEmpty())
                .findFirst();
    }

    private ImmunizationRecommendationDateCriterion createDateCriterion(final Coding criterionCoding, final LocalDate date)
    {
        if (criterionCoding == null || !StringUtils.hasText(criterionCoding.system()) || !StringUtils.hasText(
                criterionCoding.code()))
            throw new IllegalArgumentException("Date criterion coding is not configured");
        final String display = StringUtils.hasText(criterionCoding.display()) ? criterionCoding.display() : criterionCoding.code();
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

        final CodeableConcept evaluatedVaccine = Optional.ofNullable(substanceAdministrationEvent.getSubstance())
                .map(AdministrableSubstance::getSubstanceCode)
                .map(cd -> toFhirCodeableConcept(kmId, cd))
                .orElse(null);
        final List<CodeableConcept> componentDiseases = Optional.ofNullable(evaluatedVaccine)
                .filter(this::isCvxCodeableConcept)
                .stream()
                .flatMap(concept -> concept.coding().stream())
                .filter(coding -> "http://hl7.org/fhir/sid/cvx".equals(coding.system()))
                .flatMap(coding -> supportingDataService.getDiseasesForVaccine(kmId, coding.code()).stream())
                .distinct()
                .toList();
        // A component without a disease mapping is a supporting-data gap; the vaccine group's diseases keep the rest of the
        // forecast instead of failing the whole response.
        final List<CodeableConcept> targetDiseases = !componentDiseases.isEmpty()
                                                     ? componentDiseases
                                                     : observationResult.map(ObservationResult::getObservationFocus)
                                                             .map(focus -> buildTargetDiseaseCodeableConcepts(kmId, focus))
                                                             .orElse(List.of());
        if (componentDiseases.isEmpty())
            log.warn("No diseases mapped for evaluated vaccine {} in {}; using its vaccine group's diseases.",
                    Optional.ofNullable(evaluatedVaccine).map(CodeableConcept::coding).orElse(List.of()), kmId);
        if (targetDiseases.isEmpty())
            throw new IllegalArgumentException("No target diseases mapped for evaluated vaccine or its vaccine group");

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

        final ProtocolContext protocolContext = extractProtocolContext(kmId, relatedClinicalStatements,
                Boolean.TRUE.equals(outputEvaluationSeriesContextByKm.get(kmId)), false, false);

        return Stream.of(ImmunizationEvaluation.builder()
                .id(extension)
                .authority(shouldOutputScheduleAuthorities(kmId)
                           ? scheduleAuthorityExtensionBuilder.build(relatedClinicalStatements)
                           : List.of())
                .status(STATUS_COMPLETED)
                .date(Optional.ofNullable(substanceAdministrationEvent)
                        .map(SubstanceAdministrationEvent::getAdministrationTimeInterval)
                        .map(ConversionDateSupport::toTimeInterval)
                        .map(TimeInterval::low)
                        .map(this::toFhirDate)
                        .orElse(null))
                .targetDisease(targetDiseases)
                .immunizationEvent(!StringUtils.hasText(extension)
                                   ? null
                                   : Reference.builder()
                                           .reference("%s%s".formatted(REFERENCE_PREFIX_IMMUNIZATION, extension))
                                           .build())
                .doseStatus(doseStatus)
                .doseStatusReason(doseStatusReasons)
                .extension(protocolContext.extensions())
                .description(Optional.ofNullable(
                                buildProtocolDetailsDescription(protocolContext.seriesSelectionInfo(), protocolContext.seasonInfo(),
                                        protocolContext.seriesDisplay(), null))
                        .orElseGet(() -> buildEvaluationDescriptionFallback(doseStatus, doseStatusReasons)))
                .series(protocolContext.series())
                .season(protocolContext.season())
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

    private String selectIiExtensionOrRoot(final II identifier)
    {
        if (identifier == null)
            return null;

        return StringUtils.hasText(identifier.getExtension()) ? identifier.getExtension() : identifier.getRoot();
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
        return StringUtils.hasText(focusCodeSystem) && focusCodeSystem.equals(SERIES_SELECTION_TYPE_CODE_SYSTEM);
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
                .anyMatch(system -> FHIR_CVX_SYSTEM.equalsIgnoreCase(system) || CVX_OID.equals(system) || "%s%s".formatted(
                        OID_URN_PREFIX, CVX_OID).equalsIgnoreCase(system));
    }

    private String extractPrimaryIdentifierValue(final List<Identifier> identifiers)
    {
        return Optional.ofNullable(identifiers)
                .stream()
                .flatMap(Collection::stream)
                .map(Identifier::value)
                .filter(StringUtils::hasText)
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
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse(null);

        if (!StringUtils.hasText(display))
            return concept;

        if (display.equals(concept.text()))
            return concept;

        return CodeableConcept.builder().coding(concept.coding()).text(display).build();
    }

    /** The number of doses in the selected series. */
    private CodeableConcept seriesDoses(final String kmId, final SeriesSelectionInfo seriesSelectionInfo)
    {
        return Optional.ofNullable(seriesSelectionInfo)
                .map(SeriesSelectionInfo::seriesCode)
                .map(seriesCode -> supportingDataService.getNumberOfDosesInSeries(kmId, seriesCode))
                .map(this::toNumericCodeableConcept)
                .orElse(null);
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
        if (StringUtils.hasText(textValue))
            return Optional.of(CodeableConcept.builder().text(textValue).build());

        final CD conceptValue = Optional.ofNullable(observationResult)
                .map(ObservationResult::getObservationValue)
                .map(ObservationResult.ObservationValue::getConcept)
                .orElse(null);
        if (conceptValue == null || (!StringUtils.hasText(conceptValue.getCode()) && !StringUtils.hasText(
                conceptValue.getDisplayName())))
            return Optional.empty();

        final Optional<Coding> coding =
                (!StringUtils.hasText(conceptValue.getCode()) && !StringUtils.hasText(conceptValue.getCodeSystem())
                        && !StringUtils.hasText(conceptValue.getDisplayName()))
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

    // includeScheduleAuthorities is unused since schedule authorities became the resources' authority references.
    private ProtocolContext extractProtocolContext(final String kmId,
            final List<RelatedClinicalStatement> relatedClinicalStatements, final boolean outputSeriesContext,
            final boolean outputNumberOfDosesRemaining, final boolean includeScheduleAuthorities)
    {
        final List<Extension> extensions = new ArrayList<>();
        if (shouldOutputVaccineGroupRulesArtifact(kmId))
            vaccineGroupRulesArtifactExtensionBuilder.build(relatedClinicalStatements).ifPresent(extensions::add);

        if (!outputSeriesContext)
            return ProtocolContext.withoutSeries(extensions);

        final SeriesSelectionInfo seriesSelectionInfo = extractSeriesSelectionInfo(kmId, relatedClinicalStatements).orElse(null);
        final SeasonInfo seasonInfo = extractSeasonInfo(relatedClinicalStatements).orElse(null);
        final String series = Optional.ofNullable(seriesSelectionInfo)
                .map(SeriesSelectionInfo::seriesDisplay)
                .filter(StringUtils::hasText)
                .orElse(Optional.ofNullable(seriesSelectionInfo).map(SeriesSelectionInfo::seriesCode).orElse(null));
        final CodeableConcept seriesDoses =
                outputNumberOfDosesRemaining ? extractSeriesDosesFromRelatedClinicalStatements(relatedClinicalStatements).orElse(
                        null) : null;
        selectionContextExtensionBuilder.build(kmId, seriesSelectionInfo, seasonInfo).ifPresent(extensions::add);
        return toProtocolContext(kmId, seriesSelectionInfo, seasonInfo, series, seriesDoses, extensions);
    }

    /** The FHIR series values; the engine's remaining count is seriesDosesRemaining, and seriesDoses is the series size. */
    private ProtocolContext toProtocolContext(final String kmId, final SeriesSelectionInfo seriesSelectionInfo,
            final SeasonInfo seasonInfo, final String seriesDisplay, final CodeableConcept seriesDosesRemaining,
            final List<Extension> extensions)
    {
        return new ProtocolContext(seriesSelectionInfo, seasonInfo, seriesDisplay,
                selectionContextExtensionBuilder.buildSelectedSeries(kmId, seriesSelectionInfo),
                selectionContextExtensionBuilder.buildSelectedSeason(kmId, seasonInfo), seriesDoses(kmId, seriesSelectionInfo),
                seriesDosesRemaining, extensions);
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

    private List<CodeableConcept> buildTargetDiseaseCodeableConcepts(final String kmId, final CD observationFocus)
    {
        if (observationFocus == null)
            return List.of();

        final List<CodeableConcept> mappedDiseases = Optional.ofNullable(observationFocus.getCode())
                .map(code -> supportingDataService.getDiseasesForVaccineGroup(kmId, code))
                .orElse(List.of());
        if (!mappedDiseases.isEmpty())
            return mappedDiseases;

        return Optional.ofNullable(withTextFromCodingDisplay(toFhirCodeableConcept(kmId, observationFocus))).stream().toList();
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

        if (selectionType == null || !StringUtils.hasText(selectionType.getCode()) || selectedSeries == null
                || !StringUtils.hasText(selectedSeries.getCode()))
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
        if (season == null || !StringUtils.hasText(season.getCode()))
            return Optional.empty();

        final String seasonCode = season.getCode();
        final String seasonDisplay = Optional.ofNullable(season.getDisplayName()).orElse(seasonCode);
        final String seasonCodeSystem = season.getCodeSystem();
        return Optional.of(new SeasonInfo(seasonCode, seasonDisplay, seasonCodeSystem));
    }

    private String buildProtocolDetailsDescription(final SeriesSelectionInfo seriesSelectionInfo, final SeasonInfo seasonInfo,
            final String series, final CodeableConcept seriesDosesRemaining)
    {
        final List<String> segments = new ArrayList<>();

        final String seriesDisplay =
                Optional.ofNullable(seriesSelectionInfo).map(SeriesSelectionInfo::seriesDisplay).orElse(series);
        if (StringUtils.hasText(seriesDisplay))
            segments.add("Series: %s".formatted(seriesDisplay));

        final String seasonDisplay = Optional.ofNullable(seasonInfo).map(SeasonInfo::seasonDisplay).orElse(null);
        if (StringUtils.hasText(seasonDisplay))
            segments.add("Season: %s".formatted(seasonDisplay));

        final String dosesRemaining = extractCodeableConceptValueText(seriesDosesRemaining).orElse(null);
        // A count or "Recurring" is labeled; the engine's longer explanations already say what they are.
        if (StringUtils.hasText(dosesRemaining))
            segments.add(dosesRemaining.matches("\\d+|Recurring")
                         ? "Doses remaining in series: %s".formatted(dosesRemaining)
                         : dosesRemaining);

        if (segments.isEmpty())
            return null;

        return "%s.".formatted(String.join(". ", segments));
    }

    private boolean shouldOutputVaccineGroupRulesArtifact(final String kmId)
    {
        return Boolean.TRUE.equals(outputVaccineGroupRulesArtifactByKm.get(kmId));
    }

    private boolean shouldOutputScheduleAuthorities(final String kmId)
    {
        return Boolean.TRUE.equals(outputScheduleAuthoritiesByKm.get(kmId));
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
        if (!StringUtils.hasText(reason))
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
        if (!StringUtils.hasText(reason))
            return "Evaluation status: %s.".formatted(status);
        return "Evaluation status: %s. Primary reason: %s.".formatted(status, reason.replaceAll("\\.+$", ""));
    }

    private Narrative createNarrative(final String text)
    {
        if (!StringUtils.hasText(text))
            return null;

        return Narrative.builder()
                .status(STATUS_GENERATED)
                .div("<div xmlns=\"http://www.w3.org/1999/xhtml\">%s</div>".formatted(HtmlUtils.htmlEscape(text)))
                .build();
    }

    private String targetDiseaseText(final List<CodeableConcept> targetDiseases)
    {
        final String text =
                targetDiseases.stream().map(CodeableConcept::text).filter(StringUtils::hasText).collect(Collectors.joining(", "));
        return StringUtils.hasText(text) ? text : "unknown disease";
    }

    private String buildEvaluationNarrative(final ImmunizationEvaluation evaluation)
    {
        if (evaluation == null)
            return null;

        return "Evaluation for %s with dose status %s.".formatted(targetDiseaseText(evaluation.targetDisease()),
                Optional.ofNullable(evaluation.doseStatus()).map(CodeableConcept::text).orElse("unknown"));
    }

    private String buildRecommendationNarrative(final ImmunizationRecommendationRecommendation recommendation)
    {
        if (recommendation == null)
            return null;
        final String targetDisease = targetDiseaseText(recommendation.targetDisease());
        final String status = Optional.ofNullable(recommendation.forecastStatus()).map(CodeableConcept::text).orElse("unknown");
        return "Recommendation for %s with forecast status %s.".formatted(targetDisease, status);
    }

    private String toFhirDate(final LocalDate localDate)
    {
        if (localDate == null)
            return null;
        return localDate.toString();
    }

    private OperationOutcome.Issue createOperationOutcomeIssue(final String detailText, final String severity)
    {
        return OperationOutcome.Issue.builder()
                .severity(severity)
                .code(VmrConversionComponent.OUTCOME_CODE_PROCESSING)
                .details(CodeableConcept.builder().text(detailText).build())
                .build();
    }

    public Parameters createErrorParametersResponse(final String outcomeCode, final String detailText,
            final LocalDateTime requestDateTime)
    {
        return FhirParametersResponseAdapter.createErrorParametersResponse(outcomeCode, detailText, requestDateTime,
                getEngineVersion());
    }

    private String getEngineVersion()
    {
        return Optional.ofNullable(iceProperties).map(IceProperties::getIceVersion).orElse(null);
    }

    private LocalDate parseIsoLocalDate(final String value)
    {
        return ConversionDateSupport.parseIsoLocalDate(value);
    }
}
