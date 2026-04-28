package org.cdsframework.ice.supportingdata;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SupplementalReasonSupport
{
    public enum SupplementalReasonType
    {
        NONE,
        EVALUATION,
        RECOMMENDATION;

        public boolean isSupplemental()
        {
            return this != NONE;
        }
    }

    public static final String SUPPLEMENTAL_TEXT_CODE = "SUPPLEMENTAL_TEXT";
    public static final String SUPPLEMENTAL_EVALUATION_REASON_CONCEPT =
            ICEConceptType.SUPPLEMENTAL_EVALUATION_REASON.getIceConceptTypeValue();
    public static final String SUPPLEMENTAL_RECOMMENDATION_REASON_CONCEPT =
            ICEConceptType.SUPPLEMENTAL_RECOMMENDATION_REASON.getIceConceptTypeValue();

    public static boolean isSupplementalReasonConceptCodeSystem(final String cdsListCode)
    {
        return getSupplementalReasonTypeForCodeSystem(cdsListCode).isSupplemental();
    }

    public static SupplementalReasonType getSupplementalReasonTypeForCodeSystem(final String cdsListCode)
    {
        if (SUPPLEMENTAL_EVALUATION_REASON_CONCEPT.equals(cdsListCode))
            return SupplementalReasonType.EVALUATION;
        if (SUPPLEMENTAL_RECOMMENDATION_REASON_CONCEPT.equals(cdsListCode))
            return SupplementalReasonType.RECOMMENDATION;
        return SupplementalReasonType.NONE;
    }
}
