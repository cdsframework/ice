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

package org.opencds.config.api;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigData {
    private String configType;
    private String configPath;
    /**
     * Default value is 1.
     */
    private int kmThreads = 1;

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public Path getConfigLocation() {
        return Paths.get(configPath);
    }
    
    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }
    
    public int getKmThreads() {
        return kmThreads;
    }
    
    public void setKmThreads(int kmThreads) {
        this.kmThreads = kmThreads;
    }
}
