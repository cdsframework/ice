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

package org.opencds.config.api.dao.file;

import org.opencds.common.exceptions.OpenCDSRuntimeException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamCacheElement implements CacheElement {

	private final String id;
	private final InputStream inputStream;

	private StreamCacheElement(String id, InputStream inputStream) {
		this.id = id;
		this.inputStream = inputStream;
	}

	public static CacheElement create(String id, InputStream inputStream) {
		return new StreamCacheElement(id, inputStream);
	}

	@Override
	public long length() {
		return -1;
	}

	@Override
	public boolean exists() {
		return inputStream != null;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public InputStream getInputStream() {
		return inputStream;
	}

	@Override
	public File getFile() {
		File file = null;
		try {
			file = File.createTempFile(id, ".accdb");
			try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
				DataInputStream in = new DataInputStream(inputStream);
				byte[] b = new byte[1024];
				int bytesRead = 0;
				while ((bytesRead = in.read(b)) != -1) {
					out.write(b, 0, bytesRead);
				}
				out.flush();
				out.close();
			}
			return file;
		} catch (IOException e) {
			throw new OpenCDSRuntimeException();
		} finally {
			if (file != null) {
				file.deleteOnExit();
			}
		}
	}

}
