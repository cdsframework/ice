package org.opencds.config.mapper;

import java.util.ArrayList;
import java.util.List;

import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.utilities.XMLDateUtility;
import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.ConceptDeterminationMethod;
import org.opencds.config.api.model.ConceptMapping;
import org.opencds.config.api.model.impl.CDMIdImpl;
import org.opencds.config.api.model.impl.ConceptDeterminationMethodImpl;
import org.opencds.config.schema.ConceptDeterminationMethods;

public abstract class ConceptDeterminationMethodMapper {

    public static ConceptDeterminationMethod internal(org.opencds.config.schema.ConceptDeterminationMethod external) {
        if (external == null) {
            return null;
        }
        List<ConceptMapping> conceptMappings = new ArrayList<>();

        for (org.opencds.config.schema.ConceptMapping cm : external.getConceptMapping()) {
            ConceptMapping cmi = ConceptMappingMapper.internal(cm);
            conceptMappings.add(cmi);
        }
        
        CDMId cdmId = CDMIdImpl.create(external.getCodeSystem(), external.getCode(), external.getVersion());
        
        return ConceptDeterminationMethodImpl.create(cdmId, external.getDisplayName(), external.getDescription(),
                XMLDateUtility.xmlGregorian2Gregorian(external.getTimestamp()).getTime(), external.getUserId(),
                conceptMappings);

    }

    public static List<ConceptDeterminationMethod> internal(ConceptDeterminationMethods cdms) {
        if (cdms == null) {
            return null;
        }
        List<ConceptDeterminationMethod> internalCDMs = new ArrayList<>();
        for (org.opencds.config.schema.ConceptDeterminationMethod cdm : cdms.getConceptDeterminationMethod()) {
            internalCDMs.add(internal(cdm));
        }
        return internalCDMs;
    }

    public static org.opencds.config.schema.ConceptDeterminationMethod external(ConceptDeterminationMethod internal) {
        if (internal == null) {
            return null;
        }
        org.opencds.config.schema.ConceptDeterminationMethod external = new org.opencds.config.schema.ConceptDeterminationMethod();

        external.setCode(internal.getCDMId().getCode());
        external.setCodeSystem(internal.getCDMId().getCodeSystem());
        external.setVersion(internal.getCDMId().getVersion());

        external.setDisplayName(internal.getDisplayName());
        external.setTimestamp(XMLDateUtility.date2XMLGregorian(internal.getTimestamp()));
        external.setUserId(internal.getUserId());

        for (ConceptMapping cm : internal.getConceptMappings()) {
            external.getConceptMapping().add(ConceptMappingMapper.external(cm));
        }

        return external;
    }

    public static ConceptDeterminationMethods external(List<ConceptDeterminationMethod> cdmList) {
        if (cdmList == null) {
            return null;
        }
        ConceptDeterminationMethods cdms = new ConceptDeterminationMethods();
        for (ConceptDeterminationMethod cdm : cdmList) {
            cdms.getConceptDeterminationMethod().add(external(cdm));
        }
        return cdms;
    }

}
