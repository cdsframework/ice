package org.cdsframework.ice.api;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class SupportingDataOpenApiExamples
{
    public static final String KNOWLEDGE_MODULE_RESPONSE = """
                                                           {
                                                             "series": {
                                                               "VARICELLA_2_DOSE_SERIES": {
                                                                 "seriesId": "7a59daa316ccddbb9c84d97192b37394",
                                                                 "series": {
                                                                   "code": "VARICELLA_2_DOSE_SERIES",
                                                                   "codeSystem": "2.16.840.1.113883.3.795.12.100.10",
                                                                   "codeSystemName": "ICE Vaccine Series",
                                                                   "displayName": "Varicella 2-Dose Series"
                                                                 },
                                                                 "cdsVersion": {
                                                                   "1": "org.nyc.cir^ICE^1.0.0"
                                                                 },
                                                                 "numberOfDosesInSeries": 2,
                                                                 "vaccineGroup": {
                                                                   "1": {
                                                                     "code": "VARICELLA",
                                                                     "codeSystem": "2.16.840.1.113883.3.795.12.100.1",
                                                                     "codeSystemName": "ICE Vaccine Group",
                                                                     "displayName": "Varicella"
                                                                   }
                                                                 },
                                                                 "doses": {
                                                                   "1": {
                                                                     "absoluteMinimumAge": "1y-4d",
                                                                     "minimumAge": "1y",
                                                                     "earliestRecommendedAge": "1y",
                                                                     "latestRecommendedAge": "16m+4w",
                                                                     "doseVaccines": {
                                                                       "1": {
                                                                         "preferred": true,
                                                                         "vaccine": {
                                                                           "code": "21",
                                                                           "codeSystem": "2.16.840.1.113883.12.292",
                                                                           "codeSystemName": "CVX",
                                                                           "displayName": "varicella"
                                                                         }
                                                                       }
                                                                     }
                                                                   }
                                                                 }
                                                               }
                                                             },
                                                             "codeSystems": {
                                                               "SUPPORTED_VACCINES": {
                                                                 "name": "SUPPORTED_VACCINES",
                                                                 "identifier": [
                                                                   {
                                                                     "system": "urn:ietf:rfc:3986",
                                                                     "value": "urn:oid:2.16.840.1.113883.12.292"
                                                                   }
                                                                 ],
                                                                 "description": "Supported vaccines for display",
                                                                 "url": "http://hl7.org/fhir/sid/cvx",
                                                                 "title": "Vaccines (CVX)",
                                                                 "version": "org.nyc.cir^ICE^1.0.0",
                                                                 "status": "ACTIVE",
                                                                 "content": "COMPLETE",
                                                                 "property": [
                                                                   {
                                                                     "code": "conceptMapping",
                                                                     "type": "Coding"
                                                                   },
                                                                   {
                                                                     "code": "supported",
                                                                     "type": "boolean"
                                                                   }
                                                                 ],
                                                                 "concept": [
                                                                   {
                                                                     "code": "21",
                                                                     "display": "varicella",
                                                                     "property": [
                                                                       {
                                                                         "code": "conceptMapping",
                                                                         "valueCoding": {
                                                                           "code": "VACCINE_CVX_21",
                                                                           "display": "varicella"
                                                                         }
                                                                       },
                                                                       {
                                                                         "code": "supported",
                                                                         "valueBoolean": true
                                                                       }
                                                                     ]
                                                                   }
                                                                 ]
                                                               }
                                                             },
                                                             "outboundCodeSystemMap": {
                                                               "2.16.840.1.113883.6.96": "http://snomed.info/sct",
                                                               "2.16.840.1.113883.6.1": "http://loinc.org"
                                                             }
                                                           }""";

    public static final String MODULE_PLAN_DEFINITIONS_RESPONSE = """
                                                                  [
                                                                    {
                                                                      "id": "ice-forecast",
                                                                      "resourceType": "PlanDefinition",
                                                                      "url": "https://terminology.cdsframework.org/PlanDefinition/ice-forecast",
                                                                      "identifier": [
                                                                        {
                                                                          "system": "https://terminology.cdsframework.org/ice/identifiers/knowledge-bases",
                                                                          "value": "org.nyc.cir^ICE^1.0.0"
                                                                        }
                                                                      ],
                                                                      "version": "1.0.0",
                                                                      "versionAlgorithmString": "semver",
                                                                      "name": "IceForecast",
                                                                      "title": "ICE Immunization Forecast",
                                                                      "type": {
                                                                        "coding": [
                                                                          {
                                                                            "system": "http://terminology.hl7.org/CodeSystem/plan-definition-type",
                                                                            "code": "eca-rule",
                                                                            "display": "ECA Rule"
                                                                          }
                                                                        ],
                                                                        "text": "Clinical decision support rule"
                                                                      },
                                                                      "status": "active",
                                                                      "experimental": false,
                                                                      "date": "2026-04-22",
                                                                      "publisher": "CDS Framework",
                                                                      "contact": [
                                                                        {
                                                                          "name": "CDS Framework"
                                                                        }
                                                                      ],
                                                                      "description": "PlanDefinition representing the ICE immunization forecasting module used by the CDS Framework FHIR immunization forecast operation.",
                                                                      "purpose": "Provides the canonical knowledge base identity used for ICE immunization forecasting results.",
                                                                      "library": [
                                                                        "https://terminology.cdsframework.org/ice/Library/ice-forecast-logic|1.0.0"
                                                                      ]
                                                                    }
                                                                  ]""";

    public static final String CODE_SYSTEM_RESPONSE = """
                                                      {
                                                        "name": "SUPPORTED_VACCINES",
                                                        "identifier": [
                                                          {
                                                            "system": "urn:ietf:rfc:3986",
                                                            "value": "urn:oid:2.16.840.1.113883.12.292"
                                                          }
                                                        ],
                                                        "description": "Supported vaccines for display",
                                                        "url": "http://hl7.org/fhir/sid/cvx",
                                                        "title": "Vaccines (CVX)",
                                                        "version": "org.nyc.cir^ICE^1.0.0",
                                                        "status": "ACTIVE",
                                                        "content": "COMPLETE",
                                                        "property": [
                                                          {
                                                            "code": "conceptMapping",
                                                            "type": "Coding"
                                                          },
                                                          {
                                                            "code": "supported",
                                                            "type": "boolean"
                                                          }
                                                        ],
                                                        "concept": [
                                                          {
                                                            "code": "21",
                                                            "display": "varicella",
                                                            "property": [
                                                              {
                                                                "code": "conceptMapping",
                                                                "valueCoding": {
                                                                  "code": "VACCINE_CVX_21",
                                                                  "display": "varicella"
                                                                }
                                                              },
                                                              {
                                                                "code": "supported",
                                                                "valueBoolean": true
                                                              }
                                                            ]
                                                          }
                                                        ]
                                                      }""";

    public static final String SERIES_PLAN_DEFINITIONS_RESPONSE = """
                                                                              {
                                                                                "VARICELLA_2_DOSE_SERIES": {
                                                                                  "resourceType": "PlanDefinition",
                                                                                  "url": "https://terminology.cdsframework.org/ice/PlanDefinition/series/varicella_2_dose_series",
                                                                                  "version": "1.0.0",
                                                                                  "name": "VARICELLA_2_DOSE_SERIES",
                                                                                  "title": "Varicella 2-Dose Series",
                                                                                  "status": "active",
                                                                                  "meta": {
                                                                                    "profile": [
                                                                                      "https://terminology.cdsframework.org/ice/StructureDefinition/ice-series-plan-definition"
                                                                                    ]
                                                                                  },
                                                                                  "action": [
                                                                                    {
                                                                                      "id": "dose-1",
                                                                                      "title": "Dose 1"
                                                                                    },
                                                                                    {
                                                                                      "id": "dose-2",
                                                                                      "title": "Dose 2"
                                                                                    }
                                                                                  ]
                                                                    }
                                                                  }""";
}
