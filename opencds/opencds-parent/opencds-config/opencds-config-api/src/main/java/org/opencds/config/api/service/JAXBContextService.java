package org.opencds.config.api.service;

import javax.xml.bind.JAXBContext;

import org.opencds.config.api.model.SemanticSignifier;

public interface JAXBContextService {

    JAXBContext getJAXBContext(SemanticSignifier semanticSignifier);

    JAXBContext getJAXBContext(String schema);

}
