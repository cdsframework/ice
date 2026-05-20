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
            "http://terminology.cdsframework.org/fhir/StructureDefinition/immunization-selection-context";
    private static final String SELECTION_CONTEXT_CHILD_URL_SELECTED_SERIES = "selectedSeries";
    private static final String SELECTION_CONTEXT_CHILD_URL_SERIES_SELECTION_TYPE = "seriesSelectionType";
    private static final String SELECTION_CONTEXT_CHILD_URL_SELECTED_SEASON = "selectedSeason";
    private static final String SERIES_SELECTION_TYPE_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.501";
    private static final String SERIES_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.10";

    private final SupportingDataService supportingDataService;

    public SelectionContextExtensionBuilder(final SupportingDataService supportingDataService)
    {
        this.supportingDataService = supportingDataService;
    }

    Optional<Extension> build(final String kmId, final SeriesSelectionInfo seriesSelectionInfo, final SeasonInfo seasonInfo)
    {
        if (seriesSelectionInfo == null)
            return Optional.empty();

        final List<Extension> nestedExtensions = new ArrayList<>(3);

        nestedExtensions.add(Extension.builder()
                .url(SELECTION_CONTEXT_CHILD_URL_SELECTED_SERIES)
                .valueCodeableConcept(CodeableConcept.builder()
                        .coding(Coding.builder()
                                .system(supportingDataService.toFhirCodeSystemUrl(kmId, SERIES_CODE_SYSTEM))
                                .code(seriesSelectionInfo.seriesCode())
                                .display(seriesSelectionInfo.seriesDisplay())
                                .build())
                        .text(seriesSelectionInfo.seriesDisplay())
                        .build())
                .build());

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

        if (seasonInfo != null)
        {
            nestedExtensions.add(Extension.builder()
                    .url(SELECTION_CONTEXT_CHILD_URL_SELECTED_SEASON)
                    .valueCodeableConcept(CodeableConcept.builder()
                            .coding(Coding.builder()
                                    .system(supportingDataService.toFhirCodeSystemUrl(kmId, seasonInfo.seasonCodeSystem()))
                                    .code(seasonInfo.seasonCode())
                                    .display(seasonInfo.seasonDisplay())
                                    .build())
                            .text(seasonInfo.seasonDisplay())
                            .build())
                    .build());
        }

        return Optional.of(Extension.builder().url(SELECTION_CONTEXT_EXTENSION_URL).extension(nestedExtensions).build());
    }
}
