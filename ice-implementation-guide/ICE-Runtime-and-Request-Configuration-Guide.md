# ICE Runtime and Request Configuration Guide

This guide is for operators and API clients of ICE 2.58.1. It explains service-wide runtime overrides and schedule flags that a
client can supply for an individual forecast.

The behavior described here includes schedule flags introduced in commit
`0cba4d0cdd0732fd17bffeeb4dad1f99e91a05a1`.

## Runtime Configuration

Runtime configuration applies to every forecast handled by the service instance. Set it with top-level `ice.*` properties in service
configuration or with environment variables, then restart the service for changes to take effect. Request-level schedule flags apply
only to the individual forecast.

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

### Settings

| Setting                                     |      Default | Effect                                                                                                                                             |
|---------------------------------------------|-------------:|----------------------------------------------------------------------------------------------------------------------------------------------------|
| `output-earliest-and-overdue-dates`         |       `true` | Include earliest and overdue forecast dates.                                                                                                       |
| `enable-dose-override-feature`              |      `false` | Enable configured series, dose, and interval overrides.                                                                                            |
| `output-supplemental-text`                  |       `true` | Include supplemental recommendation and evaluation text.                                                                                           |
| `output-number-of-doses-remaining`          |      `false` | Include the number of doses remaining in the series.                                                                                               |
| `output-series-information`                 |      `false` | Include series-selection and series-dose information in output.                                                                                    |
| `output-vaccine-group-rules-artifact`       |      `false` | Include a link to the vaccine-group rules artifact when available.                                                                                 |
| `output-schedule-authorities`               |      `false` | Include schedule authorities associated with a recommendation. In FHIR, this is the ICE schedule-authority extension on each recommendation entry. |
| `enable-unsupported-vaccines-group`         |      `false` | Enable the unsupported-vaccines group.                                                                                                             |
| `vaccine-group-exclusions`                  | Listed above | Do not forecast the listed vaccine groups.                                                                                                         |
| `vaccine-group-inclusions`                  |        Empty | When provided, restrict forecasting to these vaccine groups.                                                                                       |
| `disable-covid19-sep2023-dose-number-reset` |      `false` | Disable the September 2023 COVID-19 dose-number reset behavior.                                                                                    |
| `supplemental-text-mode`                    |     `LEGACY` | Choose supplemental-text representation: `LEGACY`, `CODED`, or `BOTH`.                                                                             |
| `enable-fhir-r6`                            |      `false` | Enable the experimental FHIR R6 endpoint. This is a startup-only service setting, not a forecast request option.                                   |

Do not set both `vaccine-group-exclusions` and `vaccine-group-inclusions`
unless the resulting restriction is intentional.

## Environment Variables

Spring Boot maps configuration-property hierarchy separators (`.`) to underscores, removes hyphens from each property name, and
uppercases the result. The following environment variables set service-wide runtime overrides:

```bash
ICE_OUTPUTEARLIESTANDOVERDUEDATES=true
ICE_ENABLEDOSEOVERRIDEFEATURE=true
ICE_OUTPUTSUPPLEMENTALTEXT=true
ICE_OUTPUTNUMBEROFDOSESREMAINING=true
ICE_OUTPUTSERIESINFORMATION=true
ICE_OUTPUTVACCINEGROUPRULESARTIFACT=true
ICE_OUTPUTSCHEDULEAUTHORITIES=true
ICE_ENABLEUNSUPPORTEDVACCINESGROUP=true
ICE_DISABLECOVID19SEP2023DOSENUMBERRESET=true
ICE_SUPPLEMENTALTEXTMODE=BOTH
CDS_ENGINE_EXPERIMENTALFEATURES_ENABLEFHIRR6=true

# Enable service-wide schedule flags.
ICE_SCHEDULEFLAGS=HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID,POLIO_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID
```

For list settings, provide a comma-separated value:

```bash
ICE_VACCINEGROUPEXCLUSIONS=ANTHRAX,CHOLERA
ICE_VACCINEGROUPINCLUSIONS=INFLUENZA,RSV
```

### Supplemental Text Mode

`supplemental-text-mode` controls how supplemental evaluation and recommendation reasons are represented in forecast output. It
applies only when
`output-supplemental-text` is `true`. Use the global
`ice.supplemental-text-mode` property:

```yaml
ice:
  output-supplemental-text: true
  supplemental-text-mode: LEGACY
```

The following examples use the supported supplemental recommendation reason
`PCV15_OR_PCV20`: `Administer either PCV15 or PCV20.` For an evaluation, the same behavior appears in SOAP `interpretation` and FHIR
`doseStatusReason`
instead of SOAP recommendation `interpretation` and FHIR `forecastReason`.

#### `LEGACY` (default)

`LEGACY` preserves the generic `SUPPLEMENTAL_TEXT` reason. In SOAP, the specific message is in `originalText`; in FHIR, it is the
`CodeableConcept.text`.

SOAP vMR output:

```xml

<interpretation
        code="SUPPLEMENTAL_TEXT"
        codeSystem="2.16.840.1.113883.3.795.12.100.6"
        displayName="Supplemental text is available for this recommendation."
        originalText="Administer either PCV15 or PCV20."/>
```

FHIR R6 output:

```json
{
  "forecastReason": [
    {
      "coding": [
        {
          "system": "https://terminology.cdsframework.org/ice/recommendation-reason",
          "code": "SUPPLEMENTAL_TEXT",
          "display": "Supplemental text is available for this recommendation."
        }
      ],
      "text": "Administer either PCV15 or PCV20."
    }
  ]
}
```

#### `CODED`

`CODED` returns the specific supplemental-reason code. It does not return the legacy `SUPPLEMENTAL_TEXT` code or message.

```yaml
ice:
  output-supplemental-text: true
  supplemental-text-mode: CODED
```

SOAP vMR output:

```xml

<interpretation
        code="PCV15_OR_PCV20"
        codeSystem="2.16.840.1.113883.3.795.12.100.50"
        displayName="Administer either PCV15 or PCV20."/>
```

FHIR R6 output:

```json
{
  "forecastReason": [
    {
      "coding": [
        {
          "system": "https://terminology.cdsframework.org/ice/supplemental-recommendation-reason",
          "code": "PCV15_OR_PCV20",
          "display": "Administer either PCV15 or PCV20."
        }
      ],
      "text": "Administer either PCV15 or PCV20."
    }
  ]
}
```

#### `BOTH`

`BOTH` returns the legacy reason followed by the specific coded reason. Use it when clients need the legacy representation while
they migrate to the supplemental-reason code systems.

```bash
ICE_OUTPUTSUPPLEMENTALTEXT=true
ICE_SUPPLEMENTALTEXTMODE=BOTH
```

SOAP vMR output:

```xml

<interpretation
        code="SUPPLEMENTAL_TEXT"
        codeSystem="2.16.840.1.113883.3.795.12.100.6"
        displayName="Supplemental text is available for this recommendation."
        originalText="Administer either PCV15 or PCV20."/>
<interpretation
code="PCV15_OR_PCV20"
codeSystem="2.16.840.1.113883.3.795.12.100.50"
displayName="Administer either PCV15 or PCV20."/>
```

FHIR R6 output:

```json
{
  "forecastReason": [
    {
      "coding": [
        {
          "system": "https://terminology.cdsframework.org/ice/recommendation-reason",
          "code": "SUPPLEMENTAL_TEXT",
          "display": "Supplemental text is available for this recommendation."
        }
      ],
      "text": "Administer either PCV15 or PCV20."
    },
    {
      "coding": [
        {
          "system": "https://terminology.cdsframework.org/ice/supplemental-recommendation-reason",
          "code": "PCV15_OR_PCV20",
          "display": "Administer either PCV15 or PCV20."
        }
      ],
      "text": "Administer either PCV15 or PCV20."
    }
  ]
}
```

## Forecast Dates

When `output-earliest-and-overdue-dates` is enabled, ICE returns three clinically distinct forecast dates:

| Date     | Meaning                                                                                                        |
|----------|----------------------------------------------------------------------------------------------------------------|
| Earliest | The first date the vaccine can be administered and be valid, without applying the usual four-day grace period. |
| Due      | The recommended administration date.                                                                           |
| Overdue  | The date after which a recommended immunization is late.                                                       |

For example, a routine fifth DTaP dose may be due on the patient's fourth birthday and overdue on the seventh birthday. Earliest
dates support clinical review of an early administration; the due date remains the usual target for routine administration.

FHIR R6 responses carry these values in
`ImmunizationRecommendation.recommendation[].dateCriterion` with these LOINC codings:

| Date     | LOINC code                                     |
|----------|------------------------------------------------|
| Earliest | `30981-5` — Earliest date to give              |
| Due      | `30980-7` — Date vaccine due                   |
| Overdue  | `59778-1` — Date when overdue for immunization |

```json
{
  "dateCriterion": [
    {
      "code": {
        "coding": [
          {
            "system": "http://loinc.org",
            "code": "30981-5",
            "display": "Earliest date to give"
          }
        ]
      },
      "value": "2026-09-01"
    },
    {
      "code": {
        "coding": [
          {
            "system": "http://loinc.org",
            "code": "30980-7",
            "display": "Date vaccine due"
          }
        ]
      },
      "value": "2026-10-01"
    },
    {
      "code": {
        "coding": [
          {
            "system": "http://loinc.org",
            "code": "59778-1",
            "display": "Date when overdue for immunization"
          }
        ]
      },
      "value": "2029-10-01"
    }
  ]
}
```

## Dose Override Feature

Set `enable-dose-override-feature=true` only when the client must force an administered dose to be evaluated as valid or invalid,
such as for a documented vaccine efficacy concern. The override bypasses ICE's ordinary evaluation logic for that dose, so it should
be used cautiously.

```bash
ICE_ENABLEDOSEOVERRIDEFEATURE=true
```

For SOAP/vMR requests, set `isValid` on the submitted
`SubstanceAdministrationEvent`:

```xml

<isValid value="false"/>
```

For a combination vaccine, the override applies to all components; component- specific overrides are not supported. ICE reports the
applied result with
`DOSE_OVERRIDE_VALID` or `DOSE_OVERRIDE_INVALID` in the evaluation reason.

For FHIR R6, `Immunization.isSubpotent = true` maps to an invalid administration. When dose override is enabled, clients can use
that input to have the administration treated as invalid.

## Unsupported Vaccines Group

Set `enable-unsupported-vaccines-group=true` to include administered vaccines that are not supported by the configured ICE schedule,
provided the vaccine has a corresponding concept in the ICE concepts configuration.

```bash
ICE_ENABLEUNSUPPORTEDVACCINESGROUP=true
```

ICE evaluates an eligible unsupported administration as `NOT_EVALUATED` with reason `VACCINE_NOT_SUPPORTED`, under the Other vaccine
group. It also returns an Other-group recommendation with status `NOT_AVAILABLE` and reason
`NOT_SUPPORTED`. If no qualifying unsupported administration is present, the Other group is omitted.

## ICE Version in Responses

ICE returns its version with every response. SOAP/vMR evaluations and recommendations include `dataSourceType`; the `code` is the
ICE version.

```xml

<dataSourceType
        code="ICE_2.58.1"
        codeSystem="2.16.840.1.113883.3.795.5.4.12.1.2"
        codeSystemName="org.cdsframework source"/>
```

FHIR R6 responses return the version as the top-level `engineVersion`
`Parameters.parameter`:

```json
{
  "name": "engineVersion",
  "valueString": "ICE_2.58.1"
}
```

## Schedule Flags

Schedule flags alter a specific evaluation rule. An unknown flag makes the request invalid. ICE 2.58.1 defines these flags:

| Flag                                         | Effect                                                                                 |
|----------------------------------------------|----------------------------------------------------------------------------------------|
| `HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID` | Evaluate an invalid third Hepatitis B dose as an accepted extra dose.                  |
| `POLIO_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID` | Evaluate a fourth or fifth polio dose below the minimum age as an accepted extra dose. |

### Set Schedule Flags in the Service Environment

Set flags for every SOAP and FHIR forecast handled by the service instance:

```bash
ICE_SCHEDULEFLAGS=HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID,POLIO_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID
```

Equivalent YAML:

```yaml
ice:
  schedule-flags:
    - HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID
    - POLIO_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID
```

Configured flags are added only when the incoming SOAP vMR payload does not already contain that flag. In FHIR, configured flags are
combined with request flags and submitted once. The service validates configured flags during service initialization; a misspelled
or unsupported value prevents startup.

### Send a Schedule Flag in a SOAP Forecast

For SOAP, put the flag in the base64-encoded vMR `CDSInput` payload, under the patient's `clinicalStatements/observationResults`.
The SOAP envelope itself is unchanged. The decoded payload must contain an `observationResult` like this:

```xml

<observationResult>
    <templateId root="2.16.840.1.113883.3.795.11.6.3.1"/>
    <id root="2f0d8eb9-66c5-458a-a70f-ff1d539b3cc4"/>
    <observationFocus
            code="HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID"
            codeSystem="2.16.840.1.113883.3.795.12.100.502"
            displayName="Evaluate Invalid 3rd Hep B Dose as Accepted Extra Dose"/>
    <observationValue>
        <boolean value="true"/>
    </observationValue>
</observationResult>
```

The focus `codeSystem` may instead be
`http://terminology.cdsframework.org/ice/schedule-flags`. The boolean value must be `true`. Use a unique `id.root` for each
observation. Repeat the
`observationResult` element to send more than one flag, then base64-encode the updated vMR XML in the ordinary SOAP
`base64EncodedPayload` element.

### Send a Schedule Flag in a FHIR Forecast

The FHIR forecast endpoint is `POST /cds/$immds-forecast` and must first be enabled with
`CDS_ENGINE_EXPERIMENTALFEATURES_ENABLEFHIRR6=true`.

Add one repeatable `Parameters.parameter` for each flag:

```json
{
  "resourceType": "Parameters",
  "parameter": [
    {
      "name": "scheduleFlag",
      "valueCode": "HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID"
    },
    {
      "name": "scheduleFlag",
      "valueCode": "POLIO_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID"
    }
  ]
}
```

Include the required request parameters in the same `Parameters` resource. The request can use either
`application/fhir+json` or `application/json`.

## Series and Season Overrides

Series and season overrides are runtime configuration, not request options. They apply only when `enable-dose-override-feature` is
`true`. The detailed source guide
is [ICE-Configuration-Overriding-Series-and-Season-Properties.md](ICE-Configuration-Overriding-Series-and-Season-Properties.md).

### Series Display and Doses Remaining Output

`output-series-information` and `output-number-of-doses-remaining` are independent global output controls. Both default to `false`.

| Setting                                 | Output                                                                                            |
|-----------------------------------------|---------------------------------------------------------------------------------------------------|
| `output-series-information=true`        | Selected series, selection type, and season when applicable, for evaluations and recommendations. |
| `output-number-of-doses-remaining=true` | Selected series context, plus the number of doses remaining for recommendations.                  |
| Both enabled                            | Series context for evaluations and recommendations, and doses remaining for recommendations.      |

This feature adds nested output elements. SOAP/vMR consumers that parse related clinical statements must tolerate these elements
when either setting is enabled.

ICE provides the selected series and one selection type:

| Selection type               | Meaning                                                                                                                                                 |
|------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| `SERIES_DISPLAY_UNAMBIGUOUS` | Only one series can be selected. ICE forecasts from that series.                                                                                        |
| `SERIES_DISPLAY_BEST_GUESS`  | More than one series could apply; ICE forecasts from the selected best-guess series. The selection can change as additional immunizations are recorded. |
| `SERIES_DISPLAY_ALTERNATIVE` | Reserved for future use; ICE does not currently return it. It would describe an alternative series that ICE is not using for the forecast.              |

For seasonal vaccine groups—COVID-19, influenza, infant RSV, and H1N1—ICE also returns the season selected with the series,
including its effective dates. If a season predates the first coded season, ICE uses a default-season code; if it is later than the
newest coded season, it returns the newest code but displays the actual selected season dates.

The number of doses remaining is returned only with forecasts:

| Condition                                                      | Value returned                                                                       |
|----------------------------------------------------------------|--------------------------------------------------------------------------------------|
| Series incomplete and a dose is due now or due in the future   | Doses administered subtracted from the doses needed for the selected series.         |
| Complete, non-recurring series with no dose recommended        | `0`                                                                                  |
| Complete, non-recurring series but another dose is recommended | `0 doses remaining in series; one or more additional doses may still be recommended` |
| Complete recurring series with a dose recommended              | `Recurring`                                                                          |

#### SOAP/vMR Output

ICE adds a `SERIES_DISPLAY_OPTIONS` observation under the related clinical statement of the evaluated `substanceAdministrationEvent`
or forecast
`substanceAdministrationProposal`. The nested observation focus identifies the selection type; its value identifies the selected
series. A seasonal series has an additional `SEASON` observation. Forecast proposals can also contain a
`NUMBER_OF_DOSES_REMAINING` observation.

```xml

<observationResult>
    <observationFocus code="SERIES_DISPLAY_OPTIONS"
                      codeSystem="2.16.840.1.113883.3.795.12.100.500"
                      displayName="Series Options for Display"/>
    <relatedClinicalStatement>
        <observationResult>
            <observationFocus code="SERIES_DISPLAY_BEST_GUESS"
                              codeSystem="2.16.840.1.113883.3.795.12.100.501"
                              displayName="Series Display Best Guess"/>
            <observationValue>
                <concept code="INFLUENZA_1_DOSE_SERIES"
                         codeSystem="2.16.840.1.113883.3.795.12.100.10"
                         displayName="Influenza 1-Dose Series"/>
            </observationValue>
            <relatedClinicalStatement>
                <observationResult>
                    <observationFocus code="SEASON"
                                      codeSystem="2.16.840.1.113883.3.795.12.100.11"
                                      displayName="Season"/>
                    <observationValue>
                        <concept code="20152016_INFLUENZA_SEASON"
                                 codeSystem="2.16.840.1.113883.3.795.12.100.11"
                                 displayName="2015-2016 Influenza Season (07/01/2025 - 06/30/2026)"/>
                    </observationValue>
                </observationResult>
            </relatedClinicalStatement>
            <relatedClinicalStatement>
                <observationResult>
                    <observationFocus code="NUMBER_OF_DOSES_REMAINING"
                                      codeSystem="2.16.840.1.113883.3.795.12.100.10"
                                      displayName="Doses Remaining"/>
                    <observationValue>
                        <text value="1"/>
                    </observationValue>
                </observationResult>
            </relatedClinicalStatement>
        </observationResult>
    </relatedClinicalStatement>
</observationResult>
```

#### FHIR R6 Output

FHIR returns the selected series in `series`, selection type and season in the
`immunization-selection-context` extension, and the recommended dose count in
`seriesDoses.text`. The following abbreviated examples are from the response shape produced by ICE 2.58.1.

```json
{
  "resourceType": "ImmunizationEvaluation",
  "series": "Influenza 2-Dose Series",
  "doseNumber": {
    "text": "1"
  },
  "extension": [
    {
      "url": "http://terminology.cdsframework.org/fhir/StructureDefinition/immunization-selection-context",
      "extension": [
        {
          "url": "selectedSeries",
          "valueCodeableConcept": {
            "coding": [
              {
                "system": "http://terminology.cdsframework.org/ice/series",
                "code": "INFLUENZA_2_DOSE_SERIES",
                "display": "Influenza 2-Dose Series"
              }
            ]
          }
        },
        {
          "url": "seriesSelectionType",
          "valueCodeableConcept": {
            "coding": [
              {
                "system": "http://terminology.cdsframework.org/ice/series-display-selection-type",
                "code": "SERIES_DISPLAY_BEST_GUESS",
                "display": "Series Display Best Guess"
              }
            ]
          }
        },
        {
          "url": "selectedSeason",
          "valueCodeableConcept": {
            "coding": [
              {
                "system": "http://terminology.cdsframework.org/ice/seasons",
                "code": "20152016_INFLUENZA_SEASON",
                "display": "2015-2016 Influenza Season (07/01/2015 - 06/30/2016)"
              }
            ]
          }
        }
      ]
    }
  ]
}
```

```json
{
  "resourceType": "ImmunizationRecommendation",
  "recommendation": [
    {
      "series": "Polio 4-Dose Series",
      "seriesDoses": {
        "text": "5"
      },
      "extension": [
        {
          "url": "http://terminology.cdsframework.org/fhir/StructureDefinition/immunization-selection-context",
          "extension": [
            {
              "url": "selectedSeries",
              "valueCodeableConcept": {
                "text": "Polio 4-Dose Series"
              }
            },
            {
              "url": "seriesSelectionType",
              "valueCodeableConcept": {
                "text": "Series Display Best Guess"
              }
            }
          ]
        }
      ]
    }
  ]
}
```

### Vaccine-Group Rules Artifacts and Schedule Authorities

Enable these optional recommendation details globally:

```bash
ICE_OUTPUTVACCINEGROUPRULESARTIFACT=true
ICE_OUTPUTSCHEDULEAUTHORITIES=true
```

`output-vaccine-group-rules-artifact` returns the published rules URL when one is available for the vaccine group.
`output-schedule-authorities` returns the configured schedule authority or authorities for a recommendation.

SOAP/vMR returns each as a pertinent `observationResult` under the forecast proposal:

```xml

<observationResult>
    <observationFocus code="VACCINE_GROUP_RULES_URL"
                      codeSystem="2.16.840.1.113883.3.795.12.100.500"
                      displayName="URL for the vaccine group rules (i.e., logic specification)"/>
    <observationValue>
        <text value="https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/14352497/Influenza+Vaccine+Group"/>
    </observationValue>
</observationResult>
<observationResult>
<observationFocus code="ICE_VACCINE_GROUP_SCHEDULE_AUTHORITIES"
                  codeSystem="2.16.840.1.113883.3.795.12.100.500"
                  displayName="Schedule authority or authorities for the vaccine group."/>
<interpretation code="ACIP_CDC"
                codeSystem="2.16.840.1.113883.3.795.12.100.12"
                displayName="ACIP/CDC"/>
</observationResult>
```

FHIR adds extensions to the recommendation entry. The rules artifact is a
`RelatedArtifact`; each schedule authority is a reference to an organization identified by its configured code:

```json
{
  "extension": [
    {
      "url": "http://terminology.cdsframework.org/fhir/StructureDefinition/vaccine-group-rules-artifact",
      "valueRelatedArtifact": {
        "type": "documentation",
        "label": "ICE Influenza Vaccine Group Rules",
        "document": {
          "contentType": "text/html",
          "url": "https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/14352497/Influenza+Vaccine+Group"
        }
      }
    },
    {
      "url": "https://terminology.cdsframework.org/ice/StructureDefinition/ice-schedule-authority",
      "valueReference": {
        "type": "Organization",
        "identifier": {
          "system": "http://terminology.cdsframework.org/ice/schedule-authority",
          "value": "ACIP_CDC"
        },
        "display": "ACIP/CDC"
      }
    }
  ]
}
```

```yaml
ice:
  series-overrides:
    HPV_2_DOSE_SERIES:
      number-of-doses-in-series: 3
      recurring-doses-after-series-complete: false
      seasons:
        - RSV_20242025_SEASON
      dose-overrides:
        1:
          absolute-minimum-age: 9y-4d
          minimum-age: 9y
          earliest-recommended-age: 9y
          earliest-recommended-date: 2026-08-01
          absolute-maximum-age: 27y
          series-vaccine-overrides:
            '62':
              preferred: true
              allowable-minimum-age-of-use: 9y
      dose-interval-overrides:
        1:
          2:
            absolute-minimum-interval: 30d
            minimum-interval: 12w
            earliest-recommended-interval: 16w
            latest-recommended-interval: 6m
  season-overrides:
    RSV_20242025_SEASON:
      start-date: 2024-08-01
      end-date: 2025-03-31
    RSV_DEFAULT_SEASON:
      default-start-month-and-day: 08-01
      default-stop-month-and-day: 03-31
```

| Override location                                                                                   | Supported properties                                                                                                   |
|-----------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| `ice.series-overrides.<SERIES_ID>`                                                                  | `number-of-doses-in-series`, `recurring-doses-after-series-complete`, `seasons`                                        |
| `ice.series-overrides.<SERIES_ID>.dose-overrides.<DOSE_NUMBER>`                                     | `absolute-minimum-age`, `minimum-age`, `earliest-recommended-age`, `earliest-recommended-date`, `absolute-maximum-age` |
| `ice.series-overrides.<SERIES_ID>.dose-overrides.<DOSE_NUMBER>.series-vaccine-overrides.<CVX_CODE>` | `preferred`, `allowable-minimum-age-of-use`                                                                            |
| `ice.series-overrides.<SERIES_ID>.dose-interval-overrides.<FROM_DOSE>.<TO_DOSE>`                    | `absolute-minimum-interval`, `minimum-interval`, `earliest-recommended-interval`, `latest-recommended-interval`        |
| `ice.season-overrides.<SEASON_ID>`                                                                  | `start-date`, `end-date`, `default-start-month-and-day`, `default-stop-month-and-day`                                  |

### Environment Variable Examples

Enable overrides, then set the desired global series or season properties with environment variables. The series and season
identifiers are included directly in each variable name.

```bash
ICE_ENABLEDOSEOVERRIDEFEATURE=true

# Change a series-wide property.
ICE_SERIESOVERRIDES_HPV_2_DOSE_SERIES_NUMBEROFDOSESINSERIES=3

# Change one dose property in a series.
ICE_SERIESOVERRIDES_HPV_2_DOSE_SERIES_DOSEOVERRIDES_1_EARLIESTRECOMMENDEDAGE=9y

# Change an interval between two doses.
ICE_SERIESOVERRIDES_HPV_2_DOSE_SERIES_DOSEINTERVALOVERRIDES_1_2_MINIMUMINTERVAL=12w

# Change a product-specific property for a dose.
ICE_SERIESOVERRIDES_HPV_2_DOSE_SERIES_DOSEOVERRIDES_1_SERIESVACCINEOVERRIDES_62_PREFERRED=true

# Set fixed dates for a season.
ICE_SEASONOVERRIDES_RSV_20242025_SEASON_STARTDATE=2024-08-01
ICE_SEASONOVERRIDES_RSV_20242025_SEASON_ENDDATE=2025-03-31

# Set recurring month/day boundaries for a season.
ICE_SEASONOVERRIDES_RSV_DEFAULT_SEASON_DEFAULTSTARTMONTHANDDAY=08-01
ICE_SEASONOVERRIDES_RSV_DEFAULT_SEASON_DEFAULTSTOPMONTHANDDAY=03-31
```

Duration values use ICE time strings, such as `9y-4d`, `6m`, `30d`, and `2w`. Dates use `YYYY-MM-DD`; recurring season month/day
values use `MM-DD`.
