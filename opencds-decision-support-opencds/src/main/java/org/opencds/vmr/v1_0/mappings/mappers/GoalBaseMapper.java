package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.vmr.v1_0.internal.GoalBase;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class GoalBaseMapper extends ClinicalStatementMapper
{
    public static void pullIn(final org.opencds.vmr.v1_0.schema.GoalBase source, final GoalBase target,
            final String subjectPersonId, final String focalPersonId, final FactLists factLists) throws ImproperUsageException
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

        try
        {
            ClinicalStatementMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new RuntimeException(errStr);
        }

        target.setGoalFocus(MappingUtility.cD2CDInternal(source.getGoalFocus()));
        if (source.getCriticality() != null)
            target.setCriticality(MappingUtility.cD2CDInternal(source.getCriticality()));
        if (source.getGoalPursuitEffectiveTime() != null)
            target.setGoalPursuitEffectiveTime(
                    MappingUtility.iVLTS2IVLDateInternal(source.getGoalPursuitEffectiveTime(), factLists.getParsedDatesCache()));
        if (source.getGoalAchievementTargetTime() != null)
            target.setGoalAchievementTargetTime(
                    MappingUtility.iVLTS2IVLDateInternal(source.getGoalAchievementTargetTime(), factLists.getParsedDatesCache()));
        if (source.getTargetBodySite() != null)
            target.setTargetBodySite(BodySiteMapper.pullIn(source.getTargetBodySite()));
        if (source.getTargetGoalValue() != null)
            target.setTargetGoalValue(MappingUtility.targetGoalValue2TargetGoalValueInternal(source.getTargetGoalValue(),
                    factLists.getParsedDatesCache()));

    }

    public static void pushOut(final GoalBase source, final org.opencds.vmr.v1_0.schema.GoalBase target)
            throws ImproperUsageException
    {
        final String _METHODNAME = "pullOut(): ";

        if (source == null)
            return;
        if (target == null)
        {
            final String errStr = _METHODNAME + "improper usage: source supplied is null";
            log.error(errStr);
            throw new ImproperUsageException(errStr);
        }

        try
        {
            ClinicalStatementMapper.pushOut(source, target);
        }
        catch (final ImproperUsageException u)
        {
            final String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
            log.error(errStr, u);
            throw new RuntimeException(errStr);
        }

        target.setGoalFocus(MappingUtility.cDInternal2CD(source.getGoalFocus()));
        if (source.getCriticality() != null)
            target.setCriticality(MappingUtility.cDInternal2CD(source.getCriticality()));
        if (source.getGoalPursuitEffectiveTime() != null)
            target.setGoalPursuitEffectiveTime(MappingUtility.iVLDateInternal2IVLTS(source.getGoalPursuitEffectiveTime()));
        if (source.getGoalAchievementTargetTime() != null)
            target.setGoalAchievementTargetTime(MappingUtility.iVLDateInternal2IVLTS(source.getGoalAchievementTargetTime()));
        if (source.getTargetBodySite() != null)
            target.setTargetBodySite(BodySiteMapper.pushOut(source.getTargetBodySite()));
        if (source.getTargetGoalValue() != null)
            target.setTargetGoalValue(MappingUtility.targetGoalValueInternal2targetGoalValue(source.getTargetGoalValue()));
    }
}
