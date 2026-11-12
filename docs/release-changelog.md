# ICE Release Changelog

This changelog describes changes relevant to ICE implementers and operators. For changes to the documentation itself,
see the [Documentation Revision History](revision-history.md).

Release notes are also available at https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/78479371/Release+Notes

## 2.61.1 — November 12, 2026

### Actions for Implementers

#### Series output configuration

The `output-series-information` setting has been removed. ICE rejects it at startup when configured globally or for a
knowledge base. Replace it at the same configuration level with both of the following settings, using values that match
the desired output:

```yaml
output-series-information-for-evaluations: true
output-series-information-for-recommendations: true
```

Both settings default to `false`. To preserve output previously enabled by `output-series-information: true`, enable
both. When `output-number-of-doses-remaining` is enabled, the effective recommendation series setting must be `true`
for each affected knowledge base; ICE rejects incompatible settings at startup. An explicit global setting overrides
the corresponding knowledge-base value.

The removed environment variable `ICE_OUTPUTSERIESINFORMATION` must also be replaced. See
[Output Configuration](configuration/output-configuration.md#series-information-and-doses-remaining) for the new
environment variables, output combinations, and SOAP/vMR and FHIR examples.

#### FHIR R6 response changes

FHIR clients should update their response parsing for the revised R6 structures. Evaluation and recommendation
`series` and optional `season` are `CodeableConcept` objects. `seriesDoses` represents the selected series size;
recommendation `seriesDosesRemaining` carries the remaining dose count. `targetDisease` is an array of disease
concepts, and schedule authorities use native `authority` references on recommendation entries. Review configuration and
representative responses before upgrading. SOAP/vMR payload construction for series output is unchanged.

See the [FHIR R6 Implementation Guide](implementation-guides/ICE-FHIR-R6-Implementation-Guide.md) and
[Output Configuration](configuration/output-configuration.md) for details and examples.

#### FHIR contextual-condition code system

FHIR clients submitting contextual-condition observations should update `code.coding.system` from
`http://ivci.org/CodeSystem/contextual-condition` to `https://ivci.org/contextual-conditions/usa/cdc-cdsi`.
See [Contextual Conditions](reference/contextual-conditions.md) for supported codes.

#### Series and season overrides

Individual `ICE_SERIESOVERRIDES_*` and `ICE_SEASONOVERRIDES_*` environment variables shown in earlier documentation do
not bind reliably to these overrides. Use `SPRING_APPLICATION_JSON` or an additional YAML configuration file supplied
through `SPRING_CONFIG_ADDITIONAL_LOCATION` instead. Existing YAML-based overrides remain supported. Review and test any
environment-based overrides, including HPV age settings, when upgrading. This corrects the documentation; it does not
change ICE's runtime binding behavior.

See [Series and Season Overrides](configuration/series-and-season-overrides.md) for configuration details.

### Vaccine Group Changes

#### COVID-19

For the seasonal COVID-19 series for ages 2–64, ICE now retains target-dose-1 supplemental interval guidance when the
recommendation becomes `CONDITIONAL` with reason `CLINICAL_PATIENT_DISCRETION`. Previously, output status
filtering could omit this guidance. The forecast criteria and recommended intervals are unchanged.

#### Hepatitis B

ICE adds support for CVX 193 (Hep A-Hep B, adolescent/pediatric, non-US; Twinrix Jr) for
Hepatitis B evaluation. The Hepatitis A component does not count toward the Hepatitis A series.

- Adds CVX 193 as a non-preferred vaccine in the Hepatitis B child/adolescent series. Doses are subject to the applicable
  evaluation rules, including age and interval requirements.
- Evaluates a CVX 193 dose administered at age 18 years or older as invalid for Hepatitis B due to insufficient antigen.

For adults with no qualifying Hepatitis B dose, the 3-dose adult series returns the date of birth as the earliest and
recommended dates for dose 1, replacing the 19th birthday. The absolute minimum age for evaluating dose 1 remains
19 years. Series selection and doses 2 and 3 are unchanged.

#### MMR

MMR administrations assigned to target dose 2 and given at age 19 or older are evaluated using the
standard validity rules rather than automatically classified as `ACCEPTED` with reason `EXTRA_DOSE` solely because of age.
The existing adult forecasting rules, under which the series may be considered complete with one dose, are unchanged.

#### Meningococcal ACWY

- CVX 32 is removed from the MenACWY series allowed-vaccine list. Doses administered on or before September 30, 2017
  are evaluated as `ACCEPTED` with reason `VACCINE_NOT_PART_OF_THIS_SERIES` and do not count toward the series. Doses
  administered after that date are invalid with reason `VACCINE_NOT_ALLOWED`. The cutoff uses the administered vaccine's
  end date.
- The minimum valid age for CVX 203 (MenQuadfi) is lowered from 2 years minus 4 days to 6 weeks minus 4 days.
- The one-dose series-completion rule now includes the four-day grace period before the 16th birthday. A qualifying
  valid dose administered at age 16 years minus 4 days through the day before the 19th birthday can complete the series
  when it is the only administered dose in that series. The grace period applies in evaluation and forecasting.

#### Meningococcal B

Corrected an issue affecting some patient histories containing both Bexsero and Trumenba. In these cases, ICE could fail
to select the appropriate MenB series after converting the Bexsero two-dose series to a three-dose series, resulting in
empty clinical output. ICE now selects the series and returns evaluations and forecasts for these histories.

#### Pneumococcal

Updated adult risk-based supplemental text to distinguish routine vaccination beginning at age 50 from recommendations
for adults ages 19–49 with certain risk factors.

#### RSV

Corrected the selected series and season displayed with a recommendation when an adult has an infant RSV vaccination
history. ICE now displays the series that produced the recommendation instead of potentially selecting the infant
series or pairing the adult series with the infant season. Dose evaluations continue to show the infant series and its
season when applicable.

RSV CVX codes 304, 314, and 315 are now identified as unspecified formulations. This allows the existing same-day
duplicate-dose rules to evaluate an unspecified RSV dose alongside a formulation-specific dose given on the same day.

### General Rules

- When a dose is invalid because it was administered after the vaccine's configured CVX end date, ICE
  retains `VACCINE_NOT_ALLOWED` and omits the redundant `VACCINE_NOT_ALLOWED_FOR_THIS_DOSE` reason when both apply. The dose
  remains invalid, and other applicable reasons may still be returned.
- ICE adds support for routing a vaccine dose that may be considered by more than one vaccine group and validates that
  the same dose component does not remain in multiple groups after history evaluation. The current Varicella rule removes
  CVX 121 from Varicella evaluation; the supported-vaccine reference lists it under Zoster. This prepares for future rule
  work and does not add CVX 121 to the Varicella supported-vaccine list.
- Same-day vaccine components are ordered deterministically during processing. This addresses dependence on request order;
  individual clinical outcomes should be validated using the applicable regression scenarios.

### Core Service and Configuration

- Updated Spring Boot from 4.1.1 to 4.1.2 and refreshed supporting dependencies.
- Restored complete CVX vaccine display names truncated during the YAML migration and corrected several display-label
  errors, including missing formulation details and vaccine product information.
- ICE now fails startup if a required rules package cannot be loaded; previously, it could log the failure and continue.
- When rule sources are present on the classpath, the knowledge loader uses them instead of a leftover precompiled model.
- The service includes OpenTelemetry support. The bundled configuration sets trace sampling probability to `1.0` and
  disables OTLP metrics export. Actual telemetry export depends on the deployment configuration.

### FHIR Interface

- Updated FHIR R6 series, season, dose-count, target-disease, and schedule-authority representations. When an evaluated
  component lacks disease mapping, ICE uses the vaccine group's diseases and logs a warning rather than failing the response.
- Refined `OperationOutcome` issue classification and warning detection based on issue severity.
