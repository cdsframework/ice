package org.opencds.config.util;

import java.util.regex.Pattern;

import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.PPId;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.impl.CDMIdImpl;
import org.opencds.config.api.model.impl.KMIdImpl;
import org.opencds.config.api.model.impl.PPIdImpl;
import org.opencds.config.api.model.impl.SSIdImpl;
import org.opencds.config.schema.ConceptDeterminationMethod;
import org.opencds.config.schema.SemanticSignifierId;

public class URIUtil {
    // the carat is %5E
    private static final String ENTITY_IDENTIFIER_URI_DELIMITER = "^";
    private static final Pattern EIUD_PATTERN = Pattern.compile("\\" + ENTITY_IDENTIFIER_URI_DELIMITER);

    public static KMId getKMId(String eiString) {
        String[] parts = split(eiString);
        // scopingEntityId, businessId, version
        return KMIdImpl.create(parts[0], parts[1], parts[2]);
    }

    public static String buildKMIdString(org.opencds.config.schema.KMId kmid) {
        return build(kmid.getScopingEntityId(), kmid.getBusinessId(), kmid.getVersion());
    }

    public static CDMId getCDMId(String cdmidString) {
        String[] parts = split(cdmidString);
        // code, codeSystem, version
        return CDMIdImpl.create(parts[0], parts[1], parts[2]);
    }

    public static String buildCDMIdString(ConceptDeterminationMethod cdm) {
        return build(cdm.getCodeSystem(), cdm.getCode(), cdm.getVersion());
    }
    
    public static String buildPPIdString(org.opencds.config.schema.PluginPackageId ppid) {
        return build(ppid.getScopingEntityId(), ppid.getBusinessId(), ppid.getVersion());
    }

    public static PPId getPPId(String ppidString) {
        String[] parts = split(ppidString);
        // scopingEntityId, businessId, version
        return PPIdImpl.create(parts[0], parts[1], parts[2]);
    }
    
    public static SSId getSSId(String ssidString) {
        String[] parts = split(ssidString);
        // scopingEntityId, businessId, version
        return SSIdImpl.create(parts[0], parts[1], parts[2]);
    }
    
    public static String buildSSIdString(SemanticSignifierId semanticSignifierId) {
        return build(semanticSignifierId.getScopingEntityId(), semanticSignifierId.getBusinessId(), semanticSignifierId.getVersion());
    }

    private static String[] split(String string) {
        String[] parts = string.split(EIUD_PATTERN.pattern());
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid id: " + string);
        }
        return parts;
    }

    private static String build(String part1, String part2, String part3) {
        return part1 + ENTITY_IDENTIFIER_URI_DELIMITER + part2 + ENTITY_IDENTIFIER_URI_DELIMITER + part3;
    }

}
