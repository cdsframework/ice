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

package org.opencds.hooks.model.response;

import org.opencds.common.exceptions.OpenCDSRuntimeException;

import java.util.stream.Stream;

public enum LinkType {
    ABSOLUTE("absolute"),
    SMART("smart");

    private final String value;

    LinkType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LinkType resolve(String value) {
        return Stream.of(values())
                .filter(lt -> lt.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new OpenCDSRuntimeException("Link.type is required"));
    }
}