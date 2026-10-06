# Contextual Conditions

**Applicability:** ICE FHIR API (FHIR R6) only.

This reference lists the IVCI contextual condition codes supported by the API and the corresponding ICE disease and
disease-immunity-source mappings. These codes are available as of ICE 2.59.1.

The `$immds-forecast` operation accepts these as repeating `observation` input parameters. Each contextual condition `Observation`
must include `resourceType: "Observation"`, `status: "final"`, a `code.coding` with one of the supported codes below, and
`valueDateTime` for the date the condition was documented, verified, or established. Use the IVCI code in the request; do not send
the mapped ICE disease or immunity source value in its place.

| IVCI code | Contextual condition                                                   | ICE disease mapping                            | ICE immunity source                       |
|-----------|------------------------------------------------------------------------|------------------------------------------------|-------------------------------------------|
| `018`     | Laboratory Evidence of Immunity or confirmation of Hepatitis A disease | SNOMED CT `40468003` — Viral hepatitis, type A | `PROOF_OF_IMMUNITY` — Proof of Immunity   |
| `019`     | Laboratory Evidence of Immunity or confirmation of Hepatitis B disease | SNOMED CT `66071002` — Type B viral hepatitis  | `PROOF_OF_IMMUNITY` — Proof of Immunity   |
| `020`     | Laboratory Evidence of Immunity for Measles                            | SNOMED CT `14189004` — Measles                 | `PROOF_OF_IMMUNITY` — Proof of Immunity   |
| `021`     | Laboratory Evidence of Immunity for Mumps                              | SNOMED CT `36989005` — Mumps                   | `PROOF_OF_IMMUNITY` — Proof of Immunity   |
| `022`     | Laboratory Evidence of Immunity for Rubella                            | SNOMED CT `36653000` — Rubella                 | `PROOF_OF_IMMUNITY` — Proof of Immunity   |
| `023`     | Laboratory Evidence of Immunity or confirmation of Varicella disease   | SNOMED CT `38907003` — Varicella               | `PROOF_OF_IMMUNITY` — Proof of Immunity   |
| `024`     | Healthcare provider verified history of or diagnosis of Varicella      | SNOMED CT `38907003` — Varicella               | `DISEASE_DOCUMENTED` — Disease Documented |

The IVCI coding system is `http://ivci.org/CodeSystem/contextual-condition`. Disease codes use `http://snomed.info/sct`; source
codes use `https://terminology.cdsframework.org/ice/disease-immunity-source`.

If an observation does not use a configured IVCI contextual-condition code, ICE omits it from the IMMDS forecast input and returns
an `OperationOutcome` warning. For the broader operation contract, see the
[FHIR R6 Implementation Guide](../implementation-guides/ICE-FHIR-R6-Implementation-Guide.md).
