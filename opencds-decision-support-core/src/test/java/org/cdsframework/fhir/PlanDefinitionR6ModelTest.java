package org.cdsframework.fhir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class PlanDefinitionR6ModelTest
{
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesActionBasedSeriesPlanDefinitionFields() throws Exception
    {
        final String json = """
                            {
                              "resourceType": "PlanDefinition",
                              "id": "varicella-2-dose-series",
                              "meta": {
                                "profile": [
                                  "https://terminology.cdsframework.org/ice/StructureDefinition/ice-series-plan-definition"
                                ]
                              },
                              "url": "https://terminology.cdsframework.org/ice/PlanDefinition/series/VARICELLA_2_DOSE_SERIES",
                              "version": "1.0.0",
                              "status": "active",
                              "action": [
                                {
                                  "id": "dose-1",
                                  "title": "Dose 1",
                                  "extension": [
                                    {
                                      "url": "https://terminology.cdsframework.org/ice/StructureDefinition/ice-dose-number",
                                      "valueInteger": 1
                                    }
                                  ]
                                },
                                {
                                  "id": "dose-2",
                                  "title": "Dose 2",
                                  "relatedAction": [
                                    {
                                      "actionId": "dose-1",
                                      "relationship": "after-end",
                                      "extension": [
                                        {
                                          "url": "https://terminology.cdsframework.org/ice/StructureDefinition/ice-dose-interval-constraint",
                                          "extension": [
                                            {
                                              "url": "minimumInterval",
                                              "valueString": "84d"
                                            }
                                          ]
                                        }
                                      ]
                                    },
                                    {
                                      "targetId": "dose-1",
                                      "relationship": "after-start"
                                    }
                                  ],
                                  "extension": [
                                    {
                                      "url": "https://terminology.cdsframework.org/ice/StructureDefinition/ice-dose-number",
                                      "valueInteger": 2
                                    }
                                  ]
                                }
                              ]
                            }
                            """;

        final PlanDefinition planDefinition = objectMapper.readValue(json, PlanDefinition.class);

        assertEquals("PlanDefinition", planDefinition.resourceType());
        assertNotNull(planDefinition.meta());
        assertEquals("https://terminology.cdsframework.org/ice/StructureDefinition/ice-series-plan-definition",
                planDefinition.meta().profile().getFirst());
        assertEquals(2, planDefinition.action().size());
        assertEquals("dose-1", planDefinition.action().getFirst().id());
        assertEquals(Integer.valueOf(1), planDefinition.action().getFirst().extension().getFirst().valueInteger());
        assertEquals("dose-1", planDefinition.action().get(1).relatedAction().getFirst().actionId());
        assertEquals("84d", planDefinition.action()
                .get(1)
                .relatedAction()
                .getFirst()
                .extension()
                .getFirst()
                .extension()
                .getFirst()
                .valueString());
        assertEquals("dose-1", planDefinition.action().get(1).relatedAction().get(1).targetId());
        assertEquals(Integer.valueOf(2), planDefinition.action().get(1).extension().getFirst().valueInteger());
    }
}
