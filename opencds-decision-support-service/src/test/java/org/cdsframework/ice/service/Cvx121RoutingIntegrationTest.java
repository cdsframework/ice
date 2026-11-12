package org.cdsframework.ice.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.cdsframework.ice.RuleTestResources;
import org.cdsframework.ice.config.CdsEngineProperties;
import org.cdsframework.ice.config.IceProperties;
import org.drools.drl.parser.lang.dsl.DSLTokenizedMappingFile;
import org.drools.drl.parser.lang.dsl.DefaultExpander;
import org.drools.model.codegen.ExecutableModelProject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.api.KieBase;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.opencds.vmr.v1_0.internal.Demographics;
import org.opencds.vmr.v1_0.internal.EvaluatedPerson;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.internal.datatypes.BL;
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
class Cvx121RoutingIntegrationTest
{
    private enum Scenario
    {
        CVX_121_ALONE,
        MIXED_HISTORY,
        SAME_DAY,
        IMMUNITY,
        VALID_OVERRIDE,
        INVALID_OVERRIDE
    }

    private static final Path RULE_ROOT = RuleTestResources.sourceResources().resolve("drools");
    private static final String COMMON = "knowledgeCommon/org.cdsframework.ice/org.cdsframework^ICE^1.0.0";
    private static final String MODULE = "knowledgeModule/org.nyc.cir.ice/org.nyc.cir^ICE^1.0.0^Evaluation^%s.dslr";
    private static final String VARICELLA = "VACCINE_GROUP_CONCEPT.VARICELLA";
    private static final String ZOSTER = "VACCINE_GROUP_CONCEPT.ZOSTER";
    private static final String HEP_A = "VACCINE_GROUP_CONCEPT.HEP_A";
    private static final String ZOSTER_SUPPRESSION_RULE =
            "Zoster: Remove CVX 121 Target Doses from Zoster when CVX 121 is in Varicella";
    private static final LocalDate ADMINISTRATION_DATE = LocalDate.of(2020, 1, 1);

    private static Stream<Arguments> scenarios()
    {
        return Stream.of(Scenario.values())
                .flatMap((final Scenario scenario) -> Stream.of(true, false)
                        .flatMap((final Boolean varicellaRulesFirst) -> Stream.of(true, false)
                                .map((final Boolean varicellaFactsFirst) -> Arguments.of(scenario, varicellaRulesFirst,
                                        varicellaFactsFirst))));
    }

    private static Stream<Arguments> blankAdministrationIds()
    {
        return Stream.of(null, "", " ", "\t")
                .flatMap((final String administrationId) -> Stream.of(true, false)
                        .map((final Boolean varicellaRulesFirst) -> Arguments.of(administrationId, varicellaRulesFirst)));
    }

    private static SubstanceAdministrationEvent administration(final String id, final LocalDate date)
    {
        final SubstanceAdministrationEvent event = new SubstanceAdministrationEvent();
        event.setId(id);
        final IVLDate interval = new IVLDate();
        interval.setLow(date);
        interval.setHigh(date);
        event.setAdministrationTimeInterval(interval);
        return event;
    }

    private final Schedule schedule;
    private Map<Boolean, KieBase> ruleBases;
    private Map<Boolean, KieBase> retainedVaricellaRuleBases;

    @Autowired
    Cvx121RoutingIntegrationTest(final IceProperties iceProperties, final CdsEngineProperties cdsEngineProperties)
            throws InconsistentConfigurationException
    {
        final SupportingDataService supportingData = new SupportingDataService(cdsEngineProperties, iceProperties);
        schedule = new Schedule("test-schedule", supportingData.getBaseKnowledgeBaseId(),
                List.of(supportingData.getKmIdFromKnowledgeBaseUrl(
                        "https://terminology.cdsframework.org/PlanDefinition/ice-forecast|1.0.0")), supportingData,
                IceProperties.SupplementalTextMode.LEGACY);
    }

    @BeforeAll
    void compileRealEvaluationRulesInBothResourceOrders() throws IOException
    {
        final DSLTokenizedMappingFile mapping = new DSLTokenizedMappingFile();
        assertThat(
                mapping.parseAndLoad(new StringReader(Files.readString(RULE_ROOT.resolve("%s.dsl".formatted(COMMON)))))).isTrue();
        final DefaultExpander expander = new DefaultExpander();
        expander.addDSLMapping(mapping.getMapping());
        final String varicella = expander.expand(Files.readString(RULE_ROOT.resolve(MODULE.formatted("Varicella"))));
        final String zoster = expander.expand(Files.readString(RULE_ROOT.resolve(MODULE.formatted("Zoster"))));
        assertThat(expander.getErrors()).isEmpty();
        final String common = Files.readString(RULE_ROOT.resolve("%s.drl".formatted(COMMON)));
        final String validation = Files.readString(RULE_ROOT.resolve("%s^HistoryEvaluationValidation.drl".formatted(COMMON)));
        ruleBases = Stream.of(true, false).collect(Collectors.toMap(Function.identity(), (final Boolean varicellaFirst) ->
        {
            final KieHelper helper = new KieHelper();
            helper.addContent(common, ResourceType.DRL);
            helper.addContent(varicellaFirst ? varicella : zoster, ResourceType.DRL);
            helper.addContent(varicellaFirst ? zoster : varicella, ResourceType.DRL);
            helper.addContent(validation, ResourceType.DRL);
            return helper.build(ExecutableModelProject.class);
        }));
        retainedVaricellaRuleBases =
                Stream.of(true, false).collect(Collectors.toMap(Function.identity(), (final Boolean varicellaFirst) ->
                {
                    final KieHelper helper = new KieHelper();
                    helper.addContent(common, ResourceType.DRL);
                    helper.addContent(varicellaFirst ? varicella : zoster, ResourceType.DRL);
                    helper.addContent(varicellaFirst ? zoster : varicella, ResourceType.DRL);
                    helper.addContent(validation, ResourceType.DRL);
                    final KieBase ruleBase = helper.build(ExecutableModelProject.class);
                    // Model a future routing decision retaining CVX 121 in Varicella without changing today's policy.
                    ruleBase.removeRule("knowledgeModule.org.nyc.cir.ice", "Varicella: Remove all CVX 121 Target Doses");
                    return ruleBase;
                }));
    }

    @ParameterizedTest
    @MethodSource("scenarios")
    void cvx121RemainsOnlyInZosterRegardlessOfResourceAndFactOrder(final Scenario scenario, final boolean varicellaRulesFirst,
            final boolean varicellaFactsFirst)
    {
        evaluateRouting(scenario, varicellaRulesFirst, varicellaFactsFirst, false);
    }

    @ParameterizedTest
    @MethodSource("scenarios")
    void retainedVaricellaAdministrationSuppressesOnlyItsZosterCopies(final Scenario scenario, final boolean varicellaRulesFirst,
            final boolean varicellaFactsFirst)
    {
        evaluateRouting(scenario, varicellaRulesFirst, varicellaFactsFirst, true);
    }

    private void evaluateRouting(final Scenario scenario, final boolean varicellaRulesFirst, final boolean varicellaFactsFirst,
            final boolean retainVaricella)
    {
        final EvaluatedPerson person = new EvaluatedPerson();
        person.setId("test-patient");
        final Demographics demographics = new Demographics();
        demographics.setBirthTime(LocalDate.of(1960, 1, 1));
        person.setDemographics(demographics);
        final Vaccine cvx121 = schedule.getICESupportingDataConfiguration()
                .getSupportedVaccines()
                .getVaccineItem("SUPPORTED_VACCINES.121")
                .getVaccine();
        assertThat(cvx121.isValidInMultipleVaccineGroups()).isTrue();
        final SubstanceAdministrationEvent cvx121Event = administration("cvx-121-dose", ADMINISTRATION_DATE);
        final SubstanceAdministrationEvent varicellaEvent = administration("varicella-dose",
                scenario == Scenario.SAME_DAY ? ADMINISTRATION_DATE : ADMINISTRATION_DATE.minusYears(1));
        if (scenario == Scenario.VALID_OVERRIDE || scenario == Scenario.INVALID_OVERRIDE)
        {
            final BL validity = new BL();
            validity.setValue(scenario == Scenario.VALID_OVERRIDE);
            cvx121Event.setIsValid(validity);
        }

        final Map<Boolean, KieBase> bases = retainVaricella ? retainedVaricellaRuleBases : ruleBases;
        try (final KieSession session = bases.get(varicellaRulesFirst).newKieSession())
        {
            session.setGlobal("schedule", schedule);
            session.setGlobal("evalTime", LocalDate.of(2026, 1, 1));
            session.setGlobal("focalPersonId", person.getId());
            session.setGlobal("namedObjects", new HashMap<String, Object>());
            final List<String> fired = new ArrayList<>();
            final List<String> suppressedDoseIds = new ArrayList<>();
            session.insert(person);
            session.insert(cvx121Event);
            if (scenario == Scenario.VALID_OVERRIDE || scenario == Scenario.INVALID_OVERRIDE)
                session.insert(new ICEFactTypeFinding("DOSE_OVERRIDE_FEATURE_ENABLED"));
            if (scenario == Scenario.IMMUNITY)
                cvx121.getAllDiseasesTargetedForImmunity()
                        .forEach((final String disease) -> session.insert(
                                new DiseaseImmunity(disease, ADMINISTRATION_DATE.minusYears(10), "immunity", "accepted",
                                        "immune")));

            final TargetDoseInitializationTracker tracker = new TargetDoseInitializationTracker();
            final List<String> groups = varicellaFactsFirst ? List.of(VARICELLA, HEP_A, ZOSTER) : List.of(ZOSTER, HEP_A, VARICELLA);
            groups.forEach((final String group) -> schedule.getAllSeries()
                    .stream()
                    .filter((final SeriesRules rules) -> group.equals(rules.getVaccineGroup()))
                    .forEach((final SeriesRules rules) ->
                    {
                        final TargetSeries series = new TargetSeries(rules, schedule, person);
                        session.insert(series);
                        if (!HEP_A.equals(group))
                            tracker.addTargetDoseInitialization(cvx121, cvx121Event, series, schedule).forEach(session::insert);
                        if (VARICELLA.equals(group) && (scenario == Scenario.MIXED_HISTORY || scenario == Scenario.SAME_DAY))
                        {
                            final Vaccine varicella = schedule.getICESupportingDataConfiguration()
                                    .getSupportedVaccines()
                                    .getVaccineItem("SUPPORTED_VACCINES.21")
                                    .getVaccine();
                            tracker.addTargetDoseInitialization(varicella, varicellaEvent, series, schedule)
                                    .forEach(session::insert);
                        }
                        if (ZOSTER.equals(group) && retainVaricella && scenario != Scenario.CVX_121_ALONE)
                        {
                            final Vaccine zoster = schedule.getICESupportingDataConfiguration()
                                    .getSupportedVaccines()
                                    .getVaccineItem("SUPPORTED_VACCINES.187")
                                    .getVaccine();
                            // Unrelated older or same-day Zoster history must remain after routing the claimed dose.
                            tracker.addTargetDoseInitialization(zoster, administration("unrelated-zoster-dose",
                                            scenario == Scenario.SAME_DAY ? ADMINISTRATION_DATE : ADMINISTRATION_DATE.minusYears(2)),
                                    series, schedule).forEach(session::insert);
                            tracker.addTargetDoseInitialization(cvx121,
                                            administration("unrelated-cvx-121-dose", ADMINISTRATION_DATE), series, schedule)
                                    .forEach(session::insert);
                        }
                        if (HEP_A.equals(group) && scenario == Scenario.IMMUNITY)
                        {
                            // Re-enter custom evaluation after a CVX 121 copy becomes terminal through immunity.
                            final Vaccine hepA = schedule.getICESupportingDataConfiguration()
                                    .getSupportedVaccines()
                                    .getVaccineItem("SUPPORTED_VACCINES.83")
                                    .getVaccine();
                            tracker.addTargetDoseInitialization(hepA, administration("hep-a-dose", ADMINISTRATION_DATE), series,
                                    schedule).forEach(session::insert);
                        }
                    }));

            final String retainedGroup = retainVaricella ? VARICELLA : ZOSTER;
            final List<String> expectedDoseIds = session.getObjects(TargetDose.class::isInstance)
                    .stream()
                    .map(TargetDose.class::cast)
                    .filter((final TargetDose dose) -> retainedGroup.equals(dose.getAssociatedVaccineGroup()))
                    .filter((final TargetDose dose) -> "cvx-121-dose".equals(dose.getDoseId()))
                    .map(TargetDose::getUniqueId)
                    .toList();
            assertThat(expectedDoseIds).isNotEmpty();
            final List<String> claimedZosterDoseIds = session.getObjects(TargetDose.class::isInstance)
                    .stream()
                    .map(TargetDose.class::cast)
                    .filter((final TargetDose dose) -> ZOSTER.equals(dose.getAssociatedVaccineGroup()))
                    .filter((final TargetDose dose) -> "cvx-121-dose".equals(dose.getDoseId()))
                    .map(TargetDose::getUniqueId)
                    .toList();
            assertThat(claimedZosterDoseIds).isNotEmpty();
            final Map<String, Integer> initialShotNumbers = session.getObjects(TargetDose.class::isInstance)
                    .stream()
                    .map(TargetDose.class::cast)
                    .collect(Collectors.toMap(TargetDose::getUniqueId, TargetDose::getAdministeredShotNumberInSeries));
            session.addEventListener(new DefaultAgendaEventListener()
            {
                @Override
                public void beforeMatchFired(final BeforeMatchFiredEvent event)
                {
                    if (retainVaricella)
                        event.getMatch()
                                .getObjects()
                                .stream()
                                .filter(TargetDose.class::isInstance)
                                .map(TargetDose.class::cast)
                                .filter((final TargetDose dose) -> ZOSTER.equals(dose.getAssociatedVaccineGroup()))
                                .filter((final TargetDose dose) -> "cvx-121-dose".equals(dose.getDoseId()))
                                .forEach((final TargetDose dose) ->
                                {
                                    assertThat(event.getMatch().getRule().getName()).as("Claimed Zoster dose bound by a fired rule")
                                            .isEqualTo(ZOSTER_SUPPRESSION_RULE);
                                    assertThat(dose.getStatus()).isEqualTo(DoseStatus.EVALUATION_NOT_STARTED);
                                    assertThat(dose.hasBeenEvaluated()).isFalse();
                                    assertThat(dose.isDoseStatusOverridden()).isFalse();
                                    assertThat(dose.getAdministeredShotNumberInSeries()).isEqualTo(
                                            initialShotNumbers.get(dose.getUniqueId()));
                                    assertThat(dose.getAssociatedTargetSeries().isHistoryEvaluationInitiated()).isFalse();
                                    suppressedDoseIds.add(dose.getUniqueId());
                                });
                }

                @Override
                public void afterMatchFired(final AfterMatchFiredEvent event)
                {
                    fired.add(event.getMatch().getRule().getName());
                }
            });

            session.getAgenda().getAgendaGroup("HistoryEvaluation").setFocus();
            assertThat(session.fireAllRules(1000)).isLessThan(1000);
            if (retainVaricella)
                assertThat(suppressedDoseIds).containsExactlyInAnyOrderElementsOf(claimedZosterDoseIds);

            final List<TargetDose> retained =
                    session.getObjects(TargetDose.class::isInstance).stream().map(TargetDose.class::cast).toList();
            assertThat(retained).as("Fired rules: %s", fired)
                    .filteredOn((final TargetDose dose) -> "cvx-121-dose".equals(dose.getDoseId()))
                    .extracting(TargetDose::getUniqueId)
                    .containsExactlyInAnyOrderElementsOf(expectedDoseIds);
            assertThat(retained).filteredOn((final TargetDose dose) -> "cvx-121-dose".equals(dose.getDoseId()))
                    .allSatisfy((final TargetDose dose) ->
                    {
                        assertThat(dose.getAssociatedVaccineGroup()).isEqualTo(retainedGroup);
                        assertThat(dose.getStatus()).isIn(DoseStatus.ACCEPTED, DoseStatus.VALID, DoseStatus.INVALID,
                                DoseStatus.NOT_EVALUATED);
                        if (scenario == Scenario.VALID_OVERRIDE || scenario == Scenario.INVALID_OVERRIDE)
                        {
                            assertThat(dose.isDoseStatusOverridden()).isTrue();
                            assertThat(dose.getStatus()).isEqualTo(
                                    scenario == Scenario.VALID_OVERRIDE ? DoseStatus.VALID : DoseStatus.INVALID);
                        }
                        if (scenario == Scenario.IMMUNITY)
                            assertThat(dose.getStatus()).isEqualTo(DoseStatus.ACCEPTED);
                    });
            if (scenario == Scenario.MIXED_HISTORY || scenario == Scenario.SAME_DAY)
                assertThat(retained).anySatisfy((final TargetDose dose) ->
                {
                    assertThat(dose.getDoseId()).isEqualTo("varicella-dose");
                    assertThat(dose.getAssociatedVaccineGroup()).isEqualTo(VARICELLA);
                });
            if (retainVaricella && scenario == Scenario.CVX_121_ALONE)
                assertThat(session.getObjects(TargetSeries.class::isInstance)
                        .stream()
                        .map(TargetSeries.class::cast)
                        .toList()).filteredOn((final TargetSeries series) -> ZOSTER.equals(series.getVaccineGroup()))
                        .isNotEmpty()
                        .allSatisfy((final TargetSeries series) ->
                        {
                            assertThat(series.getTargetDoses()).isEmpty();
                            assertThat(series.isHistoryEvaluationRoutingInitiated()).isTrue();
                            assertThat(series.isHistoryEvaluationInitiated()).isTrue();
                        });
            if (retainVaricella && scenario != Scenario.CVX_121_ALONE)
            {
                assertThat(retained).filteredOn((final TargetDose dose) -> "unrelated-zoster-dose".equals(dose.getDoseId()))
                        .isNotEmpty()
                        .allSatisfy((final TargetDose dose) ->
                        {
                            assertThat(dose.getAssociatedVaccineGroup()).isEqualTo(ZOSTER);
                            assertThat(dose.hasBeenEvaluated() || dose.getStatus() == DoseStatus.NOT_EVALUATED).isTrue();
                        });
                assertThat(retained).filteredOn((final TargetDose dose) -> "unrelated-cvx-121-dose".equals(dose.getDoseId()))
                        .isNotEmpty()
                        .allSatisfy((final TargetDose dose) -> assertThat(dose.getAssociatedVaccineGroup()).isEqualTo(ZOSTER));
            }

            session.getAgenda().getAgendaGroup("HistoryEvaluationValidation").setFocus();
            session.fireAllRules();
            assertThat(session.getObjects(HistoryEvaluationValidationState.class::isInstance)).singleElement()
                    .satisfies((final Object state) -> assertThat(
                            ((HistoryEvaluationValidationState) state).getValidationStatus()).isEqualTo(
                            HistoryEvaluationValidationState.ValidationStatus.PASSED));
        }
    }

    @ParameterizedTest
    @EnumSource(value = DoseStatus.class, names = { "ACCEPTED", "INVALID", "VALID", "NOT_EVALUATED" })
    void terminalVaricellaAssociationSuppressesTheSameAdministrationBeforeZosterHistoryStarts(final DoseStatus status)
    {
        final EvaluatedPerson person = new EvaluatedPerson();
        person.setId("test-patient");
        final Demographics demographics = new Demographics();
        demographics.setBirthTime(LocalDate.of(1960, 1, 1));
        person.setDemographics(demographics);
        final Vaccine vaccine = schedule.getICESupportingDataConfiguration()
                .getSupportedVaccines()
                .getVaccineItem("SUPPORTED_VACCINES.121")
                .getVaccine();
        final TargetDoseInitializationTracker tracker = new TargetDoseInitializationTracker();
        final List<TargetSeries> series = Stream.of(VARICELLA, ZOSTER).map((final String group) ->
        {
            final SeriesRules rules = schedule.getAllSeries()
                    .stream()
                    .filter((final SeriesRules candidate) -> group.equals(candidate.getVaccineGroup()))
                    .findFirst()
                    .orElseThrow();
            return new TargetSeries(rules, schedule, person);
        }).toList();
        final TargetSeries varicella = series.getFirst();
        final TargetSeries zoster = series.getLast();
        varicella.setHistoryEvaluationInitiated(true);
        final List<TargetDose> varicellaDoses =
                tracker.addTargetDoseInitialization(vaccine, administration("same-administration", ADMINISTRATION_DATE), varicella,
                        schedule);
        varicellaDoses.forEach((final TargetDose dose) -> dose.setStatus(status));
        final List<TargetDose> zosterDoses =
                tracker.addTargetDoseInitialization(vaccine, administration("same-administration", ADMINISTRATION_DATE), zoster,
                        schedule);
        final List<TargetDose> unrelated =
                tracker.addTargetDoseInitialization(vaccine, administration("another-administration", ADMINISTRATION_DATE), zoster,
                        schedule);
        unrelated.forEach((final TargetDose dose) -> dose.setStatus(DoseStatus.VALID));
        final Map<String, Integer> initialShotNumbers = zosterDoses.stream()
                .collect(Collectors.toMap(TargetDose::getUniqueId, TargetDose::getAdministeredShotNumberInSeries));

        try (final KieSession session = retainedVaricellaRuleBases.get(true).newKieSession())
        {
            session.setGlobal("schedule", schedule);
            session.setGlobal("evalTime", LocalDate.of(2026, 1, 1));
            series.forEach(session::insert);
            Stream.of(varicellaDoses, zosterDoses, unrelated).flatMap(List::stream).forEach(session::insert);
            session.getAgenda().getAgendaGroup("HistoryEvaluation^preEvaluationRouting").setFocus();
            session.fireAllRules();
            assertThat(session.getObjects(TargetDose.class::isInstance).stream().map(TargetDose.class::cast).toList()).containsAll(
                    zosterDoses);

            // Updating only the containing series must make the suppression rule eligible.
            zoster.setHistoryEvaluationRoutingInitiated(true);
            session.update(session.getFactHandle(zoster), zoster);
            session.getAgenda().getAgendaGroup("HistoryEvaluation^preEvaluationRouting").setFocus();
            session.fireAllRules();
            assertThat(session.getObjects(TargetDose.class::isInstance).stream().map(TargetDose.class::cast).toList()).containsAll(
                    varicellaDoses).containsAll(unrelated).doesNotContainAnyElementsOf(zosterDoses);
            assertThat(zoster.getTargetDoses()).containsExactlyInAnyOrderElementsOf(unrelated);
            assertThat(zoster.isHistoryEvaluationInitiated()).isFalse();
            assertThat(zosterDoses).allSatisfy((final TargetDose dose) ->
            {
                assertThat(dose.getStatus()).isEqualTo(DoseStatus.EVALUATION_NOT_STARTED);
                assertThat(dose.hasBeenEvaluated()).isFalse();
                assertThat(dose.isDoseStatusOverridden()).isFalse();
                assertThat(dose.getAdministeredShotNumberInSeries()).isEqualTo(initialShotNumbers.get(dose.getUniqueId()));
            });
        }
    }

    @ParameterizedTest
    @MethodSource("blankAdministrationIds")
    void unrelatedAdministrationsWithoutUsableIdsRemainInBothVaccineGroups(final String administrationId,
            final boolean varicellaRulesFirst)
    {
        final EvaluatedPerson person = new EvaluatedPerson();
        person.setId("test-patient");
        final Demographics demographics = new Demographics();
        demographics.setBirthTime(LocalDate.of(1960, 1, 1));
        person.setDemographics(demographics);
        final Vaccine vaccine = schedule.getICESupportingDataConfiguration()
                .getSupportedVaccines()
                .getVaccineItem("SUPPORTED_VACCINES.121")
                .getVaccine();
        final List<TargetSeries> series = Stream.of(VARICELLA, ZOSTER).map((final String group) ->
        {
            final SeriesRules rules = schedule.getAllSeries()
                    .stream()
                    .filter((final SeriesRules candidate) -> group.equals(candidate.getVaccineGroup()))
                    .findFirst()
                    .orElseThrow();
            return new TargetSeries(rules, schedule, person);
        }).toList();
        final TargetSeries varicella = series.getFirst();
        final TargetSeries zoster = series.getLast();
        final TargetDose varicellaDose =
                new TargetDose(vaccine, vaccine.getVaccineComponents().getFirst(), ADMINISTRATION_DATE, varicella,
                        administration(administrationId, ADMINISTRATION_DATE));
        final TargetDose zosterDose =
                new TargetDose(vaccine, vaccine.getVaccineComponents().getFirst(), ADMINISTRATION_DATE.minusYears(1), zoster,
                        administration(administrationId, ADMINISTRATION_DATE.minusYears(1)));
        varicella.getTargetDoses().add(varicellaDose);
        zoster.getTargetDoses().add(zosterDose);
        varicella.setHistoryEvaluationRoutingInitiated(true);
        zoster.setHistoryEvaluationRoutingInitiated(true);

        try (final KieSession session = retainedVaricellaRuleBases.get(varicellaRulesFirst).newKieSession())
        {
            session.setGlobal("schedule", schedule);
            session.setGlobal("evalTime", LocalDate.of(2026, 1, 1));
            series.forEach(session::insert);
            List.of(varicellaDose, zosterDose).forEach(session::insert);
            session.getAgenda().getAgendaGroup("HistoryEvaluation^preEvaluationRouting").setFocus();
            session.fireAllRules();

            assertThat(session.getObjects(TargetSeries.class::isInstance)
                    .stream()
                    .map(TargetSeries.class::cast)
                    .toList()).containsExactlyInAnyOrderElementsOf(series);
            assertThat(session.getObjects(TargetDose.class::isInstance)
                    .stream()
                    .map(TargetDose.class::cast)
                    .toList()).containsExactlyInAnyOrder(varicellaDose, zosterDose);
            assertThat(varicella.getTargetDoses()).containsExactly(varicellaDose);
            assertThat(zoster.getTargetDoses()).containsExactly(zosterDose);
            assertThat(List.of(varicellaDose, zosterDose)).allSatisfy(
                    (final TargetDose dose) -> assertThat(dose.getStatus()).isEqualTo(DoseStatus.EVALUATION_NOT_STARTED));
            assertThat(series).allSatisfy(
                    (final TargetSeries targetSeries) -> assertThat(targetSeries.isHistoryEvaluationInitiated()).isFalse());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void validationFailureCallsRecordMethodsDirectly(final boolean varicellaRulesFirst)
    {
        final EvaluatedPerson person = new EvaluatedPerson();
        person.setId("test-patient");
        final Demographics demographics = new Demographics();
        demographics.setBirthTime(LocalDate.of(1960, 1, 1));
        person.setDemographics(demographics);
        final Vaccine vaccine = schedule.getICESupportingDataConfiguration()
                .getSupportedVaccines()
                .getVaccineItem("SUPPORTED_VACCINES.121")
                .getVaccine();
        final SubstanceAdministrationEvent event = administration("duplicate-dose", ADMINISTRATION_DATE);

        try (final KieSession session = ruleBases.get(varicellaRulesFirst).newKieSession())
        {
            List.of(VARICELLA, ZOSTER).forEach((final String group) ->
            {
                final SeriesRules rules = schedule.getAllSeries()
                        .stream()
                        .filter((final SeriesRules candidate) -> group.equals(candidate.getVaccineGroup()))
                        .findFirst()
                        .orElseThrow();
                final TargetSeries series = new TargetSeries(rules, schedule, person);
                series.setHistoryEvaluationInitiated(true);
                final Vaccine copy = Vaccine.constructDeepCopyOfVaccineObject(vaccine);
                final TargetDose dose =
                        new TargetDose(copy, copy.getVaccineComponents().getFirst(), ADMINISTRATION_DATE, series, event);
                dose.setStatus(DoseStatus.VALID);
                session.insert(series);
                session.insert(dose);
            });

            session.getAgenda().getAgendaGroup("HistoryEvaluationValidation").setFocus();
            session.fireAllRules();
            assertThat(session.getObjects(ICEFactTypeFinding.class::isInstance)).hasSize(1);
            assertThat(session.getObjects(HistoryEvaluationValidationState.class::isInstance)).singleElement()
                    .satisfies((final Object state) ->
                    {
                        final HistoryEvaluationValidationState validationState = (HistoryEvaluationValidationState) state;
                        assertThat(validationState.getDiagnosticMessage()).contains("duplicate-dose", VARICELLA, ZOSTER);
                        assertThat(HistoryEvaluationValidationState.hasValidationStatus(session.getObjects(),
                                HistoryEvaluationValidationState.ValidationStatus.FAILED)).isTrue();
                        assertThat(HistoryEvaluationValidationState.hasValidationStatus(session.getObjects(),
                                HistoryEvaluationValidationState.ValidationStatus.PASSED)).isFalse();
                    });
        }
    }
}
