package org.cdsframework.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;
import org.cdsframework.rest.opencds.MarshalUtils;
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
    public void hello() throws JAXBException, TransformerException, FileNotFoundException, JsonProcessingException, IOException {

        File file = new File("src/test/resources/sampleEvaluateAtSpecifiedTime.xml");
        FileInputStream fileInputStream = new FileInputStream(file);

        EvaluateAtSpecifiedTime evaluateAtSpecifiedTime = MarshalUtils.unmarshal(fileInputStream, EvaluateAtSpecifiedTime.class);

        fileInputStream.close();

        String jsonString = mapper.writeValueAsString(evaluateAtSpecifiedTime);
        System.out.println(jsonString);

        file = new File("src/test/resources/sampleEvaluateAtSpecifiedTime.json");

        fileInputStream = new FileInputStream(file);

        evaluateAtSpecifiedTime = mapper.readValue(fileInputStream, EvaluateAtSpecifiedTime.class);

        fileInputStream.close();

        System.out.println(new String(evaluateAtSpecifiedTime.getEvaluationRequest().getDataRequirementItemData().get(0).getData().getBase64EncodedPayload().get(0)));

    }
}
