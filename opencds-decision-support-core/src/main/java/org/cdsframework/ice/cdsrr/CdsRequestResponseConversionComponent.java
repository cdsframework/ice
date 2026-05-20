package org.cdsframework.ice.cdsrr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.cdsframework.fhir.CodeableConcept;
import org.cdsframework.fhir.Coding;
import org.cdsframework.ice.cdsrr.dto.CdsRequest;
import org.cdsframework.ice.cdsrr.dto.CdsResponse;
import org.cdsframework.ice.cdsrr.dto.Immunization;
import org.cdsframework.ice.cdsrr.dto.ImmunizationEvaluation;
import org.cdsframework.ice.cdsrr.dto.ImmunizationRecommendation;
import org.cdsframework.ice.cdsrr.dto.Observation;
import org.cdsframework.ice.service.SupportingDataService;
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

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.dataformat.xml.XmlMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class CdsRequestResponseConversionComponent
{
    private record TimeInterval(LocalDate low,
                                LocalDate high)
    {
    }

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter tzFormat = DateTimeFormatter.ofPattern("Z");
    private static final String SERIES_SELECTION_TYPE_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.501";
    private static final String VACCINE_GROUP_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.1";

    private static TimeInterval convertIVLTS(final IVLTS ivlts)
    {
        return new TimeInterval(convertDate(ivlts.getLow()), convertDate(ivlts.getHigh()));
    }

    private static String convertDate(final LocalDate date)
    {
        return date.format(dateFormat);
    }

    // TODO: return version of ice that serviced the request
    // TODO: populate the series selections

    private static LocalDate convertDate(final String date)
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

    private CodeableConcept convertCd(final String kmId, final CD cd)
    {
        return supportingDataService.getCodeableConcept(kmId, cd.getCode(), cd.getDisplayName(), cd.getCodeSystem(),
                cd.getOriginalText());
    }

    public EvaluateAtSpecifiedTime convertToEvaluateAtSpecifiedTime(final String kmId, final CdsRequest cdsRequest)
    {
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(Date.from(cdsRequest.assessmentDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        final byte[] payload = createPayload(getCdsInput(cdsRequest));
        if (log.isDebugEnabled())
            log.debug("payload: {}", new String(payload, StandardCharsets.UTF_8));

        return createEvaluateAtSpecifiedTime(kmId, createInteractionIdentifier(UUID.randomUUID().toString(),
                        DatatypeFactory.newDefaultInstance().newXMLGregorianCalendar(new GregorianCalendar())),
                DatatypeFactory.newDefaultInstance().newXMLGregorianCalendar(calendar), createEvaluationRequest(getTimezone(),
                        List.of(createKmEvaluationRequest(createEntityIdentifier("org.nyc.cir", "ICE", "1.0.0"))),
                        List.of(createDataRequirementItemData(
                                createItemIdentifier(createEntityIdentifier("org.nyc.cir", "ICEData", "1.0.0")),
                                createSemanticPayload(createEntityIdentifier("org.opencds.vmr", "VMR", "1.0"),
                                        List.of(payload))))));
    }

    public CdsResponse convertToCdsResponse(final String kmId, final EvaluationResponse evaluateAtSpecifiedTimeResponse,
            final CdsRequest cdsRequest, final LocalDateTime requestDateTime)
    {
        return Optional.ofNullable(evaluateAtSpecifiedTimeResponse)
                .map(EvaluationResponse::getFinalKMEvaluationResponse)
                .map(List::getFirst)
                .map(FinalKMEvaluationResponse::getKmEvaluationResultData)
                .map(List::getFirst)
                .map(KMEvaluationResultData::getData)
                .map(SemanticPayload::getBase64EncodedPayload)
                .map(List::getFirst)
                .map(this::convertPayload)
                .map(cdsOutput -> createCdsResponse(kmId, cdsOutput, cdsRequest, requestDateTime))
                .orElse(null);
    }

    private CDSOutput convertPayload(final byte[] payload)
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

    private CdsResponse createCdsResponse(final String kmId, final CDSOutput cdsOutput, final CdsRequest cdsRequest,
            final LocalDateTime requestDateTime)
    {
        final CdsResponse.CdsResponseBuilder cdsResponseBuilder = CdsResponse.builder()
                .patient(cdsRequest.patient())
                .module(cdsRequest.module())
                .assessmentDate(cdsRequest.assessmentDate());

        final Optional<EvaluatedPerson> patient = Optional.ofNullable(cdsOutput.getVmrOutput()).map(VMR::getPatient);

        cdsResponseBuilder.immunizationEvaluations(patient.map(EvaluatedPerson::getClinicalStatements)
                .map(EvaluatedPerson.ClinicalStatements::getSubstanceAdministrationEvents)
                .map(EvaluatedPerson.ClinicalStatements.SubstanceAdministrationEvents::getSubstanceAdministrationEvent)
                .stream()
                .flatMap(Collection::stream)
                .flatMap(substanceAdministrationEvent -> convertSubstanceAdministrationEvent(kmId, substanceAdministrationEvent))
                .sorted(Comparator.comparing(eval -> Optional.ofNullable(eval.vaccineCode())
                        .flatMap(vc -> vc.coding().stream().findFirst())
                        .map(Coding::code)
                        .orElse(""), Comparator.nullsLast(String::compareTo)))
                .toList());

        cdsResponseBuilder.immunizationRecommendations(patient.map(EvaluatedPerson::getClinicalStatements)
                .map(EvaluatedPerson.ClinicalStatements::getSubstanceAdministrationProposals)
                .map(EvaluatedPerson.ClinicalStatements.SubstanceAdministrationProposals::getSubstanceAdministrationProposal)
                .stream()
                .flatMap(Collection::stream)
                .map(substanceAdministrationProposal -> convertSubstanceAdministrationProposal(kmId,
                        substanceAdministrationProposal))
                .sorted(Comparator.comparing(rec -> rec.targetDiseases().stream().map(CodeableConcept::text).findFirst().orElse(""),
                        Comparator.nullsLast(String::compareTo)))
                .toList());

        final LocalDateTime now = LocalDateTime.now();
        return cdsResponseBuilder.responseDateTime(now).responseDuration(ChronoUnit.MILLIS.between(requestDateTime, now)).build();
    }

    private ImmunizationRecommendation convertSubstanceAdministrationProposal(final String kmId,
            final SubstanceAdministrationProposal substanceAdministrationProposal)
    {
        final ImmunizationRecommendation.ImmunizationRecommendationBuilder immunizationRecommendationBuilder =
                ImmunizationRecommendation.builder();

        immunizationRecommendationBuilder.vaccineCodes(Optional.ofNullable(substanceAdministrationProposal.getSubstance())
                .map(AdministrableSubstance::getSubstanceCode)
                .map(cd -> convertCd(kmId, cd))
                .stream()
                .toList());

        final Optional<ObservationResult> observationResult =
                findPrimaryObservationResultFromStatements(substanceAdministrationProposal.getRelatedClinicalStatement());

        immunizationRecommendationBuilder.targetDiseases(observationResult.map(ObservationResult::getObservationFocus)
                .map(focus -> supportingDataService.getDiseasesForVaccineGroup(kmId, focus.getCode()))
                .stream()
                .flatMap(List::stream)
                .sorted(Comparator.comparing(cc -> Optional.ofNullable(cc.text()).orElse(""),
                        Comparator.nullsLast(String::compareTo)))
                .toList());

        immunizationRecommendationBuilder.forecastStatus(observationResult.map(ObservationResult::getObservationValue)
                .map(ObservationResult.ObservationValue::getConcept)
                .map(cd -> convertCd(kmId, cd))
                .orElse(null));

        immunizationRecommendationBuilder.forecastStatusReasons(observationResult.map(ObservationResult::getInterpretation)
                .stream()
                .flatMap(Collection::stream)
                .map(cd -> convertCd(kmId, cd))
                .toList());

        Optional.ofNullable(substanceAdministrationProposal.getProposedAdministrationTimeInterval())
                .map(CdsRequestResponseConversionComponent::convertIVLTS)
                .ifPresent(proposedTimeInterval -> immunizationRecommendationBuilder.recommendationDate(proposedTimeInterval.low())
                        .overdueDate(proposedTimeInterval.high()));

        Optional.ofNullable(substanceAdministrationProposal.getValidAdministrationTimeInterval())
                .map(CdsRequestResponseConversionComponent::convertIVLTS)
                .ifPresent(validTimeInterval -> immunizationRecommendationBuilder.earliestDate(validTimeInterval.low())
                        .latestDate(validTimeInterval.high()));

        return immunizationRecommendationBuilder.build();
    }

    private ImmunizationEvaluation convertRelatedSubstanceAdministrationEvent(final String kmId, final String extension,
            final SubstanceAdministrationEvent substanceAdministrationEvent)
    {
        final ImmunizationEvaluation.ImmunizationEvaluationBuilder immunizationEvaluationBuilder =
                ImmunizationEvaluation.builder().id(extension);

        immunizationEvaluationBuilder.doseNumber(Optional.ofNullable(substanceAdministrationEvent)
                .map(SubstanceAdministrationEvent::getDoseNumber)
                .map(INT::getValue)
                .orElse(null));

        immunizationEvaluationBuilder.vaccineCode(Optional.ofNullable(substanceAdministrationEvent)
                .map(SubstanceAdministrationEvent::getSubstance)
                .map(AdministrableSubstance::getSubstanceCode)
                .map(cd -> convertCd(kmId, cd))
                .orElse(null));

        final Optional<ObservationResult> observationResult = Optional.ofNullable(substanceAdministrationEvent)
                .map(SubstanceAdministrationEvent::getRelatedClinicalStatement)
                .flatMap(this::findPrimaryObservationResultFromStatements);

        immunizationEvaluationBuilder.targetDiseases(observationResult.map(ObservationResult::getObservationFocus)
                .map(focus -> supportingDataService.getDiseasesForVaccineGroup(kmId, focus.getCode()))
                .stream()
                .flatMap(List::stream)
                .sorted(Comparator.comparing(cc -> Optional.ofNullable(cc.text()).orElse(""),
                        Comparator.nullsLast(String::compareTo)))
                .toList());

        immunizationEvaluationBuilder.doseStatus(observationResult.map(ObservationResult::getObservationValue)
                .map(ObservationResult.ObservationValue::getConcept)
                .map(cd -> convertCd(kmId, cd))
                .orElse(null));

        immunizationEvaluationBuilder.doseStatusReasons(observationResult.map(ObservationResult::getInterpretation)
                .stream()
                .flatMap(Collection::stream)
                .map(cd -> convertCd(kmId, cd))
                .toList());

        return immunizationEvaluationBuilder.build();
    }

    private Stream<ImmunizationEvaluation> convertSubstanceAdministrationEvent(final String kmId,
            final SubstanceAdministrationEvent substanceAdministrationEvent)
    {
        final String sourceEventId = Optional.ofNullable(substanceAdministrationEvent)
                .map(SubstanceAdministrationEvent::getId)
                .map(this::selectIiExtensionOrRoot)
                .orElse(null);

        final List<ImmunizationEvaluation> relatedEvaluations = Optional.ofNullable(substanceAdministrationEvent)
                .map(SubstanceAdministrationEvent::getRelatedClinicalStatement)
                .stream()
                .flatMap(Collection::stream)
                .map(RelatedClinicalStatement::getSubstanceAdministrationEvent)
                .filter(Objects::nonNull)
                .map(subSubstanceAdministrationEvent -> convertRelatedSubstanceAdministrationEvent(kmId, sourceEventId,
                        subSubstanceAdministrationEvent))
                .toList();
        if (!relatedEvaluations.isEmpty())
            return relatedEvaluations.stream();

        return Stream.of(convertRelatedSubstanceAdministrationEvent(kmId, sourceEventId, substanceAdministrationEvent));
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
        return Optional.ofNullable(observationResult)
                .map(ObservationResult::getObservationFocus)
                .map(CD::getCodeSystem)
                .filter(SERIES_SELECTION_TYPE_CODE_SYSTEM::equals)
                .isPresent();
    }

    private String selectIiExtensionOrRoot(final II identifier)
    {
        if (identifier == null)
            return null;

        final String extension = identifier.getExtension();
        return extension != null && !extension.isBlank() ? extension : identifier.getRoot();
    }

    private byte[] createPayload(final CDSInput cdsInput)
    {
        try (final ByteArrayOutputStream bOut = new ByteArrayOutputStream())
        {
            xmlMapper.writer().withRootName("cdsInput").writeValue(bOut, cdsInput);
            return bOut.toByteArray();
        }
        catch (final IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private EvaluateAtSpecifiedTime createEvaluateAtSpecifiedTime(final String kmId, final InteractionIdentifier interactionId,
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
        request.setClientLanguage("en");
        request.setClientTimeZoneOffset(clientTimeZoneOffset);
        if (kmEvaluationRequest != null)
            request.getKmEvaluationRequest().addAll(kmEvaluationRequest);
        if (dataRequirementItemData != null)
            request.getDataRequirementItemData().addAll(dataRequirementItemData);
        return request;
    }

    private CDSInput getCdsInput(final CdsRequest cdsRequest)
    {
        return createCdsInput(List.of(createIi("2.16.840.1.113883.3.795.11.1.1", null)),
                createCdsContext(createCd("en", "2.16.840.1.113883.6.99", "English")),
                createVmr(List.of(createIi("2.16.840.1.113883.3.795.11.1.1", null)), createEvaluatedPerson(cdsRequest)));
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
        itemIdentifier.setItemId("cdsPayload");
        return itemIdentifier;
    }

    private EvaluatedPerson createEvaluatedPerson(final CdsRequest cdsRequest)
    {
        final EvaluatedPerson evaluatedPerson = new EvaluatedPerson();

        evaluatedPerson.getTemplateId().add(createIi("2.16.840.1.113883.3.795.11.2.1.1", null));
        evaluatedPerson.setId(createIi("2.16.840.1.113883.3.795.12.100.11", cdsRequest.patient().id()));

        final EvaluatedPerson.Demographics demographics = new EvaluatedPerson.Demographics();
        evaluatedPerson.setDemographics(demographics);

        final TS birthTime = new TS();
        birthTime.setValue(convertDate(cdsRequest.patient().birthDate()));
        demographics.setBirthTime(birthTime);
        demographics.setGender(createCd(cdsRequest.patient().sex()));
        final EvaluatedPerson.ClinicalStatements clinicalStatements = new EvaluatedPerson.ClinicalStatements();
        evaluatedPerson.setClinicalStatements(clinicalStatements);
        clinicalStatements.setObservationResults(new EvaluatedPerson.ClinicalStatements.ObservationResults());

        clinicalStatements.getObservationResults()
                .getObservationResult()
                .addAll(Optional.ofNullable(cdsRequest.observations())
                        .stream()
                        .flatMap(Collection::stream)
                        .map(this::createObservationResult)
                        .toList());

        clinicalStatements.setSubstanceAdministrationEvents(createSubstanceAdministrationEvents(
                Optional.ofNullable(cdsRequest.immunizations())
                        .stream()
                        .flatMap(Collection::stream)
                        .map(this::createSubstanceAdministrationEvent)
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

    private InteractionIdentifier createInteractionIdentifier(final String interactionId, final XMLGregorianCalendar submissionTime)
    {
        final InteractionIdentifier interactionIdentifier = new InteractionIdentifier();
        interactionIdentifier.setScopingEntityId("org.nyc.cir");
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

    private CD createCd(final String code, final String codeSystem, final String displayName)
    {
        final CD cd = new CD();
        cd.setCode(code);
        cd.setCodeSystem(codeSystem);
        cd.setDisplayName(displayName);
        return cd;
    }

    private CD createCd(final CodeableConcept codeableConcept)
    {
        final CD cd = new CD();
        if (codeableConcept != null && !ObjectUtils.isEmpty(codeableConcept.coding()))
        {
            codeableConcept.coding().stream().findFirst().ifPresent((coding) ->
            {
                cd.setCode(coding.code());
                cd.setCodeSystem(coding.system());
                cd.setDisplayName(coding.display());
            });
        }
        else
        {
            log.warn("Unable to convert codeable concept to CD: {}", codeableConcept);
        }

        return cd;
    }

    private ObservationResult createObservationResult(final Observation observation)
    {
        final ObservationResult observationResult = new ObservationResult();
        observationResult.getTemplateId().add(createIi("2.16.840.1.113883.3.795.11.6.3.1", null));
        observationResult.setId(createIi(UUID.randomUUID().toString(), null));
        observationResult.getInterpretation().add(createCd("IS_IMMUNE", "2.16.840.1.113883.3.795.12.100.9", "Is Immune"));
        observationResult.setObservationEventTime(Optional.ofNullable(observation.effectiveDateTime())
                .map(CdsRequestResponseConversionComponent::convertDate)
                .map((d) -> createIvlts(d, d))
                .orElse(null));
        observationResult.setObservationFocus(createCd(observation.code()));
        final ObservationResult.ObservationValue observationValue = new ObservationResult.ObservationValue();
        observationResult.setObservationValue(observationValue);
        observationValue.setConcept(createCd(observation.valueCodeableConcept()));
        return observationResult;
    }

    private IVLTS createIvlts(final String high, final String low)
    {
        final IVLTS ivlts = new IVLTS();
        ivlts.setHigh(high);
        ivlts.setLow(low);
        return ivlts;
    }

    private SubstanceAdministrationEvent createSubstanceAdministrationEvent(final Immunization immunization)
    {
        final SubstanceAdministrationEvent substanceAdministrationEvent = new SubstanceAdministrationEvent();
        substanceAdministrationEvent.getTemplateId().add(createIi("2.16.840.1.113883.3.795.11.9.1.1", null));
        substanceAdministrationEvent.setSubstanceAdministrationGeneralPurpose(createCd("384810002", "2.16.840.1.113883.6.5", null));
        substanceAdministrationEvent.setId(Optional.of(immunization.id())
                .map((extension) -> createIi("2.16.840.1.113883.3.795.12.100.10", extension))
                .orElse(null));
        substanceAdministrationEvent.setAdministrationTimeInterval(Optional.ofNullable(immunization.occurrenceDateTime())
                .map(CdsRequestResponseConversionComponent::convertDate)
                .map((d) -> createIvlts(d, d))
                .orElse(null));
        substanceAdministrationEvent.setSubstance(
                createAdministrableSubstance(createIi(UUID.randomUUID().toString(), null), createCd(immunization.vaccineCode())));
        return substanceAdministrationEvent;
    }

    private AdministrableSubstance createAdministrableSubstance(final II id, final CD substanceCode)
    {
        final AdministrableSubstance administrableSubstance = new AdministrableSubstance();
        administrableSubstance.setId(id);
        administrableSubstance.setSubstanceCode(substanceCode);
        return administrableSubstance;
    }
}
