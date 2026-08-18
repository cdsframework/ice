package org.cdsframework.ice.cdsrr.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.LocalDate;
import java.util.List;

import org.cdsframework.ice.cdsrr.CdsRequestResponseConversionComponent;
import org.cdsframework.ice.cdsrr.dto.CdsRequest;
import org.cdsframework.ice.cdsrr.dto.Observation;
import org.junit.jupiter.api.Test;
import org.opencds.dss.evaluate.Evaluation;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.json.JsonMapper;

class CdsRequestControllerTest
{
    @Test
    void rejectsObservationWithoutDate()
    {
        final Evaluation evaluation = mock(Evaluation.class);
        final CdsRequestResponseConversionComponent conversionComponent = mock(CdsRequestResponseConversionComponent.class);
        final CdsRequestController controller = new CdsRequestController(evaluation, conversionComponent);
        final CdsRequest request = new CdsRequest(null, null, null, null, List.of(Observation.builder().build()));

        final ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> controller.evaluateAtSpecifiedTime(request));

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("observations[0] must provide valueDateTime or effectiveDateTime", exception.getReason());
        verifyNoInteractions(evaluation, conversionComponent);
    }

    @Test
    void mapsLegacyEffectiveDateTimeToValueDateTime() throws Exception
    {
        final Observation observation = JsonMapper.builder()
                .findAndAddModules()
                .build()
                .readValue("{\"effectiveDateTime\":\"2010-06-15\"}", Observation.class);

        assertEquals(LocalDate.of(2010, 6, 15), observation.valueDateTime());
    }
}
