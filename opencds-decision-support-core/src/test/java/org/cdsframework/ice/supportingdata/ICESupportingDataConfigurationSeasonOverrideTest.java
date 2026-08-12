package org.cdsframework.ice.supportingdata;

import java.time.LocalDate;
import java.time.MonthDay;

import org.cdsframework.cds.supportingdata.SupportedCdsLists;
import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.Season;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class ICESupportingDataConfigurationSeasonOverrideTest
{
    private SupportedSeasons supportedSeasons;

    @BeforeEach
    public void setUp()
    {
        final ICESupportingDataConfiguration configuration = Mockito.mock(ICESupportingDataConfiguration.class);
        final SupportedCdsLists supportedCdsLists = Mockito.mock(SupportedCdsLists.class);
        final SupportedVaccineGroups supportedVaccineGroups = Mockito.mock(SupportedVaccineGroups.class);
        Mockito.when(configuration.getSupportedCdsLists()).thenReturn(supportedCdsLists);
        Mockito.when(configuration.getSupportedVaccineGroups()).thenReturn(supportedVaccineGroups);

        supportedSeasons = new SupportedSeasons(configuration);
    }

    @Test
    public void testApplySeasonOverride_DefaultSeasonWithFixedStartDate_ThrowsException()
    {
        final Season season = Mockito.mock(Season.class);
        Mockito.when(season.isDefaultSeason()).thenReturn(true);
        Mockito.when(season.getSeasonName()).thenReturn("DefaultSeason");

        final IceProperties.SeasonOverride override = new IceProperties.SeasonOverride(LocalDate.now(), null, null, null);

        final InconsistentConfigurationException exception = Assertions.assertThrows(InconsistentConfigurationException.class,
                () -> ReflectionTestUtils.invokeMethod(supportedSeasons, "applySeasonOverride", season, override));
        Assertions.assertEquals("Attempt to set season start or end date for a default season: DefaultSeason",
                exception.getMessage());
    }

    @Test
    public void testApplySeasonOverride_DefaultSeasonWithFixedEndDate_ThrowsException()
    {
        final Season season = Mockito.mock(Season.class);
        Mockito.when(season.isDefaultSeason()).thenReturn(true);
        Mockito.when(season.getSeasonName()).thenReturn("DefaultSeason");

        final IceProperties.SeasonOverride override = new IceProperties.SeasonOverride(null, LocalDate.now(), null, null);

        final InconsistentConfigurationException exception = Assertions.assertThrows(InconsistentConfigurationException.class,
                () -> ReflectionTestUtils.invokeMethod(supportedSeasons, "applySeasonOverride", season, override));
        Assertions.assertEquals("Attempt to set season start or end date for a default season: DefaultSeason",
                exception.getMessage());
    }

    @Test
    public void testApplySeasonOverride_NonDefaultSeasonWithDefaultStart_ThrowsException()
    {
        final Season season = Mockito.mock(Season.class);
        Mockito.when(season.isDefaultSeason()).thenReturn(false);
        Mockito.when(season.getSeasonName()).thenReturn("NonDefaultSeason");

        final IceProperties.SeasonOverride override = new IceProperties.SeasonOverride(null, null, "01-01", null);

        final InconsistentConfigurationException exception = Assertions.assertThrows(InconsistentConfigurationException.class,
                () -> ReflectionTestUtils.invokeMethod(supportedSeasons, "applySeasonOverride", season, override));
        Assertions.assertEquals("Attempt to set default start or end month and day for a non-default season: NonDefaultSeason",
                exception.getMessage());
    }

    @Test
    public void testApplySeasonOverride_NonDefaultSeasonWithDefaultStop_ThrowsException()
    {
        final Season season = Mockito.mock(Season.class);
        Mockito.when(season.isDefaultSeason()).thenReturn(false);
        Mockito.when(season.getSeasonName()).thenReturn("NonDefaultSeason");

        final IceProperties.SeasonOverride override = new IceProperties.SeasonOverride(null, null, null, "12-31");

        final InconsistentConfigurationException exception = Assertions.assertThrows(InconsistentConfigurationException.class,
                () -> ReflectionTestUtils.invokeMethod(supportedSeasons, "applySeasonOverride", season, override));
        Assertions.assertEquals("Attempt to set default start or end month and day for a non-default season: NonDefaultSeason",
                exception.getMessage());
    }

    @Test
    public void testApplySeasonOverride_ValidDefaultOverride_Success()
    {
        final Season season = Mockito.mock(Season.class);
        Mockito.when(season.isDefaultSeason()).thenReturn(true);

        // We use a real object for getMonthDayObjectForSDMonthDayStr since we are using a real supportedSeasons
        // Or we can spy on it.
        final SupportedSeasons spySeasons = org.mockito.Mockito.spy(supportedSeasons);

        final MonthDay start = MonthDay.of(1, 1);
        final MonthDay stop = MonthDay.of(12, 31);
        Mockito.doReturn(start).when(spySeasons).getMonthDayObjectForSDMonthDayStr("01-01");
        Mockito.doReturn(stop).when(spySeasons).getMonthDayObjectForSDMonthDayStr("12-31");

        final IceProperties.SeasonOverride override = new IceProperties.SeasonOverride(null, null, "01-01", "12-31");

        ReflectionTestUtils.invokeMethod(spySeasons, "applySeasonOverride", season, override);

        Mockito.verify(season).setDefaultStartMonthAndDay(start);
        Mockito.verify(season).setDefaultEndMonthAndDay(stop);
    }

    @Test
    public void testApplySeasonOverride_ValidNonDefaultOverride_Success()
    {
        final Season season = Mockito.mock(Season.class);
        Mockito.when(season.isDefaultSeason()).thenReturn(false);

        final LocalDate start = LocalDate.of(2025, 1, 1);
        final LocalDate stop = LocalDate.of(2025, 12, 31);

        final IceProperties.SeasonOverride override = new IceProperties.SeasonOverride(start, stop, null, null);

        ReflectionTestUtils.invokeMethod(supportedSeasons, "applySeasonOverride", season, override);

        Mockito.verify(season).setSeasonStartDate(start);
        Mockito.verify(season).setSeasonEndDate(stop);
    }
}
