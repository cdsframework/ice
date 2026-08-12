package org.cdsframework.ice.service.conversion;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.cdsframework.fhir.Extension;
import org.cdsframework.fhir.Identifier;
import org.cdsframework.fhir.Reference;
import org.opencds.vmr.v1_0.schema.CD;
import org.opencds.vmr.v1_0.schema.ObservationResult;
import org.opencds.vmr.v1_0.schema.RelatedClinicalStatement;
import org.springframework.stereotype.Component;

@Component
public class ScheduleAuthorityExtensionBuilder
{
    private static final String SCHEDULE_AUTHORITY_FOCUS_CODE = "ICE_VACCINE_GROUP_SCHEDULE_AUTHORITIES";
    private static final String SCHEDULE_AUTHORITY_FOCUS_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.500";
    private static final String SCHEDULE_AUTHORITY_EXTENSION_URL =
            "http://terminology.cdsframework.org/fhir/StructureDefinition/ice-schedule-authority";
    private static final String SCHEDULE_AUTHORITY_IDENTIFIER_SYSTEM =
            "http://terminology.cdsframework.org/ice/schedule-authority";
    private static final String REFERENCE_TYPE_ORGANIZATION = "Organization";

    public List<Extension> build(final List<RelatedClinicalStatement> relatedClinicalStatements)
    {
        return streamObservationResults(relatedClinicalStatements)
                .filter(this::isScheduleAuthorityFocusObservation)
                .map(ObservationResult::getInterpretation)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(this::toExtension)
                .toList();
    }

    private Extension toExtension(final CD interpretation)
    {
        return Extension.builder()
                .url(SCHEDULE_AUTHORITY_EXTENSION_URL)
                .valueReference(Reference.builder()
                        .type(REFERENCE_TYPE_ORGANIZATION)
                        .identifier(Identifier.builder()
                                .system(SCHEDULE_AUTHORITY_IDENTIFIER_SYSTEM)
                                .value(interpretation.getCode())
                                .build())
                        .display(interpretation.getDisplayName())
                        .build())
                .build();
    }

    private Stream<ObservationResult> streamObservationResults(final List<RelatedClinicalStatement> relatedClinicalStatements)
    {
        return Stream.ofNullable(relatedClinicalStatements)
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

    private boolean isScheduleAuthorityFocusObservation(final ObservationResult observationResult)
    {
        return Optional.ofNullable(observationResult)
                .map(ObservationResult::getObservationFocus)
                .filter(observationFocus -> SCHEDULE_AUTHORITY_FOCUS_CODE_SYSTEM.equals(observationFocus.getCodeSystem())
                        && SCHEDULE_AUTHORITY_FOCUS_CODE.equals(observationFocus.getCode()))
                .isPresent();
    }
}