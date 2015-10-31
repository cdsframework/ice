package org.opencds.terminology.apelon;

import java.util.Set;

import com.apelon.dts.client.concept.DTSConcept;

public interface ApelonDtsClient {

    public abstract Set<DTSConcept> findConceptsWithProperty(String propertyTypeName, String propertyValuePattern) throws ApelonClientException;

    public abstract Set<DTSConcept> findDescendantsOfConcept(DTSConcept rootConcept, int maxLevelsDeep, boolean includeRoot)
            throws ApelonClientException;

    public abstract Set<DTSConcept> findDirectChildrenOfConcept(DTSConcept parentConcept, boolean includeParent) throws ApelonClientException;

    public abstract DTSConcept findConceptByCode(String code) throws ApelonClientException;

    public abstract DTSConcept findConceptByName(String name) throws ApelonClientException;

}
