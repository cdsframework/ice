package org.opencds.terminology.apelon.cache;

import java.util.Set;

import org.opencds.common.cache.OpencdsCache.CacheRegion;

import com.apelon.dts.client.concept.DTSConcept;

public enum ApelonCacheRegion implements CacheRegion {
    BY_CODE(DTSConcept.class),
    BY_NAME(DTSConcept.class),
    DESCENDANTS(Set.class),
    WITH_PROPERTY_MATCHING(Set.class),
    PROPERTY_TYPE(Set.class),
    CONCEPT_CHILDREN(Set.class);

    private Class<?> supportedType;
    
    private ApelonCacheRegion(Class<?> supportedType) {
        this.supportedType = supportedType;
    }
    
    @Override
    public boolean supports(Class<?> type) {
        return supportedType.isAssignableFrom(type);
    }
}