package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.SupplyProposal;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SupplyProposalMapper extends SupplyBaseMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.SupplyProposal source, final SupplyProposal target,
            final String subjectPersonId, final String focalPersonId, final FactLists factLists)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullIn(): ";

        if (source == null)
            return;

        try
        {
            SupplyBaseMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new RuntimeException(errStr);
        }

        if (source.getProposedSupplyTime() != null)
            target.setProposedSupplyTime(
                    MappingUtility.iVLTS2IVLDateInternal(source.getProposedSupplyTime(), factLists.getParsedDatesCache()));
        if (source.getRepeatNumber() != null)
            target.setRepeatNumber(MappingUtility.iNT2INTInternal(source.getRepeatNumber()));

        factLists.put(SupplyProposal.class, target);

        NestedObjectsMapper.pullInClinicalStatementNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);

    }

    public static org.opencds.vmr.v1_0.schema.SupplyProposal pushOut(final SupplyProposal source,
            final OrganizedResults organizedResults) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.SupplyProposal target = new org.opencds.vmr.v1_0.schema.SupplyProposal();

        try
        {
            SupplyBaseMapper.pushOut(source, target);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new RuntimeException(errStr);
        }

        if (source.getProposedSupplyTime() != null)
            target.setProposedSupplyTime(MappingUtility.iVLDateInternal2IVLTS(source.getProposedSupplyTime()));
        if (source.getRepeatNumber() != null)
            target.setRepeatNumber(MappingUtility.iNTInternal2INT(source.getRepeatNumber()));

        NestedObjectsMapper.pushOutClinicalStatementNestedObjects(source, target, organizedResults);

        if (organizedResults.output().getSupplyProposals() == null)
        {
            organizedResults.output()
                    .setSupplyProposals(new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.SupplyProposals());
        }

        return target;
    }
}
