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

package org.opencds.config.api.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.opencds.config.api.model.SupportingData;

public interface SupportingDataPackageService {
    
    boolean exists(SupportingData supportingData);
    
    /**
     * The implementation of this method needs to know where the knowledge packages exist on the backend.
     * 
     * If the underlying implementation cannot be resolved (e.g., file not found), this method returns null;
     * 
     * @param knowledgeModule
     * @return
     * @throws IOException 
     */
    InputStream getPackageInputStream(SupportingData supportingData);
    
    byte[] getPackageBytes(SupportingData supportingData);
    
    void persistPackageInputStream(SupportingData sd, InputStream supportingDataPackage);

    void deletePackage(SupportingData sd);

    File getFile(SupportingData sd);

}
