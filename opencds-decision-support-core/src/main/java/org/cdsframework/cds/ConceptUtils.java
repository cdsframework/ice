package org.cdsframework.cds;

import org.cdsframework.ice.config.iceSupportingProperties.BaseConceptDescriptor;
import org.opencds.vmr.v1_0.internal.datatypes.CD;

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
            log.debug(_METHODNAME + "{}", lErrStr);
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
     * Check to see if two CD elements are equal; two null elements are not equals
     */
    public static boolean cDElementsAreEqual(final CD pCD1, final CD pCD2)
    {
        if (pCD1 == null || pCD2 == null)
            return false;

        return pCD1.equals(pCD2);
    }

    public static org.opencds.vmr.v1_0.internal.datatypes.CD toInternalCD(final BaseConceptDescriptor pConceptDescriptor)
    {
        if (pConceptDescriptor == null)
            return null;

        final org.opencds.vmr.v1_0.internal.datatypes.CD lInternalCD = new CD();
        lInternalCD.setCode(pConceptDescriptor.code());
        lInternalCD.setCodeSystem(pConceptDescriptor.codeSystem());
        lInternalCD.setCodeSystemName(pConceptDescriptor.codeSystemName());
        lInternalCD.setDisplayName(pConceptDescriptor.displayName());
        lInternalCD.setOriginalText(pConceptDescriptor.originalText());

        return lInternalCD;
    }
}
