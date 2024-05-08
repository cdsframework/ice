/*
 * Copyright 2017-2020 OpenCDS.org
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

package org.opencds.evaluation.service;

import java.io.File;

import org.opencds.config.api.model.SupportingData;
import org.opencds.config.api.service.SupportingDataPackageService;
import org.opencds.plugin.SupportingDataPackage;
import org.opencds.plugin.SupportingDataPackageSupplier;

public class SupportingDataPackageSupplierImpl implements SupportingDataPackageSupplier {

	private final SupportingDataPackageService supportingDataPackageService;
	private final SupportingData supportingData;

	public SupportingDataPackageSupplierImpl(SupportingDataPackageService supportingDataPackageService, SupportingData sd) {
		this.supportingDataPackageService = supportingDataPackageService;
		this.supportingData = sd;
	}

	@Override
	public SupportingDataPackage get() {
		return new SupportingDataPackage() {

			@Override
			public File getFile() {
				return supportingDataPackageService.getFile(supportingData);
			}

			@Override
			public byte[] getBytes() {
				return supportingDataPackageService.getPackageBytes(supportingData);
			}
			
		};
	}

}
