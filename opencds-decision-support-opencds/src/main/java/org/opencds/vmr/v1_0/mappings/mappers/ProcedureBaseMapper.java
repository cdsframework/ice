package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.vmr.v1_0.internal.ProcedureBase;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ProcedureBaseMapper extends ClinicalStatementMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.ProcedureBase source, final ProcedureBase target,
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

        target.setProcedureCode(MappingUtility.cD2CDInternal(source.getProcedureCode()));
        if (source.getProcedureCode() != null)
            target.setProcedureMethod(MappingUtility.cD2CDInternal(source.getProcedureMethod()));
        if (source.getApproachBodySite() != null)
            target.setApproachBodySite(BodySiteMapper.pullIn(source.getApproachBodySite()));
        if (source.getTargetBodySite() != null)
            target.setTargetBodySite(BodySiteMapper.pullIn(source.getTargetBodySite()));

    }

    public static void pushOut(final ProcedureBase source, final org.opencds.vmr.v1_0.schema.ProcedureBase target)
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

        target.setProcedureCode(MappingUtility.cDInternal2CD(source.getProcedureCode()));
        if (source.getProcedureCode() != null)
            target.setProcedureMethod(MappingUtility.cDInternal2CD(source.getProcedureMethod()));
        if (source.getApproachBodySite() != null)
            target.setApproachBodySite(BodySiteMapper.pushOut(source.getApproachBodySite()));
        if (source.getTargetBodySite() != null)
            target.setTargetBodySite(BodySiteMapper.pushOut(source.getTargetBodySite()));
    }
}
