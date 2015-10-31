package org.opencds.config.store.dao.je;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.store.dao.Dao;
import org.opencds.config.store.model.je.ConfigEntity;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.PrimaryIndex;

public abstract class GenericJeDao<PK, CE extends ConfigEntity<PK>> implements Dao<PK, CE> {

    private PrimaryIndex<PK, CE> primaryIndex;

    protected GenericJeDao(PrimaryIndex<PK, CE> primaryIndex) {
        this.primaryIndex = primaryIndex;
    }

    @Override
    public CE findCE(PK pk) {
        return primaryIndex.get(pk);
    }

    @Override
    public List<CE> getAllCE() {
        List<CE> ces = new ArrayList<>();
        EntityCursor<CE> cursor = primaryIndex.entities();
        try {
            for (CE ce : cursor) {
                ces.add(ce);
            }
        } finally {
            // we have to close the cursor, otherwise we'll likely get resource
            // leaks
            cursor.close();
        }
        return ces;
    }

    @Override
    public void persist(CE ce) {
        primaryIndex.put(ce);
    }

    @Override
    public void persistAllCE(List<CE> ces) {
        for (CE ce : ces) {
            primaryIndex.put(ce);
        }
    }

    @Override
    public void delete(CE ce) {
        primaryIndex.delete(ce.getPrimaryKey());
    }

}
