# Series and Display Codes

**Applicability:** ICE SOAP/vMR API and ICE FHIR API (FHIR R6) series output.

The vMR series and series-display code systems are related: series output identifies the selected series, while the display value
explains how ICE selected it. The FHIR R6 CodeSystems are maintained in [`series.json`](../../fhir/ice/series.json) and [
`series-display-selection-type.json`](../../fhir/ice/series-display-selection-type.json).

## vMR Code Systems

- Series: `2.16.840.1.113883.3.795.12.100.10`
- Series display values: `2.16.840.1.113883.3.795.12.100.501`

## vMR Series Codes

This inventory records the series code values and descriptions. It does not by itself indicate whether a series is active in a
current ICE release.

| **Code Value**                                  | **Description**                                    |
|-------------------------------------------------|----------------------------------------------------|
| `CHOLERA_1_DOSE_RISK_SERIES`                    | Cholera Risk 1-dose Series                         |
| `COVID_19_ASTRA_ZENECA_2_DOSE_SERIES`           | COVID-19 2-Dose AstraZeneca Series                 |
| `COVID_19_BIBP_SINOPHARM_2_DOSE_SERIES`         | COVID-19 2-Dose BIBP Sinopharm Series              |
| `COVID_19_CORONA_VAC_SINOVAC_2_DOSE_SERIES`     | COVID-19 2-Dose CoronaVac Sinovac Series           |
| `COVID_19_COVAXIN_2_DOSE_SERIES`                | COVID-19 2-Dose COVAXIN Series                     |
| `COVID_19_JANSSEN_1_DOSE_SERIES`                | COVID-19 1-Dose Janssen Series                     |
| `COVID_19_MEDICAGO_2_DOSE_SERIES`               | COVID-19 2-Dose Medicago Series                    |
| `COVID_19_MIXED_PRODUCT_SERIES`                 | COVID-19 Mixed Product Series                      |
| `COVID_19_MODERNA_SERIES`                       | COVID-19 Moderna Series                            |
| `COVID_19_NOVAVAX_2_DOSE_SERIES`                | COVID-19 2-Dose Novavax Series                     |
| `COVID_19_PFIZER_SERIES`                        | COVID-19 Pfizer Series                             |
| `COVID_19_SEP_2023_GTE_5_SERIES`                | Seasonal COVID-19 Series (>= 5 years)              |
| `COVID_19_SEP_2023_MIXED_PRODUCT_LT_5_Y_SERIES` | Seasonal Mixed Product COVID-19 Series (< 5 years) |
| `COVID_19_SEP_2023_MODERNA_LT_5_Y_SERIES`       | Seasonal Moderna COVID-19 Series (< 5 years)       |
| `COVID_19_SEP_2023_PFIZER_LT_5_Y_SERIES`        | Seasonal Pfizer COVID-19 Series (< 5 years)        |
| `COVID_19_SEP_2023_NOVAVAX_SERIES`              | Seasonal Novavax COVID-19 Series                   |
| `COVID_19_AUG_2025_LT_2_SERIES`                 | Seasonal 2-dose COVID-19 Series (< 2 years)        |
| `COVID_19_AUG_2025_2_Y_TO_64_Y_SERIES`          | Seasonal 1-dose COVID-19 Series (2 - 64 years)     |
| `COVID_19_AUG_2025_GTE_65_SERIES`               | Seasonal 2-dose COVID-19 Series (>= 65 years)      |
| `DTP_3_DOSE_SERIES`                             | DTP 3-Dose Series                                  |
| `DTP_5_DOSE_SERIES`                             | DTP 5-Dose Series                                  |
| `H1N1_1_DOSE_SERIES`                            | H1N1 1-Dose Series                                 |
| `H1N1_2_DOSE_SERIES`                            | H1N1 2-Dose Series                                 |
| `HEP_A_2_DOSE_CHILD_ADULT_SERIES`               | Hep A 2-Dose Child/Adult Series                    |
| `HEP_A_4_DOSE_ACCELERATED_TWINRIX_SERIES`       | Hep A 4-dose Accelerated Twinrix Series            |
| `HEP_A_ADULT_3_DOSE_SERIES`                     | Hep A Adult 3-Dose Series                          |
| `HEP_B_3_DOSE_TWINRIX_SERIES`                   | Hep B 3-dose Twinrix Series                        |
| `HEP_B_4_DOSE_ACCELERATED_TWINRIX_SERIES`       | Hep B 4-dose Accelerated Twinrix Series            |
| `HEP_B_ADULT_2_DOSE_SERIES`                     | Hep B Adult 2-Dose Series                          |
| `HEP_B_ADULT_3_DOSE_SERIES`                     | Hep B Adult 3-Dose Series                          |
| `HEP_B_3_DOSE_CHILD_ADOLESCENT_SERIES`          | Hep B 3-dose Child/Adolescent Series               |
| `HEP_B_4_DOSE_CHILD_ADOLESCENT_SERIES`          | Hep B 4-dose Child/Adolescent Series               |
| `HIB_4_DOSE_SERIES`                             | Hib 4-Dose Series                                  |
| `HIB_OMP_SERIES`                                | Hib OMP Series                                     |
| `HPV_2_DOSE_SERIES`                             | HPV 2-Dose Series                                  |
| `HPV_3_DOSE_SERIES`                             | HPV 3-Dose Series                                  |
| `INFLUENZA_1_DOSE_SERIES`                       | Influenza 1-Dose Series                            |
| `INFLUENZA_2_DOSE_DEFAULT_SERIES`               | Influenza 2-Dose Default Series                    |
| `INFLUENZA_2_DOSE_SERIES`                       | Influenza 2-Dose Series                            |
| `JEVC_RISK_2_DOSE_SERIES`                       | JE-VC Risk 2-dose Series                           |
| `JEVC_RISK_2_DOSE_ACCELERATED_SERIES`           | JE-VC Risk 2-dose Accelerated Series (18-65 years) |
| `MCV_42_DOSE_SERIES`                            | MCV4 2-Dose Series                                 |
| `MEN_B_4_C_2_DOSE_SERIES`                       | MenB 4C 2-dose Series                              |
| `MEN_B_4_C_3_DOSE_SERIES`                       | MenB 4C 3-dose Series                              |
| `MEN_BF_HBP_2_DOSE_SERIES`                      | MenB FHbp 2-dose series                            |
| `MEN_BF_HBP_3_DOSE_SERIES`                      | MenB FHbp 3-dose series                            |
| `MMR_2_DOSE_SERIES`                             | MMR 2-Dose Series                                  |
| `MPOX_1_DOSE_SERIES`                            | Mpox 1-Dose Series                                 |
| `MPOX_2_DOSE_SERIES`                            | Mpox 2-Dose Series                                 |
| `PNEUMOCOCCAL_SERIES`                           | Pneumococcal Series                                |
| `POLIO_4_DOSE_SERIES`                           | Polio 4-Dose Series                                |
| `POLIO_FRACTIONAL_IPV_SERIES`                   | Polio fIPV Series                                  |
| `RSV_INFANT_SERIES`                             | RSV Infant Series                                  |
| `RSV_ADULT_SERIES`                              | RSV Adult Series                                   |
| `ROTAVIRUS_2_DOSE_SERIES`                       | Rotavirus 2-Dose Series                            |
| `ROTAVIRUS_3_DOSE_SERIES`                       | Rotavirus 3-Dose Series                            |
| `TYPHOID_RISK_SERIES`                           | Typhoid Risk Series                                |
| `VARICELLA_2_DOSE_SERIES`                       | Varicella 2-Dose Series                            |
| `YELLOW_FEVER_RISK_SERIES`                      | Yellow Fever Risk Series                           |
| `ZOSTER_2_DOSE_SERIES`                          | Zoster 2-dose Series                               |
| `ZOSTER_3_DOSE_SERIES`                          | Zoster 3-dose Series                               |

## Series Display Selection Types

| Selection type               | Meaning                                                                                                                                                                                                                     |
|------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `SERIES_DISPLAY_UNAMBIGUOUS` | Series selected for display; no ambiguity in which series is being followed based on the immunization history at evaluation time. ICE forecasts from that series.                                                           |
| `SERIES_DISPLAY_BEST_GUESS`  | Series display best guess, when multiple series options are available; considered viable for display. ICE forecasts from the selected best-guess series. The selection can change as additional immunizations are recorded. |
| `SERIES_DISPLAY_ALTERNATIVE` | Alternative series selection, when multiple series options are available; likely not viable for display unless in conjunction with `SERIES_DISPLAY_BEST_GUESS`. Reserved for future use; ICE does not currently return it.  |
| `SERIES_DISPLAY_NONE`        | No series to display.                                                                                                                                                                                                       |

The output controls and SOAP/FHIR response shapes are documented
in [Output Configuration](../configuration/output-configuration.md#series-information-and-doses-remaining).
