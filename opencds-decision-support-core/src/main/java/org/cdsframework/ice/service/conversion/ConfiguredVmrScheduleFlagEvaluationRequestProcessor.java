package org.cdsframework.ice.service.conversion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.cdsframework.fhir.CodeableConcept;
import org.cdsframework.ice.service.SupportingDataService;
import org.omg.dss.DataRequirementItemData;
import org.omg.dss.EntityIdentifier;
import org.omg.dss.EvaluationRequest;
import org.omg.dss.KMEvaluationRequestBase;
import org.omg.dss.SemanticPayload;
import org.opencds.dss.evaluate.EvaluationRequestPreProcessor;
import org.opencds.vmr.v1_0.schema.BL;
import org.opencds.vmr.v1_0.schema.CD;
import org.opencds.vmr.v1_0.schema.CDSInput;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson;
import org.opencds.vmr.v1_0.schema.II;
import org.opencds.vmr.v1_0.schema.ObservationResult;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.dataformat.xml.XmlMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfiguredVmrScheduleFlagEvaluationRequestProcessor implements EvaluationRequestPreProcessor
{
    private record PayloadAugmentationResult(byte[] payload,
                                             long addedScheduleFlagCount)
    {
    }

    private static final String VMR_SCOPING_ENTITY_ID = "org.opencds.vmr";
    private static final String VMR_BUSINESS_ID = "VMR";
    private static final String VMR_VERSION = "1.0";
    private static final String SCHEDULE_FLAGS_CODE_SYSTEM_OID = "2.16.840.1.113883.3.795.12.100.502";
    private static final String SCHEDULE_FLAGS_CODE_SYSTEM_URL = "http://terminology.cdsframework.org/ice/schedule-flags";
    private static final String TEMPLATE_ID_OBSERVATION_RESULT = "2.16.840.1.113883.3.795.11.6.3.1";
    private static final XmlMapper xmlMapper = XmlMapper.xmlBuilder()
            .defaultUseWrapper(false)
            .findAndAddModules()
            .changeDefaultPropertyInclusion(i -> i.withValueInclusion(JsonInclude.Include.NON_NULL))
            .build();

    private static Stream<ObservationResult> streamObservationResultTree(final ObservationResult observationResult)
    {
        return Stream.concat(Stream.of(observationResult), Optional.ofNullable(observationResult.getRelatedClinicalStatement())
                .stream()
                .flatMap(Collection::stream)
                .map(org.opencds.vmr.v1_0.schema.RelatedClinicalStatement::getObservationResult)
                .filter(Objects::nonNull)
                .flatMap(ConfiguredVmrScheduleFlagEvaluationRequestProcessor::streamObservationResultTree));
    }

    private static Optional<String> extractScheduleFlagCode(final CD concept)
    {
        return Optional.ofNullable(concept)
                .filter(cd -> SCHEDULE_FLAGS_CODE_SYSTEM_OID.equals(cd.getCodeSystem()) || SCHEDULE_FLAGS_CODE_SYSTEM_URL.equals(
                        cd.getCodeSystem()))
                .map(CD::getCode)
                .filter(StringUtils::hasText)
                .map(String::trim);
    }

    private static II createIi(final String root)
    {
        final II ii = new II();
        ii.setRoot(root);
        ii.setExtension(null);
        return ii;
    }

    private static boolean isVmrPayload(final SemanticPayload payload)
    {
        final EntityIdentifier informationModel = payload.getInformationModelSSId();
        return informationModel != null && VMR_SCOPING_ENTITY_ID.equals(informationModel.getScopingEntityId())
                && VMR_BUSINESS_ID.equals(informationModel.getBusinessId()) && VMR_VERSION.equals(informationModel.getVersion());
    }

    private static CDSInput readPayload(final byte[] payload)
    {
        try (final Reader reader = new InputStreamReader(new ByteArrayInputStream(payload), StandardCharsets.UTF_8))
        {
            return xmlMapper.readValue(reader, CDSInput.class);
        }
        catch (final IOException e)
        {
            throw new IllegalArgumentException("Failed to parse VMR payload", e);
        }
    }

    private static byte[] writePayload(final CDSInput cdsInput)
    {
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream())
        {
            xmlMapper.writeValue(outputStream, cdsInput);
            return outputStream.toByteArray();
        }
        catch (final IOException e)
        {
            throw new IllegalArgumentException("Failed to serialize VMR payload", e);
        }
    }

    private final SupportingDataService supportingDataService;

    @Override
    public Result process(final EvaluationRequest evaluationRequest)
    {
        final List<String> configuredScheduleFlags =
                Optional.ofNullable(supportingDataService.getConfiguredScheduleFlags()).orElseGet(List::of);

        final AtomicReference<String> primaryKmId = new AtomicReference<>();

        Optional.ofNullable(evaluationRequest.getKmEvaluationRequest())
                .stream()
                .flatMap(Collection::stream)
                .map(KMEvaluationRequestBase::getKmId)
                .filter(Objects::nonNull)
                .map(entityIdentifier -> Stream.of(entityIdentifier.getScopingEntityId(), entityIdentifier.getBusinessId(),
                                entityIdentifier.getVersion())
                        .map(value -> Optional.ofNullable(value).map(String::trim).orElse(""))
                        .collect(java.util.stream.Collectors.joining("^")))
                .filter(StringUtils::hasText)
                .distinct().forEach(kmId ->
                {
                    primaryKmId.compareAndSet(null, kmId);
                    if (!ObjectUtils.isEmpty(configuredScheduleFlags))
                        supportingDataService.validateScheduleFlagsForKmId(kmId, configuredScheduleFlags);
                });

        if (primaryKmId.get() == null)
            return Result.NOOP;

        final AtomicLong payloadCount = new AtomicLong();
        final AtomicLong augmentedPayloadCount = new AtomicLong();
        final AtomicLong addedScheduleFlagCount = new AtomicLong();

        Optional.ofNullable(evaluationRequest.getDataRequirementItemData())
                .stream()
                .flatMap(Collection::stream)
                .map(DataRequirementItemData::getData)
                .filter(Objects::nonNull).filter(ConfiguredVmrScheduleFlagEvaluationRequestProcessor::isVmrPayload).forEach(payload ->
                {
                    final Result payloadResult = augmentPayloads(primaryKmId.get(), configuredScheduleFlags, payload);
                    payloadCount.accumulateAndGet(payloadResult.payloadCount(), Long::sum);
                    augmentedPayloadCount.accumulateAndGet(payloadResult.augmentedPayloadCount(), Long::sum);
                    addedScheduleFlagCount.accumulateAndGet(payloadResult.addedScheduleFlagCount(), Long::sum);
                });

        return new Result(payloadCount.get(), augmentedPayloadCount.get(), addedScheduleFlagCount.get());
    }

    private Result augmentPayloads(final String kmId, final List<String> configuredScheduleFlags, final SemanticPayload payload)
    {
        final List<byte[]> payloads = payload.getBase64EncodedPayload();
        if (ObjectUtils.isEmpty(payloads))
            return Result.NOOP;

        long augmentedPayloadCount = 0;
        long addedScheduleFlagCount = 0;

        for (int i = 0; i < payloads.size(); i++)
        {
            final PayloadAugmentationResult result = addMissingScheduleFlags(kmId, configuredScheduleFlags, payloads.get(i));
            payloads.set(i, result.payload());
            if (result.addedScheduleFlagCount() > 0)
            {
                augmentedPayloadCount++;
                addedScheduleFlagCount += result.addedScheduleFlagCount();
            }
        }

        return new Result(payloads.size(), augmentedPayloadCount, addedScheduleFlagCount);
    }

    private PayloadAugmentationResult addMissingScheduleFlags(final String kmId, final List<String> configuredScheduleFlags,
            final byte[] payload)
    {
        if (ObjectUtils.isEmpty(configuredScheduleFlags))
            return new PayloadAugmentationResult(payload, 0);

        final CDSInput cdsInput = readPayload(payload);
        final EvaluatedPerson patient = Optional.ofNullable(cdsInput)
                .map(CDSInput::getVmrInput)
                .map(org.opencds.vmr.v1_0.schema.VMR::getPatient)
                .orElse(null);
        if (patient == null)
            return new PayloadAugmentationResult(payload, 0);

        patient.setClinicalStatements(Optional.ofNullable(patient.getClinicalStatements())
                .orElseGet(EvaluatedPerson.ClinicalStatements::new));
        patient.getClinicalStatements().setObservationResults(Optional.ofNullable(patient.getClinicalStatements().getObservationResults())
                .orElseGet(EvaluatedPerson.ClinicalStatements.ObservationResults::new));

        final List<ObservationResult> observationResults =
                patient.getClinicalStatements().getObservationResults().getObservationResult();
        final Set<String> existingFlags = observationResults
                .stream()
                .flatMap(ConfiguredVmrScheduleFlagEvaluationRequestProcessor::streamObservationResultTree)
                .map(ObservationResult::getObservationFocus)
                .flatMap(cd -> extractScheduleFlagCode(cd).stream())
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));

        final List<String> missingFlags = configuredScheduleFlags.stream().filter(Predicate.not(existingFlags::contains)).toList();
        if (missingFlags.isEmpty())
            return new PayloadAugmentationResult(payload, 0);

        missingFlags.stream().map(flag -> createScheduleFlagObservationResult(kmId, flag)).forEach(observationResults::add);
        return new PayloadAugmentationResult(writePayload(cdsInput), missingFlags.size());
    }

    private ObservationResult createScheduleFlagObservationResult(final String kmId, final String scheduleFlag)
    {
        final CodeableConcept scheduleFlagConcept =
                supportingDataService.getCodeableConcept(kmId, scheduleFlag, null, SCHEDULE_FLAGS_CODE_SYSTEM_OID, null);
        final CD scheduleFlagCd = createCd(kmId, scheduleFlagConcept);

        final ObservationResult observationResult = new ObservationResult();
        observationResult.getTemplateId().add(createIi(TEMPLATE_ID_OBSERVATION_RESULT));
        observationResult.setId(createIi(UUID.randomUUID().toString()));
        observationResult.setObservationFocus(scheduleFlagCd);

        final ObservationResult.ObservationValue observationValue = new ObservationResult.ObservationValue();
        final BL value = new BL();
        value.setValue(true);
        observationValue.setBoolean(value);
        observationResult.setObservationValue(observationValue);

        return observationResult;
    }

    private CD createCd(final String kmId, final CodeableConcept codeableConcept)
    {
        final CD cd = new CD();
        Optional.ofNullable(codeableConcept)
                .map(CodeableConcept::coding)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .ifPresent(coding ->
                {
                    cd.setCode(coding.code());
                    cd.setCodeSystem(supportingDataService.toRequiredInternalCodeSystemOid(kmId, coding.system()));
                    cd.setDisplayName(StringUtils.hasText(coding.display())
                                      ? coding.display()
                                      : StringUtils.hasText(codeableConcept.text()) ? codeableConcept.text() : coding.code());
                });
        return cd;
    }
}
