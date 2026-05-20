package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.SupplyOrder;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SupplyOrderMapper extends SupplyBaseMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.SupplyOrder source, final SupplyOrder target,
            final String subjectPersonId, final String focalPersonId, final FactLists factLists)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullIn(): ";

        if (source == null)
            return;

        try
        {
            SupplyBaseMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new RuntimeException(errStr);
        }

        if (source.getSupplyTime() != null)
            target.setSupplyTime(MappingUtility.iVLTS2IVLDateInternal(source.getSupplyTime(), factLists.getParsedDatesCache()));
        if (source.getOrderEventTime() != null)
            target.setOrderEventTime(
                    MappingUtility.iVLTS2IVLDateInternal(source.getOrderEventTime(), factLists.getParsedDatesCache()));
        if (source.getRepeatNumber() != null)
            target.setRepeatNumber(MappingUtility.iNT2INTInternal(source.getRepeatNumber()));

        factLists.put(SupplyOrder.class, target);

        NestedObjectsMapper.pullInClinicalStatementNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);

    }

    public static org.opencds.vmr.v1_0.schema.SupplyOrder pushOut(final SupplyOrder source, final OrganizedResults organizedResults)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.SupplyOrder target = new org.opencds.vmr.v1_0.schema.SupplyOrder();

        try
        {
            SupplyBaseMapper.pushOut(source, target);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new RuntimeException(errStr);
        }

        if (source.getSupplyTime() != null)
            target.setSupplyTime(MappingUtility.iVLDateInternal2IVLTS(source.getSupplyTime()));
        if (source.getOrderEventTime() != null)
            target.setOrderEventTime(MappingUtility.iVLDateInternal2IVLTS(source.getOrderEventTime()));
        if (source.getRepeatNumber() != null)
            target.setRepeatNumber(MappingUtility.iNTInternal2INT(source.getRepeatNumber()));

        NestedObjectsMapper.pushOutClinicalStatementNestedObjects(source, target, organizedResults);

        if (organizedResults.output().getSupplyOrders() == null)
        {
            organizedResults.output()
                    .setSupplyOrders(new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.SupplyOrders());
        }

        return target;
    }
}
