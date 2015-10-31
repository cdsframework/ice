package org.opencds.plugin;

import java.util.List;

import org.opencds.plugin.PluginContext.PreProcessPluginContext;
import org.opencds.vmr.v1_0.internal.CDSInput;
import org.opencds.vmr.v1_0.internal.datatypes.CD;

public class DebugPlugin implements OpencdsPlugin<PreProcessPluginContext> {

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
