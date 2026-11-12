package org.cdsframework.ice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cdsframework.cds.CdsConcept;
import org.junit.jupiter.api.Test;
import org.opencds.vmr.v1_0.internal.Demographics;
import org.opencds.vmr.v1_0.internal.EvaluatedPerson;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;

class TargetSeriesComparatorTest
{
    private record DoseInput(Vaccine vaccine,
                             SubstanceAdministrationEvent event)
    {
    }

    private static DoseInput doseInput(final String id, final String vaccineConcept, final LocalDate administrationDate)
    {
        return doseInput(id, vaccineConcept, vaccineConcept, administrationDate);
    }

    private static DoseInput doseInput(final String id, final String vaccineConcept, final String componentConcept,
            final LocalDate administrationDate)
    {
        final VaccineComponent component = new VaccineComponent(new CdsConcept(componentConcept), List.of("disease"));
        final Vaccine vaccine = mock(Vaccine.class);
        when(vaccine.getCdsConceptName()).thenReturn(vaccineConcept);
        when(vaccine.getVaccineComponentsTargetingSpecifiedDiseases(List.of("disease"))).thenReturn(List.of(component));

        final IVLDate administrationTimeInterval = new IVLDate();
        administrationTimeInterval.setLow(administrationDate);
        final SubstanceAdministrationEvent event = new SubstanceAdministrationEvent();
        event.setId(id);
        event.setAdministrationTimeInterval(administrationTimeInterval);
        return new DoseInput(vaccine, event);
    }

    private static TargetSeries initializeSeries(final List<DoseInput> doses)
    {
        final SeriesRules rules = new SeriesRules("test-series", new CdsConcept("VACCINE_GROUP_CONCEPT.TEST"));
        final Schedule schedule = mock(Schedule.class);
        when(schedule.getDiseasesTargetedByVaccineGroup(rules.getVaccineGroup())).thenReturn(List.of("disease"));
        final Demographics demographics = new Demographics();
        demographics.setBirthTime(LocalDate.of(2007, 1, 1));
        final EvaluatedPerson patient = new EvaluatedPerson();
        patient.setDemographics(demographics);
        final TargetSeries series = new TargetSeries(rules, schedule, patient);
        final TargetDoseInitializationTracker tracker = new TargetDoseInitializationTracker();

        doses.forEach(dose -> tracker.addTargetDoseInitialization(dose.vaccine(), dose.event(), series, schedule));
        return series;
    }

    private static void collectPermutations(final List<DoseInput> doses, final int index, final List<List<DoseInput>> permutations)
    {
        if (index == doses.size())
        {
            permutations.add(List.copyOf(doses));
            return;
        }

        for (int i = index; i < doses.size(); i++)
        {
            Collections.swap(doses, index, i);
            collectPermutations(doses, index + 1, permutations);
            Collections.swap(doses, index, i);
        }
    }

    @Test
    void ordersEveryPermutationByAdministrationDateNumericCvxAndDoseId()
    {
        final List<DoseInput> doses = List.of(doseInput("119-2007-10-29", "VACCINE_CVX_119", LocalDate.of(2007, 10, 29)),
                doseInput("74-2007-10-29", "VACCINE_CVX_74", LocalDate.of(2007, 10, 29)),
                doseInput("74-2007-12-27", "VACCINE_CVX_74", LocalDate.of(2007, 12, 27)),
                doseInput("74-2008-03-04", "VACCINE_CVX_74", LocalDate.of(2008, 3, 4)),
                doseInput("119-2008-03-04", "VACCINE_CVX_119", LocalDate.of(2008, 3, 4)),
                doseInput("119-2007-12-27", "VACCINE_CVX_119", LocalDate.of(2007, 12, 27)));
        final List<String> expectedOrder =
                List.of("74-2007-10-29", "119-2007-10-29", "74-2007-12-27", "119-2007-12-27", "74-2008-03-04", "119-2008-03-04");

        final List<List<DoseInput>> permutations = new ArrayList<>();
        collectPermutations(new ArrayList<>(doses), 0, permutations);

        assertEquals(720, permutations.size());
        permutations.forEach(permutation ->
        {
            final TargetSeries series = initializeSeries(permutation);
            assertEquals(expectedOrder, series.getTargetDoses().stream().map(TargetDose::getDoseId).toList());
            assertEquals(List.of(1, 2, 3, 4, 5, 6),
                    series.getTargetDoses().stream().map(TargetDose::getAdministeredShotNumberInSeries).toList());
        });
    }

    @Test
    void ordersSameAdministrationComponentsByComponentConcept()
    {
        final DoseInput hepatitisBDose =
                doseInput("same-administration", "VACCINE_CVX_104", "VACCINE_CVX_ENGERIX_B", LocalDate.of(2007, 10, 29));
        final DoseInput hepatitisADose =
                doseInput("same-administration", "VACCINE_CVX_104", "VACCINE_CVX_HAVRIX", LocalDate.of(2007, 10, 29));

        final TargetSeries series = initializeSeries(List.of(hepatitisBDose, hepatitisADose));

        assertEquals(List.of("VACCINE_CVX_ENGERIX_B", "VACCINE_CVX_HAVRIX"),
                series.getTargetDoses().stream().map(dose -> dose.getVaccineComponent().getCdsConceptName()).toList());
    }

    @Test
    void ordersEveryPermutationOfMixedConceptNamesConsistently()
    {
        final LocalDate administrationDate = LocalDate.of(2007, 10, 29);
        final List<DoseInput> doses = List.of(doseInput("flu", "VACCINE_FLU", administrationDate),
                doseInput("cvx-10a", "VACCINE_CVX_10A", administrationDate),
                doseInput("cvx-9", "VACCINE_CVX_9", administrationDate), doseInput("cvx-10", "VACCINE_CVX_10", administrationDate),
                doseInput("cvx-009", "VACCINE_CVX_009", administrationDate));
        final List<String> expectedOrder = List.of("cvx-009", "cvx-9", "cvx-10", "cvx-10a", "flu");

        final List<List<DoseInput>> permutations = new ArrayList<>();
        collectPermutations(new ArrayList<>(doses), 0, permutations);

        assertEquals(120, permutations.size());
        permutations.forEach(permutation ->
        {
            final TargetSeries series = initializeSeries(permutation);
            assertEquals(expectedOrder, series.getTargetDoses().stream().map(TargetDose::getDoseId).toList());
        });
    }
}
