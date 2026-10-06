# Evaluation and Recommendation Status Codes

This page documents ICE SOAP/vMR API evaluation validity and recommendation status codes. The ICE FHIR API (FHIR R6) uses separate
CodeSystems for evaluation status and recommendation status; the maintained artifacts are linked in their respective sections.

## Evaluation Validity

**Applicability:** ICE SOAP/vMR API evaluation output.

FHIR R6 returns evaluation status using the maintained [`evaluation-status.json`](../../fhir/ice/evaluation-status.json),
which has its own FHIR codes.

### SOAP/vMR Code System 2.16.840.1.113883.3.795.12.100.2

| **Code Value** | **Description**       |
|----------------|-----------------------|
| VALID          | Valid Immunization    |
| ACCEPTED       | Accepted Immunization |
| INVALID        | Invalid Immunization  |
| IGNORE         | Ignore Immunization   |
| NOT_EVALUATED  | Shot Not Evaluated    |

## Recommendation Status

**Applicability:** ICE SOAP/vMR API recommendation output.

FHIR R6 has a separate recommendation-status CodeSystem maintained in [
`recommendation-status.json`](../../fhir/ice/recommendation-status.json).

### SOAP/vMR Code System 2.16.840.1.113883.3.795.12.100.5

| **Code Value (Returned by ICE)** | **Description (Returned by ICE)**                                                    |
|----------------------------------|--------------------------------------------------------------------------------------|
| RECOMMENDED                      | Recommended                                                                          |
| CONDITIONAL                      | Conditionally Recommended                                                            |
| FUTURE_RECOMMENDED               | Recommended in the Future                                                            |
| NOT_RECOMMENDED                  | Not Recommended                                                                      |
| RECOMMENDATION_NOT_AVAILABLE     | Recommendation Not Available (*e.g.* - ICE did not forecast for unsupported vaccine) |
