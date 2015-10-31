package org.opencds.config.migrate;

import java.io.OutputStream;

import groovy.util.logging.Log4j;

import org.apache.commons.lang3.tuple.Pair
import org.opencds.config.api.model.KMId
import org.opencds.config.client.rest.ConceptDeterminationMethodRestClient
import org.opencds.config.client.rest.ExecutionEngineRestClient
import org.opencds.config.client.rest.KnowledgeModuleRestClient
import org.opencds.config.client.rest.RestClient
import org.opencds.config.client.rest.SemanticSignifierRestClient
import org.opencds.config.mapper.KMIdMapper
import org.opencds.config.schema.ConceptDeterminationMethod;
import org.opencds.config.schema.ConceptDeterminationMethods
import org.opencds.config.schema.ExecutionEngine
import org.opencds.config.schema.ExecutionEngines
import org.opencds.config.schema.KnowledgeModule;
import org.opencds.config.schema.KnowledgeModules
import org.opencds.config.schema.SemanticSignifier;
import org.opencds.config.schema.SemanticSignifiers
import org.opencds.config.util.URIUtil

@Log4j
public class RestConfigHandler implements ConfigHandler {

    Closure client

    public RestConfigHandler(Closure client) {
        this.client = client
    }

    public static RestConfigHandler createForCDM(RestClient restClient, boolean splitCDM) {
        def cdmClient = new ConceptDeterminationMethodRestClient(restClient)
        new RestConfigHandler({arg ->
            if (arg instanceof ConceptDeterminationMethod) {
                cdmClient.putConceptDeterminationMethod(URIUtil.buildCDMIdString(arg), arg)
            } else if (arg instanceof ConceptDeterminationMethods) {
                cdmClient.putConceptDeterminationMethods(arg)
            }
        })
    }

    public static createForEE(RestClient client) {
        def eeClient = new ExecutionEngineRestClient(client)
        new RestConfigHandler({ExecutionEngines ees ->
            eeClient.putExecutionEngines(ees)
        })
    }

    public static RestConfigHandler createForSS(RestClient client) {
        def ssClient = new SemanticSignifierRestClient(client)
        new RestConfigHandler({SemanticSignifiers sses ->
            ssClient.putSemanticSignifiers(sses)
        })
    }

    public static RestConfigHandler createForKM(RestClient client) {
        def kmClient = new KnowledgeModuleRestClient(client)
        new RestConfigHandler({KnowledgeModules kms ->
            kmClient.putKnowledgeModules(kms)
        })
    }

    public static Map<Pair<KMId, String>, RestConfigHandler> createForKP(RestClient restClient, List<Pair<KMId, String>> pairs) {
        Map<Pair<KMId, String>, RestConfigHandler> rsbs = new HashMap<>()
        def kpClient = new KnowledgeModuleRestClient(restClient)
        for (Pair<KMId, String> pair : pairs) {
            KMId kmid = pair.getLeft();
            log.debug("Building RestConfigHandler for pair: " + kmid)
            rsbs.put(pair, new RestConfigHandler({InputStream inputStream ->
                log.debug("Creating configHandler for KMId: " + kmid)
                kpClient.putKnowledgePackage(URIUtil.buildKMIdString(KMIdMapper.external(kmid)), inputStream)
            }))
        }
        return rsbs
    }

    @Override
    public OutputStream getOutputStream() throws Exception {
        return null;
    }

    @Override
    public OutputStream getOutputStream(String baseName) throws Exception {
        return null;
    }

    @Override
    public <T> void write(T t) throws Exception {
        log.debug("this.hashCode = " + this.hashCode())
        client(t)
    }

    @Override
    public <T> void write(T t, String baseName) throws Exception {
        client(t)
    }
}
