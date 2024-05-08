/*
 * Copyright 2015-2020 OpenCDS.org
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

package org.opencds.config.store.dao.je

import java.nio.file.Path
import java.nio.file.Paths

import org.opencds.common.exceptions.OpenCDSRuntimeException
import org.opencds.config.store.je.OpenCDSConfigStore

import spock.lang.Specification

class MigrationTo21xSpec extends Specification {
	static final File path20x = new File('src/test/resources/resources_v2.0.6-store')
	static final String STORE = 'STORE'

	static File tmpPath
	static File path
	static OpenCDSConfigStore configStore

	def setupSpec() {
		tmpPath = File.createTempDir()
		println tmpPath.getAbsolutePath()
		path = new File(tmpPath, STORE)
		path.mkdirs()
		path20x.listFiles().each {File file ->
			println "file.name is ${file.name}"
			File target = Paths.get(path.getAbsolutePath(), file.name).toFile()
			println "Copying file : ${file.absolutePath} to ${target.absolutePath}"
			println path.exists()
			println target.getAbsolutePath()
			target.createNewFile()
			target << file.bytes
		}
	}

	def cleanupSpec() {
		configStore?.close();
		tmpPath.listFiles().each {file ->
			println "deleting ${file.getAbsolutePath()} ${file.length()}"
			if (file.isDirectory()) {
				file.listFiles().each {subfile ->
					println "deleting ${subfile.getAbsolutePath()} ${subfile.length()}"
					subfile.delete()
				}
			}
			assert file.delete() == true
		}
		println "deleting ${path.getAbsolutePath()} ${path.length()}"
		assert tmpPath.delete() == true
	}

	def "setup for migration"() {
		when:
		try {
			configStore = new OpenCDSConfigStore(path)
			configStore.close()
			configStore = new OpenCDSConfigStore(path)
		} catch (Exception e) {
			e.printStackTrace()
			throw e
		}

		then:
		notThrown(OpenCDSRuntimeException)
	}
}
