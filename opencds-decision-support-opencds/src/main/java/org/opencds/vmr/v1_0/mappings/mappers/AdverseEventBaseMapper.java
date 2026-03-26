package org.opencds.vmr.v1_0.mappings.mappers;

import java.util.ArrayList;

import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.vmr.v1_0.internal.AdverseEventBase;
import org.opencds.vmr.v1_0.internal.BodySite;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AdverseEventBaseMapper extends ClinicalStatementMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.AdverseEventBase source, final AdverseEventBase target,
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
            ClinicalStatementMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new ImproperUsageException(errStr);
        }

        target.setAdverseEventCode(MappingUtility.cD2CDInternal(source.getAdverseEventCode()));
        if (source.getAdverseEventAgent() != null)
            target.setAdverseEventAgent(MappingUtility.cD2CDInternal(source.getAdverseEventAgent()));
        if (source.getAdverseEventTime() != null)
            target.setAdverseEventTime(MappingUtility.iVLTS2IVLDateInternal(source.getAdverseEventTime()));
        if (source.getDocumentationTime() != null)
            target.setDocumentationTime(MappingUtility.iVLTS2IVLDateInternal(source.getDocumentationTime()));
        if (source.getAffectedBodySite() != null)
        {
            target.setAffectedBodySite(new ArrayList<>());
            for (final org.opencds.vmr.v1_0.schema.BodySite oneBodySite : source.getAffectedBodySite())
            {
                if (oneBodySite != null)
                    target.getAffectedBodySite().add(BodySiteMapper.pullIn(oneBodySite));
            }
        }

    }

    public static void pushOut(final AdverseEventBase source, final org.opencds.vmr.v1_0.schema.AdverseEventBase target)
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

        target.setAdverseEventCode(MappingUtility.cDInternal2CD(source.getAdverseEventCode()));
        if (source.getAdverseEventAgent() != null)
            target.setAdverseEventAgent(MappingUtility.cDInternal2CD(source.getAdverseEventAgent()));
        if (source.getAdverseEventTime() != null)
            target.setAdverseEventTime(MappingUtility.iVLDateInternal2IVLTS(source.getAdverseEventTime()));
        if (source.getDocumentationTime() != null)
            target.setDocumentationTime(MappingUtility.iVLDateInternal2IVLTS(source.getDocumentationTime()));
        if (source.getAffectedBodySite() != null)
        {
            for (final BodySite oneBodySite : source.getAffectedBodySite())
            {
                if (oneBodySite != null)
                    target.getAffectedBodySite().add(BodySiteMapper.pushOut(oneBodySite));
            }
        }
    }
}
