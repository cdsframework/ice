package org.cdsframework.ice.service.conversion;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.cdsframework.fhir.CodeableConcept;
import org.cdsframework.fhir.Coding;
import org.cdsframework.fhir.Extension;
import org.cdsframework.ice.service.SupportingDataService;
import org.springframework.stereotype.Component;

@Component
public class SelectionContextExtensionBuilder
{
    private static final String SELECTION_CONTEXT_EXTENSION_URL =
            "https://terminology.cdsframework.org/ice/StructureDefinition/immunization-selection-context";
    private static final String SELECTION_CONTEXT_CHILD_URL_SERIES_SELECTION_TYPE = "seriesSelectionType";
    private static final String SERIES_SELECTION_TYPE_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.501";
    private static final String SERIES_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.10";

    private final SupportingDataService supportingDataService;

    public SelectionContextExtensionBuilder(final SupportingDataService supportingDataService)
    {
        this.supportingDataService = supportingDataService;
    }

    // The season is the resources' own season element; seasonInfo is no longer part of this extension.
    Optional<Extension> build(final String kmId, final SeriesSelectionInfo seriesSelectionInfo, final SeasonInfo seasonInfo)
    {
        if (seriesSelectionInfo == null)
            return Optional.empty();

        final List<Extension> nestedExtensions = new ArrayList<>(1);

        final String selectionTypeDisplay = Optional.ofNullable(
                        supportingDataService.getSeriesSelectionTypeDisplayName(kmId, seriesSelectionInfo.selectionTypeCode()))
                .orElseThrow(() -> new IllegalStateException(
                        "Missing display for series selection type '%s' in SERIES_DISPLAY_SELECTION_TYPE".formatted(
                                seriesSelectionInfo.selectionTypeCode())));
        nestedExtensions.add(Extension.builder()
                .url(SELECTION_CONTEXT_CHILD_URL_SERIES_SELECTION_TYPE)
                .valueCodeableConcept(CodeableConcept.builder()
                        .coding(Coding.builder()
                                .system(supportingDataService.toFhirCodeSystemUrl(kmId, SERIES_SELECTION_TYPE_CODE_SYSTEM))
                                .code(seriesSelectionInfo.selectionTypeCode())
                                .display(selectionTypeDisplay)
                                .build())
                        .text(selectionTypeDisplay)
                        .build())
                .build());

        return Optional.of(Extension.builder().url(SELECTION_CONTEXT_EXTENSION_URL).extension(nestedExtensions).build());
    }

    CodeableConcept buildSelectedSeries(final String kmId, final SeriesSelectionInfo seriesSelectionInfo)
    {
        if (seriesSelectionInfo == null)
            return null;
        return CodeableConcept.builder()
                .coding(Coding.builder()
                        .system(supportingDataService.toFhirCodeSystemUrl(kmId, SERIES_CODE_SYSTEM))
                        .code(seriesSelectionInfo.seriesCode())
                        .display(seriesSelectionInfo.seriesDisplay())
                        .build())
                .text(seriesSelectionInfo.seriesDisplay())
                .build();
    }

    CodeableConcept buildSelectedSeason(final String kmId, final SeasonInfo seasonInfo)
    {
        if (seasonInfo == null)
            return null;
        return CodeableConcept.builder()
                .coding(Coding.builder()
                        .system(supportingDataService.toFhirCodeSystemUrl(kmId, seasonInfo.seasonCodeSystem()))
                        .code(seasonInfo.seasonCode())
                        .display(seasonInfo.seasonDisplay())
                        .build())
                .text(seasonInfo.seasonDisplay())
                .build();
    }
}
