package org.cdsframework.ice.supportingdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsListItem;
import org.cdsframework.cds.supportingdata.SupportedCdsConcepts;
import org.cdsframework.cds.supportingdata.SupportedCdsLists;
import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class ICESupportingDataConfigurationValidationTest
{
    private ICESupportingDataConfiguration configuration;
    private SupportedSeasons supportedSeasons;

    @BeforeEach
    public void setUp()
    {
        configuration = Mockito.mock(ICESupportingDataConfiguration.class, Mockito.CALLS_REAL_METHODS);
        final SupportedCdsLists supportedCdsLists = Mockito.mock(SupportedCdsLists.class);
        final SupportedCdsConcepts supportedCdsConcepts = Mockito.mock(SupportedCdsConcepts.class);
        final SupportedVaccineGroups supportedVaccineGroups = Mockito.mock(SupportedVaccineGroups.class);

        Mockito.when(configuration.getSupportedCdsLists()).thenReturn(supportedCdsLists);
        Mockito.when(supportedCdsLists.getSupportedCdsConcepts()).thenReturn(supportedCdsConcepts);
        Mockito.when(configuration.getSupportedVaccineGroups()).thenReturn(supportedVaccineGroups);

        supportedSeasons = new SupportedSeasons(configuration);
    }

    @Test
    public void testInitializeFromCdsLists_InvalidSeasonOverrideKey_ThrowsException()
    {
        final SupportedCdsConcepts supportedCdsConcepts = configuration.getSupportedCdsLists().getSupportedCdsConcepts();
        final Map<CdsConcept, LocallyCodedCdsListItem> seasonsMap = new HashMap<>();

        Mockito.when(supportedCdsConcepts.getCdsConceptsAssociatedWithICEConceptType(ICEConceptType.SEASON)).thenReturn(seasonsMap);

        final Map<String, IceProperties.SeasonOverride> seasonOverrides =
                Map.of("NonExistentSeason", new IceProperties.SeasonOverride(null, null, null, null));

        final InconsistentConfigurationException exception = Assertions.assertThrows(InconsistentConfigurationException.class,
                () -> supportedSeasons.initializeFromCdsLists(seasonOverrides));
        Assertions.assertEquals("Season override key(s) do not reference existing seasons: NonExistentSeason",
                exception.getMessage());
    }

    @Test
    public void testApplySeriesOverrides_InvalidSeriesOverrideKey_ThrowsException()
    {
        final Series series = new Series("Series1", "Series1", null, "Series1");
        final SeriesData seriesData =
                new SeriesData(series, 1, false, Map.of(), Map.of("VG1", new VaccineGroup("VG1", "VG1", null, "VG1")), 1, null,
                        null, false, Map.of(), Map.of());

        final Map<String, IceProperties.SeriesOverride> seriesOverrides =
                Map.of("NonExistentSeries", new IceProperties.SeriesOverride(Map.of(), Map.of(), 1, false, List.of()));

        // Using reflection to invoke private method applySeriesOverrides
        final InconsistentConfigurationException exception = Assertions.assertThrows(InconsistentConfigurationException.class,
                () -> ReflectionTestUtils.invokeMethod(configuration, "applySeriesOverrides", List.of(seriesData),
                        seriesOverrides));
        Assertions.assertEquals("Series override key(s) do not reference existing series: NonExistentSeries",
                exception.getMessage());
    }

    @Test
    public void testOverrideDosesMap_InvalidDoseOverrideKey_ThrowsException()
    {
        final Dose dose = new Dose(1, null, null, null, null, null, Map.of());
        final Map<String, Dose> originalDoses = Map.of("1", dose);

        final Map<Integer, IceProperties.SeriesDoseOverride> doseOverrides =
                Map.of(2, new IceProperties.SeriesDoseOverride(null, null, null, null, null, Map.of()));

        final InconsistentConfigurationException exception = Assertions.assertThrows(InconsistentConfigurationException.class,
                () -> ReflectionTestUtils.invokeMethod(configuration, "overrideDosesMap", "Series1", originalDoses, doseOverrides));
        Assertions.assertEquals("Dose override key(s) for series 'Series1' do not reference existing doses: 2",
                exception.getMessage());
    }

    @Test
    public void testOverrideDoseVaccinesMap_InvalidVaccineOverrideKey_ThrowsException()
    {
        final Vaccine vaccine = new Vaccine("V1", "V1", null, "V1");
        final DoseVaccine doseVaccine = new DoseVaccine(true, vaccine, null, null);
        final Map<String, DoseVaccine> originalDoseVaccines = Map.of("V1", doseVaccine);

        final Map<String, IceProperties.SeriesDoseVaccineOverride> vaccineOverrides =
                Map.of("V2", new IceProperties.SeriesDoseVaccineOverride(true, null));

        final InconsistentConfigurationException exception = Assertions.assertThrows(InconsistentConfigurationException.class,
                () -> ReflectionTestUtils.invokeMethod(configuration, "overrideDoseVaccinesMap", "Series1", 1, originalDoseVaccines,
                        vaccineOverrides));
        Assertions.assertEquals(
                "Series vaccine override key(s) for series 'Series1', dose '1' do not reference vaccine codes for doses for the series: V2",
                exception.getMessage());
    }

    @Test
    public void testOverrideDoseIntervalsMap_InvalidIntervalOverrideKey_ThrowsException()
    {
        final DoseInterval interval = new DoseInterval(1, 2, null, null, null, null);
        final Map<String, DoseInterval> originalDoseIntervals = Map.of("1-2", interval);

        final Map<Integer, Map<Integer, IceProperties.SeriesDoseIntervalOverride>> doseIntervalOverrides =
                Map.of(1, Map.of(3, new IceProperties.SeriesDoseIntervalOverride(null, null, null, null)));

        final InconsistentConfigurationException exception = Assertions.assertThrows(InconsistentConfigurationException.class,
                () -> ReflectionTestUtils.invokeMethod(configuration, "overrideDoseIntervalsMap", "Series1", originalDoseIntervals,
                        doseIntervalOverrides));
        Assertions.assertEquals(
                "Dose interval override (from: 1, to: 3) for series 'Series1' does not reference an existing dose interval",
                exception.getMessage());
    }
}
