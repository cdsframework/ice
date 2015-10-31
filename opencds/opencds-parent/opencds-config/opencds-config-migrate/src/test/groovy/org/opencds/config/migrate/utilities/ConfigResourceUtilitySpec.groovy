package org.opencds.config.migrate.utilities;

import org.opencds.common.exceptions.OpenCDSRuntimeException
import org.opencds.config.migrate.ConfigResource
import org.opencds.config.migrate.ConfigResourceType
import org.opencds.config.migrate.OpencdsBaseConfig

import spock.lang.Specification

class ConfigResourceUtilitySpec extends Specification {

    ConfigResourceUtility util
    OpencdsBaseConfig config
    
    def setup() {
        Map<ConfigResourceType, ConfigResource> resources = [(ConfigResourceType.EXECUTION_ENGINES): new ConfigResource(location:"src/test/resources" , name: "file-utility-spec.txt")]
        config = new OpencdsBaseConfig("SIMPLE_FILE", ".", resources);
        config.fileUtility = new FileUtility()
        util = new ConfigResourceUtility()
    }
    
    def "test getResourceAsString"() {
        when:
        def result = util.getResourceAsString(config, ConfigResourceType.EXECUTION_ENGINES)
        
        then:
        result == "onetwothree"
    }

    def "test getResourceAsString for file that doesn't exist"() {
        when:
        def result = util.getResourceAsString(config, ConfigResourceType.CODE_SYSTEMS)
        
        then:
        result == null
        thrown(OpenCDSRuntimeException)
    }

    def "test getResourceAsString for null configresource"() {
        when:
        def result = util.getResourceAsString(config, (ConfigResourceType) null)
        
        then:
        result == null
        thrown(OpenCDSRuntimeException)
    }

    def "test getResourceAsString (config, resource)"() {
        when:
        def result = util.getResourceAsString(config, "src/test/resources/file-utility-spec.txt")
        
        then:
        result == "onetwothree"
    }

    def "test getResourceAsString (config, resource) where resource is not found -- throws an exception"() {
        when:
        def result = util.getResourceAsString(config, "something.txt")
        
        then:
        result == null
        thrown(OpenCDSRuntimeException)
    }

    def "test listResourceNamesByType"() {
        when:
        def result = util.listResourceNamesByType(config, ConfigResourceType.EXECUTION_ENGINES)
        
        then:
        result == [new File('./src/test/resources/file-utility-spec.txt').getPath()]
    }

    def "test listResourceNamesByType from classpath"() {
//        given:
        Map<ConfigResourceType, ConfigResource> resources = [(ConfigResourceType.EXECUTION_ENGINES): new ConfigResource(location:"schema" , name: "vmr.xsd")]
        OpencdsBaseConfig localConfig = new OpencdsBaseConfig("CLASSPATH", "resources_v1.1", resources);
        localConfig.fileUtility = new FileUtility();
        ConfigResourceUtility localUtil = new ConfigResourceUtility()
        
//        when:
        def result = localUtil.listResourceNamesByType(localConfig, ConfigResourceType.EXECUTION_ENGINES)
        
//        then:
        result.sort() == [
            'resources_v1.1/schema/',
            'resources_v1.1/schema/cdsInput.xsd',
            'resources_v1.1/schema/cdsInputSpecification.xsd',
            'resources_v1.1/schema/cdsOutput.xsd',
            'resources_v1.1/schema/datatypes.xsd',
            'resources_v1.1/schema/OmgDssKmMetadata.xsd',
            'resources_v1.1/schema/OmgDssSchema.xsd',
            'resources_v1.1/schema/OmgDssSemanticSignifiers.xsd',
            'resources_v1.1/schema/opencds-decision-support-service-config.xsd',
            'resources_v1.1/schema/OpenCDSCodeSystems.xsd',
            'resources_v1.1/schema/OpenCDSConceptTypes.xsd',
            'resources_v1.1/schema/OpenCdsExecutionEngines.xsd',
            'resources_v1.1/schema/SimpleTerminologyServiceSchema.xsd',
            'resources_v1.1/schema/vmr.xsd'
        ].sort()
    }

    def "test listResourceNamesByType where no resources exist"() {
        given:
        Map<ConfigResourceType, ConfigResource> resources = [(ConfigResourceType.EXECUTION_ENGINES): new ConfigResource(location:"src/test/resources/nonexistent-path-name" , name: "file-utility-spec.txt")]
        config = new OpencdsBaseConfig("SIMPLE_FILE", ".", resources);
        config.fileUtility = new FileUtility()
        util = new ConfigResourceUtility()

        when:
        def result = util.listResourceNamesByType(config, ConfigResourceType.EXECUTION_ENGINES)
        
        then:
        result == []
    }

    def "test listResourceNamesByType with null config resource typ"() {
        when:
        def result = util.listResourceNamesByType(config, (ConfigResourceType) null)
        
        then:
        result == null
        thrown(OpenCDSRuntimeException)
    }

    def "test getResourceAsInputStream"() {
        when:
        def result = util.getResourceAsInputStream(config, ConfigResourceType.EXECUTION_ENGINES, 'file-utility-spec.txt')
        
        then:
        result
        def lines = result.readLines()
        lines == ['one', 'two', 'three']
    }

    def "test getResourceAsInputStream with empty resourceid"() {
        when:
        def result = util.getResourceAsInputStream(config, ConfigResourceType.EXECUTION_ENGINES, '')
        
        then:
        result == null
    }

    def "test getResourceAsInputStream with null config resource type"() {
        when:
        def result = util.getResourceAsInputStream(config, (ConfigResourceType) null, 'file-utility-spec.txt')
        
        then:
        result == null
        thrown(OpenCDSRuntimeException)
    }

}
