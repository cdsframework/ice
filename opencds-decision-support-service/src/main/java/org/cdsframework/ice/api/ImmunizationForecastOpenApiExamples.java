package org.cdsframework.ice.api;

public final class ImmunizationForecastOpenApiExamples
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
                                                          "name": "module",
                                                          "valueCanonical": "http://cdsframework.org/PlanDefinition/ice-forecast|1.0.0"
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
                                                                  "display": "Influenza-LAIV3, IN, (2-49yrs)"
                                                                }
                                                              ],
                                                              "text": "Influenza-LAIV3, IN, (2-49yrs)"
                                                            },
                                                            "patient": {
                                                              "reference": "Patient/44081025"
                                                            },
                                                            "isSubpotent": true,
                                                            "subpotentReason": [
                                                              {
                                                                "coding": [
                                                                  {
                                                                    "system": "http://terminology.hl7.org/CodeSystem/immunization-subpotent-reason",
                                                                    "code": "coldchainbreak",
                                                                    "display": "Cold Chain Break"
                                                                  }
                                                                ]
                                                              }
                                                            ],
                                                            "lotNumber": "ABC1234",
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
                                                                  "system": "http://hl7.org/fhir/sid/icd-9-cm",
                                                                  "code": "052.9",
                                                                  "display": "Varicella"
                                                                }
                                                              ],
                                                              "text": "Varicella"
                                                            },
                                                            "valueCodeableConcept": {
                                                              "coding": [
                                                                {
                                                                  "system": "http://terminology.cdsframework.org/ice/recommendation-reason",
                                                                  "code": "DISEASE_DOCUMENTED",
                                                                  "display": "Disease Documented"
                                                                }
                                                              ],
                                                              "text": "Disease Documented"
                                                            },
                                                            "subject": {
                                                              "reference": "Patient/44081025"
                                                            },
                                                            "effectiveDateTime": "2016-01-01"
                                                          }
                                                        },
                                                        {
                                                          "name": "scheduleFlag",
                                                          "valueCode": "HEP_B_EXTRA_DOSE_INVALID"
                                                        },
                                                        {
                                                          "name": "scheduleFlag",
                                                          "valueCode": "POLIO_EXTRA_DOSE_INVALID"
                                                        }
                                                      ]
                                                    }
                                                    """;

    public static final String RESPONSE_SUCCESS = """
                                                  {
                                                    "resourceType": "Parameters",
                                                    "parameter": [
                                                      {
                                                        "name": "output",
                                                        "part": [
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
                                                              "occurrenceDateTime": "2026-04-04T16:20:00Z",
                                                              "moduleCanonical": "http://cdsframework.org/PlanDefinition/ice-forecast|1.0.0"
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
                                                                  },
                                                                  "extension": [
                                                                    {
                                                                      "url": "http://terminology.cdsframework.org/fhir/StructureDefinition/immunization-selection-context",
                                                                      "extension": [
                                                                        {
                                                                          "url": "selectedSeries",
                                                                          "valueCodeableConcept": {
                                                                            "coding": [
                                                                              {
                                                                                "system": "http://terminology.cdsframework.org/ice/series",
                                                                                "code": "INFLUENZA_SERIES",
                                                                                "display": "Influenza Vaccine Group"
                                                                              }
                                                                            ],
                                                                            "text": "Influenza Vaccine Group"
                                                                          }
                                                                        },
                                                                        {
                                                                          "url": "seriesSelectionType",
                                                                          "valueCodeableConcept": {
                                                                            "coding": [
                                                                              {
                                                                                "system": "http://terminology.cdsframework.org/ice/series-display-selection-type",
                                                                                "code": "SERIES_DISPLAY_UNAMBIGUOUS",
                                                                                "display": "Series Selected for Display"
                                                                              }
                                                                            ],
                                                                            "text": "Series Selected for Display"
                                                                          }
                                                                        }
                                                                      ]
                                                                    },
                                                                    {
                                                                      "url": "http://terminology.cdsframework.org/fhir/StructureDefinition/vaccine-group-rules-artifact",
                                                                      "valueRelatedArtifact": {
                                                                        "type": "documentation",
                                                                        "label": "ICE Influenza Vaccine Group Rules",
                                                                        "document": {
                                                                          "contentType": "text/html",
                                                                          "url": "https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/14352497/Influenza+Vaccine+Group"
                                                                        }
                                                                      }
                                                                    }
                                                                  ]
                                                                }
                                                              ]
                                                            }
                                                          }
                                                        ]
                                                      }
                                                    ]
                                                  }
                                                  """;

    public static final String RESPONSE_ERROR = """
                                                {
                                                  "resourceType": "Parameters",
                                                  "parameter": [
                                                    {
                                                      "name": "output",
                                                      "part": [
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
                                                                  "text": "Module canonical must be in '<canonical>|<version>' format"
                                                                }
                                                              }
                                                            ]
                                                          }
                                                        }
                                                      ]
                                                    }
                                                  ]
                                                }
                                                """;

    private ImmunizationForecastOpenApiExamples()
    {
    }
}
