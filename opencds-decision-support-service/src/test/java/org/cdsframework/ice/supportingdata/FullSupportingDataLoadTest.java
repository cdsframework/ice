package org.cdsframework.ice.supportingdata;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.cdsframework.fhir.CodeSystem;
import org.cdsframework.fhir.CodeSystemConcept;
import org.cdsframework.fhir.CodeSystemConceptProperty;
import org.cdsframework.fhir.CodeableConcept;
import org.cdsframework.fhir.PlanDefinition;
import org.cdsframework.ice.config.CdsEngineProperties;
import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.service.PlanDefinitionSeriesDataConsumer;
import org.cdsframework.ice.service.SupportingDataService;
import org.cdsframework.ice.service.Vaccine;
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
        "spring.config.import=classpath:application.yml,classpath:data/knowledgeCommon/org.cdsframework.ice/ice-supporting-data/iceSupportingData.yml,classpath:data/knowledgeModule/org.nyc.cir.ice/ice-supporting-data/iceSupportingData.yml",
        "ice.schedule-flags[0]=HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID",
        "ice.schedule-flags[1]=POLIO_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID" })
@EnableConfigurationProperties({ IceProperties.class, CdsEngineProperties.class })
public class FullSupportingDataLoadTest
{
    private static final String SUPPORTED_SERIES_CODE_SYSTEM_NAME = "SUPPORTED_SERIES";
    private static final String SUPPORTED_SCHEDULE_FLAGS_CODE_SYSTEM_NAME = "SUPPORTED_SCHEDULE_FLAGS";
    private static final String IMMUNIZATION_SUBPOTENT_REASON_CODE_SYSTEM = "IMMUNIZATION_SUBPOTENT_REASON";

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
    public void readsNumberOfDosesInSeriesFromSeriesPlanDefinitions()
    {
        final SupportingDataService supportingDataService = new SupportingDataService(cdsEngineProperties, iceProperties);
        final String kmId = supportingDataService.getKmIdFromKnowledgeBaseUrl(
                "https://terminology.cdsframework.org/PlanDefinition/ice-forecast|1.0.0");
        assertEquals(2, supportingDataService.getNumberOfDosesInSeries(kmId, "HPV_2_DOSE_SERIES"));
        assertEquals(3, supportingDataService.getNumberOfDosesInSeries(kmId, "ZOSTER_3_DOSE_SERIES"));
        assertNull(supportingDataService.getNumberOfDosesInSeries(kmId, "NO_SUCH_SERIES"));
    }

    @Test
    public void derivesTargetDiseasesFromMaintainedVaccineCvxMappings()
    {
        final SupportingDataService supportingDataService = new SupportingDataService(cdsEngineProperties, iceProperties);
        final String kmId = supportingDataService.getKmIdFromKnowledgeBaseUrl(
                "https://terminology.cdsframework.org/PlanDefinition/ice-forecast|1.0.0");
        final Map<String, Set<String>> expectedDiseases =
                Map.of("03", Set.of("14189004", "36989005", "36653000"), "04", Set.of("14189004", "36653000"), "01",
                        Set.of("397428000", "27836007", "76902006"), "28", Set.of("397428000", "76902006"), "10",
                        Set.of("398102009"));
        for (final Map.Entry<String, Set<String>> entry : expectedDiseases.entrySet())
        {
            final List<CodeableConcept> diseases = supportingDataService.getDiseasesForVaccine(kmId, entry.getKey());
            assertEquals(entry.getValue().size(), diseases.size(), "Disease count for CVX " + entry.getKey());
            assertTrue(diseases.stream()
                    .allMatch(disease -> disease.coding().size() == 1 && "http://snomed.info/sct".equals(
                            disease.coding().getFirst().system())));
            assertEquals(entry.getValue(),
                    diseases.stream().map(disease -> disease.coding().getFirst().code()).collect(Collectors.toSet()),
                    "Disease codes for CVX " + entry.getKey());
        }
        assertTrue(supportingDataService.getDiseasesForVaccine(kmId, "999").isEmpty());
    }

    @Test
    public void everyEvaluableVaccineComponentMapsEachOfItsDiseases()
    {
        // FHIR evaluations take their target diseases from the evaluated component CVX: a vaccine without components is
        // evaluated as itself, and a combination vaccine as each of its components.
        final SupportingDataService supportingDataService = new SupportingDataService(cdsEngineProperties, iceProperties);
        final String kmId = supportingDataService.getKmIdFromKnowledgeBaseUrl(
                "https://terminology.cdsframework.org/PlanDefinition/ice-forecast|1.0.0");
        final List<CodeSystemConcept> vaccines = cdsEngineProperties.getKnowledgeBaseDefinitionMap()
                .get("https://terminology.cdsframework.org/PlanDefinition/ice-forecast|1.0.0")
                .codeSystems()
                .get("SUPPORTED_VACCINES")
                .concept();
        final Predicate<CodeSystemConceptProperty> isComponent = property -> "vaccineComponent".equals(property.code());
        final Set<String> components = new TreeSet<>();
        for (final CodeSystemConcept vaccine : vaccines)
        {
            final List<CodeSystemConceptProperty> properties = Optional.ofNullable(vaccine.property()).orElse(List.of());
            if (properties.stream().noneMatch(isComponent))
                components.add(vaccine.code());
            properties.stream().filter(isComponent).map(property -> property.valueCoding().code()).forEach(components::add);
        }

        assertTrue(components.size() > 50, "Evaluable components: " + components.size());

        final Map<String, CodeSystemConcept> vaccinesByCode =
                vaccines.stream().collect(Collectors.toMap(CodeSystemConcept::code, vaccine -> vaccine, (first, _) -> first));
        final List<String> problems = new java.util.ArrayList<>();
        for (final String component : components)
        {
            final CodeSystemConcept vaccine = vaccinesByCode.get(component);
            if (vaccine == null)
            {
                problems.add("component CVX %s is not a supported vaccine".formatted(component));
                continue;
            }
            final long declared = Optional.ofNullable(vaccine.property())
                    .orElse(List.of())
                    .stream()
                    .filter(property -> "diseaseImmunity".equals(property.code()))
                    .count();
            final int mapped = supportingDataService.getDiseasesForVaccine(kmId, component).size();
            if (declared == 0 || mapped != declared)
                problems.add("CVX %s declares %d diseaseImmunity values and maps %d".formatted(component, declared, mapped));
        }
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    public void testLoadFullSupportingData_CommonAndNYC()
    {
        assertNotNull(iceProperties, "IceProperties should be injected");
        assertTrue(iceProperties.getScheduleFlags().contains("HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID"));
        assertTrue(iceProperties.getScheduleFlags().contains("POLIO_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID"));
        final SupportingDataService supportingDataService = new SupportingDataService(cdsEngineProperties, iceProperties);
        final String baseKnowledgeModuleId = supportingDataService.getBaseKnowledgeBaseId();
        final String nycKnowledgeModuleId = supportingDataService.getKmIdFromKnowledgeBaseUrl(
                "https://terminology.cdsframework.org/PlanDefinition/ice-forecast|1.0.0");

        final ICESupportingDataConfiguration configuration = assertDoesNotThrow(
                () -> new ICESupportingDataConfiguration(baseKnowledgeModuleId, List.of(nycKnowledgeModuleId),
                        supportingDataService));
        assertNotNull(configuration.getSupportedCdsLists());
        assertFalse(configuration.getSupportedCdsLists().isEmpty());
        assertFalse(configuration.getSupportedSeries().isEmpty());
        final Vaccine cvx121 = configuration.getSupportedVaccines().getVaccineItem("SUPPORTED_VACCINES.121").getVaccine();
        assertTrue(cvx121.isValidInMultipleVaccineGroups());
        assertEquals(Set.of("SUPPORTED_DISEASE_CONCEPT.ZOSTER", "SUPPORTED_DISEASE_CONCEPT.VARICELLA"),
                cvx121.getAllDiseasesTargetedForImmunity());
        final Set<String> configuredSeasonCodes = configuration.getSupportedSeasons()
                .getCopyOfAllSeasons()
                .stream()
                .map(org.cdsframework.ice.service.Season::getSeasonName)
                .map(FullSupportingDataLoadTest::normalizeSeasonCode)
                .filter(Predicate.not(String::isBlank))
                .collect(Collectors.toCollection(TreeSet::new));
        final Set<String> referencedSeasonCodes = configuration.getSupportedSeries()
                .getCopyOfAllSeriesRules()
                .stream()
                .map(org.cdsframework.ice.service.SeriesRules::getSeasons)
                .filter(java.util.Objects::nonNull)
                .flatMap(java.util.Collection::stream)
                .map(org.cdsframework.ice.service.Season::getSeasonName)
                .map(FullSupportingDataLoadTest::normalizeSeasonCode)
                .filter(Predicate.not(String::isBlank))
                .collect(Collectors.toCollection(TreeSet::new));
        final Set<String> missingSeasonCodes = new TreeSet<>(configuredSeasonCodes);
        missingSeasonCodes.removeAll(referencedSeasonCodes);
        assertTrue(missingSeasonCodes.isEmpty(),
                "Supported seasons not referenced by series data: " + String.join(", ", missingSeasonCodes));

        final CdsEngineProperties.KnowledgeBaseDefinition nycModule =
                supportingDataService.getSupportingKnowledgeBaseByKmId(nycKnowledgeModuleId);
        final CodeSystem supportedScheduleFlags = Optional.ofNullable(nycModule.codeSystems())
                .map(codeSystems -> codeSystems.get(SUPPORTED_SCHEDULE_FLAGS_CODE_SYSTEM_NAME))
                .orElse(null);
        assertNotNull(supportedScheduleFlags, "SUPPORTED_SCHEDULE_FLAGS code system should be loaded");
        assertEquals("https://terminology.cdsframework.org/ice/schedule-flags", supportedScheduleFlags.url());
        final Set<String> supportedScheduleFlagCodes = Optional.ofNullable(supportedScheduleFlags.concept())
                .orElse(List.of())
                .stream()
                .map(CodeSystemConcept::code)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        assertTrue(supportedScheduleFlagCodes.contains("HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID"));
        assertTrue(supportedScheduleFlagCodes.contains("POLIO_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID"));

        final CodeSystem subpotentReasonCodeSystem = Optional.ofNullable(nycModule.codeSystems())
                .map(codeSystems -> codeSystems.get(IMMUNIZATION_SUBPOTENT_REASON_CODE_SYSTEM))
                .orElse(null);
        assertNotNull(subpotentReasonCodeSystem, "Expected subpotent reason CodeSystem to be loaded");
        assertTrue(Optional.ofNullable(subpotentReasonCodeSystem.concept())
                .orElse(List.of())
                .stream()
                .map(CodeSystemConcept::code)
                .anyMatch("coldchainbreak"::equals), "Expected subpotent reason CodeSystem to include coldchainbreak");

        final Set<String> supportedSeriesCodes = Optional.ofNullable(nycModule.codeSystems())
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
        final Set<String> seriesPlanDefinitionNames = Optional.ofNullable(nycModule.planDefinitions())
                .orElse(java.util.Map.of())
                .entrySet()
                .stream()
                .filter(entry -> seriesConsumer.isSeriesPlanDefinition(entry.getValue()))
                .filter(entry -> entry.getKey() != null)
                .map(java.util.Map.Entry::getKey)
                .map(String::trim)
                .collect(Collectors.toCollection(TreeSet::new));
        final Set<String> mapKeyNameMismatches = Optional.ofNullable(nycModule.planDefinitions())
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
                .filter(Predicate.not(String::isBlank))
                .collect(Collectors.toCollection(TreeSet::new));
        final Set<String> supportedSeriesMissingFromSeriesData = new TreeSet<>(supportedSeriesCodes);
        supportedSeriesMissingFromSeriesData.removeAll(loadedSeriesCodes);
        assertTrue(supportedSeriesMissingFromSeriesData.isEmpty(),
                "SUPPORTED_SERIES concepts code(s) missing from series data: " + String.join(", ",
                        supportedSeriesMissingFromSeriesData));
    }
}
