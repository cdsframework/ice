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

import java.io.InputStream;
import java.util.List;

public interface ResourceUtility {

	String getContents(String source);

	List<String> listMatchingResources(final String path,
			final String startsWith, final String endsWith);

	InputStream getResourceAsInputStream(String string);

    String renormalizeSeparator(String location);

}
