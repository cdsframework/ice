package org.opencds.vmr.v1_0.mappings.mappers;

import java.util.ArrayList;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.ObservationResult;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObservationResultMapper extends ObservationBaseMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.ObservationResult external, final ObservationResult internal,
            final String subjectPersonId, final String focalPersonId, final FactLists factLists)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullIn(): ";

        if (external == null)
            return;

        try
        {
            ObservationBaseMapper.pullIn(external, internal, subjectPersonId, focalPersonId, factLists);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new RuntimeException(errStr);
        }

        if (external.getObservationEventTime() != null)
            internal.setObservationEventTime(
                    MappingUtility.iVLTS2IVLDateInternal(external.getObservationEventTime(), factLists.getParsedDatesCache()));
        if (external.getObservationValue() != null)
            internal.setObservationValue(MappingUtility.observationValue2ObservationValueInternal(external.getObservationValue(),
                    factLists.getParsedDatesCache()));
        if (external.getInterpretation() != null)
        {
            internal.setInterpretation(new ArrayList<>());
            for (final org.opencds.vmr.v1_0.schema.CD oneInterpretation : external.getInterpretation())
                internal.getInterpretation().add(MappingUtility.cD2CDInternal(oneInterpretation));
        }

        factLists.put(ObservationResult.class, internal);

        NestedObjectsMapper.pullInClinicalStatementNestedObjects(external, internal.getId(), subjectPersonId, focalPersonId,
                factLists);

    }

    public static org.opencds.vmr.v1_0.schema.ObservationResult pushOut(final ObservationResult source,
            final OrganizedResults organizedResults) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        final org.opencds.vmr.v1_0.schema.ObservationResult target = new org.opencds.vmr.v1_0.schema.ObservationResult();

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

        if (source.getObservationEventTime() != null)
            target.setObservationEventTime(MappingUtility.iVLDateInternal2IVLTS(source.getObservationEventTime()));
        if (source.getObservationValue() != null)
            target.setObservationValue(MappingUtility.observationValueInternal2ObservationValue(source.getObservationValue()));
        if (source.getInterpretation() != null)
        {
            for (final CD oneInterpretation : source.getInterpretation())
                target.getInterpretation().add(MappingUtility.cDInternal2CD(oneInterpretation));
        }

        NestedObjectsMapper.pushOutClinicalStatementNestedObjects(source, target, organizedResults);

        if (organizedResults.output().getObservationResults() == null)
        {
            organizedResults.output()
                    .setObservationResults(new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.ObservationResults());
        }

        return target;
    }
}
