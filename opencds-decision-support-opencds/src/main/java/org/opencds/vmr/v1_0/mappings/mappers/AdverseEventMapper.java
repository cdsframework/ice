package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.AdverseEvent;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdverseEventMapper extends AdverseEventBaseMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.AdverseEvent source, final AdverseEvent target,
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

        if (source.getCriticality() != null)
            target.setCriticality(MappingUtility.cD2CDInternal(source.getCriticality()));
        if (source.getSeverity() != null)
            target.setSeverity(MappingUtility.cD2CDInternal(source.getSeverity()));
        if (source.getAdverseEventStatus() != null)
            target.setAdverseEventStatus(MappingUtility.cD2CDInternal(source.getAdverseEventStatus()));

        factLists.put(AdverseEvent.class, target);

        NestedObjectsMapper.pullInClinicalStatementNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);

    }

    public static org.opencds.vmr.v1_0.schema.AdverseEvent pushOut(final AdverseEvent source,
            final OrganizedResults organizedResults) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.AdverseEvent target = new org.opencds.vmr.v1_0.schema.AdverseEvent();

        try
        {
            AdverseEventBaseMapper.pushOut(source, target);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new ImproperUsageException(errStr);
        }

        if (source.getCriticality() != null)
            target.setCriticality(MappingUtility.cDInternal2CD(source.getCriticality()));
        if (source.getSeverity() != null)
            target.setSeverity(MappingUtility.cDInternal2CD(source.getSeverity()));
        if (source.getAdverseEventStatus() != null)
            target.setAdverseEventStatus(MappingUtility.cDInternal2CD(source.getAdverseEventStatus()));

        NestedObjectsMapper.pushOutClinicalStatementNestedObjects(source, target, organizedResults);

        if (organizedResults.getOutput().getAdverseEvents() == null)
        {
            organizedResults.getOutput()
                    .setAdverseEvents(new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.AdverseEvents());
        }

        return target;
    }
}
