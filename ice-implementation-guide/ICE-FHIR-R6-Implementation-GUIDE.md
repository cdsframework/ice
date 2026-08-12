# ICE FHIR R6 Implementation Guide

## Scope

This guide documents the implemented FHIR R6 contract for ICE forecasting in this repository, including:

- Operation contract for `immunization-forecast`
- Output envelope and parameter semantics
- ICE-specific extension definitions and runtime behavior
- Configuration switches that affect FHIR output

This guide reflects the current implementation and `fhir/*.json` artifacts in this codebase.

## Canonical Artifacts (`/fhir`)

| File                                                     | Resource Type       | Canonical URL                                                                                 | Purpose                                                |
|----------------------------------------------------------|---------------------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------|
| `ice-full-operation-definition.json`                     | OperationDefinition | `http://example.org/fhir/OperationDefinition/immunization-forecast`                           | Request/response contract for `$immunization-forecast` |
| `ice-structure-definition.json`                          | StructureDefinition | `http://terminology.cdsframework.org/fhir/StructureDefinition/immunization-selection-context` | Selection context extension                            |
| `ice-vaccine-group-rules-artifact.json`                  | StructureDefinition | `http://terminology.cdsframework.org/fhir/StructureDefinition/vaccine-group-rules-artifact`   | Vaccine group rules artifact extension                 |
| `ice-plan-definition.json`                               | PlanDefinition      | `http://cdsframework.org/PlanDefinition/ice-forecast`                                         | ICE module definition                                  |
| `ice-common-plan-definition.json`                        | PlanDefinition      | `http://cdsframework.org/PlanDefinition/common-ice-forecast`                                  | Common module definition                               |
| `ice-series-plan-definition-structure-definition.json`   | StructureDefinition | `http://cdsframework.org/fhir/StructureDefinition/ice-series-plan-definition`                 | PlanDefinition profile for series content              |
| `ice-series-metadata-structure-definition.json`          | StructureDefinition | `http://cdsframework.org/fhir/StructureDefinition/ice-series-metadata`                        | Series metadata extension                              |
| `ice-dose-number-structure-definition.json`              | StructureDefinition | `http://cdsframework.org/fhir/StructureDefinition/ice-dose-number`                            | Dose number extension                                  |
| `ice-dose-age-constraint-structure-definition.json`      | StructureDefinition | `http://cdsframework.org/fhir/StructureDefinition/ice-dose-age-constraint`                    | Dose age constraint extension                          |
| `ice-dose-interval-constraint-structure-definition.json` | StructureDefinition | `http://cdsframework.org/fhir/StructureDefinition/ice-dose-interval-constraint`               | Dose interval constraint extension                     |
| `ice-dose-vaccine-structure-definition.json`             | StructureDefinition | `http://cdsframework.org/fhir/StructureDefinition/ice-dose-vaccine`                           | Dose vaccine extension                                 |
| `ice-schedule-authority-structure-definition.json`       | StructureDefinition | `http://terminology.cdsframework.org/fhir/StructureDefinition/ice-schedule-authority`         | Schedule authority extension                           |
| `ice-custom-code-systems.json`                           | Bundle              | n/a                                                                                           | Custom code systems/value sets used by ICE             |

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

- Canonical URL: `http://terminology.cdsframework.org/fhir/StructureDefinition/immunization-selection-context`
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

- Canonical URL: `http://terminology.cdsframework.org/fhir/StructureDefinition/vaccine-group-rules-artifact`
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

### 3) ICE Schedule Authority

- Canonical URL: `http://terminology.cdsframework.org/fhir/StructureDefinition/ice-schedule-authority`
- Allowed on:
    - `ImmunizationRecommendation.recommendation`
- Shape:
    - `valueReference` to an `Organization` with:
        - `type = "Organization"`
        - `identifier` with:
            - `system = "http://terminology.cdsframework.org/ice/schedule-authority"`
            - `value` = authority code (e.g., `ACIP_CDC`, `AAP`, `AAFP`)
        - `display` = authority name
- Implementation guarantee:
    - Multiple extensions may be present, one per schedule authority.
    - Emitted only for recommendations, not evaluations.

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
YAML keys are relative to ice.knowledge-modules.'[http://cdsframework.org/PlanDefinition/ice-forecast|1.0.0]'

Key properties controlling selection context and number of doses remaining output:

- YAML key: `output-series-information`
- Java property: `outputSeriesInformation`
- Drools global: `outputSeriesInformation`

- YAML key: `output-number-of-doses-remaining`
- Java property: `outputNumberOfDosesRemaining`
- Drools global: `outputNumberOfDosesRemaining`

Key property controlling rules-artifact output:

- YAML key: `output-vaccine-group-rules-artifact`
- Java property: `outputVaccineGroupRulesArtifact`
- Drools global: `outputVaccineGroupRulesArtifact`

Key property controlling schedule-authority output:

- YAML key: `output-schedule-authorities`
- Java property: `outputScheduleAuthorities`
- Drools global: `outputScheduleAuthorities`

## Conformance Notes

- This implementation uses the local FHIR R6 model classes under
  `opencds-decision-support-core/src/main/java/org/cdsframework/fhir`.
- Current spec artifacts declare `fhirVersion` as `6.0.0-ballot4`.
- OpenAPI examples in service code are aligned to the `output.part[]` response envelope and current extension URLs.
