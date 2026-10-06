# Series and Season Overrides

These runtime properties customize immunization series and seasons defined in
ICE. They are bound to the `ice` configuration prefix and are defined in
`org.cdsframework.ice.config.IceProperties`.

Series and season overrides are service-wide configuration, not request
options. They are independent of `enable-dose-override-feature`; that flag
controls only request-supplied administered-dose validity overrides. ICE binds
the configured series and season override maps separately and applies them
when constructing the effective schedule. Because schedules are preloaded and
cached, changing configured override maps normally requires schedule
reconstruction or a service restart.

Overrides can be supplied through any Spring Boot configuration mechanism,
including `application.yml`, environment variables, or command-line arguments.

## Structure and formats

- Series overrides are keyed by a series identifier, such as
  `HPV_2_DOSE_SERIES`.
- Season overrides are keyed by a season identifier, such as
  `RSV_20242025_SEASON`.
- Dose-level overrides are keyed by a 1-based dose number.
- Vaccine-level overrides inside a dose are keyed by a vaccine CVX code, such
  as `213`.
- Dose interval overrides are keyed by a `(from-dose, to-dose)` pair of 1-based
  dose numbers.

Durations—ages and intervals—use ICE time strings such as `9y-4d` (9 years
minus four days), `6m` (6 months), `30d` (30 days), and `2w` (2 weeks). Dates
use ISO `YYYY-MM-DD` format. Recurring month-and-day values use `MM-DD` format.

## Complete YAML example

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

## Series-level overrides

Configure series-wide properties under
`ice.series-overrides.<SERIES_ID>`.

Source files:
`opencds-decision-support-service/src/main/resources/data/seriesPlanDefinitions/*.yml`

| Property                                | Type           | Description                                                                                                                            |
|-----------------------------------------|----------------|----------------------------------------------------------------------------------------------------------------------------------------|
| `number-of-doses-in-series`             | Integer        | The total number of doses required to complete the primary series.                                                                     |
| `recurring-doses-after-series-complete` | Boolean        | If `true`, the series continues to recommend additional doses, such as boosters or annual doses, after the primary series is complete. |
| `seasons`                               | List of String | Replaces the list of season identifiers associated with the series. Each value must match a season known to ICE.                       |

Command-line examples:

```bash
--ice.series-overrides.COVID_19_AUG_2025_LT_2_SERIES.recurring-doses-after-series-complete=true
--ice.series-overrides.HPV_2_DOSE_SERIES.number-of-doses-in-series=3
--ice.series-overrides.RSV_INFANT_SERIES.seasons[0]=RSV_20242025_SEASON
--ice.series-overrides.RSV_INFANT_SERIES.seasons[1]=RSV_DEFAULT_SEASON
```

## Dose overrides

Dose-level overrides modify age constraints and recommended dates for a
specific dose within a series. Configure them under
`ice.series-overrides.<SERIES_ID>.dose-overrides.<DOSE_NUMBER>`.

Source files:
`opencds-decision-support-service/src/main/resources/data/seriesPlanDefinitions/*.yml`

| Property                    | Type                | Description                                                                                                                  |
|-----------------------------|---------------------|------------------------------------------------------------------------------------------------------------------------------|
| `absolute-minimum-age`      | Duration            | The lowest age at which this dose is considered valid. Administrations before this age result in an `invalid` evaluation.    |
| `minimum-age`               | Duration            | The earliest age at which the dose can be recommended for administration, used for `Earliest Date`.                          |
| `earliest-recommended-age`  | Duration            | The age at which the dose is routinely recommended, used for `Earliest Recommended Date`.                                    |
| `earliest-recommended-date` | Date (`YYYY-MM-DD`) | A fixed date for the earliest recommendation, used for `Earliest Recommended Date` if `earliest-recommended-age` is not set. |
| `absolute-maximum-age`      | Duration            | The maximum age at which this dose is considered valid. Administrations after this age result in an `invalid` evaluation.    |

Command-line examples:

```bash
--ice.series-overrides.HPV_2_DOSE_SERIES.dose-overrides.1.earliest-recommended-age=9y
--ice.series-overrides.HPV_2_DOSE_SERIES.dose-overrides.1.absolute-minimum-age=9y
--ice.series-overrides.HPV_2_DOSE_SERIES.dose-overrides.1.absolute-maximum-age=27y
--ice.series-overrides.COVID_19_AUG_2025_LT_2_SERIES.dose-overrides.1.earliest-recommended-date=2025-08-01
```

Do not confuse these configured dose-property overrides with the
request-supplied administered-dose validity override controlled by
`enable-dose-override-feature`. That request behavior is described in
[Runtime Configuration](runtime-configuration.md).

## Vaccine-specific dose overrides

Vaccine-level overrides apply to a specific vaccine, identified by CVX code,
when used to satisfy a specific dose in a series. Configure them under
`ice.series-overrides.<SERIES_ID>.dose-overrides.<DOSE_NUMBER>.series-vaccine-overrides.<CVX_CODE>`.

Source files:
`opencds-decision-support-service/src/main/resources/data/seriesPlanDefinitions/*.yml`

| Property                       | Type     | Description                                                                                                        |
|--------------------------------|----------|--------------------------------------------------------------------------------------------------------------------|
| `preferred`                    | Boolean  | If `true`, this vaccine is prioritized when recommending which product to use for this dose.                       |
| `allowable-minimum-age-of-use` | Duration | The vaccine-specific minimum age; if this vaccine is administered before this age, the dose is considered invalid. |

Command-line examples:

```bash
--ice.series-overrides.COVID_19_AUG_2025_LT_2_SERIES.dose-overrides.1.series-vaccine-overrides.213.allowable-minimum-age-of-use=6m
--ice.series-overrides.HPV_2_DOSE_SERIES.dose-overrides.1.series-vaccine-overrides.62.preferred=true
```

## Interval overrides

Dose interval overrides modify constraints between two specific doses in a
series. Configure them under
`ice.series-overrides.<SERIES_ID>.dose-interval-overrides.<FROM_DOSE>.<TO_DOSE>`,
where both dose numbers are 1-based.

Source files:
`opencds-decision-support-service/src/main/resources/data/seriesPlanDefinitions/*.yml`

| Property                        | Type     | Description                                                                                          |
|---------------------------------|----------|------------------------------------------------------------------------------------------------------|
| `absolute-minimum-interval`     | Duration | The minimum required time between this dose and the previous one for the current dose to be valid.   |
| `minimum-interval`              | Duration | The minimum interval used to recommend the next dose as early as possible, used for `Earliest Date`. |
| `earliest-recommended-interval` | Duration | The routine interval between doses, used for `Earliest Recommended Date`.                            |
| `latest-recommended-interval`   | Duration | The interval after which the next dose is considered overdue, used for `Latest Recommended Date`.    |

Command-line examples:

```bash
--ice.series-overrides.COVID_19_AUG_2025_LT_2_SERIES.dose-interval-overrides.1.2.absolute-minimum-interval=30d
--ice.series-overrides.HPV_3_DOSE_SERIES.dose-interval-overrides.2.3.minimum-interval=12w
--ice.series-overrides.HPV_3_DOSE_SERIES.dose-interval-overrides.2.3.earliest-recommended-interval=16w
```

## Season overrides

Season overrides define or replace the start and end of a season. Configure
them under `ice.season-overrides.<SEASON_ID>`. A season may use either absolute
dates for a specific instance, such as `RSV_20242025_SEASON`, or default
month-and-day values that apply every year, such as `RSV_DEFAULT_SEASON`.
Start and end date properties apply to specific, non-default seasons. Default
start and stop month-and-day properties apply to default seasons.

Source file:
`opencds-decision-support-service/src/main/resources/data/knowledgeModule/org.nyc.cir.ice/ice-supporting-data/supportedSeasons.yml`

| Property                      | Type                | Description                                                                                         |
|-------------------------------|---------------------|-----------------------------------------------------------------------------------------------------|
| `start-date`                  | Date (`YYYY-MM-DD`) | The start date of the season. Doses administered before this date in a seasonal series are invalid. |
| `end-date`                    | Date (`YYYY-MM-DD`) | The end date of the season. Doses administered after this date in a seasonal series are invalid.    |
| `default-start-month-and-day` | `MM-DD`             | The recurring start month and day for the default season.                                           |
| `default-stop-month-and-day`  | `MM-DD`             | The recurring stop month and day for the default season.                                            |

Command-line examples:

```bash
--ice.season-overrides.RSV_20242025_SEASON.start-date=2024-08-01
--ice.season-overrides.RSV_20242025_SEASON.end-date=2025-03-31
--ice.season-overrides.RSV_DEFAULT_SEASON.default-start-month-and-day=08-01
--ice.season-overrides.RSV_DEFAULT_SEASON.default-stop-month-and-day=03-31
```

## Environment variable examples

Spring Boot removes hyphens and uses underscores for property hierarchy
separators when binding environment variables. Series and season identifiers
are included directly in each variable name.

```bash
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

## Season determination logic

When evaluating a seasonal series, ICE must determine the dates of the current
season. If the evaluation date, or the date of a historical administration,
does not fall within the absolute start and end dates of a defined season such
as `RSV_20242025_SEASON`, ICE uses this logic:

1. **Direct match:** If the date falls within the absolute `start-date` and
   `end-date` of a defined season, that season is used.
2. **Off-season extension:** If the date is after a season's `end-date` but
   before the next defined season starts, it is considered to be in the
   off-season of the earlier season. ICE automatically calculates off-season
   boundaries to ensure continuity. The most recent defined season's
   applicability is extended until the day before the next season would start,
   based on the default season's recurring start date.
3. **Default season projection:** If no defined season matches the date,
   including by off-season extension, and the series explicitly includes a
   Default Season such as `RSV_DEFAULT_SEASON` or
   `DEFAULT_INFLUENZA_SEASON`, ICE uses its
   `default-start-month-and-day` and `default-stop-month-and-day` to project a
   season into the year of the evaluation date.
4. **Most recent season template for Influenza and RSV:** If the evaluation
   date is after the last defined season and no current season is applicable,
   ICE identifies the most recent prior specifically defined season associated
   with the series. It uses that prior season's start and end month/day as a
   template to construct a current season for the evaluation year.

For RSV, the most-recent-season template has higher priority than Default
Season Projection. If an RSV series includes both `RSV_20242025_SEASON` and
`RSV_DEFAULT_SEASON`, and the evaluation date is in a future year such as 2026,
ICE uses `RSV_20242025_SEASON` as the template. `RSV_DEFAULT_SEASON` applies
only if no prior specific season is available as a template.

For Influenza, Default Season Projection typically takes precedence when a
default season is configured. The template logic is a secondary fallback if no
default season matches or is defined.

Season determination is implemented in:

- General rules:
  `org.cdsframework^ICE^1.0.0.drl`, for current and past season initialization
  using default parameters.
- Vaccine-specific rules:
  `org.nyc.cir^ICE^1.0.0^CandidateSeriesInitialization.drl`, for Influenza and
  RSV most-recent-season fallback logic.
- Java engine: `org.cdsframework.ice.service.TargetSeasons` and
  `org.cdsframework.ice.service.Season`, for off-season boundary calculation
  and date applicability checks.
