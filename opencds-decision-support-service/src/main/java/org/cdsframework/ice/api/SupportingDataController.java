package org.cdsframework.ice.api;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.cdsframework.ice.config.IceSupportingDataProperties;
import org.cdsframework.ice.config.iceSupportingProperties.SeriesData;
import org.cdsframework.ice.dto.CodeSystem;
import org.cdsframework.ice.supportingdata.ICEConceptType;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/supporting-data")
public class SupportingDataController
{
    public record KnowledgeModule(String scopingEntityId,
                                  String businessId,
                                  String version)
    {
        private static final Pattern KM_ID_SEPARATOR = Pattern.compile("\\^");

        public static KnowledgeModule parse(final String kmId)
        {
            final String[] parts = KM_ID_SEPARATOR.split(kmId, -1);
            if (parts.length != 3)
            {
                throw new IllegalArgumentException(
                        "Invalid knowledge module id '%s'. Expected 'scopingEntityId^businessId^version'.".formatted(kmId));
            }
            return new KnowledgeModule(parts[0], parts[1], parts[2]);
        }
    }

    private static final Set<String> CORE_CODE_SYSTEMS =
            Set.of(ICEConceptType.DISEASE.getIceConceptTypeValue(), ICEConceptType.VACCINE_GROUP.getIceConceptTypeValue(),
                    ICEConceptType.VACCINE.getIceConceptTypeValue(), ICEConceptType.SEASON.getIceConceptTypeValue(),
                    ICEConceptType.SERIES.getIceConceptTypeValue());

    private static String kmId(final String scopingEntityId, final String businessId, final String version)
    {
        return "%s^%s^%s".formatted(scopingEntityId, businessId, version);
    }

    private final IceSupportingDataProperties iceSupportingDataProperties;

    private IceSupportingDataProperties.KnowledgeModule requireKnowledgeModule(final String scopingEntityId,
            final String businessId, final String version)
    {
        final String kmId = kmId(scopingEntityId, businessId, version);
        final Map<String, IceSupportingDataProperties.KnowledgeModule> knowledgeModules =
                iceSupportingDataProperties.getKnowledgeModules();
        if (knowledgeModules == null)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Supporting data is not initialized.");
        }
        final IceSupportingDataProperties.KnowledgeModule knowledgeModule = knowledgeModules.get(kmId);
        if (knowledgeModule == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Knowledge module '%s' not found. Use /supporting-data/knowledge-modules to list available modules.".formatted(
                            kmId));
        }
        return knowledgeModule;
    }

    private CodeSystem requireCodeSystem(final IceSupportingDataProperties.KnowledgeModule knowledgeModule,
            final String codeSystemName)
    {
        final Map<String, CodeSystem> codeSystems = knowledgeModule.codeSystems();
        if (codeSystems == null)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Knowledge module has no code systems configured.");
        }
        final CodeSystem codeSystem = codeSystems.get(codeSystemName);
        if (codeSystem == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Code system '%s' not found for the requested knowledge module.".formatted(codeSystemName));
        }
        return codeSystem;
    }

    private CodeSystem lookupCodeSystem(final String scopingEntityId, final String businessId, final String version,
            final String codeSystemName)
    {
        return requireCodeSystem(requireKnowledgeModule(scopingEntityId, businessId, version), codeSystemName);
    }

    private Map<String, CodeSystem> requireCodeSystems(final IceSupportingDataProperties.KnowledgeModule knowledgeModule)
    {
        final Map<String, CodeSystem> codeSystems = knowledgeModule.codeSystems();
        if (codeSystems == null)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Knowledge module has no code systems configured.");
        }
        return codeSystems;
    }

    private Map<String, SeriesData> requireSeries(final IceSupportingDataProperties.KnowledgeModule knowledgeModule)
    {
        final Map<String, SeriesData> series = knowledgeModule.series();
        if (series == null)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Knowledge module has no series configured.");
        }
        return series;
    }

    @GetMapping
    public IceSupportingDataProperties supportingData()
    {
        return iceSupportingDataProperties;
    }

    @GetMapping("/knowledge-modules")
    public List<KnowledgeModule> knowledgeModules()
    {
        final Map<String, IceSupportingDataProperties.KnowledgeModule> knowledgeModules =
                iceSupportingDataProperties.getKnowledgeModules();
        if (knowledgeModules == null)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Supporting data is not initialized.");
        }
        return knowledgeModules.keySet().stream().sorted().map(KnowledgeModule::parse).toList();
    }

    @GetMapping("/knowledge-module")
    public IceSupportingDataProperties.KnowledgeModule supportingData(@RequestParam @NotBlank final String scopingEntityId,
            @RequestParam @NotBlank final String businessId, @RequestParam @NotBlank final String version)
    {
        return requireKnowledgeModule(scopingEntityId, businessId, version);
    }

    @GetMapping("/diseases")
    public CodeSystem diseases(@RequestParam @NotBlank final String scopingEntityId,
            @RequestParam @NotBlank final String businessId, @RequestParam @NotBlank final String version)
    {
        return lookupCodeSystem(scopingEntityId, businessId, version, ICEConceptType.DISEASE.getIceConceptTypeValue());
    }

    @GetMapping("/vaccine-groups")
    public CodeSystem vaccineGroups(@RequestParam @NotBlank final String scopingEntityId,
            @RequestParam @NotBlank final String businessId, @RequestParam @NotBlank final String version)
    {
        return lookupCodeSystem(scopingEntityId, businessId, version, ICEConceptType.VACCINE_GROUP.getIceConceptTypeValue());
    }

    @GetMapping("/vaccines")
    public CodeSystem vaccines(@RequestParam @NotBlank final String scopingEntityId,
            @RequestParam @NotBlank final String businessId, @RequestParam @NotBlank final String version)
    {
        return lookupCodeSystem(scopingEntityId, businessId, version, ICEConceptType.VACCINE.getIceConceptTypeValue());
    }

    @GetMapping("/seasons")
    public CodeSystem seasons(@RequestParam @NotBlank final String scopingEntityId, @RequestParam @NotBlank final String businessId,
            @RequestParam @NotBlank final String version)
    {
        return lookupCodeSystem(scopingEntityId, businessId, version, ICEConceptType.SEASON.getIceConceptTypeValue());
    }

    @GetMapping("/series")
    public CodeSystem series(@RequestParam @NotBlank final String scopingEntityId, @RequestParam @NotBlank final String businessId,
            @RequestParam @NotBlank final String version)
    {
        return lookupCodeSystem(scopingEntityId, businessId, version, ICEConceptType.SERIES.getIceConceptTypeValue());
    }

    @GetMapping("/series-data")
    public Map<String, SeriesData> seriesData(@RequestParam @NotBlank final String scopingEntityId,
            @RequestParam @NotBlank final String businessId, @RequestParam @NotBlank final String version)
    {
        return requireSeries(requireKnowledgeModule(scopingEntityId, businessId, version));
    }

    @GetMapping("/code-system")
    public CodeSystem codeSystem(@RequestParam @NotBlank final String scopingEntityId,
            @RequestParam @NotBlank final String businessId, @RequestParam @NotBlank final String version,
            @RequestParam @NotBlank final String name)
    {
        return lookupCodeSystem(scopingEntityId, businessId, version, name);
    }

    @GetMapping("/other-code-systems")
    public Map<String, CodeSystem> otherCodeSystems(@RequestParam @NotBlank final String scopingEntityId,
            @RequestParam @NotBlank final String businessId, @RequestParam @NotBlank final String version)
    {
        return requireCodeSystems(requireKnowledgeModule(scopingEntityId, businessId, version)).entrySet()
                .stream()
                .filter(entry -> !CORE_CODE_SYSTEMS.contains(entry.getKey()))
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (left, _) -> left,
                        java.util.LinkedHashMap::new));
    }
}
