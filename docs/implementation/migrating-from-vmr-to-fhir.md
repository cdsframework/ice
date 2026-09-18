# Migrating from vMR to FHIR

## Overview

This guide is for implementers who currently integrate with ICE through the
legacy vMR/SOAP interface and are moving to the current FHIR forecast
interface. It is a migration crosswalk: it identifies the decisions and
mappings that affect an existing integration, while the detailed contracts
remain in the canonical guides.

- [ICE Implementation Guide](ICE_Implementation_Guide.md) describes the legacy
  vMR contract.
- [FHIR R6 Implementation Guide](ICE-FHIR-R6-Implementation-GUIDE.md) describes
  the current FHIR contract.
- [ICE API Client Integration Guide](ICE-API-Client-Integration-Guide.md)
  provides client and supporting-data guidance.

Do not confuse this migration with migration from the previous experimental
FHIR contract to the current FHIR contract. That separate migration is
described in the opening migration section of the [FHIR R6 Implementation
Guide](ICE-FHIR-R6-Implementation-GUIDE.md).

## Before You Migrate

Inventory the behavior that the existing client depends on before changing
the transport or data model. In particular, record:

- the current DSS operation and vMR payload fields used;
- service-wide and request-level schedule flags;
- runtime and output configuration that affects the response;
- response fields that the client parses;
- vaccine-group assumptions, including how evaluation and recommendation
  focus are interpreted;
- reason and status fields used by downstream logic; and
- optional series, season, date, and supplemental output that the client
  relies on.

Use the [Runtime Configuration](../configuration/runtime-configuration.md),
[Output Configuration](../configuration/output-configuration.md), and
[Schedule Flags](../configuration/schedule-flags.md) documents to identify
configuration that must be reproduced or reviewed in the FHIR deployment.

## Endpoint and Request Envelope

| Concern | Legacy integration | Current FHIR integration |
|---|---|---|
| Invocation | DSS/SOAP operation, commonly `evaluate` or `evaluateAtSpecifiedTime` | `POST /cds/$immds-forecast` |
| Payload | vMR XML, carried in the SOAP request and commonly base64-encoded in `base64EncodedPayload` | FHIR `Parameters` resource |
| Required forecast inputs | Patient and vMR clinical statements, with specified-time behavior supplied through DSS | `patient`, `assessmentDate`, and `knowledgeBase` |
| Historical immunizations | vMR `SubstanceAdministrationEvent` entries | Repeating `immunization` parameters containing FHIR `Immunization` resources |
| Context and options | vMR observation results and service/request conventions | Repeating `observation` contextual conditions and `scheduleFlag` parameters |

The FHIR request is sent to the current `$immds-forecast` operation. The
`knowledgeBase` value is required even when the client previously selected a
knowledge module through DSS/vMR conventions. See the [FHIR R6 Implementation
Guide](ICE-FHIR-R6-Implementation-GUIDE.md) for the complete request
parameter contract and examples.

## Input Mapping

### Patient and demographics

The legacy request represents the patient and demographics in the vMR patient
structure, including demographic attributes used by the forecast. The FHIR
request supplies a FHIR `Patient` resource in the `patient` parameter. Map the
demographics used by the existing forecast, especially the patient's birth
date and administrative gender, to the corresponding FHIR Patient elements.

Do not assume that an arbitrary vMR extension or local demographic field has a
FHIR equivalent. Confirm any field that affects local client behavior against
the [FHIR R6 Implementation Guide](ICE-FHIR-R6-Implementation-GUIDE.md).

### Assessment date

Legacy clients commonly obtain specified-time behavior from the DSS operation
and the vMR/DSS invocation. The FHIR request makes the forecast date explicit
with the required `assessmentDate` parameter. Preserve the same business
meaning when mapping the date; do not use response timing fields as a
replacement for the assessment date.

### Immunizations

Map each legacy `SubstanceAdministrationEvent` to a FHIR `Immunization`
resource in a repeating `immunization` parameter. Preserve the administered
vaccine coding and administration/occurrence date used by the existing
integration. Review combination-vaccine and product-code assumptions against
the [Supported Vaccines](../reference/supported-vaccines.md) and [FHIR R6
Implementation Guide](ICE-FHIR-R6-IMPLEMENTATION-GUIDE.md).

### Dose validity and subpotent handling

Legacy vMR clients can supply `SubstanceAdministrationEvent.isValid` when the
request-supplied administered-dose validity override is enabled. The current
FHIR representation is `Immunization.isSubpotent`; ICE maps
`isSubpotent = true` to an invalid administered dose for forecast processing.
The runtime feature flag controls this request-supplied override behavior; see
[Runtime Configuration](../configuration/runtime-configuration.md#dose-override-feature).

This is separate from ordinary schedule evaluation. Do not translate every
legacy invalid evaluation into `isSubpotent`; map the input only when the
legacy integration was intentionally using the request-level override.

### Disease immunity and contextual conditions

Legacy vMR integrations represent disease immunity and related context through
vMR disease-immunity observation results. The FHIR request uses repeating
contextual-condition `observation` parameters. These observations use an IVCI
contextual-condition code and `valueDateTime`; ICE maps the supported IVCI
code to its internal disease and disease-immunity-source concepts.

The client should send the supported contextual-condition representation, not
an ICE-translated disease or immunity-source value in place of the IVCI input.
See [Contextual Conditions](ICE-FHIR-R6-IMPLEMENTATION-GUIDE.md#contextual-conditions)
for the supported codes and request shape.

### Schedule flags

Legacy clients supply schedule flags as vMR observation results, or rely on
service-wide configuration. The FHIR request supplies each request-level flag
as a repeating `scheduleFlag` parameter. The service combines configured and
request-level flags according to the [Schedule Flags](../configuration/schedule-flags.md)
documentation. Preserve the flag names and intended boolean behavior rather
than treating them as generic observations.

## Output Mapping

The legacy response adds forecast-related clinical statements to the vMR
output. The FHIR response returns structured `ImmunizationEvaluation` and
`ImmunizationRecommendation` resources as top-level `Parameters.parameter`
entries. The following is a conceptual mapping; it is not a replacement for
the complete FHIR response contract.

### Evaluation

| Legacy vMR | Current FHIR |
|---|---|
| Evaluated administered vaccine | `ImmunizationEvaluation` |
| Evaluation focus | `ImmunizationEvaluation.targetDisease`, using disease concepts |
| Evaluation validity/status | `doseStatus` |
| Evaluation reasons | `doseStatusReason` and related FHIR-coded output |
| Series and dose context | FHIR series, dose number, and series-dose fields when returned/configured |

### Recommendation

| Legacy vMR | Current FHIR |
|---|---|
| `SubstanceAdministrationProposal` | `ImmunizationRecommendation` recommendation entry |
| Recommendation focus | Recommendation `targetDisease`, using disease concepts |
| Recommendation status | `forecastStatus` |
| Recommendation reasons | `forecastReason` |
| Forecast dates | FHIR date criteria such as earliest, recommended, and overdue dates when returned/configured |
| Series and dose context | FHIR series, dose number, and series-dose fields when returned/configured |

For the exact fields, cardinalities, and response envelope, use the [FHIR R6
Implementation Guide](ICE-FHIR-R6-IMPLEMENTATION-GUIDE.md). For code systems
and reason meanings, use [Codes and Terminology](../reference/codes-and-terminology.md)
and [Reason Codes](../reference/reason-codes.md).

## Vaccine Group vs `targetDisease`

This is the most important semantic difference for clients that compare
forecasts by vaccine group.

Legacy vMR evaluation and recommendation focus values use ICE vaccine-group
codes. Current FHIR output does not use those numeric vaccine-group focus
values as a direct replacement. `targetDisease` is a disease `CodeableConcept`,
typically with SNOMED CT coding.

For a legacy-to-FHIR comparison, use the supporting data to normalize the
concepts in this direction:

```text
vMR vaccine-group focus
  -> ICE vaccine-group supporting data
  -> associated ICE disease concept(s)
  -> FHIR targetDisease
```

For reverse comparison or A/B testing, normalize in the other direction:

```text
FHIR targetDisease SNOMED CT
  -> ICE disease code
  -> one or more ICE vaccine groups
```

The mapping is not always one-to-one. For example, DTP and MMR groups cover
multiple diseases, so a FHIR result can contain multiple disease codings while
the legacy comparison key remains one vaccine group. The [API Client
Integration Guide's Vaccine Group Normalization](ICE-API-Client-Integration-Guide.md#vaccine-group-normalization)
section describes the supporting-data lookups, reverse mappings, and complete
normalization table. Keep that lookup material there rather than copying it
into this migration guide.

## Status, Reason, and Terminology Differences

Legacy vMR fields expose ICE-specific evaluation and recommendation code
systems in vMR status, focus, and reason fields. FHIR gives those concepts
structured locations such as `doseStatus`, `doseStatusReason`,
`forecastStatus`, and `forecastReason`, while many coded values still use ICE
terminology.

Treat the FHIR field structure and the code-system identity as separate
migration concerns. Update parsers for the FHIR element in which a concept is
returned, then preserve the appropriate code-system and code-value handling.
Use [Codes and Terminology](../reference/codes-and-terminology.md) and [Reason
Codes](../reference/reason-codes.md) rather than reproducing the full tables
here.

## Optional Output and Extensions

Some information that legacy clients obtain from nested vMR output is
optional in the current FHIR response and is controlled by service
configuration. Depending on configuration and the forecast, this can include:

- selected series and series selection type;
- selected season;
- number of doses remaining;
- vaccine-group rules artifact or Wiki URL; and
- schedule authority information.

In FHIR, these values may be represented by resource fields or ICE-defined
extensions, including the immunization-selection-context,
vaccine-group-rules-artifact, and schedule-authority extensions. Review the
effective deployment settings in [Output Configuration](../configuration/output-configuration.md)
and the exact FHIR representations in the [FHIR R6 Implementation
Guide](ICE-FHIR-R6-IMPLEMENTATION-GUIDE.md). Series and season behavior itself
is documented in [Series and Season Overrides](../configuration/series-and-season-overrides.md).

Do not assume an optional field is present just because the legacy client
received analogous nested output. Check the relevant output control and handle
its absence.

## Error Handling

Legacy SOAP/vMR integrations commonly handle transport/DSS errors and errors
encoded in the service response separately. The current FHIR operation uses
`OperationOutcome` for application-level errors and warnings in the FHIR
response. A response can contain an `operationOutcome` without the forecast
resources, and HTTP status alone is not sufficient to determine whether a
forecast result is usable.

Update the client to inspect the FHIR `Parameters` response and its
`operationOutcome` parameter, while retaining normal HTTP and malformed-request
handling. See the [FHIR R6 Implementation Guide](ICE-FHIR-R6-IMPLEMENTATION-GUIDE.md)
for the contract and the [API Client Integration Guide](ICE-API-Client-Integration-Guide.md)
for client-side implementation considerations.

## Supporting Data and Terminology

Supporting data is particularly important during migration because it bridges
legacy vaccine-group output and FHIR disease-level output. Depending on the
client's comparison and display needs, consult ICE supporting-data endpoints
for:

- vaccine groups;
- diseases;
- vaccines;
- series;
- seasons; and
- code systems.

Use the [API Client Integration Guide](ICE-API-Client-Integration-Guide.md) for
the endpoint list, response shapes, and lookup process. Use [Supported
Vaccines](../reference/supported-vaccines.md) and [Codes and
Terminology](../reference/codes-and-terminology.md) for maintained reference
material.

## A/B Testing and Rollout

For a vMR-to-FHIR comparison, compare normalized records rather than raw vMR
and FHIR codes. In particular:

1. Normalize the vMR vaccine-group focus to an ICE vaccine-group key.
2. Normalize each FHIR `targetDisease` through ICE disease and vaccine-group
   supporting data when a vaccine-group comparison is required.
3. Compare clinically relevant dimensions such as vaccine group, evaluation or
   recommendation status, reasons, and dose/series information.
4. Retain disease-level `targetDisease` detail for debugging multi-disease and
   many-to-one mappings.

The [API Client Integration Guide](ICE-API-Client-Integration-Guide.md) has
the detailed normalization and A/B-testing workflow. This guide does not
define an official rollout policy or deprecation timeline.

## Related Documentation

- [ICE Implementation Guide](ICE_Implementation_Guide.md)
- [FHIR R6 Implementation Guide](ICE-FHIR-R6-Implementation-GUIDE.md)
- [ICE API Client Integration Guide](ICE-API-Client-Integration-Guide.md)
- [Runtime Configuration](../configuration/runtime-configuration.md)
- [Output Configuration](../configuration/output-configuration.md)
- [Schedule Flags](../configuration/schedule-flags.md)
- [Series and Season Overrides](../configuration/series-and-season-overrides.md)
- [Codes and Terminology](../reference/codes-and-terminology.md)
- [Reason Codes](../reference/reason-codes.md)
- [Supported Vaccines](../reference/supported-vaccines.md)
