package org.cdsframework.ice.service.conversion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.cdsframework.fhir.CodeSystem;
import org.cdsframework.fhir.CodeSystemConcept;
import org.cdsframework.fhir.CodeSystemContentModeEnum;
import org.cdsframework.fhir.Identifier;
import org.cdsframework.fhir.PlanDefinition;
import org.cdsframework.fhir.PublicationStatusEnum;
import org.cdsframework.ice.config.CdsEngineProperties;
import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.service.SupportingDataService;
import org.junit.jupiter.api.Test;
import org.omg.dss.DataRequirementItemData;
import org.omg.dss.EntityIdentifier;
import org.omg.dss.EvaluationRequest;
import org.omg.dss.KMEvaluationRequest;
import org.omg.dss.SemanticPayload;
import org.opencds.vmr.v1_0.schema.BL;
import org.opencds.vmr.v1_0.schema.CD;
import org.opencds.vmr.v1_0.schema.CDSContext;
import org.opencds.vmr.v1_0.schema.CDSInput;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson;
import org.opencds.vmr.v1_0.schema.II;
import org.opencds.vmr.v1_0.schema.ObservationResult;
import org.opencds.vmr.v1_0.schema.TS;
import org.opencds.vmr.v1_0.schema.VMR;
import org.springframework.core.io.ByteArrayResource;

import com.fasterxml.jackson.annotation.JsonInclude;

import tools.jackson.dataformat.xml.XmlMapper;

class ConfiguredVmrScheduleFlagEvaluationRequestProcessorTest
{
    private static final String KM_ID = "org.nyc.cir^ICE^1.0.0";
    private static final String MODULE_CANONICAL = "http://cdsframework.org/PlanDefinition/ice-forecast|1.0.0";
    private static final String SCHEDULE_FLAGS_OID = "2.16.840.1.113883.3.795.12.100.502";
    private static final XmlMapper XML_MAPPER = XmlMapper.xmlBuilder()
            .defaultUseWrapper(false)
            .findAndAddModules()
            .changeDefaultPropertyInclusion(i -> i.withValueInclusion(JsonInclude.Include.NON_NULL))
            .build();

    private static CdsEngineProperties createCdsEngineProperties()
    {
        final CdsEngineProperties properties = new CdsEngineProperties();
        properties.setKnowledgeBaseDefinitionMap(Map.of(MODULE_CANONICAL, new CdsEngineProperties.KnowledgeBaseDefinition(
                PlanDefinition.builder()
                        .identifier(Identifier.builder()
                                .system("https://terminology.cdsframework.org/ice/identifiers/knowledge-bases")
                                .value(KM_ID)
                                .build())
                        .build(), Map.of(), Map.of(), Map.of("SUPPORTED_SCHEDULE_FLAGS", CodeSystem.builder()
                .name("SUPPORTED_SCHEDULE_FLAGS")
                .identifier(Identifier.builder().system("urn:ietf:rfc:3986").value("urn:oid:" + SCHEDULE_FLAGS_OID).build())
                .url("https://terminology.cdsframework.org/ice/schedule-flags")
                .status(PublicationStatusEnum.ACTIVE)
                .content(CodeSystemContentModeEnum.COMPLETE)
                .concept(CodeSystemConcept.builder()
                        .code("HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID")
                        .display("Evaluate Invalid 3rd Hep B Dose as Accepted Extra Dose")
                        .build())
                .concept(CodeSystemConcept.builder()
                        .code("POLIO_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID")
                        .display("Evaluate 4th/5th Polio Dose Below Minimum Age as Accepted Extra Dose")
                        .build())
                .build()), Map.of())));
        return properties;
    }

    private static IceProperties createIceProperties(final List<String> scheduleFlags)
    {
        final IceProperties properties = new IceProperties();
        properties.setIceBaseKnowledgeBase(MODULE_CANONICAL);
        properties.setKnowledgeBases(Map.of(MODULE_CANONICAL,
                new IceProperties.KnowledgeBaseProperties(true, false, true, false, true, false, false, false, List.of(), List.of(),
                        false, IceProperties.SupplementalTextMode.LEGACY, new ByteArrayResource(new byte[0]))));
        properties.setScheduleFlags(scheduleFlags);
        return properties;
    }

    private static ConfiguredVmrScheduleFlagEvaluationRequestProcessor createProcessor(final List<String> scheduleFlags)
    {
        return new ConfiguredVmrScheduleFlagEvaluationRequestProcessor(
                new SupportingDataService(createCdsEngineProperties(), createIceProperties(scheduleFlags)));
    }

    private static EvaluationRequest createEvaluationRequest(final CDSInput cdsInput)
    {
        return createEvaluationRequest(cdsInput, false);
    }

    private static EvaluationRequest createEvaluationRequest(final CDSInput cdsInput, final boolean gzipPayload)
    {
        final EntityIdentifier vmrModel = new EntityIdentifier();
        vmrModel.setScopingEntityId("org.opencds.vmr");
        vmrModel.setBusinessId("VMR");
        vmrModel.setVersion("1.0");

        final SemanticPayload payload = new SemanticPayload();
        payload.setInformationModelSSId(vmrModel);
        final byte[] serializedPayload = XML_MAPPER.writeValueAsBytes(cdsInput);
        payload.getBase64EncodedPayload().add(gzipPayload ? gzip(serializedPayload) : serializedPayload);

        final DataRequirementItemData dataRequirementItemData = new DataRequirementItemData();
        if (gzipPayload)
        {
            final EntityIdentifier gzipEntityIdentifier = new EntityIdentifier();
            gzipEntityIdentifier.setBusinessId("gzip");
            final org.omg.dss.ItemIdentifier gzipDriId = new org.omg.dss.ItemIdentifier();
            gzipDriId.setContainingEntityId(gzipEntityIdentifier);
            dataRequirementItemData.setDriId(gzipDriId);
        }
        dataRequirementItemData.setData(payload);

        final EntityIdentifier kmEntityIdentifier = new EntityIdentifier();
        kmEntityIdentifier.setScopingEntityId("org.nyc.cir");
        kmEntityIdentifier.setBusinessId("ICE");
        kmEntityIdentifier.setVersion("1.0.0");

        final KMEvaluationRequest kmEvaluationRequest = new KMEvaluationRequest();
        kmEvaluationRequest.setKmId(kmEntityIdentifier);

        final EvaluationRequest evaluationRequest = new EvaluationRequest();
        evaluationRequest.getKmEvaluationRequest().add(kmEvaluationRequest);
        evaluationRequest.getDataRequirementItemData().add(dataRequirementItemData);
        return evaluationRequest;
    }

    private static byte[] gzip(final byte[] payload)
    {
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(payload.length);
                final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream))
        {
            gzipOutputStream.write(payload);
            gzipOutputStream.finish();
            return outputStream.toByteArray();
        }
        catch (final IOException e)
        {
            throw new IllegalStateException("Failed to create test gzip payload", e);
        }
    }

    private static byte[] gunzip(final byte[] payload) throws Exception
    {
        try (final GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(payload));
                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(payload.length))
        {
            gzipInputStream.transferTo(outputStream);
            return outputStream.toByteArray();
        }
    }

    private static CDSInput createCdsInput(final List<ObservationResult> observationResults)
    {
        final CDSInput cdsInput = new CDSInput();
        cdsInput.setCdsContext(new CDSContext());

        final EvaluatedPerson evaluatedPerson = new EvaluatedPerson();
        evaluatedPerson.getTemplateId().add(createIi("2.16.840.1.113883.3.795.11.2.1.1", null));
        evaluatedPerson.setId(createIi("2.16.840.1.113883.3.795.12.100.11", "p1"));
        final EvaluatedPerson.Demographics demographics = new EvaluatedPerson.Demographics();
        final TS birthTime = new TS();
        birthTime.setValue(LocalDate.parse("2020-01-15").format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")));
        demographics.setBirthTime(birthTime);
        demographics.setGender(createCd("F", "2.16.840.1.113883.5.1", "F"));
        evaluatedPerson.setDemographics(demographics);
        final EvaluatedPerson.ClinicalStatements clinicalStatements = new EvaluatedPerson.ClinicalStatements();
        clinicalStatements.setObservationResults(new EvaluatedPerson.ClinicalStatements.ObservationResults());
        clinicalStatements.getObservationResults().getObservationResult().addAll(observationResults);
        evaluatedPerson.setClinicalStatements(clinicalStatements);

        final VMR vmr = new VMR();
        vmr.setPatient(evaluatedPerson);
        cdsInput.setVmrInput(vmr);
        return cdsInput;
    }

    private static CDSInput readPayload(final EvaluationRequest evaluationRequest) throws Exception
    {
        try (final Reader reader = new InputStreamReader(new ByteArrayInputStream(
                evaluationRequest.getDataRequirementItemData().getFirst().getData().getBase64EncodedPayload().getFirst()),
                StandardCharsets.UTF_8))
        {
            return XML_MAPPER.readValue(reader, CDSInput.class);
        }
    }

    private static List<String> extractScheduleFlagsInOrder(final CDSInput cdsInput)
    {
        return Optional.ofNullable(cdsInput.getVmrInput())
                .map(VMR::getPatient)
                .map(EvaluatedPerson::getClinicalStatements)
                .map(EvaluatedPerson.ClinicalStatements::getObservationResults)
                .map(EvaluatedPerson.ClinicalStatements.ObservationResults::getObservationResult)
                .stream()
                .flatMap(Collection::stream)
                .flatMap(ConfiguredVmrScheduleFlagEvaluationRequestProcessorTest::streamObservationResultTree)
                .map(ObservationResult::getObservationFocus)
                .filter(Objects::nonNull)
                .filter(cd -> SCHEDULE_FLAGS_OID.equals(cd.getCodeSystem()))
                .map(CD::getCode)
                .toList();
    }

    private static Stream<ObservationResult> streamObservationResultTree(final ObservationResult observationResult)
    {
        return Stream.concat(Stream.of(observationResult), Optional.ofNullable(observationResult.getRelatedClinicalStatement())
                .stream()
                .flatMap(Collection::stream)
                .map(org.opencds.vmr.v1_0.schema.RelatedClinicalStatement::getObservationResult)
                .filter(Objects::nonNull)
                .flatMap(ConfiguredVmrScheduleFlagEvaluationRequestProcessorTest::streamObservationResultTree));
    }

    private static ObservationResult createScheduleFlagObservation(final String scheduleFlag)
    {
        final ObservationResult observationResult = new ObservationResult();
        observationResult.getTemplateId().add(createIi("2.16.840.1.113883.3.795.11.6.3.1", null));
        observationResult.setId(createIi(UUID.randomUUID().toString(), null));
        observationResult.setObservationFocus(createCd(scheduleFlag, SCHEDULE_FLAGS_OID, scheduleFlag));
        final ObservationResult.ObservationValue observationValue = new ObservationResult.ObservationValue();
        final BL value = new BL();
        value.setValue(true);
        observationValue.setBoolean(value);
        observationResult.setObservationValue(observationValue);
        return observationResult;
    }

    private static II createIi(final String root, final String extension)
    {
        final II ii = new II();
        ii.setRoot(root);
        ii.setExtension(extension);
        return ii;
    }

    private static CD createCd(final String code, final String codeSystem, final String displayName)
    {
        final CD cd = new CD();
        cd.setCode(code);
        cd.setCodeSystem(codeSystem);
        cd.setDisplayName(displayName);
        return cd;
    }

    @Test
    void addsConfiguredScheduleFlagsToIncomingVmrPayloadWhenMissing() throws Exception
    {
        final ConfiguredVmrScheduleFlagEvaluationRequestProcessor processor =
                createProcessor(List.of("HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID"));
        final EvaluationRequest evaluationRequest = createEvaluationRequest(createCdsInput(List.of()));

        processor.process(evaluationRequest);

        final CDSInput cdsInput = readPayload(evaluationRequest);
        final Set<String> scheduleFlags = new HashSet<>(extractScheduleFlagsInOrder(cdsInput));

        assertEquals(Set.of("HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID"), scheduleFlags);

        final ObservationResult observationResult = cdsInput.getVmrInput()
                .getPatient()
                .getClinicalStatements()
                .getObservationResults()
                .getObservationResult()
                .getFirst();
        assertNotNull(observationResult.getObservationValue().getBoolean());
        assertTrue(observationResult.getObservationValue().getBoolean().isValue());
    }

    @Test
    void doesNotDuplicateConfiguredScheduleFlagsAlreadyPresentInIncomingVmrPayload() throws Exception
    {
        final ConfiguredVmrScheduleFlagEvaluationRequestProcessor processor =
                createProcessor(List.of("HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID"));
        final EvaluationRequest evaluationRequest = createEvaluationRequest(
                createCdsInput(List.of(createScheduleFlagObservation("HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID"))));

        processor.process(evaluationRequest);

        final CDSInput cdsInput = readPayload(evaluationRequest);
        final List<String> scheduleFlags = extractScheduleFlagsInOrder(cdsInput);

        assertEquals(List.of("HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID"), scheduleFlags);
    }

    @Test
    void addsAllConfiguredScheduleFlagsIncludingHepBAndPolioWhenMissing() throws Exception
    {
        final ConfiguredVmrScheduleFlagEvaluationRequestProcessor processor = createProcessor(
                List.of("HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID", "POLIO_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID"));
        final EvaluationRequest evaluationRequest = createEvaluationRequest(createCdsInput(List.of()));

        processor.process(evaluationRequest);

        final CDSInput cdsInput = readPayload(evaluationRequest);
        final Set<String> scheduleFlags = new HashSet<>(extractScheduleFlagsInOrder(cdsInput));
        assertEquals(Set.of("HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID", "POLIO_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID"),
                scheduleFlags);
    }

    @Test
    void preservesGzipPayloadWhileAddingConfiguredScheduleFlags() throws Exception
    {
        final ConfiguredVmrScheduleFlagEvaluationRequestProcessor processor = createProcessor(
                List.of("HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID", "POLIO_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID"));
        final EvaluationRequest evaluationRequest = createEvaluationRequest(createCdsInput(List.of()), true);

        processor.process(evaluationRequest);

        final byte[] payload =
                evaluationRequest.getDataRequirementItemData().getFirst().getData().getBase64EncodedPayload().getFirst();
        assertEquals((byte) 0x1f, payload[0]);
        assertEquals((byte) 0x8b, payload[1]);
        final CDSInput cdsInput = XML_MAPPER.readValue(gunzip(payload), CDSInput.class);
        assertEquals(Set.of("HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID", "POLIO_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID"),
                new HashSet<>(extractScheduleFlagsInOrder(cdsInput)));
    }
}
