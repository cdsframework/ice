package org.opencds.config.service;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.opencds.common.cache.CacheRegion;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.dao.ConceptDeterminationMethodDao;
import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.ConceptDeterminationMethod;
import org.opencds.config.api.model.SecondaryCDM;
import org.opencds.config.api.model.SupportMethod;
import org.opencds.config.api.service.ConceptDeterminationMethodService;

public class ConceptDeterminationMethodServiceImpl implements ConceptDeterminationMethodService
{
    private static final CacheRegion<CDMId, ConceptDeterminationMethod> CDM =
            CacheRegion.create(CDMId.class, ConceptDeterminationMethod.class);

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private final ConceptDeterminationMethodDao dao;
    private final CacheService cacheService;

    public ConceptDeterminationMethodServiceImpl(final ConceptDeterminationMethodDao dao, final CacheService cacheService)
    {
        this.dao = dao;
        this.cacheService = cacheService;
        this.cacheService.putAll(CDM, buildPairs(this.dao.getAll()));
    }

    @Override
    public ConceptDeterminationMethod find(final CDMId cdmId)
    {
        return cacheService.get(CDM, cdmId);
    }

    @Override
    public List<ConceptDeterminationMethod> getAll()
    {
        return List.copyOf(cacheService.getAllValues(CDM));
    }

    @Override
    public void persist(final ConceptDeterminationMethod cdm)
    {
        dao.persist(cdm);
        cacheService.put(CDM, cdm.getCDMId(), cdm);
        tellObservers();
    }

    @Override
    public void persist(final List<ConceptDeterminationMethod> internal)
    {
        dao.persist(internal);
        cacheService.putAll(CDM, buildPairs(internal));
        tellObservers();
    }

    @Override
    public void delete(final CDMId cdmId)
    {
        final ConceptDeterminationMethod cdm = find(cdmId);
        if (cdm != null)
        {
            dao.delete(cdm);
            cacheService.evict(CDM, cdm.getCDMId());
            tellObservers();
        }
    }

    @Override
    public Map<ConceptDeterminationMethod, SupportMethod> find(final List<SecondaryCDM> secondaryCDMs)
    {
        return secondaryCDMs.stream()
                .map(sec -> Pair.of(find(sec.getCDMId()), sec.getSupportMethod()))
                .filter(pair -> pair.getLeft() != null)
                .collect(Collectors.toUnmodifiableMap(Pair::getLeft, Pair::getRight));
    }

    private void tellObservers()
    {
        support.firePropertyChange("conceptDeterminationMethod", null, null);
    }

    private Map<CDMId, ConceptDeterminationMethod> buildPairs(final List<ConceptDeterminationMethod> cdms)
    {
        return cdms.stream().collect(Collectors.toUnmodifiableMap(ConceptDeterminationMethod::getCDMId, Function.identity()));
    }

    @Override
    public void addPropertyChangeListener(final PropertyChangeListener listener)
    {
        support.addPropertyChangeListener(listener);
    }
}
