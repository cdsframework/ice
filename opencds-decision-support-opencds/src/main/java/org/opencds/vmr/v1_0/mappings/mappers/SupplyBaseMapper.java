package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.vmr.v1_0.internal.SupplyBase;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SupplyBaseMapper extends ClinicalStatementMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.SupplyBase source, final SupplyBase target,
            final String subjectPersonId, final String focalPersonId, final FactLists factLists) throws ImproperUsageException
    {
        final String _METHODNAME = "pullIn(): ";
        if (source == null)
            return;
        if (target == null)
        {
            final String errStr = _METHODNAME + "improper usage: target supplied is null";
            log.error(errStr);
            throw new ImproperUsageException(errStr);
        }

        ClinicalStatementMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists);

        if (source.getSupplyCode() != null)
            target.setSupplyCode(MappingUtility.cD2CDInternal(source.getSupplyCode()));
        if (source.getQuantity() != null)
            target.setQuantity(MappingUtility.pQ2PQInternal(source.getQuantity()));
        if (source.getTargetBodySite() != null)
            target.setTargetBodySite(BodySiteMapper.pullIn(source.getTargetBodySite()));

    }

    public static void pushOut(final SupplyBase source, final org.opencds.vmr.v1_0.schema.SupplyBase target)
            throws ImproperUsageException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return;
        if (target == null)
        {
            final String errStr = _METHODNAME + "improper usage: target supplied is null";
            log.error(errStr);
            throw new ImproperUsageException(errStr);
        }

        ClinicalStatementMapper.pushOut(source, target);

        if (source.getSupplyCode() != null)
            target.setSupplyCode(MappingUtility.cDInternal2CD(source.getSupplyCode()));
        if (source.getQuantity() != null)
            target.setQuantity(MappingUtility.pQInternal2PQ(source.getQuantity()));
        if (source.getTargetBodySite() != null)
            target.setTargetBodySite(BodySiteMapper.pushOut(source.getTargetBodySite()));
    }
}
