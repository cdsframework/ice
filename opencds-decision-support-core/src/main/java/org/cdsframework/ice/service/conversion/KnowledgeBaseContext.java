package org.cdsframework.ice.service.conversion;

import org.omg.dss.EntityIdentifier;

record KnowledgeBaseContext(String knowledgeBase,
                            String kmId,
                            EntityIdentifier kmEntityIdentifier)
{
}
