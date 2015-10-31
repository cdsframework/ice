package org.opencds.config.store.dao;

import java.util.List;

public interface Dao<K, E> {
    E findCE(K pk);
    
    List<E> getAllCE();
    
    void persist(E e);

    void delete(E e);

    void persistAllCE(List<E> ees);
    
}
