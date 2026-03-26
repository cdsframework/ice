package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.DeniedProblem;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeniedProblemMapper extends ProblemBaseMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.DeniedProblem source, final DeniedProblem target,
            final String subjectPersonId, final String focalPersonId, final FactLists factLists)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullIn(): ";
        if (source == null)
            return;

        try
        {
            ProblemBaseMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new RuntimeException(errStr);
        }

        factLists.put(DeniedProblem.class, target);

        NestedObjectsMapper.pullInClinicalStatementNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);

    }

    public static org.opencds.vmr.v1_0.schema.DeniedProblem pushOut(final DeniedProblem source,
            final OrganizedResults organizedResults) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.DeniedProblem target = new org.opencds.vmr.v1_0.schema.DeniedProblem();
        try
        {
            ProblemBaseMapper.pushOut(source, target);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new RuntimeException(errStr);
        }

        NestedObjectsMapper.pushOutClinicalStatementNestedObjects(source, target, organizedResults);

        if (organizedResults.getOutput().getAdverseEvents() == null)
        {
            organizedResults.getOutput()
                    .setAdverseEvents(new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.AdverseEvents());
        }

        if (organizedResults.getOutput().getDeniedProblems() == null)
        {
            organizedResults.getOutput()
                    .setDeniedProblems(new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.DeniedProblems());
        }

        return target;
    }
}
