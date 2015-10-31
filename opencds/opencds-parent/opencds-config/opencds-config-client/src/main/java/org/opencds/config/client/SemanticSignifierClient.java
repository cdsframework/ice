package org.opencds.config.client;

public interface SemanticSignifierClient {
    <T> T getSemanticSignifiers(Class<T> clazz);
    
    <T> void putSemanticSignifiers(T semanticSignifiers);
    
    <T> void postSemanticSignifier(T semanticSignifier);
    
    <T> T getSemanticSignifier(String ssid, Class<T> clazz);
    
    <T> void putSemanticSignifier(String ssid, T semanticSignifier);
    
    void deleteSemanticSignifier(String ssid);
}
