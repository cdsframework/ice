package org.opencds.terminology.apelon.method;

import java.util.Collections;
import java.util.Set;

import org.opencds.terminology.apelon.ApelonClientException;
import org.opencds.terminology.apelon.ApelonDtsClient;

import com.apelon.dts.client.concept.DTSConcept;

public class FindDescendantsOfConcept {

    public static Set<DTSConcept> execute(ApelonDtsClient client, String rootConceptName, int maxLevelsDeep,
            boolean includeRoot) throws ApelonClientException {
        DTSConcept rootConcept = FindConceptByIdentifier.execute(client, rootConceptName);
        if (rootConcept == null) {
            return Collections.emptySet();
        }
        return client.findDescendantsOfConcept(rootConcept, maxLevelsDeep, includeRoot);
    }
}
