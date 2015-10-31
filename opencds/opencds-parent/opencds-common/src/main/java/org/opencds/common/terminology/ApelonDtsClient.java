package org.opencds.common.terminology;

/**
 * Copyright 2013 OpenCDS.org
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 *	
 */

import java.io.IOException;
import java.io.InputStream;

import org.opencds.common.xml.XmlConverter;
import org.opencds.common.xml.XmlEntity;
import org.opencds.common.xml.XmlHttpSender;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

/**
 * The ApelonDtsClient is used as an interface to the {@link ApelonDtsService}.
 * It returns Apelon query results as XmlEnties which can be parsed for names and properties
 * 
 * @author Tyler Tippetts
 *
 */
/**
 * @author u0734599
 *
 */
public class ApelonDtsClient {

    private String apelonDtsServiceHostUrl;

    /**
     * @param apelonDtsServiceHostUrl (e.g. "http://localhost:8080")
     */
    public ApelonDtsClient(String apelonDtsServiceHostUrl) {
        this.apelonDtsServiceHostUrl = apelonDtsServiceHostUrl;
    }
 
    /**
     * Finds single concept by name
     * @param name of concept to find
     * @return an XmlEntity with child entity labeled "OpenCdsConcept" with concept name and properties as children entities
     * @throws Exception on communication error
     */
    public XmlEntity findConceptByName(String name) throws Exception {
        XmlEntity requestEntity = new XmlEntity("ApelonDtsServiceRequest");
        requestEntity.addChild(new XmlEntity("queryType", "findConceptByName", false));
        requestEntity.addChild(new XmlEntity("name", name, false));
        return sendRequestGetResponse(apelonDtsServiceHostUrl, requestEntity);
    }

    /**
     * Finds single concept by code
     * @param code of concept to find
     * @return an XmlEntity with child entity labeled "OpenCdsConcept" with concept name and properties as children entities
     * @throws Exception on communication error
     */
    public XmlEntity findConceptByCode(String code) throws Exception {
        XmlEntity requestEntity = new XmlEntity("ApelonDtsServiceRequest");
        requestEntity.addChild(new XmlEntity("queryType", "findConceptByCode", false));
        requestEntity.addChild(new XmlEntity("code", code, false));
        return sendRequestGetResponse(apelonDtsServiceHostUrl, requestEntity);
    }

    /**
     * Finds multiple concepts that are direct children of parentConcept
     * @param parentConceptIdentifier name or code (e.g. "Concept Determination Method" or "C35")
     * @param includeParent (e.g. in above example, true would include "Concept Determination Method" concept in results)
     * @return an XmlEntity with children entities labeled "OpenCdsConcept" with concept name and properties as children entities
     * @throws Exception on communication error
     */
    public XmlEntity findDirectChildrenOfConcept(String parentConceptIdentifier, boolean includeParent)
            throws Exception {
        XmlEntity requestEntity = new XmlEntity("ApelonDtsServiceRequest");
        requestEntity.addChild(new XmlEntity("queryType", "findDirectChildrenOfConcept", false));
        requestEntity.addChild(new XmlEntity("parentConcept", parentConceptIdentifier, false));
        requestEntity.addChild(new XmlEntity("includeParent", String.valueOf(includeParent), false));
        return sendRequestGetResponse(apelonDtsServiceHostUrl, requestEntity);
    }

    /**
     * Finds multiple concepts that are descendendants of parentConcept
     * @param rootConceptIdentifier name or code (e.g. "Concept Determination Method" or "C35")
     * @param maxLevelsDeep number of levels to traverse in search (e.g. 1 = children, 2 includes grandchildren, etc.)
     * @param includeRoot (e.g. in above example, true would include "Concept Determination Method" concept in results)
     * @return an XmlEntity with children entities labeled "OpenCdsConcept" with concept name and properties as children entities
     * @throws Exception on communication error
     */
    public XmlEntity findDescendantsOfConcept(String rootConceptIdentifier, int maxLevelsDeep, boolean includeRoot)
            throws Exception {
        XmlEntity requestEntity = new XmlEntity("ApelonDtsServiceRequest");
        requestEntity.addChild(new XmlEntity("queryType", "findDescendantsOfConcept", false));
        requestEntity.addChild(new XmlEntity("rootConcept", rootConceptIdentifier, false));
        requestEntity.addChild(new XmlEntity("maxLevelsDeep", String.valueOf(maxLevelsDeep), false));
        requestEntity.addChild(new XmlEntity("includeRoot", String.valueOf(includeRoot), false));
        return sendRequestGetResponse(apelonDtsServiceHostUrl, requestEntity);
    }
    
    /**
     * Finds multiple concepts that have a specified property and property value
     * @param propertyTypeName (e.g. "Description")
     * @param propertyValuePattern (e.g. "*find me*" where * is multi-character wild card)
     * @return an XmlEntity with children entities labeled "OpenCdsConcept" with concept name and properties as children entities
     * @throws Exception on communication error
     */
    public XmlEntity findConceptsWithPropertyMatching(String propertyTypeName, String propertyValuePattern) throws Exception {
        XmlEntity requestEntity = new XmlEntity("ApelonDtsServiceRequest");
        requestEntity.addChild(new XmlEntity("queryType", "findConceptsWithPropertyMatching", false));
        requestEntity.addChild(new XmlEntity("propertyTypeName", propertyTypeName, false));
        requestEntity.addChild(new XmlEntity("propertyValuePattern", propertyValuePattern, false));
        return sendRequestGetResponse(apelonDtsServiceHostUrl, requestEntity);
    }  
    
    /**
     * Utility method to extract a string property from XmlEntity result
     * @param entity 
     * @param propertyName should match a child label of entity
     * @return property value as String
     * @throws Exception if propertyName is not found as child label
     */
    public String getStringProperty(XmlEntity entity, String propertyName) throws Exception {
        propertyName = propertyName.replace(' ', '_');
        XmlEntity childEntity = entity.getFirstChildWithLabel(propertyName);
        if(childEntity == null) 
            throw new Exception(propertyName + " not found");
        return childEntity.getValue();
    }
    
    /**
     * Utility method to extract a boolean property from XmlEntity result
     * @param entity 
     * @param propertyName should match a child label of entity
     * @return property value as boolean
     * @throws Exception if propertyName is not found as child label or if value cannot be parsed as an boolean
     */
    public boolean getBooleanProperty(XmlEntity entity, String propertyName) throws Exception {        
        return Boolean.parseBoolean(getStringProperty(entity, propertyName)); 
    }
    
    /**
     * Utility method to extract an int property from XmlEntity result
     * @param entity 
     * @param propertyName should match a child label of entity
     * @return property value as int
     * @throws Exception if propertyName is not found as child label or if value cannot be parsed as an int
     */
    public int getIntProperty(XmlEntity entity, String propertyName) throws NumberFormatException, Exception {
        return Integer.parseInt(getStringProperty(entity, propertyName));
    }
    
    /**
     * Sends requestEntity to webservice
     * 
     * @param requestEntity
     * @return response {@link XmlEntity}
     * @throws SAXParseException if XML unmarshalling fails
     * @throws IOException if web-service call fails
     */
    private XmlEntity sendRequestGetResponse(String webServiceHostUrl, XmlEntity requestEntity)
            throws SAXParseException,
            IOException {
        InputStream responseStream = new XmlHttpSender("", "").sendXmlRequestGetResponse(webServiceHostUrl, requestEntity);
        InputSource responseSource = new InputSource(responseStream);
        return XmlConverter.getInstance().unmarshalXml(responseSource, false, null);
    }

}
