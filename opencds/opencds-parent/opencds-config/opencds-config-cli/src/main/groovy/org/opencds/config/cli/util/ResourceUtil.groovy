/*
 * Copyright 2014-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.config.cli.util

import groovy.util.logging.Commons
import groovy.xml.XmlSlurper
import jakarta.ws.rs.core.MediaType
import org.apache.xerces.impl.io.MalformedByteSequenceException
import org.xml.sax.SAXParseException

@Commons
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
            println("File is not XML or is improperly formatted XML\nContinuing as binary: " + file.getAbsoluteFile())
            data.input = { new FileInputStream(file) }
            data.mediaType = MediaType.APPLICATION_OCTET_STREAM
            data.type = 'binary'
        }
        return data
    }

    static Map get(String filename) {
        return get(new File(filename))
    }

}
