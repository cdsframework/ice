package org.cdsframework.ice.api;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.cdsframework.ice.dto.CodeSystem;
import org.cdsframework.ice.config.IceSupportingDataProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/supporting-data")
public class SupportingDataController
{
    @Builder
    public record KnowledgeModule(String scopingEntityId,
                                  String businessId,
                                  String version)
    {
        public static KnowledgeModule parse(final String kmId)
        {
            return Pattern.compile("\\^")
                    .splitAsStream(kmId)
                    .collect(Collectors.collectingAndThen(Collectors.toList(), parts -> KnowledgeModule.builder()
                            .scopingEntityId(parts.getFirst())
                            .businessId(parts.get(1))
                            .version(parts.get(2))
                            .build()));
        }
    }

    private static String kmId(final String scopingEntityId, final String businessId, final String version)
    {
        return "%s^%s^%s".formatted(scopingEntityId, businessId, version);
    }

    private final IceSupportingDataProperties iceSupportingDataProperties;

    @GetMapping
    public IceSupportingDataProperties supportingData()
    {
        return iceSupportingDataProperties;
    }

    @GetMapping("/knowledge-modules")
    public List<KnowledgeModule> knowledgeModules()
    {
        return iceSupportingDataProperties.getKnowledgeModules().keySet().stream().map(KnowledgeModule::parse).toList();
    }

    @GetMapping("/knowledge-module")
    public IceSupportingDataProperties.KnowledgeModule supportingData(@RequestParam final String scopingEntityId,
            @RequestParam final String businessId, @RequestParam final String version)
    {
        return iceSupportingDataProperties.getKnowledgeModules().get(kmId(scopingEntityId, businessId, version));
    }

    @GetMapping("/diseases")
    public CodeSystem diseases(@RequestParam final String scopingEntityId, @RequestParam final String businessId,
            @RequestParam final String version)
    {
        return iceSupportingDataProperties.getKnowledgeModules()
                .get(kmId(scopingEntityId, businessId, version))
                .codeSystems()
                .get("SUPPORTED_DISEASE_CONCEPT");
    }

    @GetMapping("/vaccine-groups")
    public CodeSystem vaccineGroups(@RequestParam final String scopingEntityId, @RequestParam final String businessId,
            @RequestParam final String version)
    {
        return iceSupportingDataProperties.getKnowledgeModules()
                .get(kmId(scopingEntityId, businessId, version))
                .codeSystems()
                .get("VACCINE_GROUP_CONCEPT");
    }

    @GetMapping("/vaccines")
    public CodeSystem vaccines(@RequestParam final String scopingEntityId, @RequestParam final String businessId,
            @RequestParam final String version)
    {
        return iceSupportingDataProperties.getKnowledgeModules()
                .get(kmId(scopingEntityId, businessId, version))
                .codeSystems()
                .get("SUPPORTED_VACCINES");
    }

    @GetMapping("/seasons")
    public CodeSystem seasons(@RequestParam final String scopingEntityId, @RequestParam final String businessId,
            @RequestParam final String version)
    {
        return iceSupportingDataProperties.getKnowledgeModules()
                .get(kmId(scopingEntityId, businessId, version))
                .codeSystems()
                .get("SUPPORTED_SEASON");
    }
}
