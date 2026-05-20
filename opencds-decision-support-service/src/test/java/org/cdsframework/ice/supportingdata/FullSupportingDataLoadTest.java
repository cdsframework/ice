package org.cdsframework.ice.supportingdata;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.cdsframework.fhir.CodeSystem;
import org.cdsframework.fhir.CodeSystemConcept;
import org.cdsframework.fhir.PlanDefinition;
import org.cdsframework.ice.config.CdsEngineProperties;
import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.service.PlanDefinitionSeriesDataConsumer;
import org.cdsframework.ice.service.SupportingDataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { IceProperties.class, CdsEngineProperties.class },
                      initializers = ConfigDataApplicationContextInitializer.class)
@TestPropertySource(properties = {
        "spring.config.import=classpath:application.yml,classpath:data/knowledgeCommon/org.cdsframework.ice/ice-supporting-data/iceSupportingData.yml,classpath:data/knowledgeModule/org.nyc.cir.ice/ice-supporting-data/iceSupportingData.yml" })
@EnableConfigurationProperties({ IceProperties.class, CdsEngineProperties.class })
public class FullSupportingDataLoadTest
{
    private static final String SUPPORTED_SERIES_CODE_SYSTEM_NAME = "SUPPORTED_SERIES";

    private static String normalizeSeasonCode(final String code)
    {
        if (code == null)
            return "";

        final String trimmed = code.trim();
        final String prefix = "SUPPORTED_SEASON.";
        return trimmed.startsWith(prefix) ? trimmed.substring(prefix.length()) : trimmed;
    }

    private static String normalizeSeriesCode(final String code)
    {
        if (code == null)
            return "";

        final String trimmed = code.trim();
        final String prefix = "SUPPORTED_SERIES.";
        return trimmed.startsWith(prefix) ? trimmed.substring(prefix.length()) : trimmed;
    }

    @Autowired
    private IceProperties iceProperties;
    @Autowired
    private CdsEngineProperties cdsEngineProperties;

    @Test
    public void testLoadFullSupportingData_CommonAndNYC()
    {
        assertNotNull(iceProperties, "IceProperties should be injected");
        final SupportingDataService supportingDataService = new SupportingDataService(cdsEngineProperties, iceProperties);
        final String baseKnowledgeModuleId = supportingDataService.getBaseKnowledgeModuleId();
        final String nycKnowledgeModuleId =
                supportingDataService.getKmIdFromModuleCanonicalUrl("http://cdsframework.org/PlanDefinition/ice-forecast|1.0.0");

        final ICESupportingDataConfiguration configuration = assertDoesNotThrow(
                () -> new ICESupportingDataConfiguration(baseKnowledgeModuleId, List.of(nycKnowledgeModuleId),
                        supportingDataService));
        assertNotNull(configuration.getSupportedCdsLists());
        assertFalse(configuration.getSupportedCdsLists().isEmpty());
        assertFalse(configuration.getSupportedSeries().isEmpty());
        final Set<String> configuredSeasonCodes = configuration.getSupportedSeasons()
                .getCopyOfAllSeasons()
                .stream()
                .map(org.cdsframework.ice.service.Season::getSeasonName)
                .map(FullSupportingDataLoadTest::normalizeSeasonCode)
                .filter(code -> !code.isBlank())
                .collect(java.util.stream.Collectors.toCollection(TreeSet::new));
        final Set<String> referencedSeasonCodes = configuration.getSupportedSeries()
                .getCopyOfAllSeriesRules()
                .stream()
                .map(org.cdsframework.ice.service.SeriesRules::getSeasons)
                .filter(java.util.Objects::nonNull)
                .flatMap(java.util.Collection::stream)
                .map(org.cdsframework.ice.service.Season::getSeasonName)
                .map(FullSupportingDataLoadTest::normalizeSeasonCode)
                .filter(code -> !code.isBlank())
                .collect(java.util.stream.Collectors.toCollection(TreeSet::new));
        final Set<String> missingSeasonCodes = new TreeSet<>(configuredSeasonCodes);
        missingSeasonCodes.removeAll(referencedSeasonCodes);
        assertTrue(missingSeasonCodes.isEmpty(),
                "Supported seasons not referenced by series data: " + String.join(", ", missingSeasonCodes));

        final CdsEngineProperties.ModuleCanonicalDefinition nycModule =
                supportingDataService.getSupportingKnowledgeModuleByKmId(nycKnowledgeModuleId);
        final Set<String> supportedSeriesCodes = java.util.Optional.ofNullable(nycModule.codeSystems())
                .map(codeSystems -> codeSystems.get(SUPPORTED_SERIES_CODE_SYSTEM_NAME))
                .map(CodeSystem::concept)
                .orElse(List.of())
                .stream()
                .map(CodeSystemConcept::code)
                .filter(java.util.Objects::nonNull)
                .map(String::trim)
                .filter(Predicate.not(String::isBlank))
                .collect(Collectors.toCollection(TreeSet::new));

        final PlanDefinitionSeriesDataConsumer seriesConsumer = new PlanDefinitionSeriesDataConsumer();
        final Set<String> seriesPlanDefinitionNames = java.util.Optional.ofNullable(nycModule.planDefinitions())
                .orElse(java.util.Map.of())
                .entrySet()
                .stream()
                .filter(entry -> seriesConsumer.isSeriesPlanDefinition(entry.getValue()))
                .filter(entry -> entry.getKey() != null)
                .map(java.util.Map.Entry::getKey)
                .map(String::trim)
                .collect(Collectors.toCollection(TreeSet::new));
        final Set<String> mapKeyNameMismatches = java.util.Optional.ofNullable(nycModule.planDefinitions())
                .orElse(java.util.Map.of())
                .entrySet()
                .stream()
                .filter(entry -> seriesConsumer.isSeriesPlanDefinition(entry.getValue()))
                .filter(entry ->
                {
                    final PlanDefinition planDefinition = entry.getValue();
                    return entry.getKey() == null || planDefinition == null || planDefinition.name() == null || !entry.getKey()
                            .trim()
                            .equals(planDefinition.name().trim());
                })
                .map(java.util.Map.Entry::getKey)
                .collect(Collectors.toCollection(TreeSet::new));
        assertTrue(mapKeyNameMismatches.isEmpty(),
                "Series PlanDefinition key/name mismatches: " + String.join(", ", mapKeyNameMismatches));

        final Set<String> planDefinitionNamesMissingFromSupportedSeries = new TreeSet<>(seriesPlanDefinitionNames);
        planDefinitionNamesMissingFromSupportedSeries.removeAll(supportedSeriesCodes);
        assertTrue(planDefinitionNamesMissingFromSupportedSeries.isEmpty(),
                "Series PlanDefinition name(s) not found in SUPPORTED_SERIES: " + String.join(", ",
                        planDefinitionNamesMissingFromSupportedSeries));

        final Set<String> loadedSeriesCodes = configuration.getSupportedSeries()
                .getCopyOfAllSeriesRules()
                .stream()
                .map(org.cdsframework.ice.service.SeriesRules::getSeriesName)
                .filter(java.util.Objects::nonNull)
                .map(FullSupportingDataLoadTest::normalizeSeriesCode)
                .filter(code -> !code.isBlank())
                .collect(Collectors.toCollection(TreeSet::new));
        final Set<String> supportedSeriesMissingFromSeriesData = new TreeSet<>(supportedSeriesCodes);
        supportedSeriesMissingFromSeriesData.removeAll(loadedSeriesCodes);
        assertTrue(supportedSeriesMissingFromSeriesData.isEmpty(),
                "SUPPORTED_SERIES concepts code(s) missing from series data: " + String.join(", ",
                        supportedSeriesMissingFromSeriesData));
    }
}
