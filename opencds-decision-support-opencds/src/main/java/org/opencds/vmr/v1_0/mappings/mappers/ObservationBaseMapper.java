package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.vmr.v1_0.internal.ClinicalStatement;
import org.opencds.vmr.v1_0.internal.ObservationBase;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ObservationBaseMapper extends ClinicalStatementMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.ObservationBase source, final ObservationBase target,
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

        try
        {
            ClinicalStatementMapper.pullIn((org.opencds.vmr.v1_0.schema.ClinicalStatement) source, (ClinicalStatement) target,
                    subjectPersonId, focalPersonId, factLists);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new ImproperUsageException(errStr);
        }
        target.setObservationFocus(MappingUtility.cD2CDInternal(source.getObservationFocus()));
        if (source.getObservationMethod() != null)
            target.setObservationMethod(MappingUtility.cD2CDInternal(source.getObservationMethod()));
        if (source.getTargetBodySite() != null)
            target.setTargetBodySite(BodySiteMapper.pullIn(source.getTargetBodySite()));

    }

    public static void pushOut(final ObservationBase source, final org.opencds.vmr.v1_0.schema.ObservationBase target)
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

        target.setObservationFocus(MappingUtility.cDInternal2CD(source.getObservationFocus()));
        if (source.getObservationMethod() != null)
            target.setObservationMethod(MappingUtility.cDInternal2CD(source.getObservationMethod()));
        if (source.getTargetBodySite() != null)
            target.setTargetBodySite(BodySiteMapper.pushOut(source.getTargetBodySite()));
    }
}
