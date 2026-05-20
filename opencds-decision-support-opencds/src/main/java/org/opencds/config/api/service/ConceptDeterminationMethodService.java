package org.opencds.config.api.service;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.opencds.config.api.dao.ConceptDeterminationMethodDao;
import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.ConceptDeterminationMethod;
import org.opencds.config.api.model.SecondaryCDM;
import org.opencds.config.api.model.SupportMethod;

public class ConceptDeterminationMethodService
{
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private final Map<CDMId, ConceptDeterminationMethod> map;

    public ConceptDeterminationMethodService(final ConceptDeterminationMethodDao dao)
    {
        this.map =
                dao.getAll().stream().collect(Collectors.toConcurrentMap(ConceptDeterminationMethod::cdmId, Function.identity()));
    }

    public ConceptDeterminationMethod find(final CDMId cdmId)
    {
        return map.get(cdmId);
    }

    public List<ConceptDeterminationMethod> getAll()
    {
        return List.copyOf(map.values());
    }

    public Map<ConceptDeterminationMethod, SupportMethod> find(final List<SecondaryCDM> secondaryCDMs)
    {
        return secondaryCDMs.stream()
                .map(sec -> Pair.of(find(sec.cdmId()), sec.supportMethod()))
                .filter(pair -> pair.getLeft() != null)
                .collect(Collectors.toUnmodifiableMap(Pair::getLeft, Pair::getRight));
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener)
    {
        support.addPropertyChangeListener(listener);
    }
}
