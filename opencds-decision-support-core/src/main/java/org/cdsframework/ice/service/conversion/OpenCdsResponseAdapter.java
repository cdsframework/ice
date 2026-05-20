package org.cdsframework.ice.service.conversion;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.omg.dss.EvaluationResponse;
import org.omg.dss.FinalKMEvaluationResponse;
import org.omg.dss.KMEvaluationResultData;
import org.omg.dss.SemanticPayload;
import org.opencds.vmr.v1_0.schema.CDSOutput;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.dataformat.xml.XmlMapper;

@Slf4j
@UtilityClass
public class OpenCdsResponseAdapter
{
    private static final XmlMapper xmlMapper = XmlMapper.xmlBuilder()
            .defaultUseWrapper(false)
            .findAndAddModules()
            .changeDefaultPropertyInclusion(i -> i.withValueInclusion(JsonInclude.Include.NON_NULL))
            .build();

    public static List<CDSOutput> extractCdsOutputs(final EvaluationResponse evaluateAtSpecifiedTimeResponse)
    {
        return Optional.ofNullable(evaluateAtSpecifiedTimeResponse)
                .map(EvaluationResponse::getFinalKMEvaluationResponse)
                .stream()
                .flatMap(Collection::stream)
                .map(FinalKMEvaluationResponse::getKmEvaluationResultData)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(KMEvaluationResultData::getData)
                .filter(Objects::nonNull)
                .map(SemanticPayload::getBase64EncodedPayload)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(OpenCdsResponseAdapter::tryParseCdsOutputPayload)
                .flatMap(Optional::stream)
                .toList();
    }

    private static Optional<CDSOutput> tryParseCdsOutputPayload(final byte[] payload)
    {
        try
        {
            return Optional.of(parseCdsOutputPayload(payload));
        }
        catch (final RuntimeException e)
        {
            log.debug("Skipping semantic payload that is not CDSOutput XML: {}", e.getMessage());
        }

        return Optional.empty();
    }

    private static CDSOutput parseCdsOutputPayload(final byte[] payload)
    {
        try (final InputStream bIn = new ByteArrayInputStream(payload))
        {
            try (final Reader reader = new InputStreamReader(bIn))
            {
                return xmlMapper.readValue(reader, CDSOutput.class);
            }
        }
        catch (final IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
