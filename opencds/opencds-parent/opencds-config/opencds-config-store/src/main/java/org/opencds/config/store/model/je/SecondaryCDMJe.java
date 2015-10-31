package org.opencds.config.store.model.je;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.SecondaryCDM;
import org.opencds.config.api.model.SupportMethod;

import com.sleepycat.persist.model.Persistent;

@Persistent
public class SecondaryCDMJe implements SecondaryCDM {

    private CDMIdJe cdmId;
    private SupportMethod supportMethod;

    private SecondaryCDMJe() {
    }

    public static SecondaryCDMJe create(CDMId cdmId, SupportMethod supportMethod) {
        SecondaryCDMJe scdmje = new SecondaryCDMJe();
        scdmje.cdmId = CDMIdJe.create(cdmId);
        scdmje.supportMethod = supportMethod;
        return scdmje;
    }
    
    public static SecondaryCDMJe create(SecondaryCDM scdm) {
        if (scdm == null) {
            return null;
        }
        if (scdm instanceof SecondaryCDMJe) {
            return SecondaryCDMJe.class.cast(scdm);
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

}
