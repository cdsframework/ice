# Series and Season Overrides

This document describes the configuration properties that allow runtime customization of immunization series and seasons defined in
ICE. All properties documented here are bound to the `ice` configuration prefix and are defined in
`org.cdsframework.ice.config.IceProperties`.

Overrides can be supplied through any Spring Boot configuration mechanism (e.g. `application.yml`, environment variables, or
command-line arguments). The examples in this document use command-line argument form (`--ice.<property>=<value>`), which is the
most direct way to override an individual property without editing configuration files.

General structure:

- Series overrides are keyed by a **series identifier** (e.g. `HPV_2_DOSE_SERIES`).
- Season overrides are keyed by a **season identifier** (e.g. `RSV_20242025_SEASON`).
- Dose-level overrides are keyed by a **1-based dose number**.
- Vaccine-level overrides (inside a dose) are keyed by a **vaccine CVX code** (e.g. `213`).
- Dose interval overrides are keyed by a **(from-dose, to-dose)** pair of 1-based dose numbers.

Durations (ages and intervals) are expressed as ICE time-strings such as `9y-4d` (9 years minus four days), `6m` (6 months), `30d`
(30 days), `2w` (2 weeks). Dates are expressed in ISO format (`YYYY-MM-DD`). Month-and-day values use `MM-DD` format.

---

### Series-Level Overrides

These properties apply to an entire series and are configured directly on a series entry under `ice.series-overrides.<SERIES_ID>`.

Source files: `opencds-decision-support-service/src/main/resources/data/seriesPlanDefinitions/*.yml`

| Property                                | Type           | Description                                                                                                                        |
|-----------------------------------------|----------------|------------------------------------------------------------------------------------------------------------------------------------|
| `number-of-doses-in-series`             | Integer        | The total number of doses required to complete the primary series.                                                                 |
| `recurring-doses-after-series-complete` | Boolean        | If true, the series continues to recommend additional doses (e.g., boosters or annual doses) after the primary series is complete. |
| `seasons`                               | List of String | Replaces the list of season identifiers associated with the series. Each value must match a season known to ICE.                   |

Example — mark a series as having recurring doses after completion:

```
--ice.series-overrides.COVID_19_AUG_2025_LT_2_SERIES.recurring-doses-after-series-complete=true
```

Example — override the total number of doses in a series:

```
--ice.series-overrides.HPV_2_DOSE_SERIES.number-of-doses-in-series=3
```

Example — replace the list of seasons associated with a series (list elements are supplied by index):

```
--ice.series-overrides.RSV_INFANT_SERIES.seasons[0]=RSV_20242025_SEASON
--ice.series-overrides.RSV_INFANT_SERIES.seasons[1]=RSV_DEFAULT_SEASON
```

---

### Series Dose Overrides

Dose-level overrides modify the age constraints and recommended dates for a specific dose within a series. They are configured under
`ice.series-overrides.<SERIES_ID>.dose-overrides.<DOSE_NUMBER>`.

Source files: `opencds-decision-support-service/src/main/resources/data/seriesPlanDefinitions/*.yml`

| Property                    | Type                | Description                                                                                                                   |
|-----------------------------|---------------------|-------------------------------------------------------------------------------------------------------------------------------|
| `absolute-minimum-age`      | Duration            | The lowest age at which this dose is considered valid. Administrations before this age result in an 'invalid' evaluation.     |
| `minimum-age`               | Duration            | The earliest age at which the dose can be recommended for administration (used for 'Earliest Date').                          |
| `earliest-recommended-age`  | Duration            | The age at which the dose is routinely recommended (used for 'Earliest Recommended Date').                                    |
| `earliest-recommended-date` | Date (`YYYY-MM-DD`) | A fixed date for the earliest recommendation (used for 'Earliest Recommended Date' if `earliest-recommended-age` is not set). |
| `absolute-maximum-age`      | Duration            | The maximum age at which this dose is considered valid. Administrations after this age result in an 'invalid' evaluation.     |

Example — override the earliest recommended age for dose 1 of the HPV 2-dose series:

```
--ice.series-overrides.HPV_2_DOSE_SERIES.dose-overrides.1.earliest-recommended-age=9y
```

Example — set an absolute minimum age and absolute maximum age for the same dose:

```
--ice.series-overrides.HPV_2_DOSE_SERIES.dose-overrides.1.absolute-minimum-age=9y
--ice.series-overrides.HPV_2_DOSE_SERIES.dose-overrides.1.absolute-maximum-age=27y
```

Example — fix an earliest recommended date instead of an age:

```
--ice.series-overrides.COVID_19_AUG_2025_LT_2_SERIES.dose-overrides.1.earliest-recommended-date=2025-08-01
```

---

### Series Dose Vaccine Overrides

Vaccine-level overrides apply to a specific vaccine (identified by CVX code) when used to satisfy a specific dose in a series. They
are configured under `ice.series-overrides.<SERIES_ID>.dose-overrides.<DOSE_NUMBER>.series-vaccine-overrides.<CVX_CODE>`.

Source files: `opencds-decision-support-service/src/main/resources/data/seriesPlanDefinitions/*.yml`

| Property                       | Type     | Description                                                                                                        |
|--------------------------------|----------|--------------------------------------------------------------------------------------------------------------------|
| `preferred`                    | Boolean  | If true, this vaccine is prioritized when recommending which product to use for this dose.                         |
| `allowable-minimum-age-of-use` | Duration | The vaccine-specific minimum age; if this vaccine is administered before this age, the dose is considered invalid. |

Example — set an allowable minimum age of use for CVX 213 at dose 1 of a COVID-19 series:

```
--ice.series-overrides.COVID_19_AUG_2025_LT_2_SERIES.dose-overrides.1.series-vaccine-overrides.213.allowable-minimum-age-of-use=6m
```

Example — mark a vaccine as preferred for a given dose:

```
--ice.series-overrides.HPV_2_DOSE_SERIES.dose-overrides.1.series-vaccine-overrides.62.preferred=true
```

---

### Dose Interval Overrides

Dose interval overrides modify the time constraints between two specific doses in a series. They are configured under
`ice.series-overrides.<SERIES_ID>.dose-interval-overrides.<FROM_DOSE>.<TO_DOSE>`, where `<FROM_DOSE>` and `<TO_DOSE>` are 1-based
dose numbers.

Source files: `opencds-decision-support-service/src/main/resources/data/seriesPlanDefinitions/*.yml`

| Property                        | Type     | Description                                                                                           |
|---------------------------------|----------|-------------------------------------------------------------------------------------------------------|
| `absolute-minimum-interval`     | Duration | The minimum required time between this dose and the previous one for the current dose to be valid.    |
| `minimum-interval`              | Duration | The minimum interval used to recommend the next dose as early as possible (used for 'Earliest Date'). |
| `earliest-recommended-interval` | Duration | The routine interval between doses (used for 'Earliest Recommended Date').                            |
| `latest-recommended-interval`   | Duration | The interval after which the next dose is considered overdue (used for 'Latest Recommended Date').    |

Example — override the absolute minimum interval between dose 1 and dose 2:

```
--ice.series-overrides.COVID_19_AUG_2025_LT_2_SERIES.dose-interval-overrides.1.2.absolute-minimum-interval=30d
```

Example — set the minimum and earliest recommended interval between dose 2 and dose 3:

```
--ice.series-overrides.HPV_3_DOSE_SERIES.dose-interval-overrides.2.3.minimum-interval=12w
--ice.series-overrides.HPV_3_DOSE_SERIES.dose-interval-overrides.2.3.earliest-recommended-interval=16w
```

---

### Season Overrides

Season overrides define or replace the start and end of a season. They are configured under `ice.season-overrides.<SEASON_ID>`. A
season may be defined either by absolute dates (for a specific instance such as `RSV_20242025_SEASON`) or by default month-and-day
values that apply every year (for a default season such as `RSV_DEFAULT_SEASON`). Start and end date properties are applicable to
specific (non-default) seasons, while default start and end month-and-day properties are applicable to default seasons.

Source file:
`opencds-decision-support-service/src/main/resources/data/knowledgeModule/org.nyc.cir.ice/ice-supporting-data/supportedSeasons.yml`

| Property                      | Type                | Description                                                                                         |
|-------------------------------|---------------------|-----------------------------------------------------------------------------------------------------|
| `start-date`                  | Date (`YYYY-MM-DD`) | The start date of the season. Doses administered before this date in a seasonal series are invalid. |
| `end-date`                    | Date (`YYYY-MM-DD`) | The end date of the season. Doses administered after this date in a seasonal series are invalid.    |
| `default-start-month-and-day` | `MM-DD`             | The recurring start month and day for the default season.                                           |
| `default-stop-month-and-day`  | `MM-DD`             | The recurring stop month and day for the default season.                                            |

Example — set the absolute start date of a specific RSV season:

```
--ice.season-overrides.RSV_20242025_SEASON.start-date=2024-08-01
```

Example — set both the absolute start and end dates for the same season:

```
--ice.season-overrides.RSV_20242025_SEASON.start-date=2024-08-01
--ice.season-overrides.RSV_20242025_SEASON.end-date=2025-03-31
```

Example — set the recurring default start month-and-day for a default season:

```
--ice.season-overrides.RSV_DEFAULT_SEASON.default-start-month-and-day=08-01
```

Example — set the recurring default stop month-and-day for a default season:

```
--ice.season-overrides.RSV_DEFAULT_SEASON.default-stop-month-and-day=03-31
```

#### Season Determination Logic

When evaluating a seasonal series, ICE must determine the dates of the "current" season. If the evaluation date (or the date of a
historical administration) does not fall within the absolute start and end dates of a defined season (e.g., `RSV_20242025_SEASON`),
ICE uses the following logic to determine the applicable season:

1. **Direct Match**: If the date falls within the absolute `start-date` and `end-date` of a defined season, that season is used.
2. **Off-Season Extension**: If the date is after a season's `end-date` but before the next defined season starts, it is considered
   to be in the "off-season" of the earlier season. ICE automatically calculates off-season boundaries to ensure continuity. The
   most recent defined season's applicability is extended until the day before the next season would start (based on the default
   season's recurring start date).
3. **Default Season Projection**: This is the standard fallback for seasonal series. If no defined season matches the date
   (including via off-season extension), and the series explicitly includes a "Default Season" (e.g., `RSV_DEFAULT_SEASON` or
   `DEFAULT_INFLUENZA_SEASON`), ICE uses that season's `default-start-month-and-day` and `default-stop-month-and-day` to project a
   season into the year of the evaluation date.
4. **Most Recent Season Template (Influenza/RSV)**: For Influenza and RSV, ICE employs a more specialized fallback to ensure
   continuity of season-specific rules and dose requirements. If the evaluation date is after the last defined season and no current
   season is applicable, ICE identifies the **most recent prior specifically-defined season** associated with the series. It then
   uses that prior season's start and end month/day as a template to construct a "current" season for the evaluation year.
    * **RSV Precedence**: For RSV series, this template logic has **higher priority** than Default Season Projection. For example,
      if an RSV series includes both `RSV_20242025_SEASON` and `RSV_DEFAULT_SEASON`, and the evaluation date is in a future year
      (e.g., 2026), ICE will use `RSV_20242025_SEASON` as the template. `RSV_DEFAULT_SEASON` only applies if no prior specific
      season is available to act as a template.
    * **Influenza Logic**: For Influenza series, the Default Season Projection typically takes precedence if a default season is
      configured. The template logic serves as a secondary fallback if no default season matches or is defined.

The logic for season determination is implemented in the following locations:

- **General Rules**: `org.cdsframework^ICE^1.0.0.drl` (rules for current and past season initialization using default parameters).
- **Vaccine-Specific Rules**: `org.nyc.cir^ICE^1.0.0^CandidateSeriesInitialization.drl` (Influenza and RSV specific
  most-recent-season fallback logic).
- **Java Engine**: `org.cdsframework.ice.service.TargetSeasons` and `org.cdsframework.ice.service.Season` (off-season boundary
  calculation and date applicability checks).
