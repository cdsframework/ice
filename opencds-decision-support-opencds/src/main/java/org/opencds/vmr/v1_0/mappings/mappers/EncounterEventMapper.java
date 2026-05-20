package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.EncounterEvent;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EncounterEventMapper extends EncounterBaseMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.EncounterEvent external, final EncounterEvent internal,
            final String subjectPersonId, final String focalPersonId, final FactLists factLists)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullIn(): ";

        if (external == null)
            return;

        try
        {
            EncounterBaseMapper.pullIn(external, internal, subjectPersonId, focalPersonId, factLists);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new RuntimeException(errStr);
        }

        if (external.getEncounterEventTime() == null)
            throw new InvalidDataException("EncounterEventTime was null");
        final IVLDate eet = MappingUtility.iVLTS2IVLDateInternal(external.getEncounterEventTime(), factLists.getParsedDatesCache());
        if (eet == null)
            throw new InvalidDataException("EncounterEventTime was invalid.");
        if (eet.getLow() == null && eet.getHigh() == null)
            throw new InvalidDataException("EncounterEventTime must have non-null low or high value");
        internal.setEncounterEventTime(eet);

        factLists.put(EncounterEvent.class, internal);

        NestedObjectsMapper.pullInClinicalStatementNestedObjects(external, internal.getId(), subjectPersonId, focalPersonId,
                factLists);

    }

    public static org.opencds.vmr.v1_0.schema.EncounterEvent pushOut(final EncounterEvent source,
            final OrganizedResults organizedResults) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.EncounterEvent target = new org.opencds.vmr.v1_0.schema.EncounterEvent();

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

        if (source.getEncounterEventTime() != null)
            target.setEncounterEventTime(MappingUtility.iVLDateInternal2IVLTS(source.getEncounterEventTime()));

        NestedObjectsMapper.pushOutClinicalStatementNestedObjects(source, target, organizedResults);

        if (organizedResults.output().getEncounterEvents() == null)
        {
            organizedResults.output()
                    .setEncounterEvents(new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.EncounterEvents());
        }

        return target;
    }
}
