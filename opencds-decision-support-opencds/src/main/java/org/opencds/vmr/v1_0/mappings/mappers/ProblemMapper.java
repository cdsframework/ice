package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.Problem;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProblemMapper extends ProblemBaseMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.Problem source, final Problem target, final String subjectPersonId,
            final String focalPersonId, final FactLists factLists)
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

        if (source.getImportance() != null)
            target.setImportance(MappingUtility.cD2CDInternal(source.getImportance()));
        if (source.getSeverity() != null)
            target.setSeverity(MappingUtility.cD2CDInternal(source.getSeverity()));
        if (source.getProblemStatus() != null)
            target.setProblemStatus(MappingUtility.cD2CDInternal(source.getProblemStatus()));
        if (source.getAgeAtOnset() != null)
            target.setAgeAtOnset(MappingUtility.pQ2PQInternal(source.getAgeAtOnset()));
        if (source.getWasCauseOfDeath() != null)
            target.setWasCauseOfDeath(MappingUtility.bL2BLInternal(source.getWasCauseOfDeath()));

        factLists.put(Problem.class, target);

        NestedObjectsMapper.pullInClinicalStatementNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);

    }

    public static org.opencds.vmr.v1_0.schema.Problem pushOut(final Problem source, final OrganizedResults organizedResults)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.Problem target = new org.opencds.vmr.v1_0.schema.Problem();

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

        if (source.getImportance() != null)
            target.setImportance(MappingUtility.cDInternal2CD(source.getImportance()));
        if (source.getSeverity() != null)
            target.setSeverity(MappingUtility.cDInternal2CD(source.getSeverity()));
        if (source.getProblemStatus() != null)
            target.setProblemStatus(MappingUtility.cDInternal2CD(source.getProblemStatus()));
        if (source.getAgeAtOnset() != null)
            target.setAgeAtOnset(MappingUtility.pQInternal2PQ(source.getAgeAtOnset()));
        if (source.getWasCauseOfDeath() != null)
            target.setWasCauseOfDeath(MappingUtility.bLInternal2BL(source.getWasCauseOfDeath()));

        NestedObjectsMapper.pushOutClinicalStatementNestedObjects(source, target, organizedResults);

        if (organizedResults.getOutput().getProblems() == null)
            organizedResults.getOutput().setProblems(new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.Problems());

        return target;
    }
}
