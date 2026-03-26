package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.vmr.v1_0.internal.EncounterBase;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class EncounterBaseMapper extends ClinicalStatementMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.EncounterBase source, final EncounterBase target,
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

        if (source.getEncounterType() != null)
            target.setEncounterType(MappingUtility.cD2CDInternal(source.getEncounterType()));

    }

    public static void pushOut(final EncounterBase source, final org.opencds.vmr.v1_0.schema.EncounterBase target)
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

        if (source.getEncounterType() != null)
            target.setEncounterType(MappingUtility.cDInternal2CD(source.getEncounterType()));
    }
}
