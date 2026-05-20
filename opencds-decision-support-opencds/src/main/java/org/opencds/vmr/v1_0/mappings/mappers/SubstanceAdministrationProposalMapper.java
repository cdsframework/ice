package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.DoseRestriction;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationProposal;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SubstanceAdministrationProposalMapper extends SubstanceAdministrationBaseMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal source,
            final SubstanceAdministrationProposal target, final String subjectPersonId, final String focalPersonId,
            final FactLists factLists) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullIn(): ";

        if (source == null)
            return;

        try
        {
            SubstanceAdministrationBaseMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new RuntimeException(errStr);
        }

        if (source.getCriticality() != null)
            target.setCriticality(MappingUtility.cD2CDInternal(source.getCriticality()));
        if (source.getDoseRestriction() != null)
            target.setDoseRestriction(
                    DoseRestrictionMapper.pullIn(source.getDoseRestriction(), new DoseRestriction(), null, subjectPersonId,
                            focalPersonId, factLists));
        if (source.getProposedAdministrationTimeInterval() != null)
            target.setProposedAdministrationTimeInterval(
                    MappingUtility.iVLTS2IVLDateInternal(source.getProposedAdministrationTimeInterval(),
                            factLists.getParsedDatesCache()));
        if (source.getValidAdministrationTimeInterval() != null)
            target.setValidAdministrationTimeInterval(
                    MappingUtility.iVLTS2IVLDateInternal(source.getValidAdministrationTimeInterval(),
                            factLists.getParsedDatesCache()));
        if (source.getValidAdministrationTimeInterval() != null)
            target.setNumberFillsAllowed(MappingUtility.iNT2INTInternal(source.getNumberFillsAllowed()));

        factLists.put(SubstanceAdministrationProposal.class, target);

        NestedObjectsMapper.pullInClinicalStatementNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);

    }

    public static org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal pushOut(final SubstanceAdministrationProposal source,
            final OrganizedResults organizedResults) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return null;

        final org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal target =
                new org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal();

        try
        {
            SubstanceAdministrationBaseMapper.pushOut(source, target, organizedResults);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new RuntimeException(errStr);
        }

        if (source.getCriticality() != null)
            target.setCriticality(MappingUtility.cDInternal2CD(source.getCriticality()));
        if (source.getDoseRestriction() != null)
            target.setDoseRestriction(DoseRestrictionMapper.pushOut(source.getDoseRestriction()));
        if (source.getProposedAdministrationTimeInterval() != null)
            target.setProposedAdministrationTimeInterval(
                    MappingUtility.iVLDateInternal2IVLTS(source.getProposedAdministrationTimeInterval()));
        if (source.getValidAdministrationTimeInterval() != null)
            target.setNumberFillsAllowed(MappingUtility.iNTInternal2INT(source.getNumberFillsAllowed()));
        if (source.getValidAdministrationTimeInterval() != null)
            target.setValidAdministrationTimeInterval(
                    MappingUtility.iVLDateInternal2IVLTS(source.getValidAdministrationTimeInterval()));

        NestedObjectsMapper.pushOutClinicalStatementNestedObjects(source, target, organizedResults);

        if (organizedResults.output().getSubstanceAdministrationProposals() == null)
        {
            organizedResults.output()
                    .setSubstanceAdministrationProposals(
                            new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.SubstanceAdministrationProposals());
        }

        return target;
    }
}
