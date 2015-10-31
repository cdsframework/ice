package org.opencds.terminology.apelon;

import java.util.Map;
import java.util.Set;

import org.opencds.common.xml.XmlEntity;
import org.opencds.terminology.apelon.method.FindConceptByCode;
import org.opencds.terminology.apelon.method.FindConceptByName;
import org.opencds.terminology.apelon.method.FindConceptsWithPropertyMatching;
import org.opencds.terminology.apelon.method.FindDescendantsOfConcept;
import org.opencds.terminology.apelon.method.FindDirectChildrenOfConcept;
import org.opencds.terminology.apelon.util.ApelonResponseUtil;
import org.opencds.terminology.apelon.util.DTSConceptNameComparator;
import org.opencds.terminology.apelon.util.SetUtil;

import com.apelon.dts.client.concept.DTSConcept;

public enum ApelonDtsQuery {
    CONCEPT_BY_NAME("findConceptByName") {
        @Override
        public Set<DTSConcept> executeInternal(ApelonDtsClient client, Map<String, String> params)
                throws ApelonClientException {
            return SetUtil.asSet(FindConceptByName.execute(client, params.get("name")));
        }
    },
    CONCEPT_BY_CODE("findConceptByCode") {
        @Override
        public Set<DTSConcept> executeInternal(ApelonDtsClient client, Map<String, String> params)
                throws ApelonClientException {
            return SetUtil.asSet(FindConceptByCode.execute(client, params.get("code")));
        }
    },
    DIRECT_CHILDREN("findDirectChildrenOfConcept") {
        @Override
        public Set<DTSConcept> executeInternal(ApelonDtsClient client, Map<String, String> params)
                throws ApelonClientException {
            return SetUtil.asSortedSet(FindDirectChildrenOfConcept.execute(client, params.get("parentConcept"),
                    Boolean.valueOf(params.get("includeParent"))),
                    dtsConceptNameComparator);
        }
    },
    DESCENDANTS("findDescendantsOfConcept") {
        @Override
        public Set<DTSConcept> executeInternal(ApelonDtsClient client, Map<String, String> params)
                throws ApelonClientException {
            return SetUtil.asSortedSet(FindDescendantsOfConcept.execute(client, params.get("rootConcept"),
                    Integer.valueOf(params.get("maxLevelsDeep")), Boolean.valueOf(params.get("includeRoot"))),
                    dtsConceptNameComparator);
        }
    },
    WITH_PROPERTY_MATCHING("findConceptsWithPropertyMatching") {
        @Override
        public Set<DTSConcept> executeInternal(ApelonDtsClient client, Map<String, String> params)
                throws ApelonClientException {
            return SetUtil.asSortedSet(FindConceptsWithPropertyMatching.execute(client, params.get("propertyTypeName"),
                    params.get("propertyValuePattern")),
                    dtsConceptNameComparator);
        }
    };

    protected static final DTSConceptNameComparator dtsConceptNameComparator = new DTSConceptNameComparator(); 
    
    private String queryMethod;

    private ApelonDtsQuery(String queryMethod) {
        this.queryMethod = queryMethod;
    }

    public XmlEntity execute(ApelonDtsClient client, Map<String, String> params) throws ApelonClientException {
        return ApelonResponseUtil.getXmlResponseEntity(executeInternal(client, params));
    }

    protected abstract Set<DTSConcept> executeInternal(ApelonDtsClient client, Map<String, String> params)
            throws ApelonClientException;

    public static ApelonDtsQuery resolve(String queryMethod) {
        for (ApelonDtsQuery dtsq : values()) {
            if (dtsq.queryMethod.equalsIgnoreCase(queryMethod)) {
                return dtsq;
            }
        }
        return null;
    }
}