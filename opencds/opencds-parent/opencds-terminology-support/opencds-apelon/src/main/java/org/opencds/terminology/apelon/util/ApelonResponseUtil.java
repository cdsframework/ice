package org.opencds.terminology.apelon.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.opencds.common.xml.XmlEntity;

import com.apelon.dts.client.attribute.DTSProperty;
import com.apelon.dts.client.concept.DTSConcept;

public class ApelonResponseUtil {

    /**
     * Packages concepts in an {@link XmlEntity} with the following format:
     * <pre>
     * {@code
     * <ApelonDtsServiceResponse>
     *      <OpenCdsConcept>...</OpenCdsConcept>
     *      ...
     *      <OpenCdsConcept>...</OpenCdsConcept>
     * </ApelonDtsServiceResponse>}
     * 
     * Or if no results are found:
     * {@code <ApelonDtsServiceResponse>No results found!</ApelonDtsServiceResponse>}
     * @return XmlEntity
     * @throws Exception from 
     */
    public static XmlEntity getXmlResponseEntity(Set<DTSConcept> concepts) {
        XmlEntity responseEntity;
        if (concepts != null && !concepts.isEmpty()) {
            List<XmlEntity> childrenEntities = getConceptXmlEntities(concepts);
            responseEntity = new XmlEntity("ApelonDtsServiceResponse");
            for (XmlEntity child : childrenEntities) {
                responseEntity.addChild(child);
            }
        } else {
            responseEntity = new XmlEntity("ApelonDtsServiceResponse", "No results found!", false);
        }
        return responseEntity;
    }
    
    /**
     * Transforms {@link DTSConcept}s to an XML format where each concept is represented like so:
     * <pre> {@code
     *      <OpenCdsConcept>
     *          <Name>Asthma</Name>
     *          <Code_in_Source>C32</Code_in_Source>
     *          [<OID>1.23.4.5.5.3</OID>]
     *          [<UMLS_Source_Abbreviation>CPT</UMLS_Source_Abbreviation>]
     *      </OpenCdsConcept>}
     * </pre>
     * Property names are used as XML tags, having all spaces replaced with underscores.
     * @param concepts to be transformed
     * @return list of {@link XmlEntity}s 
     */
    private static List<XmlEntity> getConceptXmlEntities(Set<DTSConcept> concepts) {
        List<XmlEntity> list = new ArrayList<>();

        // For each concept
        for (DTSConcept concept : concepts) {
            // Create ConceptNode
            XmlEntity conceptEntity = new XmlEntity("OpenCdsConcept");
            XmlEntity nameEntity = new XmlEntity("Name", concept.getName(), false);
            conceptEntity.addChild(nameEntity);
            // Create nodes for each property
            for (DTSProperty property : concept.getFetchedProperties()) {
                String name = property.getName().replace(' ', '_');
                String value = property.getValue();
                conceptEntity.addChild(new XmlEntity(name, value, false));
            }
            //TODO = Fix concepts in apelon that don't have this property
            if (conceptEntity.getFirstChildWithLabel("Code_in_Source") == null) {
                String code = concept.getCode();
                conceptEntity.addChild(new XmlEntity("Code_in_Source", code, false));
            }

            list.add(conceptEntity);
        }

        return list;
    }

}
