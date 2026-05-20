package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SubstanceAdministrationEventMapper extends SubstanceAdministrationBaseMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent source,
            final SubstanceAdministrationEvent target, final String subjectPersonId, final String focalPersonId,
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

        if (source.getDoseNumber() != null)
            target.setDoseNumber(MappingUtility.iNT2INTInternal(source.getDoseNumber()));
        if (source.getAdministrationTimeInterval() != null)
            target.setAdministrationTimeInterval(
                    MappingUtility.iVLTS2IVLDateInternal(source.getAdministrationTimeInterval(), factLists.getParsedDatesCache()));
        if (source.getDocumentationTime() != null)
            target.setDocumentationTime(
                    MappingUtility.iVLTS2IVLDateInternal(source.getDocumentationTime(), factLists.getParsedDatesCache()));
        if (source.getInformationAttestationType() != null)
            target.setInformationAttestationType(MappingUtility.cD2CDInternal(source.getInformationAttestationType()));
        if (source.getIsValid() != null)
            target.setIsValid(MappingUtility.bL2BLInternal(source.getIsValid()));

        factLists.put(SubstanceAdministrationEvent.class, target);

        NestedObjectsMapper.pullInClinicalStatementNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);

    }

    public static org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent pushOut(final SubstanceAdministrationEvent source,
            final OrganizedResults organizedResults) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent target =
                new org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent();

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

        if (source.getDoseNumber() != null)
            target.setDoseNumber(MappingUtility.iNTInternal2INT(source.getDoseNumber()));
        if (source.getAdministrationTimeInterval() != null)
            target.setAdministrationTimeInterval(MappingUtility.iVLDateInternal2IVLTS(source.getAdministrationTimeInterval()));
        if (source.getDocumentationTime() != null)
            target.setDocumentationTime(MappingUtility.iVLDateInternal2IVLTS(source.getDocumentationTime()));
        if (source.getInformationAttestationType() != null)
            target.setInformationAttestationType(MappingUtility.cDInternal2CD(source.getInformationAttestationType()));
        if (source.getIsValid() != null)
            target.setIsValid(MappingUtility.bLInternal2BL(source.getIsValid()));

        NestedObjectsMapper.pushOutClinicalStatementNestedObjects(source, target, organizedResults);

        if (organizedResults.output().getSubstanceAdministrationEvents() == null)
        {
            organizedResults.output()
                    .setSubstanceAdministrationEvents(
                            new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.SubstanceAdministrationEvents());
        }

        return target;
    }
}
