package org.opencds.terminology.apelon.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class SetUtil {

    public static <T> Set<T> asSet(T t) {
        Set<T> set = new HashSet<>();                
        if (t != null) { 
            set.add(t);
        }
        return set;
    }
    
    public static <T> Set<T> asSet(Collection<T> t) {
        Set<T> set = new HashSet<>();                
        if (t != null) {
            for (T te : t) {
                set.add(te);
            }
        }
        return set;
    }
    
    public static <T> Set<T> asSortedSet(Collection<T> t, Comparator<T> comparator) {
        SortedSet<T> set = new TreeSet<>(comparator);
        if (t != null) {
            for (T te : t) {
                set.add(te);
            }
        }
        return set;
    }
    
}
