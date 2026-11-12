# ICE FHIR R6 Implementation Guide

ICE provides the proposed ImmDS forecast operation through the ICE FHIR API (FHIR R6). The operation is experimental and is
available only when `cds-engine.experimental-features.enable-fhir-r6=true` is enabled.

## Upgrading to ICE 2.61.1

FHIR clients upgrading from 2.60.1 should review their response parsing and output configuration:

- **Series and season:** Evaluation and recommendation `series` and optional `season` are native `CodeableConcept` values.
  The `immunization-selection-context` extension carries the series selection type.
- **Dose counts:** `seriesDoses` is the size of the selected series. On recommendations, `seriesDosesRemaining` is the
  remaining count. Update clients that relied on the previous representation.
- **Schedule authorities:** When enabled, authorities use native `authority` arrays of `Organization` references on recommendation
  entries, replacing the previous custom schedule-authority extension.
- **Target diseases:** `targetDisease` is an array of disease concepts. Clients must handle multiple diseases, including for
  combination vaccines.
- **Output configuration:** Replace the removed `output-series-information` setting with
  `output-series-information-for-evaluations` and `output-series-information-for-recommendations`. Doses-remaining output
  requires recommendation series information. See [Output Configuration](../configuration/output-configuration.md#series-information-and-doses-remaining).
- **Warnings:** `OperationOutcome` issue classification and warning detection have changed. Test clients that inspect issue
  codes or severity.

Review the [release changelog](../release-changelog.md) and test representative requests and responses before upgrading.

## Upgrading from the pre-2.58.1 FHIR interface

ICE 2.58.1 introduced the proposed ImmDS forecast operation and changed the earlier experimental FHIR forecast contract:

| Previous contract                                            | ICE 2.58.1 contract                                                       |
|--------------------------------------------------------------|---------------------------------------------------------------------------|
| `POST /cds/$immunization-forecast`                           | `POST /cds/$immds-forecast`                                               |
| Required `module` parameter                                  | Required `knowledgeBase` parameter                                        |
| `http://cdsframework.org/PlanDefinition/ice-forecast\|1.0.0` | `https://terminology.cdsframework.org/PlanDefinition/ice-forecast\|1.0.0` |
| Response values nested in `output.part[]`                    | Response values are top-level `Parameters.parameter` entries              |

Partners should update clients to use the new endpoint, parameter name, HTTPS canonical URLs, and top-level response parameters. The
service publishes its FHIR capability statement at `GET /cds/metadata`.

## FHIR integration considerations

- Requests are FHIR `Parameters` resources with required `patient`, `assessmentDate`, and `knowledgeBase` inputs; optional repeating
  `immunization`, contextual-condition `observation`, and `scheduleFlag` inputs are supported.
- The operation accepts `application/fhir+json` and `application/json`.
- `Immunization.isSubpotent = true` maps to vMR `isValid=false`. When the dose override feature is enabled, ICE uses that input
  to treat the administration as invalid. See [Dose override feature](../configuration/runtime-configuration.md#dose-override-feature).
- Contextual-condition observations use `Observation.code` and `valueDateTime`. Unsupported codes are omitted from the forecast
  input and returned as an `OperationOutcome` warning rather than rejecting the request.
- Optional extensions can provide series-selection context and vaccine-group rule artifacts. Schedule authorities use the native
  `ImmunizationRecommendation.recommendation.authority` array (`0..*`). These outputs are configuration-controlled and disabled by
  default.
- Consume structured FHIR fields and extensions rather than narrative text. `ImmunizationRecommendation.date` equals the submitted
  `assessmentDate`; runtime timestamps and `durationMs` are not clinical timing values.

Clinical-content changes may affect forecast results independently of FHIR interface changes. See the
[release changelog](../release-changelog.md) for the changes in each release.

## Contextual Conditions

For supported codes, ICE mappings, the Observation requirements, and request behavior, see
[Contextual Conditions](../reference/contextual-conditions.md).

Each contextual-condition `Observation` SHALL include:

- `resourceType: "Observation"`
- `status: "final"`
- `code.coding` containing one supported code from `https://ivci.org/contextual-conditions/usa/cdc-cdsi`
- `valueDateTime`, the date on which the condition was documented, verified, or established

ICE translates a supported IVCI code to the disease and disease-immunity-source concepts shown below. Clients do not provide the
translated disease code or `DISEASE_DOCUMENTED`/`PROOF_OF_IMMUNITY` source in `Observation.valueCodeableConcept`; the configured
contextual-condition mapping supplies those values.

| IVCI code | Contextual condition                                                   | ICE disease mapping                            | ICE immunity source                       |
|-----------|------------------------------------------------------------------------|------------------------------------------------|-------------------------------------------|
| `018`     | Laboratory Evidence of Immunity or confirmation of Hepatitis A disease | SNOMED CT `40468003` — Viral hepatitis, type A | `PROOF_OF_IMMUNITY` — Proof of Immunity   |
| `019`     | Laboratory Evidence of Immunity or confirmation of Hepatitis B disease | SNOMED CT `66071002` — Type B viral hepatitis  | `PROOF_OF_IMMUNITY` — Proof of Immunity   |
| `020`     | Laboratory Evidence of Immunity for Measles                            | SNOMED CT `14189004` — Measles                 | `PROOF_OF_IMMUNITY` — Proof of Immunity   |
| `021`     | Laboratory Evidence of Immunity for Mumps                              | SNOMED CT `36989005` — Mumps                   | `PROOF_OF_IMMUNITY` — Proof of Immunity   |
| `022`     | Laboratory Evidence of Immunity for Rubella                            | SNOMED CT `36653000` — Rubella                 | `PROOF_OF_IMMUNITY` — Proof of Immunity   |
| `023`     | Laboratory Evidence of Immunity or confirmation of Varicella disease   | SNOMED CT `38907003` — Varicella               | `PROOF_OF_IMMUNITY` — Proof of Immunity   |
| `024`     | Healthcare provider verified history of or diagnosis of Varicella      | SNOMED CT `38907003` — Varicella               | `DISEASE_DOCUMENTED` — Disease Documented |

The disease codes use `http://snomed.info/sct`. The source codes use
`https://terminology.cdsframework.org/ice/disease-immunity-source`.

### Request example

The following `observation` parameter records provider-verified varicella history. ICE translates contextual-condition code
`024` to Varicella with the `DISEASE_DOCUMENTED` source before evaluating the forecast.

```json
{
  "name": "observation",
  "resource": {
    "resourceType": "Observation",
    "status": "final",
    "code": {
      "coding": [
        {
          "system": "https://ivci.org/contextual-conditions/usa/cdc-cdsi",
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

- Operation contract for `immds-forecast`
- Output envelope and parameter semantics
- ICE-specific extension definitions and runtime behavior
- Configuration switches that affect FHIR output

This guide reflects the current implementation and JSON artifacts in the `../../fhir` directory.

## Canonical Artifacts (`/fhir`)

| File                                                          | Resource Type       | Canonical URL                                                                                 | Purpose                                         |
|---------------------------------------------------------------|---------------------|-----------------------------------------------------------------------------------------------|-------------------------------------------------|
| `ice/OperationDefinition/immds-forecast.json`                 | OperationDefinition | `https://terminology.cdsframework.org/ice/OperationDefinition/immds-forecast`                 | Request/response contract for `$immds-forecast` |
| `ice/StructureDefinition/immunization-selection-context.json` | StructureDefinition | `https://terminology.cdsframework.org/ice/StructureDefinition/immunization-selection-context` | Selection context extension                     |
| `ice/StructureDefinition/vaccine-group-rules-artifact.json`   | StructureDefinition | `https://terminology.cdsframework.org/ice/StructureDefinition/vaccine-group-rules-artifact`   | Vaccine group rules artifact extension          |
| `PlanDefinition/ice-forecast.json`                            | PlanDefinition      | `https://terminology.cdsframework.org/PlanDefinition/ice-forecast`                            | ICE forecast knowledge-base definition          |
| `ice/PlanDefinition/common-ice-forecast.json`                 | PlanDefinition      | `https://terminology.cdsframework.org/ice/PlanDefinition/common-ice-forecast`                 | Common forecast knowledge-base definition       |
| `ice/StructureDefinition/ice-series-plan-definition.json`     | StructureDefinition | `https://terminology.cdsframework.org/ice/StructureDefinition/ice-series-plan-definition`     | PlanDefinition profile for series content       |
| `ice/StructureDefinition/ice-series-metadata.json`            | StructureDefinition | `https://terminology.cdsframework.org/ice/StructureDefinition/ice-series-metadata`            | Series metadata extension                       |
| `ice/StructureDefinition/ice-dose-number.json`                | StructureDefinition | `https://terminology.cdsframework.org/ice/StructureDefinition/ice-dose-number`                | Dose number extension                           |
| `ice/StructureDefinition/ice-dose-age-constraint.json`        | StructureDefinition | `https://terminology.cdsframework.org/ice/StructureDefinition/ice-dose-age-constraint`        | Dose age constraint extension                   |
| `ice/StructureDefinition/ice-dose-interval-constraint.json`   | StructureDefinition | `https://terminology.cdsframework.org/ice/StructureDefinition/ice-dose-interval-constraint`   | Dose interval constraint extension              |
| `ice/StructureDefinition/ice-dose-vaccine.json`               | StructureDefinition | `https://terminology.cdsframework.org/ice/StructureDefinition/ice-dose-vaccine`               | Dose vaccine extension                          |
| `Bundle/ice-custom-code-systems.json`                         | Bundle              | n/a                                                                                           | Custom code systems/value sets used by ICE      |

## Operation Contract

### Endpoint

- Operation code: `immds-forecast`
- Transport in service docs: `POST /cds/$immds-forecast`
- Body: FHIR `Parameters`

### Request Parameters (in)

- `patient` (required, `Patient`, max 1)
- `assessmentDate` (required, `date`, max 1)
- `knowledgeBase` (required, `canonical`, max 1)
- `immunization` (optional, `Immunization`, repeating)
- `observation` (optional, `Observation`, repeating)
- `scheduleFlag` (optional, `code`, repeating)
- `data` (optional, `Bundle`, max 1; may contain `Patient`, `Immunization`, and `Observation` resources)
- `parameters` (optional, nested `Parameters`, max 1)

### Response Parameters (out)

Response is a `Parameters` resource whose forecast results are returned as top-level `parameter` entries:

- `durationMs` (`integer`, 0..1)
- `engineVersion` (`string`, 1..1)
- `guidanceResponse` (`GuidanceResponse`, 0..1)
- `evaluation` (`ImmunizationEvaluation`, 0..*)
- `recommendation` (`ImmunizationRecommendation`, 0..*)
- `operationOutcome` (`OperationOutcome`, 0..1)

Notes:

- Handled forecast errors return `operationOutcome` and may also include `durationMs` and `engineVersion`; forecast result resources
  may be omitted.
- `assessmentDate` is not emitted as an output parameter.

## Timing Semantics

| Field                                 | Meaning                                                   | Source                 | Notes                                                   |
|---------------------------------------|-----------------------------------------------------------|------------------------|---------------------------------------------------------|
| `Parameters.parameter[durationMs]`    | Total processing time for the ICE request in milliseconds | Service runtime        | Technical metric; not clinically relevant               |
| `GuidanceResponse.occurrenceDateTime` | Timestamp when the forecast/guidance was generated        | ICE engine             | Represents when decision support logic executed         |
| `ImmunizationRecommendation.date`     | Clinical evaluation date used for recommendations         | Input (assessmentDate) | Drives due/overdue calculations; independent of runtime |

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
    - Parent extension with one nested slice:
        - `seriesSelectionType` (`CodeableConcept`, required)
    - Series and season use native `CodeableConcept` fields.
- Implementation guarantee:
    - Extension is emitted only when series selection context is present.
    - Native optional season may be populated independently of the extension.

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

- `targetDisease` is an array of one or more `CodeableConcept` values, one per disease. MMR and DTP each have three entries.
  Evaluation diseases come from the evaluated component CVX mapping in `supportedVaccines.yml`; MR and DT each have two entries.
  A component without a mapping uses its vaccine group's diseases, and ICE logs a warning.
  Recommendations use the vaccine-group disease mapping. Codings within a concept describe the same disease.
  See [Target Diseases](../reference/target-diseases.md) for the supported ICE disease keys and SNOMED CT codings.
- Evaluation and recommendation `series` are `CodeableConcept` values containing selected series coding and text.
- Both also have optional `season` (`CodeableConcept`, `0..1`), populated from selected-season observations.
- Evaluation and recommendation `seriesDoses` (`CodeableConcept`, `0..1`) is the number of doses in the selected series,
  from its series definition and any configured series override.
- Recommendation `seriesDosesRemaining` (`CodeableConcept`, `0..1`) carries only the remaining count, including zero when
  supplied. Evaluations have no remaining count.
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
  `../../opencds-decision-support-service/src/main/resources/data/knowledgeModule/org.nyc.cir.ice/ice-supporting-data/supportedImmunizationSubpotentReasons.yml`

## Configuration Controls

Key property controlling rules-artifact output:

- YAML key: `output-vaccine-group-rules-artifact`
- Java property: `outputVaccineGroupRulesArtifact`
- Drools global: `outputVaccineGroupRulesArtifact`

## Conformance Notes

- This implementation uses the local FHIR R6 model classes under
  `../../opencds-decision-support-core/src/main/java/org/cdsframework/fhir`.
- Current spec artifacts declare `fhirVersion` as `6.0.0-ballot4`.
- OpenAPI examples in service code are aligned to the top-level `Parameters.parameter` response entries and current extension URLs.
