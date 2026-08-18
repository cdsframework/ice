package org.cdsframework.ice.api;

public final class ImmDsForecastOpenApiExamples
{
    public static final String REQUEST_PARAMETERS = """
                                                    {
                                                      "resourceType": "Parameters",
                                                      "parameter": [
                                                        {
                                                          "name": "patient",
                                                          "resource": {
                                                            "resourceType": "Patient",
                                                            "identifier": [
                                                              {
                                                                "system": "http://nyc.gov/cir/identifier/patient-id",
                                                                "value": "44081025"
                                                              }
                                                            ],
                                                            "birthDate": "2010-12-01",
                                                            "gender": "female"
                                                          }
                                                        },
                                                        {
                                                          "name": "assessmentDate",
                                                          "valueDate": "2026-04-04"
                                                        },
                                                        {
                                                          "name": "knowledgeBase",
                                                          "valueCanonical": "https://terminology.cdsframework.org/PlanDefinition/ice-forecast|1.0.0"
                                                        },
                                                        {
                                                          "name": "immunization",
                                                          "resource": {
                                                            "resourceType": "Immunization",
                                                            "identifier": [
                                                              {
                                                                "system": "http://nyc.gov/cir/identifier/immunization-id",
                                                                "value": "587974"
                                                              }
                                                            ],
                                                            "vaccineCode": {
                                                              "coding": [
                                                                {
                                                                  "system": "http://hl7.org/fhir/sid/cvx",
                                                                  "code": "111",
                                                                  "display": "Influenza, live, trivalent, intranasal, PF"
                                                                }
                                                              ],
                                                              "text": "Influenza, live, trivalent, intranasal, PF"
                                                            },
                                                            "patient": {
                                                              "reference": "Patient/44081025"
                                                            },
                                                            "occurrenceDateTime": "2016-02-02"
                                                          }
                                                        },
                                                        {
                                                          "name": "observation",
                                                          "resource": {
                                                            "resourceType": "Observation",
                                                            "status": "final",
                                                            "code": {
                                                              "coding": [
                                                                {
                                                                  "system": "http://ivci.org/CodeSystem/contextual-condition",
                                                                  "code": "024",
                                                                  "display": "Healthcare provider verified history of or diagnosis of Varicella"
                                                                }
                                                              ],
                                                              "text": "Healthcare provider verified history of or diagnosis of Varicella"
                                                            },
                                                            "valueDateTime": "2016-01-01"
                                                          }
                                                        }
                                                      ]
                                                    }
                                                    """;

    public static final String RESPONSE_SUCCESS = """
                                                  {
                                                    "resourceType": "Parameters",
                                                    "parameter": [
                                                      {
                                                        "name": "durationMs",
                                                        "valueInteger": 187
                                                      },
                                                      {
                                                        "name": "engineVersion",
                                                        "valueString": "ICE_2.55.1"
                                                      },
                                                      {
                                                        "name": "guidanceResponse",
                                                        "resource": {
                                                          "resourceType": "GuidanceResponse",
                                                          "status": "success",
                                                          "subject": {
                                                            "reference": "Patient/44081025"
                                                          },
                                                          "occurrenceDateTime": "2026-04-04T16:20:00Z"
                                                        }
                                                      },
                                                      {
                                                        "name": "evaluation",
                                                        "resource": {
                                                          "resourceType": "ImmunizationEvaluation",
                                                          "status": "completed"
                                                        }
                                                      },
                                                      {
                                                        "name": "recommendation",
                                                        "resource": {
                                                          "resourceType": "ImmunizationRecommendation",
                                                          "recommendation": [
                                                            {
                                                              "series": "Influenza Vaccine Group",
                                                              "seriesDoses": {
                                                                "text": "2"
                                                              }
                                                            }
                                                          ]
                                                        }
                                                      },
                                                      {
                                                        "name": "operationOutcome",
                                                        "resource": {
                                                          "resourceType": "OperationOutcome",
                                                          "issue": [
                                                            {
                                                              "severity": "information",
                                                              "code": "informational"
                                                            }
                                                          ]
                                                        }
                                                      }
                                                    ]
                                                  }
                                                  """;

    public static final String RESPONSE_ERROR = """
                                                {
                                                  "resourceType": "Parameters",
                                                  "parameter": [
                                                    {
                                                      "name": "durationMs",
                                                      "valueInteger": 4
                                                    },
                                                    {
                                                      "name": "engineVersion",
                                                      "valueString": "ICE_2.55.1"
                                                    },
                                                    {
                                                      "name": "operationOutcome",
                                                      "resource": {
                                                        "resourceType": "OperationOutcome",
                                                        "issue": [
                                                          {
                                                            "severity": "error",
                                                            "code": "exception",
                                                            "details": {
                                                              "text": "Parameters must contain knowledgeBase valueCanonical"
                                                            }
                                                          }
                                                        ]
                                                      }
                                                    }
                                                  ]
                                                }
                                                """;

    private ImmDsForecastOpenApiExamples()
    {
    }
}
