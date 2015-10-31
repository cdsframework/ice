package org.opencds.config.service;

import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.api.model.XSDComputableDefinition;
import org.opencds.config.api.service.JAXBContextService;

public class JAXBContextServiceImpl implements JAXBContextService {
    private static final Log log = LogFactory.getLog(JAXBContextServiceImpl.class);

    @Override
    public JAXBContext getJAXBContext(SemanticSignifier semanticSignifier) {
        JAXBContext jaxbContext = null;
        List<XSDComputableDefinition> xsdcds = semanticSignifier.getXSDComputableDefinitions();
        if (xsdcds == null || xsdcds.size() == 0) {
            throw new OpenCDSRuntimeException("SemanticSignifier contains no XSD computable definitions for SSID: "
                    + semanticSignifier.getSSId() + ".");
        }
        XSDComputableDefinition xsdcd = xsdcds.get(0);
        if (xsdcd == null) {
            throw new OpenCDSRuntimeException("XSD Computable Definition for SSID (" + semanticSignifier.getSSId()
                    + ") is null");
        }
        jaxbContext = getJAXBContext(xsdcd.getUrl());
        if (jaxbContext == null) {
            log.fatal("JAXBContext cannot be loaded for dataModel (" + semanticSignifier.getSSId() + "), for schema: "
                    + semanticSignifier.getXSDComputableDefinitions().get(0).getUrl()
                    + " XML Marshalling/Unmarshalling will fail for this schema.");
        }
        return jaxbContext;
    }

    @Override
    public JAXBContext getJAXBContext(String schemaUrl) {
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance(schemaUrl);
            log.debug("JAXBContext created: " + jaxbContext);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return jaxbContext;
    }

}
