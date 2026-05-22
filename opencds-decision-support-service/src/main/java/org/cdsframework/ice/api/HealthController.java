package org.cdsframework.ice.api;

import org.cdsframework.ice.service.VersionData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping
public class HealthController
{
    private final VersionData versionData;

    /**
     * Returns health status.
     */
    @SuppressWarnings("SameReturnValue")
    @GetMapping("/healthcheck")
    public String healthcheck()
    {
        log.debug("/healthcheck called!");
        return "all's well";
    }

    /**
     * Returns quick up status.
     */
    @GetMapping("/_k8s-health-check")
    public void k8sHealthCheck()
    {
        log.debug("/_k8s-health-check called!");
    }

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
