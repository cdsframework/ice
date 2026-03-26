package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.ObservationOrder;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObservationOrderMapper extends ObservationBaseMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.ObservationOrder source, final ObservationOrder target,
            final String subjectPersonId, final String focalPersonId, final FactLists factLists)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullIn(): ";

        if (source == null)
            return;

        try
        {
            ObservationBaseMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists);
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
        if (source.getObservationTime() != null)
            target.setObservationTime(MappingUtility.iVLTS2IVLDateInternal(source.getObservationTime()));
        if (source.getRepeatNumber() != null)
            target.setRepeatNumber(MappingUtility.iNT2INTInternal(source.getRepeatNumber()));

        factLists.put(ObservationOrder.class, target);

        NestedObjectsMapper.pullInClinicalStatementNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);

    }

    public static org.opencds.vmr.v1_0.schema.ObservationOrder pushOut(final ObservationOrder source,
            final OrganizedResults organizedResults) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";
        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.ObservationOrder target = new org.opencds.vmr.v1_0.schema.ObservationOrder();

        try
        {
            ObservationBaseMapper.pushOut(source, target);
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
        if (source.getObservationTime() != null)
            target.setObservationTime(MappingUtility.iVLDateInternal2IVLTS(source.getObservationTime()));
        if (source.getRepeatNumber() != null)
            target.setRepeatNumber(MappingUtility.iNTInternal2INT(source.getRepeatNumber()));

        NestedObjectsMapper.pushOutClinicalStatementNestedObjects(source, target, organizedResults);

        if (organizedResults.getOutput().getObservationOrders() == null)
        {
            organizedResults.getOutput()
                    .setObservationOrders(new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.ObservationOrders());
        }

        return target;
    }
}
