package org.opencds.terminology.apelon.method;

import java.util.Collections;
import java.util.Set;

import org.opencds.terminology.apelon.ApelonClientException;
import org.opencds.terminology.apelon.ApelonDtsClient;

import com.apelon.dts.client.concept.DTSConcept;

public class FindDirectChildrenOfConcept {

    public static Set<DTSConcept> execute(ApelonDtsClient client, String parentConceptIdentifier, boolean includeParent) throws ApelonClientException {
        DTSConcept parentConcept = FindConceptByIdentifier.execute(client, parentConceptIdentifier);
        if (parentConcept == null) {
            return Collections.emptySet();
        }
        return client.findDirectChildrenOfConcept(parentConcept, includeParent);
    }

}
