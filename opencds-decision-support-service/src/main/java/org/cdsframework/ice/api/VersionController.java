package org.cdsframework.ice.api;

import org.cdsframework.ice.service.VersionData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Health REST Web Service
 *
 * @author sdn
 */
@RequiredArgsConstructor
@Slf4j
@RestController
public class VersionController
{
    private final VersionData versionData;

    /**
     * Returns version details.
     */
    @GetMapping("/version")
    public VersionData version()
    {
        log.debug("/version called!");
        return versionData;
    }
}
