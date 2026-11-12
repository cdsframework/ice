package org.cdsframework.ice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.ice.config.CdsEngineProperties;
import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.supportingdata.LocallyCodedVaccineGroupItem;
import org.cdsframework.ice.supportingdata.SupportedVaccineGroups;
import org.drools.kiesession.rulebase.SessionsAwareKnowledgeBase;
import org.drools.model.codegen.ExecutableModelProject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.opencds.vmr.v1_0.internal.Demographics;
import org.opencds.vmr.v1_0.internal.EvalTime;
import org.opencds.vmr.v1_0.internal.EvaluatedPerson;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.internal.concepts.ImmunizationConcept;
import org.opencds.vmr.v1_0.internal.datatypes.BL;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { IceProperties.class, CdsEngineProperties.class },
                      initializers = ConfigDataApplicationContextInitializer.class)
@TestPropertySource(properties = "spring.config.import=classpath:application.yml,"
        + "classpath:data/knowledgeCommon/org.cdsframework.ice/ice-supporting-data/iceSupportingData.yml,"
        + "classpath:data/knowledgeModule/org.nyc.cir.ice/ice-supporting-data/iceSupportingData.yml")
@EnableConfigurationProperties({ IceProperties.class, CdsEngineProperties.class })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HistoryEvaluationClinicalRegressionTest
{
    private record Administration(String cvx,
                                  LocalDate date)
    {
    }

    private record Scenario(String name,
                            LocalDate birth,
                            List<Administration> administrations,
                            Boolean override,
                            boolean immune)
    {
    }

    private record DoseResult(String administration,
                              String group,
                              String series,
                              int number,
                              DoseStatus status,
                              List<String> validReasons,
                              List<String> invalidReasons,
                              List<String> acceptedReasons,
                              List<String> ignoredReasons)
    {
    }

    private record SeriesResult(String group,
                                String series,
                                String vaccine,
                                RecommendationStatus status,
                                LocalDate earliest,
                                LocalDate recommended,
                                LocalDate overdue,
                                List<String> reasons)
    {
    }

    private record ClinicalResult(List<DoseResult> doses,
                                  List<SeriesResult> selectedSeries)
    {
    }

    private static final String COMMON_PACKAGE = "knowledgeCommon.org.cdsframework.ice";
    private static final String ROUTING_RULE = "HistoryEvaluation: Route candidate doses before series evaluation";
    private static final String VARICELLA = "VACCINE_GROUP_CONCEPT.VARICELLA";
    private static final LocalDate EVALUATION_DATE = LocalDate.of(2026, 1, 1);
    private static final LocalDate ADMINISTRATION_DATE = LocalDate.of(2020, 1, 1);

    private static EvaluatedPerson person(final LocalDate birth)
    {
        final EvaluatedPerson person = new EvaluatedPerson();
        person.setId("test-patient");
        final Demographics demographics = new Demographics();
        demographics.setBirthTime(birth);
        final CD gender = new CD();
        gender.setCode("M");
        demographics.setGender(gender);
        person.setDemographics(demographics);
        return person;
    }

    private static Stream<Scenario> clinicalScenarios()
    {
        final LocalDate adult = LocalDate.of(1960, 1, 1);
        final LocalDate child = LocalDate.of(2018, 1, 1);
        return Stream.of(new Scenario("MMRV", child, List.of(new Administration("94", ADMINISTRATION_DATE)), null, false),
                new Scenario("MMRV at 27-day live interval", child, List.of(new Administration("03", ADMINISTRATION_DATE),
                        new Administration("94", ADMINISTRATION_DATE.plusDays(27))), null, false),
                new Scenario("MMRV at 28-day live interval", child, List.of(new Administration("03", ADMINISTRATION_DATE),
                        new Administration("94", ADMINISTRATION_DATE.plusDays(28))), null, false), new Scenario("Twinrix", adult,
                        List.of(new Administration("104", ADMINISTRATION_DATE),
                                new Administration("104", ADMINISTRATION_DATE.plusMonths(1))), null, false),
                new Scenario("Pediarix", LocalDate.of(2019, 11, 1), List.of(new Administration("110", ADMINISTRATION_DATE),
                        new Administration("110", ADMINISTRATION_DATE.plusMonths(2))), null, false),
                // H1N1 (priority 10) now finishes history evaluation before seasonal influenza (priority 0).
                new Scenario("H1N1 and seasonal influenza", LocalDate.of(2007, 1, 1),
                        List.of(new Administration("127", LocalDate.of(2009, 11, 2)),
                                new Administration("141", LocalDate.of(2009, 11, 2)),
                                new Administration("141", LocalDate.of(2009, 12, 1))), null, false),
                new Scenario("CVX 121", adult, List.of(new Administration("121", ADMINISTRATION_DATE)), null, false),
                new Scenario("CVX 121 and 187 same day", adult,
                        List.of(new Administration("121", ADMINISTRATION_DATE), new Administration("187", ADMINISTRATION_DATE)),
                        null, false), new Scenario("CVX 121 and prior 187", adult,
                        List.of(new Administration("121", ADMINISTRATION_DATE),
                                new Administration("187", ADMINISTRATION_DATE.minusMonths(2))), null, false),
                new Scenario("CVX 121 valid override", adult, List.of(new Administration("121", ADMINISTRATION_DATE)), true, false),
                new Scenario("CVX 121 invalid override", adult, List.of(new Administration("121", ADMINISTRATION_DATE)), false,
                        false),
                new Scenario("CVX 121 immunity", adult, List.of(new Administration("121", ADMINISTRATION_DATE)), null, true));
    }

    private final IceProperties iceProperties;
    private final CdsEngineProperties cdsEngineProperties;
    private KieBase currentRules;
    private KieBase baselineRules;
    private KieBase retainedVaricellaRules;

    @Autowired
    HistoryEvaluationClinicalRegressionTest(final IceProperties iceProperties, final CdsEngineProperties cdsEngineProperties)
    {
        this.iceProperties = iceProperties;
        this.cdsEngineProperties = cdsEngineProperties;
    }

    @BeforeAll
    void loadCompleteRulesAndCleanupBaseline() throws IOException
    {
        // Separate bases retain every clinical rule and the real PrimaryProcess BPMN.
        currentRules = KieServices.get().getKieClasspathContainer().newKieBase(null);
        baselineRules = KieServices.get().getKieClasspathContainer().newKieBase(null);
        retainedVaricellaRules = KieServices.get().getKieClasspathContainer().newKieBase(null);
        // Exercise the foundational routing policy without changing the deployed Varicella rule.
        retainedVaricellaRules.removeRule("knowledgeModule.org.nyc.cir.ice", "Varicella: Remove all CVX 121 Target Doses");
        final String baseline;
        try (final InputStream resource = getClass().getResourceAsStream("/history-evaluation-before-priority.drl"))
        {
            assertThat(resource).isNotNull();
            baseline = new String(resource.readAllBytes(), StandardCharsets.UTF_8);
        }
        final List<String> replacedRules = Pattern.compile("(?m)^rule \"([^\"]+)\"")
                .matcher(baseline)
                .results()
                .map((final MatchResult match) -> match.group(1))
                .toList();
        assertThat(replacedRules).hasSize(8);
        baselineRules.removeRule(COMMON_PACKAGE, ROUTING_RULE);
        replacedRules.forEach((final String name) -> baselineRules.removeRule(COMMON_PACKAGE, name));
        final KieBase originalHistoryRules =
                new KieHelper().addContent(baseline, ResourceType.DRL).build(ExecutableModelProject.class);
        ((SessionsAwareKnowledgeBase) baselineRules).addPackages(originalHistoryRules.getKiePackages());
        assertThat(currentRules.getRule(COMMON_PACKAGE, ROUTING_RULE)).isNotNull();
        assertThat(baselineRules.getRule(COMMON_PACKAGE, ROUTING_RULE)).isNull();
        assertThat(currentRules.getProcess("PrimaryProcess")).isNotNull();
        assertThat(baselineRules.getProcess("PrimaryProcess")).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("clinicalScenarios")
    void completeClinicalResultsMatchCleanupCommit(final Scenario scenario) throws InconsistentConfigurationException
    {
        final ClinicalResult baseline = evaluate(baselineRules, scenario, true);
        final ClinicalResult current = evaluate(currentRules, scenario, false);
        assertThat(current.doses()).isNotEmpty();
        assertThat(current.selectedSeries()).isNotEmpty();
        assertThat(current).as(scenario.name()).isEqualTo(baseline);
        if (scenario.name().startsWith("CVX 121"))
            assertThat(current.doses()).filteredOn((final DoseResult dose) -> dose.administration().equals("dose-0"))
                    .extracting(DoseResult::group)
                    .containsOnly("VACCINE_GROUP_CONCEPT.ZOSTER");
    }

    @Test
    void retainedVaricellaClaimReachesSeriesSelectionAndForecast() throws InconsistentConfigurationException
    {
        final Scenario scenario = new Scenario("Retained Varicella and same-day recombinant Zoster", LocalDate.of(1960, 1, 1),
                List.of(new Administration("121", ADMINISTRATION_DATE), new Administration("187", ADMINISTRATION_DATE)), null,
                false);
        final ClinicalResult result = evaluate(retainedVaricellaRules, scenario, false);
        assertThat(result.doses()).filteredOn((final DoseResult dose) -> "dose-0".equals(dose.administration()))
                .isNotEmpty()
                .extracting(DoseResult::group)
                .containsOnly(VARICELLA);
        assertThat(result.doses()).filteredOn((final DoseResult dose) -> "VACCINE_GROUP_CONCEPT.ZOSTER".equals(dose.group()))
                .singleElement()
                .satisfies((final DoseResult dose) ->
                {
                    assertThat(dose.administration()).isEqualTo("dose-1");
                    assertThat(dose.status()).isEqualTo(DoseStatus.VALID);
                    assertThat(dose.number()).isEqualTo(1);
                    assertThat(dose.invalidReasons()).isEmpty();
                });
        assertThat(result.selectedSeries()).filteredOn(
                        (final SeriesResult series) -> "VACCINE_GROUP_CONCEPT.ZOSTER".equals(series.group()))
                .singleElement()
                .satisfies((final SeriesResult series) ->
                {
                    assertThat(series.series()).isEqualTo("SUPPORTED_SERIES.ZOSTER_2_DOSE_SERIES");
                    assertThat(series.status()).isEqualTo(RecommendationStatus.RECOMMENDED);
                    assertThat(series.vaccine()).isEqualTo("VACCINE_CVX_187");
                    assertThat(series.recommended()).isEqualTo(ADMINISTRATION_DATE.plusDays(56));
                    assertThat(series.earliest()).isEqualTo(ADMINISTRATION_DATE.plusDays(28));
                });
    }

    @Test
    void missingVaccineGroupProducesConfigurationError() throws InconsistentConfigurationException
    {
        final Schedule schedule = newSchedule();
        final EvaluatedPerson person = person(LocalDate.of(1960, 1, 1));
        final SeriesRules rules = SeriesRules.constructDeepCopyOfSeriesRulesObject(schedule.getAllSeries().getFirst());
        final TargetSeries series = new TargetSeries(rules, schedule, person);
        series.setSeriesRules(new SeriesRules("missing-group", new CdsConcept("UNKNOWN_VACCINE_GROUP")));
        assertThatIllegalArgumentException().isThrownBy(series::getVaccineGroupPriority)
                .withMessageContaining("UNKNOWN_VACCINE_GROUP");
    }

    private Schedule newSchedule() throws InconsistentConfigurationException
    {
        final SupportingDataService supportingData = new SupportingDataService(cdsEngineProperties, iceProperties);
        return new Schedule("clinical-regression", supportingData.getBaseKnowledgeBaseId(),
                List.of(supportingData.getKmIdFromKnowledgeBaseUrl(
                        "https://terminology.cdsframework.org/PlanDefinition/ice-forecast|1.0.0")), supportingData,
                IceProperties.SupplementalTextMode.LEGACY);
    }

    private ClinicalResult evaluate(final KieBase rules, final Scenario scenario, final boolean baseline)
            throws InconsistentConfigurationException
    {
        final Schedule schedule = spy(newSchedule());
        if (baseline)
        {
            // This is the only supporting-data priority changed since cleanup commit 0ce93cfc.
            final SupportedVaccineGroups groups = spy(schedule.getSupportedVaccineGroups());
            final LocallyCodedVaccineGroupItem varicella = spy(groups.getVaccineGroupItem(VARICELLA));
            doReturn(0).when(varicella).getPriority();
            doReturn(varicella).when(groups).getVaccineGroupItem(VARICELLA);
            doReturn(groups).when(schedule).getSupportedVaccineGroups();
        }
        try (final KieSession session = rules.newKieSession())
        {
            session.getEnvironment().set("org.jbpm.rule.task.waitstate", true);
            final Map<String, Object> globals = new HashMap<>();
            globals.put("schedule", schedule);
            globals.put("evalTime", EVALUATION_DATE);
            globals.put("clientLanguage", "en");
            globals.put("clientTimeZoneOffset", "+00:00");
            globals.put("iceVersion", "test");
            globals.put("namedObjects", new HashMap<String, Object>());
            globals.put("assertions", new HashSet<String>());
            globals.put("doseOverrideFeatureEnabled", scenario.override() != null);
            globals.put("outputEarliestOverdueDates", true);
            globals.put("enableUnsupportedVaccinesGroup", false);
            globals.put("outputNumberOfDosesRemaining", false);
            globals.put("outputSeriesInformationForEvaluations", true);
            globals.put("outputSeriesInformationForRecommendations", true);
            globals.forEach(session::setGlobal);
            session.insert(person(scenario.birth()));
            final EvalTime evalTime = new EvalTime();
            evalTime.setEvalTimeValue(EVALUATION_DATE);
            session.insert(evalTime);
            IntStream.range(0, scenario.administrations().size()).forEach((final int index) ->
            {
                final Administration administration = scenario.administrations().get(index);
                final String id = "dose-" + index;
                final SubstanceAdministrationEvent event = new SubstanceAdministrationEvent();
                event.setId(id);
                final IVLDate interval = new IVLDate();
                interval.setLow(administration.date());
                interval.setHigh(administration.date());
                event.setAdministrationTimeInterval(interval);
                if (index == 0 && scenario.override() != null)
                {
                    final BL validity = new BL();
                    validity.setValue(scenario.override());
                    event.setIsValid(validity);
                }
                final Vaccine vaccine = schedule.getICESupportingDataConfiguration()
                        .getSupportedVaccines()
                        .getVaccineItem("SUPPORTED_VACCINES." + administration.cvx())
                        .getVaccine();
                final ImmunizationConcept concept = new ImmunizationConcept();
                concept.setId("concept-" + index);
                concept.setConceptTargetId(id);
                concept.setOpenCdsConceptCode(vaccine.getCdsConceptName());
                session.insert(event);
                session.insert(concept);
                if (index == 0 && scenario.immune())
                    vaccine.getAllDiseasesTargetedForImmunity()
                            .forEach((final String disease) -> session.insert(
                                    new DiseaseImmunity(disease, ADMINISTRATION_DATE.minusYears(10),
                                            "EVALUATION_REASON_CONCEPT.DISEASE_DOCUMENTED",
                                            "RECOMMENDATION_REASON_CONCEPT.DISEASE_DOCUMENTED", "immune")));
            });
            session.startProcess("PrimaryProcess");
            assertThat(session.fireAllRules(10000)).isLessThan(10000);
            assertThat(session.getProcessInstances()).isEmpty();
            assertThat(HistoryEvaluationValidationState.hasValidationStatus(session.getObjects(),
                    HistoryEvaluationValidationState.ValidationStatus.PASSED)).isTrue();
            final List<TargetSeries> selected = session.getObjects(TargetSeries.class::isInstance)
                    .stream()
                    .map(TargetSeries.class::cast)
                    .filter(TargetSeries::isSelectedSeries)
                    .toList();
            final List<DoseResult> doses = selected.stream()
                    .flatMap((final TargetSeries series) -> series.getTargetDoses()
                            .stream()
                            .map((final TargetDose dose) -> new DoseResult(dose.getDoseId(), series.getVaccineGroup(),
                                    series.getSeriesName(), dose.getDoseNumberInSeries(), dose.getStatus(),
                                    dose.getValidReasons().stream().sorted().toList(),
                                    dose.getInvalidReasons().stream().sorted().toList(),
                                    dose.getAcceptedReasons().stream().sorted().toList(),
                                    dose.getNotEvaluatedReasons().stream().sorted().toList())))
                    .sorted(Comparator.comparing(DoseResult::toString))
                    .toList();
            final List<SeriesResult> forecasts = selected.stream().map((final TargetSeries series) ->
            {
                final Vaccine vaccine = series.getRecommendationVaccine();
                return new SeriesResult(series.getVaccineGroup(), series.getSeriesName(),
                        vaccine == null ? null : vaccine.getCdsConceptName(), series.getRecommendationStatus(),
                        series.getFinalEarliestDate(), series.getFinalRecommendationDate(), series.getFinalOverdueDate(),
                        series.getFinalRecommendations()
                                .stream()
                                .map(Recommendation::getRecommendationReason)
                                .sorted(Comparator.nullsFirst(Comparator.naturalOrder()))
                                .toList());
            }).sorted(Comparator.comparing(SeriesResult::toString)).toList();
            return new ClinicalResult(doses, forecasts);
        }
    }
}
