package org.opencds.config.api.util;

import org.opencds.config.api.model.EntityIdentifier;
import org.opencds.config.api.model.impl.EntityIdentifierImpl;

public class EntityIdentifierUtil
{
    public static EntityIdentifier makeEI(final String scopingEntityId, final String businessId, final String version)
    {
        return EntityIdentifierImpl.create(scopingEntityId, businessId, version);
    }

    public static EntityIdentifier makeEI(final String eiString)
    {
        return makeEI(eiString.substring(0, eiString.indexOf("^")),
                eiString.substring(eiString.indexOf("^") + 1, eiString.lastIndexOf("^")),
                eiString.substring(eiString.lastIndexOf("^") + 1));
    }

    public static String makeEIString(final EntityIdentifier ei)
    {
        if (ei == null)
            return null;

        return ei.getScopingEntityId() + "^" + ei.getBusinessId() + "^" + ei.getVersion();
    }
}
