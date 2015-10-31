package org.opencds.config.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import org.opencds.common.cache.OpencdsCache.CacheRegion;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.dao.ConceptDeterminationMethodDao;
import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.ConceptDeterminationMethod;
import org.opencds.config.api.model.SecondaryCDM;
import org.opencds.config.api.model.SupportMethod;
import org.opencds.config.api.service.ConceptDeterminationMethodService;

import com.google.common.collect.ImmutableList;

public class ConceptDeterminationMethodServiceImpl extends Observable implements ConceptDeterminationMethodService {

    private final ConceptDeterminationMethodDao dao;
    private final CacheService cacheService;

    public ConceptDeterminationMethodServiceImpl(ConceptDeterminationMethodDao dao, CacheService cacheService) {
        this.dao = dao;
        this.cacheService = cacheService;
        this.cacheService.putAll(CDMCacheRegion.CDM, buildPairs(this.dao.getAll()));
    }

    @Override
    public ConceptDeterminationMethod find(CDMId cdmId) {
        return cacheService.get(CDMCacheRegion.CDM, cdmId);
    }

    @Override
    public List<ConceptDeterminationMethod> getAll() {
        Set<ConceptDeterminationMethod> cdmList = cacheService.getAll(CDMCacheRegion.CDM);
        return ImmutableList.<ConceptDeterminationMethod>builder().addAll(cdmList).build();
    }

    @Override
    public void persist(ConceptDeterminationMethod cdm) {
        dao.persist(cdm);
        cacheService.put(CDMCacheRegion.CDM, cdm.getCDMId(), cdm);
        // will reload all ConceptServices for all KMs
        tellObservers();
    }

    @Override
    public void persist(List<ConceptDeterminationMethod> internal) {
        dao.persist(internal);
        cacheService.putAll(CDMCacheRegion.CDM, buildPairs(internal));
        tellObservers();
    }
    
    @Override
    public void delete(CDMId cdmId) {
        ConceptDeterminationMethod cdm = find(cdmId);
        if (cdm != null) {
            dao.delete(cdm);
            cacheService.evict(CDMCacheRegion.CDM, cdm.getCDMId());
            tellObservers();
        }
    }
    
    @Override
    public Map<ConceptDeterminationMethod, SupportMethod> find(List<SecondaryCDM> secondaryCDMs) {
        Map<ConceptDeterminationMethod, SupportMethod> cdms = new HashMap<>();
        for (SecondaryCDM sec : secondaryCDMs) {
            cdms.put(find(sec.getCDMId()), sec.getSupportMethod());
        }
        return cdms;
    }
    
    private void tellObservers() {
        setChanged();
        notifyObservers();
    }
    
    private Map<CDMId, ConceptDeterminationMethod> buildPairs(List<ConceptDeterminationMethod> cdms) {
        Map<CDMId, ConceptDeterminationMethod> cachables = new HashMap<>();
        for (ConceptDeterminationMethod cdm : cdms) {
            cachables.put(cdm.getCDMId(), cdm);
        }
        return cachables;
    }

    private enum CDMCacheRegion implements CacheRegion {
        CDM(ConceptDeterminationMethod.class);

        private Class<?> type;
        
        CDMCacheRegion(Class<?> type) {
            this.type = type;
        }
        
        @Override
        public boolean supports(Class<?> type) {
            return this.type.isAssignableFrom(type);
        }
    }

}
