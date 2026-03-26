package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.vmr.v1_0.internal.BodySite;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

public class BodySiteMapper
{
    public static BodySite pullIn(final org.opencds.vmr.v1_0.schema.BodySite source)
    {
        if (source == null)
            return null;

        final BodySite target = new BodySite();
        if (source.getBodySiteCode() != null)
            target.setBodySiteCode(MappingUtility.cD2CDInternal(source.getBodySiteCode()));
        if (source.getLaterality() != null)
            target.setLaterality(MappingUtility.cD2CDInternal(source.getLaterality()));

        return target;
    }

    public static org.opencds.vmr.v1_0.schema.BodySite pushOut(final BodySite source)
    {
        if (source == null)
            return null;
        final org.opencds.vmr.v1_0.schema.BodySite target = new org.opencds.vmr.v1_0.schema.BodySite();
        if (source.getBodySiteCode() != null)
            target.setBodySiteCode(MappingUtility.cDInternal2CD(source.getBodySiteCode()));
        if (source.getLaterality() != null)
            target.setLaterality(MappingUtility.cDInternal2CD(source.getLaterality()));

        return target;
    }
}
