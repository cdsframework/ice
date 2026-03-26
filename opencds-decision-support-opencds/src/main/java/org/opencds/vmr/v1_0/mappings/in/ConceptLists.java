package org.opencds.vmr.v1_0.mappings.in;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lombok.ToString;

@ToString
public class ConceptLists
{
    private final ConcurrentMap<Class<?>, List<?>> conceptListMap = new ConcurrentHashMap<>();

    public <C> List<C> get(final Class<C> clazz)
    {
        return (List<C>) conceptListMap.get(clazz);
    }

    @SuppressWarnings("unchecked")
    public <C> void put(final Class<C> clazz, final C c)
    {
        ensureInitialized(clazz);
        ((List<C>) conceptListMap.get(clazz)).add(c);
    }

    private <C> void ensureInitialized(final Class<C> clazz)
    {
        conceptListMap.putIfAbsent(clazz, new ArrayList<C>());
    }

    public Iterable<Entry<Class<?>, List<?>>> iterable()
    {
        return conceptListMap.entrySet();
    }
}
