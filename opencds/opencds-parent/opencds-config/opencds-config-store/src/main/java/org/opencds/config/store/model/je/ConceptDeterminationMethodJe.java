package org.opencds.config.store.model.je;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.ConceptDeterminationMethod;
import org.opencds.config.api.model.ConceptMapping;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class ConceptDeterminationMethodJe implements ConceptDeterminationMethod, ConfigEntity<CDMIdJe> {
    @PrimaryKey
    private CDMIdJe cdmId;
    private String displayName;
    private String description;
    private Date timestamp;
    private String userId;
    /*
     * It's either:
     * - complexity in the BDB-JE model, or
     * - complexity at a higher level where we may use a cache.
     */
    private List<ConceptMapping> conceptMappings;

    private ConceptDeterminationMethodJe() {
    }

    public static ConceptDeterminationMethodJe create(CDMId cdmid, String displayName, String description,
            Date timestamp, String userId, List<ConceptMapping> conceptMappings) {
        ConceptDeterminationMethodJe cdmje = new ConceptDeterminationMethodJe();
        cdmje.cdmId = CDMIdJe.create(cdmid);
        cdmje.displayName = displayName;
        cdmje.description = description;
        cdmje.timestamp = timestamp;
        cdmje.userId = userId;
        cdmje.conceptMappings = ConceptMappingJe.create(conceptMappings);
        return cdmje;
    }

    public static ConceptDeterminationMethodJe create(ConceptDeterminationMethod cdm) {
        if (cdm == null) {
            return null;
        }
        if (cdm instanceof ConceptDeterminationMethodJe) {
            return ConceptDeterminationMethodJe.class.cast(cdm);
        }
        return create(cdm.getCDMId(), cdm.getDisplayName(), cdm.getDescription(), cdm.getTimestamp(), cdm.getUserId(),
                cdm.getConceptMappings());
    }

    public static List<ConceptDeterminationMethodJe> create(List<ConceptDeterminationMethod> cdms) {
        if (cdms == null) {
            return null;
        }
        List<ConceptDeterminationMethodJe> cdmjes = new ArrayList<>();
        for (ConceptDeterminationMethod cdm : cdms) {
            cdmjes.add(create(cdm));
        }
        return cdmjes;
    }

    @Override
    public CDMIdJe getPrimaryKey() {
        return getCDMId();
    }

    public CDMIdJe getCDMId() {
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

}
