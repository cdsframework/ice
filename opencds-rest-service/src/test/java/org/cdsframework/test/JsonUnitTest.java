package org.cdsframework.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.rest.opencds.utils.MarshalUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.dss.evaluation.EvaluateAtSpecifiedTime;

/**
 *
 * @author sdn
 */
public class JsonUnitTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Log log = LogFactory.getLog(JsonUnitTest.class);

    public JsonUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void hello()
            throws JAXBException, TransformerException, FileNotFoundException, JsonProcessingException, IOException {

        File file = new File("src/test/resources/sampleEvaluateAtSpecifiedTime.xml");
        FileInputStream fileInputStream = new FileInputStream(file);

        EvaluateAtSpecifiedTime evaluateAtSpecifiedTime = MarshalUtils.unmarshal(fileInputStream,
                EvaluateAtSpecifiedTime.class);

        fileInputStream.close();

        String jsonString = mapper.writeValueAsString(evaluateAtSpecifiedTime);
        log.debug(jsonString);

        file = new File("src/test/resources/sampleEvaluateAtSpecifiedTime.json");

        fileInputStream = new FileInputStream(file);

        evaluateAtSpecifiedTime = mapper.readValue(fileInputStream, EvaluateAtSpecifiedTime.class);

        fileInputStream.close();

        InputStream inputStream = new ByteArrayInputStream(evaluateAtSpecifiedTime.getEvaluationRequest()
                .getDataRequirementItemData().get(0).getData().getBase64EncodedPayload().get(0));
        GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipInputStream, "UTF-8"));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        bufferedReader.close();
        gzipInputStream.close();
        inputStream.close();
        log.debug(stringBuilder.toString());

    }
}
