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

package org.opencds.config.migrate.utilities;

import static org.junit.Assert.*

import org.opencds.common.exceptions.OpenCDSRuntimeException
import org.opencds.config.migrate.utilities.FileUtility;

import spock.lang.Specification

class FileUtilitySpec extends Specification {
	private final String TEST_FILE = "src/test/resources/file-utility-spec.txt"
	
	FileUtility fileUtility

	def setup() {
		fileUtility = new FileUtility()
	}

	def "get file name list in src/test/resources returns a list of one file"() {
		when:
		List<String> filenames = fileUtility.listMatchingResources("src/test/resources","file","txt")

		then:
		filenames != null
		filenames.size() == 1
		filenames[0] == 'file-utility-spec.txt'
	}

	def "get file contents of src/test/resources/file-utility-spec.txt returns a string of the contents"() {
		when:
		String contents = fileUtility.getContents(TEST_FILE)

		then:"no newlines"
		contents != null
		contents == 'onetwothree'
	}

	def "get file InputStream returns the expected InputStream object"() {
		when:
		InputStream input = fileUtility.getResourceAsInputStream(TEST_FILE)
		
		then:
		input != null
	}

	def "get InputStream of nonexistent file doesn't throw an expectation"() {
		when:
		InputStream input = fileUtility.getResourceAsInputStream(TEST_FILE + "nope")
		
		then:
		notThrown(OpenCDSRuntimeException)
		input == null
	}
    
    def "test blank renormalize"() {
        given:
        def path = ""
        
        when:
        def result = fileUtility.renormalizeSeparator(path)
        
        then:
        println result
        path == result
    }
}
