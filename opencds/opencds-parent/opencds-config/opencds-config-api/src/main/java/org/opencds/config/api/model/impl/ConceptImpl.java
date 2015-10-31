package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.opencds.config.api.model.Concept;

public class ConceptImpl implements Concept {
    private String code;
    private String codeSystem;
    private String codeSystemName;
    private String displayName;

    private ConceptImpl() {
    }

    public static ConceptImpl create(String code, String codeSystem, String codeSystemName, String displayName) {
        ConceptImpl ci = new ConceptImpl();
        ci.code = code;
        ci.codeSystem = codeSystem;
        ci.codeSystemName = codeSystemName;
        ci.displayName = displayName;
        return ci;
    }

    public static ConceptImpl create(Concept concept) {
        if (concept == null) {
            return null;
        }
        if (concept instanceof ConceptImpl) {
            return ConceptImpl.class.cast(concept);
        }
        return create(concept.getCode(), concept.getCodeSystem(), concept.getCodeSystemName(), concept.getDisplayName());
    }

    public static List<Concept> create(List<Concept> fromConcepts) {
        if (fromConcepts == null) {
            return null;
        }
        List<Concept> cis = new ArrayList<>();
        for (Concept c : fromConcepts) {
            cis.add(create(c));
        }
        return cis;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getCodeSystem() {
        return codeSystem;
    }

    @Override
    public String getCodeSystemName() {
        return codeSystemName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(401, 677).append(code).append(codeSystem).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        ConceptImpl rhs = (ConceptImpl) obj;
        return new EqualsBuilder().append(code, rhs.code).append(codeSystem, rhs.codeSystem).isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("code: " + code).append("codeSystem: " + codeSystem)
                .append("codeSystemName: " + codeSystemName).append("displayName: " + displayName).toString();
    }

}
