package org.cdsframework.ice.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CollectionUtils
{
    /**
     * Return a Collection that contains String arguments in all of the collections, or an empty set if there is no intersection of common elements (or argument provided is null).
     */
    @SafeVarargs
    public static Collection<String> intersectionOfStringCollections(final Collection<String>... pCollection)
    {
        if (pCollection == null)
            return Set.of();

        if (pCollection.length == 0 || pCollection.length == 1)
            return pCollection[0];

        final Set<String> lIntersectionSet = new HashSet<>(pCollection[0]);
        for (final Collection<String> lCollection : pCollection)
            lIntersectionSet.retainAll(lCollection);

        return lIntersectionSet;
    }

    /**
     * Return a Collection that contains Comparable arguments in all of the collections, or an empty set if there is no intersection of common elements (or argument provided is null).
     */
    @SafeVarargs
    public static <T extends Comparable<T>> Collection<T> intersectionOfCollections(final Collection<T>... pCollection)
    {
        if (pCollection == null)
            return new HashSet<>();

        if (pCollection.length == 0 || pCollection.length == 1)
            return pCollection[0];

        final Set<T> lIntersectionSet = new HashSet<>(pCollection[0]);
        for (final Collection<T> lCollection : pCollection)
            lIntersectionSet.retainAll(lCollection);

        return lIntersectionSet;
    }
}
