package org.cdsframework.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

import javax.xml.transform.TransformerException;

import org.cdsframework.rest.opencds.utils.MarshalUtils;
import org.junit.Test;
import org.omg.dss.evaluation.EvaluateAtSpecifiedTime;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sdn
 */
@Slf4j
public class JsonUnitTest
{
    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void hello() throws JAXBException, TransformerException, IOException
    {
        EvaluateAtSpecifiedTime evaluateAtSpecifiedTime;

        try (final InputStream inputStream = Files.newInputStream(Path.of("src/test/resources/sampleEvaluateAtSpecifiedTime.xml")))
        {
            evaluateAtSpecifiedTime = MarshalUtils.unmarshal(inputStream, EvaluateAtSpecifiedTime.class);
        }

        log.debug(mapper.writeValueAsString(evaluateAtSpecifiedTime));

        try (final InputStream inputStream = Files.newInputStream(Path.of("src/test/resources/sampleEvaluateAtSpecifiedTime.json")))
        {
            evaluateAtSpecifiedTime = mapper.readValue(inputStream, EvaluateAtSpecifiedTime.class);
        }

        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(
                new ByteArrayInputStream(evaluateAtSpecifiedTime.getEvaluationRequest()
                        .getDataRequirementItemData()
                        .getFirst()
                        .getData()
                        .getBase64EncodedPayload()
                        .getFirst())), StandardCharsets.UTF_8)))
        {
            final StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line);
            log.debug(stringBuilder.toString());
        }
    }
}
