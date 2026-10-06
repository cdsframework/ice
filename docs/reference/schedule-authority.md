# Schedule Authority Codes

**Applicability:** ICE SOAP/vMR API and ICE FHIR API (FHIR R6) recommendation output.

The vMR schedule authority code system is `2.16.840.1.113883.3.795.12.100.12`. FHIR R6 uses the schedule authority extension and
coding system defined in the [
`ice-schedule-authority` StructureDefinition](../../fhir/ice/StructureDefinition/ice-schedule-authority-structure-definition.json).

| Code       | Schedule authority                                                                        |
|------------|-------------------------------------------------------------------------------------------|
| `ACIP_CDC` | Advisory Committee on Immunization Practices / Centers for Disease Control and Prevention |
| `AAP`      | American Academy of Pediatrics                                                            |
| `AAFP`     | American Academy of Family Physicians                                                     |

The service-wide setting and SOAP/FHIR response examples are documented
in [Output Configuration](../configuration/output-configuration.md#vaccine-group-rules-artifact-and-schedule-authorities).
