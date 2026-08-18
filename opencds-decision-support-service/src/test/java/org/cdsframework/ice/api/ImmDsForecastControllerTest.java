package org.cdsframework.ice.api;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.cdsframework.fhir.Parameters;
import org.cdsframework.fhir.ParametersParameter;
import org.cdsframework.ice.service.conversion.VmrConversionComponent;
import org.junit.jupiter.api.Test;
import org.omg.dss.EvaluateAtSpecifiedTime;
import org.opencds.dss.evaluate.Evaluation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

class ImmDsForecastControllerTest
{
    @Test
    void exposesImmDsForecastAsSystemLevelOperation() throws NoSuchMethodException
    {
        assertArrayEquals(new String[] { "/cds" }, ImmDsForecastController.class.getAnnotation(RequestMapping.class).value());
        assertArrayEquals(new String[] { "/$immds-forecast" },
                ImmDsForecastController.class.getMethod("immDsForecast", Parameters.class)
                        .getAnnotation(PostMapping.class)
                        .value());
    }

    @Test
    void delegatesKnowledgeBaseRequestAndReturnsSharedFlatResponse() throws Exception
    {
        final Evaluation evaluationService = mock(Evaluation.class);
        final VmrConversionComponent vmrConversionComponent = mock(VmrConversionComponent.class);
        final ImmDsForecastController controller =
                new ImmDsForecastController(evaluationService, vmrConversionComponent, new CapabilityStatementProvider());

        final Parameters request = Parameters.builder()
                .parameter(ParametersParameter.builder().name("knowledgeBase").valueCanonical("http://example.org/kb|1").build())
                .parameter(ParametersParameter.builder().name("assessmentDate").valueDate("2026-07-09").build())
                .build();
        final EvaluateAtSpecifiedTime evaluateAtSpecifiedTime = mock(EvaluateAtSpecifiedTime.class);
        final Parameters response = Parameters.builder()
                .parameter(ParametersParameter.builder().name("durationMs").valueInteger(1).build())
                .parameter(ParametersParameter.builder().name("engineVersion").valueString("ICE_TEST").build())
                .build();

        when(vmrConversionComponent.convertImmDsToEvaluateAtSpecifiedTime(request)).thenReturn(evaluateAtSpecifiedTime);
        when(evaluationService.evaluateAtSpecifiedTime(any(), any(), any())).thenReturn(null);
        when(vmrConversionComponent.convertImmDsToParametersResponse(any(), any(), any())).thenReturn(response);

        assertSame(response, controller.immDsForecast(request));
    }
}
