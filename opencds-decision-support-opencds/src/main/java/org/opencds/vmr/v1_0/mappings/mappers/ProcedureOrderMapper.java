package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.ProcedureOrder;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcedureOrderMapper extends ProcedureBaseMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.ProcedureOrder source, final ProcedureOrder target,
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

        if (source.getCriticality() != null)
            target.setCriticality(MappingUtility.cD2CDInternal(source.getCriticality()));
        if (source.getOrderEventTime() != null)
            target.setOrderEventTime(MappingUtility.iVLTS2IVLDateInternal(source.getOrderEventTime()));
        if (source.getProcedureTime() != null)
            target.setProcedureTime(MappingUtility.iVLTS2IVLDateInternal(source.getProcedureTime()));
        if (source.getRepeatNumber() != null)
            target.setRepeatNumber(MappingUtility.iNT2INTInternal(source.getRepeatNumber()));

        factLists.put(ProcedureOrder.class, target);

        NestedObjectsMapper.pullInClinicalStatementNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);

    }

    public static org.opencds.vmr.v1_0.schema.ProcedureOrder pushOut(final ProcedureOrder source,
            final OrganizedResults organizedResults) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";
        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.ProcedureOrder target = new org.opencds.vmr.v1_0.schema.ProcedureOrder();

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

        if (source.getCriticality() != null)
            target.setCriticality(MappingUtility.cDInternal2CD(source.getCriticality()));
        if (source.getOrderEventTime() != null)
            target.setOrderEventTime(MappingUtility.iVLDateInternal2IVLTS(source.getOrderEventTime()));
        if (source.getProcedureTime() != null)
            target.setProcedureTime(MappingUtility.iVLDateInternal2IVLTS(source.getProcedureTime()));
        if (source.getRepeatNumber() != null)
            target.setRepeatNumber(MappingUtility.iNTInternal2INT(source.getRepeatNumber()));

        NestedObjectsMapper.pushOutClinicalStatementNestedObjects(source, target, organizedResults);

        if (organizedResults.getOutput().getProcedureOrders() == null)
        {
            organizedResults.getOutput()
                    .setProcedureOrders(new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.ProcedureOrders());
        }

        return target;
    }
}
