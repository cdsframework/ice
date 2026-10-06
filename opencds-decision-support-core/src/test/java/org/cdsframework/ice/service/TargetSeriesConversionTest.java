package org.cdsframework.ice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.ice.util.TimePeriod;
import org.junit.jupiter.api.Test;
import org.opencds.vmr.v1_0.internal.Demographics;
import org.opencds.vmr.v1_0.internal.EvaluatedPerson;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationEvent;

class TargetSeriesConversionTest
{
    private static final String VACCINE_GROUP = "VACCINE_GROUP_CONCEPT.COVID_19";
    private static final String ADULT_SERIES = "SUPPORTED_SERIES.COVID_19_AUG_2025_2_Y_TO_64_Y_SERIES";
    private static final String OLDER_ADULT_SERIES = "SUPPORTED_SERIES.COVID_19_AUG_2025_GTE_65_SERIES";
    private static final LocalDate BIRTH_DATE = LocalDate.of(1960, 11, 20);

    @Test
    void conversionPreservesOtherSeasonsAndTheCapturedDoseEvaluation() throws InconsistentConfigurationException
    {
        final SeriesRules adultRules = new SeriesRules(ADULT_SERIES, new CdsConcept(VACCINE_GROUP));
        adultRules.setSeriesGroupToTransitionTo(2);
        final DoseRule adultDoseRule = new DoseRule(adultRules);
        adultDoseRule.setDoseNumber(1);
        adultDoseRule.setAbsoluteMaximumAge(new TimePeriod("65y-1d"));
        adultRules.addSeriesDoseRule(adultDoseRule);

        final SeriesRules olderAdultRules = new SeriesRules(OLDER_ADULT_SERIES, new CdsConcept(VACCINE_GROUP));
        final DoseRule olderAdultFirstDoseRule = new DoseRule(olderAdultRules);
        olderAdultFirstDoseRule.setDoseNumber(1);
        olderAdultFirstDoseRule.setAbsoluteMinimumAge(new TimePeriod("65y"));
        olderAdultFirstDoseRule.setAbsoluteMinimumInterval(new TimePeriod("6m-4d"));
        olderAdultRules.addSeriesDoseRule(olderAdultFirstDoseRule);
        final DoseRule olderAdultSecondDoseRule = new DoseRule(olderAdultRules);
        olderAdultSecondDoseRule.setDoseNumber(2);
        olderAdultRules.addSeriesDoseRule(olderAdultSecondDoseRule);

        final Schedule schedule = mock(Schedule.class);
        when(schedule.getScheduleSeriesByName(VACCINE_GROUP, OLDER_ADULT_SERIES)).thenReturn(olderAdultRules);
        final Demographics demographics = new Demographics();
        demographics.setBirthTime(BIRTH_DATE);
        final EvaluatedPerson person = new EvaluatedPerson();
        person.setDemographics(demographics);
        final TargetSeries pastSeason = new TargetSeries(adultRules, schedule, person);
        final TargetSeries currentSeason = new TargetSeries(adultRules, schedule, person);
        final TargetDose dose6 = dose(pastSeason, LocalDate.of(2025, 10, 1));
        dose6.setStatus(DoseStatus.VALID);
        dose6.captureEvaluationContext(pastSeason, 3);

        pastSeason.convertToSpecifiedSeries(OLDER_ADULT_SERIES, 2, true);

        assertEquals(OLDER_ADULT_SERIES, pastSeason.getSeriesName());
        assertEquals(2, pastSeason.getSeriesRules().getNumberOfDosesInSeries());
        assertEquals(2, pastSeason.getSeriesRules().getSeriesGroupToTransitionTo());
        assertNotSame(adultRules, pastSeason.getSeriesRules());
        assertSame(adultRules, currentSeason.getSeriesRules());
        assertEquals(ADULT_SERIES, currentSeason.getSeriesName());
        assertEquals(1, adultRules.getNumberOfDosesInSeries());
        assertEquals(ADULT_SERIES, dose6.getEvaluatedSeriesName());
        assertEquals(DoseStatus.VALID, dose6.getStatus());
        assertEquals("65y-1d", pastSeason.obtainDoseRuleForSeriesByTargetDose(dose6)
                .getAbsoluteMaximumAge().getTimePeriodStringRepresentation());
        assertNotSame(adultDoseRule, pastSeason.obtainDoseRuleForSeriesByTargetDose(dose6));
        assertNotSame(olderAdultSecondDoseRule, pastSeason.getSeriesRules().getSeriesDoseRuleByDoseNumber(2));
        assertNotSame(olderAdultFirstDoseRule.getAbsoluteMinimumInterval(),
                pastSeason.obtainDoseRuleForSeriesByTargetDose(dose6).getAbsoluteMinimumInterval());

        final TargetSeries olderAdultCurrentSeason = new TargetSeries(olderAdultRules, schedule, person);
        final TargetDose dose7 = dose(olderAdultCurrentSeason, LocalDate.of(2026, 9, 29));
        olderAdultCurrentSeason.evaluateVaccineGroupMaximumAgeandRecordReason(BIRTH_DATE, dose7);
        assertFalse(dose7.containsInvalidReason("EVALUATION_REASON_CONCEPT.ABOVE_MAXIMUM_AGE_SERIES"));
    }

    private static TargetDose dose(final TargetSeries series, final LocalDate administrationDate)
    {
        final VaccineComponent component = new VaccineComponent(new CdsConcept("VACCINE_CVX_309"), List.of("COVID_19"));
        final Vaccine vaccine = new Vaccine(new CdsConcept("VACCINE_CVX_309"), new ArrayList<>(List.of(component)));
        final SubstanceAdministrationEvent event = new SubstanceAdministrationEvent();
        event.setId(administrationDate.toString());
        return new TargetDose(vaccine, component, administrationDate, series, event);
    }
}
