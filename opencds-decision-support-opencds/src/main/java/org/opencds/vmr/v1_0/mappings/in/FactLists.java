package org.opencds.vmr.v1_0.mappings.in;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.ToString;

@ToString
public class FactLists
{
    private final Map<Class<?>, List<?>> factListCache = new ConcurrentHashMap<>();
    @Getter
    private final Map<String, LocalDate> parsedDatesCache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <F> void put(final Class<F> clazz, final List<F> list)
    {
        if (list == null)
            return;

        ((List<F>) factListCache.computeIfAbsent(clazz, _ -> new ArrayList<>())).addAll(list);
    }

    @SuppressWarnings("unchecked")
    public <F> void put(final Class<F> clazz, final F fact)
    {
        ((List<F>) factListCache.computeIfAbsent(clazz, _ -> new ArrayList<>())).add(fact);
    }

    @SuppressWarnings("unchecked")
    public <F> List<F> get(final Class<F> clazz)
    {
        return (List<F>) factListCache.get(clazz);
    }

    public void populate(final Map<Class<?>, List<?>> allFactLists)
    {
        for (final Entry<Class<?>, List<?>> entry : factListCache.entrySet())
            allFactLists.put(entry.getKey(), new ArrayList<>(entry.getValue()));
    }
}
