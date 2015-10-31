package org.opencds.terminology.apelon.cache;

import org.opencds.common.cache.OpencdsCache.CacheRegion;
import org.opencds.common.xml.XmlEntity;

public enum ApelonDtsServiceCacheRegion implements CacheRegion {
    RESPONSE(XmlEntity.class);
    
    private Class<?> supportedType;
    
    private ApelonDtsServiceCacheRegion(Class<?> supportedType) {
        this.supportedType = supportedType;
    }
    
    @Override
    public boolean supports(Class<?> type) {
        return supportedType.isAssignableFrom(type);
    }
}