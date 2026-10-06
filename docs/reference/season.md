# Season Codes

**Applicability:** ICE SOAP/vMR API and ICE FHIR API (FHIR R6) seasonal output.

The vMR season code system is `2.16.840.1.113883.3.795.12.100.11`. The FHIR R6 season CodeSystem is maintained in [
`seasons.json`](../../fhir/ice/seasons.json).

ICE returns the season selected with a seasonal series, including its effective dates. The FHIR CodeSystem is maintained in
`seasons.json` and contains these 13 season codes. Output behavior and fallback handling are described
in [Output Configuration](../configuration/output-configuration.md#series-information-and-doses-remaining).

## Season Codes

| **Code Value**              | **Description**             | **Season Start** | **Season End** |
|-----------------------------|-----------------------------|------------------|----------------|
| `2009_H1N1_SEASON`          | 2009-2010 H1N1 Season       | 2009-10-01       | 2010-06-30     |
| `20122013_INFLUENZA_SEASON` | 2012-2013 Influenza Season  | 2012-07-01       | 2013-06-30     |
| `20132014_INFLUENZA_SEASON` | 2013-2014 Influenza Season  | 2013-07-01       | 2014-06-30     |
| `20142015_INFLUENZA_SEASON` | 2014-2015 Influenza Season  | 2014-07-01       | 2015-06-30     |
| `20152016_INFLUENZA_SEASON` | 2015-2016 Influenza Season  | 2015-07-01       | 2016-06-30     |
| `DEFAULT_INFLUENZA_SEASON`  | Default Influenza Season    | Annual: 07-01    | Annual: 06-30  |
| `COVID_19_DEC_2020_SEASON`  | Pandemic COVID-19 Season    | 1970-01-01       | 2023-09-11     |
| `COVID_19_SEP_2023_SEASON`  | 2023-2024 COVID-19 Season   | 2023-09-12       | 2024-08-21     |
| `COVID_19_AUG_2024_SEASON`  | 2024-2025 COVID-19 Season   | 2024-08-22       | 2025-08-26     |
| `COVID_19_AUG_2025_SEASON`  | 2025-2026 COVID-19 Season   | 2025-08-27       | 2026-08-27     |
| `COVID_19_AUG_2026_SEASON`  | 2026-2027 COVID-19 Season   | 2026-08-28       | 2100-01-01     |
| `RSV_20242025_SEASON`       | 2024-2025 Infant RSV Season | 2024-10-01       | 2025-03-31     |
| `RSV_DEFAULT_SEASON`        | Default Infant RSV Season   | Annual: 10-01    | Annual: 03-31  |
