package org.opencds.config.mapper.util;

import java.io.InputStream;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.opencds.config.api.model.ConceptDeterminationMethod;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.PluginPackage;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.api.model.SupportingData;
import org.opencds.config.mapper.ConceptDeterminationMethodMapper;
import org.opencds.config.mapper.ExecutionEngineMapper;
import org.opencds.config.mapper.KnowledgeModuleMapper;
import org.opencds.config.mapper.PluginPackageMapper;
import org.opencds.config.mapper.SemanticSignifierMapper;
import org.opencds.config.mapper.SupportingDataMapper;
import org.opencds.config.schema.ConceptDeterminationMethods;
import org.opencds.config.schema.ExecutionEngines;
import org.opencds.config.schema.KnowledgeModules;
import org.opencds.config.schema.PluginPackages;
import org.opencds.config.schema.SemanticSignifiers;
import org.opencds.config.schema.SupportingDataList;
import org.xml.sax.InputSource;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestConfigUtil
{
    private static final String CONFIG_SCHEMA_URL = "org.opencds.config.schema";
    private final JAXBContext jaxbContext;

    public RestConfigUtil()
    {
        try
        {
            this.jaxbContext = JAXBContext.newInstance(CONFIG_SCHEMA_URL);
        }
        catch (final JAXBException e)
        {
            throw new RuntimeException(e);
        }
    }

    public List<ConceptDeterminationMethod> unmarshalCdms(final InputStream is)
    {
        return unmarshal((ConceptDeterminationMethods cdms) -> ConceptDeterminationMethodMapper.internal(cdms), is, List::of);
    }

    public ConceptDeterminationMethod unmarshalCdm(final InputStream is)
    {
        return unmarshal(
                (org.opencds.config.schema.ConceptDeterminationMethod cdm) -> ConceptDeterminationMethodMapper.internal(cdm), is,
                () -> null);
    }

    public List<ExecutionEngine> unmarshalExecutionEngines(final InputStream is)
    {
        return unmarshal((ExecutionEngines ee) -> ExecutionEngineMapper.internal(ee), is, List::of);
    }

    public List<KnowledgeModule> unmarshalKnowledgeModules(final InputStream is)
    {
        return unmarshal((KnowledgeModules kms) -> KnowledgeModuleMapper.internal(kms), is, List::of);
    }

    public List<SemanticSignifier> unmarshalSemanticSignifiers(final InputStream is)
    {
        return unmarshal((SemanticSignifiers ss) -> SemanticSignifierMapper.internal(ss), is, List::of);
    }

    public List<SupportingData> unmarshalSupportingDataList(final InputStream is)
    {
        return unmarshal((SupportingDataList sdl) -> SupportingDataMapper.internal(sdl), is, List::of);
    }

    public List<PluginPackage> unmarshalPluginPackages(final InputStream is)
    {
        return unmarshal((PluginPackages pp) -> PluginPackageMapper.internal(pp), is, List::of);
    }

    @SuppressWarnings("unchecked")
    public <X, T> T unmarshal(final Function<X, T> mapper, final InputStream is, final Supplier<T> defaultValueSupplier)
    {
        try
        {
            final SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            spf.setNamespaceAware(true);

            return mapper.apply(
                    (X) unmarshaller().unmarshal(new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(is))));
        }
        catch (final Exception e)
        {
            log.warn("Resource is not an expected instance.");
        }
        finally
        {
            log.info("Loaded resource");
        }
        return defaultValueSupplier.get();
    }

    private Unmarshaller unmarshaller() throws JAXBException
    {
        return jaxbContext.createUnmarshaller();
    }
}
