package org.cdsframework.ice.service.configurations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.service.Schedule;
import org.cdsframework.ice.service.SupportingDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencds.plugin.api.PluginDataCache;
import org.opencds.plugin.api.PreProcessPluginContext;
import org.opencds.plugin.api.SupportingData;

public class ICESupportingDataLoaderPluginTest
{

    private ICESupportingDataLoaderPlugin plugin;
    private PreProcessPluginContext context;
    private PluginDataCache cache;
    private SupportingData supportingData;
    private SupportingDataService supportingDataService;
    private Map<String, Object> globals;

    @BeforeEach
    public void setUp()
    {
        plugin = spy(new ICESupportingDataLoaderPlugin());
        context = mock(PreProcessPluginContext.class);
        cache = mock(PluginDataCache.class);
        supportingData = mock(SupportingData.class);
        supportingDataService = mock(SupportingDataService.class);
        globals = new HashMap<>();

        when(context.cache()).thenReturn(cache);
        when(context.supportingData()).thenReturn(Collections.singletonMap("sd", supportingData));
        when(context.globals()).thenReturn(globals);
        when(supportingData.kmId()).thenReturn("testKmId");
        when(supportingDataService.getKnowledgeModulePropertiesByKmId()).thenReturn(Collections.emptyMap());

        final IceProperties iceProperties = mock(IceProperties.class);
        when(iceProperties.getOutputEarliestAndOverdueDates()).thenReturn(Optional.empty());
        when(iceProperties.getOutputNumberOfDosesRemaining()).thenReturn(Optional.empty());
        when(iceProperties.getOutputSeriesInformation()).thenReturn(Optional.empty());
        when(iceProperties.getOutputSupplementalText()).thenReturn(Optional.empty());
        when(iceProperties.getOutputVaccineGroupRulesArtifact()).thenReturn(Optional.empty());
        when(iceProperties.getEnableDoseOverrideFeature()).thenReturn(Optional.empty());
        when(iceProperties.getEnableUnsupportedVaccinesGroup()).thenReturn(Optional.empty());
        when(iceProperties.getDisableCovid19Sep2023DoseNumberReset()).thenReturn(Optional.empty());
        when(iceProperties.getSupplementalTextMode()).thenReturn(Optional.empty());
        when(iceProperties.getVaccineGroupExclusions()).thenReturn(null);
        when(iceProperties.getVaccineGroupInclusions()).thenReturn(null);

        ICESupportingDataLoaderPlugin.setIceProperties(iceProperties);
        ICESupportingDataLoaderPlugin.setSupportingDataService(supportingDataService);
    }

    @Test
    public void testExecuteContextNull()
    {
        assertThrows(IllegalArgumentException.class, () -> plugin.execute(null));
    }

    @Test
    public void testExecuteSupportingDataEmpty()
    {
        when(context.supportingData()).thenReturn(Collections.emptyMap());
        assertThrows(IllegalArgumentException.class, () -> plugin.execute(context));
    }

    @Test
    public void testExecuteKnowledgeModulePropertiesNotFound()
    {
        when(supportingDataService.getKnowledgeModulePropertiesForKmId("testKmId")).thenThrow(
                new IllegalStateException("KnowledgeModuleProperties not found for kmId: testKmId"));
        assertThrows(RuntimeException.class, () -> plugin.execute(context));
    }

    @Test
    public void testExecuteSuccessful()
    {
        final IceProperties.KnowledgeModuleProperties kmProps = createMockKmProps();
        when(supportingDataService.getKnowledgeModulePropertiesForKmId("testKmId")).thenReturn(kmProps);

        final Schedule schedule = mock(Schedule.class);
        when(schedule.isScheduleInitialized()).thenReturn(true);
        when(cache.get(supportingData)).thenReturn(null);

        // Mock private method loadImmunizationSchedule using spy
        doReturn(schedule).when(plugin).loadImmunizationSchedule(eq("testKmId"), any());

        plugin.execute(context);

        verify(cache).put(supportingData, schedule);
        assertEquals(schedule, globals.get("schedule"));
        assertEquals(true, globals.get("outputEarliestOverdueDates"));
        assertEquals(false, globals.get("outputNumberOfDosesRemaining"));
        assertEquals(false, globals.get("outputSeriesInformation"));
        assertEquals(false, globals.get("doseOverrideFeatureEnabled"));
        assertEquals(true, globals.get("outputSupplementalText"));
        assertEquals(false, globals.get("outputVaccineGroupRulesArtifact"));
        assertNotNull(globals.get("vaccineGroupExclusions"));
    }

    @Test
    public void testExecuteCacheHit()
    {
        final IceProperties.KnowledgeModuleProperties kmProps = createMockKmProps();
        when(supportingDataService.getKnowledgeModulePropertiesForKmId("testKmId")).thenReturn(kmProps);

        final Schedule schedule = mock(Schedule.class);
        when(schedule.isScheduleInitialized()).thenReturn(true);
        when(cache.get(supportingData)).thenReturn(schedule);

        plugin.execute(context);

        verify(cache, atLeastOnce()).get(supportingData);
        assertEquals(schedule, globals.get("schedule"));
    }

    @Test
    public void testExecuteInvalidSupportingDataSize()
    {
        final Map<String, SupportingData> sdMap = new HashMap<>();
        sdMap.put("sd1", mock(SupportingData.class));
        sdMap.put("sd2", mock(SupportingData.class));
        when(context.supportingData()).thenReturn(sdMap);

        assertThrows(IllegalArgumentException.class, () -> plugin.execute(context));
    }

    @Test
    public void testExecuteScheduleNotInitialized()
    {
        final IceProperties.KnowledgeModuleProperties kmProps = createMockKmProps();
        when(supportingDataService.getKnowledgeModulePropertiesForKmId("testKmId")).thenReturn(kmProps);

        final Schedule schedule = mock(Schedule.class);
        when(schedule.isScheduleInitialized()).thenReturn(false);
        when(cache.get(supportingData)).thenReturn(schedule);

        assertThrows(RuntimeException.class, () -> plugin.execute(context));
    }

    private IceProperties.KnowledgeModuleProperties createMockKmProps()
    {
        final IceProperties.KnowledgeModuleProperties props = mock(IceProperties.KnowledgeModuleProperties.class);
        when(props.outputEarliestAndOverdueDates()).thenReturn(true);
        when(props.enableDoseOverrideFeature()).thenReturn(false);
        when(props.outputSupplementalText()).thenReturn(true);
        when(props.outputNumberOfDosesRemaining()).thenReturn(false);
        when(props.outputSeriesInformation()).thenReturn(false);
        when(props.outputVaccineGroupRulesArtifact()).thenReturn(false);
        when(props.enableUnsupportedVaccinesGroup()).thenReturn(false);
        when(props.vaccineGroupExclusions()).thenReturn(Collections.emptyList());
        when(props.vaccineGroupInclusions()).thenReturn(Collections.emptyList());
        when(props.disableCovid19Sep2023DoseNumberReset()).thenReturn(false);
        when(props.supplementalTextMode()).thenReturn(IceProperties.SupplementalTextMode.LEGACY);
        return props;
    }

    @Test
    public void testPreloadSchedules()
    {
        assertDoesNotThrow(ICESupportingDataLoaderPlugin::preloadSchedules);
    }
}
