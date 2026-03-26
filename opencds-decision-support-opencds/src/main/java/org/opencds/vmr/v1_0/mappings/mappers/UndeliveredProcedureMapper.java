package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.UndeliveredProcedure;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UndeliveredProcedureMapper extends ProcedureBaseMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.UndeliveredProcedure source, final UndeliveredProcedure target,
            final String subjectPersonId, final String focalPersonId, final FactLists factLists)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullIn(): ";

        if (source == null)
            return;

        try
        {
            ProcedureBaseMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new RuntimeException(errStr);
        }

        if (source.getReason() != null)
            target.setReason(MappingUtility.cD2CDInternal(source.getReason()));
        if (source.getSubjectEffectiveTime() != null)
            target.setSubjectEffectiveTime(MappingUtility.iVLTS2IVLDateInternal(source.getSubjectEffectiveTime()));
        if (source.getDocumentationTime() != null)
            target.setDocumentationTime(MappingUtility.iVLTS2IVLDateInternal(source.getDocumentationTime()));

        factLists.put(UndeliveredProcedure.class, target);

        NestedObjectsMapper.pullInClinicalStatementNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);

    }

    public static org.opencds.vmr.v1_0.schema.UndeliveredProcedure pushOut(final UndeliveredProcedure source,
            final OrganizedResults organizedResults) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.UndeliveredProcedure target = new org.opencds.vmr.v1_0.schema.UndeliveredProcedure();

        try
        {
            ProcedureBaseMapper.pushOut(source, target);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new RuntimeException(errStr);
        }

        if (source.getReason() != null)
            target.setReason(MappingUtility.cDInternal2CD(source.getReason()));
        if (source.getSubjectEffectiveTime() != null)
            target.setSubjectEffectiveTime(MappingUtility.iVLDateInternal2IVLTS(source.getSubjectEffectiveTime()));
        if (source.getDocumentationTime() != null)
            target.setDocumentationTime(MappingUtility.iVLDateInternal2IVLTS(source.getDocumentationTime()));

        NestedObjectsMapper.pushOutClinicalStatementNestedObjects(source, target, organizedResults);

        if (organizedResults.getOutput().getUndeliveredProcedures() == null)
        {
            organizedResults.getOutput()
                    .setUndeliveredProcedures(
                            new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.UndeliveredProcedures());
        }

        return target;
    }
}
