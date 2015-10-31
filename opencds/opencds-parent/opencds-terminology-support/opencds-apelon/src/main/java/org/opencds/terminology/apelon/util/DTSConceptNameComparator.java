package org.opencds.terminology.apelon.util;

import java.util.Comparator;

import com.apelon.dts.client.concept.DTSConcept;

public class DTSConceptNameComparator implements Comparator<DTSConcept> {

    @Override
    public int compare(DTSConcept o1, DTSConcept o2) {
        return o1.getName().compareTo(o2.getName());
    }

}
