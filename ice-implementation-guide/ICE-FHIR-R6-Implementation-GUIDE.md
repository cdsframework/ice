# ICE FHIR R6 Implementation Guide

## Release 2.58.1 — FHIR Partner Update

ICE 2.58.1 introduces a proposed FHIR R6 ImmDS forecast operation. The operation is experimental and is available only when
`cds-engine.experimental-features.enable-fhir-r6=true` is enabled.

### Migration required for existing FHIR forecast clients

The prior experimental FHIR forecast contract has changed:

| Previous contract                                            | ICE 2.58.1 contract                                                       |
|--------------------------------------------------------------|---------------------------------------------------------------------------|
| `POST /cds/$immunization-forecast`                           | `POST /cds/$immds-forecast`                                               |
| Required `module` parameter                                  | Required `knowledgeBase` parameter                                        |
| `http://cdsframework.org/PlanDefinition/ice-forecast\|1.0.0` | `https://terminology.cdsframework.org/PlanDefinition/ice-forecast\|1.0.0` |
| Response values nested in `output.part[]`                    | Response values are top-level `Parameters.parameter` entries              |

Partners should update clients to use the new endpoint, parameter name, HTTPS canonical URLs, and top-level response parameters. The
service publishes its FHIR capability statement at `GET /cds/metadata`.

### New R6 capabilities and integration considerations

- Requests are FHIR `Parameters` resources with required `patient`, `assessmentDate`, and `knowledgeBase` inputs; optional repeating
  `immunization`, contextual-condition `observation`, and `scheduleFlag` inputs are supported.
- The operation accepts `application/fhir+json` and `application/json`.
- `Immunization.isSubpotent = true` is treated as an invalid administration during forecast evaluation.
- Contextual-condition observations use `Observation.code` and `valueDateTime`. Unsupported codes are omitted from the forecast
  input and returned as an `OperationOutcome` warning rather than rejecting the request.
- Optional extensions can provide series-selection context, vaccine-group rule artifacts, and schedule authorities. These outputs
  are configuration-controlled and disabled by default.
- Consume structured FHIR fields and extensions rather than narrative text. `ImmunizationRecommendation.date` equals the submitted
  `assessmentDate`; runtime timestamps and `durationMs` are not clinical timing values.

ICE 2.58.1 also includes current ICE clinical-content updates, including DTP/Tdap, pneumococcal, meningococcal B, zoster, and
schedule-authority behavior. Forecast results may therefore change independently of this API migration.

## Contextual Conditions

The `$immds-forecast` operation accepts a contextual condition as a repeating `observation` input parameter. A contextual condition
conveys disease history or evidence of immunity that affects forecasting; it is not sent as a legacy direct vMR observation.

Each contextual-condition `Observation` SHALL include:

- `resourceType: "Observation"`
- `status: "final"`
- `code.coding` containing one supported code from `http://ivci.org/CodeSystem/contextual-condition`
- `valueDateTime`, the date on which the condition was documented, verified, or established

ICE translates a supported IVCI code to the disease and disease-immunity-source concepts shown below. Clients do not provide the
translated disease code or `DISEASE_DOCUMENTED`/`PROOF_OF_IMMUNITY` source in `Observation.valueCodeableConcept`; the configured
contextual-condition mapping supplies those values.

| IVCI code | Contextual condition                                                   | ICE disease mapping                            | ICE immunity source                       |
|-----------|------------------------------------------------------------------------|------------------------------------------------|-------------------------------------------|
| `018`     | Laboratory Evidence of Immunity or confirmation of Hepatitis A disease | SNOMED CT `40468003` — Viral hepatitis, type A | `DISEASE_DOCUMENTED` — Disease Documented |
| `019`     | Laboratory Evidence of Immunity or confirmation of Hepatitis B disease | SNOMED CT `66071002` — Type B viral hepatitis  | `DISEASE_DOCUMENTED` — Disease Documented |
| `020`     | Laboratory Evidence of Immunity for Measles                            | SNOMED CT `14189004` — Measles                 | `DISEASE_DOCUMENTED` — Disease Documented |
| `021`     | Laboratory Evidence of Immunity for Mumps                              | SNOMED CT `36989005` — Mumps                   | `DISEASE_DOCUMENTED` — Disease Documented |
| `022`     | Laboratory Evidence of Immunity for Rubella                            | SNOMED CT `36653000` — Rubella                 | `DISEASE_DOCUMENTED` — Disease Documented |
| `023`     | Laboratory Evidence of Immunity or confirmation of Varicella disease   | SNOMED CT `38907003` — Varicella               | `DISEASE_DOCUMENTED` — Disease Documented |
| `024`     | Healthcare provider verified history of or diagnosis of Varicella      | SNOMED CT `38907003` — Varicella               | `PROOF_OF_IMMUNITY` — Proof of Immunity   |

The disease codes use `http://snomed.info/sct`. The source codes use
`https://terminology.cdsframework.org/ice/disease-immunity-source`.

### Request example

The following `observation` parameter records provider-verified varicella history. ICE translates contextual-condition code
`024` to Varicella with the `PROOF_OF_IMMUNITY` source before evaluating the forecast.

```json
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
      ]
    },
    "valueDateTime": "2016-01-01"
  }
}
```

If an observation does not use a configured IVCI contextual-condition code, ICE omits it from the IMMDS forecast input and returns
an `OperationOutcome` warning.

## Scope

This guide documents the implemented FHIR R6 contract for ICE forecasting in this repository, including:

- Operation contract for `immunization-forecast`
- Output envelope and parameter semantics
- ICE-specific extension definitions and runtime behavior
- Configuration switches that affect FHIR output

This guide reflects the current implementation and JSON artifacts in the `fhir/` directory.

## Canonical Artifacts (`/fhir`)

| File                                                          | Resource Type       | Canonical URL                                                                                 | Purpose                                                |
|---------------------------------------------------------------|---------------------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------|
| `ice/OperationDefinition/patient-immds-forecast.json`         | OperationDefinition | `https://terminology.cdsframework.org/ice/OperationDefinition/patient-immds-forecast`         | Request/response contract for `$immunization-forecast` |
| `ice/StructureDefinition/immunization-selection-context.json` | StructureDefinition | `https://terminology.cdsframework.org/ice/StructureDefinition/immunization-selection-context` | Selection context extension                            |
| `ice/StructureDefinition/vaccine-group-rules-artifact.json`   | StructureDefinition | `https://terminology.cdsframework.org/ice/StructureDefinition/vaccine-group-rules-artifact`   | Vaccine group rules artifact extension                 |
| `PlanDefinition/ice-forecast.json`                            | PlanDefinition      | `https://terminology.cdsframework.org/PlanDefinition/ice-forecast`                            | ICE module definition                                  |
| `ice/PlanDefinition/common-ice-forecast.json`                 | PlanDefinition      | `https://terminology.cdsframework.org/ice/PlanDefinition/common-ice-forecast`                 | Common module definition                               |
| `ice/StructureDefinition/ice-series-plan-definition.json`     | StructureDefinition | `https://terminology.cdsframework.org/ice/StructureDefinition/ice-series-plan-definition`     | PlanDefinition profile for series content              |
| `ice/StructureDefinition/ice-series-metadata.json`            | StructureDefinition | `https://terminology.cdsframework.org/ice/StructureDefinition/ice-series-metadata`            | Series metadata extension                              |
| `ice/StructureDefinition/ice-dose-number.json`                | StructureDefinition | `https://terminology.cdsframework.org/ice/StructureDefinition/ice-dose-number`                | Dose number extension                                  |
| `ice/StructureDefinition/ice-dose-age-constraint.json`        | StructureDefinition | `https://terminology.cdsframework.org/ice/StructureDefinition/ice-dose-age-constraint`        | Dose age constraint extension                          |
| `ice/StructureDefinition/ice-dose-interval-constraint.json`   | StructureDefinition | `https://terminology.cdsframework.org/ice/StructureDefinition/ice-dose-interval-constraint`   | Dose interval constraint extension                     |
| `ice/StructureDefinition/ice-dose-vaccine.json`               | StructureDefinition | `https://terminology.cdsframework.org/ice/StructureDefinition/ice-dose-vaccine`               | Dose vaccine extension                                 |
| `Bundle/ice-custom-code-systems.json`                         | Bundle              | n/a                                                                                           | Custom code systems/value sets used by ICE             |

## Operation Contract

### Endpoint

- Operation code: `immunization-forecast`
- Transport in service docs: `POST /cds/$immunization-forecast`
- Body: FHIR `Parameters`

### Request Parameters (in)

- `patient` (required, `Patient`, max 1)
- `assessmentDate` (required, `date`, max 1)
- `module` (required, `canonical`, max 1)
- `immunization` (optional, `Immunization`, repeating)
- `observation` (optional, `Observation`, repeating)
- `parameters` (optional, nested `Parameters`, max 1)

### Response Parameters (out)

Response is a `Parameters` resource with a single top-level output container:

- `parameter[0].name = "output"`
- `parameter[0].part[]` contains:
    - `durationMs` (`integer`, 0..1)
    - `engineVersion` (`string`, 1..1)
    - `guidanceResponse` (`GuidanceResponse`, 0..1)
    - `evaluation` (`ImmunizationEvaluation`, 0..*)
    - `recommendation` (`ImmunizationRecommendation`, 0..*)
    - `operationOutcome` (`OperationOutcome`, 0..1)

Notes:

- Error responses include `durationMs`, `engineVersion`, and `operationOutcome` and may omit `guidanceResponse`.
- `assessmentDate` is not emitted as an output parameter.

## Timing Semantics

| Field                                           | Meaning                                                   | Source                 | Notes                                                   |
|-------------------------------------------------|-----------------------------------------------------------|------------------------|---------------------------------------------------------|
| `Parameters.parameter[output].part[durationMs]` | Total processing time for the ICE request in milliseconds | Service runtime        | Technical metric; not clinically relevant               |
| `GuidanceResponse.occurrenceDateTime`           | Timestamp when the forecast/guidance was generated        | ICE engine             | Represents when decision support logic executed         |
| `ImmunizationRecommendation.date`               | Clinical evaluation date used for recommendations         | Input (assessmentDate) | Drives due/overdue calculations; independent of runtime |

Additional clarifications:

- `durationMs` measures how long the service ran, not when clinical logic applies.
- `occurrenceDateTime` indicates when the forecast was produced.
- `ImmunizationRecommendation.date` reflects the clinical evaluation context.

Normative Behavior:

- `ImmunizationRecommendation.date` SHALL equal the request `assessmentDate`.

Implementation Implications:

- The forecast may be generated at a different time (`occurrenceDateTime`) than the clinical evaluation date.
- Consumers MUST NOT use `durationMs` or `occurrenceDateTime` to infer clinical timing.

## ICE Extensions

### 1) Immunization Selection Context

- Canonical URL: `https://terminology.cdsframework.org/ice/StructureDefinition/immunization-selection-context`
- Allowed on:
    - `ImmunizationEvaluation`
    - `ImmunizationRecommendation.recommendation`
- Shape:
    - Parent extension with nested slices (2..3 total):
        - `selectedSeries` (`CodeableConcept`, required)
        - `seriesSelectionType` (`CodeableConcept`, required)
        - `selectedSeason` (`CodeableConcept`, optional)
- Implementation guarantee:
    - Extension is emitted only when series selection context is present.
    - A `selectedSeason`-only extension is not emitted.

### 2) Vaccine Group Rules Artifact

- Canonical URL: `https://terminology.cdsframework.org/ice/StructureDefinition/vaccine-group-rules-artifact`
- Allowed on:
    - `ImmunizationEvaluation`
    - `ImmunizationRecommendation.recommendation`
- Value type:
    - `valueRelatedArtifact` with:
        - `type = "documentation"`
        - `label` (runtime label; format: `ICE %s Rules` using vaccine-group display name when available)
        - `display` is not currently emitted; consumers should use `label`
        - `document.contentType = "text/html"`
        - `document.url` = rules URL
- Important:
    - `RelatedArtifact.url` is intentionally omitted to avoid duplicate URL representation.

## Runtime Mapping Rules

- `targetDisease` can include multiple codings (for component/multi-disease contexts) under a single `CodeableConcept`.
- `seriesDoses.text = "0"` is suppressed and not emitted in FHIR output.
- Narrative and description fields are human-readable convenience text only.
    - Consumers should rely on structured fields and extensions, not text parsing.

## Immunization Subpotent Support

The FHIR `Immunization` input model supports the R6 subpotent fields:

- `Immunization.isSubpotent` (`boolean`)
- `Immunization.subpotentReason` (`CodeableConcept[]`)

Runtime mapping behavior:

- When `Immunization.isSubpotent = true`, the vMR input sets
  `SubstanceAdministrationEvent.isValid` to `false`.
- If `isSubpotent` is absent or `false`, no invalidity override is applied by this mapping.
- `subpotentReason` is accepted in the FHIR payload for interoperability with R6 content and code validation use cases.

Supported terminology:

- CodeSystem URL:
  `http://terminology.hl7.org/CodeSystem/immunization-subpotent-reason`
- OID:
  `2.16.840.1.113883.4.642.1.1098`
- Supporting data source in this repository:
  `opencds-decision-support-service/src/main/resources/data/knowledgeModule/org.nyc.cir.ice/ice-supporting-data/supportedImmunizationSubpotentReasons.yml`

## Configuration Controls

Key property controlling rules-artifact output:

- YAML key: `output-vaccine-group-rules-artifact`
- Java property: `outputVaccineGroupRulesArtifact`
- Drools global: `outputVaccineGroupRulesArtifact`

## Conformance Notes

- This implementation uses the local FHIR R6 model classes under
  `opencds-decision-support-core/src/main/java/org/cdsframework/fhir`.
- Current spec artifacts declare `fhirVersion` as `6.0.0-ballot4`.
- OpenAPI examples in service code are aligned to the `output.part[]` response envelope and current extension URLs.
