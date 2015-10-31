package org.opencds.config.file.dao.support;

import org.opencds.config.api.dao.util.FileUtil;

import spock.lang.Specification

class FileUtilSpec extends Specification {
    
    FileUtil fileUtil
    
    def setup() {
        fileUtil = new FileUtil()
    }
    
    def "test findFiles"() {
        given:
        def path = new File('src/test/resources/resources_v1.3')
        
        when:
        def list = fileUtil.findFiles(path, true)
        
        then:
        !list.empty
    }
}
