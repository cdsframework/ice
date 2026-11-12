package org.cdsframework.ice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.ice.RuleTestResources;
import org.drools.model.codegen.ExecutableModelProject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.internal.datatypes.BL;

class HistoryEvaluationPriorityTest
{
    private enum EntryPath
    {
        NON_SEASONAL,
        NON_SEASONAL_NOS,
        SEASONAL,
        SEASONAL_NOS,
        OVERRIDE;

        boolean seasonal()
        {
            return this == SEASONAL || this == SEASONAL_NOS;
        }

        boolean nos()
        {
            return this == NON_SEASONAL_NOS || this == SEASONAL_NOS;
        }
    }

    private static final LocalDate DATE = LocalDate.of(2020, 1, 1);
    private static final String ROUTING_RULE = "HistoryEvaluation: Route candidate doses before series evaluation";
    private static KieBase startupRules;

    @BeforeAll
    static void compileRealHistoryStartupRules() throws IOException
    {
        startupRules = compileHistoryStartupRules(readCommonRules());
    }

    private static Stream<Arguments> entryPaths()
    {
        return Stream.of(EntryPath.values())
                .flatMap((final EntryPath path) -> Stream.of(true, false)
                        .map((final Boolean highFirst) -> Arguments.of(path, highFirst)));
    }

    private static Stream<Arguments> emptySeries()
    {
        return Stream.of(true, false)
                .flatMap((final Boolean highSeasonal) -> Stream.of(true, false)
                        .flatMap((final Boolean lowSeasonal) -> Stream.of(true, false)
                                .map((final Boolean highFirst) -> Arguments.of(highSeasonal, lowSeasonal, highFirst))));
    }

    private static Stream<Arguments> regularEntryPaths()
    {
        return entryPaths().filter((final Arguments arguments) -> arguments.get()[0] != EntryPath.OVERRIDE);
    }

    private static String readCommonRules() throws IOException
    {
        return Files.readString(RuleTestResources.sourceResources()
                .resolve("drools/knowledgeCommon/org.cdsframework.ice/org.cdsframework^ICE^1.0.0.drl"));
    }

    private static KieBase compileHistoryStartupRules(final String source)
    {
        final String common = source.replace("\r\n", "\n");
        // Isolate selection from clinical evaluation so each next-dose decision can be observed directly.
        final List<String> rules = Pattern.compile("(?ms)^rule \"([^\"]+)\"\\s.*?^end\\s*$")
                .matcher(common)
                .results()
                .filter((final MatchResult match) -> match.group(1).startsWith("HistoryEvaluation.setNonSeasonalNext")
                        || match.group(1).startsWith("HistoryEvaluation.setNextSeasonal") || match.group(1)
                        .startsWith("HistoryEvaluation.evaluateNonSeasonalSeriesWhenNoTargetDoses") || match.group(1)
                        .startsWith("HistoryEvaluation.evaluateSeasonalSeriesWhenNoTargetDoses") || match.group(1)
                        .startsWith("HistoryEvaluation: If Dose Override Specified") || match.group(1).equals(ROUTING_RULE))
                .map(MatchResult::group)
                .toList();
        assertThat(rules).hasSize(8);
        return new KieHelper().addContent(
                        String.join("\n", common.substring(0, common.indexOf("\nrule \"")), String.join("\n", rules)), ResourceType.DRL)
                .build(ExecutableModelProject.class);
    }

    private static TargetSeries series(final String name, final int priority, final int seriesGroup, final boolean seasonal,
            final LocalDate seasonStart)
    {
        final TargetSeries series = mock(TargetSeries.class);
        final SeriesRules rules = mock(SeriesRules.class);
        when(rules.getSeriesGroup()).thenReturn(seriesGroup);
        when(series.getSeriesRules()).thenReturn(rules);
        when(series.getSeriesName()).thenReturn(name);
        when(series.getVaccineGroup()).thenReturn(name);
        when(series.getVaccineGroupPriority()).thenReturn(priority);
        when(series.targetSeasonExists()).thenReturn(seasonal);
        when(series.getSeasonStartDate()).thenReturn(seasonStart);

        final NavigableSet<TargetDose> doses = new TreeSet<>(Comparator.comparing(TargetDose::getUniqueId));
        when(series.getTargetDoses()).thenReturn(doses);
        when(series.getNumberOfShotsAdministeredInSeries()).thenAnswer(_ -> doses.size());

        when(series.isHistoryEvaluationInitiated()).thenCallRealMethod();
        doCallRealMethod().when(series).setHistoryEvaluationInitiated(anyBoolean());
        when(series.isHistoryEvaluationRoutingInitiated()).thenCallRealMethod();
        doCallRealMethod().when(series).setHistoryEvaluationRoutingInitiated(anyBoolean());

        return series;
    }

    private static TargetDose dose(final TargetSeries series, final String id, final LocalDate date, final boolean nos)
    {
        final VaccineComponent component = new VaccineComponent(new CdsConcept("VACCINE_CVX_121"), List.of("varicella"));
        component.setUnspecifiedFormulation(nos);
        final Vaccine vaccine = new Vaccine(new CdsConcept("VACCINE_CVX_121"), Collections.singletonList(component));
        final SubstanceAdministrationEvent event = new SubstanceAdministrationEvent();
        event.setId(id);
        final TargetDose dose = new TargetDose(vaccine, component, date, series, event);
        series.getTargetDoses().add(dose);
        return dose;
    }

    private static KieSession session(final List<String> started, final boolean highFirst, final TargetSeries high,
            final TargetSeries low, final TargetDose... doses)
    {
        return session(started, highFirst, high, low, false, doses);
    }

    private static KieSession session(final List<String> started, final boolean highFirst, final TargetSeries high,
            final TargetSeries low, final boolean override, final TargetDose... doses)
    {
        final KieSession session = startupRules.newKieSession();
        session.addEventListener(new DefaultAgendaEventListener()
        {
            @Override
            public void beforeMatchFired(final BeforeMatchFiredEvent event)
            {
                if (!ROUTING_RULE.equals(event.getMatch().getRule().getName()))
                    assertThat(((TargetSeries) event.getMatch()
                            .getDeclarationValue("$ts")).isHistoryEvaluationRoutingInitiated()).isTrue();
            }

            @Override
            public void afterMatchFired(final AfterMatchFiredEvent event)
            {
                if (!ROUTING_RULE.equals(event.getMatch().getRule().getName()))
                    started.add(((TargetSeries) event.getMatch().getDeclarationValue("$ts")).getSeriesName());
            }
        });

        session.insert(highFirst ? high : low);
        session.insert(highFirst ? low : high);
        (highFirst ? List.of(doses) : List.of(doses).reversed()).forEach((final TargetDose dose) ->
        {
            session.insert(dose);
            final SubstanceAdministrationEvent event = new SubstanceAdministrationEvent();
            event.setId(dose.getDoseId());
            if (override)
            {
                final BL valid = new BL();
                valid.setValue(true);
                event.setIsValid(valid);
            }
            session.insert(event);
        });
        session.insert(new ICEFactTypeFinding("DOSE_OVERRIDE_FEATURE_ENABLED"));
        session.getAgenda().getAgendaGroup("HistoryEvaluation").setFocus();
        return session;
    }

    @ParameterizedTest
    @ValueSource(strings = { "\n", "\r\n" })
    void realHistoryStartupRulesCompileWithEitherLineEnding(final String lineEnding) throws IOException
    {
        final KieBase rules = compileHistoryStartupRules(readCommonRules().replace("\r\n", "\n").replace("\n", lineEnding));
        assertThat(rules.getKiePackages()
                .stream()
                .flatMap((final KiePackage rulePackage) -> rulePackage.getRules().stream())
                .map(Rule::getName)).hasSize(8).contains(ROUTING_RULE);
    }

    @ParameterizedTest
    @MethodSource("entryPaths")
    void higherPriorityFinishesBeforeOlderLowerPriorityHistory(final EntryPath path, final boolean highFirst)
    {
        final TargetSeries high = series("high", 10, 1, path.seasonal(), DATE);
        final TargetSeries low = series("low", 0, 0, path.seasonal(), DATE.minusYears(2));
        final TargetDose highDose = dose(high, "high-dose", DATE, path.nos());
        final TargetDose lowDose = dose(low, "low-dose", DATE.minusYears(2), path.nos());
        final List<String> started = new ArrayList<>();
        try (final KieSession session = session(started, highFirst, high, low, path == EntryPath.OVERRIDE, highDose, lowDose))
        {
            session.fireAllRules();
            if (path != EntryPath.OVERRIDE)
            {
                assertThat(started).containsExactly("high");
                assertThat(lowDose.getStatus()).isEqualTo(DoseStatus.EVALUATION_NOT_STARTED);
                assertThat(low.isHistoryEvaluationRoutingInitiated()).isFalse();

                // NOT_EVALUATED is terminal even though hasBeenEvaluated remains false.
                highDose.setStatus(DoseStatus.NOT_EVALUATED);
                session.update(session.getFactHandle(highDose), highDose);
                session.getAgenda().getAgendaGroup("HistoryEvaluation").setFocus();
                session.fireAllRules();
            }
            assertThat(started).containsExactly("high", "low");
        }
    }

    @ParameterizedTest
    @MethodSource("emptySeries")
    void emptySeriesHonorPriorityAcrossSeasonalAndNonSeasonalGroups(final boolean highSeasonal, final boolean lowSeasonal,
            final boolean highFirst)
    {
        final TargetSeries high = series("high", 10, 1, highSeasonal, DATE);
        final TargetSeries low = series("low", 0, 0, lowSeasonal, DATE.minusYears(2));
        final List<String> started = new ArrayList<>();
        try (final KieSession session = session(started, highFirst, high, low))
        {
            session.fireAllRules();
            assertThat(started).containsExactly("high", "low");
            assertThat(high.isHistoryEvaluationInitiated()).isTrue();
            assertThat(low.isHistoryEvaluationInitiated()).isTrue();
        }
    }

    @ParameterizedTest
    @MethodSource("regularEntryPaths")
    void equalPriorityPreservesExistingDateAndSeasonOrder(final EntryPath path, final boolean earlyFirst)
    {
        final TargetSeries early = series("early", 0, 0, path.seasonal(), DATE.minusYears(1));
        final TargetSeries late = series("late", 0, 1, path.seasonal(), DATE);
        final TargetDose earlyDose = dose(early, "early-dose", DATE.minusYears(1), path.nos());
        final TargetDose lateDose = dose(late, "late-dose", DATE, path.nos());
        final List<String> started = new ArrayList<>();
        try (final KieSession session = session(started, earlyFirst, early, late, earlyDose, lateDose))
        {
            session.fireAllRules();
            assertThat(started).containsExactly("early");

            earlyDose.setStatus(DoseStatus.VALID);
            session.update(session.getFactHandle(earlyDose), earlyDose);
            session.getAgenda().getAgendaGroup("HistoryEvaluation").setFocus();
            session.fireAllRules();
            assertThat(started).containsExactly("early", "late");
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void nonNosPrecedesSameDayNosWithinHigherPriorityGroup(final boolean seasonal)
    {
        final TargetSeries high = series("high", 10, 1, seasonal, DATE);
        final TargetSeries low = series("low", 0, 0, seasonal, DATE.minusYears(2));
        final TargetDose nos = dose(high, "nos", DATE, true);
        final TargetDose specific = dose(high, "specific", DATE, false);
        final TargetDose lower = dose(low, "low", DATE.minusYears(2), false);
        final List<String> started = new ArrayList<>();
        try (final KieSession session = session(started, false, high, low, nos, specific, lower))
        {
            session.fireAllRules();
            assertThat(specific.getStatus()).isEqualTo(DoseStatus.PRIMARY_SHOT_DETERMINATION_IN_PROCESS);
            assertThat(nos.getStatus()).isEqualTo(DoseStatus.EVALUATION_NOT_STARTED);

            specific.setStatus(DoseStatus.VALID);
            session.update(session.getFactHandle(specific), specific);
            session.getAgenda().getAgendaGroup("HistoryEvaluation").setFocus();
            session.fireAllRules();
            assertThat(nos.getStatus()).isEqualTo(DoseStatus.PRIMARY_SHOT_DETERMINATION_IN_PROCESS);
            assertThat(lower.getStatus()).isEqualTo(DoseStatus.EVALUATION_NOT_STARTED);
        }
    }

    @Test
    void uninitiatedHigherPriorityEmptySeriesRunsBeforeLowerPriorityDose()
    {
        final TargetSeries high = series("high", 10, 1, false, DATE);
        final TargetSeries low = series("low", 0, 0, false, DATE);
        final TargetDose lowDose = dose(low, "low-dose", DATE, false);
        final List<String> started = new ArrayList<>();
        try (final KieSession session = session(started, false, high, low, true, lowDose))
        {
            session.fireAllRules();
            assertThat(started).containsExactly("high", "low");
        }
    }
}
