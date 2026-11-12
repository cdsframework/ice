# Schedule Authority Codes

**Applicability:** ICE SOAP/vMR API and ICE FHIR API (FHIR R6) authority output.

The vMR schedule authority code system is `2.16.840.1.113883.3.795.12.100.12`.
FHIR output uses `ImmunizationRecommendation.recommendation.authority` (`0..*`), an array of `Reference` values pointing to
`Organization`s. Each logical identifier uses system `http://terminology.cdsframework.org/ice/schedule-authority` and the configured
authority code; its display contains the authority name.

When `output-schedule-authorities` is enabled, the FHIR converter reads authority observations associated with each recommendation
proposal. Distinct authorities appear together in the `authority` array. A forecast proposal produces one recommendation entry;
multiple authorities do not duplicate it. Disabled output or absent authority observations leave no authority populated.
Single-authority results still use an array.

SOAP/vMR payload construction and authority observations are unchanged.

This adopts the move proposed in [FHIR-57247](https://jira.hl7.org/browse/FHIR-57247)
and agreed in the Immunization Focus Group discussion dated October 1, 2026.
Recommendation authority uses `0..*` in the ICE contract.

**Client migration:** Iterate the native `authority` array on each recommendation entry. Multiple authorities remain on one
recommendation entry.

| Code       | Schedule authority                                                                        |
|------------|-------------------------------------------------------------------------------------------|
| `ACIP_CDC` | Advisory Committee on Immunization Practices / Centers for Disease Control and Prevention |
| `AAP`      | American Academy of Pediatrics                                                            |
| `AAFP`     | American Academy of Family Physicians                                                     |

The service-wide setting and SOAP/FHIR response examples are documented
in [Output Configuration](../configuration/output-configuration.md#vaccine-group-rules-artifact-and-schedule-authorities).
