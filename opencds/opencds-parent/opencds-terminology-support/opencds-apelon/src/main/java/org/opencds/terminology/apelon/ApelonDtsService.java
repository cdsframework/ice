package org.opencds.terminology.apelon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.cache.OpencdsCache;
import org.opencds.common.xml.XmlEntity;
import org.opencds.common.xml.XmlHttpSender;
import org.opencds.terminology.apelon.cache.ApelonDtsServiceCacheRegion;
import org.opencds.terminology.apelon.cache.CacheKey;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestHandler;

import com.apelon.dts.client.concept.DTSConcept;

public class ApelonDtsService implements HttpRequestHandler {
    private static Log log = LogFactory.getLog(ApelonDtsService.class);
    
    private static final List<MediaType> ACCEPTABLE_TYPES = Arrays.asList(MediaType.APPLICATION_XML, MediaType.TEXT_XML);
    RequestParser requestParser = new XmlRequestParser();
    
    private ApelonDtsClient apelonDtsClient;
    private XmlHttpSender xmlHttpSender;
    
    private OpencdsCache cache;
    
    private final ForkJoinPool refreshPool = new ForkJoinPool(32, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
    
    /**
     * Finds a concept within apelon namespace, searching by name
     * 
     * @param name of concept
     * @return {@link DTSConcept} or null if not found
     * @throws Exception on failed connection or Apelon internal error
     */
    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        XmlEntity responseEntity = null;
        
        final String contentType = request.getContentType();
        if (contentType == null || (!contentType.contains(MediaType.TEXT_XML_VALUE) && !contentType.contains(MediaType.APPLICATION_XML_VALUE))) {            
            throw new HttpMediaTypeNotAcceptableException(ACCEPTABLE_TYPES);
        }

        Map<String, String> params = requestParser.getParameters(request.getInputStream());
        CacheKey key = CacheKey.create(params);
        XmlEntity cachedResponseEntity = cache.get(ApelonDtsServiceCacheRegion.RESPONSE, key);
        if (cachedResponseEntity != null) {
            log.debug("Found cached response for key= " + key.toString());
            responseEntity = cachedResponseEntity;
        } else {
            log.debug("No cache response for key; querying... key= " + key.toString());
            String queryType = params.get("queryType");
            ApelonDtsQuery adq = ApelonDtsQuery.resolve(queryType);
            if (adq == null) {
                responseEntity = getErrorEntity("queryType: " + queryType
                        + " is invalid. Valid query types include:\n");
                // TODO FIXME What are valid query types?
            }
            try {
                responseEntity = adq.execute(apelonDtsClient, params);
                cache.put(ApelonDtsServiceCacheRegion.RESPONSE, key, responseEntity);
                log.debug("Added response to cache for key= " + key.toString());
            } catch (ApelonClientException e) {
                responseEntity = getErrorEntity(e);
            }
        }
        xmlHttpSender.sendXmlResponse(response, responseEntity);
    }
    
    public void refreshCache() {
        log.debug("Cache refresh started...");
        Set<CacheKey> cacheKeys = cache.getCacheKeys(ApelonDtsServiceCacheRegion.RESPONSE);
        List<RefreshTask> tasks = new ArrayList<>();
        for (CacheKey key : cacheKeys) {
            RefreshTask task = new RefreshTask(apelonDtsClient, cache, key);
            tasks.add(task);
            refreshPool.execute(task);
        }
        for (RefreshTask task : tasks) {
            task.join();
        }
        log.debug("Cache refresh complete.");
    }
    
    static class RefreshTask extends ForkJoinTask<CacheKey> {
        private static final long serialVersionUID = 1L;
        
        private ApelonDtsClient apelonDtsClient;
        private OpencdsCache cache;
        private CacheKey cacheKey;
        
        public RefreshTask(ApelonDtsClient apelonDtsClient, OpencdsCache cache, CacheKey cacheKey) {
            this.apelonDtsClient = apelonDtsClient;
            this.cache = cache;
            this.cacheKey = cacheKey;
        }
        
        @Override
        public CacheKey getRawResult() {
            return cacheKey;
        }

        @Override
        protected void setRawResult(CacheKey cacheKey) {
            this.cacheKey = cacheKey;
        }

        @Override
        protected boolean exec() {
            Map<String, String> params = cacheKey.getParams();
            String queryType = params.get("queryType");
            ApelonDtsQuery adq = ApelonDtsQuery.resolve(queryType);
            if (adq != null) {
                XmlEntity responseEntity;
                try {
                    responseEntity = adq.execute(apelonDtsClient, params);
                    cache.put(ApelonDtsServiceCacheRegion.RESPONSE, cacheKey, responseEntity);
                    log.debug("Refreshed cache entry for key= " + cacheKey);
                } catch (ApelonClientException e) {
                    log.error("Was unable to refresh cache entry for key= " + cacheKey);
                    log.error(e.getMessage(),e);
                }
            }
            return true;
        }
        
    }
    
    /**
     * Creates and returns an XmlEntity containing the exception message and stack trace
     * 
     * @param response 
     * @param e The exception to be transformed into an error {@link XmlEntity}.
     */
    private XmlEntity getErrorEntity(Exception e) throws IOException {
        return getErrorEntity(e.getMessage());
    }

    private XmlEntity getErrorEntity(String message) throws IOException {
        XmlEntity rootEntity = new XmlEntity("ApelonDtsServiceErrorResponse");
        XmlEntity errorEntity = new XmlEntity("Error", message, false);
        rootEntity.addChild(errorEntity);
        return rootEntity;
    }

    public void setXmlHttpSender(XmlHttpSender xmlHttpSender) {
        this.xmlHttpSender = xmlHttpSender;
    }

    public void setApelonDtsClient(ApelonDtsClient apelonDtsClient) {
        this.apelonDtsClient = apelonDtsClient;
    }
    
    public void setCache(OpencdsCache cache) {
        this.cache = cache;
    }
    
}
