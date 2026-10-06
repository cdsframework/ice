# Administrative Gender Codes

**Applicability:** ICE SOAP/vMR API gender table; the ICE FHIR API (FHIR R6) uses the HL7 Administrative Gender CodeSystem.

The following values are used for the vMR `gender` element. `UN` suppresses ICE gender-specific logic. For the FHIR R6 CodeSystem
artifact, see [v3-AdministrativeGender](../../fhir/terminology.hl7.org/CodeSystem/v3-AdministrativeGender.json).

## HL7 Administrative Gender - Code System 2.16.840.1.113883.5.1

| **Code Value** | **Description**                                                                   |
|----------------|-----------------------------------------------------------------------------------|
| F              | Female                                                                            |
| M              | Male                                                                              |
| UN             | Undifferentiated (results in any gender-specific logic in ICE to not be executed) |
