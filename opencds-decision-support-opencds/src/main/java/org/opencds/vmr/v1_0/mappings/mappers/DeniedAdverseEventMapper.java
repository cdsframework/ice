package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.DeniedAdverseEvent;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeniedAdverseEventMapper extends AdverseEventBaseMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.DeniedAdverseEvent source, final DeniedAdverseEvent target,
            final String subjectPersonId, final String focalPersonId, final FactLists factLists)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullIn(): ";

        if (source == null)
            return;

        try
        {
            AdverseEventBaseMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new ImproperUsageException(errStr);
        }

        factLists.put(DeniedAdverseEvent.class, target);

        NestedObjectsMapper.pullInClinicalStatementNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);

    }

    public static org.opencds.vmr.v1_0.schema.DeniedAdverseEvent pushOut(final DeniedAdverseEvent source,
            final OrganizedResults organizedResults) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.DeniedAdverseEvent target = new org.opencds.vmr.v1_0.schema.DeniedAdverseEvent();

        try
        {
            AdverseEventBaseMapper.pushOut(source, target);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new RuntimeException(errStr);
        }

        NestedObjectsMapper.pushOutClinicalStatementNestedObjects(source, target, organizedResults);

        if (organizedResults.output().getDeniedAdverseEvents() == null)
        {
            organizedResults.output()
                    .setDeniedAdverseEvents(
                            new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.DeniedAdverseEvents());
        }

        return target;
    }
}
