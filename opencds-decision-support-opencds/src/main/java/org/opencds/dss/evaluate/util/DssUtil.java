package org.opencds.dss.evaluate.util;

import java.util.zip.GZIPInputStream;

import org.omg.dss.DataRequirementItemData;
import org.omg.dss.EntityIdentifier;
import org.opencds.common.structures.EvaluationRequestDataItem;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DssUtil
{
    public static EntityIdentifier makeEI(final String eiString)
    {
        final EntityIdentifier ei = new EntityIdentifier();
        ei.setScopingEntityId(eiString.substring(0, eiString.indexOf("^")));
        ei.setBusinessId(eiString.substring(eiString.indexOf("^") + 1, eiString.lastIndexOf("^")));
        ei.setVersion(eiString.substring(eiString.lastIndexOf("^") + 1));
        return ei;
    }

    public static String makeEIString(final EntityIdentifier ei)
    {
        final String scopingEntityId = ei.getScopingEntityId();
        final String businessId = ei.getBusinessId();
        final String version = ei.getVersion();
        return scopingEntityId + "^" + businessId + "^" + version;
    }

    public static boolean isGZipped(final byte[] bytes)
    {
        if (bytes == null || bytes.length < 2)
            return false;

        final int head = ((int) bytes[0] & 0xff) | ((bytes[1] << 8) & 0xff00);
        return (GZIPInputStream.GZIP_MAGIC == head);
    }

    public static boolean isGZipDesignated(final DataRequirementItemData driData)
    {
        return (driData != null && driData.getDriId() != null && driData.getDriId().getContainingEntityId() != null
                && driData.getDriId().getContainingEntityId().getBusinessId() != null && driData.getDriId()
                .getContainingEntityId()
                .getBusinessId()
                .toLowerCase()
                .contains("gzip"));
    }

    public static boolean isGZipDesignated(final EvaluationRequestDataItem evaluationRequestDataItem)
    {
        return (evaluationRequestDataItem != null && evaluationRequestDataItem.inputContainingEntityId() != null
                && evaluationRequestDataItem.inputContainingEntityId().toLowerCase().contains("gzip"));
    }
}
