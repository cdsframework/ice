package org.opencds.config.store.dao.je;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.dao.KnowledgeModuleDao;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.impl.KnowledgeModuleImpl;
import org.opencds.config.store.je.OpenCDSConfigStore;
import org.opencds.config.store.model.je.KMIdJe;
import org.opencds.config.store.model.je.KnowledgeModuleJe;

public class KnowledgeModuleJeDao extends GenericJeDao<KMIdJe, KnowledgeModuleJe> implements KnowledgeModuleDao {

    public KnowledgeModuleJeDao(OpenCDSConfigStore openCDSConfigStore) {
        super(openCDSConfigStore.getEntityStore().getPrimaryIndex(KMIdJe.class, KnowledgeModuleJe.class));
    }

    @Override
    public KnowledgeModule find(KMId kmId) {
        return KnowledgeModuleImpl.create(findCE(KMIdJe.create(kmId)));
    }

    @Override
    public List<KnowledgeModule> getAll() {
        List<KnowledgeModuleJe> kmjs = getAllCE();
        List<KnowledgeModule> kms = new ArrayList<>();
        for (KnowledgeModuleJe kmj : kmjs) {
            kms.add(KnowledgeModuleImpl.create(kmj));
        }
        return kms;
    }

    @Override
    public void persist(KnowledgeModule km) {
        persist(KnowledgeModuleJe.create(km));
    }

    @Override
    public void persist(List<KnowledgeModule> internal) {
        persistAllCE(KnowledgeModuleJe.create(internal));
    }
    
    @Override
    public void delete(KnowledgeModule km) {
        delete(KnowledgeModuleJe.create(km));
    }

}
