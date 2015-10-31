package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.SecondaryCDM;
import org.opencds.config.api.model.SupportMethod;

public class SecondaryCDMImpl implements SecondaryCDM {

    private CDMId cdmId;
    private SupportMethod supportMethod;

    private SecondaryCDMImpl() {
    }

    public static SecondaryCDMImpl create(CDMId cdmId, SupportMethod supportMethod) {
        SecondaryCDMImpl scdm = new SecondaryCDMImpl();
        scdm.cdmId = CDMIdImpl.create(cdmId);
        scdm.supportMethod = supportMethod;
        return scdm;
    }
    
    public static SecondaryCDMImpl create(SecondaryCDM scdm) {
        if (scdm == null) {
            return null;
        }
        if (scdm instanceof SecondaryCDMImpl) {
            return SecondaryCDMImpl.class.cast(scdm);
        }
        return create(scdm.getCDMId(), scdm.getSupportMethod());
    }

    public static List<SecondaryCDM> create(List<SecondaryCDM> secondaryCDMs) {
        if (secondaryCDMs == null) {
            return null;
        }
        List<SecondaryCDM> scdms = new ArrayList<>();
        for (SecondaryCDM scdm : secondaryCDMs) {
            scdms.add(create(scdm));
        }
        return scdms;
    }

    @Override
    public CDMId getCDMId() {
        return cdmId;
    }

    @Override
    public SupportMethod getSupportMethod() {
        return supportMethod;
    }
    
    @Override
    public String toString() {
        return "SecondaryCDMImpl [cdmId= " + cdmId + ", supportMethod= " + supportMethod + "]";
    }


}
