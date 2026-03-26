package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.vmr.v1_0.internal.DoseRestriction;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

public class DoseRestrictionMapper
{
    public static DoseRestriction pullIn(final org.opencds.vmr.v1_0.schema.DoseRestriction source, final DoseRestriction target,
            final String parentId, final String subjectPersonId, final String focalPersonId, final FactLists factLists)
    {
        if (source == null)
            return null;

        target.setMaxDoseForInterval(MappingUtility.pQ2PQInternal(source.getMaxDoseForInterval()));
        target.setTimeInterval(MappingUtility.pQ2PQInternal(source.getTimeInterval()));
        return target;
    }

    public static org.opencds.vmr.v1_0.schema.DoseRestriction pushOut(final DoseRestriction source)
    {
        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.DoseRestriction target = new org.opencds.vmr.v1_0.schema.DoseRestriction();
        target.setMaxDoseForInterval(MappingUtility.pQInternal2PQ(source.getMaxDoseForInterval()));
        target.setTimeInterval(MappingUtility.pQInternal2PQ(source.getTimeInterval()));

        return target;
    }
}
