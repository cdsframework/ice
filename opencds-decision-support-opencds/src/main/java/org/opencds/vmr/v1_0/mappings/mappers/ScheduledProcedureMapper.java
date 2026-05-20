package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.ScheduledProcedure;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScheduledProcedureMapper extends EncounterBaseMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.ScheduledProcedure source, final ScheduledProcedure target,
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

        if (source.getProcedureTime() != null)
            target.setProcedureTime(
                    MappingUtility.iVLTS2IVLDateInternal(source.getProcedureTime(), factLists.getParsedDatesCache()));

        factLists.put(ScheduledProcedure.class, target);

        NestedObjectsMapper.pullInClinicalStatementNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);

    }

    public static org.opencds.vmr.v1_0.schema.ScheduledProcedure pushOut(final ScheduledProcedure source,
            final OrganizedResults organizedResults) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.ScheduledProcedure target = new org.opencds.vmr.v1_0.schema.ScheduledProcedure();

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

        if (source.getProcedureTime() != null)
            target.setProcedureTime(MappingUtility.iVLDateInternal2IVLTS(source.getProcedureTime()));

        NestedObjectsMapper.pushOutClinicalStatementNestedObjects(source, target, organizedResults);

        if (organizedResults.output().getScheduledProcedures() == null)
        {
            organizedResults.output()
                    .setScheduledProcedures(
                            new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.ScheduledProcedures());
        }

        return target;
    }
}
