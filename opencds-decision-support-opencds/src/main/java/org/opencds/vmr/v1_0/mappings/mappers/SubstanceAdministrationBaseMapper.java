package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.AdministrableSubstance;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationBase;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SubstanceAdministrationBaseMapper extends ClinicalStatementMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.SubstanceAdministrationBase source,
            final SubstanceAdministrationBase target, final String subjectPersonId, final String focalPersonId,
            final FactLists factLists) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullIn(): ";
        if (source == null)
            return;
        if (target == null)
        {
            final String errStr = _METHODNAME + "improper usage: target supplied is null";
            log.error(errStr);
            throw new ImproperUsageException(errStr);
        }

        ClinicalStatementMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists);

        if (source.getSubstanceAdministrationGeneralPurpose() != null)
            target.setSubstanceAdministrationGeneralPurpose(
                    MappingUtility.cD2CDInternal(source.getSubstanceAdministrationGeneralPurpose()));
        if (source.getSubstance() != null)
            target.setSubstance(AdministrableSubstanceMapper.pullIn(source.getSubstance(), new AdministrableSubstance(), null, null,
                    subjectPersonId, focalPersonId, factLists));
        if (source.getDeliveryMethod() != null)
            target.setDeliveryMethod(MappingUtility.cD2CDInternal(source.getDeliveryMethod()));
        if (source.getDoseQuantity() != null)
            target.setDoseQuantity(MappingUtility.iVLPQ2IVLPQInternal(source.getDoseQuantity()));
        if (source.getDeliveryRoute() != null)
            target.setDeliveryRoute(MappingUtility.cD2CDInternal(source.getDeliveryRoute()));
        if (source.getApproachBodySite() != null)
            target.setApproachBodySite(BodySiteMapper.pullIn(source.getApproachBodySite()));
        if (source.getTargetBodySite() != null)
            target.setTargetBodySite(BodySiteMapper.pullIn(source.getTargetBodySite()));
        if (source.getDosingPeriod() != null)
            target.setDosingPeriod(MappingUtility.iVLPQ2IVLPQInternal(source.getDosingPeriod()));
        if (source.getDosingPeriodIntervalIsImportant() != null)
            target.setDosingPeriodIntervalIsImportant(MappingUtility.bL2BLInternal(source.getDosingPeriodIntervalIsImportant()));
        if (source.getDeliveryRate() != null)
            target.setDeliveryRate(MappingUtility.iVLPQ2IVLPQInternal(source.getDeliveryRate()));
        if (source.getDoseType() != null)
            target.setDoseType(MappingUtility.cD2CDInternal(source.getDoseType()));
    }

    public static void pushOut(final SubstanceAdministrationBase source,
            final org.opencds.vmr.v1_0.schema.SubstanceAdministrationBase target) throws ImproperUsageException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return;
        if (target == null)
        {
            final String errStr = _METHODNAME + "improper usage: target supplied is null";
            log.error(errStr);
            throw new ImproperUsageException(errStr);
        }

        ClinicalStatementMapper.pushOut(source, target);

        if (source.getSubstanceAdministrationGeneralPurpose() != null)
            target.setSubstanceAdministrationGeneralPurpose(
                    MappingUtility.cDInternal2CD(source.getSubstanceAdministrationGeneralPurpose()));
        if (source.getSubstance() != null)
        {
            target.setSubstance(new org.opencds.vmr.v1_0.schema.AdministrableSubstance());
            AdministrableSubstanceMapper.pushOut(source.getSubstance(), target.getSubstance());
        }
        if (source.getDeliveryMethod() != null)
            target.setDeliveryMethod(MappingUtility.cDInternal2CD(source.getDeliveryMethod()));
        if (source.getDoseQuantity() != null)
            target.setDoseQuantity(MappingUtility.iVLPQInternal2IVLPQ(source.getDoseQuantity()));
        if (source.getDeliveryRoute() != null)
            target.setDeliveryRoute(MappingUtility.cDInternal2CD(source.getDeliveryRoute()));
        if (source.getApproachBodySite() != null)
            target.setApproachBodySite(BodySiteMapper.pushOut(source.getApproachBodySite()));
        if (source.getTargetBodySite() != null)
            target.setTargetBodySite(BodySiteMapper.pushOut(source.getTargetBodySite()));
        if (source.getDosingPeriod() != null)
            target.setDosingPeriod(MappingUtility.iVLPQInternal2IVLPQ(source.getDosingPeriod()));
        if (source.getDosingPeriodIntervalIsImportant() != null)
            target.setDosingPeriodIntervalIsImportant(MappingUtility.bLInternal2BL(source.getDosingPeriodIntervalIsImportant()));
        if (source.getDeliveryRate() != null)
            target.setDeliveryRate(MappingUtility.iVLPQInternal2IVLPQ(source.getDeliveryRate()));
        if (source.getDoseType() != null)
            target.setDoseType(MappingUtility.cDInternal2CD(source.getDoseType()));
    }

    public static void pushOut(final SubstanceAdministrationBase source,
            final org.opencds.vmr.v1_0.schema.SubstanceAdministrationBase target, final OrganizedResults organizedResults)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOut(): ";

        if (source == null)
            return;
        if (target == null)
        {
            final String errStr = _METHODNAME + "improper usage: target supplied is null";
            log.error(errStr);
            throw new ImproperUsageException(errStr);
        }

        ClinicalStatementMapper.pushOut(source, target);

        if (source.getSubstanceAdministrationGeneralPurpose() != null)
            target.setSubstanceAdministrationGeneralPurpose(
                    MappingUtility.cDInternal2CD(source.getSubstanceAdministrationGeneralPurpose()));
        if (source.getSubstance() != null)
        {
            target.setSubstance(new org.opencds.vmr.v1_0.schema.AdministrableSubstance());
            AdministrableSubstanceMapper.pushOut(source.getSubstance(), target.getSubstance(), organizedResults);
        }
        if (source.getDeliveryMethod() != null)
            target.setDeliveryMethod(MappingUtility.cDInternal2CD(source.getDeliveryMethod()));
        if (source.getDoseQuantity() != null)
            target.setDoseQuantity(MappingUtility.iVLPQInternal2IVLPQ(source.getDoseQuantity()));
        if (source.getDeliveryRoute() != null)
            target.setDeliveryRoute(MappingUtility.cDInternal2CD(source.getDeliveryRoute()));
        if (source.getApproachBodySite() != null)
            target.setApproachBodySite(BodySiteMapper.pushOut(source.getApproachBodySite()));
        if (source.getTargetBodySite() != null)
            target.setTargetBodySite(BodySiteMapper.pushOut(source.getTargetBodySite()));
        if (source.getDosingPeriod() != null)
            target.setDosingPeriod(MappingUtility.iVLPQInternal2IVLPQ(source.getDosingPeriod()));
        if (source.getDosingPeriodIntervalIsImportant() != null)
            target.setDosingPeriodIntervalIsImportant(MappingUtility.bLInternal2BL(source.getDosingPeriodIntervalIsImportant()));
        if (source.getDeliveryRate() != null)
            target.setDeliveryRate(MappingUtility.iVLPQInternal2IVLPQ(source.getDeliveryRate()));
        if (source.getDoseType() != null)
            target.setDoseType(MappingUtility.cDInternal2CD(source.getDoseType()));
    }
}
