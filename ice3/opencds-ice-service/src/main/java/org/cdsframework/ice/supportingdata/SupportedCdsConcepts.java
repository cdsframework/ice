package org.cdsframework.ice.supportingdata;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.cdsframework.ice.service.ICEConcept;

public class SupportedCdsConcepts {

	private Map<IceConceptType, Map<ICEConcept, Set<LocallyCodedCdsListItem>>> concepts;
	
	protected SupportedCdsConcepts() {
		
		concepts = new HashMap<IceConceptType, Map<ICEConcept, Set<LocallyCodedCdsListItem>>>();
	}
}
