package org.opencds.terminology.apelon.cache;

import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CacheKey {
    private static final String COLON = ":";
    private static final String EQUALS = "=";
    private static final String PIPE = "|";
    
    private final String key;
    private final int hashCode;
    private final Map<String, String> params;

    private CacheKey(Map<String, String> params, String key) {
        this.params = params;
        this.key = key;
        hashCode = new HashCodeBuilder().append(key).toHashCode();
    }
    
    public static CacheKey create(Map<String, String> params) {
        StringBuilder sb = new StringBuilder(PIPE);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey());
            sb.append(entry.getKey().hashCode());
            sb.append(EQUALS);
            sb.append(entry.getValue());
            sb.append(COLON);
            sb.append(entry.getValue().hashCode());
            sb.append(PIPE);
        }
        return new CacheKey(params, sb.toString());
    }
    
    public Map<String, String> getParams() {
        return params;
    }
    
    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CacheKey)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        CacheKey ck = (CacheKey) obj;
        return new EqualsBuilder().append(key, ck.key).isEquals();
    }

    @Override
    public String toString() {
        return key;
    }

}
