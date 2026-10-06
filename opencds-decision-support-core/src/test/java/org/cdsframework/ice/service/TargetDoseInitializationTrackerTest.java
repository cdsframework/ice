package org.cdsframework.ice.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.internal.concepts.ImmunizationConcept;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;

class TargetDoseInitializationTrackerTest
{
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
        final List<VaccineComponent> components = new ArrayList<>();
        for (final String componentCode : componentCodes)
        {
            final VaccineComponent component = mock(VaccineComponent.class);
            when(component.getCdsConceptName()).thenReturn(componentCode);
            components.add(component);
        }
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
}
