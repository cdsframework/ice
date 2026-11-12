package org.cdsframework.ice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.cdsframework.cds.CdsConcept;
import org.junit.jupiter.api.Test;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.internal.concepts.ImmunizationConcept;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;

class TargetDoseInitializationTrackerTest
{
    private static final String ADMINISTRATION_ID = "administration-1";
    private static final String VACCINE_GROUP_A = "VACCINE_GROUP_A";
    private static final String VACCINE_GROUP_B = "VACCINE_GROUP_B";
    private static final String VACCINE_GROUP = "VACCINE_GROUP_CONCEPT.TEST";

    private static SubstanceAdministrationEvent event(final String id)
    {
        final SubstanceAdministrationEvent event = new SubstanceAdministrationEvent();
        event.setId(id);
        final IVLDate administrationDate = new IVLDate();
        administrationDate.setLow(LocalDate.of(2026, 1, 1));
        event.setAdministrationTimeInterval(administrationDate);
        return event;
    }

    private static TargetSeries series(final String id, final boolean acceptsDose)
    {
        final TargetSeries series = mock(TargetSeries.class);
        final SeriesRules rules = mock(SeriesRules.class);
        when(series.getSeriesRules()).thenReturn(rules);
        when(rules.getVaccineGroup()).thenReturn(VACCINE_GROUP);
        when(series.getTargetSeriesIdentifier()).thenReturn(id);
        when(series.addTargetDoseToSeries(any(TargetDose.class))).thenReturn(acceptsDose);
        return series;
    }

    private static Vaccine vaccine(final String... componentCodes)
    {
        final Vaccine vaccine = mock(Vaccine.class);
        final List<VaccineComponent> components = Arrays.stream(componentCodes).map((final String componentCode) ->
        {
            final VaccineComponent component = mock(VaccineComponent.class);
            when(component.getCdsConceptName()).thenReturn(componentCode);
            return component;
        }).toList();
        when(vaccine.getVaccineComponents()).thenReturn(components);
        when(vaccine.getVaccineComponentsTargetingSpecifiedDiseases(List.of("disease"))).thenReturn(components);
        return vaccine;
    }

    private static Schedule schedule()
    {
        final Schedule schedule = mock(Schedule.class);
        when(schedule.getDiseasesTargetedByVaccineGroup(VACCINE_GROUP)).thenReturn(List.of("disease"));
        return schedule;
    }

    private static ImmunizationConcept immunization(final String shotId, final String componentCode)
    {
        final ImmunizationConcept immunization = mock(ImmunizationConcept.class);
        when(immunization.getConceptTargetId()).thenReturn(shotId);
        when(immunization.getOpenCdsConceptCode()).thenReturn(componentCode);
        return immunization;
    }

    private static SubstanceAdministrationEvent administrationEvent()
    {
        final IVLDate administrationDate = new IVLDate();
        administrationDate.setLow(LocalDate.of(2025, 1, 1));
        administrationDate.setHigh(LocalDate.of(2025, 1, 1));
        final SubstanceAdministrationEvent event = mock(SubstanceAdministrationEvent.class);
        when(event.getId()).thenReturn(ADMINISTRATION_ID);
        when(event.getAdministrationTimeInterval()).thenReturn(administrationDate);
        return event;
    }

    private static ImmunizationConcept immunizationConcept(final Vaccine vaccine)
    {
        final ImmunizationConcept concept = mock(ImmunizationConcept.class);
        when(concept.getConceptTargetId()).thenReturn(ADMINISTRATION_ID);
        when(concept.getOpenCdsConceptCode()).thenReturn(vaccine.getCdsConceptName());
        return concept;
    }

    private static Vaccine monovalentVaccine(final boolean validInMultipleVaccineGroups)
    {
        final CdsConcept vaccineConcept = new CdsConcept("VACCINE_CVX_121");
        final VaccineComponent vaccineComponent = new VaccineComponent(vaccineConcept, List.of("DISEASE_A", "DISEASE_B"));
        final Vaccine vaccine = new Vaccine(vaccineConcept, Collections.singletonList(vaccineComponent));
        vaccine.setValidInMultipleVaccineGroups(validInMultipleVaccineGroups);
        return vaccine;
    }

    private static Schedule multiGroupSchedule()
    {
        final Schedule schedule = mock(Schedule.class);
        when(schedule.getDiseasesTargetedByVaccineGroup(VACCINE_GROUP_A)).thenReturn(List.of("DISEASE_A"));
        when(schedule.getDiseasesTargetedByVaccineGroup(VACCINE_GROUP_B)).thenReturn(List.of("DISEASE_B"));
        return schedule;
    }

    private static TargetSeries targetSeries(final String vaccineGroup, final String seriesIdentifier)
    {
        final SeriesRules seriesRules = new SeriesRules(seriesIdentifier, new CdsConcept(vaccineGroup));
        final TargetSeries targetSeries = mock(TargetSeries.class);
        when(targetSeries.getSeriesRules()).thenReturn(seriesRules);
        when(targetSeries.getTargetSeriesIdentifier()).thenReturn(seriesIdentifier);
        when(targetSeries.getSeriesName()).thenReturn(seriesIdentifier);
        when(targetSeries.addTargetDoseToSeries(any(TargetDose.class))).thenReturn(true);
        return targetSeries;
    }

    @Test
    void excludesMappedDoseButAllowsOtherComponentsForTheSameShotAndSeries()
    {
        final TargetDoseInitializationTracker tracker = new TargetDoseInitializationTracker();
        final SubstanceAdministrationEvent event = event("shot-1");
        final TargetSeries series = series("series-1", true);
        final VaccineComponent initializedComponent = vaccine("CVX_B").getVaccineComponents().getFirst();
        tracker.addTargetDoseInitialization(vaccine("CVX_B"), event, series, schedule());

        final ImmunizationConcept immunization = immunization("shot-1", "CVX_A");
        assertTrue(tracker.isTargetDoseInitializedInSeries(event, initializedComponent, series));
        assertFalse(tracker.shotAdministeredIsEligibleForInclusionInTargetSeries(immunization, VACCINE_GROUP, event, series,
                vaccine("CVX_A")));
        assertTrue(tracker.shotAdministeredIsEligibleForInclusionInTargetSeries(immunization, VACCINE_GROUP, event, series,
                vaccine("CVX_A", "CVX_B")));
        assertFalse(tracker.shotAdministeredIsEligibleForInclusionInTargetSeries(immunization, VACCINE_GROUP, event, series,
                vaccine("CVX_A"), "CVX_B"));
        assertTrue(tracker.shotAdministeredIsEligibleForInclusionInTargetSeries(immunization, VACCINE_GROUP, event, series,
                vaccine("CVX_A"), "CVX_C"));
    }

    @Test
    void separatesShotsAndSeriesWithOverlappingIdentifiers()
    {
        final TargetDoseInitializationTracker tracker = new TargetDoseInitializationTracker();
        final SubstanceAdministrationEvent otherShot = event("shot-1-extra");
        final TargetSeries otherSeries = series("prefix-series-1", true);
        tracker.addTargetDoseInitialization(vaccine("CVX_B"), otherShot, otherSeries, schedule());

        final SubstanceAdministrationEvent shot = event("shot-1");
        final TargetSeries series = series("series-1", true);
        final VaccineComponent component = vaccine("CVX_B").getVaccineComponents().getFirst();
        assertFalse(tracker.isTargetDoseInitializedInSeries(shot, component, otherSeries));
        assertFalse(tracker.isTargetDoseInitializedInSeries(otherShot, component, series));
        assertTrue(
                tracker.shotAdministeredIsEligibleForInclusionInTargetSeries(immunization("shot-1", "CVX_A"), VACCINE_GROUP, shot,
                        otherSeries, vaccine("CVX_A")));
        assertTrue(
                tracker.shotAdministeredIsEligibleForInclusionInTargetSeries(immunization("shot-1-extra", "CVX_A"), VACCINE_GROUP,
                        otherShot, series, vaccine("CVX_A")));
    }

    @Test
    void tracksOnlyDosesAcceptedByTheSeries()
    {
        final TargetDoseInitializationTracker tracker = new TargetDoseInitializationTracker();
        final SubstanceAdministrationEvent event = event("shot-1");
        final TargetSeries series = series("series-1", false);
        final VaccineComponent component = vaccine("CVX_B").getVaccineComponents().getFirst();
        tracker.addTargetDoseInitialization(vaccine("CVX_B"), event, series, schedule());

        assertFalse(tracker.isTargetDoseInitializedInSeries(event, component, series));
        assertTrue(
                tracker.shotAdministeredIsEligibleForInclusionInTargetSeries(immunization("shot-1", "CVX_A"), VACCINE_GROUP, event,
                        series, vaccine("CVX_A")));
    }

    @Test
    void flaggedMonovalentVaccineInitializesInTwoVaccineGroups()
    {
        final Vaccine vaccine = monovalentVaccine(true);
        final SubstanceAdministrationEvent event = administrationEvent();
        final TargetDoseInitializationTracker tracker = new TargetDoseInitializationTracker();
        final Schedule schedule = multiGroupSchedule();
        final TargetSeries firstSeries = targetSeries(VACCINE_GROUP_A, "SERIES_A");
        final TargetSeries secondSeries = targetSeries(VACCINE_GROUP_B, "SERIES_B");

        assertEquals(1, tracker.addTargetDoseInitialization(vaccine, event, firstSeries, schedule).size());
        assertTrue(
                tracker.shotAdministeredIsEligibleForInclusionInTargetSeries(immunizationConcept(vaccine), VACCINE_GROUP_B, event,
                        secondSeries, vaccine));
        assertEquals(1, tracker.addTargetDoseInitialization(vaccine, event, secondSeries, schedule).size());
    }

    @Test
    void unflaggedMonovalentVaccineIsRejectedForSecondVaccineGroup()
    {
        final Vaccine vaccine = monovalentVaccine(false);
        final SubstanceAdministrationEvent event = administrationEvent();
        final TargetDoseInitializationTracker tracker = new TargetDoseInitializationTracker();
        final Schedule schedule = multiGroupSchedule();
        final TargetSeries firstSeries = targetSeries(VACCINE_GROUP_A, "SERIES_A");
        final TargetSeries secondSeries = targetSeries(VACCINE_GROUP_B, "SERIES_B");

        assertEquals(1, tracker.addTargetDoseInitialization(vaccine, event, firstSeries, schedule).size());
        assertFalse(
                tracker.shotAdministeredIsEligibleForInclusionInTargetSeries(immunizationConcept(vaccine), VACCINE_GROUP_B, event,
                        secondSeries, vaccine));
        assertTrue(tracker.addTargetDoseInitialization(vaccine, event, secondSeries, schedule).isEmpty());
    }

    @Test
    void reenteringFlaggedVaccineGroupAndSeriesDoesNotCreateDuplicateTargetDose()
    {
        final Vaccine vaccine = monovalentVaccine(true);
        final SubstanceAdministrationEvent event = administrationEvent();
        final TargetDoseInitializationTracker tracker = new TargetDoseInitializationTracker();
        final Schedule schedule = multiGroupSchedule();
        final TargetSeries targetSeries = targetSeries(VACCINE_GROUP_A, "SERIES_A");

        assertEquals(1, tracker.addTargetDoseInitialization(vaccine, event, targetSeries, schedule).size());
        assertFalse(
                tracker.shotAdministeredIsEligibleForInclusionInTargetSeries(immunizationConcept(vaccine), VACCINE_GROUP_A, event,
                        targetSeries, vaccine));
        verify(targetSeries, times(1)).addTargetDoseToSeries(any(TargetDose.class));
    }

    @Test
    void combinationVaccineStillInitializesItsApplicableComponents()
    {
        final VaccineComponent componentA = new VaccineComponent(new CdsConcept("VACCINE_COMPONENT_A"), List.of("DISEASE_A"));
        final VaccineComponent componentB = new VaccineComponent(new CdsConcept("VACCINE_COMPONENT_B"), List.of("DISEASE_B"));
        final Vaccine vaccine = new Vaccine(new CdsConcept("VACCINE_CVX_COMBINATION"),
                Collections.unmodifiableList(Arrays.asList(componentA, componentB)));
        final SubstanceAdministrationEvent event = administrationEvent();
        final TargetDoseInitializationTracker tracker = new TargetDoseInitializationTracker();
        final Schedule schedule = multiGroupSchedule();
        final TargetSeries firstSeries = targetSeries(VACCINE_GROUP_A, "SERIES_A");
        final TargetSeries secondSeries = targetSeries(VACCINE_GROUP_B, "SERIES_B");

        assertEquals(1, tracker.addTargetDoseInitialization(vaccine, event, firstSeries, schedule).size());
        assertEquals(1, tracker.addTargetDoseInitialization(vaccine, event, secondSeries, schedule).size());
        verify(firstSeries).addTargetDoseToSeries(any(TargetDose.class));
        verify(secondSeries).addTargetDoseToSeries(any(TargetDose.class));
    }
}
