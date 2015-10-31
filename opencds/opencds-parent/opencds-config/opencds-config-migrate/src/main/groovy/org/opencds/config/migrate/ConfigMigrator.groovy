package org.opencds.config.migrate

import groovy.util.logging.Log4j

import java.nio.file.Path
import java.nio.file.Paths

import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.opencds.common.cache.OpencdsCache
import org.opencds.config.api.model.ConceptDeterminationMethod
import org.opencds.config.api.model.ExecutionEngine
import org.opencds.config.api.model.KnowledgeModule
import org.opencds.config.api.model.SemanticSignifier
import org.opencds.config.client.rest.RestClient
import org.opencds.config.mapper.ConceptDeterminationMethodMapper
import org.opencds.config.mapper.ExecutionEngineMapper
import org.opencds.config.mapper.KnowledgeModuleMapper
import org.opencds.config.mapper.SemanticSignifierMapper
import org.opencds.config.migrate.builder.CDMConfigBuilder
import org.opencds.config.migrate.builder.CodeSystemsConfigBuilder
import org.opencds.config.migrate.builder.ConfigBuilder
import org.opencds.config.migrate.builder.ExecutionEnginesConfigBuilder
import org.opencds.config.migrate.builder.KnowledgeModulesConfigBuilder
import org.opencds.config.migrate.builder.SemanticSignifiersConfigBuilder
import org.opencds.config.migrate.builder.SupportedConceptsBuilder
import org.opencds.config.migrate.cache.ConfigCacheRegion
import org.opencds.config.migrate.utilities.ConfigResourceUtility
import org.opencds.config.migrate.utilities.FileUtility

@Log4j
public class ConfigMigrator {
    private OpencdsBaseConfig config

    public void migrate(Map metadata, OpencdsBaseConfig config) throws Exception {
        ExecutionEnginesConfigBuilder eecb = new ExecutionEnginesConfigBuilder()
        SemanticSignifiersConfigBuilder sscb = new SemanticSignifiersConfigBuilder()
        KnowledgeModulesConfigBuilder kmcb = new KnowledgeModulesConfigBuilder()
        CodeSystemsConfigBuilder cscb = new CodeSystemsConfigBuilder()
        SupportedConceptsBuilder scb = new SupportedConceptsBuilder()
        CDMConfigBuilder cdmcb = new CDMConfigBuilder(config, new ConfigResourceUtility())
        ConfigBuilder builder = new ConfigBuilder(eecb, sscb, kmcb, cscb, scb, cdmcb)

        OpencdsCache cache = new OpencdsCache()
        builder.build(config, cache)

        NewConfigWriter configWriter = new NewConfigWriter()
        configWriter.writeCDMConfig(metadata.streamBuilder.createForCDM(metadata.param, metadata.splitCDM), ConceptDeterminationMethodMapper.external(cache.<List<ConceptDeterminationMethod>> get(ConfigCacheRegion.METADATA, ConfigResourceType.CONCEPT_DETERMINATION_METHOD)), metadata.splitCDM);
        configWriter.writeExecutionEngineConfig(metadata.streamBuilder.createForEE(metadata.param), ExecutionEngineMapper.external(cache.<List<ExecutionEngine>> get(ConfigCacheRegion.METADATA, ConfigResourceType.EXECUTION_ENGINES)));
        configWriter.writeSemanticSignifierConfig(metadata.streamBuilder.createForSS(metadata.param), SemanticSignifierMapper.external(cache.<List<SemanticSignifier>> get(ConfigCacheRegion.METADATA, ConfigResourceType.SEMANTIC_SIGNIFIERS)));
        configWriter.writeKnowledgeModuleConfig(metadata.streamBuilder.createForKM(metadata.param), KnowledgeModuleMapper.external(cache.<List<KnowledgeModule>> get(ConfigCacheRegion.METADATA, ConfigResourceType.KNOWLEDGE_MODULES)));
        configWriter.writeKnowledgePackages(metadata.streamBuilder.createForKP(metadata.param, cache.<List<String>> get(ConfigCacheRegion.DATA, ConfigResourceType.KNOWLEDGE_PACKAGE)), cache.<List<String>> get(ConfigCacheRegion.DATA, ConfigResourceType.KNOWLEDGE_PACKAGE));
    }

    public static void main(String[] args) {
        def cli = buildCli(args)
        try {
            OptionAccessor options = cli.options()
            def source = options.source
            def target = options.target
            boolean splitCDM = options.'split-cdm'
            boolean overwrite = options.overwrite
            if (!source) {
                error("source option required")
            }
            if (!target) {
                error("target option required")
            }
            Map<ConfigResourceType, ConfigResource> crs = buildConfigResources()
            def sourcePath = Paths.get(source)
            if (sourcePath.toFile().exists()) {
                crs.values().each {
                    ConfigResource cr ->
                    println "Checking existence of ${cr}"
                    def location = cr?.location
                    def name = cr?.name ?: ""
                    def crFile = Paths.get(source, location, name).toFile()
                    if (!crFile.exists()) {
                        error("Resource must exist before migration: location: ${location}, name: ${name}, file: ${crFile.getAbsolutePath()}")
                    }
                }
            } else {
                error("source does not exist")
            }
            Map builder
            if (target.startsWith("http")) {
                Client c = ClientBuilder.newClient()
                def username = options.username
                def password = options.password
                def authFeature = HttpAuthenticationFeature.basicBuilder().credentials(username, password).build()
                c.register(authFeature)
                RestClient client = new RestClient(c.target(target))
                builder = [streamBuilder: RestConfigHandler.class,  param: client]
            } else {
                def targetPath = Paths.get(target)
                def targetFile = targetPath.toFile()
                if (targetFile.exists() && !overwrite) {
                    error("target exists; please move or delete before migrating; target= ${target}")
                } else {
                    log.info "creating target ${target}"
                    targetFile.mkdirs()
                }
                builder = [streamBuilder: FileConfigHandler.class, param: targetPath]
            }
            builder.splitCDM = splitCDM
            OpencdsBaseConfig config = new OpencdsBaseConfig("SIMPLE_FILE", source, crs);
            config.setFileUtility(new FileUtility());
            ConfigMigrator migrator = new ConfigMigrator(config: config)
            migrator.migrate(builder, config)
        } catch (Exception e) {
            e.printStackTrace()
            log.error(e.message, e)
            cli.usage()
        }
    }

    static Map buildCli(String[] args) {
        def cli = new CliBuilder()
        cli._(longOpt: 'source', args: 1, "Source folder for existing configuration")
        cli._(longOpt: 'target', args: 1, "Target folder for migrated configuration")
        cli._(longOpt: 'username', args: 1, "Username")
        cli._(longOpt: 'password', args: 1, "Password")
        cli._(longOpt: 'split-cdm', args: 0, "If provided, the ConceptDeterminationMethods will be split into multiple files, one per CDM.")
        cli._(longOpt: 'overwrite', args: 0, "If provided, and target folder exists, all pertinent configuration files will be overwritten.")
        def options = cli.parse(args)
        [usage: {
                cli.usage()
            }, options: {
                options
            }]
    }

    static Map<ConfigResourceType, ConfigResource> buildConfigResources() {
        return [
            (ConfigResourceType.CODE_SYSTEMS): new ConfigResource(location: "resourceAttributes", name: "openCDSCodeSystems.xml"),
            (ConfigResourceType.EXECUTION_ENGINES): new ConfigResource(location: "resourceAttributes", name: "openCdsExecutionEngines.xml"),
            (ConfigResourceType.KNOWLEDGE_MODULES): new ConfigResource(location: "resourceAttributes", name: "knowledgeModules.xml"),
            (ConfigResourceType.SEMANTIC_SIGNIFIERS): new ConfigResource(location: "resourceAttributes", name: "semanticSignifiers.xml"),
            (ConfigResourceType.SUPPORTED_CONCEPTS): new ConfigResource(location: "resourceAttributes", name: "supportedConceptsConfigFile.xml"),
            (ConfigResourceType.AUTOGEN_CONCEPT_MAPPINGS_SPEC): new ConfigResource(location: "conceptMappingSpecifications" + File.separator + "autoGeneratedMappings"),
            (ConfigResourceType.MANUAL_CONCEPT_MAPPINGS_SPEC): new ConfigResource(location: "conceptMappingSpecifications" + File.separator + "manualMappings"),
            (ConfigResourceType.EXECUTION_ENGINE_MODULES): new ConfigResource(location: "knowledgeModules")
        ]
    }

    static void error(String msg) {
        throw new IllegalArgumentException(msg)
    }
}
