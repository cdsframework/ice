/*
 * Copyright 2020 OpenCDS.org
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

package org.opencds.config.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.opencds.common.exceptions.OpenCDSRuntimeException;

public class ZipUtil {
	
	public Map<String, byte[]> readZipFile(InputStream packageInputStream) {
		Map<String, byte[]> data = new HashMap<>();
		try (ZipInputStream zip = new ZipInputStream(packageInputStream)) {
			ZipEntry entry;
			while ((entry = zip.getNextEntry()) != null) {
				if (!entry.isDirectory()) {
					data.put(entry.getName(), readEntry(zip));
				}
			}
		} catch (IOException e) {
			throw new OpenCDSRuntimeException("Error reading ZIP file", e);
		}
		return data;
	}

	public byte[] readEntry(ZipInputStream zip) {
		byte[] buffer = new byte[2048]; // magic number
		List<Byte> entryData = new ArrayList<>();
		int len = 0;
		try {
			while ((len = zip.read(buffer)) > 0) {
				for (int i = 0; i < len; i++) {
					entryData.add(buffer[i]);
				}
			}
		} catch (IOException e) {
			throw new OpenCDSRuntimeException("Error reading entry in ZIP file.", e);
		}
		byte[] newBuffer = new byte[entryData.size()];
		for (int i = 0; i < entryData.size(); i++) {
			newBuffer[i] = entryData.get(i);
		}
		return newBuffer;
	}


}
