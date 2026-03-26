package org.cdsframework.ice.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Health REST Web Service
 *
 * @author sdn
 */
@Slf4j
@RestController
public class HealthController
{
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
}
