package org.opencds.config.cli.util

import groovy.util.slurpersupport.GPathResult;

class IdUtil {
    static String cdmid(GPathResult cdm) {
        return cdm.@codeSystem.text() + "^" + cdm.@code.text() + "^" + cdm.@version.text()
    }

    static String entityid(GPathResult km) {
        return km.@scopingEntityId.text() + "^" + km.@businessId.text() + "^" + km.@version.text()
    }
}
