package org.cdsframework.cds;

import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.springframework.util.ObjectUtils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class ConceptUtils
{
    public static final String _attributeNamingConvention = "[a-zA-Z0-9_\\/\\.\\- ]+";

    /**
     * Return a modified String of the argument supplied as the attribute name which conforms to the required naming convention ([a-zA-Z0-9_\\.\\- ]) by stripping out all
     * spaces, tabs, line feeds, newline and carriage return characters. If an argument is such that it cannot be modified, then throw an IllegalArgumentException.
     */
    public static String modifyAttributeNameToConformToRequiredNamingConvention(final String pAttributeName)
            throws IllegalArgumentException
    {
        final String _METHODNAME = "modifyAttributeNameToConformToRequiredNamingConvention(): ";
        if (!attributeNameConformsToRequiredNamingConvention(pAttributeName))
        {
            final String lErrStr = "argument \"" + pAttributeName + "\"  contains invalid characters; must conform to "
                    + _attributeNamingConvention;
            log.info(_METHODNAME + "{}", lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        return pAttributeName;
    }

    public static boolean attributeNameConformsToRequiredNamingConvention(final String pAttributeName)
    {
        if (pAttributeName == null)
            return false;

        return pAttributeName.matches(_attributeNamingConvention);
    }

    /**
     * Checks to make sure that the schema CD element is populated with at least the code and codeSystem attributes.
     *
     * @return true if so, false if not.
     */
    public static boolean requiredAttributesForCDSpecified(final org.opencds.vmr.v1_0.schema.CD pCD)
    {
        return pCD != null && !ObjectUtils.isEmpty(pCD.getCode()) && !ObjectUtils.isEmpty(pCD.getCodeSystem());
    }

    /**
     * Checks to make sure that the schema CD element is populated with at least the code and codeSystem attributes.
     *
     * @return true if so, false if not.
     */
    public static boolean requiredAttributesForCDSpecified(final org.opencds.vmr.v1_0.internal.datatypes.CD pCD)
    {
        return pCD != null && !ObjectUtils.isEmpty(pCD.getCode()) && !ObjectUtils.isEmpty(pCD.getCodeSystem());
    }

    /**
     * Check to see if two CD elements are equal; two null elements are not equals
     */
    public static boolean cDElementsAreEqual(final CD pCD1, final CD pCD2)
    {
        if (pCD1 == null || pCD2 == null)
            return false;

        return pCD1.equals(pCD2);
    }

    /**
     * Check to see that two CD elements are equal and minimally populated; two null CD elements are not equal
     */
    public static boolean cDElementsArePopulatedAndEqual(final CD pCD1, final CD pCD2)
    {
        if (pCD1 == null || pCD2 == null)
            return false;

        if (requiredAttributesForCDSpecified(pCD1) && requiredAttributesForCDSpecified(pCD2))
            return pCD1.equals(pCD2);

        return false;
    }

    /**
     * Returns a string representation of the schema CD element
     */
    public static String toStringCD(final org.opencds.vmr.v1_0.schema.CD lCD)
    {
        if (lCD == null)
            return "No CD information supplied";

        return "CD: \n\t.getCode(): %s\n\t.getCodeSystem(): %s\n\t.geCodeSystemName(): %s\n\t.getDisplayName(): %s\n\t.getOriginalText(): %s".formatted(
                lCD.getCode(), lCD.getCodeSystem(), lCD.getCodeSystemName(), lCD.getDisplayName(), lCD.getOriginalText());
    }

    public static org.opencds.vmr.v1_0.internal.datatypes.CD toInternalCD(final org.opencds.vmr.v1_0.schema.CD pCD)
    {
        if (pCD == null)
            return null;

        final org.opencds.vmr.v1_0.internal.datatypes.CD lInternalCD = new CD();
        lInternalCD.setCode(pCD.getCode());
        lInternalCD.setCodeSystem(pCD.getCodeSystem());
        lInternalCD.setCodeSystemName(pCD.getCodeSystemName());
        lInternalCD.setDisplayName(pCD.getDisplayName());
        lInternalCD.setOriginalText(pCD.getOriginalText());

        return lInternalCD;
    }
}
