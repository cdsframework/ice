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

package org.opencds.config.api.model;

public enum KMStatus {
    /**
     * The KM successfully passed the QA tests.
     * 
     */
    APPROVED,

    /**
     * Can be executed. Precondition: the KM is syntactically valid. When a
     * defined KM changes, a new version should be created.
     * 
     */
    DEFINED,

    /**
     * Draft KM.
     * 
     */
    DRAFT,

    /**
     * The KM can be used in the production environment.
     * 
     */
    PROMOTED,

    /**
     * The KM definition doesn't match the KM specification.
     * 
     */
    REJECTED,

    /**
     * The KM was historically promoted, but should not be used any more.
     * 
     */
    RETIRED;

}
