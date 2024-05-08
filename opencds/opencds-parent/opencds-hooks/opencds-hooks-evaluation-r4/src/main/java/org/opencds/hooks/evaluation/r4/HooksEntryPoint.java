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

package org.opencds.hooks.evaluation.r4;

import org.opencds.config.api.ss.EntryPoint;
import org.opencds.hooks.model.r4.util.R4JsonUtil;
import org.opencds.hooks.model.request.CdsRequest;

public class HooksEntryPoint implements EntryPoint<CdsRequest> {

	private final R4JsonUtil jsonUtil = new R4JsonUtil();
	
	@Override
	public CdsRequest buildInput(byte[] inputPayload) {
		return jsonUtil.fromJson(inputPayload, CdsRequest.class);
	}

}
