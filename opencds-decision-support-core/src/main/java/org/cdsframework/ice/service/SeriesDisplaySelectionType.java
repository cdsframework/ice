package org.cdsframework.ice.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SeriesDisplaySelectionType
{
    SERIES_DISPLAY_NOT_SELECTED("SERIES_DISPLAY_NOT_SELECTED"),
    SERIES_DISPLAY_UNAMBIGUOUS("SERIES_DISPLAY_UNAMBIGUOUS"),
    SERIES_DISPLAY_BEST_GUESS("SERIES_DISPLAY_BEST_GUESS"),
    SERIES_DISPLAY_ALTERNATIVE("SERIES_DISPLAY_ALTERNATIVE"),
    SERIES_DISPLAY_NONE("SERIES_DISPLAY_NONE");

    private final String code;
}
