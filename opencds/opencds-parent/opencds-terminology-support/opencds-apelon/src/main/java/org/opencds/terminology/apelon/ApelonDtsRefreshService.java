package org.opencds.terminology.apelon;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.HttpRequestHandler;

public class ApelonDtsRefreshService implements HttpRequestHandler {
    private static Log log = LogFactory.getLog(ApelonDtsRefreshService.class);

    private ApelonDtsService apelonDtsService;
    
    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException,
            IOException {
        log.debug("Refreshing cache in ApelonDtsService...");
        apelonDtsService.refreshCache();
        log.debug("ApelonDtsService cache refreshed...");
        response.getWriter().write("Cache Refreshed");
    }

    public void setApelonDtsService(ApelonDtsService apelonDtsService) {
        this.apelonDtsService = apelonDtsService;
    }
    
}
