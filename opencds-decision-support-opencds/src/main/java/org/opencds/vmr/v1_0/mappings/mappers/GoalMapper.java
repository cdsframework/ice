package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.Goal;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoalMapper extends GoalBaseMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.Goal source, final Goal target, final String subjectPersonId,
            final String focalPersonId, final FactLists factLists)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullIn(): ";

        if (source == null)
            return;

        try
        {
            GoalBaseMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new RuntimeException(errStr);
        }

        if (source.getGoalObserverEventTime() != null)
            target.setGoalObserverEventTime(
                    MappingUtility.iVLTS2IVLDateInternal(source.getGoalObserverEventTime(), factLists.getParsedDatesCache()));
        if (source.getGoalStatus() != null)
            target.setGoalStatus(MappingUtility.cD2CDInternal(source.getGoalStatus()));

        factLists.put(Goal.class, target);

        NestedObjectsMapper.pullInClinicalStatementNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);

    }

    public static org.opencds.vmr.v1_0.schema.Goal pushOut(final Goal source, final OrganizedResults organizedResults)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.Goal target = new org.opencds.vmr.v1_0.schema.Goal();

        try
        {
            GoalBaseMapper.pushOut(source, target);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new RuntimeException(errStr);
        }

        if (source.getGoalObserverEventTime() != null)
            target.setGoalObserverEventTime(MappingUtility.iVLDateInternal2IVLTS(source.getGoalObserverEventTime()));
        if (source.getGoalStatus() != null)
            target.setGoalStatus(MappingUtility.cDInternal2CD(source.getGoalStatus()));

        NestedObjectsMapper.pushOutClinicalStatementNestedObjects(source, target, organizedResults);

        if (organizedResults.output().getGoals() == null)
            organizedResults.output().setGoals(new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.Goals());

        return target;
    }
}
