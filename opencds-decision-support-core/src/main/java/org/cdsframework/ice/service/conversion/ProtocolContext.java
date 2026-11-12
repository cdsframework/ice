package org.cdsframework.ice.service.conversion;

import java.util.List;

import org.cdsframework.fhir.CodeableConcept;
import org.cdsframework.fhir.Extension;

/**
 * Series output for one evaluation or recommendation: the coded FHIR series, season, and series size, the engine's count of
 * doses remaining, and the display text the description uses.
 */
record ProtocolContext(SeriesSelectionInfo seriesSelectionInfo,
                       SeasonInfo seasonInfo,
                       String seriesDisplay,
                       CodeableConcept series,
                       CodeableConcept season,
                       CodeableConcept seriesDoses,
                       CodeableConcept seriesDosesRemaining,
                       List<Extension> extensions)
{
    static ProtocolContext withoutSeries(final List<Extension> extensions)
    {
        return new ProtocolContext(null, null, null, null, null, null, null, extensions);
    }
}
