package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.UndeliveredSubstanceAdministration;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UndeliveredSubstanceAdministrationMapper extends SubstanceAdministrationBaseMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.UndeliveredSubstanceAdministration source,
            final UndeliveredSubstanceAdministration target, final String subjectPersonId, final String focalPersonId,
            final FactLists factLists) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullIn(): ";

        if (source == null)
            return;

        try
        {
            SubstanceAdministrationBaseMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists);
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

        factLists.put(UndeliveredSubstanceAdministration.class, target);

        NestedObjectsMapper.pullInClinicalStatementNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);

    }

    public static org.opencds.vmr.v1_0.schema.UndeliveredSubstanceAdministration pushOut(
            final UndeliveredSubstanceAdministration source, final OrganizedResults organizedResults)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.UndeliveredSubstanceAdministration target =
                new org.opencds.vmr.v1_0.schema.UndeliveredSubstanceAdministration();

        try
        {
            SubstanceAdministrationBaseMapper.pushOut(source, target, organizedResults);
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

        if (organizedResults.getOutput().getUndeliveredSubstanceAdministrations() == null)
        {
            organizedResults.getOutput()
                    .setUndeliveredSubstanceAdministrations(
                            new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.UndeliveredSubstanceAdministrations());
        }

        return target;
    }
}
