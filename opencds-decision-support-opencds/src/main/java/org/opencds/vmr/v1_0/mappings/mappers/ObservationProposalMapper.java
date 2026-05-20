package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.ObservationProposal;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObservationProposalMapper extends ObservationBaseMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.ObservationProposal source, final ObservationProposal target,
            final String subjectPersonId, final String focalPersonId, final FactLists factLists)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullIn(): ";

        if (source == null)
            return;

        try
        {
            ObservationBaseMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new RuntimeException(errStr);
        }

        if (source.getCriticality() != null)
            target.setCriticality(MappingUtility.cD2CDInternal(source.getCriticality()));
        if (source.getProposedObservationTime() != null)
            target.setProposedObservationTime(
                    MappingUtility.iVLTS2IVLDateInternal(source.getProposedObservationTime(), factLists.getParsedDatesCache()));
        if (source.getRepeatNumber() != null)
            target.setRepeatNumber(MappingUtility.iNT2INTInternal(source.getRepeatNumber()));

        factLists.put(ObservationProposal.class, target);

        NestedObjectsMapper.pullInClinicalStatementNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);

    }

    public static org.opencds.vmr.v1_0.schema.ObservationProposal pushOut(final ObservationProposal source,
            final OrganizedResults organizedResults) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";
        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.ObservationProposal target = new org.opencds.vmr.v1_0.schema.ObservationProposal();

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

        if (source.getCriticality() != null)
            target.setCriticality(MappingUtility.cDInternal2CD(source.getCriticality()));
        if (source.getProposedObservationTime() != null)
            target.setProposedObservationTime(MappingUtility.iVLDateInternal2IVLTS(source.getProposedObservationTime()));
        if (source.getRepeatNumber() != null)
            target.setRepeatNumber(MappingUtility.iNTInternal2INT(source.getRepeatNumber()));

        NestedObjectsMapper.pushOutClinicalStatementNestedObjects(source, target, organizedResults);

        if (organizedResults.output().getObservationProposals() == null)
        {
            organizedResults.output()
                    .setObservationProposals(
                            new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.ObservationProposals());
        }

        return target;
    }
}
