package org.opencds.vmr.v1_0.mappings.in;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lombok.ToString;

@ToString
public class FactLists
{
    private final ConcurrentMap<Class<?>, List<?>> factListMap = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <F> void put(final Class<F> clazz, final List<F> list)
    {
        if (list != null)
        {
            for (final F f : list)
            {
                ensureInitialized(clazz);
                ((List<F>) factListMap.get(clazz)).add(f);
            }
        }
    }

    private <F> void ensureInitialized(final Class<F> clazz)
    {
        if (factListMap.get(clazz) == null)
            factListMap.putIfAbsent(clazz, new ArrayList<F>());
    }

    @SuppressWarnings("unchecked")
    public <F> void put(final Class<F> clazz, final F fact)
    {
        ensureInitialized(clazz);
        ((List<F>) factListMap.get(clazz)).add(fact);
    }

    @SuppressWarnings("unchecked")
    public <F> List<F> get(final Class<F> clazz)
    {
        return (List<F>) factListMap.get(clazz);
    }

    public void populateAllFactLists(final Map<Class<?>, List<?>> allFactLists)
    {
        for (final Entry<Class<?>, List<?>> entry : factListMap.entrySet())
            allFactLists.put(entry.getKey(), new ArrayList<>(entry.getValue()));
    }
}
