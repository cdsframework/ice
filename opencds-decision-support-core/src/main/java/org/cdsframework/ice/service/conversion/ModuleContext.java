package org.cdsframework.ice.service.conversion;

import org.omg.dss.EntityIdentifier;

record ModuleContext(String moduleCanonical,
                     String kmId,
                     EntityIdentifier kmEntityIdentifier)
{
}