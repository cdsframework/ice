package org.opencds.service.evaluate;

import java.util.Comparator;
import ca.uhn.fhir.model.dstu2.resource.Observation;

public class ObservationComparator {
	public static final Comparator<Observation> ISSUED_DATE  = 
            new Comparator<Observation>() {
			@Override
			public int compare(Observation o1, Observation o2) {
				return o2.getIssued().compareTo(o1.getIssued());
			}
	};
}
