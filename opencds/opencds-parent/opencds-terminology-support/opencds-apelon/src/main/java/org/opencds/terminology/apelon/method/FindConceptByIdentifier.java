package org.opencds.terminology.apelon.method;

import org.opencds.terminology.apelon.ApelonClientException;
import org.opencds.terminology.apelon.ApelonDtsClient;

import com.apelon.dts.client.concept.DTSConcept;

public class FindConceptByIdentifier {

    public static DTSConcept execute(ApelonDtsClient client, String identifier) throws ApelonClientException {
        DTSConcept concept = FindConceptByName.execute(client, identifier);
        if (concept == null) {
            concept = FindConceptByCode.execute(client, identifier);
        }
        return concept;
    }
}
