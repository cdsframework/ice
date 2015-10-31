package org.opencds.common.terminology;

import java.io.File;

import spock.lang.Specification;

class VMREnumerationsClientSpec extends Specification {

    static String clProperty
    static String path
    static String pathRenamed
    static boolean hasExistingConfig = false
    static boolean hasSystemProperty = false
    
    private static getConfigPath() {
        return System.getProperty("user.home") + File.separator + VMREnumerationsClient.CONFIG_LOCATION + File.separator + VMREnumerationsClient.CONFIG_FILE
    }
    
    def setupSpec() {
        clProperty = System.getProperty(VMREnumerationsClient.APELON_SERVICE_URL_PROPERTY_NAME)
        if (clProperty) {
            System.getProperties().remove(VMREnumerationsClient.APELON_SERVICE_URL_PROPERTY_NAME)
        }
        path = getConfigPath()
        pathRenamed = path + ".moved-for-testing"
        File configFile = new File(path)
        if (configFile.exists()) {
            hasExistingConfig = configFile.renameTo(pathRenamed)
        }
    }
    
    def cleanupSpec() {
        if (hasExistingConfig) {
            File file = new File(path)
            file.delete()
            File configFile = new File(pathRenamed)
            def renamed = configFile.renameTo(path)
        } else {
            File file = new File(path)
            file.delete()
        }
        if (hasSystemProperty) {
            System.getProperties().put(VMREnumerationsClient.APELON_SERVICE_URL_PROPERTY_NAME, clProperty)
        }
    }
    
    def "test constructor with no system properties"() {
        when:
        def client = new VMREnumerationsClient()
        
        then:
        thrown(RuntimeException)
    }
    
    def "test constructor with no system properties and no config values in config file"() {
        given:
        File configFile = new File(path)
        configFile.createNewFile()
        configFile << "HIHIHI=HELLOHELLOHELLO\n"
        
        when:
        def client = new VMREnumerationsClient()
        
        then:
        thrown(RuntimeException)
        configFile.delete()
    }
    
    def "test constructor with no system properties and config"() {
        given:
        File configFile = new File(path)
        configFile.createNewFile()
        configFile << "guvnor.vmrenumerations.service.url=http://localhost:8080/opencds-apelon/apelonDtsService\n"
        
        when:
        def client = new VMREnumerationsClient()
        
        then:
        notThrown(RuntimeException)
        configFile.delete()
    }
    
    def "test constructor with system properties"() {
        given:
        System.setProperty(VMREnumerationsClient.APELON_SERVICE_URL_PROPERTY_NAME, "http://localhost:8080/opencds-apelon/apelonDtsService")
        
        when:
        def client = new VMREnumerationsClient()
        
        then:
        System.getProperties().remove(VMREnumerationsClient.APELON_SERVICE_URL_PROPERTY_NAME)
        notThrown(RuntimeException)
    }

}
