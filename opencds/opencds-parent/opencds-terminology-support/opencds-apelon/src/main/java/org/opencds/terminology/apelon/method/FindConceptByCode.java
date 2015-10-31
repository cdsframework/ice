package org.opencds.terminology.apelon.method;

import org.opencds.terminology.apelon.ApelonClientException;
import org.opencds.terminology.apelon.ApelonDtsClient;

import com.apelon.dts.client.concept.DTSConcept;

public class FindConceptByCode {

    public static DTSConcept execute(ApelonDtsClient client, String code) throws ApelonClientException {
        return client.findConceptByCode(code);
    }

}
