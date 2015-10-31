package org.opencds.config.cli.util

import groovy.util.logging.Log4j

import javax.ws.rs.core.MediaType

import org.apache.xerces.impl.io.MalformedByteSequenceException;
import org.xml.sax.SAXParseException

@Log4j
class ResourceUtil {

    static Map get(File file) {
        def input = null
        def data = [:]
        try {
            if (!file.exists()) {
                throw new Exception('File does not exist: ' + file.absoluteFile)
            } else if (file.isDirectory()) {
                throw new Exception('File is a directory: ' + file.absoluteFile)
            }
            def xml = new XmlSlurper().parse(file)
            // if it's xml, let's add some metadata to the map
            data.input = file.text
            data.xml = xml // so we can refer to it later...
            data.mediaType = MediaType.APPLICATION_XML
            data.type = xml.name()
        } catch (SAXParseException | MalformedByteSequenceException e) {
            println("File is not XML or is improperly formatted XML; will assume binary: " + file.getAbsoluteFile())
            input = new FileInputStream(file)
            data.input = input
            data.mediaType = MediaType.APPLICATION_OCTET_STREAM
            data.type = 'binary'
        }
        return data
    }
    
    static Map get(String filename) {
        return get(new File(filename))
    }

}
