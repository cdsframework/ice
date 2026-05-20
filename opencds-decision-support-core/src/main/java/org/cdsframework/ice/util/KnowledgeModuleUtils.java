package org.cdsframework.ice.util;

import org.opencds.config.api.model.KMId;
import org.springframework.util.ObjectUtils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Some of these knowledge module routines are ICE-specific, as noted in the documentation for each method below
 */
@Slf4j
@UtilityClass
public class KnowledgeModuleUtils
{
    public static String returnStringRepresentationOfKnowledgeModuleName(final String scopingEntityId, final String businessId,
            final String version)
    {
        final String _METHODNAME = "returnStringRepresentationOfKnowledgeModuleName(): ";

        if (ObjectUtils.isEmpty(scopingEntityId) || ObjectUtils.isEmpty(businessId) || ObjectUtils.isEmpty(version))
        {
            final String lErrStr = "One or more parameters not specified";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        return "%s^%s^%s".formatted(scopingEntityId, businessId, version);
    }

    public static String returnPackageNameForKnowledgeModule(final String scopingEntityId, final String businessId,
            final String version)
    {
        final String _METHODNAME = "returnPackageNameForKnowledgeModule(): ";

        if (ObjectUtils.isEmpty(scopingEntityId) || ObjectUtils.isEmpty(businessId) || ObjectUtils.isEmpty(version))
        {
            final String lErrStr = "One or more parameters not specified";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        return "%s.%s".formatted(scopingEntityId, businessId).toLowerCase();
    }

    public static KMId returnKMIdRepresentationOfKnowledgeModule(final String pStringRepresentationOfKnowledgeModuleName)
    {
        if (pStringRepresentationOfKnowledgeModuleName == null)
            return null;

        final String[] lKmIdParts = pStringRepresentationOfKnowledgeModuleName.split("\\^");
        if (lKmIdParts.length != 3)
            return null;

        return new KMId(lKmIdParts[0], lKmIdParts[1], lKmIdParts[2]);
    }
}
