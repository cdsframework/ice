package org.opencds.terminology.apelon.method;

import java.util.Set;

import org.opencds.terminology.apelon.ApelonClientException;
import org.opencds.terminology.apelon.ApelonDtsClient;

import com.apelon.dts.client.concept.DTSConcept;

public class FindConceptsWithPropertyMatching {

    public static Set<DTSConcept> execute(ApelonDtsClient client, String propertyTypeName, String propertyValuePattern)
            throws ApelonClientException {
        return client.findConceptsWithProperty(propertyTypeName, propertyValuePattern);
    }

}
