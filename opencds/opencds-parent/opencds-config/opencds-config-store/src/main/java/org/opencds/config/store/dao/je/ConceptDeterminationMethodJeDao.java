package org.opencds.config.store.dao.je;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.dao.ConceptDeterminationMethodDao;
import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.ConceptDeterminationMethod;
import org.opencds.config.api.model.impl.ConceptDeterminationMethodImpl;
import org.opencds.config.store.je.OpenCDSConfigStore;
import org.opencds.config.store.model.je.CDMIdJe;
import org.opencds.config.store.model.je.ConceptDeterminationMethodJe;

public class ConceptDeterminationMethodJeDao extends GenericJeDao<CDMIdJe, ConceptDeterminationMethodJe> implements ConceptDeterminationMethodDao {

    public ConceptDeterminationMethodJeDao(OpenCDSConfigStore openCDSConfigStore) {
        super(openCDSConfigStore.getEntityStore().getPrimaryIndex(CDMIdJe.class,
                ConceptDeterminationMethodJe.class));
    }
    
    @Override
    public ConceptDeterminationMethod find(CDMId cdmId) {
        return ConceptDeterminationMethodImpl.create(findCE(CDMIdJe.create(cdmId)));
    }
    
    @Override
    public List<ConceptDeterminationMethod> getAll() {
        List<ConceptDeterminationMethodJe> cdmjs = getAllCE();
        List<ConceptDeterminationMethod> cdms = new ArrayList<>();
        for (ConceptDeterminationMethodJe cdmj : cdmjs) {
            cdms.add(ConceptDeterminationMethodImpl.create(cdmj));
        }
        return cdms;
    }
    
    @Override
    public void persist(ConceptDeterminationMethod cdm) {
        persist(ConceptDeterminationMethodJe.create(cdm));
    }
    
    @Override
    public void persist(List<ConceptDeterminationMethod> internal) {
        persistAllCE(ConceptDeterminationMethodJe.create(internal));
    }
    
    @Override
    public void delete(ConceptDeterminationMethod cdm) {
        delete(ConceptDeterminationMethodJe.create(cdm));
    }

}
