/*
 * Copyright 2013-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.dss.evaluate.util;

import org.omg.dss.common.EntityIdentifier;
import org.omg.dss.evaluation.requestresponse.DataRequirementItemData;
import org.opencds.common.structures.EvaluationRequestDataItem;

import java.util.zip.GZIPInputStream;

public class DssUtil {

    public static EntityIdentifier makeEIFromCommon(org.opencds.config.api.model.EntityIdentifier commonEI) {
        EntityIdentifier ei = new EntityIdentifier();
        ei.setScopingEntityId(commonEI.getScopingEntityId());
        ei.setBusinessId(commonEI.getBusinessId());
        ei.setVersion(commonEI.getVersion());
        return ei;
    }

    public static EntityIdentifier makeEI( String eiString ) {
        EntityIdentifier ei = new EntityIdentifier();
        ei.setScopingEntityId( eiString.substring(0, eiString.indexOf("^")) );
        ei.setBusinessId( eiString.substring(eiString.indexOf("^")+1, eiString.lastIndexOf("^")) );
        ei.setVersion( eiString.substring(eiString.lastIndexOf("^")+1) );
        return ei;
    }

    public static EntityIdentifier makeEI(String scopingEntityId, String businessId, String version) {
        EntityIdentifier ei = new EntityIdentifier();
        ei.setScopingEntityId(scopingEntityId);
        ei.setBusinessId(businessId);
        ei.setVersion(version);
        return ei;
    }

    public static String makeEIString ( EntityIdentifier ei ) {
        String scopingEntityId = ei.getScopingEntityId();
        String businessId = ei.getBusinessId();
        String version = ei.getVersion();
        return scopingEntityId + "^" + businessId + "^" + version;
    }
    public static boolean isGZipped(byte[] bytes) {
        if (bytes == null || bytes.length < 2)
            return false;
        int head = ((int) bytes[0] & 0xff) | ((bytes[1] << 8) & 0xff00);
        return (GZIPInputStream.GZIP_MAGIC == head);
    }

    public static boolean isGZipDesignated(DataRequirementItemData driData) {
        return (driData != null
                && driData.getDriId() != null
                && driData.getDriId().getContainingEntityId() != null
                && driData.getDriId().getContainingEntityId().getBusinessId() != null
                && driData.getDriId().getContainingEntityId().getBusinessId().toLowerCase().contains("gzip"));
    }

    public static boolean isGZipDesignated(EvaluationRequestDataItem evaluationRequestDataItem) {
        return (evaluationRequestDataItem != null
                && evaluationRequestDataItem.getInputContainingEntityId() != null
                && evaluationRequestDataItem.getInputContainingEntityId().toLowerCase().contains("gzip"));
    }

}
