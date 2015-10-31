package org.opencds.terminology.apelon.method;

import org.opencds.terminology.apelon.ApelonClientException;
import org.opencds.terminology.apelon.ApelonDtsClient;

import com.apelon.dts.client.concept.DTSConcept;

public class FindConceptByName {

    public static DTSConcept execute(ApelonDtsClient client, String name) throws ApelonClientException {
        return client.findConceptByName(name);
    }

}
