package org.cdsframework.ice.service.conversion;

import java.util.List;

import org.cdsframework.fhir.CodeableConcept;
import org.cdsframework.fhir.Extension;

record ProtocolContext(SeriesSelectionInfo seriesSelectionInfo,
                       SeasonInfo seasonInfo,
                       String series,
                       CodeableConcept seriesDoses,
                       List<Extension> extensions)
{
}