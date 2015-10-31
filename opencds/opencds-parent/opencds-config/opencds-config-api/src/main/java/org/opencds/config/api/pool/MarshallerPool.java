package org.opencds.config.api.pool;

import javax.xml.bind.Marshaller;

import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.opencds.config.api.model.SemanticSignifier;

public class MarshallerPool extends GenericKeyedObjectPool<SemanticSignifier, Marshaller> {

    public MarshallerPool(KeyedPooledObjectFactory<SemanticSignifier, Marshaller> marshallerFactory) {
        super(marshallerFactory);
    }

}
