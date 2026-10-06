# ICE Code Values

This page indexes ICE code system references by concept and interface. The Markdown tables for ICE SOAP/vMR API codes are not FHIR
bindings; where the ICE FHIR API (FHIR R6) uses a separate CodeSystem, its repository JSON artifact is identified on the relevant
page.

## Reference pages

### ICE SOAP/vMR API

- [Purpose Code](purpose.md)
- [Optional Output Codes](optional-output.md)

### Reference pages covering ICE SOAP/vMR and ICE FHIR APIs

- [Administrative Gender Codes](gender.md)
- [Disease and Disease Immunity Codes](disease-immunity.md)
- [Evaluation and Recommendation Status Codes](evaluation-and-recommendation-statuses.md)
- [Evaluation and Recommendation Reason Codes](evaluation-and-recommendation-reasons.md)
- [Evaluation and Recommendation Supplemental Text](evaluation-and-recommendation-supplemental-text.md)
- [Vaccine Group Codes](vaccine-groups.md)
- [Series and Display Codes](series.md)
- [Season Codes](season.md)
- [Schedule Authority Codes](schedule-authority.md)
- [Supported Vaccines](supported-vaccines.md)

### ICE FHIR API

- [FHIR Target Diseases](target-diseases.md) — SNOMED CT disease codings used in FHIR `targetDisease` response elements.
- [Contextual Conditions](contextual-conditions.md) — FHIR R6 contextual-condition inputs and ICE mappings.

## Related guidance

FHIR-specific code systems without a separate Markdown table are maintained in [`fhir/ice`](../../fhir/ice) and [
`fhir/Bundle/ice-custom-code-systems.json`](../../fhir/Bundle/ice-custom-code-systems.json).
The [FHIR R6 Implementation Guide](../implementation-guides/ICE-FHIR-R6-Implementation-Guide.md) documents their use in the request and
response contract. The ICE version identifier is described
in [Runtime Configuration](../configuration/runtime-configuration.md#ice-version-in-responses).

For message structure and vMR request and response contexts, see
the [ICE vMR Implementation Guide](../implementation-guides/ICE-vMR-Implementation-Guide.md). Configuration behavior remains documented by
topic under [Configuration](../README.md#configuration).
