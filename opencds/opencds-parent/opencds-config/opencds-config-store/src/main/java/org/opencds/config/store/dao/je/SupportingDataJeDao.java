package org.opencds.config.store.dao.je;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.dao.SupportingDataDao;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.SupportingData;
import org.opencds.config.api.model.impl.SupportingDataImpl;
import org.opencds.config.store.je.OpenCDSConfigStore;
import org.opencds.config.store.model.je.SDIdJe;
import org.opencds.config.store.model.je.SupportingDataJe;

public class SupportingDataJeDao extends GenericJeDao<SDIdJe, SupportingDataJe> implements SupportingDataDao {

    public SupportingDataJeDao(OpenCDSConfigStore openCDSConfigStore) {
        super(openCDSConfigStore.getEntityStore().getPrimaryIndex(SDIdJe.class, SupportingDataJe.class));
    }

    @Override
    public SupportingData find(KMId kmId, String identifier) {
        return SupportingDataImpl.create(findCE(SDIdJe.create(kmId, identifier)));
    }

    @Override
    public List<SupportingData> getAll() {
        List<SupportingDataJe> sdjs = getAllCE();
        List<SupportingData> sds = new ArrayList<>();
        for (SupportingDataJe sdj : sdjs) {
            sds.add(SupportingDataImpl.create(sdj));
        }
        return sds;
    }

    @Override
    public void persist(SupportingData sd) {
        persist(SupportingDataJe.create(sd));
    }

    @Override
    public void delete(SupportingData sd) {
        delete(SupportingDataJe.create(sd));
    }

    @Override
    public List<SupportingData> find(KMId kmId) {
        List<SupportingDataJe> sdjs = getAllCE();
        List<SupportingData> sds = new ArrayList<>();
        for (SupportingDataJe sdj : sdjs) {
            if (sdj.getPrimaryKey().getKMId().equals(kmId)) {
                sds.add(SupportingDataImpl.create(sdj));
            }
        }
        return sds;
    }

}
