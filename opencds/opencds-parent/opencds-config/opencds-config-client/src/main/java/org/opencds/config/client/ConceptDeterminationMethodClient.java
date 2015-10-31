package org.opencds.config.client;

public interface ConceptDeterminationMethodClient {
    <T> T getConceptDeterminationMethods(Class<T> clazz);
    
    <T> void putConceptDeterminationMethods(T cdms);
    
    <T> void postConceptDeterminationMethod(T cdm);
    
    <T> T getConceptDeterminationMethod(String cdmId, Class<T> clazz);
    
    <T> void putConceptDeterminationMethod(String cdmId, T cdm);
    
    void deleteConceptDeterminationMethod(String cdmId);
    
}
