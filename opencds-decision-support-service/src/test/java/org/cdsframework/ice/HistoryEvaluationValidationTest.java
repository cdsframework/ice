package org.cdsframework.ice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.ice.service.DoseStatus;
import org.cdsframework.ice.service.HistoryEvaluationValidationHelper.ValidationResult;
import org.cdsframework.ice.service.HistoryEvaluationValidationState;
import org.cdsframework.ice.service.HistoryEvaluationValidationState.ValidationStatus;
import org.cdsframework.ice.service.ICEFactTypeFinding;
import org.cdsframework.ice.service.RecommendationStatus;
import org.cdsframework.ice.service.Schedule;
import org.cdsframework.ice.service.TargetDose;
import org.cdsframework.ice.service.TargetSeries;
import org.cdsframework.ice.service.Vaccine;
import org.cdsframework.ice.service.VaccineComponent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;
import org.opencds.vmr.v1_0.internal.ClinicalStatementRelationship;
import org.opencds.vmr.v1_0.internal.ObservationResult;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationProposal;
import org.opencds.vmr.v1_0.internal.datatypes.CD;

class HistoryEvaluationValidationTest
{
    public static final class SeriesSelectionContinuation
    {
    }

    public static final class RecommendationForecastContinuation
    {
    }

    private static final Path RULE_RESOURCE_ROOT = RuleTestResources.sourceResources();
    private static final String FINDING_CODE = "MULTIPLE_VACCINE_GROUP_TARGET_DOSE_ASSOCIATION";
    private static final String HISTORY_EVALUATION_VALIDATION_DRL =
            "drools/knowledgeCommon/org.cdsframework.ice/org.cdsframework^ICE^1.0.0^HistoryEvaluationValidation.drl";
    private static final String PRIMARY_PROCESS = "PrimaryProcess";
    private static final String PRIMARY_PROCESS_BPMN =
            "drools/knowledgeCommon/org.cdsframework.ice/org.cdsframework^ICE^1.0.0.bpmn";

    private static void assertPassedAndContinued(final KieSession session)
    {
        assertThat(validationState(session).getValidationStatus()).isEqualTo(ValidationStatus.PASSED);
        assertThat(facts(session, SeriesSelectionContinuation.class)).hasSize(1);
        assertThat(facts(session, RecommendationForecastContinuation.class)).hasSize(1);
        assertThat(facts(session, ValidationResult.class)).isEmpty();
    }

    private static void assertNotContinued(final KieSession session)
    {
        assertThat(facts(session, SeriesSelectionContinuation.class)).isEmpty();
        assertThat(facts(session, RecommendationForecastContinuation.class)).isEmpty();
    }

    private static void executeValidationAndDownstreamGroups(final KieSession session)
    {
        session.getAgenda().getAgendaGroup("HistoryEvaluationValidation").setFocus();
        session.fireAllRules();
        if (validationState(session).getValidationStatus() == ValidationStatus.PASSED)
        {
            session.getAgenda().getAgendaGroup("SeriesSelection").setFocus();
            session.fireAllRules();
            session.getAgenda().getAgendaGroup("RecommendationForecast").setFocus();
            session.fireAllRules();
        }
    }

    private static <T> List<T> facts(final KieSession session, final Class<T> type)
    {
        return session.getObjects(type::isInstance).stream().map(type::cast).toList();
    }

    private static KieSession newSession(final List<TargetDose> targetDoses)
    {
        final KieSession session = configuredKieHelper().build().newKieSession();
        targetDoses.stream().map(TargetDose::getAssociatedTargetSeries).distinct().forEach(session::insert);
        targetDoses.forEach(session::insert);
        return session;
    }

    private static KieSession newPrimaryProcessSession(final List<TargetDose> targetDoses)
    {
        final KieHelper kieHelper = configuredKieHelper();
        kieHelper.addResource(ResourceFactory.newFileResource(RULE_RESOURCE_ROOT.resolve(PRIMARY_PROCESS_BPMN).toFile()),
                ResourceType.BPMN2);

        final KieSession session = kieHelper.build().newKieSession();
        // Keep BPMN rule tasks on this session so the process can advance as its ruleflow groups finish.
        session.getEnvironment().set("org.jbpm.rule.task.waitstate", true);
        final Schedule schedule = org.mockito.Mockito.mock(Schedule.class, org.mockito.Mockito.RETURNS_DEEP_STUBS);
        when(schedule.isScheduleInitialized()).thenReturn(true);
        List.of(RecommendationStatus.RECOMMENDATION_NOT_AVAILABLE.getCdsListItemName(), "VACCINE_GROUP_CONCEPT.OTHER",
                "RECOMMENDATION_REASON_CONCEPT.NOT_SUPPORTED").forEach((final String code) ->
        {
            final CD concept = new CD();
            concept.setCodeSystem("test-code-system");
            concept.setCode(code);
            when(schedule.getICESupportingDataConfiguration()
                    .getSupportedCdsLists()
                    .getCdsListItem(code)
                    .getCdsListItemCD()).thenReturn(concept);
        });
        session.setGlobal("schedule", schedule);
        session.setGlobal("namedObjects", new HashMap<String, Object>());
        session.setGlobal("focalPersonId", "test-patient");
        session.setGlobal("iceVersion", "test-version");
        targetDoses.stream().map(TargetDose::getAssociatedTargetSeries).distinct().forEach(session::insert);
        targetDoses.forEach(session::insert);
        return session;
    }

    private static KieHelper configuredKieHelper()
    {
        final KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(validationGlobals(), ResourceType.DRL);
        // The executable-model build removes the source rules from target/classes.
        kieHelper.addResource(
                ResourceFactory.newFileResource(RULE_RESOURCE_ROOT.resolve(HISTORY_EVALUATION_VALIDATION_DRL).toFile()),
                ResourceType.DRL);
        kieHelper.addContent(testRules(), ResourceType.DRL);
        return kieHelper;
    }

    private static String testRules()
    {
        return """
               package org.cdsframework.ice;
               
               import org.cdsframework.ice.HistoryEvaluationValidationTest.SeriesSelectionContinuation;
               import org.cdsframework.ice.HistoryEvaluationValidationTest.RecommendationForecastContinuation;
               
               rule "HistoryEvaluationValidationTest: Series selection continuation"
                   ruleflow-group "SeriesSelection"
               when
               then
                   insert(new SeriesSelectionContinuation());
               end
               
               rule "HistoryEvaluationValidationTest: Recommendation forecast continuation"
                   ruleflow-group "RecommendationForecast"
               when
               then
                   insert(new RecommendationForecastContinuation());
               end""";
    }

    private static String validationGlobals()
    {
        return """
               package knowledgeCommon.org.cdsframework.ice;
               
               global java.lang.String focalPersonId;
               global java.util.Map namedObjects;
               global org.cdsframework.ice.service.Schedule schedule;
               global java.lang.Boolean outputSupplementalText;
               global java.lang.Boolean outputScheduleAuthorities;
               global java.lang.Boolean outputVaccineGroupRulesArtifact;
               global java.lang.String iceVersion;""";
    }

    private static TargetDose targetDose(final String doseId, final String seriesName, final String vaccineGroup,
            final boolean validInMultipleVaccineGroups)
    {
        final VaccineComponent vaccineComponent = vaccineComponent();
        return targetDose(doseId, seriesName, vaccineGroup, vaccine(vaccineComponent, validInMultipleVaccineGroups),
                vaccineComponent);
    }

    private static TargetDose targetDose(final String doseId, final String seriesName, final String vaccineGroup,
            final Vaccine vaccine, final VaccineComponent vaccineComponent)
    {
        final TargetSeries targetSeries = org.mockito.Mockito.mock(TargetSeries.class);
        when(targetSeries.isHistoryEvaluationInitiated()).thenReturn(true);
        when(targetSeries.getSeriesName()).thenReturn(seriesName);
        when(targetSeries.getVaccineGroup()).thenReturn(vaccineGroup);

        final SubstanceAdministrationEvent substanceAdministrationEvent = new SubstanceAdministrationEvent();
        substanceAdministrationEvent.setId(doseId);

        final TargetDose targetDose =
                new TargetDose(vaccine, vaccineComponent, LocalDate.of(2020, 1, 1), targetSeries, substanceAdministrationEvent);
        targetDose.setStatus(DoseStatus.VALID);
        return targetDose;
    }

    private static VaccineComponent vaccineComponent()
    {
        return new VaccineComponent(new CdsConcept("VACCINE_CVX_121"), List.of("DISEASE_CONCEPT_VARICELLA"));
    }

    private static Vaccine vaccine(final VaccineComponent vaccineComponent, final boolean validInMultipleVaccineGroups)
    {
        final Vaccine vaccine = new Vaccine(new CdsConcept("VACCINE_CVX_121"), Collections.singletonList(vaccineComponent));
        vaccine.setValidInMultipleVaccineGroups(validInMultipleVaccineGroups);
        return vaccine;
    }

    private static HistoryEvaluationValidationState validationState(final KieSession session)
    {
        return facts(session, HistoryEvaluationValidationState.class).getFirst();
    }

    private static void assertControlledErrorResponse(final KieSession session)
    {
        final List<SubstanceAdministrationProposal> proposals = facts(session, SubstanceAdministrationProposal.class);
        assertThat(proposals).hasSize(1);
        assertThat(proposals.getFirst().isToBeReturned()).isTrue();
        final List<ObservationResult> observations = facts(session, ObservationResult.class);
        assertThat(observations).hasSize(1);
        assertThat(observations.getFirst().isToBeReturned()).isTrue();
        assertThat(observations.getFirst().getObservationValue().getConcept().getCode()).isEqualTo(
                RecommendationStatus.RECOMMENDATION_NOT_AVAILABLE.getCdsListItemName());
        assertThat(facts(session, ClinicalStatementRelationship.class)).hasSize(1);
    }

    @Test
    void primaryProcessRoutesPassedValidationWithoutMvelArrayFilterFailure()
    {
        final TargetDose varicellaDose = targetDose("dose-121", "VARICELLA_2_DOSE_SERIES", "VARICELLA", true);

        try (final KieSession session = newPrimaryProcessSession(List.of(varicellaDose)))
        {
            assertThatCode(() -> session.startProcess(PRIMARY_PROCESS)).doesNotThrowAnyException();
            session.fireAllRules();
            assertPassedAndContinued(session);
        }
    }

    @Test
    void terminalNotEvaluatedMenBDosePassesAndPrimaryProcessContinues()
    {
        final VaccineComponent component =
                new VaccineComponent(new CdsConcept("VACCINE_CVX_164"), List.of("DISEASE_CONCEPT_MENINGOCOCCAL_B"));
        final Vaccine vaccine = new Vaccine(new CdsConcept("VACCINE_CVX_164"), Collections.singletonList(component));
        final TargetDose ignoredDose = targetDose("dose-164", "MEN_B_4_C_2_DOSE_SERIES", "MENINGOCOCCAL_B", vaccine, component);
        ignoredDose.setStatus(DoseStatus.NOT_EVALUATED);
        ignoredDose.addNotEvaluatedReason("EVALUATION_REASON_CONCEPT.VACCINE_NOT_SUPPORTED");
        ignoredDose.setIsShotIgnored(true);
        final VaccineComponent validComponent =
                new VaccineComponent(new CdsConcept("VACCINE_CVX_163"), List.of("DISEASE_CONCEPT_MENINGOCOCCAL_B"));
        final Vaccine validVaccine = new Vaccine(new CdsConcept("VACCINE_CVX_163"), Collections.singletonList(validComponent));
        final TargetDose validDose =
                targetDose("dose-163", "MEN_B_4_C_2_DOSE_SERIES", "MENINGOCOCCAL_B", validVaccine, validComponent);

        try (final KieSession session = newPrimaryProcessSession(List.of(ignoredDose, validDose)))
        {
            session.startProcess(PRIMARY_PROCESS);
            session.fireAllRules();

            assertPassedAndContinued(session);
            assertThat(ignoredDose.getStatus()).isEqualTo(DoseStatus.NOT_EVALUATED);
            assertThat(ignoredDose.hasBeenEvaluated()).isFalse();
            assertThat(facts(session, SubstanceAdministrationProposal.class)).isEmpty();
        }
    }

    @ParameterizedTest
    @EnumSource(value = DoseStatus.class, names = { "ACCEPTED", "INVALID", "VALID", "NOT_EVALUATED" },
                mode = EnumSource.Mode.EXCLUDE)
    @NullSource
    void unfinishedDoseBlocksValidationUntilItsOutcomeIsTerminal(final DoseStatus status)
    {
        final TargetDose terminalDose = targetDose("ignored-dose", "MEN_B_4_C_2_DOSE_SERIES", "MENINGOCOCCAL_B", false);
        terminalDose.setStatus(DoseStatus.NOT_EVALUATED);
        final TargetDose unfinishedDose = targetDose("unfinished-dose", "MEN_B_4_C_2_DOSE_SERIES", "MENINGOCOCCAL_B", false);
        unfinishedDose.setStatus(status);

        try (final KieSession session = newSession(List.of(terminalDose, unfinishedDose)))
        {
            executeValidationAndDownstreamGroups(session);

            assertThat(validationState(session).getValidationStatus()).isEqualTo(ValidationStatus.NOT_RUN);
            assertThat(facts(session, ValidationResult.class)).isEmpty();
            assertNotContinued(session);

            unfinishedDose.setStatus(DoseStatus.NOT_EVALUATED);
            session.update(session.getFactHandle(unfinishedDose), unfinishedDose);
            executeValidationAndDownstreamGroups(session);

            assertPassedAndContinued(session);
        }
    }

    @Test
    void uninitiatedSeriesBlocksValidationUntilHistoryEvaluationStarts()
    {
        final TargetDose dose = targetDose("dose", "MEN_B_4_C_2_DOSE_SERIES", "MENINGOCOCCAL_B", false);
        dose.setStatus(DoseStatus.NOT_EVALUATED);
        final TargetSeries series = dose.getAssociatedTargetSeries();
        when(series.isHistoryEvaluationInitiated()).thenReturn(false);

        try (final KieSession session = newSession(List.of(dose)))
        {
            executeValidationAndDownstreamGroups(session);
            assertThat(validationState(session).getValidationStatus()).isEqualTo(ValidationStatus.NOT_RUN);
            assertNotContinued(session);

            when(series.isHistoryEvaluationInitiated()).thenReturn(true);
            session.update(session.getFactHandle(series), series);
            executeValidationAndDownstreamGroups(session);
            assertPassedAndContinued(session);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void primaryProcessReturnsControlledErrorWhenValidationUnexpectedlyDoesNotRun(final boolean uninitiatedSeries)
    {
        final TargetDose dose = targetDose("unfinished-dose", "MEN_B_4_C_2_DOSE_SERIES", "MENINGOCOCCAL_B", false);
        if (uninitiatedSeries)
            when(dose.getAssociatedTargetSeries().isHistoryEvaluationInitiated()).thenReturn(false);
        else
            dose.setStatus(DoseStatus.EVALUATION_IN_PROCESS);

        try (final KieSession session = newPrimaryProcessSession(List.of(dose)))
        {
            session.startProcess(PRIMARY_PROCESS);
            session.fireAllRules();

            assertThat(validationState(session).getValidationStatus()).isEqualTo(ValidationStatus.FAILED);
            assertThat(validationState(session).getDiagnosticMessage()).contains("validation did not run");
            assertNotContinued(session);
            assertThat(facts(session, ValidationResult.class)).isEmpty();
            assertControlledErrorResponse(session);

            session.getAgenda().getAgendaGroup("HistoryEvaluationValidationFailure").setFocus();
            session.fireAllRules();
            assertControlledErrorResponse(session);
        }
    }

    @ParameterizedTest
    @EnumSource(value = DoseStatus.class, names = { "ACCEPTED", "INVALID", "VALID", "NOT_EVALUATED" })
    void residualCvx121TargetDosesInVaricellaAndZosterFailClosed(final DoseStatus status)
    {
        final VaccineComponent vaccineComponent = vaccineComponent();
        final Vaccine vaccine = vaccine(vaccineComponent, true);
        final TargetDose varicellaDose = targetDose("dose-121", "VARICELLA_2_DOSE_SERIES", "VARICELLA", vaccine, vaccineComponent);
        final TargetDose zosterDose = targetDose("dose-121", "ZOSTER_2_DOSE_SERIES", "ZOSTER", vaccine, vaccineComponent);
        varicellaDose.setStatus(status);
        zosterDose.setStatus(status);

        try (final KieSession session = newPrimaryProcessSession(List.of(varicellaDose, zosterDose)))
        {
            session.startProcess(PRIMARY_PROCESS);
            session.fireAllRules();

            assertThat(validationState(session).getValidationStatus()).isEqualTo(ValidationStatus.FAILED);
            assertThat(facts(session, ICEFactTypeFinding.class)).extracting(ICEFactTypeFinding::getIceResultFinding)
                    .contains(FINDING_CODE);
            assertNotContinued(session);
            assertThat(facts(session, ValidationResult.class)).isEmpty();
            assertControlledErrorResponse(session);
        }
    }

    @Test
    void twoSeriesInTheSameVaccineGroupPass()
    {
        final TargetDose firstVaricellaDose = targetDose("dose-121", "VARICELLA_1_DOSE_SERIES", "VARICELLA", true);
        final TargetDose secondVaricellaDose = targetDose("dose-121", "VARICELLA_2_DOSE_SERIES", "VARICELLA", true);

        try (final KieSession session = newSession(List.of(firstVaricellaDose, secondVaricellaDose)))
        {
            executeValidationAndDownstreamGroups(session);

            assertPassedAndContinued(session);
        }
    }

    @Test
    void unflaggedVaccineIsIgnoredByMultiGroupValidation()
    {
        final TargetDose varicellaDose = targetDose("dose-single-group", "VARICELLA_2_DOSE_SERIES", "VARICELLA", false);
        final TargetDose zosterDose = targetDose("dose-single-group", "ZOSTER_2_DOSE_SERIES", "ZOSTER", false);

        try (final KieSession session = newSession(List.of(varicellaDose, zosterDose)))
        {
            executeValidationAndDownstreamGroups(session);

            assertPassedAndContinued(session);
            assertThat(facts(session, ICEFactTypeFinding.class)).extracting(ICEFactTypeFinding::getIceResultFinding)
                    .doesNotContain(FINDING_CODE);
        }
    }
}
