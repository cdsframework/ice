package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.MissedAppointment;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MissedAppointmentMapper extends EncounterBaseMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.MissedAppointment source, final MissedAppointment target,
            final String subjectPersonId, final String focalPersonId, final FactLists factLists)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullIn(): ";

        if (source == null)
            return;

        try
        {
            EncounterBaseMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new RuntimeException(errStr);
        }

        if (source.getAppointmentTime() != null)
            target.setAppointmentTime(MappingUtility.iVLTS2IVLDateInternal(source.getAppointmentTime()));

        factLists.put(MissedAppointment.class, target);

        NestedObjectsMapper.pullInClinicalStatementNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);

    }

    public static org.opencds.vmr.v1_0.schema.MissedAppointment pushOut(final MissedAppointment source,
            final OrganizedResults organizedResults) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";
        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.MissedAppointment target = new org.opencds.vmr.v1_0.schema.MissedAppointment();

        try
        {
            EncounterBaseMapper.pushOut(source, target);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new RuntimeException(errStr);
        }

        if (source.getAppointmentTime() != null)
            target.setAppointmentTime(MappingUtility.iVLDateInternal2IVLTS(source.getAppointmentTime()));

        NestedObjectsMapper.pushOutClinicalStatementNestedObjects(source, target, organizedResults);

        if (organizedResults.getOutput().getMissedAppointments() == null)
        {
            organizedResults.getOutput()
                    .setMissedAppointments(new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.MissedAppointments());
        }

        return target;
    }
}
