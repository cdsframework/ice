package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.ConceptDeterminationMethod;
import org.opencds.config.api.model.ConceptMapping;

public class ConceptDeterminationMethodImpl implements ConceptDeterminationMethod {
    private CDMIdImpl cdmId;
    private String displayName;
    private String description;
    private Date timestamp;
    private String userId;
    private List<ConceptMapping> conceptMappings;

    private ConceptDeterminationMethodImpl() { }
    
    public static ConceptDeterminationMethodImpl create(CDMId cdmId, String displayName, String description, Date timestamp, String userId, List<ConceptMapping> conceptMappings) {
        ConceptDeterminationMethodImpl cdmi = new ConceptDeterminationMethodImpl();
        cdmi.cdmId = CDMIdImpl.create(cdmId);
        cdmi.displayName = displayName;
        cdmi.description = description;
        cdmi.timestamp = timestamp;
        cdmi.userId = userId;
        cdmi.conceptMappings = ConceptMappingImpl.create(conceptMappings);
        return cdmi;
    }
    
    public static ConceptDeterminationMethodImpl create(ConceptDeterminationMethod cdm) {
        if (cdm == null) {
            return null;
        }
        if (cdm instanceof ConceptDeterminationMethodImpl) {
            return ConceptDeterminationMethodImpl.class.cast(cdm);
        }
        return create(cdm.getCDMId(), cdm.getDisplayName(), cdm.getDescription(), cdm.getTimestamp(), cdm.getUserId(), cdm.getConceptMappings());
    }
    
    public static List<ConceptDeterminationMethodImpl> create(List<ConceptDeterminationMethod> cdms) {
        if (cdms == null) {
            return null;
        }
        List<ConceptDeterminationMethodImpl> cdmis = new ArrayList<>();
        for (ConceptDeterminationMethod cdm : cdms) {
            cdmis.add(create(cdm));
        }
        return cdmis;
    }
    
    @Override
    public CDMId getCDMId() {
        return cdmId;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public List<ConceptMapping> getConceptMappings() {
        return Collections.unmodifiableList(conceptMappings);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
        .append("cdmId: " + cdmId)
        .toString();
    }
}
