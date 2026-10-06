# Vaccine Group Codes

**Applicability:** ICE SOAP/vMR API evaluation and recommendation focus.

The ICE SOAP/vMR API and ICE FHIR API (FHIR R6) use distinct code representations for vaccine groups; the FHIR codes map to the
SOAP/vMR numeric focus values. Product-to-group mappings are listed in [Supported Vaccines](supported-vaccines.md). The maintained
FHIR R6 CodeSystem is [`vaccine-group.json`](../../fhir/ice/vaccine-group.json).

## SOAP/vMR Evaluation and Recommendation Focus

Code System 2.16.840.1.113883.3.795.12.100.1

| **Code Value** | **Description**                     |
|----------------|-------------------------------------|
| 100            | Hep B Vaccine Group                 |
| 200            | DTP Vaccine Group                   |
| 300            | Hib Vaccine Group                   |
| 400            | Polio Vaccine Group                 |
| 500            | MMR Vaccine Group                   |
| 600            | Varicella Vaccine Group             |
| 620            | Zoster Vaccine Group                |
| 750            | Pneumococcal Vaccine Group          |
| 800            | Influenza                           |
| 810            | Hep A Vaccine Group                 |
| 820            | Rotavirus Vaccine Group             |
| 830            | Meningococcal Vaccine Group         |
| 835            | Meningococcal B Vaccine Group       |
| 840            | Human Papillomavirus Vaccine Group  |
| 850            | COVID-19 Vaccine Group              |
| 860            | Mpox Vaccine Group                  |
| 875            | RSV Vaccine Group                   |
| 890            | H1N1 Influenza                      |
| 901            | Cholera Vaccine Group               |
| 902            | Japanese Encephalitis Vaccine Group |
| 904            | Typhoid Vaccine Group               |
| 905            | Yellow Fever Vaccine Group          |
| 991            | Anthrax Vaccine Group               |
| 992            | Mosquito-Borne Vaccine Group        |
| 999            | “Other” Vaccine Group               |

## FHIR R6 Vaccine Group Codes

FHIR R6 uses the following codes from `https://terminology.cdsframework.org/ice/vaccine-group`:

The Rules page column reflects the `vaccineGroupRulesUrl` values in the
maintained vaccine-group supporting data. A dash means no URL is currently
configured for that group.

| **Code**                | **Display**                         | **Rules page**                                                                                                                          |
|-------------------------|-------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| `OTHER`                 | Other Vaccine Group                 | —                                                                                                                                       |
| `ANTHRAX`               | Anthrax Vaccine Group               | —                                                                                                                                       |
| `CHOLERA`               | Cholera Vaccine Group               | [Wiki page](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/3459514369/Cholera+Vaccine+Group+Travel+Non-Routine)               |
| `COVID_19`              | COVID-19 Vaccine Group              | [Wiki page](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/2828861441/COVID-19+Seasonal+Vaccine+Group)                        |
| `DTP`                   | DTP Vaccine Group                   | [Wiki page](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/14352485/DTP+Vaccine+Group)                                        |
| `HEP_A`                 | Hep A Vaccine Group                 | [Wiki page](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/14352489/Hep+A+Vaccine+Group)                                      |
| `HEP_B`                 | Hep B Vaccine Group                 | [Wiki page](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/14352491/Hep+B+Vaccine+Group)                                      |
| `HIB`                   | Hib Vaccine Group                   | [Wiki page](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/14352493/Hib+Vaccine+Group)                                        |
| `HPV`                   | HPV Vaccine Group                   | [Wiki page](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/14352495/HPV+Vaccine+Group)                                        |
| `INFLUENZA`             | Influenza Vaccine Group             | [Wiki page](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/14352497/Influenza+Vaccine+Group)                                  |
| `INFLUENZA_H1N1`        | H1N1 Influenza Vaccine Group        | [Wiki page](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/14352487/H1N1+Vaccine+Group)                                       |
| `JAPANESE_ENCEPHALITIS` | Japanese Encephalitis Vaccine Group | [Wiki page](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/3461021697/Japanese+Encephalitis+Vaccine+Group+Travel+Non-Routine) |
| `MENINGOCOCCAL_ACWY`    | Meningococcal ACWY Vaccine Group    | [Wiki page](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/14352499/Meningococcal+ACWY+Vaccine+Group)                         |
| `MENINGOCOCCAL_B`       | Meningococcal B Vaccine Group       | [Wiki page](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/117571589/Meningococcal+B+Vaccine+Group)                           |
| `MMR`                   | MMR Vaccine Group                   | [Wiki page](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/14352511/MMR+Vaccine+Group)                                        |
| `MOSQUITO_BORNE`        | Mosquito-Borne Vaccine Group        | —                                                                                                                                       |
| `MPOX`                  | Mpox Vaccine Group                  | [Wiki page](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/2662596609/Mpox+Vaccine+Group)                                     |
| `PNEUMOCOCCAL`          | Pneumococcal Vaccine Group          | [Wiki page](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/14352501/Pneumococcal+Vaccine+Group)                               |
| `POLIO`                 | Polio Vaccine Group                 | [Wiki page](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/14352503/Polio+Vaccine+Group)                                      |
| `ROTAVIRUS`             | Rotavirus Vaccine Group             | [Wiki page](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/14352505/Rotavirus+Vaccine+Group)                                  |
| `RSV`                   | RSV Vaccine Group                   | [Wiki page](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/3118497793/RSV+Vaccine+Group)                                      |
| `TYPHOID`               | Typhoid Vaccine Group               | [Wiki page](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/3463380993/Typhoid+Vaccine+Group+Travel+Non-Routine)               |
| `VARICELLA`             | Varicella Vaccine Group             | [Wiki page](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/14352507/Varicella+Vaccine+Group)                                  |
| `YELLOW_FEVER`          | Yellow Fever Vaccine Group          | [Wiki page](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/3465052161/Yellow+Fever+Vaccine+Group+Travel+Non-Routine)          |
| `ZOSTER`                | Zoster Vaccine Group                | [Wiki page](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/61341698/Zoster+Vaccine+Group)                                     |
