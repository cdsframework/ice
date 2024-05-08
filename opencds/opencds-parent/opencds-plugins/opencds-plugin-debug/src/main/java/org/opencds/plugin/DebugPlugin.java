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

package org.opencds.plugin;

import java.util.List;

import org.opencds.plugin.PluginContext.PreProcessPluginContext;
import org.opencds.vmr.v1_0.internal.CDSInput;
import org.opencds.vmr.v1_0.internal.datatypes.CD;

public class DebugPlugin implements PreProcessPlugin {

    private static final String DEBUG_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.1.1";
    private static final String DEBUG_CODE = "debug";

    @Override
    public void execute(PreProcessPluginContext context) {
        List<CDSInput> cdsInputList = (List<CDSInput>) context.getAllFactLists().get(CDSInput.class);
        CDSInput cdsInput = cdsInputList.get(0);
        CD userType = cdsInput.getCdsContext().getCdsSystemUserTaskContext();
        if (userType.getCodeSystem() == DEBUG_CODE_SYSTEM && userType.getCode() == DEBUG_CODE) {
            context.getGlobals().put("org.opencds.plugin.DebugPlugin", "debug");
        }
    }
    
}
