# Target Diseases

**Applicability:** ICE FHIR API (FHIR R6) response `targetDisease` values.

The ICE FHIR API returns target diseases on `ImmunizationEvaluation.targetDisease` and
`ImmunizationRecommendation.recommendation.targetDisease`. The values are disease
`CodeableConcept`s coded with SNOMED CT from the maintained [ICE Disease
CodeSystem](../../fhir/ice/disease.json). The table lists each CodeSystem concept
key and the SNOMED CT code and display used for FHIR target-disease coding.

A `targetDisease` may contain multiple codings in component or multi-disease
contexts. The rows below describe the ICE disease concepts and their SNOMED CT
codings; they do not define a vaccine-group mapping. For vaccine-group comparisons,
see [Vaccine Group Codes](vaccine-groups.md) and the [Vaccine Group Normalization
section of the API Client Integration Guide](../implementation-guides/ICE-API-Client-Integration-Guide.md#vaccine-group-normalization).

| **ICE disease key**      | **Disease**               | **SNOMED CT code** | **SNOMED CT display**                                                         |
|--------------------------|---------------------------|--------------------|-------------------------------------------------------------------------------|
| `ANTHRAX`                | Anthrax                   | `409498004`        | Anthrax                                                                       |
| `CHOLERA`                | Cholera                   | `63650001`         | Cholera                                                                       |
| `COVID_19`               | Coronavirus (unspecified) | `840539006`        | Disease caused by 2019 novel coronavirus                                      |
| `DIPHTHERIA`             | Diphtheria                | `397428000`        | Diphtheria                                                                    |
| `INFLUENZA_H1N1`         | H1N1 Influenza            | `442438000`        | Influenza caused by Influenza A virus                                         |
| `HEP_A`                  | Hepatitis A               | `40468003`         | Viral hepatitis, type A                                                       |
| `HEP_B`                  | Hepatitis B               | `66071002`         | Type B viral hepatitis                                                        |
| `HIB`                    | Hib                       | `709410003`        | Haemophilus influenzae type b infection                                       |
| `HPV`                    | Human Papillomavirus      | `240532009`        | Human papilloma virus infection                                               |
| `INFLUENZA`              | Influenza                 | `6142004`          | Influenza                                                                     |
| `JAPANESE_ENCEPHALITIS`  | Japanese Encephalitis     | `52947006`         | Japanese encephalitis virus disease                                           |
| `MEASLES`                | Measles                   | `14189004`         | Measles                                                                       |
| `MENINGOCOCCAL_ACWY`     | Meningococcal ACWY        | `23511006`         | Meningococcal infectious disease                                              |
| `MENINGOCOCCAL_B`        | Meningococcal B           | `1354584007`       | Meningococcal infectious disease caused by Neisseria meningitidis serogroup B |
| `MOSQUITO_BORNE`         | Mosquito-borne, other     | `4730003`          | Mosquito-borne infectious disease                                             |
| `MPOX`                   | Mpox                      | `359814004`        | Monkeypox                                                                     |
| `MUMPS`                  | Mumps                     | `36989005`         | Mumps                                                                         |
| `PERTUSSIS`              | Pertussis                 | `27836007`         | Pertussis                                                                     |
| `PNEUMOCOCCAL_PNEUMONIA` | Pneumococcal Pneumonia    | `233604007`        | Pneumonia                                                                     |
| `POLIO`                  | Polio                     | `398102009`        | Acute poliomyelitis                                                           |
| `ROTAVIRUS`              | Rotavirus                 | `18624000`         | Rotavirus infection                                                           |
| `RSV`                    | RSV                       | `55735004`         | Respiratory syncytial virus infection                                         |
| `RUBELLA`                | Rubella                   | `36653000`         | Rubella                                                                       |
| `TETANUS`                | Tetanus                   | `76902006`         | Tetanus                                                                       |
| `TYPHOID`                | Typhoid fever             | `4834000`          | Typhoid fever                                                                 |
| `VARICELLA`              | Varicella                 | `38907003`         | Varicella                                                                     |
| `YELLOW_FEVER`           | Yellow fever              | `16541001`         | Yellow fever                                                                  |
| `ZOSTER`                 | Zoster                    | `4740000`          | Herpes zoster                                                                 |
