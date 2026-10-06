# Runtime Configuration

This page describes service-wide configuration for ICE. Runtime
configuration applies to every forecast handled by the service instance.

## Configuration mechanisms

ICE properties are bound to the top-level `ice` configuration prefix and are
defined in `org.cdsframework.ice.config.IceProperties`. They can be supplied
through any Spring Boot configuration mechanism, including `application.yml`,
environment variables, and command-line arguments. Restart the service after
changing runtime configuration.

Spring Boot maps configuration-property hierarchy separators (`.`) to
underscores, removes hyphens from each property name, and uppercases the
result. For example:

```text
ice.enable-unsupported-vaccines-group
```

maps to:

```bash
ICE_ENABLEUNSUPPORTEDVACCINESGROUP=true
```

List settings use comma-separated environment-variable values:

```bash
ICE_VACCINEGROUPEXCLUSIONS=ANTHRAX,CHOLERA
ICE_VACCINEGROUPINCLUSIONS=INFLUENZA,RSV
```

## Service-wide settings

The following configuration shows the top-level ICE settings and their
defaults. Output settings are explained in
[Output Configuration](output-configuration.md), schedule flags in
[Schedule Flags](schedule-flags.md), and series and season overrides in
[Series and Season Overrides](series-and-season-overrides.md).

```yaml
ice:
  output-earliest-and-overdue-dates: true
  enable-dose-override-feature: false
  output-supplemental-text: true
  output-number-of-doses-remaining: false
  output-series-information: false
  output-vaccine-group-rules-artifact: false
  output-schedule-authorities: false
  enable-unsupported-vaccines-group: false
  vaccine-group-exclusions:
    - ANTHRAX
    - MOSQUITO_BORNE
    - CHOLERA
    - JAPANESE_ENCEPHALITIS
    - TYPHOID
    - YELLOW_FEVER
  disable-covid19-sep2023-dose-number-reset: false
  supplemental-text-mode: LEGACY

cds-engine:
  experimental-features:
    enable-fhir-r6: false
```

| Setting                                     |      Default | Effect                                                                                                                                                                                                                                                 |
|---------------------------------------------|-------------:|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `enable-dose-override-feature`              |      `false` | Permit request-supplied administered-dose validity overrides.                                                                                                                                                                                          |
| `enable-unsupported-vaccines-group`         |      `false` | Enable the unsupported-vaccines group.                                                                                                                                                                                                                 |
| `output-vaccine-group-rules-artifact`       |      `false` | Include a vaccine-group Wiki URL in ICE SOAP/vMR API or ICE FHIR API (FHIR R6) output when configured. See [Output Configuration](output-configuration.md#vaccine-group-rules-artifact-and-schedule-authorities) to enable it and configure its scope. |
| `vaccine-group-exclusions`                  | Listed above | Do not forecast the listed vaccine groups.                                                                                                                                                                                                             |
| `vaccine-group-inclusions`                  |        Empty | When provided, restrict forecasting to these vaccine groups.                                                                                                                                                                                           |
| `disable-covid19-sep2023-dose-number-reset` |      `false` | Disable the September 2023 COVID-19 dose-number reset behavior.                                                                                                                                                                                        |
| `enable-fhir-r6`                            |      `false` | Enable the experimental FHIR R6 endpoint. This is a startup-only service setting under `cds-engine.experimental-features`, not a forecast request option or an `ice.*` property.                                                                       |

Do not set both `vaccine-group-exclusions` and
`vaccine-group-inclusions` unless the resulting restriction is intentional.

Equivalent environment variables for the settings on this page are:

```bash
ICE_ENABLEDOSEOVERRIDEFEATURE=true
ICE_ENABLEUNSUPPORTEDVACCINESGROUP=true
ICE_DISABLECOVID19SEP2023DOSENUMBERRESET=true
CDS_ENGINE_EXPERIMENTALFEATURES_ENABLEFHIRR6=true
```

See the linked configuration pages for the environment variables associated
with output, schedule flags, and series or season overrides.

## Unsupported vaccines group

Set `enable-unsupported-vaccines-group=true` to include administered vaccines
that are not supported by the configured ICE schedule, provided the vaccine
has a corresponding concept in the ICE concepts configuration.

```bash
ICE_ENABLEUNSUPPORTEDVACCINESGROUP=true
```

ICE evaluates an eligible unsupported administration as `NOT_EVALUATED` with
reason `VACCINE_NOT_SUPPORTED`, under the Other vaccine group. It also returns
an Other-group recommendation with status `NOT_AVAILABLE` and reason
`NOT_SUPPORTED`. If no qualifying unsupported administration is present, the
Other group is omitted.

## Dose override feature

Set `enable-dose-override-feature=true` only when a client must use a request
value to force an administered dose to be evaluated as valid or invalid, such
as for a documented vaccine efficacy concern. The feature controls SOAP/vMR
`SubstanceAdministrationEvent.isValid` and the FHIR
`Immunization.isSubpotent` value that ICE maps to it. The override bypasses
ICE's ordinary evaluation logic for that dose, so it should be used cautiously.

```bash
ICE_ENABLEDOSEOVERRIDEFEATURE=true
```

For SOAP/vMR requests, set `isValid` on the submitted
`SubstanceAdministrationEvent`:

```xml
<isValid value="false"/>
```

For a combination vaccine, the override applies to all components;
component-specific overrides are not supported. ICE reports the applied result
with `DOSE_OVERRIDE_VALID` or `DOSE_OVERRIDE_INVALID` in the evaluation reason.

For FHIR R6, `Immunization.isSubpotent = true` maps to an invalid
administration. When dose override is enabled, clients can use that input to
have the administration treated as invalid.

Configured series-level, dose, dose interval, vaccine-specific dose, and season
overrides are independent of this feature flag. ICE binds those properties
separately and applies them when constructing the effective schedule. Because
schedules are preloaded and cached, changing those override maps normally
requires schedule reconstruction or a service restart. See
[Series and Season Overrides](series-and-season-overrides.md) for the
configuration properties and examples.

## ICE version in responses

ICE returns its version with every response. SOAP/vMR evaluations and
recommendations include `dataSourceType`; the `code` is the ICE version.
The exact value depends on the ICE release running in the service instance.

```xml
<dataSourceType
        code="ICE_<release-version>"
        codeSystem="2.16.840.1.113883.3.795.5.4.12.1.2"
        codeSystemName="org.cdsframework source"/>
```

FHIR R6 responses return the version as the top-level `engineVersion`
`Parameters.parameter`:

```json
{
  "name": "engineVersion",
  "valueString": "ICE_<release-version>"
}
```
