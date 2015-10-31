package org.opencds.config.api.pool;

import javax.xml.bind.Unmarshaller;

import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.opencds.config.api.model.SemanticSignifier;

public class UnmarshallerPool extends GenericKeyedObjectPool<SemanticSignifier, Unmarshaller> {

    public UnmarshallerPool(KeyedPooledObjectFactory<SemanticSignifier, Unmarshaller> unmarshallerFactory) {
        super(unmarshallerFactory);
    }

}
