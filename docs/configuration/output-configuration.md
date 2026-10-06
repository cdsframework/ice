# Output Configuration

ICE provides service-wide controls for optional forecast and evaluation output.
These settings apply to every forecast handled by the service instance.
The related ICE SOAP/vMR API values are listed in [Evaluation and Recommendation
Reason Codes](../reference/evaluation-and-recommendation-reasons.md); ICE FHIR API (FHIR R6) CodeSystem values are
maintained in the repository's [`fhir/ice`](../../fhir/ice) artifacts.

```yaml
ice:
  output-earliest-and-overdue-dates: true
  output-supplemental-text: true
  supplemental-text-mode: LEGACY
  output-series-information: false
  output-number-of-doses-remaining: false
  output-vaccine-group-rules-artifact: false
  output-schedule-authorities: false
```

| Setting                               |  Default | Effect                                                                                                                                             |
|---------------------------------------|---------:|----------------------------------------------------------------------------------------------------------------------------------------------------|
| `output-earliest-and-overdue-dates`   |   `true` | Include earliest and overdue forecast dates.                                                                                                       |
| `output-supplemental-text`            |   `true` | Include supplemental recommendation and evaluation text.                                                                                           |
| `supplemental-text-mode`              | `LEGACY` | Choose supplemental-text representation: `LEGACY`, `CODED`, or `BOTH`.                                                                             |
| `output-series-information`           |  `false` | Include series-selection and series-dose information in output.                                                                                    |
| `output-number-of-doses-remaining`    |  `false` | Include the number of doses remaining in the series.                                                                                               |
| `output-vaccine-group-rules-artifact` |  `false` | Include a link to the vaccine-group rules artifact when available.                                                                                 |
| `output-schedule-authorities`         |  `false` | Include schedule authorities associated with a recommendation. In FHIR, this is the ICE schedule-authority extension on each recommendation entry. |

Spring Boot environment-variable equivalents are:

```bash
ICE_OUTPUTEARLIESTANDOVERDUEDATES=true
ICE_OUTPUTSUPPLEMENTALTEXT=true
ICE_SUPPLEMENTALTEXTMODE=BOTH
ICE_OUTPUTSERIESINFORMATION=true
ICE_OUTPUTNUMBEROFDOSESREMAINING=true
ICE_OUTPUTVACCINEGROUPRULESARTIFACT=true
ICE_OUTPUTSCHEDULEAUTHORITIES=true
```

See [Runtime Configuration](runtime-configuration.md) for Spring Boot
configuration mechanisms and environment-variable mapping.

## Earliest, due, and overdue dates

When `output-earliest-and-overdue-dates` is enabled, ICE returns three
clinically distinct forecast dates:

| Date     | Meaning                                                                                                        |
|----------|----------------------------------------------------------------------------------------------------------------|
| Earliest | The first date the vaccine can be administered and be valid, without applying the usual four-day grace period. |
| Due      | The recommended administration date.                                                                           |
| Overdue  | The date after which a recommended immunization is late.                                                       |

For example, a routine fifth DTaP dose may be due on the patient's fourth
birthday and overdue on the seventh birthday. Earliest dates support clinical
review of an early administration; the due date remains the usual target for
routine administration.

FHIR R6 responses carry these values in
`ImmunizationRecommendation.recommendation[].dateCriterion` with these LOINC
codings:

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

## Supplemental text

`supplemental-text-mode` controls how supplemental evaluation and
recommendation reasons are represented. It applies only when
`output-supplemental-text` is `true`.

The examples below use the supported supplemental recommendation reason
`PCV15_OR_PCV20`: `Administer either PCV15 or PCV20.` For an evaluation, the
same behavior appears in SOAP `interpretation` and FHIR `doseStatusReason`
instead of SOAP recommendation `interpretation` and FHIR `forecastReason`.

### `LEGACY` (default)

`LEGACY` preserves the generic `SUPPLEMENTAL_TEXT` reason. In SOAP, the
specific message is in `originalText`; in FHIR, it is the
`CodeableConcept.text`.

```yaml
ice:
  output-supplemental-text: true
  supplemental-text-mode: LEGACY
```

SOAP/vMR output:

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

### `CODED`

`CODED` returns the specific supplemental-reason code. It does not return the
generic `SUPPLEMENTAL_TEXT` code or message.

```yaml
ice:
  output-supplemental-text: true
  supplemental-text-mode: CODED
```

SOAP/vMR output:

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

### `BOTH`

`BOTH` returns the generic reason followed by the specific coded reason. Use it
when clients need the `SUPPLEMENTAL_TEXT` representation alongside the
supplemental-reason code systems.

```bash
ICE_OUTPUTSUPPLEMENTALTEXT=true
ICE_SUPPLEMENTALTEXTMODE=BOTH
```

SOAP/vMR output:

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

## Series information and doses remaining

`output-series-information` and `output-number-of-doses-remaining` are
independent global output controls. Both default to `false`.
For the related vMR and FHIR code systems, see [Series and Display Codes](../reference/series.md)
and [Season Codes](../reference/season.md).

| Setting                                 | Output                                                                                            |
|-----------------------------------------|---------------------------------------------------------------------------------------------------|
| `output-series-information=true`        | Selected series, selection type, and season when applicable, for evaluations and recommendations. |
| `output-number-of-doses-remaining=true` | Selected series context, plus the number of doses remaining for recommendations.                  |
| Both enabled                            | Series context for evaluations and recommendations, and doses remaining for recommendations.      |

The output setting is `output-number-of-doses-remaining`. In SOAP/vMR, the
resulting nested observation uses the focus code `NUMBER_OF_DOSES_REMAINING`
from code system `2.16.840.1.113883.3.795.12.100.10`.

This feature adds nested output elements. SOAP/vMR consumers that parse related
clinical statements must tolerate these elements when either setting is
enabled.

ICE provides the selected series and one selection type. See [Series and Display Codes](../reference/series.md) for the
selection-type values and meanings.

For seasonal vaccine groups—COVID-19, influenza, infant RSV, and H1N1—ICE
also returns the season selected with the series, including its effective
dates. If a season predates the first coded season, ICE uses a default-season
code; if it is later than the newest coded season, it returns the newest code
but displays the actual selected season dates.

The number of doses remaining is returned only with forecasts:

| Condition                                                      | Value returned                                                                       |
|----------------------------------------------------------------|--------------------------------------------------------------------------------------|
| Series incomplete and a dose is due now or due in the future   | Doses administered subtracted from the doses needed for the selected series.         |
| Complete, non-recurring series with no dose recommended        | `0`                                                                                  |
| Complete, non-recurring series but another dose is recommended | `0 doses remaining in series; one or more additional doses may still be recommended` |
| Complete recurring series with a dose recommended              | `Recurring`                                                                          |

### SOAP/vMR series output

ICE adds a `SERIES_DISPLAY_OPTIONS` observation under the related clinical
statement of the evaluated `substanceAdministrationEvent` or forecast
`substanceAdministrationProposal`. The nested observation focus identifies the
selection type; its value identifies the selected series. A seasonal series
has an additional `SEASON` observation. Forecast proposals can also contain a
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

### FHIR R6 series output

FHIR returns the selected series in `series`, selection type and season in the
`immunization-selection-context` extension, and the recommended dose count in
`seriesDoses.text`. These abbreviated examples show the response shape
produced by ICE.

```json
{
  "resourceType": "ImmunizationEvaluation",
  "series": "Influenza 2-Dose Series",
  "doseNumber": {
    "text": "1"
  },
  "extension": [
    {
      "url": "https://terminology.cdsframework.org/ice/StructureDefinition/immunization-selection-context",
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
          "url": "https://terminology.cdsframework.org/ice/StructureDefinition/immunization-selection-context",
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

## Vaccine-group rules artifact and schedule authorities

`output-vaccine-group-rules-artifact` returns the published rules URL when one
is available for the vaccine group. It defaults to `false`. To enable the Wiki
URL output for all knowledge bases, set the service-wide property:

```yaml
ice:
  output-vaccine-group-rules-artifact: true
```

To enable it for one knowledge base only, set the property under that
knowledge base instead:

```yaml
ice:
  knowledge-bases:
    '[https://terminology.cdsframework.org/PlanDefinition/ice-forecast|1.0.0]':
      output-vaccine-group-rules-artifact: true
```

An explicit service-wide value takes precedence over the knowledge-base
setting. Leave the service-wide property unset when you need different values
for different knowledge bases.

The service-wide setting can also be provided as
`ICE_OUTPUTVACCINEGROUPRULESARTIFACT=true`. Restart ICE after changing the
configuration; see [Runtime Configuration](runtime-configuration.md) for
configuration precedence and mechanisms.

ICE reads the URL from the `vaccineGroupRulesUrl` property on the selected
vaccine group's supporting-data entry. If the setting is enabled but the selected group has no URL in
supporting data, ICE omits this output for that group. This URL is distinct
from the output code: SOAP/vMR returns it with the
`VACCINE_GROUP_RULES_URL` observation focus, while FHIR R6 represents it with
the `vaccine-group-rules-artifact` extension and a `RelatedArtifact` value.

For vaccine group schedule authorities,`output-schedule-authorities` returns the configured schedule authority or
authorities for a recommendation.

Supported schedule-authority codes are:

See [Schedule Authority Codes](../reference/schedule-authority.md) for the code values and FHIR extension definition. The
optional-output focus codes are listed in [Optional Output Codes](../reference/optional-output.md).

SOAP/vMR returns each as a pertinent `observationResult` under the forecast
proposal:

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
`RelatedArtifact`; each schedule authority is a reference to an organization
identified by its configured code:

```json
{
  "extension": [
    {
      "url": "https://terminology.cdsframework.org/ice/StructureDefinition/vaccine-group-rules-artifact",
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
