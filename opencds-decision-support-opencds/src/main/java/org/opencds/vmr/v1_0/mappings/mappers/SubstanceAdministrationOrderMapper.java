package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.DoseRestriction;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationOrder;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SubstanceAdministrationOrderMapper extends SubstanceAdministrationBaseMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.SubstanceAdministrationOrder source,
            final SubstanceAdministrationOrder target, final String subjectPersonId, final String focalPersonId,
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

        if (source.getCriticality() != null)
            target.setCriticality(MappingUtility.cD2CDInternal(source.getCriticality()));
        if (source.getDoseRestriction() != null)
            target.setDoseRestriction(
                    DoseRestrictionMapper.pullIn(source.getDoseRestriction(), new DoseRestriction(), null, subjectPersonId,
                            focalPersonId, factLists));
        if (source.getAdministrationTimeInterval() != null)
            target.setAdministrationTimeInterval(MappingUtility.iVLTS2IVLDateInternal(source.getAdministrationTimeInterval()));
        if (source.getNumberFillsAllowed() != null)
            target.setNumberFillsAllowed(MappingUtility.iNT2INTInternal(source.getNumberFillsAllowed()));
        if (source.getOrderEventTime() != null)
            target.setOrderEventTime(MappingUtility.iVLTS2IVLDateInternal(source.getOrderEventTime()));

        factLists.put(SubstanceAdministrationOrder.class, target);

        NestedObjectsMapper.pullInClinicalStatementNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);

    }

    public static org.opencds.vmr.v1_0.schema.SubstanceAdministrationOrder pushOut(final SubstanceAdministrationOrder source,
            final OrganizedResults organizedResults) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.SubstanceAdministrationOrder target =
                new org.opencds.vmr.v1_0.schema.SubstanceAdministrationOrder();

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

        if (source.getCriticality() != null)
            target.setCriticality(MappingUtility.cDInternal2CD(source.getCriticality()));
        if (source.getDoseRestriction() != null)
            target.setDoseRestriction(DoseRestrictionMapper.pushOut(source.getDoseRestriction()));
        if (source.getAdministrationTimeInterval() != null)
            target.setAdministrationTimeInterval(MappingUtility.iVLDateInternal2IVLTS(source.getAdministrationTimeInterval()));
        if (source.getNumberFillsAllowed() != null)
            target.setNumberFillsAllowed(MappingUtility.iNTInternal2INT(source.getNumberFillsAllowed()));
        if (source.getOrderEventTime() != null)
            target.setOrderEventTime(MappingUtility.iVLDateInternal2IVLTS(source.getOrderEventTime()));

        NestedObjectsMapper.pushOutClinicalStatementNestedObjects(source, target, organizedResults);

        if (organizedResults.getOutput().getSubstanceAdministrationOrders() == null)
        {
            organizedResults.getOutput()
                    .setSubstanceAdministrationOrders(
                            new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.SubstanceAdministrationOrders());
        }

        return target;
    }
}
