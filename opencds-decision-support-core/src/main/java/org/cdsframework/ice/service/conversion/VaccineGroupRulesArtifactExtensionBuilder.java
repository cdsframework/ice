package org.cdsframework.ice.service.conversion;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.cdsframework.fhir.Attachment;
import org.cdsframework.fhir.Extension;
import org.cdsframework.fhir.RelatedArtifact;
import org.opencds.vmr.v1_0.schema.CD;
import org.opencds.vmr.v1_0.schema.ObservationResult;
import org.opencds.vmr.v1_0.schema.RelatedClinicalStatement;
import org.opencds.vmr.v1_0.schema.ST;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class VaccineGroupRulesArtifactExtensionBuilder
{
    private record VaccineGroupRulesInfo(String url,
                                         String vaccineGroupDisplayName,
                                         String fallbackLabel)
    {
    }

    private static final String VACCINE_GROUP_RULES_ARTIFACT_FOCUS_CODE = "VACCINE_GROUP_RULES_URL";
    private static final String VACCINE_GROUP_RULES_ARTIFACT_FOCUS_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.500";
    private static final String VACCINE_GROUP_FOCUS_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.1";
    private static final String VACCINE_GROUP_RULES_ARTIFACT_EXTENSION_URL =
            "https://terminology.cdsframework.org/ice/StructureDefinition/vaccine-group-rules-artifact";
    private static final String RELATED_ARTIFACT_TYPE_DOCUMENTATION = "documentation";

    Optional<Extension> build(final List<RelatedClinicalStatement> relatedClinicalStatements)
    {
        return extractVaccineGroupRulesInfo(relatedClinicalStatements).map(vaccineGroupRulesInfo -> Extension.builder()
                .url(VACCINE_GROUP_RULES_ARTIFACT_EXTENSION_URL)
                .valueRelatedArtifact(RelatedArtifact.builder()
                        .type(RELATED_ARTIFACT_TYPE_DOCUMENTATION)
                        .label(toRulesLabel(vaccineGroupRulesInfo.vaccineGroupDisplayName(), vaccineGroupRulesInfo.fallbackLabel()))
                        .document(Attachment.builder().contentType("text/html").url(vaccineGroupRulesInfo.url()).build())
                        .build())
                .build());
    }

    private String toRulesLabel(final String vaccineGroupDisplayName, final String fallbackLabel)
    {
        if (StringUtils.hasText(vaccineGroupDisplayName))
            return "ICE %s Rules".formatted(vaccineGroupDisplayName.trim());
        return StringUtils.hasText(fallbackLabel) ? fallbackLabel.trim() : null;
    }

    private Optional<VaccineGroupRulesInfo> extractVaccineGroupRulesInfo(
            final List<RelatedClinicalStatement> relatedClinicalStatements)
    {
        final String vaccineGroupDisplayName =
                streamObservationResults(relatedClinicalStatements).map(ObservationResult::getObservationFocus)
                        .filter(Objects::nonNull)
                        .filter(observationFocus -> VACCINE_GROUP_FOCUS_CODE_SYSTEM.equals(observationFocus.getCodeSystem()))
                        .map(CD::getDisplayName)
                        .filter(StringUtils::hasText)
                        .findFirst()
                        .orElse(null);

        return streamObservationResults(relatedClinicalStatements).filter(this::isVaccineGroupRulesArtifactFocusObservation)
                .map(observationResult ->
                {
                    final String url = Optional.ofNullable(observationResult.getObservationValue())
                            .map(ObservationResult.ObservationValue::getText)
                            .map(ST::getValue)
                            .orElse(null);
                    final String fallbackLabel =
                            Optional.ofNullable(observationResult.getObservationFocus()).map(CD::getDisplayName).orElse(null);
                    return new VaccineGroupRulesInfo(url, vaccineGroupDisplayName, fallbackLabel);
                })
                .filter(info -> StringUtils.hasText(info.url()))
                .findFirst();
    }

    private Stream<ObservationResult> streamObservationResults(final List<RelatedClinicalStatement> relatedClinicalStatements)
    {
        return Optional.ofNullable(relatedClinicalStatements)
                .stream()
                .flatMap(Collection::stream)
                .map(RelatedClinicalStatement::getObservationResult)
                .filter(Objects::nonNull)
                .flatMap(this::streamObservationResultTree);
    }

    private Stream<ObservationResult> streamObservationResultTree(final ObservationResult observationResult)
    {
        return Stream.concat(Stream.of(observationResult),
                streamObservationResults(observationResult.getRelatedClinicalStatement()));
    }

    private boolean isVaccineGroupRulesArtifactFocusObservation(final ObservationResult observationResult)
    {
        return Optional.ofNullable(observationResult)
                .map(ObservationResult::getObservationFocus)
                .filter(observationFocus -> VACCINE_GROUP_RULES_ARTIFACT_FOCUS_CODE.equals(observationFocus.getCode())
                        && VACCINE_GROUP_RULES_ARTIFACT_FOCUS_CODE_SYSTEM.equals(observationFocus.getCodeSystem()))
                .isPresent();
    }
}
