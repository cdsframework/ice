package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.vmr.v1_0.internal.ProblemBase;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ProblemBaseMapper extends ClinicalStatementMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.ProblemBase source, final ProblemBase target,
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

        target.setProblemCode(MappingUtility.cD2CDInternal(source.getProblemCode()));
        if (source.getProblemEffectiveTime() != null)
            target.setProblemEffectiveTime(
                    MappingUtility.iVLTS2IVLDateInternal(source.getProblemEffectiveTime(), factLists.getParsedDatesCache()));
        if (source.getDiagnosticEventTime() != null)
            target.setDiagnosticEventTime(
                    MappingUtility.iVLTS2IVLDateInternal(source.getDiagnosticEventTime(), factLists.getParsedDatesCache()));

    }

    public static void pushOut(final ProblemBase source, final org.opencds.vmr.v1_0.schema.ProblemBase target)
            throws ImproperUsageException
    {
        final String _METHODNAME = "pullOut(): ";

        if (source == null)
            return;
        if (target == null)
        {
            final String errStr = _METHODNAME + "improper usage: target supplied is null";
            log.error(errStr);
            throw new ImproperUsageException(errStr);
        }

        ClinicalStatementMapper.pushOut(source, target);

        target.setProblemCode(MappingUtility.cDInternal2CD(source.getProblemCode()));
        if (source.getProblemEffectiveTime() != null)
            target.setProblemEffectiveTime(MappingUtility.iVLDateInternal2IVLTS(source.getProblemEffectiveTime()));
        if (source.getDiagnosticEventTime() != null)
            target.setDiagnosticEventTime(MappingUtility.iVLDateInternal2IVLTS(source.getDiagnosticEventTime()));
    }
}
