package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.DoseRestriction;
import org.opencds.vmr.v1_0.internal.SubstanceDispensationEvent;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SubstanceDispensationEventMapper extends SubstanceAdministrationBaseMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.SubstanceDispensationEvent source,
            final SubstanceDispensationEvent target, final String subjectPersonId, final String focalPersonId,
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

        if (source.getDaysSupply() != null)
            target.setDaysSupply(MappingUtility.iNT2INTInternal(source.getDaysSupply()));
        if (source.getDispensationQuantity() != null)
            target.setDispensationQuantity(MappingUtility.pQ2PQInternal(source.getDispensationQuantity()));
        if (source.getDoseRestriction() != null)
            target.setDoseRestriction(
                    DoseRestrictionMapper.pullIn(source.getDoseRestriction(), new DoseRestriction(), null, subjectPersonId,
                            focalPersonId, factLists));
        if (source.getDispensationTime() != null)
            target.setDispensationTime(
                    MappingUtility.iVLTS2IVLDateInternal(source.getDispensationTime(), factLists.getParsedDatesCache()));
        if (source.getFillNumber() != null)
            target.setFillNumber(MappingUtility.iNT2INTInternal(source.getFillNumber()));
        if (source.getFillsRemaining() != null)
            target.setFillsRemaining(MappingUtility.iNT2INTInternal(source.getFillsRemaining()));

        factLists.put(SubstanceDispensationEvent.class, target);

        NestedObjectsMapper.pullInClinicalStatementNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);

    }

    public static org.opencds.vmr.v1_0.schema.SubstanceDispensationEvent pushOut(final SubstanceDispensationEvent source,
            final OrganizedResults organizedResults) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.SubstanceDispensationEvent target =
                new org.opencds.vmr.v1_0.schema.SubstanceDispensationEvent();

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

        if (source.getDaysSupply() != null)
            target.setDaysSupply(MappingUtility.iNTInternal2INT(source.getDaysSupply()));
        if (source.getDispensationQuantity() != null)
            target.setDispensationQuantity(MappingUtility.pQInternal2PQ(source.getDispensationQuantity()));
        if (source.getDoseRestriction() != null)
            target.setDoseRestriction(DoseRestrictionMapper.pushOut(source.getDoseRestriction()));
        if (source.getDispensationTime() != null)
            target.setDispensationTime(MappingUtility.iVLDateInternal2IVLTS(source.getDispensationTime()));
        if (source.getFillNumber() != null)
            target.setFillNumber(MappingUtility.iNTInternal2INT(source.getFillNumber()));
        if (source.getFillsRemaining() != null)
            target.setFillsRemaining(MappingUtility.iNTInternal2INT(source.getFillsRemaining()));

        NestedObjectsMapper.pushOutClinicalStatementNestedObjects(source, target, organizedResults);

        if (organizedResults.output().getSubstanceDispensationEvents() == null)
        {
            organizedResults.output()
                    .setSubstanceDispensationEvents(
                            new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.SubstanceDispensationEvents());
        }

        return target;
    }
}
