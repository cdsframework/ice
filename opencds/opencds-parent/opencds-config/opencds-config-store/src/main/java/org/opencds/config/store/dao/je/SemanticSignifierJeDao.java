package org.opencds.config.store.dao.je;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.dao.SemanticSignifierDao;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.api.model.impl.SemanticSignifierImpl;
import org.opencds.config.store.je.OpenCDSConfigStore;
import org.opencds.config.store.model.je.SSIdJe;
import org.opencds.config.store.model.je.SemanticSignifierJe;

public class SemanticSignifierJeDao extends GenericJeDao<SSIdJe, SemanticSignifierJe> implements SemanticSignifierDao {

    public SemanticSignifierJeDao(OpenCDSConfigStore openCDSConfigStore) {
        super(openCDSConfigStore.getEntityStore().getPrimaryIndex(SSIdJe.class, SemanticSignifierJe.class));
    }

    @Override
    public SemanticSignifier find(SSId ssId) {
        return SemanticSignifierImpl.create(findCE(SSIdJe.create(ssId)));
    }

    @Override
    public List<SemanticSignifier> getAll() {
        List<SemanticSignifierJe> ssjs = getAllCE();
        List<SemanticSignifier> sss = new ArrayList<>();
        for (SemanticSignifierJe ssj : ssjs) {
            sss.add(SemanticSignifierImpl.create(ssj));
        }
        return sss;
    }

    @Override
    public void persist(SemanticSignifier ss) {
        persist(SemanticSignifierJe.create(ss));
    }

    @Override
    public void persist(List<SemanticSignifier> sses) {
        persistAllCE(SemanticSignifierJe.create(sses));
    }
    
    @Override
    public void delete(SemanticSignifier ss) {
        delete(SemanticSignifierJe.create(ss));
    }

}
