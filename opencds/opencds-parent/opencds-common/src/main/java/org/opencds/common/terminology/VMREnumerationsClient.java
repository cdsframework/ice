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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.xml.XmlEntity;

/**
 * VMREnumerations creates an enumeration lookup class in the format specified
 * for Guvnor.
 * 
 * Two things have to happen for this to work:
 * 
 * 1. This class has to be built as a jar file, and deployed as a library to the
 * container that runs Guvnor
 * 
 * 2. The global enumerations in Guvnor need to all be changed to the following
 * structure (ref.:
 * http://docs.jboss.org/drools/release/5.5.0.Final/drools-guvnor
 * -docs/html_single/index.html#d0e1927 ): =(new
 * VMREnumerations()).loadData('Fact.field') where 'Fact.field' is something
 * like the following: 'AdverseEventConcept.determinationMethodCode' or
 * 'AdverseEventConcept.openCdsConceptCode'
 * 
 * 
 * @author Tyler Tippetts
 * 
 */
public class VMREnumerationsClient {
    private static final Log log = LogFactory.getLog(VMREnumerationsClient.class);
    protected static final String APELON_SERVICE_URL_PROPERTY_NAME = "guvnor.vmrenumerations.service.url";
    protected static final String CONFIG_LOCATION = ".opencds";
    protected static final String CONFIG_FILE = "opencds-guvnor.properties";
    
    private ApelonDtsClient apelonDtsClient;
    
    public VMREnumerationsClient() {
        String apelonServiceUrl = System.getProperty(APELON_SERVICE_URL_PROPERTY_NAME);
        if (apelonServiceUrl == null || apelonServiceUrl.isEmpty()) {
            String path = System.getProperty("user.home") + File.separator + CONFIG_LOCATION + File.separator + CONFIG_FILE;
            File file = new File(path);
            try (InputStream in = new FileInputStream(file)) {
                Properties config = new Properties();
                config.load(in);
                apelonServiceUrl = config.getProperty(APELON_SERVICE_URL_PROPERTY_NAME);
            } catch (IOException e) {
                log.error(e.getMessage(),e);
                throw new RuntimeException("Failure loading or cannot find system property; property= " + APELON_SERVICE_URL_PROPERTY_NAME + ", expected location= " + path);
            }
        }
        if (apelonServiceUrl == null) {
            throw new RuntimeException("Could not find ApelonDTS Service URL");
        }
        this.apelonDtsClient = new ApelonDtsClient(apelonServiceUrl);
    }

    public List<String> getOpenCdsConcepts(String openCdsConceptType) {  
        try {
            if (openCdsConceptType == null) {
                throw new Exception("openCdsConceptType cannot be null");
            }
            //Handle Guvnor Update on "problemConcept"
            XmlEntity responseEntity;
            switch (openCdsConceptType) {
            case "calendarUnit":
                return getCalendarUnits();
            case "all":
                return getAllConceptsAndAssertions();
            case "assertion":
                responseEntity = apelonDtsClient.findDescendantsOfConcept("OpenCDS Assertions", 20, false);
                break;
            case "determinationMethod":
                responseEntity = apelonDtsClient.findDirectChildrenOfConcept("Concept Determination Method", false);
                break;
            default:
                XmlEntity initialResponseEntity = apelonDtsClient.findConceptsWithPropertyMatching("OpenCDS Concept Type",
                        openCdsConceptType);
                XmlEntity parentEntity = initialResponseEntity.getFirstChildWithLabel("OpenCdsConcept");
                if (parentEntity != null) {
                    XmlEntity parentCodeEntity = parentEntity.getFirstChildWithLabel("Code_in_Source");
                    String code = parentCodeEntity.getValue();
                    responseEntity = apelonDtsClient.findDirectChildrenOfConcept(code, false);
                } else {
                    return new ArrayList<>();
                }
            }
            return getConceptCodeNameStringsFromResponse(responseEntity);
        } catch (Exception e) {
            e.printStackTrace();
            return Arrays.asList("Error connecting to VMREnumerationsService.  Details: " + e.getMessage());
        }
    }
    
    private List<String> getAllConceptsAndAssertions() throws Exception {
        XmlEntity concepts = apelonDtsClient.findDescendantsOfConcept("OpenCDS Concepts", 20, false);
        XmlEntity assertions = apelonDtsClient.findDescendantsOfConcept("OpenCDS Assertions", 20, false);
        SortedMap<String, String> codes = new TreeMap<>();
        for (XmlEntity concept : concepts.getChildren()) {
            String code = apelonDtsClient.getStringProperty(concept, "Code in Source");
            String name = apelonDtsClient.getStringProperty(concept, "Name");
            codes.put(name, code);
        }
        for (XmlEntity concept : assertions.getChildren()) {
            String code = apelonDtsClient.getStringProperty(concept, "Code in Source");
            String name = apelonDtsClient.getStringProperty(concept, "Name");
            codes.put(name, code);
        }
        List<String> codeEnumList = new ArrayList<>();
        for (Map.Entry<String, String> entry : codes.entrySet()) {
            codeEnumList.add(entry.getValue() + "=" + entry.getKey());
        }
        return codeEnumList;
    }

    private List<String> getCalendarUnits() {
        List<String> d = new ArrayList<String>();
        d.add(Calendar.YEAR + "=year(s)");
        d.add(Calendar.MONTH + "=month(s)");
        d.add(Calendar.WEEK_OF_YEAR + "=week(s)");
        d.add(Calendar.DATE + "=day(s)");
        d.add(Calendar.HOUR_OF_DAY + "=hour(s)");
        d.add(Calendar.MINUTE + "=minute(s)");
        d.add(Calendar.SECOND + "=second(s)");
        return d;
    }

    private List<String> getConceptCodeNameStringsFromResponse(XmlEntity responseEntity) throws Exception {
        List<String> arrayToReturn = new ArrayList<String>();

        String codeLabel = "Code in Source";
        String nameLabel = "Name";
        for (XmlEntity openCdsConceptEntity : responseEntity.getChildrenWithLabel("OpenCdsConcept")) {
            String code = apelonDtsClient.getStringProperty(openCdsConceptEntity, codeLabel);
            String name = apelonDtsClient.getStringProperty(openCdsConceptEntity, nameLabel);
            arrayToReturn.add(code + "=" + name);
        }

        return arrayToReturn;
    }    
    
    public void setApelonDtsClient(String url) {
        this.apelonDtsClient = new ApelonDtsClient(url);
    }
    
}
