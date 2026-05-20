package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.AppointmentRequest;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppointmentRequestMapper extends EncounterBaseMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.AppointmentRequest source, final AppointmentRequest target,
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

        if (source.getCriticality() != null)
            target.setCriticality(MappingUtility.cD2CDInternal(source.getCriticality()));
        if (source.getRequestedAppointmentTime() != null)
            target.setRequestedAppointmentTime(
                    MappingUtility.iVLTS2IVLDateInternal(source.getRequestedAppointmentTime(), factLists.getParsedDatesCache()));
        if (source.getRequestIssuanceTime() != null)
            target.setRequestIssuanceTime(
                    MappingUtility.iVLTS2IVLDateInternal(source.getRequestIssuanceTime(), factLists.getParsedDatesCache()));
        if (source.getRepeatNumber() != null)
            target.setRepeatNumber(MappingUtility.iNT2INTInternal(source.getRepeatNumber()));

        factLists.put(AppointmentRequest.class, target);

        NestedObjectsMapper.pullInClinicalStatementNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);

    }

    public static org.opencds.vmr.v1_0.schema.AppointmentRequest pushOut(final AppointmentRequest source,
            final OrganizedResults organizedResults) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.AppointmentRequest target = new org.opencds.vmr.v1_0.schema.AppointmentRequest();

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

        if (source.getCriticality() != null)
            target.setCriticality(MappingUtility.cDInternal2CD(source.getCriticality()));
        if (source.getRequestedAppointmentTime() != null)
            target.setRequestedAppointmentTime(MappingUtility.iVLDateInternal2IVLTS(source.getRequestedAppointmentTime()));
        if (source.getRequestIssuanceTime() != null)
            target.setRequestIssuanceTime(MappingUtility.iVLDateInternal2IVLTS(source.getRequestIssuanceTime()));
        if (source.getRepeatNumber() != null)
            target.setRepeatNumber(MappingUtility.iNTInternal2INT(source.getRepeatNumber()));

        NestedObjectsMapper.pushOutClinicalStatementNestedObjects(source, target, organizedResults);

        if (organizedResults.output().getAppointmentRequests() == null)
        {
            organizedResults.output()
                    .setAppointmentRequests(
                            new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.AppointmentRequests());
        }

        return target;
    }
}
