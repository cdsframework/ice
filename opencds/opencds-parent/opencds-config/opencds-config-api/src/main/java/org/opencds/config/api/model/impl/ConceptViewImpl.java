package org.opencds.config.api.model.impl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.opencds.config.api.model.Concept;
import org.opencds.config.api.model.ConceptView;

public class ConceptViewImpl implements ConceptView {

    private final Concept toConcept;
    private final String cdmCode;

    public ConceptViewImpl(Concept toConcept, String cdmCode) {
        this.toConcept = toConcept;
        this.cdmCode = cdmCode;
    }

    @Override
    public Concept getToConcept() {
        return toConcept;
    }

    @Override
    public String getCdmCode() {
        return cdmCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
        .append("toConcept: " + toConcept)
        .append("cdm: " + cdmCode)
        .toString();
    }
    
}
